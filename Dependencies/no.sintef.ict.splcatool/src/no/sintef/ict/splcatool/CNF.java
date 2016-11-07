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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeoutException;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVecInt;

import splar.core.constraints.BooleanVariable;
import splar.core.constraints.BooleanVariableInterface;
import splar.core.constraints.CNFClause;
import splar.core.constraints.CNFFormula;
import splar.core.constraints.CNFLiteral;

public class CNF {
	public enum type {dimacs, dot, cnf}

	@Override
	public boolean equals(Object _cnfb) {
		CNF cnfb = (CNF) _cnfb;
		CNF cnfa = this;
	
		System.out.println(cnfa.cnf.getVariables().size());
		System.out.println(cnfb.cnf.getVariables().size());

		if(!cnfa.cnf.getVariables().containsAll(cnfb.cnf.getVariables())){
			return false;
		}

/*		if(!cnfb.cnf.getLiterals().containsAll(cnfa.cnf.getLiterals())){
			return false;
		}
*/		
		return true;
	}

	private CNFFormula cnf;
	private Map<String, Integer> idnr = new HashMap<String, Integer>();
	private Map<Integer, String> nrid = new HashMap<Integer, String>();
	private File fmdir;
	
	public Integer getNr(String id){
		return idnr.get(id);
	}
	
	public String getID(Integer nr){
		return nrid.get(nr);
	}
	
	public Set<BooleanVariableInterface> getVariables(){
		return cnf.getVariables();
	}
	
	public Set<CNFClause> getClauses(){
		return cnf.getClauses();
	}

	public CNF(CNFFormula cnf) {
		this.cnf = cnf;
		
		// Init id-number mappings
		int i = 1;
		for(BooleanVariableInterface v : cnf.getVariables()){
			nrid.put(new Integer(i), v.getID());
			idnr.put(v.getID(), new Integer(i));
			//System.out.println(new Integer(i) + ":" + v.getID());
			i++;
		}
	}
	
	public CNF(String fmfile, type t) throws IOException{
		if(t == type.dimacs){
			loadDimacs(fmfile);
		}else if(t == type.dot){
			loadDot(fmfile);
		}else if(t == type.cnf){
			loadCnf(fmfile);
		}
	}
	
	private void loadCnf(String file) throws IOException {
		//Map<Integer, String> nrid = new HashMap<Integer, String>();
		Set<Set<Integer>> cs = new HashSet<Set<Integer>>();
		
		String filec = new FileUtility().readFileAsString(file);
		
		int j = 0;
		int c_count = 0;
		int nc_count = 0;
		int p_count = 0;
		int given_p = 0;
		int given_c = 0;
		for(String line : filec.split("\n")){
			line = line.trim();
			if(line.startsWith("c") && line.split(" ").length==2){
				//System.out.println(line);
				String nr = line.split(" ")[1].replace("$", "").split("->")[0];
				String id = line.split("->")[1];
				nrid.put(new Integer(nr), id);
				idnr.put(id, new Integer(nr));
				p_count++;
			}else if(line.endsWith(" 0")){
				Set<Integer> c = new HashSet<Integer>();
				line = line.substring(0, line.length()-1).trim(); // Remove end 0
				for(String p : line.split(" ")){
					c.add(new Integer(p));
				}
				int oldsize = cs.size();
				cs.add(c);
				if(oldsize == cs.size()){
					nc_count++;
					/*System.out.println("Duplicate line: " + line);
					List<Set<Integer>> nns = new ArrayList<Set<Integer>>(cs);
					for(int i = 0; i < nns.size(); i++){
						if(nns.get(i).equals(c)){
							System.out.println(nns.get(i));
						}
					}
					System.exit(0);*/
				}else{
					c_count++;
				}
				j++;
			}else if(line.startsWith("p cnf")){
				given_p = new Integer(line.split(" ")[2]);
				given_c = new Integer(line.split(" ")[3]);
			}else{
				System.out.println("Error loading file due to: " + line);
				System.exit(-1);
			}
		}
		
		if(given_p != p_count || (c_count + nc_count) != given_c){
			System.out.println("Given p and c not equal with actual p and c: " + given_p + " and " + given_c + " vs " + p_count + " and " + (c_count + nc_count));
			System.exit(-1);
		}
		
		System.out.println("CNF: Given p and c: " + given_p + " and " + given_c);
		/*
		System.out.println("Features: " + p_count);
		System.out.println("Constraints: " + c_count);
		System.out.println("All constraints: " + (c_count + nc_count));
		*/
		
		// Write CNF
		Map<String, BooleanVariableInterface> vars = new HashMap<String, BooleanVariableInterface>();
		CNFFormula cnf = new CNFFormula();
		for(Set<Integer> clause : cs){
			CNFClause cl = new CNFClause();
			
			//System.out.println("Clause: " + clause);
		
			for(Integer p : clause){
				boolean isNegative = (p<0);
				BooleanVariableInterface bv = null;
				if(vars.get(nrid.get(Math.abs(p))) == null){
					//System.out.println(nrid.get(Math.abs(p)));
					bv = new BooleanVariable(nrid.get(Math.abs(p)));
					vars.put(nrid.get(Math.abs(p)), bv);
				}else{
					bv = vars.get(nrid.get(Math.abs(p)));
				}
				CNFLiteral l = new CNFLiteral(bv, !isNegative);
				cl.addLiteral(l);
			}
			cnf.addClause(cl);
		}
		
		this.cnf = cnf;
	}

