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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class Util {
	public static String loadFile(String Resource) throws IOException {
		ClassLoader classLoader = Util.class.getClassLoader();
		URL resource = classLoader.getResource(Resource);
		if (resource != null) {
			File file = new File(resource.getFile());
			InputStream inputStream = new FileInputStream(file);
			
			StringBuilder sb = new StringBuilder();
	
		    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		    String ln;
		    while ((ln = br.readLine()) != null) {
		        sb.append(ln).append("\n");
		    }
	    
		    return sb.toString();
		} else {
			throw new FileNotFoundException("Resource '" + Resource + "' not found.");
		}
	}
}
