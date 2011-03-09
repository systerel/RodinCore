/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofBuilderTests;

import static org.eventb.core.seqprover.proofBuilderTests.Factory.P;
import static org.eventb.core.seqprover.proofBuilderTests.Factory.Q;
import static org.eventb.core.seqprover.proofBuilderTests.Factory.R;
import static org.eventb.core.seqprover.proofBuilderTests.Factory.S;
import static org.eventb.core.seqprover.proofBuilderTests.Factory.land;
import static org.eventb.core.seqprover.proofBuilderTests.Factory.limp;
import static org.eventb.core.seqprover.proofBuilderTests.Factory.makeProofTreeNode;
import static org.eventb.core.seqprover.proofBuilderTests.ProofTreeShape.hyp;
import static org.eventb.core.seqprover.proofBuilderTests.ProofTreeShape.open;
import static org.eventb.core.seqprover.proofBuilderTests.ProofTreeShape.splitGoal;
import static org.eventb.core.seqprover.proofBuilderTests.ProofTreeShape.splitImplication;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.proofBuilder.ProofBuilder;
import org.junit.Test;

public class ProofRebuildTests {

	private static void assertRebuild(IProofTreeNode node,
			IProofSkeleton skeleton, IProofMonitor proofMonitor) {
		assertTrue(ProofBuilder.rebuild(node, skeleton, proofMonitor));
	}

	/**
	 * Ensures that the rebuild method still works when a conjunction is
	 * simplified.
	 * 
	 * @throws Exception
	 */
	@Test
	public void rebuildTestConjunction() throws Exception {
		IProofTreeNode node = makeProofTreeNode(P, Q, R, land(P, Q, P, R));
		IProofTreeNode proofSkeleton = makeProofTreeNode(P, Q, R,
				land(P, Q, P, R));
		splitGoal(hyp(P), hyp(Q), hyp(P), hyp(R)).create(proofSkeleton);
		IProofTreeNode nodeSimplified = makeProofTreeNode(P, Q, R,
				land(P, Q, R));
		assertRebuild(node, (IProofSkeleton) proofSkeleton, null);
		assertRebuild(nodeSimplified, (IProofSkeleton) proofSkeleton, null);
		splitGoal(hyp(P), hyp(Q), hyp(R)).check(nodeSimplified);
	}

	/**
	 * Ensures that the rebuild method still works when a conjunction is
	 * augmented.
	 */
	@Test
	public void rebuildTestConjunctionMore() throws Exception {
		IProofTreeNode node = makeProofTreeNode(P, Q, R, land(P, Q, R));
		IProofTreeNode proofSkeleton = makeProofTreeNode(P, Q, R,
				land(P, Q, R));
		splitGoal(hyp(P), hyp(Q), hyp(R)).create(proofSkeleton);
		IProofTreeNode nodeAugmented = makeProofTreeNode(P, Q, R,
				land(P, Q, P, R));
		assertRebuild(node, (IProofSkeleton) proofSkeleton, null);
		assertRebuild(nodeAugmented, (IProofSkeleton) proofSkeleton, null);
		splitGoal(hyp(P), hyp(Q), hyp(P), hyp(R)).check(nodeAugmented);
	}

	/**
	 * Tests a bug that occurred when conjunction is augmented by 2 or more with
	 * a new predicate (ArrayIndexOutOfBoundsException: 3 is thrown when bug is
	 * present).
	 */
	@Test
	public void rebuildTestConjunctionMoreAndNew() throws Exception {
		IProofTreeNode node = makeProofTreeNode(P, Q, R, land(P, Q, R));
		IProofTreeNode proofSkeleton = makeProofTreeNode(P, Q, R, land(P, Q, R));
		splitGoal(hyp(P), hyp(Q), hyp(R)).create(proofSkeleton);
		IProofTreeNode nodeAugmented = makeProofTreeNode(P, Q, R,
				land(P, Q, R, P, S));
		assertRebuild(node, (IProofSkeleton) proofSkeleton, null);
		assertFalse(ProofBuilder.rebuild(nodeAugmented,
				((IProofSkeleton) proofSkeleton), null));
		splitGoal(hyp(P), hyp(Q), hyp(R), hyp(P), open).check(nodeAugmented);
	}

	/**
	 * Ensures that the rebuild method still works when the terms of a
	 * conjunction are shuffled.
	 * 
	 * @throws Exception
	 */
	@Test
	public void rebuildTestShuffledConjunction() throws Exception {
		IProofTreeNode node = makeProofTreeNode(P, Q, R, land(P, Q, P, R));
		IProofTreeNode proofSkeleton = makeProofTreeNode(P, Q, R,
				land(P, Q, P, R));
		splitGoal(hyp(P), hyp(Q), hyp(P), hyp(R)).create(proofSkeleton);
		IProofTreeNode nodeShuffled = makeProofTreeNode(P, Q, R, land(R, P, Q));
		assertRebuild(node, (IProofSkeleton) proofSkeleton, null);
		assertRebuild(nodeShuffled, (IProofSkeleton) proofSkeleton, null);
		splitGoal(hyp(R), hyp(P), hyp(Q)).check(nodeShuffled);
	}

