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
package org.eventb.core.seqprover.rewriterTests;

import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FiniteDefRewriter;
import org.junit.Test;

public class FiniteDefTests extends AbstractFormulaRewriterTests {

	private static final FiniteDefRewriter rewriter = new FiniteDefRewriter(
			true, ff);

	public FiniteDefTests() {
		super(rewriter);
	}

	@Test
	public void testDEF_FINITE() {
		rewritePred("finite(A)", "∃n,f · f∈1‥n⤖A", "A", "ℙ(S)");
		rewritePred("finite(∅⦂ℙ(S))", "∃n,f · f∈1‥n⤖(∅⦂ℙ(S))");
		rewritePred("finite(A×B)", "∃n,f · f∈1‥n⤖A×B", "A", "ℙ(S)", "B", "ℙ(T)");
		rewritePred("¬A=∅ ∧ finite(A)", "¬A=∅ ∧ (∃n,f · f∈1‥n⤖A)", "A", "ℙ(S)");
		rewritePred("finite({x · x=1÷0 ∣ x})", "∃n,f · f∈1‥n⤖{x · x=1÷0 ∣ x}");
	}

}
