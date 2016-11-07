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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import no.sintef.ict.splcatool.CNF;
import no.sintef.ict.splcatool.CoveringArray;
import no.sintef.ict.splcatool.CoveringArrayFile;
import no.sintef.ict.splcatool.FileUtility;
import no.sintef.ict.splcatool.GUIDSL;
import no.sintef.ict.splcatool.SXFM;

import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVecInt;

import de.ovgu.featureide.fm.core.io.UnsupportedModelException;
import splar.core.fm.FeatureModelException;


public class GenerateCoveringArraysTest {
	@Test
	public void testCoveringArray() throws UnsupportedModelException, IOException, FeatureModelException, TimeoutException, CoveringArrayGenerationException{
		for(String file : new FileUtility().traverseDirCollectFiles("TestData/Realistic")){
			SXFM fm = null;
			if(file.contains("_results")) continue;
			if(file.endsWith(".m")){
				GUIDSL m1 = new GUIDSL(new File(file));
				fm = m1.getSXFM();
			}else if(file.endsWith(".xml")){
				fm = new SXFM(file);
			}else continue;
			
			//System.out.println(file);
			
			CNF cnf = fm.getCNF();
			
			CoveringArray ca = cnf.getCoveringArrayGenerator("ICPL", 1);
			
			ca.setTimeout(60);
			ca.generate();
			
			assertTrue(ca.getRowCount()>0);
		}
	}
	
	@Test
	public void testReadWriteCoveringArray_type1() throws UnsupportedModelException, IOException, FeatureModelException, CSVException, CoveringArrayGenerationException{
		for(String file : new FileUtility().traverseDirCollectFiles("TestData/Realistic")){
			if(file.contains("_results")) continue;
			SXFM fm = null;
			if(file.endsWith(".m")){
				GUIDSL m1 = new GUIDSL(new File(file));
				fm = m1.getSXFM();
			}else if(file.endsWith(".xml")){
				fm = new SXFM(file);
			}else continue;
			
			//System.out.println(file);
			
			CNF cnf = fm.getCNF();
			
			CoveringArray ca = cnf.getCoveringArrayGenerator("ICPL", 2);
			
			ca.setTimeout(2);
			try {
				ca.generate();
			} catch (TimeoutException e) {
				continue;
			}
			
			assertTrue(ca.getRowCount()>0);
			
			ca.writeToFile("test.dat", CoveringArrayFile.Type.horizontal);
			CoveringArray cab = new CoveringArrayFile("test.dat");
			
			assertTrue(ca.equals(cab));
		}
	}
	
	@Test
	public void testReadWriteCoveringArray_type2() throws UnsupportedModelException, IOException, FeatureModelException, ContradictionException, org.sat4j.specs.TimeoutException, CSVException{
		String file = "TestData/Realistic/Eclipse-red.m";

		GUIDSL m1 = new GUIDSL(new File(file));
		SXFM fm = m1.getSXFM();
		CNF cnf = fm.getCNF();
		
		SAT4JSolver solver = cnf.getSAT4JSolver();
		if(!solver.solver.isSatisfiable()){
			System.out.println("Feature model not satisfiable");
			System.exit(0);
		}
		
		CoveringArray ca = new CoveringArrayFile("TestData/Realistic/Eclipse-red.m.actual.csv");
		for(int n = 0; n < ca.getRowCount(); n++){
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				if(cnf.getNr(ca.getId(i+1)) == null){
					fail("Cannot find \""+ca.nrid.get(i+1)+"\" in feature model, it is in the covering array");
				}
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			//System.out.println(assumps);
			//System.out.println(n + ", " + assumps.size());
			
			// Test
			if(!solver.solver.isSatisfiable(assumps)){
				String failstr = "Solution invalid: " + n;
				failstr += "Reason: (";
				for(int x :solver.solver.unsatExplanation().toArray()){
					failstr += ((x<0)?"-":"") + cnf.getID(Math.abs(x)) + ", ";
				}
				failstr += ")";
				fail(failstr);
			}else{
				//System.out.println("Solution valid: " + n);
			}
		}
		//System.out.println("Verification done");
	}
}
