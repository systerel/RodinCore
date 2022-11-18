/*******************************************************************************
 * Copyright (c) 2007, 2022 ETH Zurich and others.
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
import org.eventb.internal.core.seqprover.eventbExtensions.FiniteInter;
import org.junit.Test;

/**
 * Unit tests for the Finite of intersection reasoner {@link FiniteInter}
 * 
 * @author htson
 */
public class FiniteInterTests extends AbstractEmptyInputReasonerTests {

	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteInterGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteInter";
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess("⊤ |- finite(S ∩ {0 ↦ 3} ∩ T)",
				"{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- finite(S)∨finite({0 ↦ 3})∨finite(T)");
		assertReasonerSuccess("⊤ |- finite(inter({S, {0 ↦ 3}, T}))",
				"{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- ∃s· s ∈ {S, {0 ↦ 3}, T} ∧ finite(s)");
		assertReasonerSuccess("⊤ |- finite(⋂s·s ∈ {S, {0 ↦ 3}, T} ∣ s)",
				"{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- ∃s· s ∈ {S, {0 ↦ 3}, T} ∧ finite(s)");
		assertReasonerSuccess("⊤ |- finite(⋂s ∣ s ∈ {S, {0 ↦ 3}, T})",
				"{S=ℤ↔ℤ; T=ℤ↔ℤ}[][][⊤] |- ∃s· s ∈ {S, {0 ↦ 3}, T} ∧ finite(s)");
		assertReasonerSuccess("⊤ |- finite(⋂s·s ∈ {S, {0, 3}, T} ∧ s ∩ {1} ⊆ s ∣ { t ∣ t = s ∩ {2}})",
				"{S=ℙ(ℤ); T=ℙ(ℤ)}[][][⊤] |- ∃s· (s ∈ {S, {0, 3}, T} ∧ s ∩ {1} ⊆ s) ∧ finite({ t ∣ t = s ∩ {2}})");
	}

	@Test
	public void failure() throws Exception {
		assertReasonerFailure("⊤ |- (x = 2) ⇒ finite(S ∩ {0 ↦ 3} ∩ T)",
				"Inference 'finite of intersection' is not applicable");
		assertReasonerFailure("⊤ |- ∀x· x = 2 ⇒ finite(S ∩ {0 ↦ 3} ∩ T)",
				"Inference 'finite of intersection' is not applicable");
	}

	@Test
	public void testPositions() {
		testGetPosition("(x = 2) ⇒ finite(S ∩ {0 ↦ 3} ∩ T)", "");
		testGetPosition("∀x· x = 2 ⇒ finite(S ∩ {0 ↦ 3} ∩ T)", "");
		testGetPosition("finite(S ∩ {0 ↦ 3} ∩ T)", "ROOT");
		testGetPosition("finite(inter({S, {0 ↦ 3}, T}))", "ROOT");
		testGetPosition("finite(⋂s·s ∈ {S, {0 ↦ 3}, T} ∣ s)", "ROOT");
	}

}
