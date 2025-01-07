/*******************************************************************************
 * Copyright (c) 2011, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.contImpHypGetPositions;
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.ContImplHypRewrites;
import org.junit.Test;

/**
 * Unit tests for the Contraposition of Implication in Hypotheses reasoner
 * {@link ContImplHypRewrites}
 * 
 * @author Nicolas Beauger
 */
public class ContImplHypTests extends AbstractManualReasonerTests {

	@Override
	public String getReasonerID() {
		return new ContImplHypRewrites().getReasonerID();
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return contImpHypGetPositions(predicate);
	}

	@Test
	public void testPositions() {
		assertGetPositions("x = 1 ⇒ x = 2", "ROOT");
		assertGetPositions("∀x·x=0 ⇒ (x = 1 ⇒ x = 2)", "1", "1.1");
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess("x = 1 ⇒ x = 2 |- ⊤", makeInput("x = 1 ⇒ x = 2", ""),
				"{}[x = 1 ⇒ x = 2][][¬ x = 2 ⇒ ¬ x = 1] |- ⊤");
		assertReasonerSuccess("∀x·x=0 ⇒ (x = 1 ⇒ x = 2) |- ⊤", makeInput("∀x·x=0 ⇒ (x = 1 ⇒ x = 2)", "1"),
				"{}[∀x·x=0 ⇒ (x = 1 ⇒ x = 2)][][∀x· ¬(x = 1 ⇒ x = 2) ⇒ ¬x=0] |- ⊤");
		assertReasonerSuccess("∀x·x=0 ⇒ (x = 1 ⇒ x = 2) |- ⊤", makeInput("∀x·x=0 ⇒ (x = 1 ⇒ x = 2)", "1.1"),
				"{}[∀x·x=0 ⇒ (x = 1 ⇒ x = 2)][][∀x· x=0 ⇒ (¬ x = 2 ⇒ ¬ x = 1)] |- ⊤");
	}

	private static IReasonerInput makeInput(String hyp, String position) {
		final Predicate pHyp = genPred(hyp);
		final IPosition pPosition = makePosition(position);
		return new AbstractManualRewrites.Input(pHyp, pPosition);
	}

	@Test
	public void failure() throws Exception {
		assertReasonerFailure("x = 1 ⇒ x = 2 |- ⊤", makeInput("x = 1 ⇒ x = 2", "0"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis x=1⇒x=2 at position 0");
		assertReasonerFailure("∀x·x=0 ⇒ (x = 1 ⇒ x = 2) |- ⊤", makeInput("∀x·x=0 ⇒ (x = 1 ⇒ x = 2)", "1.0"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis ∀x·x=0⇒(x=1⇒x=2) at position 1.0");
		assertReasonerFailure("x = 1 ⇒ x = 2 |- ⊤", makeInput("x = 1 ⇒ x = 2", "3"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis x=1⇒x=2 at position 3");
	}

}
