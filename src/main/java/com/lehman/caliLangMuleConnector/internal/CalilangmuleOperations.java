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

import static org.mule.runtime.extension.api.annotation.param.MediaType.APPLICATION_JSON ;


import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.mule.runtime.api.component.ConfigurationProperties;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.extension.api.annotation.param.MediaType;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.Summary;


/**
 * This class is a container for operations, every public method in this class will be taken as an extension operation.
 */
public class CalilangmuleOperations {
  // Access the config
  @Inject
  private ConfigurationProperties configurationProperties;
  
  /**
   * Executes the Cali-Lang script.
   * @param scriptFileName is a String with the cali script file to execute.
   * @return A JSON string with the result.
 * @throws Exception 
   */
  @Summary("Cali-Lang transform connector.")
  @MediaType(value = APPLICATION_JSON, strict = false)
  public String caliLangTransform(
		  String scriptFileName, 
		  @Optional(defaultValue="#[message]") Object messageObj, 
		  @Optional(defaultValue="#[vars]") Object varsObj,
		  @Optional String logClass
	) throws Exception {
	  Message message = null;
	  if (messageObj instanceof Message) {
          message = (Message) messageObj;
      } else {
    	  System.out.print("messageObj type: " + messageObj.getClass().getName());
      }
	  
	  Map<String, Object> vars = new HashMap<String, Object>();
	  if (varsObj instanceof Map) {
          vars = (Map<String, Object>) varsObj;
      } else {
    	  System.out.print("varsObj type: " + varsObj.getClass().getName());
      }
	  
	  // Create class instance.
	  CaliLangMule cali = new CaliLangMule(scriptFileName, this.configurationProperties, message, vars, logClass);
	  String ret = cali.runScript();
	  
	  return ret;
  }
}
