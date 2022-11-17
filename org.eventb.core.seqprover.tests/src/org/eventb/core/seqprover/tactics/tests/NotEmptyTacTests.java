/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial implementation
 *******************************************************************************/
package org.eventb.core.seqprover.tactics.tests;

import static org.eventb.core.seqprover.tactics.tests.TreeShape.empty;
import static org.eventb.core.seqprover.tactics.tests.TreeShape.rn;
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.seqprover.eventbExtensions.AutoTactics;
import org.junit.Test;

/**
 * Tests for the NotEmpty auto tactic.
 *
 * @author Guillaume Verdier
 */
public class NotEmptyTacTests extends AbstractTacticTests {

	public NotEmptyTacTests() {
		super(new AutoTactics.NotEmptyAutoTac(), "org.eventb.core.seqprover.notEmptyTac");
	}

	/**
	 * Ensures that the tactic applies to '¬ ... = ∅"
	 */
	@Test
	public void applyEmptyRight() {
		assertSuccess(" ;H; ;S; ¬S=(∅⦂ℙ(ℤ)) |- ⊥", rn(genPred("¬S=(∅⦂ℙ(ℤ))"), "", empty));
	}

	/**
	 * Ensures that the tactic applies to '¬ ∅ = ...'
	 */
	@Test
	public void applyEmptyLeft() {
		assertSuccess(" ;H; ;S; ¬(∅⦂ℙ(ℤ))=S |- ⊥", rn(genPred("¬(∅⦂ℙ(ℤ))=S"), "", empty));
	}

	/**
	 * Ensures that the tactic fails on other hypotheses, such as:
	 * <ul>
	 * <li>a hypothesis without the negation</li>
	 * <li>a not equal hypothesis that does not have an empty set on either side of
	 * the equality</li>
	 * <li>a hypothesis on which the underlying rewriter could be applied, but
	 * which is outside the scope of this auto tactic</li>
	 * </ul>
	 */
	@Test
	public void failure() {
		assertFailure(" ;H; ;S; S=(∅⦂ℙ(ℤ)) |- ⊥");
		assertFailure(" ;H; ;S; ¬S={1} |- ⊥");
		assertFailure(" ;H; ;S; ¬¬1=1 |- ⊥");
	}

}
