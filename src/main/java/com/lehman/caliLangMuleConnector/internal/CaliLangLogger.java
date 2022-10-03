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

import com.cali.CallStack;
import com.cali.Environment;
import com.cali.types.CaliNull;
import com.cali.types.CaliType;
import com.cali.types.CaliTypeInt;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implements logging support in the Cali-Lang Mule Transform.
 * @author Austin Lehman
 */
public class CaliLangLogger {
	private Logger log;
	
	/**
	 * Default constructor takes no arguments.
	 */
    public CaliLangLogger() { }
    
    /**
     * Sets the logger name. This name is what is set in the 
     * log4j2.xml file.
     * @param Lgr A String with the logger name to use or blank 
     * or null if not provided.
     */
    public void setLoggerString(String Lgr) {
    	if (Lgr != null && !Lgr.equals("")) {
    		this.log = LogManager.getLogger(Lgr);
    	} else {
    		this.log = LogManager.getLogger(CaliLangLogger.class.getName());
    	}
    }

    /**
     * Cali-Lang external function that implements debug log.
     * @param env is the cali-lang environment object.
     * @param args is an ArrayList of function arguments to pass.
     * @return A CaliNull object.
     */
    public CaliType _debug(Environment env, ArrayList<CaliType> args) {
        CallStack st = env.getCallStack().getParent().getParent().getParent();
        this.debug("cali-lang-transform", st.getClassName() + "." + st.getFunctionName() + "()", ((CaliTypeInt)args.get(0)).str());
        return new CaliNull();
    }
    
    /**
     * Cali-Lang external function that implements info log.
     * @param env is the cali-lang environment object.
     * @param args is an ArrayList of function arguments to pass.
     * @return A CaliNull object.
     */
    public CaliType _info(Environment env, ArrayList<CaliType> args) {
        CallStack st = env.getCallStack().getParent().getParent().getParent();
        this.info("cali-lang-transform", st.getClassName() + "." + st.getFunctionName() + "()", ((CaliTypeInt)args.get(0)).str());
        return new CaliNull();
    }

    /**
     * Cali-Lang external function that implements warn log.
     * @param env is the cali-lang environment object.
     * @param args is an ArrayList of function arguments to pass.
     * @return A CaliNull object.
     */
    public CaliType _warn(Environment env, ArrayList<CaliType> args) {
        CallStack st = env.getCallStack().getParent().getParent().getParent();
        this.warn("cali-lang-transform", st.getClassName() + "." + st.getFunctionName() + "()", ((CaliTypeInt)args.get(0)).str());
        return new CaliNull();
    }

    /**
     * Cali-Lang external function that implements err log.
     * @param env is the cali-lang environment object.
     * @param args is an ArrayList of function arguments to pass.
     * @return A CaliNull object.
     */
    public CaliType _err(Environment env, ArrayList<CaliType> args) {
        CallStack st = env.getCallStack().getParent().getParent().getParent();
        this.err("cali-lang-transform", st.getClassName() + "." + st.getFunctionName() + "()", ((CaliTypeInt)args.get(0)).str());
        return new CaliNull();
    }

    /**
     * Cali-Lang external function that implements print log.
     * @param env is the cali-lang environment object.
     * @param args is an ArrayList of function arguments to pass.
     * @return A CaliNull object.
     */
    public CaliType _print(Environment env, ArrayList<CaliType> args) {
        this.info("cali-lang-transform", "print", ((CaliTypeInt)args.get(0)).str());
        return new CaliNull();
    }

    /**
     * Cali-Lang external function that implements println log.
     * @param env is the cali-lang environment object.
     * @param args is an ArrayList of function arguments to pass.
     * @return A CaliNull object.
     */
    public CaliType _println(Environment env, ArrayList<CaliType> args) {
        CaliType ret = this._print(env, args);
        System.out.println();
        return ret;
    }

    /**
     * Executes the debug entry.
     * @param Application is a String with the application name.
     * @param Function is a String with the function name.
     * @param Str is a String with the text to log.
     * @return An instance of Logger.
     */
    private Logger debug(String Application, String Function, String Str) {
        log.debug(this.formatAppLine(Application, Function) + " " + Str);
        return log;
    }
    
    /**
     * Executes the info entry.
     * @param Application is a String with the application name.
     * @param Function is a String with the function name.
     * @param Str is a String with the text to log.
     * @return An instance of Logger.
     */
    private Logger info(String Application, String Function, String Str) {
        log.info(this.formatAppLine(Application, Function) + " " + Str);
        return log;
    }

    /**
     * Executes the warn entry.
     * @param Application is a String with the application name.
     * @param Function is a String with the function name.
     * @param Str is a String with the text to log.
     * @return An instance of Logger.
     */
    private Logger warn(String Application, String Function, String Str) {
        log.warn(this.formatAppLine(Application, Function) + " " + Str);
        return log;
    }

    /**
     * Executes the err entry.
     * @param Application is a String with the application name.
     * @param Function is a String with the function name.
     * @param Str is a String with the text to log.
     * @return An instance of Logger.
     */
    private Logger err(String Application, String Function, String Str) {
        log.error(this.formatAppLine(Application, Function) + " " + Str);
        return log;
    }

    /**
     * Formats the application line.
     * @param AppName is a String with the app name.
     * @param FunctName is a String with the function name.
     * @return A String with the formatted app line.
     */
    private String formatAppLine(String AppName, String FunctName) {
        return AppName + " [" + FunctName + "] - ";
    }
}
