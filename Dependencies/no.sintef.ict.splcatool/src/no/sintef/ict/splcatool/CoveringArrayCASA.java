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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import splar.core.constraints.BooleanVariableInterface;

public class CoveringArrayCASA extends CoveringArray {

	private List<String> ms;
	private List<List<String>> cs;
	private String result;
	private List<List<Integer>> sols;

	public CoveringArrayCASA(List<BooleanVariableInterface> vlist, List<String> ms, List<List<String>> cs, int t) {
		this.ms = ms;
		this.cs = cs;
		this.t = t;
		
		// Fill nrid and idnr
		nrid = new HashMap<Integer, String>();
		idnr = new HashMap<String, Integer>();
		for(int i = 1; i < vlist.size()+1; i++){
			nrid.put(i, vlist.get(i-1).getID());
			idnr.put(vlist.get(i-1).getID(), i);
		}
	}

	@Override
	public void generate() throws TimeoutException, CoveringArrayGenerationException {
		
		// Make CIT Model file
		StringBuffer citmodel = new StringBuffer();
		citmodel.append(t+"\n");
		citmodel.append(ms.size()+"\n");
		for(String i : ms){
			citmodel.append(i+" ");
		}
		citmodel.deleteCharAt(citmodel.length()-1);
		citmodel.append("\n");
		
		// Write to file
		File citfile = null;
		try {
			citfile = File.createTempFile("citmodel", "x");
			new FileUtility().writeStringToFile(
				citfile.getAbsoluteFile().toString(), 
				citmodel.toString()
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Make CIT constraint file
		StringBuffer cfilestr = new StringBuffer();
		cfilestr.append(cs.size()+"\n");
		for(List<String> x : cs){
			cfilestr.append(x.size()+"\n");
			for(String a : x){
				if(a.contains("-")){
					a = a.replace("-", "");
					cfilestr.append("- ");
				}else{
					cfilestr.append("+ ");
				}
				cfilestr.append(a + " ");
			}
			cfilestr.deleteCharAt(cfilestr.length()-1);
			cfilestr.append("\n");
		}
		
		// Write to file
		File cfile = null;
		try {
			cfile = File.createTempFile("citconstraints", "x");
			new FileUtility().writeStringToFile(
				cfile.getAbsoluteFile().toString(), 
				cfilestr.toString()
			);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File resca = null;
		try {
			resca = File.createTempFile("casaca", "x");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		try {
			String line;
			//System.out.println("casa1_1/cover.exe -c "+cfile.getAbsoluteFile()+" "+citfile.getAbsoluteFile()+" -o " + resca.getAbsoluteFile());
			Process p = Runtime.getRuntime().exec("casa1_1/cover -c "+cfile.getAbsoluteFile()+" "+citfile.getAbsoluteFile()+" -o " + resca.getAbsoluteFile()); //
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader input = new BufferedReader(isr);
			//System.out.println("a");
			InputStream es = p.getErrorStream();
			InputStreamReader esr = new InputStreamReader(es);
			BufferedReader error = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			//System.out.println("b");
			int waitseconds = 0;
			while(true){
				//System.out.println(is.available() + " and " + es.available());
				int c = ' ';
				if(is.available() > 0){
					input.skip(is.available());
					while ((c = isr.read()) != -1) {
					//System.out.print((char)c);
					}
				}
				if(es.available() > 0){
					error.skip(es.available());
					while ((c = isr.read()) != -1) {
					//System.out.print((char)c);
					}
				}
				//Thread.currentThread().sleep(1);
				try{
					//System.out.println("Exit value: " + p.exitValue());
					break;
				}catch(IllegalThreadStateException e){

				}
				waitseconds++;
				if(waitseconds==3600){
					//System.out.println("Process timeout after 3600 seconds.");
					p.destroy();
				}
			}
			
			//System.out.println("wait over");
			int c = ' ';
	    	while ((c = isr.read()) != -1) {
	    		//System.out.print((char)c);
	    	}
	    	while ((c = esr.read()) != -1) {
	    		//System.err.print((char)c);
	    	}
	    	
			while ((line = input.readLine()) != null) {
	    		//System.out.println("stdout:" + line);
	    	}
	    	//System.out.println("c");
	    	while ((line = error.readLine()) != null) {
	    		//System.out.println("stderr:" + line);
	    	}
			
			//System.out.println("d");

			input.close();
			isr.close();
			is.close();
			error.close();
			esr.close();
			es.close();
			//error.close();
			//System.out.println("e");

		}catch (Exception err) {
			err.printStackTrace();
		}
		
		String res = "";
		
		try {
			res = new FileUtility().readFileAsString(resca.getAbsoluteFile().toString());
			//System.out.println("#" + res + "#");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		res = res.replace("\r", "");
		result = res.substring(res.indexOf("\n")+1);
		
		//System.out.println(nrid);
		
		sols = new ArrayList<List<Integer>>();
		
		for(String row : result.split("\n")){
			List<Integer> sol = new ArrayList<Integer>();
			for(String l : row.split(" ")){
				int n = new Integer(l);
				boolean positive = true;
				if(n%2 == 1){
					positive = false;
				}
				n /= 2;
				
				sol.add(new Integer((positive?"0":"1")));
				
			}
			sols.add(sol);
		}
	}

	@Override
	public void generate(int i, Integer sizelimit) throws TimeoutException,
			CoveringArrayGenerationException {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer[] getRow(int n) {
		Integer[]res = new Integer[sols.get(n).size()];
		for(int i = 0; i < sols.get(n).size(); i++){
			res[i] = sols.get(n).get(i);
		}
		return res;
	}

	@Override
	public int getRowCount() {
		return sols.size();
	}

	@Override
	public void setTimeout(int timeout) {
		// TODO Auto-generated method stub

	}

	@Override
	public double estimateGenerationTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getAlgorithmName() {
		return "CASA 1.1 - http://cse.unl.edu/citportal/tools/casa/";
	}

	@Override
	public long getCoverage() {
		// TODO Auto-generated method stub
		return 0;
	}

}