	/**
	 * Ensures that the rebuild method works with an implication.
	 * 
	 * @throws Exception
	 */
	@Test
	public void rebuildTestImplication() throws Exception {
		IProofTreeNode node = makeProofTreeNode(P, Q, limp(P, Q));
		IProofTreeNode proof = makeProofTreeNode(P, Q, limp(P, Q));
		splitImplication(hyp(Q)).create(proof);
		assertRebuild(node, (IProofSkeleton) proof, null);
		splitImplication(hyp(Q)).check(node);
	}

	/**
	 * Ensures that the rebuild method still works when a subsumed implication
	 * is simplified.
	 * 
	 * @throws Exception
	 */
	@Test
	public void rebuildTestSimplificationImplication() throws Exception {
		IProofTreeNode node = makeProofTreeNode(P, Q,
				land(limp(P, Q), limp(P, Q)));
		IProofTreeNode proof = makeProofTreeNode(P, Q,
				land(limp(P, Q), limp(P, Q)));
		splitGoal(splitImplication(hyp(Q)), splitImplication(hyp(Q))).create(
				proof);
		assertRebuild(node, (IProofSkeleton) proof, null);
		IProofTreeNode nodeSimplified = makeProofTreeNode(P, Q, limp(P, Q));
		assertRebuild(nodeSimplified, (IProofSkeleton) proof, null);
		splitImplication(hyp(Q)).check(nodeSimplified);
	}

	/**
	 * Ensures that the rebuild method still works when a conjunction, with
	 * terms that aren't trivial cases, is simplified.
	 * 
	 * @throws Exception
	 */
	@Test
	public void rebuildTestImplications() throws Exception {
		IProofTreeNode node = makeProofTreeNode(P, Q, R, S,
				land(limp(P, Q), limp(P, Q), limp(R, S)));
		IProofTreeNode proof = makeProofTreeNode(P, Q, R, S,
				land(limp(P, Q), limp(P, Q), limp(R, S)));
		splitGoal(splitImplication(hyp(Q)), splitImplication(hyp(Q)),
				splitImplication(hyp(S))).create(proof);
		assertRebuild(node, (IProofSkeleton) proof, null);
		IProofTreeNode nodeSimplified = makeProofTreeNode(P, Q, R, S,
				land(limp(P, Q), limp(R, S)));
		assertRebuild(nodeSimplified, (IProofSkeleton) proof, null);
		splitGoal(splitImplication(hyp(Q)), splitImplication(hyp(S))).check(
				nodeSimplified);
	}

	/**
	 * Ensures that the rebuild method still works when only half of an
	 * implication is simplified.
	 * 
	 * @throws Exception
	 */
	@Test
	public void rebuildDeepSimplification() throws Exception {
		IProofTreeNode node = makeProofTreeNode(P, Q, R, S,
				limp(P, land(Q, limp(R, land(S, Q)))));
		IProofTreeNode proof = makeProofTreeNode(P, Q, R, S,
				limp(P, land(Q, limp(R, land(S, Q)))));
		splitImplication(
				splitGoal(hyp(Q), splitImplication(splitGoal(hyp(S), hyp(Q)))))
				.create(proof);
		assertRebuild(node, (IProofSkeleton) proof, null);
		IProofTreeNode nodeSimplified = makeProofTreeNode(P, Q, R, S,
				limp(P, land(Q, limp(R, S))));
		assertRebuild(nodeSimplified, (IProofSkeleton) proof, null);
		splitImplication(splitGoal(hyp(Q), splitImplication(hyp(S)))).check(
				nodeSimplified);
	}

	/**
	 * Ensures that the rebuild method still works when there are multiple
	 * simplifications.
	 * 
	 * @throws Exception
	 */
	@Test
	public void rebuildDoubleSimplification() throws Exception {
		IProofTreeNode node = makeProofTreeNode(P, Q, R,
				land(limp(land(R, P), Q), limp(P, land(Q, Q))));
		IProofTreeNode proof = makeProofTreeNode(P, Q, R,
				land(limp(land(R, P), Q), limp(P, land(Q, Q))));
		splitGoal(splitImplication(hyp(Q)),
				splitImplication(splitGoal(hyp(Q), hyp(Q)))).create(proof);
		assertRebuild(node, (IProofSkeleton) proof, null);
		IProofTreeNode nodeSimplified = makeProofTreeNode(P, Q, limp(P, Q));
		assertRebuild(nodeSimplified, (IProofSkeleton) proof, null);
		splitImplication(hyp(Q)).check(nodeSimplified);
	}

	/**
	 * Ensures that the rebuild method still works when there is an open node.
	 * 
	 * @throws Exception
	 */
	@Test
	public void rebuildTreeWithOpenNode() throws Exception {
		IProofTreeNode proof = makeProofTreeNode(P, land(P, P, P));
		splitGoal(hyp(P), hyp(P), hyp(P)).create(proof);
		IProofTreeNode[] ProofChildren = proof.getChildNodes();
		ProofChildren[2].pruneChildren();
		IProofTreeNode nodeSimplified = makeProofTreeNode(P, P);
		assertRebuild(nodeSimplified, (IProofSkeleton) proof, null);
		hyp(P).check(nodeSimplified);
	}

}