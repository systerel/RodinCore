/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofSimplifierTests;

import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.ProverFactory.makeSequent;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.allD;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.eqE;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.hyp;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genPreds;
import static org.eventb.core.seqprover.tests.TestLib.genTypeEnv;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tests.TestLib;
import org.junit.Test;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProofSimplifierTests {

	private static void assertNoHypAction(IProofRule proofRule,
			String actionType) {
		assertHypActions(proofRule, actionType, 0);
	}

	private static void assertHypActions(IProofRule rule, String actionType,
			int expectedCount) {
		final IAntecedent[] antecedents = rule.getAntecedents();
		int count = 0;
		for (IAntecedent antecedent : antecedents) {
			final List<IHypAction> hypActions = antecedent.getHypActions();
			for (IHypAction hypAction : hypActions) {
				if (hypAction.getActionType().equals(actionType)) {
					count++;
				}
			}
		}
		assertEquals("unexpected hyp actions count", expectedCount, count);
	}

	private static void assertTree(IProofTree expected, IProofTree actual) {
		assertNotNull("expected a non null proof tree", actual);
		assertTrue("unexpected proof tree", ProverLib.deepEquals(expected,
				actual));
	}

	private static final AutoTactics.AutoRewriteTac autoRewriteTac = new AutoTactics.AutoRewriteTac();
	
	@Test
	public void testNonClosedTree() throws Exception {
		final IProofTree openProofTree = ProverFactory.makeProofTree(TestLib
				.genSeq("|- ⊥"), this);
		
		assertFalse(openProofTree.isClosed());
		try {
			ProverLib.simplify(openProofTree, null);
			fail("IllegalArgumentException expected (non closed proof tree)");
		} catch (IllegalArgumentException e) {
			// as expected
		}
	}
	
	@Test
	public void testMustNotSimplify() throws Exception {
		final IProofTree prTree = ProverFactory.makeProofTree(TestLib
				.genSeq("c1≠1  |- ¬c1=1"), this);
		final IProofTreeNode root = prTree.getRoot();
		autoRewriteTac.apply(root, null);
		hyp().apply(root.getFirstOpenDescendant(), null);

		final IProofTree simplified = ProverLib.simplify(prTree, null);

		assertNull("should not have been able to simplify", simplified);
	}

	@Test
	public void testRemoveFwdHide() throws Exception {
		final IProofTree prTree = ProverFactory.makeProofTree(TestLib
				.genSeq("c1≠1 ;; c1≠c2 |- ¬c1=1"), this);
		
		final IProofTreeNode root = prTree.getRoot();
		autoRewriteTac.apply(root, null); // generates unneeded pred:  "¬c2=c1"
		hyp().apply(root.getFirstOpenDescendant(), null);

		final IProofTree simplified = ProverLib.simplify(prTree, null);

		final IProofRule proofRule = simplified.getRoot().getRule();
		assertHypActions(proofRule,
				IHypAction.IForwardInfHypAction.ACTION_TYPE, 1);
		assertHypActions(proofRule,
				IHypAction.ISelectionHypAction.HIDE_ACTION_TYPE, 1);
	}

	@Test
	public void testSkipNode() throws Exception {
		final IProverSequent sequent = TestLib
				.genSeq("c1≠1 ;; c2≠2;; ¬c2=c1  |- ¬c2=c1");
		final IProofTree prTree = ProverFactory.makeProofTree(sequent, this);

		final IProofTreeNode root = prTree.getRoot();
		autoRewriteTac.apply(root, null); // generates unneeded pred:  "¬c2=c1"
		hyp().apply(root.getFirstOpenDescendant(), null);

		final IProofTree expected = ProverFactory.makeProofTree(sequent, this);
		hyp().apply(expected.getRoot().getFirstOpenDescendant(), null);

		final IProofTree simplified = ProverLib.simplify(prTree, null);

		assertTree(expected, simplified);
		final IProofRule proofRule = simplified.getRoot().getRule();
		assertNoHypAction(proofRule,
				IHypAction.IForwardInfHypAction.ACTION_TYPE);
		assertNoHypAction(proofRule,
				IHypAction.ISelectionHypAction.HIDE_ACTION_TYPE);
	}

	private IProofTree makeMoreComplex() {
		final ITypeEnvironment typeEnv = genTypeEnv("S=ℙ(S),c=S,c2=ℤ,r=ℙ(S×S)");
		final Set<Predicate> hyps = genPreds(typeEnv, "c∈dom(r)", "c2=0" , "c2≠c1" , "r∈S ⇸ S" , "∀x·r(x)∈S ∖ {x}");
		final Predicate goal = TestLib.genPred(typeEnv, "r(r(c)) ≠ r(c)");
		final IProverSequent sequent = makeSequent(typeEnv, hyps, goal);
		final IProofTree prTree = makeProofTree(sequent, this);
		IProofTreeNode node = prTree.getRoot();
		allD(genPred(typeEnv, "∀x·r(x)∈S ∖ {x}"), "r(c)").apply(node, null);
		final IProofTreeNode branch1 = node;

		node = branch1.getFirstOpenDescendant();
		autoRewriteTac.apply(node, null); // generates unneeded pred:  "¬c2=c1"
		node = node.getFirstOpenDescendant();
		eqE(genPred("c2=0")).apply(node, null);
		node = node.getFirstOpenDescendant();
		Tactics.conjI().apply(node, null);
		final IProofTreeNode branch2 = node;
		node = node.getFirstOpenDescendant();
		hyp().apply(node, null);
		node = branch2.getFirstOpenDescendant();
		hyp().apply(node, null);
		
		node = branch1.getFirstOpenDescendant();
		Tactics.removeMembership(genPred(typeEnv, "r(r(c))∈S ∖ {r(c)}"), IPosition.ROOT).apply(node, null);
		node = node.getFirstOpenDescendant();
		Tactics.removeMembership(genPred(typeEnv, "¬r(r(c))∈{r(c)}"), IPosition.ROOT).apply(node, null);
		node = node.getFirstOpenDescendant();
		autoRewriteTac.apply(node, null);
		node = node.getFirstOpenDescendant();
		hyp().apply(node, null);

		assertTrue(prTree.isClosed());
		return prTree;
	}
	
	@Test
	public void testMoreComplex() throws Exception {
		final IProofTree prTree = makeMoreComplex();

		final IProofTree simplified = ProverLib.simplify(prTree, null);

		assertNotNull(simplified);
	}

	@Test
	public void testCancelSimplifier() throws Exception {
		final IProofTree prTree = makeMoreComplex();

		final IProofMonitor monitor = new FakeProofMonitor(20);
		// 20th cancel check should occur while removing fwd in an antecedent
		final IProofTree simplified = ProverLib.simplify(prTree, monitor);

		assertNull(simplified);
	}

	@Test
	public void testRemoveSelect() throws Exception {
		final String sequent = "c ∈ ℤ∖{0} ;; c ∈ ℕ∖{0} |- c ∈ ℕ";
		
		final IProofTree prTree = ProverFactory.makeProofTree(TestLib
				.genSeq(sequent), this);
		final IProofTreeNode root = prTree.getRoot();
		Tactics.removeMembership(TestLib.genPred("c ∈ ℤ∖{0}"), IPosition.ROOT)
				.apply(root.getFirstOpenDescendant(), null);
		Tactics.removeMembership(TestLib.genPred("c ∈ ℕ∖{0}"), IPosition.ROOT)
				.apply(root.getFirstOpenDescendant(), null);
		hyp().apply(root.getFirstOpenDescendant(), null);
	
		final IProofTree expected = ProverFactory.makeProofTree(TestLib
				.genSeq(sequent), this);
		final IProofTreeNode eRoot = expected.getRoot();
		Tactics.removeMembership(TestLib.genPred("c ∈ ℕ∖{0}"), IPosition.ROOT)
		.apply(eRoot.getFirstOpenDescendant(), null);
		hyp().apply(eRoot.getFirstOpenDescendant(), null);
		
		final IProofTree simplified = ProverLib.simplify(prTree, null);
	
		assertTree(expected, simplified);
	}

}
