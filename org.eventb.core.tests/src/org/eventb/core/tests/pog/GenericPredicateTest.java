/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tests.pog;

import org.eventb.core.IPOFile;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IRodinFile;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class GenericPredicateTest <IRF extends IRodinFile> 
extends BasicPOTest 
implements IGenericPOTest<IRF>{

	/*
	 * proper creation of theorem PO
	 */
	public void testTheorems_00_theorem() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addTheorems(cmp, makeSList("T1"), makeSList("∀x·x>1"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasGoal(sequent, emptyEnv, "∀x·x>1");
		
		noSequent(po, "T1/WD");
		
	}
	
	/*
	 * proper creation of theorem well-definedness PO
	 */
	public void testTheorems_01_wDef() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addTheorems(cmp, makeSList("T1"), makeSList("1÷0=0"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasGoal(sequent, emptyEnv, "1÷0=0");

		sequent = getSequent(po, "T1/WD");
		
		sequentHasGoal(sequent, emptyEnv, "0≠0");
	
	}
	
	/*
	 * theorem in hypothesis
	 */
	public void testTheorems_02_ThmInHyp() throws Exception {
		IRF cmp = createComponent("mac", (IRF) null);

		addTheorems(cmp, makeSList("T1", "T2"), makeSList("∀x·x>1", "∀x·x>2"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
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
		IRF cmp = createComponent("cmp", (IRF) null);

		addIdents(cmp, "x");
		addNonTheorems(cmp, makeSList("N1"), makeSList("x∈ℤ"));
		addTheorems(cmp, makeSList("T1","T2", "T3"), makeSList("x∈ℤ", "x>1", "⊤"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
		noSequent(po, "T1/THM");
		
		IPOSequent sequent = getSequent(po, "T2/THM");
		
		sequentHasGoal(sequent, emptyEnv, "x>1");
		
		noSequent(po, "T3/THM");
	}

	/*
	 * non-creation of trivial well-definedness non-theorem PO
	 */
	public void testNonTheorems_04_nonTheorem() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addTheorems(cmp, makeSList("N1"), makeSList("∀x·x>1"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
		noSequent(po, "N1/WD");
		
	}

	/*
	 * proper creation of non-theorem well-definedness PO
	 */
	public void testNonTheorems_05_wDef() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addNonTheorems(cmp, makeSList("N1"), makeSList("1÷0=0"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "N1/WD");
		
		sequentHasGoal(sequent, emptyEnv, "0≠0");
	
	}

	/*
	 * proper creation of hypothesis of non-theorem well-definedness PO
	 */
	public void testNonTheorems_06_wDefHypOK() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addNonTheorems(cmp, makeSList("N0", "N1"), makeSList("1<0", "1÷0=0"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "N1/WD");
		
		sequentHasHypotheses(sequent, emptyEnv, "1<0");
		sequentHasGoal(sequent, emptyEnv, "0≠0");
	
	}

	/*
	 * proper creation of hypothesis of non-theorem well-definedness PO
	 */
	public void testTheorems_07_NonTheoremInHyp() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addNonTheorems(cmp, makeSList("N1"), makeSList("1=0"));
		addTheorems(cmp, makeSList("T1"), makeSList("1<0"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "1=0");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}
	
	/*
	 * proper creation of hypothesis from abstraction
	 */
	public void testTheorems_08_abstraction() throws Exception {
		IRF abs = createComponent("abs", (IRF) null);
		
		addNonTheorems(abs, makeSList("N0"), makeSList("2>9"));
		
		abs.save(null, true);
		
		IRF cmp = createComponent("cmp", (IRF) null);

		addSuper(cmp, "abs");
		addNonTheorems(cmp, makeSList("N1"), makeSList("7<1"));
		addTheorems(cmp, makeSList("T1"), makeSList("1<0"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "2>9", "7<1");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}

	
	/*
	 * proper creation of transitive hypothesis
	 */
	public void testTheorems_09_transitive() throws Exception {
		IRF f0 = createComponent("f0", (IRF) null);
		
		addTheorems(f0, makeSList("T0"), makeSList("5>9"));	
		
		f0.save(null, true);

		IRF f1 = createComponent("f1", (IRF) null);
		
		addSuper(f1, "f0");
		addTheorems(f1, makeSList("N0"), makeSList("2>9"));
		
		f1.save(null, true);
		
		IRF f2 = createComponent("f2", (IRF) null);

		addSuper(f2, "f1");
		addNonTheorems(f2, makeSList("N1"), makeSList("7<1"));
		addTheorems(f2, makeSList("T1"), makeSList("1<0"));
		
		f2.save(null, true);
		
		runBuilder();
		
		IPOFile po = getPOFile(f2);
		
		IPOSequent sequent = getSequent(po, "T1/THM");
		
		sequentHasHypotheses(sequent, emptyEnv, "5>9", "2>9", "7<1");
		sequentHasGoal(sequent, emptyEnv, "1<0");
	
	}

	/*
	 * proper creation of identifiers and hypotheses of non-theorem well-definedness PO
	 */
	public void testNonTheorems_10_identAndHyp() throws Exception {
		IRF cmp = createComponent("cmp", (IRF) null);

		addIdents(cmp, "x");
		addNonTheorems(cmp, makeSList("N1"), makeSList("x÷x ∈ ℕ"));
		
		cmp.save(null, true);
		
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		IPOFile po = getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "N1/WD");
		
		sequentHasIdentifiers(sequent, "x");
		sequentHasGoal(sequent, emptyEnv, "x≠0");
	
	}


	/*
	 * proper creation of identifiers and hypotheses of theorem well-definedness PO
	 */
	public void testTheorems_11_identAndHyp() throws Exception {
		IRF abs = createComponent("abs", (IRF) null);

		addIdents(abs, "x");
		addNonTheorems(abs, makeSList("N1"), makeSList("x ∈ ℕ"));
		
		abs.save(null, true);
		
		IRF cmp = createComponent("cmp", (IRF) null);
		addSuper(cmp, "abs");
		addTheorems(cmp, makeSList("N2"), makeSList("x÷x ∈ ℕ"));
		
		cmp.save(null, true);
		runBuilder();
		
		ITypeEnvironment environment = factory.makeTypeEnvironment();
		environment.addName("x", intType);
		
		IPOFile po = getPOFile(cmp);
		
		IPOSequent sequent = getSequent(po, "N2/WD");
		
		sequentHasIdentifiers(sequent, "x");
		sequentHasGoal(sequent, emptyEnv, "x≠0");
	
	}


}
