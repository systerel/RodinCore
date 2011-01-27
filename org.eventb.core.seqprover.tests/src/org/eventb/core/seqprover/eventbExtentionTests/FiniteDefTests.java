/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtentionTests;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Tactics;

public class FiniteDefTests extends AbstractManualRewriterTests {

	private final String P1 = "finite({1})";
	private final String P1result = "∃n,f · f∈1‥n⤖{1}";

	private final String P2 = "finite({1}×{x · x=2 ∣ x})";
	private final String P2result = "∃n,f · f∈1‥n⤖{1}×{x · x=2 ∣ x}";

	private final String P3 = "{{2}} = {x⦂ℙ(ℤ) · finite(x) ∣ x}";
	private final String P3result = "{{2}} = {x⦂ℙ(ℤ) · ∃n,f · f∈1‥n⤖x ∣ x}";

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] { 
				new SuccessfulTest(P1, "", P1result),
				new SuccessfulTest(P2, "", P2result),
				new SuccessfulTest(P3, "1.1", P3result)
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] {};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] { P1, "ROOT", P2, "ROOT", P3, "1.1" };
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteDefGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteDefRewrites";
	}

}