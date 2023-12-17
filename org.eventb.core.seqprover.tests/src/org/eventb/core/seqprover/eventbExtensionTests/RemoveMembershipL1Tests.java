/*******************************************************************************
 * Copyright (c) 2010, 2023 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     UPEC - refactored to use new test methods
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembershipL1;
import org.junit.Test;

/**
 * Unit tests for the rm level L1 reasoner {@link RemoveMembershipL1}.
 * 
 * @author htson
 */
public class RemoveMembershipL1Tests extends RemoveMembershipTests {

	public RemoveMembershipL1Tests() {
		super(new RemoveMembershipL1());
	}

	@Test
	public void testSuccessful() throws Exception {
		// r : S <-> T == r <: S x T
		assertReasonerSuccess(" 0 = x ⇒ f ∈ ℕ ↔ BOOL", "1", "0 = x ⇒ f ⊆ ℕ × BOOL");
		assertReasonerSuccess("∀x·0 = x ⇒ f ∈ ℕ ↔ BOOL", "1.1", "∀x·0 = x ⇒ f ⊆ ℕ × BOOL");
	}

	@Test
	public void testUnsuccessful() {
		// r : S <-> T == r <: S x T
		assertReasonerFailure(" 0 = x ⇒ f ∈ ℕ ↔ BOOL", "0");
		assertReasonerFailure("∀x·0 = x ⇒ f ∈ ℕ ↔ BOOL", "0");
	}

}
