/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     University of Dusseldorf - added theorem attribute
 *******************************************************************************/
package org.eventb.core.tests.sc;


import org.eventb.core.IContextRoot;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestAxiomsAndTheorems extends GenericPredicateTest<IContextRoot, ISCContextRoot> {
	
	
	/**
	 * check partial typing
	 */
	public void testAxiomsAndTheorems_05_axiomPartialTyping() throws Exception {
		IContextRoot con = createContext("ctx");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");

		addCarrierSets(con, "S1");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2"), makeSList("C1∈ℕ∪S1", "C1∈S1"), false, false);
	
		saveRodinFileOf(con);
		
		runBuilder();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsAxioms(file, typeEnvironment, makeSList("A2"), makeSList("C1∈S1"), false);
		
		hasMarker(con.getAxioms()[0]);
	}
	
	/**
	 * more on partial typing (more complex)
	 */
	public void testAxiomsAndTheorems_06_axiomPartialTyping() throws Exception {
		IContextRoot con = createContext("ctx");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");
		typeEnvironment.addName("C1", factory.makeGivenType("S1"));
		
		addCarrierSets(con, "S1");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2", "A3", "A4"), makeSList("C1=C1", "C1∈S1", "C1∈{C1}", "S1 ⊆ {C1}"), false, false, false, false);
	
		saveRodinFileOf(con);
		
		runBuilder();
		
		ISCContextRoot file = con.getSCContextRoot();
		
		containsAxioms(file, typeEnvironment, makeSList("A2", "A3", "A4"), makeSList("C1∈S1", "C1∈{C1}", "S1 ⊆ {C1}"), false, false, false);
		
		hasMarker(con.getAxioms()[0]);
		
	}

	@Override
	protected IGenericSCTest<IContextRoot, ISCContextRoot> newGeneric() {
		return new GenericContextSCTest(this);
	}
	
}
