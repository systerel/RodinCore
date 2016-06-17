/*******************************************************************************
 * Copyright (c) 2010, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilderTests;

import static java.util.Arrays.asList;
import static org.eventb.core.seqprover.proofBuilderTests.Factory.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IVersionedReasoner;
import org.eventb.core.seqprover.SequentProver;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.core.seqprover.tactics.BasicTactics;

/**
 * Common implementation for representing the shape of a proof tree. Instances
 * can be used for checking the global shape of a proof tree, or building a
 * proof tree of a specific shape.
 * 
 * @author Laurent Voisin
 */
public abstract class ProofTreeShape {

	public static final ProofTreeShape open = new ProofTreeShape() {

		@Override
		public void check(IProofTreeNode node) {
			assertTrue(node.isOpen());
		}

		@Override
		public void create(IProofTreeNode node) {
			assertTrue(node.isOpen());
		}

	};

	public static final ProofTreeShape ctrHyp(final Predicate p) {
		return new ProofTreeShape() {

			@Override
			public void check(IProofTreeNode node) {
				assertReasonerID(node, SequentProver.PLUGIN_ID + ".contrHyps");
				assertGoal(node, null);
				assertNeededHyps(node, p, not(p));
			}

			@Override
			public void create(IProofTreeNode node) {
				assertTrue(node.isOpen());
				Tactics.contrHyps(not(p)).apply(node, null);
			}

		};
	}
	
	public static final ProofTreeShape hyp(final Predicate p) {
		return new ProofTreeShape() {

			@Override
			public void check(IProofTreeNode node) {
				assertReasonerID(node, SequentProver.PLUGIN_ID + ".hyp");
				assertGoal(node, p);
				assertNeededHyps(node, p);
			}

			@Override
			public void create(IProofTreeNode node) {
				assertEquals(p, node.getSequent().goal());
				Tactics.hyp().apply(node, null);
			}
		};
	}

	public static final ProofTreeShape splitGoal(
			final ProofTreeShape... childShapes) {
		return new ProofTreeShape() {

			@Override
			public void check(IProofTreeNode node) {
				assertReasonerID(node, SequentProver.PLUGIN_ID + ".conj");
				checkChildShapes(node, childShapes);
			}

			@Override
			public void create(IProofTreeNode node) {
				assertTrue(node.isOpen());
				Tactics.conjI().apply(node, null);
				createChildShapes(node, childShapes);
			}
		};
	}

	public static final ProofTreeShape splitImplication(
			final ProofTreeShape... childShapes) {
		return new ProofTreeShape() {

			@Override
			public void check(IProofTreeNode node) {
				assertReasonerID(node, SequentProver.PLUGIN_ID + ".impI");
				checkChildShapes(node, childShapes);
			}

			@Override
			public void create(IProofTreeNode node) {
				assertTrue(node.isOpen());
				Tactics.impI().apply(node, null);
				createChildShapes(node, childShapes);
			}
		};
	}

	public static final ProofTreeShape reasoner(final IReasoner r,
			final IReasonerInput input, final ProofTreeShape... childShapes) {
		return new ProofTreeShape() {

			@Override
			public void check(IProofTreeNode node) {
				assertReasonerID(node, r.getReasonerID());
				final IProofRule rule = node.getRule();
				assertEquals(input.getClass(), rule.generatedUsing().getClass());
				if (r instanceof IVersionedReasoner) {
					assertEquals(((IVersionedReasoner) r).getVersion(), rule
							.getReasonerDesc().getVersion());
				}
				checkChildShapes(node, childShapes);
			}

			@Override
			public void create(IProofTreeNode node) {
				assertTrue(node.isOpen());
				BasicTactics.reasonerTac(r, input).apply(node, null);
				createChildShapes(node, childShapes);
			}

		};
	}
	
	private static void createChildShapes(IProofTreeNode node,
			final ProofTreeShape... childShapes) {
		final IProofTreeNode[] childNodes = node.getChildNodes();
		assertNotNull(childNodes);
		assertEquals(childShapes.length, childNodes.length);
		for (int i = 0; i < childShapes.length; i++) {
			childShapes[i].create(childNodes[i]);
		}
	}

	private static void checkChildShapes(IProofTreeNode node, ProofTreeShape... childShapes) {
		final IProofTreeNode[] childNodes = node.getChildNodes();
		assertEquals(childShapes.length, childNodes.length);
		for (int i = 0; i < childShapes.length; i++) {
			childShapes[i].check(childNodes[i]);
		}
	}

	/**
	 * Ensures that the proof tree below the given proof tree node has the shape
	 * defined by this instance.
	 * 
	 * @param node
	 *            a proof tree node
	 */
	public abstract void check(IProofTreeNode node);

	/**
	 * Creates a proof tree of the shape defined by this instance. The node must
	 * be initially open.
	 * 
	 * @param node
	 *            an open proof tree node
	 */
	public abstract void create(IProofTreeNode node);

	protected void assertReasonerID(IProofTreeNode node, String expected) {
		assertEquals(expected, getRule(node).generatedBy().getReasonerID());
	}

	protected void assertGoal(IProofTreeNode node, Predicate expected) {
		assertEquals(expected, getRule(node).getGoal());
	}

	protected void assertNeededHyps(IProofTreeNode node, Predicate... expecteds) {
		final Set<Predicate> expected = new HashSet<Predicate>(
				asList(expecteds));
		assertEquals(expected, getRule(node).getNeededHyps());
	}

	private IProofRule getRule(IProofTreeNode node) {
		assertFalse(node.isOpen());
		return node.getRule();
	}

}