	private void loadDimacs(String dimacsFile) throws IOException{
		Set<Set<Integer>> cs = new HashSet<Set<Integer>>();
		
		String filec = new FileUtility().readFileAsString(dimacsFile);
		
		int j = 0;
		int c_count = 0;
		int nc_count = 0;
		int p_count = 0;
		int given_p = 0;
		int given_c = 0;
		for(String line : filec.split("\n")){
			line = line.trim();
			if(line.startsWith("c") && line.split(" ").length==3){
				//System.out.println(line);
				String nr = line.split(" ")[1].replace("$", "");
				String id = line.split(" ")[2];
				nrid.put(new Integer(nr), id);
				idnr.put(id, new Integer(nr));
				p_count++;
			}else if(line.endsWith(" 0")){
				Set<Integer> c = new HashSet<Integer>();
				line = line.substring(0, line.length()-1).trim(); // Remove end 0
				for(String p : line.split(" ")){
					c.add(new Integer(p));
				}
				int oldsize = cs.size();
				cs.add(c);
				if(oldsize == cs.size()){
					nc_count++;
					/*System.out.println("Duplicate line: " + line);
					List<Set<Integer>> nns = new ArrayList<Set<Integer>>(cs);
					for(int i = 0; i < nns.size(); i++){
						if(nns.get(i).equals(c)){
							System.out.println(nns.get(i));
						}
					}
					System.exit(0);*/
				}else{
					c_count++;
				}
				j++;
			}else if(line.startsWith("p cnf")){
				given_p = new Integer(line.split(" ")[2]);
				given_c = new Integer(line.split(" ")[3]);
			}else{
				System.out.println("Error loading file due to: " + line);
				System.exit(-1);
			}
		}
		
		if(given_p != p_count || (c_count + nc_count) != given_c){
			System.out.println("Given p and c not equal with actual p and c: " + given_p + " and " + given_c + " vs " + p_count + " and " + (c_count + nc_count));
			System.exit(-1);
		}
		
		System.out.println("CNF: Given p and c: " + given_p + " and " + given_c);
		/*
		System.out.println("Features: " + p_count);
		System.out.println("Constraints: " + c_count);
		System.out.println("All constraints: " + (c_count + nc_count));
		*/
		
		// Write CNF
		Map<String, BooleanVariableInterface> vars = new HashMap<String, BooleanVariableInterface>();
		Set<String> stored = new HashSet<String>();
		CNFFormula cnf = new CNFFormula();
		for(Set<Integer> clause : cs){
			CNFClause cl = new CNFClause();
			
			//System.out.println("Clause: " + clause);
		
			for(Integer p : clause){
				boolean isNegative = (p<0);
				BooleanVariableInterface bv = null;
				if(vars.get(nrid.get(Math.abs(p))) == null){
					//System.out.println(nrid.get(Math.abs(p)));
					bv = new BooleanVariable(nrid.get(Math.abs(p)));
					vars.put(nrid.get(Math.abs(p)), bv);
				}else{
					bv = vars.get(nrid.get(Math.abs(p)));
				}
				stored.add(bv.getID());
				CNFLiteral l = new CNFLiteral(bv, !isNegative);
				cl.addLiteral(l);
			}
			cnf.addClause(cl);
		}
		
		// Add the remaining vars
/*		System.out.println(stored.size());
		System.out.println(nrid.values().size());
*/		for(String x : nrid.values()){
			if(!stored.contains(x)){
				BooleanVariableInterface bv = new BooleanVariable(x);
				vars.put(x, bv);
				CNFClause cl = new CNFClause();
				CNFLiteral l1 = new CNFLiteral(bv, true);
				CNFLiteral l2 = new CNFLiteral(bv, true);
				cl.addLiteral(l1);
				cl.addLiteral(l2);
				cnf.addClause(cl);
			}
		}
		
		this.cnf = cnf;
	}
	
