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
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestConstants extends GenericIdentTest<IContextFile, ISCContextFile> {

	/**
	 * create constant with carrier set type
	 */
	public void testConstants_03_constantWithCarrierSetType() throws Exception {
		IContextFile con = createContext("con");

		addConstants(con, makeSList("C1"));
		addCarrierSets(con, makeSList("S1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈S1"));
		
		con.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addGivenSet("S1");
		environment.addName("C1", factory.makeGivenType("S1"));

		ISCContextFile file = con.getSCContextFile();
		
		containsCarrierSets(file, "S1");
		
		containsConstants(file, "C1");
		
		containsAxioms(file, environment, makeSList("A1"), makeSList("C1∈S1"));
	}
	
	/**
	 * copy constant from abstraction
	 */
	public void testConstants_04_constantFromAbstraction() throws Exception {
		IContextFile abs1 = createContext("abs1");
		addConstants(abs1, makeSList("C1"));
		addAxioms(abs1, makeSList("A1"), makeSList("C1∈ℕ"));
		
		abs1.save(null, true);
		
		runBuilder();
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs1");

		addConstants(con, makeSList("C2"));
		addAxioms(con, makeSList("A1"), makeSList("C2∈ℕ"));
		
		con.save(null, true);
		
		runBuilder();

		ISCContextFile file = con.getSCContextFile();
		
		containsConstants(file, "C2");
	
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

	}
	
	/**
	 * name conflict with constant from abstraction
	 */
	public void testConstants_05_constantFromAbstractionNameConflict() throws Exception {
		IContextFile abs1 = createContext("abs1");
		addConstants(abs1, makeSList("C1"));
		addAxioms(abs1, makeSList("A1"), makeSList("C1∈ℕ"));
		
		abs1.save(null, true);
		
		runBuilder();
		
		IContextFile con = createContext("con");
		addContextExtends(con, "abs1");

		addConstants(con, makeSList("C1"));
		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"));
		
		con.save(null, true);
		
		runBuilder();

		ISCContextFile file = con.getSCContextFile();
		
		containsConstants(file);
	
		ISCInternalContext[] contexts = getInternalContexts(file, 1);
		
		containsConstants(contexts[0], "C1");

	}

	/**
	 * constant type across axioms
	 */
	public void testConstants_06_constantTypingOK() throws Exception {
		IContextFile con = createContext("con");

		addConstants(con, makeSList("d"));
		addAxioms(con, makeSList("A1", "A2"), makeSList("d∈ℕ", "d>0"));
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("d", factory.makeIntegerType());
		
		con.save(null, true);
		
		runBuilder();
		
		ISCContextFile file = con.getSCContextFile();
		
		containsConstants(file, "d");
		containsAxioms(file, typeEnvironment, makeSList("A1", "A2"), makeSList("d∈ℕ", "d>0"));
		
	}

}
