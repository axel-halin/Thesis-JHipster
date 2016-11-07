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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import splar.core.constraints.BooleanVariableInterface;

import de.ovgu.featureide.fm.core.io.UnsupportedModelException;

public class CASAModel {
	private List<String> ms = new ArrayList<String>();
	private List<List<String>> cs = new ArrayList<List<String>>();
	private List<BooleanVariableInterface> vlist;
	
	public CASAModel(List<BooleanVariableInterface> vlist, List<String> ms, List<List<String>> cs){
		this.ms = ms;
		this.cs = cs;
		this.vlist = vlist;
	}

	public CASAModel(File mFile, File cFile) throws IOException, UnsupportedModelException {
		String m = new FileUtility().readFileAsString(mFile.getAbsolutePath());
		
		m = m.replace("\n", " ");
		m = m.replace("\r", " ");
		
		for(String am : m.split(" ")){
			ms.add(am);
		}
		int size = new Integer(ms.get(1));
		ms = ms.subList(2, ms.size());
		
		if(size != ms.size()){
			throw new UnsupportedModelException("Size signature not consistent", 1);
		}
		
		//System.out.println(ms.size() + ":" + ms);
		
		String c = new FileUtility().readFileAsString(cFile.getAbsolutePath());
		c = c.replace("\r", " ");
		c = c.replace("\n", ",");
		c = c.replace("- ", "-");
		
		List<String> cStr = new ArrayList<String>();
		for(String ac : c.split(",")){
			cStr.add(ac);
		}
		int csize = new Integer(cStr.get(0));
		
		for(int i = 2; i < cStr.size(); i+=2){
			List<String> ac = new ArrayList<String>();
			String line = cStr.get(i);
			while(line.contains("+ ") || line.contains("- "))
				line = line.replace("+ ", "+").replace("- ", "-");
			line = line.replace("+", "");
			for(String lit : line.split(" ")){
				ac.add(lit);
			}
			cs.add(ac);
		}
		
		//System.out.println(cs);
	}

/*	public String getGUIDSL() {
		String g = "X : ";
		
		// Features
		int c = 0;
		for(String f : ms){
			g += "[f" + c + "]" + " ";
			c += new Integer(f);
		}
		
		g += ":: _X;\r\n\r\n";
		
		// Features higher than 2
		c = 0;
		for(String f : ms){
			if(new Integer(f) > 2){
				g += "f" + c + " : ";
				for(int i = 1; i < new Integer(f); i++){
					g += "f" + (c+i) + " | ";
				}
				g = g.substring(0, g.length()-3);
				g += ";\r\n";
			}
			c += new Integer(f);
		}
		
		// Constraints
		g += "\r\n%%\r\n\r\n";
		
		for(List<String> ac : cs){
			for(String l : ac){
				boolean neg = false;
				if(new Integer(l)<0){
					neg = true;
					l = l.replace("-", "");
				}
				if(!exist(new Integer(l))){
					if(cardinality(new Integer(l))==2){
						neg = !neg;
						l--;
					}
				}
				g += (neg?"not ":"") + "f" + l + " and ";
			}
			g = g.substring(0, g.length()-5);
			g += ";\r\n";
		}
		
		// Done
		//System.out.println(g);
		return g;
	}

	private boolean exist(Integer x){
		int c = 0;
		for(String f : ms){
			if(x == c) return true;
			c += new Integer(f);
		}
		return false;
	}
	
	private int cardinality(Integer x){
		int c = 0;
		for(String f : ms){
			c += new Integer(f);
			if(x < c) return new Integer(f);
		}
		return -1;
	}
*/
	public CoveringArray getCoveringArrayGenerator(int t) {
		return new CoveringArrayCASA(vlist, ms, cs, t);
	}
}
