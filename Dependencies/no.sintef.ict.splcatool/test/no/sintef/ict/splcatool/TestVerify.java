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
import no.sintef.ict.splcatool.GUIDSL;
import no.sintef.ict.splcatool.SXFM;
import no.sintef.ict.splcatool.Verifier;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.LecteurDimacs;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;

import splar.core.fm.FeatureModelException;

import de.ovgu.featureide.fm.core.io.UnsupportedModelException;

public class TestVerify {
	Verifier v;
	
	@Before
	public void setup(){
		v = new Verifier();
	}
	
	@Test
	public void verify1w_Chvatal() throws UnsupportedModelException, IOException, FeatureModelException, TimeoutException, org.sat4j.specs.TimeoutException, ContradictionException, CoveringArrayGenerationException{
		GUIDSL m1 = new GUIDSL(new File("TestData/Realistic/Apl.m"));
		SXFM fm = m1.getSXFM();
		CNF cnf = fm.getCNF();
		CoveringArray ca = cnf.getCoveringArrayGenerator("Chvatal", 1);
		ca.generate();
		assertTrue(v.verify1w(cnf, ca));
		
		for(int n = 0; n < ca.getRowCount(); n++){
			SAT4JSolver solver = cnf.getSAT4JSolver();
			if(!solver.solver.isSatisfiable()){
				System.out.println("Feature model not satisfiable");
				System.exit(0);
			}
			
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				//System.out.println("Cannot find \""+ca.nrid.get(i+1)+"\"");
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			//System.out.println(assumps);
			//System.out.println(n + ", " + assumps.size());
			
			// Test
			if(!solver.solver.isSatisfiable(assumps)){
				String out = "Solution invalid: " + n;
				out += "Reason: (";
				for(int x :solver.solver.unsatExplanation().toArray()){
					out += ((x<0)?"-":"") + cnf.getID(Math.abs(x)) + ", ";
				}
				out += ")";
				fail(out);
			}else{
				//System.out.println("Solution valid: " + n);
			}
		}
	}
	
	@Test
	public void verify2w_Chvatal() throws UnsupportedModelException, IOException, FeatureModelException, TimeoutException, org.sat4j.specs.TimeoutException, ContradictionException, CoveringArrayGenerationException{
		GUIDSL m1 = new GUIDSL(new File("TestData/Realistic/Apl.m"));
		SXFM fm = m1.getSXFM();
		CNF cnf = fm.getCNF();
		CoveringArray ca = cnf.getCoveringArrayGenerator("Chvatal", 2);
		ca.generate();
		assertTrue(v.verify2w(cnf, ca));
		
		for(int n = 0; n < ca.getRowCount(); n++){
			SAT4JSolver solver = cnf.getSAT4JSolver();
			if(!solver.solver.isSatisfiable()){
				System.out.println("Feature model not satisfiable");
				System.exit(0);
			}
			
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				//System.out.println("Cannot find \""+ca.nrid.get(i+1)+"\"");
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			//System.out.println(assumps);
			//System.out.println(n + ", " + assumps.size());
			
			// Test
			if(!solver.solver.isSatisfiable(assumps)){
				String out = "Solution invalid: " + n;
				out += "Reason: (";
				for(int x :solver.solver.unsatExplanation().toArray()){
					out += ((x<0)?"-":"") + cnf.getID(Math.abs(x)) + ", ";
				}
				out += ")";
				fail(out);
			}else{
				//System.out.println("Solution valid: " + n);
			}
		}
	}
	
	@Test
	public void verify2w_Chvatal3() throws UnsupportedModelException, IOException, FeatureModelException, TimeoutException, org.sat4j.specs.TimeoutException, ContradictionException, CoveringArrayGenerationException{
		SXFM fm = new SXFM("TestData/Realistic/car_fm.xml");
		CNF cnf = fm.getCNF();
		CoveringArray ca = cnf.getCoveringArrayGenerator("Chvatal", 2);
		ca.generate();
		assertTrue(v.verify2w(cnf, ca));
		
		for(int n = 0; n < ca.getRowCount(); n++){
			SAT4JSolver solver = cnf.getSAT4JSolver();
			if(!solver.solver.isSatisfiable()){
				System.out.println("Feature model not satisfiable");
				System.exit(0);
			}
			
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				//System.out.println("Cannot find \""+ca.nrid.get(i+1)+"\"");
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			//System.out.println(assumps);
			//System.out.println(n + ", " + assumps.size());
			
			// Test
			if(!solver.solver.isSatisfiable(assumps)){
				String out = "Solution invalid: " + n;
				out += "Reason: (";
				for(int x :solver.solver.unsatExplanation().toArray()){
					out += ((x<0)?"-":"") + cnf.getID(Math.abs(x)) + ", ";
				}
				out += ")";
				fail(out);
			}else{
				//System.out.println("Solution valid: " + n);
			}
		}
	}
	
