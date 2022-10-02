/*
 * Copyright 2022 Austin Lehman (austin@rosevillecode.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.lehman.caliLangMuleConnector.internal;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mule.runtime.api.component.ConfigurationProperties;
import org.mule.runtime.api.message.Message;

import com.cali.CallStack;
import com.cali.Engine;
import com.cali.Environment;
import com.cali.ast.astClass;
import com.cali.stdlib.Lang;
import com.cali.stdlib.console;
import com.cali.types.CaliException;
import com.cali.types.CaliList;
import com.cali.types.CaliMap;
import com.cali.types.CaliNull;
import com.cali.types.CaliObject;
import com.cali.types.CaliString;
import com.cali.types.CaliType;
import com.cali.types.CaliTypeObjectInt;
import com.cali.types.Members;

public class CaliLangMule extends Engine {
	private static Logger log = LogManager.getLogger(CaliLangMule.class.getName());
	
	/**
	 * The Cali-Lang script file to run.
	 */
	private String scriptFileName = "";
	
	private ConfigurationProperties configurationProperties = null;
	private Message message = null;
	private Map<String, Object> variables = new HashMap<String, Object>();
	private String loggerStr = "";

	/**
	 * Default constructor takes the cali-lang script file name.
	 * @param ScriptFileName is a String with the
	 * cali-lang script file to run.
	 * @throws Exception
	 */
	public CaliLangMule(
			String ScriptFileName,
			ConfigurationProperties ConfigurationProperties, 
			Message Mess, 
			Map<String, Object> Vars,
			String LoggerStr
		) throws Exception {
		this.scriptFileName = ScriptFileName;
		this.setDebug(true);
		this.addIncludePath("./");
		
		this.configurationProperties = ConfigurationProperties;
		this.message = Mess;
		this.variables = Vars;
		this.loggerStr = LoggerStr;
	}

	public String runScript() throws Exception {
		String scriptFile = Util.loadFile(this.scriptFileName);
		
		this.parseString(this.scriptFileName, scriptFile);

		astClass mainClass = this.getMainClass();
		if (mainClass != null) {
			return this.runClass(mainClass);
		} else {
			throw new CaliLangClassNotFoundException("No main function found in loaded classes.");
		}
	}

	private astClass getMainClass() {
		astClass ret = null;

		Map<String, astClass> classes = this.getClasses();
		for (String className : classes.keySet()) {
			astClass cls = classes.get(className);
			if (cls.containsFunction("main")) {
				ret = cls;
				break;
			}
		}

		return ret;
	}

	private String runClass(astClass cls) throws Exception {
        CallStack callStack = new CallStack();
        Environment tenv = new Environment(this);
        
        CaliType tci = cls.instantiate(tenv, false, new CaliList());
		CaliObject app = null;
        if(!tci.isEx())
        {
            app = (CaliObject)tci;
            this.setupLogger(tenv);
            this.setupEnv(tenv);
            tenv.setClassInstance(app);
        } else {
            CaliException ex = (CaliException)tci;
            System.err.println(ex.toString());
        }

        Members locals = new Members();
        tenv.setEnvironment(app, locals, callStack);
        CaliList functArgs = new CaliList(false);
        CaliType ret = new CaliNull();
        
        ret = cls.call(tenv, false, "main", functArgs);
        if(ret.isEx()) {
            CaliException ex = (CaliException) ret;
            log.error(ex.toString());
            throw new CaliLangException(ex.toString());
        }

        CaliString retStr = (CaliString) ((CaliTypeObjectInt)ret).toJson(tenv, new ArrayList<CaliType>());
        
        return retStr.getValue();
    }

	/**
	 * Override the add include function to load the files from
	 * the resources directory.
	 * @param Include
	 * @throws Exception
	 */
	@Override
	public void addInclude(String Include) throws Exception {
		if (this.getDebug()) console.get().info("Engine.addInclude(): Include: " + Include);
		if (Lang.get().langIncludes.containsKey(Include)) {
			if (!this.getIncludes().contains(Include)) {
				if (this.getDebug()) console.get().info("Engine.addInclude(): Adding langInclude: " + Include);
				this.getIncludes().add(Include);
				this.parseString(Include, Lang.get().langIncludes.get(Include));
			}
		} else {
			if (this.getDebug()) console.get().info("Engine.addInclude(): Attempting to find in resourceIncludePaths ...");
			for (String pth : this.getResourceIncludePath()) {
				List<String> resDir = Lang.get().listResourceDirectory(pth);
				String tinc = pth + Include;

				for (String fname : resDir) {
					if (fname.contains(tinc)) {
						if (this.getDebug()) console.get().info("Engine.addInclude(): Include " + Include + " found in '" + fname + "'");
						this.getIncludes().add(tinc);
						this.parseString(Include, com.cali.Util.loadResource(tinc));
						return;
					}
				}
			}

			if (this.getDebug()) console.get().info("Engine.addInclude(): Attempting to find in includePaths ...");
			for (String pth : this.getIncludePaths()) {
				String tinc = pth + Include;

				ClassLoader classLoader = CaliLangMule.class.getClassLoader();
				URL resource = classLoader.getResource(tinc);
				File f = new File(resource.getFile());

				if (f.exists()) {
					if (!this.getIncludes().contains(tinc)) {
						if (this.getDebug()) console.get().info("Engine.addInclude(): Include " + Include + " found in '" + pth + "'");
						this.getIncludes().add(tinc);
						this.parseString(Include, Util.loadFile(tinc));
						return;
					}
				}
			}

			if (this.getDebug()) console.get().info("Engine.addInclude(): Include '" + Include + "' not found at all.");
		}
	}
	
	private void setupLogger(Environment tenv) throws Exception {
		// Create the log and add parse it.
		this.createLogObject();
		
        CaliType tci = this.getStaticClass("log");
        if(!tci.isEx())
        {
            CaliObject lobj = (CaliObject)tci;
            CaliLangLogger lgr = (CaliLangLogger)lobj.getExternObject();
            lgr.setLoggerString(this.loggerStr);
        } else {
            CaliException ex = (CaliException)tci;
            log.error(ex.toString());
            throw new CaliLangException(ex.toString());
        }
    }
	
	private void createLogObject() throws Exception {
		String clsStr = ""
			+ "static extern class log : com.lehman.caliLangMuleConnector.internal.CaliLangLogger {" + "\n"
			+ "	// Logger functions" + "\n"
			+ "	public debug(Content) { this._debug(Content); return this; }" + "\n"
			+ "	public info(Content) { this._info(Content); return this; }" + "\n"
			+ "	public warn(Content) { this._warn(Content); return this; }" + "\n"
			+ "	public err(Content) { this._err(Content); return this; }" + "\n"
			+ "	public print(Content) { this._print(Content); return this; }" + "\n"
			+ "	public println(Content) { this._println(Content); return this; }" + "\n"
			+ "	" + "\n"
			+ "	// Chained extern functions" + "\n"
			+ "	private extern _debug(Content);" + "\n"
			+ "	private extern _info(Content);" + "\n"
			+ "	private extern _warn(Content);" + "\n"
			+ "	private extern _err(Content);" + "\n"
			+ "	private extern _print(Content);" + "\n"
			+ "	private extern _println(Content);" + "\n"
			+ "}" + "\n"
		;
		
		this.parseString("log.ca", clsStr);
	}
	
	private void setupEnv(Environment tenv) throws Exception {
		// Create the env and add parse it.
		this.createEnvObject();
		
        CaliType tci = this.getStaticClass("env");
        if(!tci.isEx())
        {
            CaliObject eobj = (CaliObject)tci;
            MuleEnv ev = (MuleEnv)eobj.getExternObject();
            ev.setConfigurationProperties(this.configurationProperties);
            this.convertMessage(eobj, this.message);
            this.convertVariables(eobj, this.variables);
        } else {
            CaliException ex = (CaliException)tci;
            log.error(ex.toString());
            throw new CaliLangException(ex.toString());
        }
    }
	
	private void createEnvObject() throws Exception {
		String clsStr = ""
			+ "static extern class env : com.lehman.caliLangMuleConnector.internal.MuleEnv {" + "\n"
			+ "	public payload = {};" + "\n"
			+ "	public attributes = {};" + "\n"
			+ "	public variables = {};" + "\n"
			+ "	public extern p(string prop);" + "\n"
			+ "	public toString() {" + "\n"
			+ "		rstr = 'attributes:\\n';" + "\n"
			+ "		rstr += this.attributes + '\\n';" + "\n"
			+ "		rstr += 'variables:\\n';" + "\n"
			+ "		rstr += this.variables + '\\n';" + "\n"
			+ "		rstr += 'payload:\\n';" + "\n"
			+ "		rstr += this.payload + '\\n';" + "\n"
			+ "	return rstr;" + "\n"
			+ "	}" + "\n"
			+ "}" + "\n"
		;
		
		this.parseString("env.ca", clsStr);
	}
        
    private void convertMessage(CaliObject env, Message message) {
    	// Payload
      	CaliMap payloadMap = new CaliMap();
      	payloadMap.put("dataTypeStr", MuleToCaliConverter.convert(message.getPayload().getDataType().toString()));
      	payloadMap.put("dataType", MuleToCaliConverter.convert(message.getPayload().getDataType().getClass().getName()));
      	payloadMap.put("mimeType", MuleToCaliConverter.convert(
      		message.getPayload().getDataType().getMediaType().getPrimaryType().toString()
      		+ "/" + message.getPayload().getDataType().getMediaType().getSubType().toString()));
      	payloadMap.put("charset", MuleToCaliConverter.convert(message.getPayload().getDataType().getMediaType().getCharset().get().toString()));
      	payloadMap.put("values", MuleToCaliConverter.convert(message.getPayload().getValue()));
      	env.addMember("payload", payloadMap);
      	  
      	// Attributes
      	CaliMap attrMap = new CaliMap();
      	attrMap.put("dataTypeStr", MuleToCaliConverter.convert(message.getAttributes().getDataType().toString()));
      	attrMap.put("dataType", MuleToCaliConverter.convert(message.getAttributes().getDataType().getClass().getName()));
      	attrMap.put("mimeType", MuleToCaliConverter.convert(
      		message.getAttributes().getDataType().getMediaType().getPrimaryType().toString()
      		+ "/" + message.getAttributes().getDataType().getMediaType().getSubType().toString()));
      	attrMap.put("charset", MuleToCaliConverter.convert(message.getPayload().getDataType().getMediaType().getCharset().get().toString()));
      	if (message.getAttributes().getValue() == null) {
      		attrMap.put("values", new CaliMap());
      	} else {
      		attrMap.put("values", MuleToCaliConverter.convert(message.getAttributes().getValue()));
      	}
      	env.addMember("attributes", attrMap);
    }
    
    private void convertVariables(CaliObject env, Map<String, Object> variables) {
  	  env.getMembers().getMap().put("variables", MuleToCaliConverter.convert(variables));
    }
}
