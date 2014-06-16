/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - ensure that all AST problems are reported
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - use marker matcher
 *******************************************************************************/
package org.eventb.core.tests.sc;

import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.EmptyLabelError;
import static org.eventb.core.sc.GraphProblem.FreeIdentifierFaultyDeclError;
import static org.eventb.core.sc.GraphProblem.UndeclaredFreeIdentifierError;
import static org.eventb.core.sc.ParseProblem.LexerError;
import static org.eventb.core.sc.ParseProblem.TypesDoNotMatchError;
import static org.eventb.core.tests.MarkerMatcher.marker;

import org.junit.Test;
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
	@Test
	public void test_00() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addPredicates(con, makeSList("P"), makeSList("ℕ≠∅"), false);
		
		getGeneric().save(con);
		
		runBuilderCheck();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList("P"), makeSList("ℕ≠∅"), false);
	}
	
	/**
	 * name conflict
	 */
	@Test
	public void test_01() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addPredicates(con, makeSList("P"), makeSList("ℕ≠∅"), false);
		getGeneric().addPredicates(con, makeSList("P"), makeSList("ℕ=∅"), false);
	
		getGeneric().save(con);
		
		runBuilderCheck(
				marker(getGeneric().getPredicates(con)[0], LABEL_ATTRIBUTE,
						getGeneric().getLabelConflictWarning(), "P"),
				marker(getGeneric().getPredicates(con)[1], LABEL_ATTRIBUTE,
						getGeneric().getLabelConflictError(), "P"));
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList("P"), makeSList("ℕ≠∅"), false);
	}
	
	/**
	 * type conflict in one axiom or invariant
	 */
	@Test
	public void test_02() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addPredicates(con, makeSList("A1"), makeSList("ℕ≠BOOL"), false);
	
		getGeneric().save(con);
		
		runBuilderCheck(marker(getGeneric().getPredicates(con)[0],
				PREDICATE_ATTRIBUTE, 0, 6, TypesDoNotMatchError, "ℤ", "BOOL"));
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
	}
	
	/**
	 * use of declared constant or variable
	 */
	@Test
	public void test_03() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addIdents(con, "x");
		getGeneric().addPredicates(con, makeSList("P"), makeSList("x∈1‥0"), false);
		getGeneric().addInitialisation(con, "x");
	
		getGeneric().save(con);
		
		runBuilderCheck();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList("P"), makeSList("x∈1‥0"), false);
	}
	
	/**
	 * use of undeclared constants or variables
	 */
	@Test
	public void test_04() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addPredicates(con, makeSList("A1"), makeSList("C1∈ℕ"), false);
	
		getGeneric().save(con);
		
		runBuilderCheck(marker(getGeneric().getPredicates(con)[0],
				PREDICATE_ATTRIBUTE, 0, 2, UndeclaredFreeIdentifierError, "C1"));
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
	}

	/**
	 * create theorem
	 */
	@Test
	public void test_05() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addPredicates(con, makeSList("T1"), makeSList("ℕ≠∅"), true);
		
		getGeneric().save(con);
		
		runBuilderCheck();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"), true);
	}
	
	/**
	 * create two theorems
	 */
	@Test
	public void test_06() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addPredicates(con, makeSList("T1", "T2"), makeSList("ℕ≠∅", "ℕ=∅"), true, true);
		
		getGeneric().save(con);
		
		runBuilderCheck();
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList("T1", "T2"), makeSList("ℕ≠∅", "ℕ=∅"), true, true);
	}
	
	/**
	 * create two theorems with name conflict
	 */
	@Test
	public void test_07() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addPredicates(con, makeSList("T1"), makeSList("ℕ≠∅"), true);
		getGeneric().addPredicates(con, makeSList("T1"), makeSList("ℕ=∅"), true);
		
		getGeneric().save(con);
		
		runBuilderCheck(
				marker(getGeneric().getPredicates(con)[0], LABEL_ATTRIBUTE,
						getGeneric().getLabelConflictWarning(), "T1"),
				marker(getGeneric().getPredicates(con)[1], LABEL_ATTRIBUTE,
						getGeneric().getLabelConflictError(), "T1"));
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"), true);
	}
	
	/**
	 * name conflict of axiom (resp. invariant) and theorem
	 */
	@Test
	public void test_08() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addPredicates(con, makeSList("T1"), makeSList("ℕ≠∅"), false);
		getGeneric().addPredicates(con, makeSList("T1"), makeSList("ℕ=∅"), true);
		
		getGeneric().save(con);
		
		runBuilderCheck(
				marker(getGeneric().getPredicates(con)[0], LABEL_ATTRIBUTE,
						getGeneric().getLabelConflictWarning(), "T1"),
				marker(getGeneric().getPredicates(con)[1], LABEL_ATTRIBUTE,
						getGeneric().getLabelConflictError(), "T1"));
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList("T1"), makeSList("ℕ≠∅"), false);
	}
	
	/**
	 * use of undeclared and faulty constants or variables
	 * (do not create too many error messages)
	 */
	@Test
	public void test_09() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addIdents(con, "C1", "C1");
		getGeneric().addPredicates(con, makeSList("A1"), makeSList("C1=∅"), false);
		getGeneric().addPredicates(con, makeSList("A2"), makeSList("C2=∅"), false);
	
		getGeneric().save(con);
		
		runBuilderCheck(
				marker(getGeneric().getIdents(con)[0], IDENTIFIER_ATTRIBUTE,
						getGeneric().getUntypedProblem(), "C1"),
				marker(getGeneric().getIdents(con)[0], IDENTIFIER_ATTRIBUTE,
						getGeneric().getIdentConflictProblem(), "C1"),
				marker(getGeneric().getIdents(con)[1], IDENTIFIER_ATTRIBUTE,
						getGeneric().getIdentConflictProblem(), "C1"),
				marker(getGeneric().getPredicates(con)[0], PREDICATE_ATTRIBUTE,
						0, 2, FreeIdentifierFaultyDeclError, "C1"),
				marker(getGeneric().getPredicates(con)[1], PREDICATE_ATTRIBUTE,
						0, 2, UndeclaredFreeIdentifierError, "C2"));
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
	}

	/**
	 * An invalid character in a predicate is ignored, but reported as a warning
	 */
	@Test
	public void test_10_bug2689872() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addPredicates(con, makeSList("A1"), makeSList("0/=1"), false);
	
		getGeneric().save(con);
		
		runBuilderCheck(marker(getGeneric().getPredicates(con)[0],
				PREDICATE_ATTRIBUTE, 1, 2, LexerError, "/"));
		
		SCE file = getGeneric().getSCElement(con);
		
		getGeneric().containsPredicates(file, emptyEnv, makeSList(), makeSList());
	}

	/**
	 * A piece of data can now be typed by a theorem.
	 */
	@Test
	public void test_11() throws Exception {
		E con = getGeneric().createElement("elt");

		getGeneric().addIdents(con, "C1");
		getGeneric().addPredicates(con, makeSList("T1"), makeSList("C1 ∈ ℤ"), true);
		getGeneric().addInitialisation(con, "C1");
	
		getGeneric().save(con);
		
		runBuilderCheck();
	}
	
	/**
	 * Creation of an axiom or an invariant with an empty label.
	 */
	@Test
	public void test_12() throws Exception {
		E con = getGeneric().createElement("elt");
        
		getGeneric().addPredicates(con, makeSList(""), makeSList("ℕ≠∅"), false);
		
		getGeneric().save(con);
		
		runBuilderCheck(marker(getGeneric().getPredicates(con)[0],
				LABEL_ATTRIBUTE, EmptyLabelError));
	}

}
