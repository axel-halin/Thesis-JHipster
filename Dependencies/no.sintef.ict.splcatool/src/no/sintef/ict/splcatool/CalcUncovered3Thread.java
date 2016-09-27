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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import splar.core.constraints.BooleanVariableInterface;

public class CalcUncovered3Thread implements Runnable, ProgressReporter {
	private List<BooleanVariableInterface> vars;
	private boolean coverOnlyOnes;
	private boolean coverZeros;
	private Map<String, Integer> idnr;
	private List<List<Integer>> solutions;
	private List<Pair3> uncovered;
	private long invalid;
	private long progress;
	private Set<Pair2> invalid2w;
	private Set<Pair3> invalid3w;
	private int begin;
	private int end;
	private boolean firstHalfOnly;
	private boolean secondHalfOnly;
	private boolean firstFourthOnly;
	private boolean thirdFourthOnly;
	private int coverEight;

	public CalcUncovered3Thread(int begin, int end,
			List<BooleanVariableInterface> vars, boolean coverOnlyOnes,
			boolean coverZerosOnly, Set<Pair2> invalid2w,
			Map<String, Integer> idnr, List<List<Integer>> solutions,
			Set<Pair3> invalid3w, boolean firstHalfOnly, boolean secondHalfOnly, boolean firstFourthOnly, boolean thirdFourthOnly, int coverEight) {
		this.begin = begin;
		this.end = end;
		this.vars = vars;
		this.coverOnlyOnes = coverOnlyOnes;
		this.coverZeros = coverZerosOnly;
		this.invalid2w = invalid2w;
		this.idnr = idnr;
		this.solutions = solutions;
		this.invalid3w = invalid3w;
		this.firstHalfOnly = firstHalfOnly;
		this.secondHalfOnly = secondHalfOnly;
		this.firstFourthOnly = firstFourthOnly;
		this.thirdFourthOnly = thirdFourthOnly;
		this.coverEight = coverEight;
		progress = 0;
		uncovered = new ArrayList<Pair3>();
	}

	public void run() {
		List<BooleanVariableInterface> vars2 = new ArrayList<BooleanVariableInterface>(vars);
		List<BooleanVariableInterface> vars3 = new ArrayList<BooleanVariableInterface>(vars);
		invalid = 0;
		boolean tf[] = new boolean [] {true, false};
		for(int i = begin; i < end; i++){
			BooleanVariableInterface var1 = vars.get(i);
			for(int j = i+1; j < vars2.size(); j++){
				BooleanVariableInterface var2 = vars2.get(j);
				for(int k = j+1; k < vars3.size(); k++){
					if(coverOnlyOnes){
						progress += 1;
					}else if(!coverZeros){
						progress += 7;
					}else if(firstHalfOnly || secondHalfOnly){
						progress += 4;
					}else if(firstFourthOnly || thirdFourthOnly){
						progress += 2;
					}else if(coverEight != 0){
						progress += 1;
					}else{
						progress += 8;
					}
					BooleanVariableInterface var3 = vars3.get(k);
					
					for(boolean b1 : tf) for(boolean b2 : tf) for(boolean b3 : tf){
						// Check if invalid
						Pair2 p = new Pair2(idnr);
						p.v1 = var1;
						p.b1 = b1;
						p.v2 = var2;
						p.b2 = b2;
						if(invalid2w.contains(p)){
							invalid++;
							Pair3 ip = new Pair3(idnr);
							ip.v1 = var1;
							ip.b1 = b1;
							ip.v2 = var2;
							ip.b2 = b2;
							ip.v3 = var3;
							ip.b3 = true;
							invalid3w.add(ip);
							ip = new Pair3(idnr);
							ip.v1 = var1;
							ip.b1 = b1;
							ip.v2 = var2;
							ip.b2 = b2;
							ip.v3 = var3;
							ip.b3 = false;
							invalid3w.add(ip);
							continue;
						}
						p.v1 = var1;
						p.b1 = b1;
						p.v2 = var3;
						p.b2 = b3;
						if(invalid2w.contains(p)){
							invalid++;
							Pair3 ip = new Pair3(idnr);
							ip.v1 = var1;
							ip.b1 = b1;
							ip.v2 = var2;
							ip.b2 = true;
							ip.v3 = var3;
							ip.b3 = b3;
							invalid3w.add(ip);
							ip = new Pair3(idnr);
							ip.v1 = var1;
							ip.b1 = b1;
							ip.v2 = var2;
							ip.b2 = false;
							ip.v3 = var3;
							ip.b3 = b3;
							invalid3w.add(ip);
							continue;
						}
						p.v1 = var2;
						p.b1 = b2;
						p.v2 = var3;
						p.b2 = b3;
						if(invalid2w.contains(p)){
							invalid++;
							Pair3 ip = new Pair3(idnr);
							ip.v1 = var1;
							ip.b1 = true;
							ip.v2 = var2;
							ip.b2 = b2;
							ip.v3 = var3;
							ip.b3 = b3;
							invalid3w.add(ip);
							ip = new Pair3(idnr);
							ip.v1 = var1;
							ip.b1 = false;
							ip.v2 = var2;
							ip.b2 = b2;
							ip.v3 = var3;
							ip.b3 = b3;
							invalid3w.add(ip);
							continue;
						}
						
						// Filters
						if(coverOnlyOnes && (b1==false || b2==false || b3==false)) continue;
						if(!coverZeros && (b1==false && b2==false && b3==false)) continue;
						if(firstHalfOnly && b1!=true) continue;
						if(secondHalfOnly && b1!=false) continue;
						if(firstFourthOnly)
							if(b1==true && b2==false);
							else continue;
						if(thirdFourthOnly)
							if(b1==false && b2==false);
							else continue;
						
						if(coverEight!=0){
							int nr = 1;
							if(b1 == true) nr += 4;
							if(b2 == true) nr += 2;
							if(b3 == true) nr += 1;
							
							if(nr != coverEight) continue;
						}
						
						// Add tuple
						Pair3 unc = new Pair3(idnr);
						unc.v1 = var1;
						unc.b1 = b1;
						unc.v2 = var2;
						unc.b2 = b2;
						unc.v3 = var3;
						unc.b3 = b3;
						if(!isCovered3(unc, solutions)){
							uncovered.add(unc);
						}else{
							invalid++;
						}
					}
				}
			}
		}
	}

	private boolean isCovered3(Pair3 unc, List<List<Integer>> solutions) {
		for(List<Integer> s : solutions){
			String id1 = unc.v1.getID();
			Integer nr1 = idnr.get(id1);
			if(!unc.b1) nr1 = -nr1;
			
			String id2 = unc.v2.getID();
			Integer nr2 = idnr.get(id2);
			if(!unc.b2) nr2 = -nr2;
			
			String id3 = unc.v2.getID();
			Integer nr3 = idnr.get(id3);
			if(!unc.b3) nr3 = -nr3;
			
			if(s.contains(nr1) && s.contains(nr2) && s.contains(nr3)){
				return true;
			}
		}
		
		return false;
	}

	public long getInvalidCount() {
		return invalid;
	}

	public long getProgress() {
		return progress;
	}

	public List<Pair3> getUncovered() {
		return uncovered;
	}

	public Set<Pair3> getInvalid() {
		return invalid3w;
	}
}
