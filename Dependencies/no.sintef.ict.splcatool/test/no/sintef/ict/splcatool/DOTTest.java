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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import no.sintef.ict.splcatool.CNF;
import no.sintef.ict.splcatool.CoveringArray;
import no.sintef.ict.splcatool.FileUtility;
import no.sintef.ict.splcatool.SAT4JSolver;

import org.sat4j.specs.ContradictionException;

import splar.core.constraints.BooleanVariable;
import splar.core.constraints.BooleanVariableInterface;
import splar.core.constraints.CNFClause;
import splar.core.constraints.CNFFormula;
import splar.core.constraints.CNFLiteral;

public class DOTTest {
	public static void main(String[] args) throws IOException, ContradictionException, CoveringArrayGenerationException {
		for(String file : new FileUtility().traverseDirCollectFiles("..\\INWCA\\TestData\\Processed\\Realistic\\")){
			if(file.contains("_results")) continue;
			if(file.contains("2.6.32-2var.dimacs")) continue;
			if(file.contains("2.6.33.3-2var.dimacs")) continue;
			//if(!file.contains("ecos-icse11.dimacs")) continue;
			//if(!file.contains("freebsd-icse11.dimacs")) continue;
			if(!file.contains("2.6.28.6-icse11.dimacs")) continue;
			if(!file.endsWith(".dimacs")) continue;
			
			System.out.println(file);
			
			CNF cnf = new CNF(file, CNF.type.cnf);
			
			//System.out.println("CNF size: " + cnf.cnf.toString().length());
			
			SAT4JSolver x = cnf.getSAT4JSolver();
			
			try {
				System.out.println(x.isSatisfiable());
			} catch (org.sat4j.specs.TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			CoveringArray ca1 = cnf.getCoveringArrayGenerator("ICPL", 1);
			ca1.setTimeout(0);
			
			try {
				ca1.generate();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ca1.writeToFile("ca1-ecos.csv");
		}
	}

}
