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

import com.cali.SecurityManagerImpl;

/**
 * This is the security manager implemented for this Mule 
 * version of cali-lang. In other versions of the interpreter permissions could
 * be more or less depending on need.
 */
public class CaliLangSecurityManager extends SecurityManagerImpl {
    public CaliLangSecurityManager() {
        /*
         * Security manager itself.
         */
        // instantiate - can new instances be created from this one? This
        // normally applies to ones instantiated from within cali and are blocked
        // in CSecurityManager sub-class constructor.
        this.props.put("securitymanager.instantiate", true);

        // getProp
        this.props.put("securitymanager.property.get", true);

        // keySet/getMap
        this.props.put("securitymanager.property.list", true);

        // setProp
        this.props.put("securitymanager.property.set", false);

        /*
         *  System information view. See com.cali.stdlib.CSys.java.
         */
        this.props.put("os.info.view", true);
        this.props.put("java.info.view", true);
        this.props.put("java.home.view", true);
        this.props.put("java.classpath.view", true);
        this.props.put("cali.info.view", true);
        this.props.put("cali.path.view", true);
        this.props.put("current.path.view", true);
        this.props.put("home.path.view", true);
        this.props.put("user.name.view", true);

        /*
         *  Reflection actions. See com.cali.stdlib.CReflect.java.
         */
        this.props.put("reflect.eval.string", true);
        this.props.put("reflect.eval.file", true);
        this.props.put("reflect.include.module", true);
    }
}

