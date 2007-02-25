/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericPredicateTest <IRF extends IRodinFile, ISCRF extends IRodinFile> 
extends BasicSCTest 
implements IGenericSCTest <IRF, ISCRF> {
	
	/**
	 * creation of axiom or invariant
	 */
	public void test_00() throws Exception {
		IRF con = createComponent("con", (IRF) null);

		addNonTheorems(con, makeSList("P"), makeSList("ℕ≠∅"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(con);
		
		containsNonTheorems(file, emptyEnv, makeSList("P"), makeSList("ℕ≠∅"));
		
		containsMarkers(con, false);
	}
	
	/**
	 * name conflict
	 */
	public void test_01() throws Exception {
		IRF con = createComponent("con", (IRF) null);

		addNonTheorems(con, makeSList("P"), makeSList("ℕ≠∅"));
		addNonTheorems(con, makeSList("P"), makeSList("ℕ=∅"));
	
		con.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(con);
		
		containsNonTheorems(file, emptyEnv, makeSList("P"), makeSList("ℕ≠∅"));
		
	}
	
	/**
	 * type conflict in one axiom or invariant
	 */
	public void test_02() throws Exception {
		IRF con = createComponent("con", (IRF) null);

		addNonTheorems(con, makeSList("A1"), makeSList("ℕ≠BOOL"));
	
		con.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(con);
		
		containsNonTheorems(file, emptyEnv, makeSList(), makeSList());
		
	}
	
	/**
	 * use of declared constant or variable
	 */
	public void test_03() throws Exception {
		IRF con = createComponent("con", (IRF) null);

		addIdents(con, "x");
		addNonTheorems(con, makeSList("P"), makeSList("x∈1‥0"));
	
		con.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(con);
		
		containsNonTheorems(file, emptyEnv, makeSList("P"), makeSList("x∈1‥0"));
		
		containsMarkers(con, false);
	}
	
	/**
	 * use of undeclared constants or variables
	 */
	public void test_04() throws Exception {
		IRF con = createComponent("con", (IRF) null);

		addNonTheorems(con, makeSList("A1"), makeSList("C1∈ℕ"));
	
		con.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(con);
		
		containsNonTheorems(file, emptyEnv, makeSList(), makeSList());
		
	}

	/**
	 * create theorem
	 */
	public void test_05() throws Exception {
		IRF con = createComponent("con", (IRF) null);

		addTheorems(con, makeSList("T1"), makeSList("ℕ≠∅"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(con);
		
		containsTheorems(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		
		containsMarkers(con, false);
	}
	
	/**
	 * create two theorems
	 */
	public void test_06() throws Exception {
		IRF con = createComponent("con", (IRF) null);

		addTheorems(con, makeSList("T1", "T2"), makeSList("ℕ≠∅", "ℕ=∅"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(con);
		
		containsTheorems(file, emptyEnv, makeSList("T1", "T2"), makeSList("ℕ≠∅", "ℕ=∅"));
		
		containsMarkers(con, false);
	}
	
	/**
	 * create two theorems with name conflict
	 */
	public void test_07() throws Exception {
		IRF con = createComponent("con", (IRF) null);

		addTheorems(con, makeSList("T1"), makeSList("ℕ≠∅"));
		addTheorems(con, makeSList("T1"), makeSList("ℕ=∅"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(con);
		
		containsTheorems(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		
	}
	
	/**
	 * name conflict of axiom (resp. invariant) and theorem
	 */
	public void test_08() throws Exception {
		IRF con = createComponent("con", (IRF) null);

		addNonTheorems(con, makeSList("T1"), makeSList("ℕ≠∅"));
		addTheorems(con, makeSList("T1"), makeSList("ℕ=∅"));
		
		con.save(null, true);
		
		runBuilder();
		
		ISCRF file = getSCComponent(con);
		
		containsNonTheorems(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		containsTheorems(file, emptyEnv, makeSList(), makeSList());
		
	}

}
