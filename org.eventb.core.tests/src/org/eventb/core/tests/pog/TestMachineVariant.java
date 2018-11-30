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
		
			IPOSequent sequent;
			
			if (convergence == ORDINARY)
				
				noSequent(po, "evt/VAR");
			
			else {
			
				sequent = getSequent(po, "evt/VAR");
				
				String[] hypotheses = concat(invPredicates, item.guards);
		
				sequentHasExactlyHypotheses(sequent, environment, hypotheses);
			
				String rel = getRelationSymbol(convergence, item.kind);
			
				sequentHasGoal(sequent, environment, item.varPost + rel + item.variant);
			
				if (item.kind == INT_VARIANT && convergence != ANTICIPATED) {
					sequent = getSequent(po, "evt/NAT");
				
					sequentHasExactlyHypotheses(sequent, environment, hypotheses);
					sequentHasGoal(sequent, environment, item.variant + "∈ℕ");
				}
			}
			
			if (item.kind == SET_VARIANT) {
				sequent = getSequent(po, "FIN");
				
				sequentHasExactlyHypotheses(sequent, environment, invPredicates);
				sequentHasGoal(sequent, environment, "finite(" + item.variant + ")");
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
		
		IPOSequent sequent = getSequent(po, "VWD");
	
		sequentHasExactlyHypotheses(sequent, environment, invPredicates);
		sequentHasGoal(sequent, environment, "x≠0");
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
		IPOSequent sequent;

		sequent = getSequent(po, "evt/VAR");
		sequentHasExactlyHypotheses(sequent, tenv, concat(invs, grd1, baPred));
		sequentHasGoal(sequent, tenv, "(x + p) + y' < x + y");

		sequent = getSequent(po, "evt/NAT");
		sequentHasExactlyHypotheses(sequent, tenv, concat(invs, grd1));
		sequentHasGoal(sequent, tenv, "x + y ∈ ℕ");
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
		IPOSequent sequent;

		noSequent(po, "VWD"); // No PO for unlabeled variant
		noSequent(po, "vrn1/VWD");
		sequent = getSequent(po, "vrn2/VWD");
		sequentHasExactlyHypotheses(sequent, tenv, invs);
		sequentHasGoal(sequent, tenv, "y≠0");
		noSequent(po, "vrn3/VWD");
		sequent = getSequent(po, "vrn4/VWD");
		sequentHasExactlyHypotheses(sequent, tenv, invs);
		sequentHasGoal(sequent, tenv, "B ∈ dom(f) ∧ f ∈ ℙ(BOOL) ⇸ ℤ");
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
		IPOSequent sequent;

		noSequent(po, "VWD"); // No PO for unlabeled variant
		sequent = getSequent(po, "vrn1/FIN");
		sequentHasExactlyHypotheses(sequent, tenv, invs);
		sequentHasGoal(sequent, tenv, "finite(A)");
		noSequent(po, "vrn2/VWD");
		noSequent(po, "vrn3/VWD");
		sequent = getSequent(po, "vrn4/FIN");
		sequentHasExactlyHypotheses(sequent, tenv, invs);
		sequentHasGoal(sequent, tenv, "finite(C)");
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

}
