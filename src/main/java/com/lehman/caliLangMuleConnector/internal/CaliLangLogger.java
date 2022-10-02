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

public class CaliLangLogger {
	private Logger log;
	
    public CaliLangLogger() { }
    
    public void setLoggerString(String Lgr) {
    	if (Lgr != null && !Lgr.equals("")) {
    		this.log = LogManager.getLogger(Lgr);
    	} else {
    		this.log = LogManager.getLogger(CaliLangLogger.class.getName());
    	}
    }

    public CaliType _debug(Environment env, ArrayList<CaliType> args) {
        CallStack st = env.getCallStack().getParent().getParent().getParent();
        this.debug("cali-lang-transform", st.getClassName() + "." + st.getFunctionName() + "()", ((CaliTypeInt)args.get(0)).str());
        return new CaliNull();
    }
    
    public CaliType _info(Environment env, ArrayList<CaliType> args) {
        CallStack st = env.getCallStack().getParent().getParent().getParent();
        this.info("cali-lang-transform", st.getClassName() + "." + st.getFunctionName() + "()", ((CaliTypeInt)args.get(0)).str());
        return new CaliNull();
    }

    public CaliType _warn(Environment env, ArrayList<CaliType> args) {
        CallStack st = env.getCallStack().getParent().getParent().getParent();
        this.warn("cali-lang-transform", st.getClassName() + "." + st.getFunctionName() + "()", ((CaliTypeInt)args.get(0)).str());
        return new CaliNull();
    }

    public CaliType _err(Environment env, ArrayList<CaliType> args) {
        CallStack st = env.getCallStack().getParent().getParent().getParent();
        this.err("cali-lang-transform", st.getClassName() + "." + st.getFunctionName() + "()", ((CaliTypeInt)args.get(0)).str());
        return new CaliNull();
    }

    public CaliType _print(Environment env, ArrayList<CaliType> args) {
        this.info("cali-lang-transform", "print", ((CaliTypeInt)args.get(0)).str());
        return new CaliNull();
    }

    public CaliType _println(Environment env, ArrayList<CaliType> args) {
        CaliType ret = this._print(env, args);
        System.out.println();
        return ret;
    }

    private Logger debug(String Application, String Function, String Str) {
        log.debug(this.formatAppLine(Application, Function) + " " + Str);
        return log;
    }
    
    private Logger info(String Application, String Function, String Str) {
        log.info(this.formatAppLine(Application, Function) + " " + Str);
        return log;
    }

    private Logger warn(String Application, String Function, String Str) {
        log.warn(this.formatAppLine(Application, Function) + " " + Str);
        return log;
    }

    private Logger err(String Application, String Function, String Str) {
        log.error(this.formatAppLine(Application, Function) + " " + Str);
        return log;
    }

    private String formatAppLine(String AppName, String FunctName) {
        return AppName + " [" + FunctName + "] - ";
    }
}
