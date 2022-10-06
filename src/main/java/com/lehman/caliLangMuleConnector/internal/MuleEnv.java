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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.mule.runtime.api.component.ConfigurationProperties;

import com.cali.Environment;
import com.cali.types.CaliException;
import com.cali.types.CaliString;
import com.cali.types.CaliType;

/**
 * This class provides the functions needed from the global 
 * env object availble in a cali-lang script. 
 * @author Austin Lehman
 */
public class MuleEnv {
	private ConfigurationProperties configurationProperties = null;

	/**
	 * Gets the Mule ConfigurationProperties object.
	 * @return A Mule ConfigurationProperties object.
	 */
	public ConfigurationProperties getConfigurationProperties() {
		return configurationProperties;
	}

	/**
	 * Sets the Mule ConfigurationProperties object.
	 * @param configurationProperties is a Mule ConfigurationProperties 
	 * object to set.
	 */
	public void setConfigurationProperties(ConfigurationProperties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}
	
	/**
	 * This is the implementation of the env.p() function call which 
	 * will attempt to find the config property with the provided name.
	 * @param env is the cali-lang Environment object.
	 * @param args is an ArrayList with the function args.
	 * @return A CaliType object of type CaliString with the property 
	 * value or a blank string if not found.
	 */
	public CaliType p(Environment env, ArrayList<CaliType> args) {
		String ret = "";
		CaliString key = (CaliString) args.get(0);
		if (this.configurationProperties != null && key != null) {
			try {
				Optional<String> optVal = this.configurationProperties.resolveStringProperty(key.getValue());
				if (optVal != null) {
					ret = optVal.get();
				}
			} catch (Exception e) {
				ret = "";
			}
		}
		return new CaliString(ret);
	}
	
	/**
	 * This is the implementation of env.loadResource() which loads a file from 
	 * the src/main/resources directory in the app.
	 * @param env is the cali-lang Environment object.
	 * @param args is an ArrayList with the function args.
	 * @return A CaliType object of type CaliString with the 
	 * loaded resource.
	 * @throws IOException
	 */
	public CaliType loadResource(Environment env, ArrayList<CaliType> args) {
		CaliString resource = (CaliString) args.get(0);
		String res;
		try {
			res = Util.loadFile(resource.getValue());
		} catch (IOException e) {
			return new CaliException(e.getMessage());
		}
		return new CaliString(res);
	}
}
