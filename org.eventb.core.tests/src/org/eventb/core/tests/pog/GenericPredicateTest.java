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
package org.eventb.core.tests.pog;

import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericPredicateTest <F extends IInternalElement> 
extends GenericEventBPOTest<F> {

	/*
	 * proper creation of theorem PO
	 */
	public void testTheorems_00_theorem() throws Exception {
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addPredicates(cmp, makeSList("T1"), makeSList("∀x·x>1"), true);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasGoal(sequent, emptyEnv, "∀x·x>1");
		
		noSequent(po, "T1/WD");
		
	}
	
	/*
	 * proper creation of theorem well-definedness PO
	 */
	public void testTheorems_01_wDef() throws Exception {
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addPredicates(cmp, makeSList("T1"), makeSList("1÷0=0"), true);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasGoal(sequent, emptyEnv, "1÷0=0");

		sequent = getSequent(po, "T1/WD");
		
		sequentHasGoal(sequent, emptyEnv, "0≠0");
	
	}
	
	/*
	 * theorem in hypothesis
	 */
	public void testTheorems_02_ThmInHyp() throws Exception {
		F cmp = getGeneric().createElement("mac");

		getGeneric().addPredicates(cmp, makeSList("T1", "T2"), makeSList("∀x·x>1", "∀x·x>2"), true, true);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv);
		sequentHasGoal(sequent, emptyEnv, "∀x·x>1");

		sequent = getSequent(po, "T2/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "∀x·x>1");
		sequentHasGoal(sequent, emptyEnv, "∀x·x>2");
	
	}
	
	/*
	 * non-creation of theorem with trivial goal
	 */
	public void testTheorems_03_trivialTheorem() throws Exception {
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, "x");
		getGeneric().addPredicates(cmp, makeSList("N1"), makeSList("x∈ℤ"), false);
		getGeneric().addPredicates(cmp, makeSList("T1","T2", "T3"), makeSList("x∈ℤ", "x>1", "⊤"), true, true, true);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		noSequent(po, "T1/THM");
		
		IPOSequent sequent = getSequent(po, "T2/THM");
		
		sequentHasGoal(sequent, emptyEnv, "x>1");
		
		noSequent(po, "T3/THM");
	}

	/*
	 * non-creation of trivial well-definedness non-theorem PO
	 */
	public void testNonTheorems_04_nonTheorem() throws Exception {
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addPredicates(cmp, makeSList("N1"), makeSList("∀x·x>1"), true);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		noSequent(po, "N1/WD");
		
	}

	/*
	 * proper creation of non-theorem well-definedness PO
	 */
	public void testNonTheorems_05_wDef() throws Exception {
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addPredicates(cmp, makeSList("N1"), makeSList("1÷0=0"), false);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "N1/WD");
		
		sequentHasGoal(sequent, emptyEnv, "0≠0");
	
	}

	/*
	 * proper creation of hypothesis of non-theorem well-definedness PO
	 */
	public void testNonTheorems_06_wDefHypOK() throws Exception {
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addPredicates(cmp, makeSList("N0", "N1"), makeSList("1<0", "1÷0=0"), false, false);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "N1/WD");
		
		sequentHasHypotheses(sequent, emptyEnv, "1<0");
		sequentHasGoal(sequent, emptyEnv, "0≠0");
	
	}

	/*
	 * proper creation of hypothesis of non-theorem well-definedness PO
	 */
	public void testTheorems_07_NonTheoremInHyp() throws Exception {
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addPredicates(cmp, makeSList("N1"), makeSList("1=0"), false);
		getGeneric().addPredicates(cmp, makeSList("T1"), makeSList("1<0"), true);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "1=0");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}
	
	/*
	 * proper creation of hypothesis from abstraction
	 */
	public void testTheorems_08_abstraction() throws Exception {
		F abs = getGeneric().createElement("abs");
		
		getGeneric().addPredicates(abs, makeSList("N0"), makeSList("2>9"), false);
		
		saveRodinFileOf(abs);
		
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addSuper(cmp, "abs");
		getGeneric().addPredicates(cmp, makeSList("N1"), makeSList("7<1"), false);
		getGeneric().addPredicates(cmp, makeSList("T1"), makeSList("1<0"), true);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "2>9", "7<1");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}

	
	/*
	 * proper creation of transitive hypothesis
	 */
	public void testTheorems_09_transitive() throws Exception {
		F f0 = getGeneric().createElement("f0");
		
		getGeneric().addPredicates(f0, makeSList("T0"), makeSList("5>9"), true);	
		
		saveRodinFileOf(f0);

		F f1 = getGeneric().createElement("f1");
		
		getGeneric().addSuper(f1, "f0");
		getGeneric().addPredicates(f1, makeSList("N0"), makeSList("2>9"), true);
		
		saveRodinFileOf(f1);
		
		F f2 = getGeneric().createElement("f2");

		getGeneric().addSuper(f2, "f1");
		getGeneric().addPredicates(f2, makeSList("N1"), makeSList("7<1"), false);
		getGeneric().addPredicates(f2, makeSList("T1"), makeSList("1<0"), true);
		
		saveRodinFileOf(f2);
		
		runBuilder();
		
		IPORoot po = getGeneric().getPOFile(f2);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "5>9", "2>9", "7<1");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}

	/*
	 * proper creation of identifiers and hypotheses of non-theorem well-definedness PO
	 */
	public void testNonTheorems_10_identAndHyp() throws Exception {
		F cmp = getGeneric().createElement("cmp");

		getGeneric().addIdents(cmp, "x");
		getGeneric().addPredicates(cmp, makeSList("N1"), makeSList("x÷x ∈ ℕ"), false);
		
		saveRodinFileOf(cmp);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "N1/WD");
		
		sequentHasIdentifiers(sequent, "x");
		sequentHasGoal(sequent, emptyEnv, "x≠0");
	
	}


	/*
	 * proper creation of identifiers and hypotheses of theorem well-definedness PO
	 */
	public void testTheorems_11_identAndHyp() throws Exception {
		F abs = getGeneric().createElement("abs");

		getGeneric().addIdents(abs, "x");
		getGeneric().addPredicates(abs, makeSList("N1"), makeSList("x ∈ ℕ"), false);
		
		saveRodinFileOf(abs);
		
		F cmp = getGeneric().createElement("cmp");
		getGeneric().addSuper(cmp, "abs");
		getGeneric().addPredicates(cmp, makeSList("N2"), makeSList("x÷x ∈ ℕ"), true);
		
		saveRodinFileOf(cmp);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		IPORoot po = getGeneric().getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "N2/WD");
		
		sequentHasIdentifiers(sequent, "x");
		sequentHasGoal(sequent, emptyEnv, "x≠0");
	
	}


}
