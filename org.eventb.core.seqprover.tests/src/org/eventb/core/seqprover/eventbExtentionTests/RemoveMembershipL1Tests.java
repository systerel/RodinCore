/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     Systerel - added DEF_IN_NAT, DEF_IN_NAT1
 ******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;


import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembership.RMLevel;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.RemoveMembershipL1;

/**
 * Unit tests for the rm level L1 reasoner {@link RemoveMembershipL1}
 * 
 * @author htson
 */
public class RemoveMembershipL1Tests extends RemoveMembershipTests {

	private static final String REASONER_ID = "org.eventb.core.seqprover.rmL1";

	public RemoveMembershipL1Tests() {
		super(REASONER_ID, RMLevel.L1);
	}

	// E : NAT == 0 <= E
	private static final String P1 = "(0 = 1) ⇒ (1 ∈ ℕ)";

	private static final String resultP1 = "0=1⇒0≤1";

	private static final String P2 = "∀x·x = 0 ⇒ x ∈ ℕ";

	private static final String resultP2 = "∀x·x=0⇒0≤x";

	// E : NAT1 == 1 <= E
	private static final String P3 = "(0 = 1) ⇒ 2 ∈ ℕ1";

	private static final String resultP3 = "0=1⇒1≤2";

	private static final String P4 = "∀x·x = 0 ⇒ x ∈ ℕ1";

	private static final String resultP4 = "∀x·x=0⇒1≤x";

	public String[] getTestGetPositions() {
		return new String[] {
				P1, "1",
				P2, "1.1",
				P3, "1",
				P4, "1.1", };
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] { 
				new SuccessfulTest(P1, "1", resultP1),
				new SuccessfulTest(P2, "1.1", resultP2),
				new SuccessfulTest(P3, "1", resultP3),
				new SuccessfulTest(P4, "1.1", resultP4), };
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		final String[] testsL1 = new String[] {
				P1, "0",
				P2, "1.0",
				P3, "0",
				P4, "1.0",
			};
		return testsL1;
	}

}
