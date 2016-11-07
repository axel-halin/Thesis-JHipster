/*******************************************************************************
 * Copyright (c) 2011, 2012 SINTEF, Martin F. Johansen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     SINTEF, Martin F. Johansen - the implementation
 *******************************************************************************/
package no.sintef.ict.splcatool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Report {
	Map<String, Map<String, String>> results  = new HashMap<String, Map<String, String>>();
	
	public Report(String filename) throws IOException{
		if(!new File(filename).exists()) return;
		
		String content = new FileUtility().readFileAsString(filename);
		String firstRow = content.split("\n")[0];
		Map<Integer, String> keys = new HashMap<Integer, String>();
		int i = 0;
		for(String key : firstRow.split(";"))
			keys.put(i++, key.trim());
		//System.out.println(keys);
		i = 0;
		for(String row : content.split("\n")){
			if(i++==0) continue;
			int j = 0;
			String file = "";
			for(String key : row.split(";")){
				if(j==0){
					file = key;
					j++;
					continue;
				}
				addValue(file, keys.get(j), key.trim());
				j++;
			}
		}
		//System.out.println(results);
	}
	
	@Override
	public boolean equals(Object obj) {
		Report ra = this;
		Report rb = (Report) obj;
		return this.results.equals(rb.results);
	}

	public Report() {
	}

	public void addValue(String file, String key, String value){
		if(results.get(file)==null)
			results.put(file, new HashMap<String, String>());
		results.get(file).put(key, value);
	}
	
	public String getValue(String file, String key){
		if(results.get(file) == null) return null;
		return results.get(file).get(key);
	}

	@Override
	public String toString(){
		return results.toString();
	}
	
	public void writeToFile(String filename) throws FileNotFoundException, IOException{
		String output = "";
		Set<String> keys = new HashSet<String>();
		for(Entry<String, Map<String, String>> e : results.entrySet())
			keys.addAll(e.getValue().keySet());
		output += "fms\\keys;";
		for(String key : keys)
			output += key + ";";
		output += "\n";
		for(Entry<String, Map<String, String>> e : results.entrySet()){
			output += e.getKey() + ";";
			for(String key : keys){
				String v = e.getValue().get(key);
				if(v == null || v.equals("")) v = "-";
				if(v != null && v.contains(".")) v.replace(".", ",");
				output += v + ";";
			}
			output += "\n";
		}
		new FileUtility().writeStringToFile(filename, output);
	}
	
}
