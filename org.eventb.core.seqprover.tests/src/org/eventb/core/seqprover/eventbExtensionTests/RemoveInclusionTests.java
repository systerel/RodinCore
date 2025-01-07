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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveInclusion;
import org.junit.Test;

/**
 * Unit tests for the ri reasoner {@link RemoveInclusion}
 * 
 * @author htson
 */
public class RemoveInclusionTests extends AbstractManualRewriterTests {
	
	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.ri";
	}

	@Test
	public void testPositions() {
		assertGetPositions("(0 = 1) ⇒ {1} ⊆ {1, 2}", "1");
		assertGetPositions("∀x·x = TRUE ⇒ {x} ⊆ {x, FALSE}", "1.1");
		assertGetPositions("(0 = 1) ⇒ ∅ ⊆ {1, 2}", "1");
		assertGetPositions("∀x·x = TRUE ⇒ ∅ ⊆ {x, FALSE}", "1.1");
		assertGetPositions("(0 = 1) ⇒ {1, 2} ⊆ {1, 2}", "1");
		assertGetPositions("∀x·x = TRUE ⇒ {x, FALSE} ⊆ {x, FALSE}", "1.1");
		assertGetPositions("(0 = 1) ⇒ {1 ↦ 3} ⊆ {1 ↦ 2, 2 ↦ 3}", "1");
		assertGetPositions("∀x·x = TRUE ⇒ {x ↦ 1} ⊆ {x ↦ 2, x ↦ 3}", "1.1");
		assertGetPositions("(0 = 1) ⇒ {(0 ↦ 2) ↦ (2 ↦ 3)} ⊆ {(1 ↦ 2) ↦ (2 ↦ 3)}", "1");
		assertGetPositions("∀x·x = TRUE ⇒ {FALSE ↦ (2 ↦ 2) ↦ x} ⊆ {x ↦ (2 ↦ 2) ↦ TRUE}", "1.1");
		assertGetPositions("(0 = 1) ⇒ {1 ↦ {2}} ⊆ {1 ↦ {2}, 2 ↦ {3}}", "1");
		assertGetPositions("∀x·x = TRUE ⇒ {{x} ↦ 1} ⊆ {{x} ↦ 2, {x} ↦ 3}", "1.1");
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.riGetPositions(predicate);
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess("(0 = 1) ⇒ {1} ⊆ {1, 2}", "1", "0=1⇒(∀x·x∈{1}⇒x∈{1,2})");
		assertReasonerSuccess("∀x·x = TRUE ⇒ {x} ⊆ {x, FALSE}", "1.1", "∀x·x=TRUE⇒(∀x0·x0∈{x}⇒x0∈{x,FALSE})");
		assertReasonerSuccess("(0 = 1) ⇒ ∅ ⊆ {1, 2}", "1", "0=1⇒⊤");
		assertReasonerSuccess("∀x·x = TRUE ⇒ ∅ ⊆ {x, FALSE}", "1.1", "∀x·x=TRUE⇒⊤");
		assertReasonerSuccess("(0 = 1) ⇒ {1, 2} ⊆ {1, 2}", "1", "0=1⇒⊤");
		assertReasonerSuccess("∀x·x = TRUE ⇒ {x, FALSE} ⊆ {x, FALSE}", "1.1", "∀x·x=TRUE⇒⊤");
		assertReasonerSuccess("(0 = 1) ⇒ {1 ↦ 3} ⊆ {1 ↦ 2, 2 ↦ 3}", "1",
				"0=1⇒(∀x,x0·x ↦ x0∈{1 ↦ 3}⇒x ↦ x0∈{1 ↦ 2,2 ↦ 3})");
		assertReasonerSuccess("∀x·x = TRUE ⇒ {x ↦ 1} ⊆ {x ↦ 2, x ↦ 3}", "1.1",
				"∀x·x=TRUE⇒(∀x0,x1·x0 ↦ x1∈{x ↦ 1}⇒x0 ↦ x1∈{x ↦ 2,x ↦ 3})");
		assertReasonerSuccess("(0 = 1) ⇒ {(0 ↦ 2) ↦ (2 ↦ 3)} ⊆ {(1 ↦ 2) ↦ (2 ↦ 3)}", "1",
				"0=1⇒(∀x,x0,x1,x2·x ↦ x0 ↦ (x1 ↦ x2)∈{0 ↦ 2 ↦ (2 ↦ 3)}⇒x ↦ x0 ↦ (x1 ↦ x2)∈{1 ↦ 2 ↦ (2 ↦ 3)})");
		assertReasonerSuccess("∀x·x = TRUE ⇒ {FALSE ↦ (2 ↦ 2) ↦ x} ⊆ {x ↦ (2 ↦ 2) ↦ TRUE}", "1.1",
				"∀x·x=TRUE⇒(∀x0,x1,x2,x3·x0 ↦ (x1 ↦ x2) ↦ x3∈{FALSE ↦ (2 ↦ 2) ↦ x}⇒x0 ↦ (x1 ↦ x2) ↦ x3∈{x ↦ (2 ↦ 2) ↦ TRUE})");
		assertReasonerSuccess("(0 = 1) ⇒ {1 ↦ {2}} ⊆ {1 ↦ {2}, 2 ↦ {3}}", "1",
				"0=1⇒(∀x,x0·x ↦ x0∈{1 ↦ {2}}⇒x ↦ x0∈{1 ↦ {2},2 ↦ {3}})");
		assertReasonerSuccess("∀x·x = TRUE ⇒ {{x} ↦ 1} ⊆ {{x} ↦ 2, {x} ↦ 3}", "1.1",
				"∀x·x=TRUE⇒(∀x0,x1·x0 ↦ x1∈{{x} ↦ 1}⇒x0 ↦ x1∈{{x} ↦ 2,{x} ↦ 3})");
	}

	@Test
	public void failure() {
		assertReasonerFailure("(0 = 1) ⇒ {1} ⊆ {1, 2}", "0");
		assertReasonerFailure("∀x·x = TRUE ⇒ {x} ⊆ {x, FALSE}", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ ∅ ⊆ {1, 2}", "0");
		assertReasonerFailure("∀x·x = TRUE ⇒ ∅ ⊆ {x, FALSE}", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ {1, 2} ⊆ {1, 2}", "0");
		assertReasonerFailure("∀x·x = TRUE ⇒ {x, FALSE} ⊆ {x, FALSE}", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ {1 ↦ 3} ⊆ {1 ↦ 2, 2 ↦ 3}", "0");
		assertReasonerFailure("∀x·x = TRUE ⇒ {x ↦ 1} ⊆ {x ↦ 2, x ↦ 3}", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ {(0 ↦ 2) ↦ (2 ↦ 3)} ⊆ {(1 ↦ 2) ↦ (2 ↦ 3)}", "0");
		assertReasonerFailure("∀x·x = TRUE ⇒ {FALSE ↦ (2 ↦ 2) ↦ x} ⊆ {x ↦ (2 ↦ 2) ↦ TRUE}", "1.0");
		assertReasonerFailure("(0 = 1) ⇒ {1 ↦ {2}} ⊆ {1 ↦ {2}, 2 ↦ {3}}", "0");
		assertReasonerFailure("∀x·x = TRUE ⇒ {{x} ↦ 1} ⊆ {{x} ↦ 2, {x} ↦ 3}", "1.0");
		assertReasonerFailure("∀x·x = TRUE ⇒ {{x} ↦ 1} ⊆ {{x} ↦ 2, {x} ↦ 3}", "3");
	}

	// Commented out, but makes the tests succeed
//	@Override
//	public ITactic getJustDischTactic() {
//		return B4freeCore.externalPP(false);
//	}

}
