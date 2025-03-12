/*******************************************************************************
 * Copyright (c) 2007, 2025 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.core.seqprover.eventbExtensions.CardComparison;
import org.junit.Test;

/**
 * Unit tests for the Cardinal Comparison Rewrites reasoner
 * {@link CardComparison}
 * 
 * @author htson
 */
public class CardComparisonTests extends AbstractEmptyInputReasonerTests {

	@Test
	public void testPositions() {
		assertGetPositions("(x = 2) ⇒ card(1‥x) = card(x‥3)");
		assertGetPositions("∀x· x = 2 ⇒ card(1‥x) = card(x‥3)");
		assertGetPositions("card(1‥x) = card(x‥3)", "ROOT");
		assertGetPositions("(x = 2) ⇒ card(1‥x) ≤ card(x‥3)");
		assertGetPositions("∀x· x = 2 ⇒ card(1‥x) ≤ card(x‥3)");
		assertGetPositions("card(1‥x) ≤ card(x‥3)", "ROOT");
		assertGetPositions("(x = 2) ⇒ card(1‥x) < card(x‥3)");
		assertGetPositions("∀x· x = 2 ⇒ card(1‥x) < card(x‥3)");
		assertGetPositions("card(1‥x) < card(x‥3)", "ROOT");
		assertGetPositions("(x = 2) ⇒ card(1‥x) ≥ card(x‥3)");
		assertGetPositions("∀x· x = 2 ⇒ card(1‥x) ≥ card(x‥3)");
		assertGetPositions("card(1‥x) ≥ card(x‥3)", "ROOT");
		assertGetPositions("(x = 2) ⇒ card(1‥x) > card(x‥3)");
		assertGetPositions("∀x· x = 2 ⇒ card(1‥x) > card(x‥3)");
		assertGetPositions("card(1‥x) > card(x‥3)", "ROOT");
		assertGetPositions("¬ card(1‥x) > card(x‥3)");
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.cardComparisonGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.cardComparison";
	}

	@Test
	public void testSuccess() throws Exception {
		assertReasonerSuccess(" ⊤ |- card(1‥x) = card(x‥3)", "{x=ℤ}[][][⊤] |- 1 ‥ x=x ‥ 3");
		assertReasonerSuccess(" ⊤ |- card(1‥x) ≤ card(x‥3)", "{x=ℤ}[][][⊤] |- 1 ‥ x⊆x ‥ 3");
		assertReasonerSuccess(" ⊤ |- card(1‥x) < card(x‥3)", "{x=ℤ}[][][⊤] |- 1 ‥ x⊂x ‥ 3");
		assertReasonerSuccess(" ⊤ |- card(1‥x) ≥ card(x‥3)", "{x=ℤ}[][][⊤] |- x ‥ 3⊆1 ‥ x");
		assertReasonerSuccess(" ⊤ |- card(1‥x) > card(x‥3)", "{x=ℤ}[][][⊤] |- x ‥ 3⊂1 ‥ x");
	}

	@Test
	public void testFailure() throws Exception {
		// Only applicable to goal, not to hypothesis
		assertReasonerFailure("card(1‥x) = card(x‥3) |- ⊤ ", "Inference " + getReasonerID() + " is not applicable");
		// Not a valid goal (more invalid cases are tested in testPositions())
		assertReasonerFailure(" ⊤ |- ¬ card(1‥x) > card(x‥3)", "Inference " + getReasonerID() + " is not applicable");
	}

}
