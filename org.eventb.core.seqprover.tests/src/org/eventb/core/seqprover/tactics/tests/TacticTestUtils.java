/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;
import org.eventb.core.seqprover.tests.TestLib;

/**
 * Utility class for tactic tests.
 */
public class TacticTestUtils {

	private static final IAutoTacticRegistry tacRegistry = SequentProver
			.getAutoTacticRegistry();

	/**
	 * Asserts that the application of the given tactic returns
	 * <code>null</code> and that the resulting tree shape is equal to the given
	 * <code>expected</code> tree shape.
	 * 
	 * @param node
	 *            the considered proof tree node
	 * @param expected
	 *            the expected tree shape
	 * @param tac
	 *            the tactic tested
	 */
	public static void assertSuccess(IProofTreeNode node, TreeShape expected,
			ITactic tac) {
		TreeShape.assertSuccess(node, expected, tac);
	}

	/**
	 * Asserts that the application of the given tactic fails and does not
	 * modify the proof tree.
	 * 
	 * @param node
	 *            the considered proof tree node
	 * @param tac
	 *            the tactic tested
	 */
	public static void assertFailure(IProofTreeNode node, ITactic tac) {
		TreeShape.assertFailure(node, tac);
	}

	/**
	 * Asserts that the tactic registry contains a descriptor corresponding to
	 * the given id, and that the instance of the tactic attached to this
	 * descriptor is equal to the tactic given as parameter.
	 * 
	 * @param id
	 *            the id of the tactic that should be registered
	 * @param tac
	 *            the instance of the tactic to compare the runtime class with
	 */
	public static void assertTacticRegistered(String id, ITactic tac) {
		final ITacticDescriptor desc = tacRegistry.getTacticDescriptor(id);
		assertNotNull(desc);
		assertEquals(tac.getClass(), desc.getTacticInstance().getClass());
	}

	/**
	 * Asserts that all the items of the given pair of parameter arrays are
	 * registered. Note that arrays must have the same length as each id must
	 * have a corresponding tactic instance.
	 * 
	 * @param tacticIds
	 *            the array of tactic ids to assert the registration
	 * @param tacs
	 *            the array of corresponding tactic instances
	 */
	public static void assertTacticsRegistered(String[] tacticIds,
			ITactic[] tacs) {
		assertEquals(
				"Tactic ids and tactic instances arrays should have the same length",
				tacticIds.length, tacs.length);
		int i = 0;
		for (String id : tacticIds) {
			assertTacticRegistered(id, tacs[i]);
			i++;
		}
	}

	/**
	 * Generates a proof tree from the given predicates, where the last
	 * predicate is the goal.
	 * 
	 * @param preds
	 *            the predicates contained in the proof tree to create
	 * @return a proof tree made out of the given predicates
	 */
	public static IProofTree genProofTree(String... preds) {
		final IProverSequent seq = genSeq(preds);
		return ProverFactory.makeProofTree(seq, null);
	}

	/**
	 * Generates a proof tree from the given predicates, where the last
	 * predicate is the goal.
	 * 
	 * @param preds
	 *            selected hypotheses and then goal
	 * @return a proof tree made out of the given predicates
	 */
	public static IProofTree genProofTree(Predicate... preds) {
		final IProverSequent seq = TestLib.genSeq(preds);
		return ProverFactory.makeProofTree(seq, null);
	}

	private static IProverSequent genSeq(String... preds) {
		final int nbHyps = preds.length - 1;
		final StringBuilder b = new StringBuilder();
		String sep = "";
		for (int i = 0; i < nbHyps; i++) {
			b.append(sep);
			sep = ";;";
			b.append(preds[i]);
		}
		b.append("|-");
		b.append(preds[nbHyps]);
		return TestLib.genSeq(b.toString());
	}

}