	private void loadDot(String dotFile) throws IOException{
		Map<Integer, String> nrid = new HashMap<Integer, String>();
		Set<Set<Integer>> cs = new HashSet<Set<Integer>>();
		
		String filec = new FileUtility().readFileAsString(dotFile);
		
		for(String line : filec.split("\n")){
			line = line.trim();
			if(line.contains(" [label = ")){
				//System.out.println(line);
				String nr = line.split(" ")[0];
				String id = line.substring(("  "+nr+" [label = \"").length(), line.length()-3);
				nrid.put(new Integer(nr), id);
				idnr.put(id, new Integer(nr));
			}else if(line.contains("->")){
				Set<Integer> c = new HashSet<Integer>();
				line = line.substring(0, line.length()-1).trim(); // Remove end ;
				for(String p : line.split(" -> ")){
					c.add(new Integer(p));
				}
				cs.add(c);
			}
		}
		
		//System.out.println("Features: " + nrid.size());
		//System.out.println("Constraints: " + cs.size());
		
		// Write CNF
		Map<String, BooleanVariableInterface> vars = new HashMap<String, BooleanVariableInterface>();
		CNFFormula cnf = new CNFFormula();
		for(Set<Integer> clause : cs){
			CNFClause cl = new CNFClause();
			
			//System.out.println("Clause: " + clause);
		
			boolean isNegative = true; // first is true, second is false
			for(Integer p : clause){
				BooleanVariableInterface bv = null;
				if(vars.get(nrid.get(Math.abs(p))) == null){
					bv = new BooleanVariable(nrid.get(p));
					vars.put(nrid.get(p), bv);
				}else{
					bv = vars.get(nrid.get(p));
				}
				CNFLiteral l = new CNFLiteral(bv, !isNegative);
				cl.addLiteral(l);
				isNegative = false;
			}
			cnf.addClause(cl);
		}
		
		this.cnf = cnf;
	}

