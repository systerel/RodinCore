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

import java.util.Arrays;
import java.util.List;

import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPORoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.ast.ITypeEnvironment;
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
		public final String[] hypotheses;
		public VariantTestItem(
				final int kind, final String variant, 
				final String[] guardLabels, final String[] guards, 
				final String[] actionLabels, final String[] actions, 
				final String varPost, final String[] hypotheses) {
			assert guardLabels.length == guards.length;
			assert actionLabels.length == actions.length;
			this.kind = kind;
			this.variant = variant;
			this.guardLabels = guardLabels;
			this.guards = guards;
			this.actionLabels = actionLabels;
			this.actions = actions;
			this.varPost = varPost;
			this.hypotheses = hypotheses;
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
				"1",
				makeSList()),
		new VariantTestItem(
				INT_VARIANT,
				"x",
				makeSList(),
				makeSList(),
				makeSList("A"),
				makeSList("x ≔ x+1"),
				"x+1",
				makeSList()),
		new VariantTestItem(
				INT_VARIANT,
				"x+y",
				makeSList(),
				makeSList(),
				makeSList("A", "B"),
				makeSList("x ≔ x+1", "y ≔ y∗x"),
				"(x+1)+(y∗x)",
				makeSList()),
		new VariantTestItem(
				SET_VARIANT,
				"A",
				makeSList(),
				makeSList(),
				makeSList("A", "B"),
				makeSList("x ≔ x+1", "A ≔ {x}"),
				"{x}",
				makeSList()),
		new VariantTestItem(
				SET_VARIANT,
				"A∖{x}",
				makeSList(),
				makeSList(),
				makeSList("A", "B"),
				makeSList("x ≔ x+1", "A ≔ A ∪ {x}"),
				"(A∪{x})∖{x+1}",
				makeSList()),
		new VariantTestItem(
				SET_VARIANT,
				"A∖{x}",
				makeSList("G", "H"),
				makeSList("x>1", "A≠∅"),
				makeSList("A", "B"),
				makeSList("x ≔ x+1", "A ≔ A ∪ {x}"),
				"(A∪{x})∖{x+1}",
				makeSList()),
	};
	
	private String getRelationSymbol(IConvergenceElement.Convergence convergence, int kind) {
		if (convergence == IConvergenceElement.Convergence.ANTICIPATED)
			switch (kind) {
			case INT_VARIANT:
				return " ≤ ";
			case SET_VARIANT:
				return " ⊆ ";
			default:
				return null;
			}
		if (convergence == IConvergenceElement.Convergence.CONVERGENT)
			switch (kind) {
			case INT_VARIANT:
				return " < ";
			case SET_VARIANT:
				return " ⊂ ";
			default:
				return null;
			}
		return null;
	}
	
	/*
	 * anticipated event variants
	 */
	public void test_01_anticipated() throws Exception {
		
		Convergence convergence = IConvergenceElement.Convergence.ANTICIPATED;
		
		testItemList(convergence);
	
	}

	/*
	 * anticipated event variants
	 */
	public void test_02_convergent() throws Exception {
		
		Convergence convergence = IConvergenceElement.Convergence.CONVERGENT;
		
		testItemList(convergence);
	
	}

	String[] invLabels = makeSList("I1", "I2", "I3");
	String[] invPredicates = makeSList("A ⊆ ℤ", "x ∈ ℤ", "y ∈ ℤ");
	boolean[] isTheorem = new boolean[] { false, false, false};
	
	ITypeEnvironment environment;
	{
		environment = factory.makeTypeEnvironment();
		environment.addName("A", factory.makePowerSetType(factory.makeIntegerType()));
		environment.addName("x", factory.makeIntegerType());
		environment.addName("y", factory.makeIntegerType());
	}
	
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
			
			if (convergence == IConvergenceElement.Convergence.ORDINARY)
				
				noSequent(po, "evt/VAR");
			
			else {
			
				sequent = getSequent(po, "evt/VAR");
				
				String[] hypotheses = concat(invPredicates, item.hypotheses);
		
				sequentHasHypotheses(sequent, environment, hypotheses);
			
				String rel = getRelationSymbol(convergence, item.kind);
			
				sequentHasGoal(sequent, environment, item.varPost + rel + item.variant);
			
				if (item.kind == INT_VARIANT) {
					sequent = getSequent(po, "evt/NAT");
				
					sequentHasHypotheses(sequent, environment, hypotheses);
					sequentHasGoal(sequent, environment, item.variant + "∈ℕ");
				}
			}
			
			if (item.kind == SET_VARIANT) {
				sequent = getSequent(po, "FIN");
				
				sequentHasHypotheses(sequent, environment, invPredicates);
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

	private String[] concat(String[] as, String[] bs) {
		List<String> hypothesesList = Arrays.asList(as);
		hypothesesList.addAll(Arrays.asList(bs));
		String[] cs = new String[hypothesesList.size()];
		hypothesesList.toArray(cs);
		return cs;
	}
	
	/*
	 * ordinary event variants
	 */
	public void test_03_ordinary() throws Exception {
		
		Convergence convergence = IConvergenceElement.Convergence.ORDINARY;
		
		testItemList(convergence);
	
	}
	
	/*
	 * well-definedness of variants
	 */
	public void test_04_wDef() throws Exception {
		IMachineRoot mac = createMachineFragment("mac");

		addVariant(mac, "1÷x");
		
		saveRodinFileOf(mac);
		
		runBuilder();

		IPORoot po = mac.getPORoot();
		
		IPOSequent sequent = getSequent(po, "VWD");
	
		sequentHasHypotheses(sequent, environment, invPredicates);
		sequentHasGoal(sequent, environment, "x≠0");
	}
	
	/**
	 * No PO is generated for convergent events refining convergent events
	 */
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
	 * if a machine does not have a variant, antipated events should generate
	 * variant proof obligations
	 */
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
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		
		IPORoot po = ref.getPORoot();
		
		IPOSequent sequent = getSequent(po, "evt/VAR");
		sequentHasGoal(sequent, typeEnvironment, "x+2<x");
	}
	
	/**
	 * if an anticipated and a convergent event are merged the abstraction is treated like an
	 * anticipated event: PO for anticipation is created for anticipated concrete events.
	 */
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
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		
		IPORoot po = ref.getPORoot();
		
		IPOSequent sequent = getSequent(po, "evt/VAR");
		sequentHasGoal(sequent, typeEnvironment, "x+2≤x");
	}
	
	/**
	 * A PO is generated for anticipated events refining convergent events.
	 * The abstract event is regarded as "only" being anticipated; the stronger
	 * property is "forgotten".
	 */
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
		
		ITypeEnvironment typeEnvironment = factory.makeTypeEnvironment();
		typeEnvironment.addName("x", intType);
		
		IPORoot po = ref.getPORoot();
		
		IPOSequent sequent = getSequent(po, "evt/VAR");
		sequentHasGoal(sequent, typeEnvironment, "x+2≤x");
	}

}