	@Test
	public void verify3w_Chvatal() throws UnsupportedModelException, IOException, FeatureModelException, TimeoutException, org.sat4j.specs.TimeoutException, ContradictionException, CoveringArrayGenerationException{
		SXFM fm = new SXFM("TestData/Realistic/aircraft_fm.xml");
		CNF cnf = fm.getCNF();
		CoveringArray ca = cnf.getCoveringArrayGenerator("Chvatal", 3);
		ca.generate();
		assertTrue(v.verify3w(cnf, ca));
		
		for(int n = 0; n < ca.getRowCount(); n++){
			SAT4JSolver solver = cnf.getSAT4JSolver();
			if(!solver.solver.isSatisfiable()){
				System.out.println("Feature model not satisfiable");
				System.exit(0);
			}
			
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				//System.out.println("Cannot find \""+ca.nrid.get(i+1)+"\"");
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			//System.out.println(assumps);
			//System.out.println(n + ", " + assumps.size());
			
			// Test
			if(!solver.solver.isSatisfiable(assumps)){
				String out = "Solution invalid: " + n;
				out += "Reason: (";
				for(int x :solver.solver.unsatExplanation().toArray()){
					out += ((x<0)?"-":"") + cnf.getID(Math.abs(x)) + ", ";
				}
				out += ")";
				fail(out);
			}else{
				//System.out.println("Solution valid: " + n);
			}
		}
	}
	
	@Test
	public void verify4w_Chvatal() throws UnsupportedModelException, IOException, FeatureModelException, TimeoutException, org.sat4j.specs.TimeoutException, ContradictionException, CoveringArrayGenerationException{
		SXFM fm = new SXFM("TestData/Realistic/aircraft_fm.xml");
		CNF cnf = fm.getCNF();
		CoveringArray ca = cnf.getCoveringArrayGenerator("Chvatal", 4);
		ca.generate();
		
		for(int n = 0; n < ca.getRowCount(); n++){
			SAT4JSolver solver = cnf.getSAT4JSolver();
			if(!solver.solver.isSatisfiable()){
				System.out.println("Feature model not satisfiable");
				System.exit(0);
			}
			
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				//System.out.println("Cannot find \""+ca.nrid.get(i+1)+"\"");
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			//System.out.println(assumps);
			//System.out.println(n + ", " + assumps.size());
			
			// Test
			if(!solver.solver.isSatisfiable(assumps)){
				String out = "Solution invalid: " + n;
				out += "Reason: (";
				for(int x :solver.solver.unsatExplanation().toArray()){
					out += ((x<0)?"-":"") + cnf.getID(Math.abs(x)) + ", ";
				}
				out += ")";
				fail(out);
			}else{
				//System.out.println("Solution valid: " + n);
			}
		}
	}
	
	@Test
	public void verify1w_Alg2() throws UnsupportedModelException, IOException, FeatureModelException, TimeoutException, CoveringArrayGenerationException{
		GUIDSL m1 = new GUIDSL(new File("TestData/Realistic/Apl.m"));
		SXFM fm = m1.getSXFM();
		CNF cnf = fm.getCNF();
		CoveringArray ca = cnf.getCoveringArrayGenerator("ICPL", 1);
		ca.generate();
		assertTrue(v.verify1w(cnf, ca));
	}
	
	@Test
	public void verify2w_ICPL() throws UnsupportedModelException, IOException, FeatureModelException, TimeoutException, CoveringArrayGenerationException{
		GUIDSL m1 = new GUIDSL(new File("TestData/Realistic/Apl.m"));
		SXFM fm = m1.getSXFM();
		CNF cnf = fm.getCNF();
		CoveringArray ca = cnf.getCoveringArrayGenerator("ICPL", 2);
		ca.generate();
		boolean isCovered = v.verify2w(cnf, ca);
		assertTrue(isCovered);
	}
	
