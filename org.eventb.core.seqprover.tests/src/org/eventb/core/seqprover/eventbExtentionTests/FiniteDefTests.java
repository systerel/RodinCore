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
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FiniteDefRewrites;
import org.junit.Test;

public class FiniteDefTests extends AbstractManualRewriterTests {

	private final String P1 = "finite({1})";
	private final String P1result = "∃n,f · f∈1‥n⤖{1}";

	private final String P2 = "finite({1}×{x · x=2 ∣ x})";
	private final String P2result = "∃n,f · f∈1‥n⤖{1}×{x · x=2 ∣ x}";

	private final String P3 = "{{2}} = {x⦂ℙ(ℤ) · finite(x) ∣ x}";
	private final String P3result = "{{2}} = {x⦂ℙ(ℤ) · ∃n,f · f∈1‥n⤖x ∣ x}";

	public FiniteDefTests() {
		super(new FiniteDefRewrites());
	}

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest[] { //
		new SuccessfulTest(P1, "", P1result), //
				new SuccessfulTest(P2, "", P2result), //
				new SuccessfulTest(P3, "1.1", P3result), //
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String[] { //
		P3, "1", //
		};
	}

	@Override
	protected String[] getTestGetPositions() {
		return new String[] { //
		P1, "ROOT", //
				P2, "ROOT", //
				P3, "1.1", //
		};
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return Tactics.finiteDefGetPositions(predicate);
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.finiteDefRewrites";
	}

	@Test
	public void testDEF_FINITE() {
		rewritePred("finite(A)", "", "∃n,f · f∈1‥n⤖A", "A=ℙ(S)");
		rewritePred("finite(∅⦂ℙ(S))", "", "∃n,f · f∈1‥n⤖(∅⦂ℙ(S))");
		rewritePred("finite(A×B)", "", "∃n,f · f∈1‥n⤖A×B", "A=ℙ(S),B=ℙ(T)");
		rewritePred("¬A=∅ ∧ finite(A)", "1", "¬A=∅ ∧ (∃n,f · f∈1‥n⤖A)",
				"A=ℙ(S)");
		rewritePred("finite({x · x=1÷0 ∣ x})", "",
				"∃n,f · f∈1‥n⤖{x · x=1÷0 ∣ x}");
		rewritePred("a = {x⦂ℙ(S) · finite(x) ∣ x}", "1.1",
				"a = {x⦂ℙ(S) · ∃n,f · f∈1‥n⤖x ∣ x}");
		rewritePred("finite({x⦂ℙ(S) · finite(x) ∣ x})", "",
				"∃n,f · f∈1‥n⤖{x⦂ℙ(S) · finite(x) ∣ x}");
		rewritePred("finite({x⦂ℙ(S) · finite(x) ∣ x})", "0.1",
				"finite({x⦂ℙ(S) · ∃n,f · f∈1‥n⤖x ∣ x})");
		noRewritePred("a=0", "");
		noRewritePred("¬A=∅ ∧ finite(A)", "", "A=ℙ(S)");
	}

}