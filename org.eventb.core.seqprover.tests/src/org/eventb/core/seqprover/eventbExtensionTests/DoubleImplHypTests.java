/*******************************************************************************
 * Copyright (c) 2025 INP Toulouse and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     INP Toulouse - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.doubleImpHypGetPositions;
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.junit.Test;

/**
 * Tests for the double implication rewriter.
 *
 * @author Guillaume Verdier
 */
public class DoubleImplHypTests extends AbstractManualReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.doubleImplHypRewrites";
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return doubleImpHypGetPositions(predicate);
	}

	@Test
	public void testPositions() throws Exception {
		assertGetPositions("x = 1 ⇒ (y = 2 ⇒ z = 3)", "ROOT");
		assertGetPositions("(x = 1 ⇒ (y = 2 ⇒ z = 3)) ∧ x = 1", "0");
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess("x = 1 ⇒ (y = 2 ⇒ z = 3) |- ⊤", input("x = 1 ⇒ (y = 2 ⇒ z = 3)", ""),
				"{}[x = 1 ⇒ (y = 2 ⇒ z = 3)][][x = 1 ∧ y = 2 ⇒ z = 3] |- ⊤");
		assertReasonerSuccess("(x = 1 ⇒ (y = 2 ⇒ z = 3)) ∧ x = 1 |- ⊤", input("(x = 1 ⇒ (y = 2 ⇒ z = 3)) ∧ x = 1", "0"),
				"{}[(x = 1 ⇒ (y = 2 ⇒ z = 3)) ∧ x = 1][][(x = 1 ∧ y = 2 ⇒ z = 3) ;; x = 1] |- ⊤");
	}

	@Test
	public void failure() throws Exception {
		// Input predicate not in hypotheses
		assertReasonerFailure("|- ⊤", input("x = 1 ⇒ (y = 2 ⇒ z = 3)", ""), "Nonexistent hypothesis: x=1⇒(y=2⇒z=3)");
		// Position on element of wrong type
		assertReasonerFailure("x = 1 ⇒ (y = 2 ⇒ z = 3) |- ⊤", input("x = 1 ⇒ (y = 2 ⇒ z = 3)", "0"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis x=1⇒(y=2⇒z=3) at position 0");
		// Position out of range
		assertReasonerFailure("x = 1 ⇒ (y = 2 ⇒ z = 3) |- ⊤", input("x = 1 ⇒ (y = 2 ⇒ z = 3)", "3"),
				"Rewriter " + getReasonerID() + " is inapplicable for hypothesis x=1⇒(y=2⇒z=3) at position 3");
	}

	private static IReasonerInput input(String predicate, String position) {
		return new AbstractManualRewrites.Input(genPred(predicate), makePosition(position));
	}

}
