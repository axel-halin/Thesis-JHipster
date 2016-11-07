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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import splar.core.constraints.BooleanVariableInterface;

public class CALib {
	static public boolean isCovered1(Map<String, Integer> idnr, Pair unc, List<List<Integer>> solutions) {
		for(List<Integer> s : solutions){
			String id1 = unc.v.getID();
			Integer nr1 = idnr.get(id1);
			if(!unc.b) nr1 = -nr1;
			
			if(s.contains(nr1)){
				return true;
			}
		}
		
		return false;
	}
	
	static public boolean isCovered(Map<String, Integer> idnr, Pair2 unc, List<List<Integer>> solutions) {
		for(List<Integer> s : solutions){
			boolean isCovered = isCoveredS(idnr, unc, s);
			if(isCovered == true) return true;
		}
		
		return false;
	}
	
	static public boolean isCoveredS(Map<String, Integer> idnr, Pair2 unc, List<Integer> s) {
		String id1 = unc.v1.getID();
		Integer nr1 = idnr.get(id1);
		if(!unc.b1) nr1 = -nr1;
		
		String id2 = unc.v2.getID();
		Integer nr2 = idnr.get(id2);
		if(!unc.b2) nr2 = -nr2;
		
		if(s.contains(nr1) && s.contains(nr2)){
			return true;
		}
		
		return false;
	}
	
	static public boolean isCovered3(Map<String, Integer> idnr, Pair3 unc, List<List<Integer>> solutions) {
		for(List<Integer> s : solutions){
			boolean isCovered = isCovered3S(idnr, unc, s);
			if(isCovered == true) return true;
		}
		
		return false;
	}
	
	static public boolean isCovered3S(Map<String, Integer> idnr, Pair3 unc, List<Integer> s) {
		String id1 = unc.v1.getID();
		Integer nr1 = idnr.get(id1);
		if(!unc.b1) nr1 = -nr1;
		
		String id2 = unc.v2.getID();
		Integer nr2 = idnr.get(id2);
		if(!unc.b2) nr2 = -nr2;
		
		String id3 = unc.v3.getID();
		Integer nr3 = idnr.get(id3);
		if(!unc.b3) nr3 = -nr3;
		
		if(s.contains(nr1) && s.contains(nr2) && s.contains(nr3)){
			return true;
		}
		
		return false;
	}
	
	static public boolean isCovered4(Map<String, Integer> idnr, Pair4 unc, List<List<Integer>> solutions) {
		for(List<Integer> s : solutions){
			String id1 = unc.v1.getID();
			Integer nr1 = idnr.get(id1);
			if(!unc.b1) nr1 = -nr1;
			
			String id2 = unc.v2.getID();
			Integer nr2 = idnr.get(id2);
			if(!unc.b2) nr2 = -nr2;
			
			String id3 = unc.v3.getID();
			Integer nr3 = idnr.get(id3);
			if(!unc.b3) nr3 = -nr3;
			
			String id4 = unc.v4.getID();
			Integer nr4 = idnr.get(id4);
			if(!unc.b4) nr4 = -nr4;
			
			if(s.contains(nr1) && s.contains(nr2) && s.contains(nr3) && s.contains(nr4)){
				//if(Math.abs(nr1)==11 && Math.abs(nr2)==14)
				//System.out.println(nr1 + ", " + nr2 + " is covered by " + s);
				return true;
			}
		}
		
		return false;
	}
	
	static public List<Pair> getCovered(Map<Integer, String> nrid, List<Integer> solution, List<BooleanVariableInterface> vars) {
		List<Pair> covered = new ArrayList<Pair>();
		
		for(int i = 0; i < solution.size(); i++){
			Pair pair = new Pair();
			int p = solution.get(i);
			for(BooleanVariableInterface var : vars){
				if(var.getID().equals(nrid.get(Math.abs(p)))){
					pair.v = var;
					pair.b = p>0;
				}
			}
			if(pair.v != null)
				covered.add(pair);
		}
		return covered;
	}

	static public Set<Pair2> intersect(Set<Pair2> set1, Set<Pair2> set2)
	{
	    Set<Pair2> intersection = new HashSet<Pair2>(set1);
	    intersection.retainAll(new HashSet<Pair2>(set2));
	    return intersection;
	}

	static public Set<Pair3> intersect(Set<Pair3> set1, List<Pair3> set2) {
	    Set<Pair3> intersection = new HashSet<Pair3>(set1);
	    intersection.retainAll(new HashSet<Pair3>(set2));
	    return intersection;
	}

	public static void removeAllZeros1(Collection<Pair> collection) {
		Collection<Pair> zeros = new HashSet<Pair>();
		for(Pair p : collection){
			if(p.b == false)
				zeros.add(p);
		}
		collection.removeAll(zeros);
	}

	public static void removeAllZeros2(Collection<Pair2> collection) {
		Collection<Pair2> zeros = new HashSet<Pair2>();
		for(Pair2 p : collection){
			if(p.b1 == false && p.b2 == false)
				zeros.add(p);
		}
		collection.removeAll(zeros);
	}

	public static void removeAllWithZeroes(Collection<Pair2> collection) {
		Collection<Pair2> zeros = new HashSet<Pair2>();
		for(Pair2 p : collection){
			if(p.b1 == false || p.b2 == false)
				zeros.add(p);
		}
		collection.removeAll(zeros);
	}

	public static void removeAllZeros3(Collection<Pair3> collection) {
		Collection<Pair3> zeros = new HashSet<Pair3>();
		for(Pair3 p : collection){
			if(p.b1 == false && p.b2 == false && p.b3 == false)
				zeros.add(p);
		}
		collection.removeAll(zeros);
	}

	public static void removeAllWithZeroes3(Collection<Pair3> collection) {
		Collection<Pair3> zeros = new HashSet<Pair3>();
		for(Pair3 p : collection){
			if(p.b1 == false || p.b2 == false || p.b3 == false)
				zeros.add(p);
		}
		collection.removeAll(zeros);
	}
}
