/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypeRewriterImpl;
import org.junit.Test;

/**
 * @author htson
 *         <p>
 *         This is the class for testing type rewriter {@link TypeRewriterImpl}
 *         using the abstract formula rewriter tests
 *         {@link AbstractFormulaRewriterTests}.
 */
public class TypeRewriterTests extends AbstractFormulaRewriterTests {

	// The type rewriter for testing.
	private static final IFormulaRewriter rewriter = new TypeRewriterImpl();
	
	/**
	 * Constructor.
	 * <p>
	 * Create an abstract formula rewriter test with the input is the type rewriter.
	 */
	public TypeRewriterTests() {
		super(rewriter);
	}

	/**
	 * Testing the trivial type writes.
	 */
	@Test
	public void testTypeRewrites() {
		// Typ = {} == false (where Typ is a type expression)
		predicateTest("⊥", "ℤ = ∅");
		predicateTest("⊥", "ℙ(ℤ) = ∅");

		
		// {} = Typ == false (where Typ is a type expression)
		predicateTest("⊥", "∅ = ℤ");
		predicateTest("⊥", "∅ = ℙ(ℤ)");
		

		// E : Typ == true (where Typ is a type expression)
		predicateTest("⊤", "E ∈ ℤ");
		predicateTest("⊤", "E ∈ ℙ(ℤ)");

	}

}
