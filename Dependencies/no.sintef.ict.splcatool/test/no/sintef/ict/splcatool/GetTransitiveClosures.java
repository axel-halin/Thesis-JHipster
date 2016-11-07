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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.sintef.ict.splcatool.CNF;
import no.sintef.ict.splcatool.GUIDSL;
import no.sintef.ict.splcatool.SAT4JSolver;
import no.sintef.ict.splcatool.SXFM;

import org.junit.Test;
import org.sat4j.core.VecInt;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IVecInt;

import splar.core.constraints.BooleanVariableInterface;
import splar.core.constraints.CNFClause;
import splar.core.fm.FeatureModelException;

import de.ovgu.featureide.fm.core.io.UnsupportedModelException;


public class GetTransitiveClosures {
	@Test
	public void testGetTransitiveClosures() throws UnsupportedModelException, IOException, FeatureModelException, ContradictionException{
		String file = "TestData/Artificial/EclipseSPL.m";
		GUIDSL m1 = new GUIDSL(new File(file));
		SXFM fm = m1.getSXFM();
		CNF cnf = fm.getCNF();

		Map<Integer, Set<BooleanVariableInterface>> eqcs = getClosures(cnf);
		assertEquals(4, eqcs.size());
	}
	
	private boolean isMandatory(CNF cnf, BooleanVariableInterface v){
		SAT4JSolver satSolver = null;
		try {
			satSolver = cnf.getSAT4JSolver();
		} catch (ContradictionException e) {
		}
		
		int [] clause = new int [1];
		clause[0] = -cnf.getNr(v.getID());
		try {
			satSolver.solver.addClause((IVecInt) new VecInt(clause));
		} catch (ContradictionException e1) {
			return true;
		}
		try {
			if(!satSolver.solver.isSatisfiable()){
				return true;
			}
		} catch (org.sat4j.specs.TimeoutException e1) {
		}
		
		return false;
	}
	
	private Map<Integer, Set<BooleanVariableInterface>> getClosures(CNF cnf){
		Set<BooleanVariableInterface> mandatory = new HashSet<BooleanVariableInterface>();
		
		// Find mandatory features
		int x = 0;
		for(BooleanVariableInterface v : cnf.getVariables()){
			x++;
			boolean man = isMandatory(cnf, v);
			if(man){
				mandatory.add(v);
			}
			//System.out.println(x  + " of " + cnf.cnf.getVariables().size() + " " + mandatory.size());
		}
		
		//System.out.println(mandatory);
		
		// Find closures ignoring the mandatory features
		Set<Set<BooleanVariableInterface>> closures = new HashSet<Set<BooleanVariableInterface>>();
		for(BooleanVariableInterface v : cnf.getVariables()){
			if(mandatory.contains(v)) continue;
			
			Set<BooleanVariableInterface> closure = new HashSet<BooleanVariableInterface>();
			int oldsize = closure.size();
			closure.add(v);
			while(oldsize!=closure.size()){
				oldsize = closure.size();
				
				for(CNFClause c : cnf.getClauses()){
					boolean hasone = false;
					for(BooleanVariableInterface vi : closure)
						if(c.getVariables().contains(vi)){
							hasone = true;
							break;
						}
					if(hasone){
						List<BooleanVariableInterface> newvars = c.getVariables();
						newvars.removeAll(mandatory);
						closure.addAll(newvars);
					}
				}
			}
			
			//System.out.println(v.getID() + " - " + closure.size());
			closures.add(closure);
		}
		
		// Find equivalence classes
		Map<Integer, Set<BooleanVariableInterface>> eqcs = new HashMap<Integer, Set<BooleanVariableInterface>>();
		for(Set<BooleanVariableInterface> c : closures){
			eqcs.put(c.size(), c);
		}
		
		return eqcs;
	}
}
