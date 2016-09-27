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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;

import splar.core.fm.FeatureModelException;
import splar.plugins.reasoners.bdd.javabdd.BDDExceededBuildingTimeException;

import de.ovgu.featureide.fm.core.io.UnsupportedModelException;

public class SPLCATool {
	
	GUIDSL m = null;
	CNF cnf = null;
	SXFM fm = null;
	private CASAModel cm;
	
	static SPLCATool vspl;

	public static void main(String[] args){
		vspl = new SPLCATool();
		vspl.mainObj(args);
	}
	
	public void mainObj(String[] args){
		System.out.println("SPL Covering Array Tool v0.3 (SPLC 2012) (ICPL edition)");
		System.out.println("http://heim.ifi.uio.no/martifag/splc2012/");
		
		// Defaults
		Map<String, String> argsMap = new HashMap<String, String>();
		argsMap.put("t", "help");
        argsMap.put("limit", "100%");
        argsMap.put("a", "ICPL");
		
		// Get args
		String last = "";
		for(int i = 0; i < args.length; i++){
			if(args[i].startsWith("-")){
				last = args[i].substring(1);
				argsMap.put(last, "");
			}else{
				String params = argsMap.get(last);
				params += " " + args[i];
				argsMap.put(last, params.trim());
			}
		}
		
		System.out.println("Args: " + argsMap);
		
		String task = argsMap.get("t");
		
		try {
			if(task.equals("help")){
				System.out.println("Usage: <task>");
				System.out.println("Tasks: ");
				System.out.println(" -t count_solutions -fm <feature_model>");
				System.out.println(" -t sat_time -fm <feature_model>");
				System.out.println(" -t t_wise -a Chvatal -fm <feature_model> -s <strength, 1-4> (-startFrom <covering array>) (-limit <coverage limit>) (-sizelimit <rows>) (-onlyOnes) (-noAllZeros)");
				System.out.println(" -t t_wise -a ICPL -fm <feature_model> -s <strength, 1-3> (-startFrom <covering array>) (-onlyOnes) (-noAllZeros) [Inexact: (-sizelimit <rows>) (-limit <coverage limit>)] (for 3-wise, -eights <1-8>)");
				System.out.println(" -t t_wise -a CASA -fm <feature_model> -s <strength, 1-6>");
				System.out.println(" -t calc_cov -fm <feature_model> -s <strength> -ca <covering array>");
				System.out.println(" -t verify_solutions -fm <feature_model> -check <covering array>");
				System.out.println(" -t help (this menu)");
				System.out.println("Supported Feature models formats: ");
				System.out.println(" - Feature IDE GUI DSL (.m)");
				System.out.println(" - Simple XML Feature Models (.xml)");
				System.out.println(" - Dimacs (.dimacs)");
				System.out.println(" - CNF (.cnf)");
				return;
			}else if(task.equals("count_solutions")){
				count_solutions(argsMap);
			}else if(task.equals("sat_time")){
				sat_time(argsMap);
			}else if(task.equals("t_wise")){
				String alg = argsMap.get("a");
				if(alg.equals("CASA")){
					t_wise_casa(argsMap);
				}else{
					t_wise(argsMap);
				}
			}else if(task.equals("calc_cov")){
				calc_cov(argsMap);
			}else if(task.equals("verify_solutions")){
				verify_solutions(argsMap);
			}else{
				System.out.println("task " + task + " unknown");
				throw new UnsupportedOperationException();
			}
		} catch (UnsupportedModelException e) {
			System.err.println("Failed to load feature model: " + argsMap.get("fm"));
			System.err.println("Exception: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("File IO failed.");
			System.err.println("Exception: " + e.getMessage());
		} catch (FeatureModelException e) {
			System.err.println("Failed to load feature model: " + argsMap.get("fm"));
			System.err.println("Exception: " + e.getMessage());
		} catch (BDDExceededBuildingTimeException e) {
			System.err.println("BDD Exceeded Building Time.");
			System.err.println("Exception: " + e.getMessage());
		} catch (ContradictionException e) {
			System.err.println("SAT4J reached a contradiction.");
			System.err.println("Exception: " + e.getMessage());
		} catch (TimeoutException e) {
			System.err.println("SAT4J timed out.");
			System.err.println("Exception: " + e.getMessage());
		} catch (java.util.concurrent.TimeoutException e) {
			System.err.println("Java timeout reached.");
			System.err.println("Exception: " + e.getMessage());
		} catch (CSVException e) {
			System.err.println("Error in CSV file.");
			System.err.println("Exception: " + e.getMessage());
		} catch (CoveringArrayGenerationException e) {
			System.err.println("Error generating covering array.");
			System.err.println("Exception: " + e.getMessage());
		}
	}

	boolean verify_solutions(Map<String, String> argsMap)
			throws UnsupportedModelException, IOException,
			FeatureModelException, ContradictionException, TimeoutException, CSVException {
		loadFM(argsMap.get("fm"));
		CoveringArray ca = new CoveringArrayFile(argsMap.get("check"));
		boolean isValid = verifyCA(cnf, ca);
		System.out.println("Verification done");
		
		return isValid;
	}

	float calc_cov(Map<String, String> argsMap) throws UnsupportedModelException, IOException, FeatureModelException, CSVException {
		int t = new Integer(argsMap.get("s"));
		loadFM(argsMap.get("fm"));
		CoveringArray ca = new CoveringArrayFile(argsMap.get("ca"));
		System.out.println("Rows: " + ca.getRowCount());
		
		if(t==1){
			// Calculate the valid pairs
			List<Pair> uncovered = cnf.getAllValidSingles(1);
			System.out.println("Valid singles: " + uncovered.size());
			
			// Calculate the covered pairs
			List<List<Integer>> sols = ca.getSolutionsAsList();
			Set<Pair> coveredPairs = ca.getCovInv1(sols, uncovered);
			System.out.println("Covered singles: " + coveredPairs.size());
			
			// Coverage
			float coverage = (float)coveredPairs.size()*100/uncovered.size();
			System.out.println("Coverage: " + coveredPairs.size() + "/" + uncovered.size() + " = " + coverage + "%");
			return coverage;
		}else if(t==2){
			// Calculate the valid pairs
			List<Pair2> uncovered = cnf.getAllValidPairs(1);
			System.out.println("Valid pairs: " + uncovered.size());
			
			// Calculate the covered pairs
			List<List<Integer>> sols = ca.getSolutionsAsList();
			long coveredPairs = ca.getCovInvCount(sols, uncovered);
			System.out.println("Covered pairs: " + coveredPairs);
			
			// Coverage
			float coverage = (float)coveredPairs*100/uncovered.size();
			System.out.println("Coverage: " + coveredPairs + "/" + uncovered.size() + " = " + coverage + "%");
			return coverage;
		}else if(t==3){
			// Calculate the valid pairs
			List<Pair3> u3 = cnf.getAllValidTriples(1);
			System.out.println("Valid triples: " + u3.size());
			
			// Calculate the covered pairs
			List<List<Integer>> sols = ca.getSolutionsAsList();
			long coveredTriples = ca.getCovCount3(sols, u3);
			System.out.println("Covered triples: " + coveredTriples);
			
			// Coverage
			float coverage = (float)coveredTriples*100/u3.size();
			System.out.println("Coverage: " + coveredTriples + "/" + u3.size() + " = " + coverage + "%");
			
			return coverage;
		}else{
			System.out.println("Unsupported value of t: " + t);
			throw new UnsupportedOperationException();
		}
	}

	public CoveringArray t_wise(Map<String, String> argsMap) throws UnsupportedModelException, IOException, FeatureModelException, java.util.concurrent.TimeoutException, FileNotFoundException, CSVException, CoveringArrayGenerationException {
		String fmfile = argsMap.get("fm");
		if(fmfile==null){
			System.out.println("Error: You must specify a feature model.");
			return null;
		}
		loadFM(fmfile);
		
		// Handle special multi-file formats
		if(fmfile.contains(",")){
			fmfile = fmfile.split(",")[0];
		}
		
		// Algorithm
		if(!argsMap.get("a").equals("ICPL") && !argsMap.get("a").equals("Chvatal")){
			System.out.println("Unsupported algorithm: " + argsMap.get("a"));
			return null;
		}
		String a = argsMap.get("a");
		
		// Strength
		if(argsMap.get("s")==null){
			System.out.println("Error: You must specify a coverage strength.");
			return null;
		}
		try{
			Integer.parseInt(argsMap.get("s"));
		}catch(NumberFormatException e){
			System.out.println("s must be an integer value.");
			return null;
		}
		int t = new Integer(argsMap.get("s"));
		if(t<1){
			System.out.println("Unsupported value for t: " + t);
			return null;
		}
		if(a.equals("ICPL") && t > 3){
			System.out.println("Unsupported value for t for algorithm ICPL: " + t);
			return null;
		}
		if(a.equals("Chvatal") && t > 4){
			System.out.println("Unsupported value for t for algorithm Chvatal: " + t);
			return null;
		}
		
		// Make CA
		System.out.println("Generating "+t+"-wise covering array with algorithm: " + a);
		CoveringArray ca = null;
		
		ca = cnf.getCoveringArrayGenerator(a, t);
		
		// Do not cover all zero tuples
		if(argsMap.containsKey("noAllZeros")){
			ca.coverZeros(false);
		}
		
		// Cover only 
		if(argsMap.containsKey("onlyOnes")){
			ca.coverOnlyOnes(true);
		}
		
		// Cover eight only
		if(argsMap.containsKey("eights")){
			ca.coverEightOnly(new Integer(argsMap.get("eights")));
		}
		
		// -
		System.out.println("Running algorithm: " + ca.getAlgorithmName());
		
		// Limit
		String lim = argsMap.get("limit");
		if(lim.contains("%")) lim = lim.substring(0, lim.length()-1);
		int limit = new Integer(lim);
		System.out.println("Covering " + limit + "%");
		
		// Size limit
		Integer sizelimit = Integer.MAX_VALUE;
		if(argsMap.get("sizelimit") != null){
			sizelimit = new Integer(argsMap.get("sizelimit"));
		}
		
		// Start from partial covering array
		if(argsMap.containsKey("startFrom")){
			CoveringArray startFrom = new CoveringArrayFile(argsMap.get("startFrom"));
			ca.startFrom(startFrom);
			System.out.println("Starting from " + argsMap.get("startFrom"));
		}
		
		// Cover
		long start = System.currentTimeMillis();
		ca.generate(limit, sizelimit);
		long end = System.currentTimeMillis();
		System.out.println("Done. Size: " + ca.getRowCount() + ", time: " + (end-start) + " milliseconds");
		
		if(argsMap.containsKey("BTR")){
			if(fm == null){
				System.out.println("BTR only supported for SXFM and GUIDSL");
			}else{
				ca.bowTieReduce(fmfile + ".afm", fm);
				System.out.println("BTR. Size: " + ca.getRowCountBTR());
			}
		}
		
		// Generate output file name
		String cafilename = argsMap.get("o");
		if(cafilename == null)
			cafilename = fmfile + ".ca" + t + ".csv";
		
		// Write to file
		ca.writeToFile(cafilename, CoveringArrayFile.Type.horizontal);
		System.out.println("Wrote result to " + cafilename);
		
		// Bow-tie reduce
		if(argsMap.get("BTR") != null){
			ca.bowTieReduce(fmfile+".afm", fm);
			ca.writeToFile(cafilename + ".btr.csv");
			System.out.println("Wrote result to " + cafilename + ".btr.csv");
		}
		
		return ca;
	}

	CoveringArray t_wise_casa(Map<String, String> argsMap) throws UnsupportedModelException, IOException, FeatureModelException, CoveringArrayGenerationException, java.util.concurrent.TimeoutException {
		String fmfile = argsMap.get("fm");
		if(fmfile==null){
			System.out.println("Error: You must specify a feature model.");
			return null;
		}
		loadFM(fmfile);
		
		// Handle special multi-file formats
		if(fmfile.contains(",")){
			fmfile = fmfile.split(",")[0];
		}
		
		// Strength
		if(argsMap.get("s")==null){
			System.out.println("Error: You must specify a coverage strength.");
			return null;
		}
		try{
			Integer.parseInt(argsMap.get("s"));
		}catch(NumberFormatException e){
			System.out.println("s must be an integer value.");
			return null;
		}
		int t = new Integer(argsMap.get("s"));
		if(t < 1){
			System.out.println("Unsupported value for t: " + t);
			return null;
		}
		if(t > 6){
			System.out.println("Unsupported value for t: " + t);
			return null;
		}
		
		// Make CA
		System.out.println("Generating "+t+"-wise covering array with algorithm: " + argsMap.get("a"));
		CoveringArray ca = null;
		
		if(cm == null)
			cm = cnf.getCASAInput();
		ca = cm.getCoveringArrayGenerator(t);
		
		// -
		System.out.println("Running algorithm: " + ca.getAlgorithmName());
		
		// Cover
		long start = System.currentTimeMillis();
		ca.generate();
		long end = System.currentTimeMillis();
		System.out.println("Done. Size: " + ca.getRowCount() + ", time: " + (end-start) + " milliseconds");
		
		// Generate output file name
		String cafilename = argsMap.get("o");
		if(cafilename == null)
			cafilename = fmfile + ".ca" + t + ".csv";
		
		// Write to file
		ca.writeToFile(cafilename, CoveringArrayFile.Type.horizontal);
		System.out.println("Wrote result to " + cafilename);
		
		return ca;
	}

	long sat_time(Map<String, String> argsMap)
			throws UnsupportedModelException, IOException,
			FeatureModelException, ContradictionException, TimeoutException {
		loadFM(argsMap.get("fm"));
		System.out.println("Satisfying the feature model");
		
		long start, end;
		
		SAT4JSolver s = null;
		s = cnf.getSAT4JSolver();
		//start = System.currentTimeMillis();
		start = System.nanoTime();
		boolean issat = s.isSatisfiable();
		//end = System.currentTimeMillis();
		end = System.nanoTime();
		System.out.println("SAT done: " + (end-start)/1000 + " microseconds, sat: " + issat);
		
		return (end-start)/1000;
	}

	double count_solutions(Map<String, String> argsMap)
			throws UnsupportedModelException, IOException,
			FeatureModelException, BDDExceededBuildingTimeException {
		loadFM(argsMap.get("fm"));
		System.out.println("Counting solutions");
		double sols = fm.getNrOfProducts();
		System.out.println("Solutions: " + sols);
		return sols;
	}

	@Override
	public String toString() {
		return "SPLCATool [m=" + m + ", cnf=" + cnf + ", fm=" + fm + "]";
	}

	boolean verifyCA(CNF cnf, CoveringArray ca) throws ContradictionException,
			TimeoutException {
		boolean allvalid = true;
		
		SAT4JSolver solver = cnf.getSAT4JSolver();
		if(!solver.solver.isSatisfiable()){
			System.out.println("Feature model not satisfiable");
			System.exit(0);
		}
		for(int n = 0; n < ca.getRowCount(); n++){
			// Convert
			Integer[] solinteger = ca.getRow(n);
			int[] sol = new int[solinteger.length];
			for(int i = 0; i < sol.length; i++){
				if(cnf.getNr(ca.getId(i+1)) == null){
					System.out.println("Cannot find \""+ca.nrid.get(i+1)+"\" in feature model, it is in the covering array");
					return false;
				}
				sol[i] = cnf.getNr(ca.getId(i+1));
				if(solinteger[i]==1) sol[i] = -sol[i];
			}
			IVecInt assumps = new VecInt(sol);
			//System.out.println(assumps);
			//System.out.println(n + ", " + assumps.size());
			
			// Test
			if(!solver.solver.isSatisfiable(assumps)){
				System.out.println("Solution invalid: " + n);
				System.out.print("Reason: (");
				for(int x :solver.solver.unsatExplanation().toArray()){
					System.out.print(((x<0)?"-":"") + cnf.getID(Math.abs(x)) + ", ");
				}
				System.out.println(")");
				
				//allvalid = false;
				return false;
			}else{
				//System.out.println("Solution valid: " + n);
			}
		}
		
		return allvalid;
	}

	private void loadFM(String file) throws UnsupportedModelException,
			IOException, FeatureModelException {
		if(file.endsWith(".m")){
			System.out.println("Loading GUI DSL: " + file);
			m = new GUIDSL(new File(file));
			fm = m.getSXFM();
			cnf = fm.getCNF();
		}else if(file.endsWith(".xml")){
			System.out.println("Loading SXFM: " + file);
			fm = new SXFM(file);
			cnf = fm.getCNF();
		}else if(file.endsWith(".dimacs")){
			System.out.println("Loading dimacs: " + file);
			cnf = new CNF(file, CNF.type.dimacs);
		}else if(file.endsWith(".cnf")){
			System.out.println("Loading cnf: " + file);
			cnf = new CNF(file, CNF.type.cnf);
		}else if(file.endsWith(".dot")){
			System.out.println("Loading DOT: " + file);
			cnf = new CNF(file, CNF.type.dot);
		}/*else if(file.endsWith(".constraints")){
			System.out.println("Loading CASA Model: " + file);
			CASAModel cm = new CASAModel(new File(file.split(",")[0]), new File(file.split(",")[1]));
			String guiDSL = cm.getGUIDSL();
			m = new GUIDSL(guiDSL);
			fm = m.getSXFM();
			cnf = fm.getCNF();
		}*/else{
			System.out.println("Unable to load file: " + file);
			return;
		}
		
		System.out.println("Successfully loaded and converted the model:");
		if(fm!=null){
			System.out.println("Features: " + fm.getFeatures());
			System.out.println("Constraints: " + fm.fm.countConstraints());
		}else if(cnf!=null){
			System.out.println("Features: " + cnf.getVariables().size());
			System.out.println("Constraints: " + cnf.getClauses().size());
		}
	}

}
