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
public class TestAxiomsAndTheorems extends BasicTest {
	
	public void testAxiomsAndTheorems_00_createAxiom() throws Exception {
		IContextFile con = createContext("con");

		addAxioms(con, makeSList("A1"), makeSList("ℕ≠∅"));
		
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsAxioms(file, emptyEnv, makeSList("A1"), makeSList("ℕ≠∅"));
		
	}
	
	public void testAxiomsAndTheorems_01_twoAxiomsLableConflict() throws Exception {
		IContextFile con = createContext("con");

		addAxioms(con, makeSList("A1"), makeSList("ℕ≠∅"));
		addAxioms(con, makeSList("A1"), makeSList("ℕ=∅"));
	
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsAxioms(file, emptyEnv, makeSList("A1"), makeSList("ℕ≠∅"));
		
	}
	
	public void testAxiomsAndTheorems_02_axiomTypeConflict() throws Exception {
		IContextFile con = createContext("con");

		addAxioms(con, makeSList("A1"), makeSList("ℕ≠BOOL"));
	
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsAxioms(file, emptyEnv, makeSList(), makeSList());
		
	}
	
	public void testAxiomsAndTheorems_03_axiomUsesConstantOK() throws Exception {
		IContextFile con = createContext("con");

		addConstants(con, "C1");
		addAxioms(con, makeSList("A1"), makeSList("C1∈1‥0"));
	
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsAxioms(file, emptyEnv, makeSList("A1"), makeSList("C1∈1‥0"));
		
	}
	
	public void testAxiomsAndTheorems_04_axiomUndeclaredConstant() throws Exception {
		IContextFile con = createContext("con");

		addAxioms(con, makeSList("A1"), makeSList("C1∈ℕ"));
	
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsAxioms(file, emptyEnv, makeSList(), makeSList());
		
	}
	
	public void testAxiomsAndTheorems_05_axiomPartialTyping() throws Exception {
		IContextFile con = createContext("con");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");

		addCarrierSets(con, "S1");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2"), makeSList("C1∈ℕ∪S1", "C1∈S1"));
	
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsAxioms(file, typeEnvironment, makeSList("A2"), makeSList("C1∈S1"));
		
	}
	
	public void testAxiomsAndTheorems_06_axiomPartialTyping() throws Exception {
		IContextFile con = createContext("con");
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addGivenSet("S1");
		typeEnvironment.addName("C1", factory.makeGivenType("S1"));
		
		addCarrierSets(con, "S1");
		addConstants(con, "C1");
		addAxioms(con, makeSList("A1", "A2", "A3", "A4"), makeSList("C1=C1", "C1∈S1", "C1∈{C1}", "S1 ⊆ {C1}"));
	
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsAxioms(file, typeEnvironment, makeSList("A2", "A3", "A4"), makeSList("C1∈S1", "C1∈{C1}", "S1 ⊆ {C1}"));
		
	}
	
	public void testAxiomsAndTheorems_07_createTheorem() throws Exception {
		IContextFile con = createContext("con");

		addTheorems(con, makeSList("T1"), makeSList("ℕ≠∅"));
		
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsTheorems(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		
	}
	
	public void testAxiomsAndTheorems_08_twoTheorems() throws Exception {
		IContextFile con = createContext("con");

		addTheorems(con, makeSList("T1", "T2"), makeSList("ℕ≠∅", "ℕ=∅"));
		
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsTheorems(file, emptyEnv, makeSList("T1", "T2"), makeSList("ℕ≠∅", "ℕ=∅"));
		
	}
	
	public void testAxiomsAndTheorems_09_twoTheoremsNameConflict() throws Exception {
		IContextFile con = createContext("con");

		addTheorems(con, makeSList("T1"), makeSList("ℕ≠∅"));
		addTheorems(con, makeSList("T1"), makeSList("ℕ=∅"));
		
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsTheorems(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		
	}
	
	public void testAxiomsAndTheorems_10_axiomTheoremNameConflict() throws Exception {
		IContextFile con = createContext("con");

		addAxioms(con, makeSList("T1"), makeSList("ℕ≠∅"));
		addTheorems(con, makeSList("T1"), makeSList("ℕ=∅"));
		
		con.save(null, true);
		
		runSC(con);
		
		ISCContextFile file = con.getSCContextFile();
		
		containsAxioms(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		containsTheorems(file, emptyEnv, makeSList(), makeSList());
		
	}

}
