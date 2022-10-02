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
import java.util.HashMap;
import java.util.LinkedHashMap;

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

public class MuleToCaliConverter {
	public static CaliType convert(Object val) {
		  CaliType ret = new CaliString("");
		  
		  if (val instanceof HashMap) {
			  CaliMap m = new CaliMap();
			  LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)val;
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
		  } else {
			  ret = new CaliString("Unknown type found: [" + val.getClass() + "] value: " + val.toString());
		  }
		  
		  return ret;
	  }
}
