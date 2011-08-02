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

import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
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
	 * @param tactic
	 *            the tactic to apply
	 */
	public static void assertSuccess(IProofTreeNode node, TreeShape expected,
			ITactic tactic) {
		TreeShape.assertSuccess(node, expected, tactic);
	}

	/**
	 * Asserts that the application of the given tactic to a proof tree node
	 * containing the given sequent returns <code>null</code> and that the
	 * resulting tree shape is equal to the given <code>expected</code> tree
	 * shape.
	 * 
	 * @param sequent
	 *            a proof sequent
	 * @param expected
	 *            the expected tree shape
	 * @param tactic
	 *            the tactic to apply
	 */
	public static void assertSuccess(IProverSequent sequent,
			TreeShape expected, ITactic tactic) {
		final IProofTreeNode node = makeProofTree(sequent, null).getRoot();
		assertSuccess(node, expected, tactic);
	}

	/**
	 * Asserts that the application of the given tactic fails and does not
	 * modify the proof tree.
	 * 
	 * @param node
	 *            the considered proof tree node
	 * @param tactic
	 *            the tactic to apply
	 */
	public static void assertFailure(IProofTreeNode node, ITactic tactic) {
		TreeShape.assertFailure(node, tactic);
	}

	/**
	 * Asserts that the application of the given tactic to a proof tree node
	 * containing the given sequent fails and does not modify the proof tree.
	 * 
	 * @param sequent
	 *            a proof sequent
	 * @param tactic
	 *            the tactic to apply
	 */
	public static void assertFailure(IProverSequent sequent, ITactic tactic) {
		final IProofTreeNode node = makeProofTree(sequent, null).getRoot();
		assertFailure(node, tactic);
	}

	/**
	 * Asserts that the tactic registry contains a descriptor corresponding to
	 * the given id, and that the instance of the tactic attached to this
	 * descriptor is equal to the tactic given as parameter.
	 * 
	 * @param id
	 *            the id of the tactic that should be registered
	 * @param tactic
	 *            the instance of the tactic to compare the runtime class with
	 */
	public static void assertTacticRegistered(String id, ITactic tactic) {
		final ITacticDescriptor desc = tacRegistry.getTacticDescriptor(id);
		assertNotNull(desc);
		assertEquals(tactic.getClass(), desc.getTacticInstance().getClass());
	}

	/**
	 * Generates a proof tree from the given predicates, where the last
	 * predicate is the goal.
	 * 
	 * @param predicateStrings
	 *            predicates as strings (selected hypotheses and goal)
	 * @return a proof tree made out of the given predicates
	 */
	public static IProofTree genProofTree(String... predicateStrings) {
		final IProverSequent sequent = genSeq(predicateStrings);
		return ProverFactory.makeProofTree(sequent, null);
	}

	/**
	 * Generates a proof tree from the given predicates, where the last
	 * predicate is the goal.
	 * 
	 * @param predicates
	 *            selected hypotheses and then goal
	 * @return a proof tree made out of the given predicates
	 */
	public static IProofTree genProofTree(Predicate... predicates) {
		final IProverSequent seq = TestLib.genSeq(predicates);
		return ProverFactory.makeProofTree(seq, null);
	}

	private static IProverSequent genSeq(String... predicates) {
		final int nbHyps = predicates.length - 1;
		final StringBuilder b = new StringBuilder();
		String sep = "";
		for (int i = 0; i < nbHyps; i++) {
			b.append(sep);
			sep = ";;";
			b.append(predicates[i]);
		}
		b.append("|-");
		b.append(predicates[nbHyps]);
		return TestLib.genSeq(b.toString());
	}

}
