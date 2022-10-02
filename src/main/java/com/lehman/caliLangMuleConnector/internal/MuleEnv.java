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

import java.util.ArrayList;
import java.util.Optional;

import org.mule.runtime.api.component.ConfigurationProperties;

import com.cali.Environment;
import com.cali.types.CaliString;
import com.cali.types.CaliType;

public class MuleEnv {
	private ConfigurationProperties configurationProperties = null;

	public ConfigurationProperties getConfigurationProperties() {
		return configurationProperties;
	}

	public void setConfigurationProperties(ConfigurationProperties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}
	
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
}
