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

import static org.junit.Assert.*;

import java.io.IOException;

import no.sintef.ict.splcatool.Report;

import org.junit.Test;

public class ReportTest {

	@Test
	public void testReport() throws IOException {
		Report r = new Report("TestData/reports/report.csv");
		r.writeToFile("test.dat");
		Report rb = new Report("test.dat");
		assertTrue(r.equals(rb));
	}
	
	@Test
	public void testReport2() throws IOException {
		Report r = new Report("TestData/reports/report.csv");
		r.writeToFile("test.dat");
		Report rb = new Report("test.dat");
		//System.out.println(rb);
		rb.addValue("Apl.m", "Features", "31");
		//System.out.println(rb);
		assertTrue(r.equals(rb));
	}

}