	@Test
	public void verify2w_ICPL_2() throws UnsupportedModelException, IOException, FeatureModelException, TimeoutException, CoveringArrayGenerationException{
		SXFM fm = new SXFM("TestData/Realistic/Eshop-fm.xml");
		CNF cnf = fm.getCNF();
		CoveringArray ca = cnf.getCoveringArrayGenerator("ICPL", 2);
		ca.generate();
/*		boolean isCovered = v.verify2w(cnf, ca);
		assertTrue(isCovered);
*/	}
	
	@Test
	public void verify3w_ICPL() throws UnsupportedModelException, IOException, FeatureModelException, TimeoutException, ContradictionException, org.sat4j.specs.TimeoutException, CoveringArrayGenerationException{
		SXFM fm = new SXFM("TestData/Realistic/car_fm.xml");
		CNF cnf = fm.getCNF();
		CoveringArray ca = cnf.getCoveringArrayGenerator("ICPL", 3);
		ca.generate();
		boolean isCovered = v.verify3w(cnf, ca);
		assertTrue(isCovered);
		
		for(int n = 0; n < ca.getRowCount(); n++){
			SAT4JSolver solver = cnf.getSAT4JSolver();
			if(!solver.solver.isSatisfiable()){
				System.out.println("Feature model not satisfiable");
				System.exit(0);
			}
			
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				//System.out.println("Cannot find \""+ca.nrid.get(i+1)+"\"");
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			//System.out.println(assumps);
			//System.out.println(n + ", " + assumps.size());
			
			// Test
			if(!solver.solver.isSatisfiable(assumps)){
				String out = "Solution invalid: " + n;
				out += "Reason: (";
				for(int x :solver.solver.unsatExplanation().toArray()){
					out += ((x<0)?"-":"") + cnf.getID(Math.abs(x)) + ", ";
				}
				out += ")";
				fail(out);
			}else{
				//System.out.println("Solution valid: " + n);
			}
		}
	}
	
	@Test
	public void verifySatICPL_1() throws IOException, ParseFormatException, ContradictionException, org.sat4j.specs.TimeoutException, CSVException{
		ISolver solver = SolverFactory.newDefault();
		Reader reader = new LecteurDimacs(solver);
		IProblem problem = reader.parseInstance("TestData/Realistic/ecos-icse11.dimacs");
		assertTrue(problem.isSatisfiable());
		
		CNF cnf = new CNF("TestData/Realistic/ecos-icse11.dimacs", CNF.type.dimacs);
		
		CoveringArray ca = new CoveringArrayFile("reports/bestcoverages/ecos-icse11-size66-1thread.dimacs.ca2.csv");
		for(int n = 0; n < ca.getRowCount(); n++){
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				//System.out.println(i+1 + ", " + ca.nrid.get(i+1));
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			//System.out.println(assumps);
			//System.out.println(n + ", " + assumps.size());
			
			// Test
			assertTrue(problem.isSatisfiable(assumps));
		}
	}
	
	@Test
	public void verifySatICPL_2() throws IOException, ParseFormatException, ContradictionException, org.sat4j.specs.TimeoutException, CSVException{
		ISolver solver = SolverFactory.newDefault();
		Reader reader = new LecteurDimacs(solver);
		IProblem problem = reader.parseInstance("TestData/Realistic/freebsd-icse11.dimacs");
		assertTrue(problem.isSatisfiable());
		
		CNF cnf = new CNF("TestData/Realistic/freebsd-icse11.dimacs", CNF.type.dimacs);
		
		CoveringArray ca = new CoveringArrayFile("reports/bestcoverages/freebsd-icse11-size78-1thread.dimacs.ca2.csv");
		for(int n = 0; n < ca.getRowCount(); n++){
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			
			// Test
			assertTrue(problem.isSatisfiable(assumps));
		}
	}
	
	@Test
	public void verifySatICPL_3() throws IOException, ParseFormatException, ContradictionException, org.sat4j.specs.TimeoutException, CSVException{
		ISolver solver = SolverFactory.newDefault();
		Reader reader = new LecteurDimacs(solver);
		IProblem problem = reader.parseInstance("TestData/Realistic/2.6.28.6-icse11.dimacs");
		assertTrue(problem.isSatisfiable());
		
		CNF cnf = new CNF("TestData/Realistic/2.6.28.6-icse11.dimacs", CNF.type.dimacs);
		
		CoveringArray ca = new CoveringArrayFile("reports/bestcoverages/2.6.28.6-icse11-size469-1thread.dimacs.ca2.csv");
		for(int n = 0; n < ca.getRowCount(); n++){
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			//System.out.println(n + ", " + assumps.size());
			
			// Test
			assertTrue(problem.isSatisfiable(assumps));
		}
	}
}
