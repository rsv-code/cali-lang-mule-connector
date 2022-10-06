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
import java.util.Map;

import org.mule.extension.http.api.HttpRequestAttributes;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.util.CaseInsensitiveHashMap;

import com.cali.types.CaliBool;
import com.cali.types.CaliDouble;
import com.cali.types.CaliInt;
import com.cali.types.CaliList;
import com.cali.types.CaliMap;
import com.cali.types.CaliNull;
import com.cali.types.CaliString;
import com.cali.types.CaliType;

/**
 * Class with support to convert Mule objects to Cali-Lang objects.
 * @author Austin Lehman
 */
public class MuleToCaliConverter {
	
	/**
	 * Static function that converts the provided Mule object 
	 * to a Cali-Lang one for use in the script.
	 * @param val is a Mule object to converver.
	 * @return A CaliType object with the converted data.
	 */
	public static CaliType convert(Object val) {
		  CaliType ret = new CaliString("");
		  
		  if (val instanceof Map) {
			  CaliMap m = new CaliMap();
			  Map<String, Object> map = (Map<String, Object>)val;
			  for (String key : map.keySet()) {
				  Object tval = map.get(key);
				  m.put(key, convert(tval));
			  }
			  ret = m;
		  } else if (val instanceof CaseInsensitiveHashMap) {
			  CaliMap m = new CaliMap();
			  CaseInsensitiveHashMap<String, Object> map = (CaseInsensitiveHashMap<String, Object>)val;
			  for (String key : map.keySet()) {
				  Object tval = map.get(key);
				  m.put(key, convert(tval));
			  }
			  ret = m;
		  } else if (val instanceof ArrayList) {
			  CaliList l = new CaliList();
			  ArrayList<Object> lst = (ArrayList<Object>)val;
			  for (Object obj : lst) {
				  l.add(convert(obj));
			  }
			  ret = l;
		  } else if (val instanceof String) {
			  ret = new CaliString((String)val);
		  } else if (val instanceof Integer) {
			  ret = new CaliInt((Integer)val);
		  } else if (val instanceof Double) {
			  ret = new CaliDouble((Double)val);
		  } else if (val instanceof Boolean) {
			  ret = new CaliBool((Boolean)val);
		  } else if (val == null) {
			  ret = new CaliNull();
		  } else if (val instanceof TypedValue) {
			  TypedValue<Object> tval = (TypedValue<Object>)val;
			  ret = convert(tval.getValue());
		  } else if (val instanceof HttpRequestAttributes) {
			  ret = mapHttpReqAttr((HttpRequestAttributes) val);
		  } else {
			  ret = new CaliString("Unknown type found: [" + val.getClass() + "] value: " + val.toString());
		  }
		  
		  return ret;
	  }
	
	/**
	 * Maps the Mule HttpRequestAttributes object to a Cali-Lang map.
	 * @param attr is the Mule HttpRequestAttributes object to map from.
	 * @return A CaliMap object with the result.
	 */
	public static CaliType mapHttpReqAttr(HttpRequestAttributes attr) {
		CaliMap ret = new CaliMap();
		
		CaliMap headers = new CaliMap();
		for (String key : attr.getHeaders().keySet()) {
			headers.put(key, new CaliString(attr.getHeaders().get(key)));
		}
		ret.put("headers", headers);
		
		CaliMap queryParams = new CaliMap();
		for (String key : attr.getQueryParams().keySet()) {
			queryParams.put(key, new CaliString(attr.getQueryParams().get(key)));
		}
		ret.put("queryParams", queryParams);
		
		CaliMap uriParams = new CaliMap();
		for (String key : attr.getUriParams().keySet()) {
			uriParams.put(key, new CaliString(attr.getUriParams().get(key)));
		}
		ret.put("uriParams", uriParams);
		
		ret.put("requestPath", new CaliString(attr.getRequestPath()));
		ret.put("listenerPath", new CaliString(attr.getListenerPath()));
		ret.put("rawRequestPath", new CaliString(attr.getRawRequestPath()));
		ret.put("relativePath", new CaliString(attr.getRelativePath()));
		ret.put("maskedRequestPath", new CaliString(attr.getMaskedRequestPath()));
		ret.put("version", new CaliString(attr.getVersion()));
		ret.put("scheme", new CaliString(attr.getScheme()));
		ret.put("method", new CaliString(attr.getMethod()));
		ret.put("requestUri", new CaliString(attr.getRequestUri()));
		ret.put("rawRequestUri", new CaliString(attr.getRawRequestUri()));
		ret.put("queryString", new CaliString(attr.getQueryString()));
		ret.put("localAddress", new CaliString(attr.getLocalAddress()));
		ret.put("remoteAddress", new CaliString(attr.getRemoteAddress()));
		if (attr.getClientCertificate() != null) {
			ret.put("clientCertificate", new CaliString(attr.getClientCertificate().toString()));
		}
		
		return ret;
	}
}
