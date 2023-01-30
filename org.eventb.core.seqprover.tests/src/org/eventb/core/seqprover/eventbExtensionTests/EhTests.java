/*******************************************************************************
 * Copyright (c) 2007, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added mathematical extension related tests
 *     Systerel - port to AbstractReasonerTests
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L0;
import static org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level.L1;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.UntranslatableException;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerExtensionTests.ExtendedOperators.AssocExt;
import org.eventb.core.seqprover.reasonerInputs.HypothesisReasoner;
import org.eventb.internal.core.seqprover.eventbExtensions.EqHe.Level;
import org.junit.Test;

/**
 * Unit tests for the Eh reasoner
 * 
 * @author htson
 */
public class EhTests extends AbstractReasonerTests {

	private static final FormulaFactory FF_WITH_ASSOC = FormulaFactory
			.getInstance(AssocExt.getInstance());

	private final Level level;

	protected EhTests(Level level) {
		super(FF_WITH_ASSOC);
		this.level = level;
	}

	public EhTests() {
		this(L0);
	}

	@Override
	public String getReasonerID() {
		if (level.from(L1)) {
			return "org.eventb.core.seqprover.eqL1";
		} else {
			return "org.eventb.core.seqprover.eq";
		}
	}

	@Test
	public void testSuccess() throws UntranslatableException {
		assertReasonerSuccess("0 = 1 ;; 0+1 = 2 |- 1+0+1 = 3", makeInput("0 = 1"),
				"{}[][0+1 = 2][0 = 1 ;; 1+1 = 2] |- 1+1+1 = 3");
		assertReasonerSuccess("0 + 1 = 1 ;; 0+1 = 2 |- 2+0+1 = 3", makeInput("0 + 1 = 1"),
				"{}[][0+1=2][0+1=1 ;; 1=2] |- 2+1=3");
		assertReasonerSuccess("0 + 1 + 2 = 2 + 1 ;; 0+1 = 0+1+2 |- 2+0+1 = 0+1+2+3 ", makeInput("0 + 1 + 2 = 2 + 1"),
				"{}[][0+1=0+1+2][0+1+2=2+1 ;; 0+1=2+1] |- 2+0+1 = 2+1+3");
		assertReasonerSuccess("1 = 2 ;; 1+1 = 2 |- 1+1+1 = 3", makeInput("1 = 2"),
				"{}[][1+1=2][1 = 2;; 2+2=2] |- 2+2+2=3");
		assertReasonerSuccess("1+2 = 12 |- 0+1+2+3 = 123", makeInput("1+2 = 12"), //
				"{}[][][1+2=12] |- 0+12+3=123");
		// bug 3389537
		assertReasonerSuccess("1●2 = 12 ;; 1●2 + 1●2 = 2 |- 1 + 1●2 + 1 = 1●2", makeInput("1●2 = 12"),
				"{}[][1●2 + 1●2 = 2][1●2 = 12;; 12+12=2] |- 1+12+1=12");
		// bug 3389537
		assertReasonerSuccess("1●2 = 12 ;; 0●1●2●3 = 123 |- 0 = 1 + 1●2●3 + 1", makeInput("1●2 = 12"),
				"{}[][0●1●2●3 = 123][1●2=12;; 0●12●3 = 123] |- 0 = 1 + 12●3 + 1");
		// FR #371
		if (level.from(L1)) {
			// Hypothesis is hidden when rewriting an identifier
			assertReasonerSuccess("x = y + 1 |- x + 1 = 1 + y + 1", makeInput("x = y + 1"),
					"{}[x = y + 1][][] |- y + 1 + 1 = 1 + y + 1");
		} else {
			assertReasonerSuccess("x = y + 1 |- x + 1 = 1 + y + 1", makeInput("x = y + 1"),
					"{}[][][x = y + 1] |- y + 1 + 1 = 1 + y + 1");
		}
		// Behavior is not modified when rewriting a complex expression
		assertReasonerSuccess("y + 1 = x |- x + 1 = 1 + y + 1", makeInput("y + 1 = x"),
				"{}[][][y + 1 = x] |- x + 1 = 1 + x");
	}

	@Test
	public void testFailure() throws UntranslatableException {
		// eqHyp not present
		assertReasonerFailure("⊤ |- ⊤", makeInput("1=2"), "Nonexistent hypothesis: 1=2");
		// eqHyp not an equality
		assertReasonerFailure("⊤ |- ⊥", makeInput("⊤"), "Unsupported hypothesis: ⊤");
		// nothing to do
		assertReasonerFailure("1=2 ;; ⊤ |- ⊤", makeInput("1=2"), "Nothing to rewrite");
		// nothing to do
		assertReasonerFailure("1=2 ;; 1=1 ;; 2=2 |- ⊤", makeInput("1=2"), "Nothing to rewrite");
	}

	private HypothesisReasoner.Input makeInput(String predImage) {
		final Predicate pred = genPred(predImage, ff);
		return new HypothesisReasoner.Input(pred);
	}

	public void assertReasonerSuccess(String sequentImage, IReasonerInput input, String... newSequents)
			throws UntranslatableException {
		assertReasonerSuccess(genSeq(sequentImage, ff), input, newSequents);
	}

	public void assertReasonerFailure(String sequentImage, IReasonerInput input, String reason)
			throws UntranslatableException {
		assertReasonerFailure(genSeq(sequentImage, ff), input, reason);
	}

}
