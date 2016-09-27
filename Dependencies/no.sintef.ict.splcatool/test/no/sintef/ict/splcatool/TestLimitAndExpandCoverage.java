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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.concurrent.TimeoutException;

import no.sintef.ict.splcatool.CNF;
import no.sintef.ict.splcatool.CoveringArray;
import no.sintef.ict.splcatool.CoveringArrayFile;
import no.sintef.ict.splcatool.FileUtility;
import no.sintef.ict.splcatool.GUIDSL;
import no.sintef.ict.splcatool.Pair2;
import no.sintef.ict.splcatool.SXFM;

import org.junit.Test;

import splar.core.fm.FeatureModelException;

import de.ovgu.featureide.fm.core.io.UnsupportedModelException;

public class TestLimitAndExpandCoverage {
	@Test
	public void testLimitCoverage() throws IOException, UnsupportedModelException, FeatureModelException, TimeoutException, CoveringArrayGenerationException{
		GUIDSL m1 = new GUIDSL(new File("TestData/Realistic/Apl.m"));
		SXFM fm = m1.getSXFM();
		CNF cnf = fm.getCNF();
		CoveringArray ca = cnf.getCoveringArrayGenerator("ICPL", 2);
		ca.generate(75, Integer.MAX_VALUE);
		
		// Calculate the valid pairs
		List<Pair2> uncovered = cnf.getAllValidPairs(1);
		System.out.println("Valid pairs: " + uncovered.size());
		
		// Calculate the covered pairs
		List<List<Integer>> sols = ca.getSolutionsAsList();
		Set<Pair2> coveredPairs = ca.getCovInv(sols, uncovered);
		System.out.println("Covered pairs: " + coveredPairs.size());
		
		// Coverage
		System.out.println("Coverage: " + coveredPairs.size() + "/" + uncovered.size() + " = " + (float)coveredPairs.size()*100/uncovered.size() + "%");
		
		assertTrue(ca.getCoverage() >= (int)((float)coveredPairs.size()*100/uncovered.size()));
	}
	
	@Test
	public void testExpandCoverage() throws IOException, UnsupportedModelException, FeatureModelException, TimeoutException, CSVException, CoveringArrayGenerationException{
		{
			GUIDSL m1 = new GUIDSL(new File("TestData/Realistic/Apl.m"));
			SXFM fm = m1.getSXFM();
			CNF cnf = fm.getCNF();
			CoveringArray ca = cnf.getCoveringArrayGenerator("ICPL", 2);
			ca.generate(75, Integer.MAX_VALUE);
			ca.writeToFile("test.dat", CoveringArrayFile.Type.vertical);
		}
		
		{
			GUIDSL m1 = new GUIDSL(new File("TestData/Realistic/Apl.m"));
			SXFM fm = m1.getSXFM();
			CNF cnf = fm.getCNF();
			CoveringArray ca = cnf.getCoveringArrayGenerator("ICPL", 2);
			CoveringArray partial = new CoveringArrayFile("test.dat");
			ca.startFrom(partial);
			ca.generate(95, Integer.MAX_VALUE);
			
			List<List<Integer>> a = partial.getSolutionsAsList();
			List<List<Integer>> b = ca.getSolutionsAsList();
			
			Set<Set<Integer>> as = new HashSet<Set<Integer>>();
			for(List<Integer> l : a) as.add(new HashSet<Integer>(l));
			Set<Set<Integer>> bs = new HashSet<Set<Integer>>();
			for(List<Integer> l : b) bs.add(new HashSet<Integer>(l));
			
			assertTrue(bs.containsAll(as));
			
			ca.writeToFile("test2.dat", CoveringArrayFile.Type.vertical);
			
			String f1 = new FileUtility().readFileAsString("test.dat");
			String f2 = new FileUtility().readFileAsString("test2.dat");
			assertEquals(0, f2.indexOf(f1));
			assertTrue(f2.contains(f1));
		}
	}
}
