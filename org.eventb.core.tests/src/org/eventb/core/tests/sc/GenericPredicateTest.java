/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - ensure that all AST problems are reported
 *******************************************************************************/
package org.eventb.core.tests.sc;

import org.eventb.core.EventBAttributes;
import org.eventb.core.sc.ParseProblem;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericPredicateTest <E extends IRodinElement, SCE extends IRodinElement> 
extends GenericEventBSCTest<E, SCE> {
	
	/**
	 * creation of axiom or invariant
	 */
	public void test_00() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addNonTheorems(con, makeSList("P"), makeSList("ℕ≠∅"));
		
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsNonTheorems(file, emptyEnv, makeSList("P"), makeSList("ℕ≠∅"));
		
		getGeneric().containsMarkers(con, false);
	}
	
	/**
	 * name conflict
	 */
	public void test_01() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addNonTheorems(con, makeSList("P"), makeSList("ℕ≠∅"));
		getGeneric().addNonTheorems(con, makeSList("P"), makeSList("ℕ=∅"));
	
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsNonTheorems(file, emptyEnv, makeSList("P"), makeSList("ℕ≠∅"));
		
		hasMarker(getGeneric().getNonTheorems(con)[1]);
	}
	
	/**
	 * type conflict in one axiom or invariant
	 */
	public void test_02() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addNonTheorems(con, makeSList("A1"), makeSList("ℕ≠BOOL"));
	
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsNonTheorems(file, emptyEnv, makeSList(), makeSList());
		
		hasMarker(getGeneric().getNonTheorems(con)[0]);
	}
	
	/**
	 * use of declared constant or variable
	 */
	public void test_03() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addIdents(con, "x");
		getGeneric().addNonTheorems(con, makeSList("P"), makeSList("x∈1‥0"));
	
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsNonTheorems(file, emptyEnv, makeSList("P"), makeSList("x∈1‥0"));
		
		getGeneric().containsMarkers(con, false);
	}
	
	/**
	 * use of undeclared constants or variables
	 */
	public void test_04() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addNonTheorems(con, makeSList("A1"), makeSList("C1∈ℕ"));
	
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsNonTheorems(file, emptyEnv, makeSList(), makeSList());
		
		hasMarker(getGeneric().getNonTheorems(con)[0]);
	}

	/**
	 * create theorem
	 */
	public void test_05() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addTheorems(con, makeSList("T1"), makeSList("ℕ≠∅"));
		
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsTheorems(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		
		getGeneric().containsMarkers(con, false);
	}
	
	/**
	 * create two theorems
	 */
	public void test_06() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addTheorems(con, makeSList("T1", "T2"), makeSList("ℕ≠∅", "ℕ=∅"));
		
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsTheorems(file, emptyEnv, makeSList("T1", "T2"), makeSList("ℕ≠∅", "ℕ=∅"));
		
		getGeneric().containsMarkers(con, false);
	}
	
	/**
	 * create two theorems with name conflict
	 */
	public void test_07() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addTheorems(con, makeSList("T1"), makeSList("ℕ≠∅"));
		getGeneric().addTheorems(con, makeSList("T1"), makeSList("ℕ=∅"));
		
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsTheorems(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		
		hasMarker(getGeneric().getTheorems(con)[1]);
	}
	
	/**
	 * name conflict of axiom (resp. invariant) and theorem
	 */
	public void test_08() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addNonTheorems(con, makeSList("T1"), makeSList("ℕ≠∅"));
		getGeneric().addTheorems(con, makeSList("T1"), makeSList("ℕ=∅"));
		
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsNonTheorems(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"));
		getGeneric().containsTheorems(file, emptyEnv, makeSList(), makeSList());
		
		hasMarker(getGeneric().getTheorems(con)[0]);
		
	}
	
	/**
	 * use of undeclared and faulty constants or variables
	 * (do not create too many error messages)
	 */
	public void test_09() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addIdents(con, "C1", "C1");
		getGeneric().addNonTheorems(con, makeSList("A1"), makeSList("C1=∅"));
		getGeneric().addNonTheorems(con, makeSList("A2"), makeSList("C2=∅"));
	
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsNonTheorems(file, emptyEnv, makeSList(), makeSList());
		
		hasMarker(getGeneric().getNonTheorems(con)[0]);
		hasNotMarker(getGeneric().getNonTheorems(con)[0], ParseProblem.TypeUnknownError);
		
		hasMarker(getGeneric().getNonTheorems(con)[1]);
		hasNotMarker(getGeneric().getNonTheorems(con)[1], ParseProblem.TypeUnknownError);
	}

	/**
	 * An invalid character in a predicate is ignored, but reported as a warning
	 */
	public void test_10_bug2689872() throws Exception {
		E con = getGeneric().createElement("con");

		getGeneric().addNonTheorems(con, makeSList("A1"), makeSList("0/=1"));
	
		getGeneric().save(con);
		
		runBuilder();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsNonTheorems(file, emptyEnv, makeSList(), makeSList());
		
		hasMarker(getGeneric().getNonTheorems(con)[0],
				EventBAttributes.PREDICATE_ATTRIBUTE,
				ParseProblem.LexerError,
				"/"
				);
	}

}
