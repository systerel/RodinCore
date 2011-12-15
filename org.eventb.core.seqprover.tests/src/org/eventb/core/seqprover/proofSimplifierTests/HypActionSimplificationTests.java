/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofSimplifierTests;

import static java.util.Arrays.asList;
import static org.eventb.core.seqprover.ProverFactory.makeForwardInfHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeHideHypAction;
import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.proofBuilder.ProofBuilder.rebuild;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.autoRewrites;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.hyp;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.trueGoal;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.typeRewrites;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eventb.core.seqprover.IHypAction;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.tactics.tests.TreeShape;
import org.junit.runners.Parameterized.Parameters;

/**
 * @author Nicolas Beauger
 *
 */
public class HypActionSimplificationTests extends AbstractSimplificationTests {
	
	private static IHypAction fwd(String hyp, String inf) {
		return makeForwardInfHypAction(asList(p(hyp)), asList(p(inf)));
	}

	private static IHypAction hide(String hyp) {
		return makeHideHypAction(asList(p(hyp)));
	}

	private static void assertHypActions(IProofTree simplified, List<IHypAction> expectedHypActions) {
		final IAntecedent[] antecedents = simplified.getRoot().getRule().getAntecedents();
		if (antecedents.length == 0) {
			assertTrue(expectedHypActions.isEmpty());
			return;
		}
		final List<IHypAction> actual = antecedents[0].getHypActions();
		assertEquals(expectedHypActions.size(), actual.size());
		for (int i = 0; i < actual.size(); i++) {
			assertTrue(ProverLib.deepEquals(expectedHypActions.get(i), actual.get(i)));
		}
	}

	private static Object[] test(String shapeSequent, TreeShape initial,
			String testSequent, List<IHypAction> testHypActions,
			TreeShape expected, List<IHypAction> hypActions) {
		return new Object[] { shapeSequent, initial, testSequent,
				testHypActions, expected, hypActions };
	}
	
	// the shapeSequent is used to generate the initial tree shape
	// then the testSequent is used to build the initial proof tree
	// it typically contains less hyps, which makes superfluous hyp actions
	private final String testSequent;

	// hyp actions expected on root node before simplification
	private final List<IHypAction> testHypActions;
	
	// hyp actions expected on root node after simplification
	private final List<IHypAction> simpHypActions;
	
	public HypActionSimplificationTests(String sequent, TreeShape initial,
			String testSequent, List<IHypAction> testHypActions,
			TreeShape expected, List<IHypAction> simpHypActions) {
		super(sequent, initial, expected);
		this.testSequent = testSequent;
		this.testHypActions = testHypActions;
		this.simpHypActions = simpHypActions;
	}

	@Override
	protected IProofTree genProofTree() {
		final IProofTree initProof = super.genProofTree();

		final IProverSequent treeSeq = genSeq(testSequent);
		final IProofTree testProof = makeProofTree(treeSeq, null);

		assertTrue(rebuild(testProof.getRoot(), initProof.getRoot(), null));
		assertTrue(testProof.isClosed());
		assertHypActions(testProof, testHypActions);
		return testProof;
	}

	@Override
	protected void additionalChecks(IProofTree simplified) {
		assertHypActions(simplified, simpHypActions);
	}
	
	@Parameters
	public static List<Object[]> getTestCases() throws Exception {
		return Arrays.<Object[]> asList(

		/**
		 * 1 hyp action:
		 * 'x ∈ ℤ': hide
		 * the step producing the hyp action gets deleted by simplification
		 * 
		 * Proof tree:
		 * 0
		 * 1
		 * without the 'x ∈ ℤ' hypothesis
		 * 
		 * Dependencies:
		 * {}
		 * 
		 * Expected:
		 * 1
		 */
		test("x ∈ ℤ |- ⊤",
				// initial
				typeRewrites(
						trueGoal()),
				"|- ⊤",
				asList(hide("x ∈ ℤ")),
				// expected
				trueGoal(),
				Collections.<IHypAction>emptyList()),
				
		/**
		 * 5 hyp actions:
		 *  'x={0 ↦ 1}∼': forward inference + hide
		 *  '¬ FALSE=y': forward inference + hide
		 *  '0=0': hide
		 * the step producing the hyp action remains after simplification
		 *  
		 * Proof tree:
		 * 0
		 * 1
		 * 'x={0 ↦ 1}∼' is the only remaining hyp
		 * 
		 * Dependencies:
		 * {0->1}
		 * 
		 * Expected:
		 * 0
		 * 1
		 * and the 2 hyp actions on 'x={0 ↦ 1}∼'
		 */
		test("x={0 ↦ 1}∼ ;; ¬ FALSE=y ;; 0=0 |- x={1 ↦ 0}",
				// initial
				autoRewrites(
						hyp()),
				"x={0 ↦ 1}∼ |- x={1 ↦ 0}",
				asList(fwd("x={0 ↦ 1}∼", "x={1 ↦ 0}"), hide("x={0 ↦ 1}∼"),
						fwd("¬ FALSE = y", "TRUE = y"), hide("¬ FALSE=y"),
						hide("0=0")),
				// expected
				autoRewrites(
						hyp()),
				asList(fwd("x={0 ↦ 1}∼", "x={1 ↦ 0}"), hide("x={0 ↦ 1}∼")))
				
				
		);
	}
	
}
