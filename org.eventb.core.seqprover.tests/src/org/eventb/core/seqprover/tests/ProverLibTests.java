/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tests;

import static org.eventb.core.seqprover.ProverFactory.makeProofRule;
import static org.eventb.core.seqprover.reasonerExtentionTests.AbstractReasonerTests.DT_FAC;
import static org.eventb.internal.core.seqprover.ReasonerRegistry.getReasonerRegistry;
import static org.junit.Assert.*;

import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerDesc;
import org.eventb.core.seqprover.ProverLib;
import org.eventb.core.seqprover.proofBuilderTests.SuccessReasoner;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.junit.Test;

/**
 * Tests about ProverLib functions.
 * 
 * @author Nicolas Beauger
 */
public class ProverLibTests {

	// A valid proof skeleton
	private static class ValidProofSkeleton implements IProofSkeleton {

		private final IProverSequent sequent;

		private ValidProofSkeleton(IProverSequent sequent) {
			this.sequent = sequent;
		}

		@Override
		public IProofRule getRule() {
			final IReasonerDesc reasoner = getReasonerRegistry()
					.getLiveReasonerDesc(SuccessReasoner.REASONER_ID);
			return makeProofRule(reasoner, new EmptyInput(), sequent.goal(),
					null, null, null);
		}

		@Override
		public String getComment() {
			return "empty node";
		}

		@Override
		public IProofSkeleton[] getChildNodes() {
			return new IProofSkeleton[0];
		}

	}

	// A proof skeleton where the rule is null
	private static class ProofSkeletonWithMissingRule extends
			ValidProofSkeleton {

		ProofSkeletonWithMissingRule(IProverSequent sequent) {
			super(sequent);
		}

		@Override
		public IProofRule getRule() {
			return null;
		}

	}

	/**
	 * Translates the given proof skeleton to the DT factory and ensure that the
	 * result is equal to the original skeleton.
	 * 
	 * @param skel
	 *            a proof skeleton
	 */
	private static void doTranslationTest(IProofSkeleton skel) {
		final IProofSkeleton translated = ProverLib.translate(skel, DT_FAC);
		assertTrue(ProverLib.deepEquals(skel, translated));
		final IProofRule trRule = translated.getRule();
		if (trRule != null) {
			assertSame(DT_FAC, trRule.getGoal().getFactory());
		}
	}

	/**
	 * Verifies that a closed skeleton node is correctly translated.
	 */
	@Test
	public void translateClosedSkeleton() throws Exception {
		final IProverSequent seq = TestLib.genSeq("|- ⊤", TestLib.ff);
		final IProofSkeleton skel = new ValidProofSkeleton(seq);
		doTranslationTest(skel);
	}

	/**
	 * Verifies that an open skeleton node is correctly translated.
	 */
	@Test
	public void translateOpenSkeleton() throws Exception {
		final IProofSkeleton skel = new ProofSkeletonWithMissingRule(null);
		doTranslationTest(skel);
	}
}
