/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.tactics.BasicTactics.reuseTac;
import static org.eventb.core.seqprover.tests.TestLib.genFullSeq;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.IHypAction.IRewriteHypAction;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTree;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Ensures that {@link IRewriteHypAction} which is involved by Generalize Modus
 * Ponens tactic is not erroneously reused.
 */
public class RewriteHypActionTacticTest extends ProofTreeTests {

	/**
	 * Ensures that the rewrite hypothesis action involved by Generalize Modus
	 * Ponens tactic is not applied when a reuse operation is performed.
	 */
	@Test
	public void testGenMPReuse() {
		// input sequent
		final String typeEnv = "S=ℙ(S);A=ℙ(S);B=ℙ(S)";
		final IProverSequent seq = genSeq(typeEnv, "x∈A ;; x∈A ⇒ x∈B", "x∈B");
		
		final IProofTree pt = makeProofTree(seq, null);
		final IProofTreeNode root = pt.getRoot();

		final AutoTactics.GenMPTac genMPTac = new AutoTactics.GenMPTac();
		genMPTac.apply(root, null);

		assertNodePending(root);

		final IProofSkeleton pSkel = root.copyProofSkeleton();
		final IProverSequent seq2 = genSeq(typeEnv, " x∈A ⇒ x∈B", "x∈B ");
		final IProofTreeNode root2 = makeProofTree(seq2, null).getRoot();

		// apply reuse
		final Object problem = reuseTac(pSkel).apply(root2, null);
		// check that apply was successful
		assertNull(problem);

		// check that there is only one open descendant and that it corresponds
		// to the seq2
		final IProofTreeNode[] childNodes = root2.getChildNodes();
		assertEquals(1, childNodes.length);
		final IProofTreeNode childNode = childNodes[0];
		final IProofTreeNode expectedChildNode = makeProofTree(seq2, null)
				.getRoot();
		assertTrue(ProverLib.deepEquals(expectedChildNode, childNode));
	}

	private static IProverSequent genSeq(String typeEnv, String selHyps,
			String goal) {
		return genFullSeq(typeEnv, "", "", selHyps, goal);
	}

}
