/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.IContextFile;
import org.eventb.core.ISCContextFile;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestAxiomsAndTheorems extends GenericPredicateTest<IContextFile, ISCContextFile> {
	
	
	/**
	 * check partial typing
	 */
	public void testAxiomsAndTheorems_05_axiomPartialTyping() throws Exception {
		IContextFile con = createContext("con");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");

		addCarrierSets(con, "S1");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2"), makeSList("C1∈ℕ∪S1", "C1∈S1"));
	
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		containsAxioms(file, typeEnvironment, makeSList("A2"), makeSList("C1∈S1"));
		
		hasMarker(con.getAxioms()[0]);
	}
	
	/**
	 * more on partial typing (more complex)
	 */
	public void testAxiomsAndTheorems_06_axiomPartialTyping() throws Exception {
		IContextFile con = createContext("con");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");
		typeEnvironment.addName("C1", factory.makeGivenType("S1"));
		
		addCarrierSets(con, "S1");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2", "A3", "A4"), makeSList("C1=C1", "C1∈S1", "C1∈{C1}", "S1 ⊆ {C1}"));
	
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		containsAxioms(file, typeEnvironment, makeSList("A2", "A3", "A4"), makeSList("C1∈S1", "C1∈{C1}", "S1 ⊆ {C1}"));
		
		hasMarker(con.getAxioms()[0]);
		
	}

	@Override
	protected IGenericSCTest<IContextFile, ISCContextFile> newGeneric() {
		return new GenericContextSCTest(this);
	}
	
}
