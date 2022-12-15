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
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.eventbExtensions.Tactics.finiteUnionGetPositions;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteUnion;
import org.junit.Test;

/**
 * Unit tests for the "Finite of union" reasoner.
 *
 * @see FiniteUnion
 */
public class FiniteUnionTests extends AbstractEmptyInputReasonerTests {

	protected List<IPosition> getPositions(Predicate predicate) {
		return finiteUnionGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteUnion";
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess("⊤ |- finite(S ∪ {0 ↦ 3} ∪ T)",
				"{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- finite(S)∧finite({0 ↦ 3})∧finite(T)");
		assertReasonerSuccess("⊤ |- finite(union({S, {0 ↦ 3}, T}))",
				"{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- finite({S, {0 ↦ 3}, T}) ∧ (∀s· s ∈ {S, {0 ↦ 3}, T} ⇒ finite(s))");
		assertReasonerSuccess("⊤ |- finite(⋃s·s ∈ {S, {0 ↦ 3}, T} ∣ s)",
				"{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- finite({s·s ∈ {S, {0 ↦ 3}, T} ∣ s}) ∧ (∀s· s ∈ {S, {0 ↦ 3}, T} ⇒ finite(s))");
		assertReasonerSuccess("⊤ |- finite(⋃s ∣ s ∈ {S, {0 ↦ 3}, T})",
				"{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- finite({s ∣ s ∈ {S, {0 ↦ 3}, T}}) ∧ (∀s· s ∈ {S, {0 ↦ 3}, T} ⇒ finite(s))");
		assertReasonerSuccess("⊤ |- finite(⋃s·s ∈ {S, {0, 3}, T} ∧ s ⊆ s ∪ {1} ∣ { t ∣ t = s ∪ {2}})",
				"{S=ℙ(ℤ); T=ℙ(ℤ)}[][][⊤] |- finite({s·s ∈ {S, {0, 3}, T} ∧ s ⊆ s ∪ {1} ∣ { t ∣ t = s ∪ {2}}}) ∧ "
						+ "(∀s· s ∈ {S, {0, 3}, T} ∧ s ⊆ s ∪ {1} ⇒ finite({ t ∣ t = s ∪ {2}}))");
	}

	@Test
	public void failure() throws Exception {
		assertReasonerFailure("⊤ |- (x = 2) ⇒ finite(S ∪ {0 ↦ 3} ∪ T)",
				"Inference 'finite of union' is not applicable");
		assertReasonerFailure("⊤ |- ∀x· x = 2 ⇒ finite(S ∪ {0 ↦ 3} ∪ T)",
				"Inference 'finite of union' is not applicable");
	}

	@Test
	public void testPositions() {
		assertGetPositions("(x = 2) ⇒ finite(S ∪ {0 ↦ 3} ∪ T)");
		assertGetPositions("∀x· x = 2 ⇒ finite(S ∪ {0 ↦ 3} ∪ T)");
		assertGetPositions("finite(S ∪ {0 ↦ 3} ∪ T)", "ROOT");
		assertGetPositions("finite(union({S, {0 ↦ 3}, T}))", "ROOT");
		assertGetPositions("finite(⋃s·s ∈ {S, {0 ↦ 3}, T} ∣ s)", "ROOT");
	}

}