	public SAT4JSolver getSAT4JSolver() throws ContradictionException {
		// Load into SAT4J
		final int MAXVAR = cnf.countVariables();
		final int NBCLAUSES = cnf.countClauses();
		ISolver solver = SolverFactory.newDefault();
		solver.setDBSimplificationAllowed(true);
		solver.newVar(MAXVAR);
		solver.setExpectedNumberOfClauses(NBCLAUSES);
/*		int i = 1;
		synchronized(this){
		for(BooleanVariableInterface v : cnf.getVariables()){
			idnr.put(v.getID(), i);
			nrid.put(i, v.getID());
			i++;
		}
		}
*/		
		for(CNFClause c : cnf.getClauses()){
			//System.out.println("clause " + x++ + " of " + cnf.getClauses().size());
			
			List<Integer> li = new ArrayList<Integer>();
			for(CNFLiteral l : c.getLiterals()){
				//System.out.println(idnr.get(l.getVariable().getID()));
				if(l.isPositive()){
					li.add(idnr.get(l.getVariable().getID()));
				}else{
					li.add(-idnr.get(l.getVariable().getID()));
				}
			}
			int [] clause = new int [li.size()];
			for(int j=0;j<li.size();j++)
				clause[j] = li.get(j);

			try{
				solver.addClause((IVecInt) new VecInt(clause));
			}catch(ContradictionException ce){
				System.out.println("empty clause:" + c);
				System.out.println("empty clause:" + clause.length);
			}
			
/*			try {
				if(!solver.isSatisfiable()){
					return null;
				}
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/		}
		
		return new SAT4JSolver((ISolver)solver);
	}
	
	public SAT4JSolver getSAT4JSolverInverse() throws ContradictionException {
		// Load into SAT4J
		final int MAXVAR = cnf.countVariables();
		final int NBCLAUSES = cnf.countClauses();
		ISolver solver = SolverFactory.newDefault();
		solver.setDBSimplificationAllowed(true);
		solver.newVar(MAXVAR);
		solver.setExpectedNumberOfClauses(NBCLAUSES);
/*		int i = 1;
		synchronized(this){
		for(BooleanVariableInterface v : cnf.getVariables()){
			idnr.put(v.getID(), i);
			nrid.put(i, v.getID());
			i++;
		}
		}
*/		
		for(CNFClause c : cnf.getClauses()){
			//System.out.println("clause " + x++ + " of " + cnf.getClauses().size());
			
			List<Integer> li = new ArrayList<Integer>();
			for(CNFLiteral l : c.getLiterals()){
				//System.out.println(idnr.get(l.getVariable().getID()));
				if(l.isPositive()){
					li.add(-idnr.get(l.getVariable().getID()));
				}else{
					li.add(idnr.get(l.getVariable().getID()));
				}
			}
			int [] clause = new int [li.size()];
			for(int j=0;j<li.size();j++)
				clause[j] = li.get(j);

			try{
				solver.addClause((IVecInt) new VecInt(clause));
			}catch(ContradictionException ce){
				System.out.println("empty clause:" + c);
				System.out.println("empty clause:" + clause.length);
			}
			
/*			try {
				if(!solver.isSatisfiable()){
					return null;
				}
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
*/		}
		
		return new SAT4JSolver((ISolver)solver);
	}

	private CoveringArray getCoveringArrayGeneratorICPL(int t) {
		return new CoveringArrayAlgICPL(t, this, nrid, idnr);
	}
	
	private CoveringArray getCoveringArrayGeneratorChvatal(int t) {
		return new CoveringArrayChvatal(t, this, nrid, idnr);
	}
	
	@Override
	public String toString() {
		return cnf.toString();
	}

	public CoveringArray getCoveringArrayGenerator(String a, int t) {
		if(a.equals("ICPL")){
			if(t==1 || t==2 || t==3) return getCoveringArrayGeneratorICPL(t);
			else return null;
		}else if(a.equals("Chvatal")){
			if(t==1 || t==2 || t==3 || t==4) return getCoveringArrayGeneratorChvatal(t);
			else return null;
		}else{
			return null;
		}
	}

	public void writeToFile(String filename) throws FileNotFoundException, IOException{
		StringBuffer sb = new StringBuffer();
		for(BooleanVariableInterface v : cnf.getVariables()){
			sb.append("c " + getNr(v.getID()) + " " + v.getID() + "\n");
		}
		sb.append("p cnf " + cnf.getVariables().size() + " " + cnf.getClauses().size() + "\n");
		for(CNFClause c : cnf.getClauses()){
			for(CNFLiteral l : c.getLiterals())
				sb.append((l.isPositive()?1:-1) * getNr(l.getVariable().getID()) + " ");
			sb.append("0\n");
		}
		new FileUtility().writeStringToFile(filename, sb.toString());
	}

	public CASAModel getCASAInput(){
		List<String> ms = new ArrayList<String>();
		List<List<String>> cs = new ArrayList<List<String>>();
		
		List<BooleanVariableInterface> vlist = new ArrayList<BooleanVariableInterface>(cnf.getVariables());
		
		// Add variables
		for(BooleanVariableInterface v : vlist){
			ms.add("2");
		}
		
		// Add constraints
		for(CNFClause c : cnf.getClauses()){
			List<String> literals = new ArrayList<String>();
			for(CNFLiteral l : c.getLiterals()){
				int nr = 0;
				for(int i = 0; i < vlist.size(); i++){
					if(vlist.get(i).equals(l.getVariable())){
						nr = i;
						break;
					}
				}
				if(l.isPositive()){
					//System.out.print(" + ");
					literals.add("" + 2*nr);
				}else{
					//System.out.print(" - ");
					literals.add("-" + (2*nr));
				}
				//System.out.print(2*nr);
			}
			//System.out.println();
			cs.add(literals);
		}
		
		CASAModel cm = new CASAModel(vlist, ms, cs);
		return cm;
	}

	public List<Pair> getAllValidSingles(int threads) {
		List<Pair> uncovered = new ArrayList<Pair>();
		
	    // Get a list of vars
		List<BooleanVariableInterface> vars = new ArrayList<BooleanVariableInterface>(cnf.getVariables());
		
		// Find mandatory and dead features
	    Set<BooleanVariableInterface> mandatory = new HashSet<BooleanVariableInterface>();
		Set<BooleanVariableInterface> dead = new HashSet<BooleanVariableInterface>();
		MandatoryAndDeadDetection mdd = new MandatoryAndDeadDetection(this, nrid); 
		mdd.findMandatoryAndDeadFeatures(vars, mandatory, dead);
		System.out.println("Mandatory: " + mandatory.size());
		System.out.println("Dead: " + dead.size());
		
		for(BooleanVariableInterface var1 : vars){
			Pair unc;
			if(!mandatory.contains(var1)){
				unc = new Pair();
				unc.v = var1;
				unc.b = false;
				uncovered.add(unc);
			}
			if(!dead.contains(var1)){
				unc = new Pair();
				unc.v = var1;
				unc.b = true;
				uncovered.add(unc);
			}
		}
		
		return uncovered;
	}

	public List<Pair2> getAllValidPairs(int threads) {
	    // Get a list of vars
		List<BooleanVariableInterface> vars = new ArrayList<BooleanVariableInterface>(cnf.getVariables());
		
		// Find mandatory and dead features
	    Set<BooleanVariableInterface> mandatory = new HashSet<BooleanVariableInterface>();
		Set<BooleanVariableInterface> dead = new HashSet<BooleanVariableInterface>();
		MandatoryAndDeadDetection mdd = new MandatoryAndDeadDetection(this, nrid); 
		mdd.findMandatoryAndDeadFeatures(vars, mandatory, dead);
		System.out.println("Mandatory: " + mandatory.size());
		System.out.println("Dead: " + dead.size());
		
		// Solutions
		List<List<Integer>> solutions = new ArrayList<List<Integer>>();
		int coveredInitially = 0;
		
		// Find two solutions
		System.out.println("Find two solutions");
		{
			SAT4JSolver satSolver = null;
			try {
				satSolver = getSAT4JSolver();
				satSolver.solver.isSatisfiable();
			} catch (ContradictionException e1) {
			} catch (org.sat4j.specs.TimeoutException e) {
			}
			int[] s1 = satSolver.solver.model();
			List<Integer> solution1 = new ArrayList<Integer>();
			for(int z : s1)
				solution1.add(z);
			try {
				satSolver = getSAT4JSolverInverse();
				satSolver.solver.isSatisfiable();
			} catch (ContradictionException e1) {
			} catch (org.sat4j.specs.TimeoutException e) {
			}
			int[] s2 = satSolver.solver.model();
			List<Integer> solution2 = new ArrayList<Integer>();
			for(int z : s2)
				solution2.add(-z); // Must be inverted since sat is inverted
			
			solutions.add(solution1);
			solutions.add(solution2);
			
			Set<Pair2> coveredSet = new HashSet<Pair2>();
			GCThread c1 = new GCThread(solution1, vars, nrid, idnr);
			GCThread c2 = new GCThread(solution2, vars, nrid, idnr);
			Thread t1 = new Thread(c1);
			Thread t2 = new Thread(c2);
			t1.start();
			t2.start();
			
			// Start monitoring thread
			List<ProgressReporter> prs = new ArrayList<ProgressReporter>();
			prs.add(c1);
			prs.add(c2);
			long total = 2*TCalc.tsCovered(vars.size(), 2);
			ProgressThread pt = new ProgressThread("Find covered", prs, total);
			Thread ptt = new Thread(pt);
			ptt.start();
			
			// wait
			try {
				t1.join();
				t2.join();
			} catch (InterruptedException e) {
			}
			
			// Stop monitoring
			pt.stop();
			
			// Collect
			coveredSet.addAll(c1.getCovered());
			coveredSet.addAll(c2.getCovered());
		
			coveredInitially = coveredSet.size();
		}
		System.out.println("The two solutions covered: " + coveredInitially);
	
		// Calculate uncovered tuples
		List<Pair2> uncovered = new ArrayList<Pair2>();
		List<BooleanVariableInterface> vars2 = new ArrayList<BooleanVariableInterface>(vars);
		for(BooleanVariableInterface var1 : vars){
			vars2.remove(var1);
			for(BooleanVariableInterface var2 : vars2){
				Pair2 unc;
				if(!mandatory.contains(var1) && !mandatory.contains(var2)){
					unc = new Pair2(idnr);
					unc.v1 = var1;
					unc.b1 = false;
					unc.v2 = var2;
					unc.b2 = false;
					uncovered.add(unc);
				}
				if(!mandatory.contains(var1) && !dead.contains(var2)){
					unc = new Pair2(idnr);
					unc.v1 = var1;
					unc.b1 = false;
					unc.v2 = var2;
					unc.b2 = true;
					uncovered.add(unc);
				}
				if(!dead.contains(var1) && !mandatory.contains(var2)){
					unc = new Pair2(idnr);
					unc.v1 = var1;
					unc.b1 = true;
					unc.v2 = var2;
					unc.b2 = false;
					uncovered.add(unc);
				}
				if(!dead.contains(var1) && !dead.contains(var2)){
					unc = new Pair2(idnr);
					unc.v1 = var1;
					unc.b1 = true;
					unc.v2 = var2;
					unc.b2 = true;
					uncovered.add(unc);
				}
			}
		}
			
		System.out.println("Uncovered pairs left: " + uncovered.size());
		
		// -----
		{
			int uncTotal = uncovered.size() + coveredInitially;
			
			// Remove invalid
			List<List<Pair2>> uncSplit = new ArrayList<List<Pair2>>();
			int beg=0, range=uncovered.size()/threads + 1;
			for(int i = 0; i < threads; i++){
				if(beg+range > uncovered.size()) range = uncovered.size() - beg;
				uncSplit.add(uncovered.subList(beg, beg+range));
				//System.out.println(beg + " ->" + (beg+range));
				beg += range;
			}
	
			List<RIThread> rits = new ArrayList<RIThread>();
			List<Thread> ts = new ArrayList<Thread>();
			for(int i = 0; i < threads; i++){
				RIThread rit = new RIThread(this, uncSplit.get(i), nrid, idnr);
				rits.add(rit);
				Thread t = new Thread(rit);
				ts.add(t);
			}
			
			for(int i = 0; i < threads; i++){
				ts.get(i).start();
			}
	
			// Start monitoring thread
			List<ProgressReporter> prs = new ArrayList<ProgressReporter>(rits);
			ProgressThread pt = new ProgressThread("Remove invalid", prs, uncTotal);
			Thread ptt = new Thread(pt);
			ptt.start();
			
			// Wait for all threads to finish
			for(int i = 0; i < threads; i++){
				try {
					ts.get(i).join();
				} catch (InterruptedException e) {
				}
			}
			
			// Stop monitoring
			pt.stop();
			
			// Collect
			uncovered = new ArrayList<Pair2>();
			for(int i = 0; i < threads; i++){
				uncovered.addAll(rits.get(i).getValid());
			}
		}
		
		System.out.println("Uncovered and valid tuples: " + uncovered.size());
		return uncovered;
	}

	public List<Pair3> getAllValidTriples(int threads) {
        // Get a list of vars
		List<BooleanVariableInterface> vars = new ArrayList<BooleanVariableInterface>(cnf.getVariables());
		
		// Find mandatory and dead features
        Set<BooleanVariableInterface> mandatory = new HashSet<BooleanVariableInterface>();
		Set<BooleanVariableInterface> dead = new HashSet<BooleanVariableInterface>();
		MandatoryAndDeadDetection mdd = new MandatoryAndDeadDetection(this, nrid); 
		mdd.findMandatoryAndDeadFeatures(vars, mandatory, dead);
		System.out.println("Mandatory: " + mandatory.size());
		System.out.println("Dead: " + dead.size());
		
		// Solutions
		int coveredInitially = 0;
	
		// Calculate uncovered tuples
		List<Pair3> uncovered = new ArrayList<Pair3>();
		List<BooleanVariableInterface> vars2 = new ArrayList<BooleanVariableInterface>(vars);
		List<BooleanVariableInterface> vars3 = new ArrayList<BooleanVariableInterface>(vars);
		long invalid = 0;
		for(int i = 0; i < vars.size(); i++){
			BooleanVariableInterface var1 = vars.get(i);
			for(int j = i+1; j < vars2.size(); j++){
				BooleanVariableInterface var2 = vars2.get(j);
				for(int k = j+1; k < vars3.size(); k++){
					BooleanVariableInterface var3 = vars3.get(k);
					
					// Set pair
					if(!mandatory.contains(var1) && !mandatory.contains(var2) && !mandatory.contains(var3)){
						Pair3 unc = new Pair3(idnr);
						unc.v1 = var1;
						unc.b1 = false;
						unc.v2 = var2;
						unc.b2 = false;
						unc.v3 = var3;
						unc.b3 = false;
						uncovered.add(unc);
					}else invalid++;
					if(!mandatory.contains(var1) && !mandatory.contains(var2) && !dead.contains(var3)){
						Pair3 unc = new Pair3(idnr);
						unc.v1 = var1;
						unc.b1 = false;
						unc.v2 = var2;
						unc.b2 = false;
						unc.v3 = var3;
						unc.b3 = true;
						uncovered.add(unc);
					}else invalid++;
					if(!mandatory.contains(var1) && !dead.contains(var2) && !mandatory.contains(var3)){
						Pair3 unc = new Pair3(idnr);
						unc.v1 = var1;
						unc.b1 = false;
						unc.v2 = var2;
						unc.b2 = true;
						unc.v3 = var3;
						unc.b3 = false;
						uncovered.add(unc);
					}else invalid++;
					if(!mandatory.contains(var1) && !dead.contains(var2) && !dead.contains(var3)){
						Pair3 unc = new Pair3(idnr);
						unc.v1 = var1;
						unc.b1 = false;
						unc.v2 = var2;
						unc.b2 = true;
						unc.v3 = var3;
						unc.b3 = true;
						uncovered.add(unc);
					}else invalid++;
					if(!dead.contains(var1) && !mandatory.contains(var2) && !mandatory.contains(var3)){
						Pair3 unc = new Pair3(idnr);
						unc.v1 = var1;
						unc.b1 = true;
						unc.v2 = var2;
						unc.b2 = false;
						unc.v3 = var3;
						unc.b3 = false;
						uncovered.add(unc);
					}else invalid++;
					if(!dead.contains(var1) && !mandatory.contains(var2) && !dead.contains(var3)){
						Pair3 unc = new Pair3(idnr);
						unc.v1 = var1;
						unc.b1 = true;
						unc.v2 = var2;
						unc.b2 = false;
						unc.v3 = var3;
						unc.b3 = true;
						uncovered.add(unc);
					}else invalid++;
					if(!dead.contains(var1) && !dead.contains(var2) && !mandatory.contains(var3)){
						Pair3 unc = new Pair3(idnr);
						unc.v1 = var1;
						unc.b1 = true;
						unc.v2 = var2;
						unc.b2 = true;
						unc.v3 = var3;
						unc.b3 = false;
						uncovered.add(unc);
					}else invalid++;
					if(!dead.contains(var1) && !dead.contains(var2) && !dead.contains(var3)){
						Pair3 unc = new Pair3(idnr);
						unc.v1 = var1;
						unc.b1 = true;
						unc.v2 = var2;
						unc.b2 = true;
						unc.v3 = var3;
						unc.b3 = true;
						uncovered.add(unc);
					}else invalid++;
				}
			}
		}
			
		System.out.println("Uncovered triples left: " + uncovered.size() + " invalid: " + invalid);
		
		int uncTotal = uncovered.size() + coveredInitially;
		
		// Remove invalid
		List<List<Pair3>> uncSplit = new ArrayList<List<Pair3>>();
		int beg=0, range=uncovered.size()/threads + 1;
		for(int i = 0; i < threads; i++){
			if(beg+range > uncovered.size()) range = uncovered.size() - beg;
			uncSplit.add(uncovered.subList(beg, beg+range));
			//System.out.println(beg + " ->" + (beg+range));
			beg += range;
		}

		List<RIThread3> rits = new ArrayList<RIThread3>();
		List<Thread> ts = new ArrayList<Thread>();
		for(int i = 0; i < threads; i++){
			RIThread3 rit = new RIThread3(this, uncSplit.get(i), nrid, idnr);
			rits.add(rit);
			Thread t = new Thread(rit);
			ts.add(t);
		}
		
		for(int i = 0; i < threads; i++){
			ts.get(i).start();
		}
		
		// Start monitoring thread
		List<ProgressReporter> prs = new ArrayList<ProgressReporter>(rits);
		ProgressThread pt = new ProgressThread("Find invalid", prs, uncTotal);
		Thread ptt = new Thread(pt);
		ptt.start();
		
		// Wait for all threads to finish
		for(int i = 0; i < threads; i++){
			try {
				ts.get(i).join();
			} catch (InterruptedException e) {
			}
		}
		
		// Stop monitoring
		pt.stop();
		
		// Collect
		uncovered = new ArrayList<Pair3>();
		for(int i = 0; i < threads; i++){
			uncovered.addAll(rits.get(i).getValid());
		}
		
		return uncovered;
	}
}
