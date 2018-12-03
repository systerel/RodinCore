/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Universitaet Duesseldorf - added theorem attribute
 *     Systerel - Simplify PO for anticipated event (FR326)
 *     Systerel - lexicographic variants
 *******************************************************************************/
package org.eventb.core.tests.pog;

import static java.lang.System.arraycopy;
import static org.eventb.core.IConvergenceElement.Convergence.ANTICIPATED;
import static org.eventb.core.IConvergenceElement.Convergence.CONVERGENT;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;
import static org.eventb.core.tests.pom.POUtil.mTypeEnvironment;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.ast.Predicate;
import org.junit.Test;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class TestMachineVariant extends EventBPOTest {
	
	public static class VariantTestItem {
		public final int kind;
		public final String variant;
		public final String[] guardLabels;
		public final String[] guards;
		public final String[] actionLabels;
		public final String[] actions;
		public final String varPost;
		public VariantTestItem(
				final int kind, final String variant, 
				final String[] guardLabels, final String[] guards, 
				final String[] actionLabels, final String[] actions, 
				final String varPost) {
			assert guardLabels.length == guards.length;
			assert actionLabels.length == actions.length;
			this.kind = kind;
			this.variant = variant;
			this.guardLabels = guardLabels;
			this.guards = guards;
			this.actionLabels = actionLabels;
			this.actions = actions;
			this.varPost = varPost;
		}
	}
	
	public static final int INT_VARIANT = 1;
	public static final int SET_VARIANT = 2;
	
	public final static VariantTestItem[] items = new VariantTestItem[] {
		new VariantTestItem(
				INT_VARIANT,
				"x",
				makeSList(),
				makeSList(),
				makeSList("A"),
				makeSList("x ≔ 1"),
				"1"),
		new VariantTestItem(
				INT_VARIANT,
				"x",
				makeSList(),
				makeSList(),
				makeSList("A"),
				makeSList("x ≔ x+1"),
				"x+1"),
		new VariantTestItem(
				INT_VARIANT,
				"x+y",
				makeSList(),
				makeSList(),
				makeSList("A", "B"),
				makeSList("x ≔ x+1", "y ≔ y∗x"),
				"(x+1)+(y∗x)"),
		new VariantTestItem(
				SET_VARIANT,
				"A",
				makeSList(),
				makeSList(),
				makeSList("A", "B"),
				makeSList("x ≔ x+1", "A ≔ {x}"),
				"{x}"),
		new VariantTestItem(
				SET_VARIANT,
				"A∖{x}",
				makeSList(),
				makeSList(),
				makeSList("A", "B"),
				makeSList("x ≔ x+1", "A ≔ A ∪ {x}"),
				"(A∪{x})∖{x+1}"),
		new VariantTestItem(
				SET_VARIANT,
				"A∖{x}",
				makeSList("G", "H"),
				makeSList("x>1", "A≠∅"),
				makeSList("A", "B"),
				makeSList("x ≔ x+1", "A ≔ A ∪ {x}"),
				"(A∪{x})∖{x+1}"),
	};
	
	private String getRelationSymbol(Convergence convergence, int kind) {
		switch (convergence) {
		case ANTICIPATED:
			switch (kind) {
			case INT_VARIANT:
				return " ≤ ";
			case SET_VARIANT:
				return " ⊆ ";
			default:
				return null;
			}
		case CONVERGENT:
			switch (kind) {
			case INT_VARIANT:
				return " < ";
			case SET_VARIANT:
				return " ⊂ ";
			default:
				return null;
			}
		default:
			return null;
		}
	}
	
	/*
	 * anticipated event variants
	 */
	@Test
	public void test_01_anticipated() throws Exception {
		
		Convergence convergence = ANTICIPATED;
		
		testItemList(convergence);
	
	}

	/*
	 * anticipated event variants
	 */
	@Test
	public void test_02_convergent() throws Exception {
		
		Convergence convergence = CONVERGENT;
		
		testItemList(convergence);
	
	}

	String[] invLabels = makeSList("I1", "I2", "I3");
	String[] invPredicates = makeSList("A ⊆ ℤ", "x ∈ ℤ", "y ∈ ℤ");
	boolean[] isTheorem = new boolean[] { false, false, false};
	
	ITypeEnvironmentBuilder environment = mTypeEnvironment(
			"A=ℙ(ℤ); x=ℤ; y=ℤ", factory);
	
	private void testItemList(Convergence convergence) throws Exception {
		
		int index = 0;
		for (VariantTestItem item : items) {
			String macName = "mac" + index++;
			IMachineRoot mac = createMachineFragment(macName);
			addVariant(mac, item.variant);
			IEvent event = addEvent(mac, "evt", makeSList(), 
					item.guardLabels, item.guards, 
					item.actionLabels, item.actions);
			setConvergence(event, convergence);
		
			saveRodinFileOf(mac);
		
			runBuilder();
			
			IPORoot po = mac.getPORoot();
			
			if (convergence == ORDINARY)
				
				noSequent(po, "evt/VAR");
			
			else {
			
				final String rel = getRelationSymbol(convergence, item.kind);
				final String goal = item.varPost + rel + item.variant;
				hasSequent(po, "evt/VAR", environment, goal, invPredicates, item.guards);
			
				if (item.kind == INT_VARIANT && convergence != ANTICIPATED) {
					hasSequent(po, "evt/NAT", environment, item.variant + "∈ℕ", invPredicates, item.guards);
				}
			}
			
			if (item.kind == SET_VARIANT) {
				hasSequent(po, "FIN", environment, "finite(" + item.variant + ")", invPredicates);
			}
		}
	}

	private IMachineRoot createMachineFragment(String macName) throws RodinDBException {
		IMachineRoot mac = createMachine(macName);
		addVariables(mac, "A", "x", "y");
		addInvariants(mac, invLabels, invPredicates, isTheorem);
		return mac;
	}

	private String[] concat(String[] xs, String... ys) {
		final String[] rs = new String[xs.length + ys.length];
		arraycopy(xs, 0, rs, 0, xs.length);
		arraycopy(ys, 0, rs, xs.length, ys.length);
		return rs;
	}

	/*
	 * ordinary event variants
	 */
	@Test
	public void test_03_ordinary() throws Exception {
		
		Convergence convergence = ORDINARY;
		
		testItemList(convergence);
	
	}
	
	/*
	 * well-definedness of variants
	 */
	@Test
	public void test_04_wDef() throws Exception {
		IMachineRoot mac = createMachineFragment("mac");

		addVariant(mac, "1÷x");
		
		saveRodinFileOf(mac);
		
		runBuilder();

		IPORoot po = mac.getPORoot();
		hasSequent(po, "VWD", environment, "x≠0", invPredicates);
	}
	
	/**
	 * No PO is generated for convergent events refining convergent events
	 */
	@Test
	public void test_05_cvgRefinesCvg() throws Exception {
		IMachineRoot abs = createMachine("abs");

		addVariables(abs, "x");
		addInvariants(abs, makeSList("I"), makeSList("x∈ℤ"), false);
		IEvent aev = addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x ≔ x+1"));
		setConvergent(aev);
		addVariant(abs, "x");

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "x");
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("x ≔ x+2"));
		addEventRefines(evt, "evt");
		setConvergent(evt);
		addVariant(abs, "x");

		saveRodinFileOf(ref);
		
		runBuilder();
		
		IPORoot po = ref.getPORoot();
		
		noSequent(po, "evt/VAR");
	}

	/*
	 * if a machine does not have a variant, anticipated events should not
	 * generate variant proof obligations
	 */
	@Test
	public void test_06_anticipatedNoVariantNoPO() throws Exception {
		IMachineRoot mac = createMachineFragment("mac");
		IEvent evt = addEvent(mac, "evt");
		setAnticipated(evt);
		
		saveRodinFileOf(mac);
		
		runBuilder();

		IPORoot po = mac.getPORoot();
		
		noSequent(po, "evt/VAR");
	}
	
	/**
	 * if an anticipated and a convergent event are merged the abstraction is treated like an
	 * anticipated event: PO for convergence is created for convergent concrete events.
	 */
	@Test
	public void test_07_mergeAntCvg() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I"), makeSList("x∈ℤ"), false);
		IEvent aev = addEvent(abs, "aev", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x ≔ x+1"));
		setConvergent(aev);
		IEvent bev = addEvent(abs, "bev", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x ≔ x+1"));
		setAnticipated(bev);
		addVariant(abs, "x");

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "x");
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("x ≔ x+2"));
		addEventRefines(evt, "aev", "bev");
		setConvergent(evt);
		addVariant(ref, "x");

		saveRodinFileOf(ref);
		
		runBuilder();
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		
		IPOSequent sequent = getSequent(po, "evt/VAR");
		sequentHasGoal(sequent, typeEnvironment, "x+2<x");
	}
	
	/**
	 * if an anticipated and a convergent event are merged the abstraction is treated like an
	 * anticipated event: PO for anticipation is created for anticipated concrete events.
	 */
	@Test
	public void test_08_mergeAntCvg() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I"), makeSList("x∈ℤ"), false);
		IEvent aev = addEvent(abs, "aev", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x ≔ x+1"));
		setConvergent(aev);
		IEvent bev = addEvent(abs, "bev", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x ≔ x+1"));
		setAnticipated(bev);
		addVariant(abs, "x");

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "x");
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("x ≔ x+2"));
		addEventRefines(evt, "aev", "bev");
		setAnticipated(evt);
		addVariant(ref, "x");

		saveRodinFileOf(ref);
		
		runBuilder();
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		
		IPOSequent sequent = getSequent(po, "evt/VAR");
		sequentHasGoal(sequent, typeEnvironment, "x+2≤x");
	}
	
	/**
	 * A PO is generated for anticipated events refining convergent events.
	 * The abstract event is regarded as "only" being anticipated; the stronger
	 * property is "forgotten".
	 */
	@Test
	public void test_09_antRefinesCvg() throws Exception {
		IMachineRoot abs = createMachine("abs");
		addVariables(abs, "x");
		addInvariants(abs, makeSList("I"), makeSList("x∈ℤ"), false);
		IEvent aev = addEvent(abs, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x ≔ x+1"));
		setConvergent(aev);
		addVariant(abs, "x");

		saveRodinFileOf(abs);
		
		IMachineRoot ref = createMachine("ref");
		addMachineRefines(ref, "abs");
		addVariables(ref, "x");
	
		IEvent evt = addEvent(ref, "evt", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("B"), makeSList("x ≔ x+2"));
		addEventRefines(evt, "evt");
		setAnticipated(evt);
		addVariant(ref, "x");

		saveRodinFileOf(ref);
		
		runBuilder();
		
		ITypeEnvironmentBuilder typeEnvironment = mTypeEnvironment(
				"x=ℤ", factory);
		
		IPORoot po = ref.getPORoot();
		
		IPOSequent sequent = getSequent(po, "evt/VAR");
		sequentHasGoal(sequent, typeEnvironment, "x+2≤x");
	}

	/**
	 * No PO is generated for an anticipated event that does not modify the
	 * variant expression (case with a deterministic action).
	 */
	@Test
	public void test_10_anticipatedDoesNotModifyVariantDeterministic() throws Exception {
		IMachineRoot mch = createMachine("abs");

		addVariables(mch, "x", "y");
		addInvariants(mch, makeSList("I1", "I2"), makeSList("x∈ℤ", "y∈ℤ"),
				false, false);
		IEvent cvg = addEvent(mch, "cvg", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x ≔ x+1"));
		setConvergent(cvg);
		IEvent ant = addEvent(mch, "ant", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("y ≔ y+1"));
		setAnticipated(ant);
		addVariant(mch, "x");
		saveRodinFileOf(mch);
		
		runBuilder();
		
		IPORoot po = mch.getPORoot();
		
		noSequent(po, "ant/VAR");
		noSequent(po, "ant/NAT");
	}

	/**
	 * No PO is generated for an anticipated event that does not modify the
	 * variant expression (case with a non-deterministic action).
	 */
	@Test
	public void test_11_anticipatedDoesNotModifyVariantNonDeterministic() throws Exception {
		IMachineRoot mch = createMachine("abs");

		addVariables(mch, "x", "y");
		addInvariants(mch, makeSList("I1", "I2"), makeSList("x∈ℤ", "y∈ℤ"),
				false, false);
		IEvent cvg = addEvent(mch, "cvg", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("x ≔ x+1"));
		setConvergent(cvg);
		IEvent ant = addEvent(mch, "ant", 
				makeSList(), 
				makeSList(), makeSList(), 
				makeSList("A"), makeSList("y :∣ y < y'"));
		setAnticipated(ant);
		addVariant(mch, "x");
		saveRodinFileOf(mch);
		
		runBuilder();
		
		IPORoot po = mch.getPORoot();
		
		noSequent(po, "ant/VAR");
		noSequent(po, "ant/NAT");
	}

	/**
	 * Verify the local hypotheses of VAR and NAT POs.
	 */
	@Test
	public void test_12_LocalHyps() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		String[] invs = makeSList("x ∈ ℤ", "y ∈ ℤ", "z ∈ ℤ");
		String grd1 = "p ∈ 1 ‥ 2";
		ITypeEnvironment tenv = addInvariants(mch, invs);
		addVariant(mch, "vrn1", "x + y");
		IEvent event = addEvent(mch, "evt", //
				makeSList("p"), //
				makeSList("grd1"), makeSList(grd1), //
				makeSList("act1", "act2", "act3"), //
				makeSList("x ≔ x + p", "y :∈ 3 ‥ 4", "z :∈ 5 ‥ 6"));
		String baPred = "y' ∈ 3 ‥ 4";
		setConvergence(event, CONVERGENT);
		saveRodinFileOf(mch);

		runBuilder();

		final IPORoot po = mch.getPORoot();
		hasSequent(po, "evt/VAR", tenv, "(x + p) + y' < x + y", invs, grd1, baPred);
		hasSequent(po, "evt/NAT", tenv, "x + y ∈ ℕ", invs, grd1);
	}

	/**
	 * A VWD PO is generated for each potentially ill-defined variant (case of multi
	 * variants).
	 */
	@Test
	public void test_13_VWDmulti() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		String[] invs = makeSList("x ∈ ℤ", "y ∈ ℤ", "A ⊆ ℤ", "f ∈ ℙ(BOOL) ↔ ℤ", "B ⊆ BOOL");
		ITypeEnvironment tenv = addInvariants(mch, invs);
		addVariant(mch, "vrn1", "x");
		addVariant(mch, "vrn2", "1÷y");
		addVariant(mch, "vrn3", "A");
		addVariant(mch, "vrn4", "f(B)");
		saveRodinFileOf(mch);

		runBuilder();

		final IPORoot po = mch.getPORoot();
		noSequent(po, "VWD"); // No PO for unlabeled variant
		noSequent(po, "vrn1/VWD");
		hasSequent(po, "vrn2/VWD", tenv, "y≠0", invs);
		noSequent(po, "vrn3/VWD");
		hasSequent(po, "vrn4/VWD", tenv, "B ∈ dom(f) ∧ f ∈ ℙ(BOOL) ⇸ ℤ", invs);
	}

	/**
	 * A FIN PO is generated for each potentially infinite set variant (case of
	 * multi variants).
	 */
	@Test
	public void test_14_FINmulti() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		String[] invs = makeSList("A ⊆ ℤ", "B ⊆ BOOL", "x ∈ ℤ", "C ⊆ ℤ");
		ITypeEnvironment tenv = addInvariants(mch, invs);
		addVariant(mch, "vrn1", "A");
		addVariant(mch, "vrn2", "B");
		addVariant(mch, "vrn3", "x");
		addVariant(mch, "vrn4", "C");
		saveRodinFileOf(mch);

		runBuilder();

		final IPORoot po = mch.getPORoot();
		noSequent(po, "VWD"); // No PO for unlabeled variant
		hasSequent(po, "vrn1/FIN", tenv, "finite(A)", invs);
		noSequent(po, "vrn2/VWD");
		noSequent(po, "vrn3/VWD");
		hasSequent(po, "vrn4/FIN", tenv, "finite(C)", invs);
	}

	/**
	 * A VAR PO is generated for each set variant, when they are all modified by a
	 * convergent or anticipated event.
	 */
	@Test
	public void test_15_VARAllSet() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		String[] invs = makeSList("A ⊆ ℤ", "B ⊆ BOOL");
		ITypeEnvironment tenv = addInvariants(mch, invs);
		addVariant(mch, "vrn1", "A");
		addVariant(mch, "vrn2", "B");
		IEvent cvg = addEvent(mch, "cvg", makeSList(), makeSList(), makeSList(), //
				makeSList("act1", "act2"), makeSList("A :∈ ℙ(ℤ)", "B :∈ ℙ(BOOL)"));
		setConvergence(cvg, CONVERGENT);
		IEvent ant = addEvent(mch, "ant", makeSList(), makeSList(), makeSList(), //
				makeSList("act1", "act2"), makeSList("A :∈ ℙ(ℤ)", "B :∈ ℙ(BOOL)"));
		setConvergence(ant, ANTICIPATED);
		saveRodinFileOf(mch);

		runBuilder();

		final IPORoot po = mch.getPORoot();
		noSequent(po, "NAT"); // No PO for unlabeled variant
		noSequent(po, "VAR"); // No PO for unlabeled variant
		noSequent(po, "cvg/NAT"); // No PO for unlabeled variant
		noSequent(po, "cvg/VAR"); // No PO for unlabeled variant
		noSequent(po, "ant/NAT"); // No PO for unlabeled variant
		noSequent(po, "ant/VAR"); // No PO for unlabeled variant

		noSequent(po, "cvg/vrn1/NAT");
		hasSequent(po, "cvg/vrn1/VAR", tenv, "A' ⊆ A", invs, "A' ∈ ℙ(ℤ)");

		noSequent(po, "cvg/vrn2/NAT");
		hasSequent(po, "cvg/vrn2/VAR", tenv, "B' ⊂ B", invs, "A' ∈ ℙ(ℤ)", "A' = A", "B' ∈ ℙ(BOOL)");

		noSequent(po, "ant/vrn1/NAT");
		hasSequent(po, "ant/vrn1/VAR", tenv, "A' ⊆ A", invs, "A' ∈ ℙ(ℤ)");

		noSequent(po, "ant/vrn2/NAT");
		hasSequent(po, "ant/vrn2/VAR", tenv, "B' ⊆ B", invs, "A' ∈ ℙ(ℤ)", "A' = A", "B' ∈ ℙ(BOOL)");
	}

	/**
	 * A VAR and a NAT PO is generated for each natural variant, when they are all
	 * modified by a convergent or anticipated event.
	 */
	@Test
	public void test_16_VARAllInt() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		String[] invs = makeSList("x ∈ ℤ", "y ∈ ℤ");
		ITypeEnvironment tenv = addInvariants(mch, invs);
		addVariant(mch, "vrn1", "x");
		addVariant(mch, "vrn2", "y");
		IEvent cvg = addEvent(mch, "cvg", makeSList(), makeSList(), makeSList(), //
				makeSList("act1", "act2"), makeSList("x :∈ ℤ", "y :∈ ℤ"));
		setConvergence(cvg, CONVERGENT);
		IEvent ant = addEvent(mch, "ant", makeSList(), makeSList(), makeSList(), //
				makeSList("act1", "act2"), makeSList("x :∈ ℤ", "y :∈ ℤ"));
		setConvergence(ant, ANTICIPATED);
		saveRodinFileOf(mch);

		runBuilder();

		final IPORoot po = mch.getPORoot();
		noSequent(po, "NAT"); // No PO for unlabeled variant
		noSequent(po, "VAR"); // No PO for unlabeled variant
		noSequent(po, "cvg/NAT"); // No PO for unlabeled variant
		noSequent(po, "cvg/VAR"); // No PO for unlabeled variant
		noSequent(po, "ant/NAT"); // No PO for unlabeled variant
		noSequent(po, "ant/VAR"); // No PO for unlabeled variant

		hasSequent(po, "cvg/vrn1/NAT", tenv, "x ∈ ℕ", invs);
		hasSequent(po, "cvg/vrn1/VAR", tenv, "x' ≤ x", invs, "x' ∈ ℤ");

		hasSequent(po, "cvg/vrn2/NAT", tenv, "y ∈ ℕ", invs, "x' ∈ ℤ", "x' = x");
		hasSequent(po, "cvg/vrn2/VAR", tenv, "y' < y", invs, "x' ∈ ℤ", "x' = x", "y' ∈ ℤ");

		hasSequent(po, "ant/vrn1/NAT", tenv, "x ∈ ℕ", invs);
		hasSequent(po, "ant/vrn1/VAR", tenv, "x' ≤ x", invs, "x' ∈ ℤ");

		noSequent(po, "ant/vrn2/NAT");
		hasSequent(po, "ant/vrn2/VAR", tenv, "y' ≤ y", invs, "x' ∈ ℤ", "x' = x", "y' ∈ ℤ");
	}

	/**
	 * A VAR (and a NAT PO) is generated for each variant which is modified by a
	 * convergent or anticipated event.
	 */
	@Test
	public void test_17_VARModOnly() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		String[] invs = makeSList("A ⊆ ℤ", "B ⊆ BOOL", "x ∈ ℤ", "y ∈ ℤ", "z ∈ ℤ");
		ITypeEnvironment tenv = addInvariants(mch, invs);
		addVariant(mch, "vrn1", "A");
		addVariant(mch, "vrn2", "x");
		addVariant(mch, "vrn3", "y");
		addVariant(mch, "vrn4", "B");
		addVariant(mch, "vrn5", "z");
		IEvent cvg = addEvent(mch, "cvg", makeSList(), makeSList(), makeSList(), //
				makeSList("act1", "act2"), makeSList("x :∈ ℤ", "B :∈ ℙ(BOOL)"));
		setConvergence(cvg, CONVERGENT);
		IEvent ant = addEvent(mch, "ant", makeSList(), makeSList(), makeSList(), //
				makeSList("act1", "act2"), makeSList("A :∈ ℙ(ℤ)", "y :∈ ℤ"));
		setConvergence(ant, ANTICIPATED);
		saveRodinFileOf(mch);

		runBuilder();

		final IPORoot po = mch.getPORoot();
		noSequent(po, "cvg/vrn1/NAT"); // Set variant
		noSequent(po, "cvg/vrn1/VAR"); // Not modified

		hasSequent(po, "cvg/vrn2/NAT", tenv, "x ∈ ℕ", invs);
		hasSequent(po, "cvg/vrn2/VAR", tenv, "x' ≤ x", invs, "x' ∈ ℤ");

		noSequent(po, "cvg/vrn3/NAT"); // Not modified
		noSequent(po, "cvg/vrn3/VAR"); // Not modified

		noSequent(po, "cvg/vrn4/NAT"); // Set variant
		hasSequent(po, "cvg/vrn4/VAR", tenv, "B' ⊂ B", invs, "x' ∈ ℤ", "x' = x", "B' ∈ ℙ(BOOL)");

		noSequent(po, "cvg/vrn5/NAT"); // Not modified
		noSequent(po, "cvg/vrn5/VAR"); // Not modified
		
		noSequent(po, "ant/vrn1/NAT"); // Set variant
		hasSequent(po, "ant/vrn1/VAR", tenv, "A' ⊆ A", invs, "A' ∈ ℙ(ℤ)");

		noSequent(po, "ant/vrn2/NAT"); // Not modified
		noSequent(po, "ant/vrn2/VAR"); // Not modified

		noSequent(po, "ant/vrn3/NAT"); // Last modified variant for anticipated
		hasSequent(po, "ant/vrn3/VAR", tenv, "y' ≤ y", invs, "A' ∈ ℙ(ℤ)", "A' = A", "y' ∈ ℤ");

		noSequent(po, "ant/vrn4/NAT"); // Set variant
		noSequent(po, "ant/vrn4/VAR"); // Not modified

		noSequent(po, "ant/vrn5/NAT"); // Not modified
		noSequent(po, "ant/vrn5/VAR"); // Not modified
	}

	/**
	 * A VAR is generated for the last variant when no variant is modified by a
	 * convergent event. For an anticipated event, there is no PO.
	 */
	@Test
	public void test_17_VARSetUntouched() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		String[] invs = makeSList("A ⊆ ℤ", "B ⊆ BOOL");
		ITypeEnvironment tenv = addInvariants(mch, invs);
		addVariant(mch, "vrn1", "A");
		addVariant(mch, "vrn2", "B");
		IEvent cvg = addEvent(mch, "cvg", makeSList(), makeSList(), makeSList(), //
				makeSList(), makeSList());
		setConvergence(cvg, CONVERGENT);
		IEvent ant = addEvent(mch, "ant", makeSList(), makeSList(), makeSList(), //
				makeSList(), makeSList());
		setConvergence(ant, ANTICIPATED);
		saveRodinFileOf(mch);

		runBuilder();

		final IPORoot po = mch.getPORoot();
		noSequent(po, "cvg/vrn1/NAT"); // Set variant
		noSequent(po, "cvg/vrn1/VAR"); // Not modified

		noSequent(po, "cvg/vrn2/NAT"); // Set variant
		hasSequent(po, "cvg/vrn2/VAR", tenv, "B ⊂ B", invs);

		noSequent(po, "ant/vrn1/NAT"); // Set variant
		noSequent(po, "ant/vrn1/VAR"); // Not modified

		noSequent(po, "ant/vrn2/NAT"); // Set variant
		noSequent(po, "ant/vrn2/VAR"); // Not modified
	}

	/**
	 * A VAR and a NAT PO is generated for the last variant when no variant is
	 * modified by a convergent event. For an anticipated event, there is no PO.
	 */
	@Test
	public void test_18_NATSetUntouched() throws Exception {
		final IMachineRoot mch = createMachine("mch");
		String[] invs = makeSList("x ∈ ℤ", "y ∈ ℤ");
		ITypeEnvironment tenv = addInvariants(mch, invs);
		addVariant(mch, "vrn1", "x");
		addVariant(mch, "vrn2", "y");
		IEvent cvg = addEvent(mch, "cvg", makeSList(), makeSList(), makeSList(), //
				makeSList(), makeSList());
		setConvergence(cvg, CONVERGENT);
		IEvent ant = addEvent(mch, "ant", makeSList(), makeSList(), makeSList(), //
				makeSList(), makeSList());
		setConvergence(ant, ANTICIPATED);
		saveRodinFileOf(mch);

		runBuilder();

		final IPORoot po = mch.getPORoot();
		noSequent(po, "cvg/vrn1/NAT"); // Not modified
		noSequent(po, "cvg/vrn1/VAR"); // Not modified

		hasSequent(po, "cvg/vrn2/NAT", tenv, "y ∈ ℕ", invs);
		hasSequent(po, "cvg/vrn2/VAR", tenv, "y < y", invs);

		noSequent(po, "ant/vrn1/NAT"); // Not modified
		noSequent(po, "ant/vrn1/VAR"); // Not modified

		noSequent(po, "ant/vrn2/NAT"); // Not modified
		noSequent(po, "ant/vrn2/VAR"); // Not modified
	}

	/**
	 * Adds all the given invariants to the machine, together with the variables
	 * they contain and returns the resulting type environment.
	 * 
	 * @param mch       some Event-B machine
	 * @param invImages array of predicate strings
	 * @return the resulting type environment
	 * @throws RodinDBException
	 */
	private ITypeEnvironment addInvariants(IMachineRoot mch, String[] invImages) throws RodinDBException {
		final ITypeEnvironmentBuilder tenv = factory.makeTypeEnvironment();
		for (int i = 0; i < invImages.length; ++i) {
			addInvariant(mch, "inv" + i, invImages[i], false);
			final Predicate p = factory.parsePredicate(invImages[i], null).getParsedPredicate();
			final ITypeCheckResult tcResult = p.typeCheck(tenv);
			assertTrue(p.isTypeChecked());
			tenv.addAll(tcResult.getInferredEnvironment());
		}
		final Set<String> names = tenv.getNames();
		addVariables(mch, names.toArray(new String[names.size()]));
		return tenv;
	}

	public void hasSequent(IPORoot po, String name, ITypeEnvironment tenv, //
			String goal, String[] globals, String... locals) throws Exception {
		final IPOSequent sequent = getSequent(po, name);
		sequentHasExactlyHypotheses(sequent, tenv, globals, locals);
		sequentHasGoal(sequent, tenv, goal);
	}

	public void sequentHasExactlyHypotheses(IPOSequent sequent, //
			ITypeEnvironment typeEnvironment, //
			String[] globals, String... locals) throws Exception {
		final String[] hyps = concat(globals, locals);
		sequentHasExactlyHypotheses(sequent, typeEnvironment, hyps);
	}
}
