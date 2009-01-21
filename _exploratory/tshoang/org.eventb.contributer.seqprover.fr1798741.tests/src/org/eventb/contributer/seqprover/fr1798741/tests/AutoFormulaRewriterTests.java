/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.contributer.seqprover.fr1798741.tests;

import org.eventb.contributer.seqprover.fr1798741.AutoRewriterImpl;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.seqprover.rewriterTests.AbstractFormulaRewriterTests;
import org.junit.Test;

public class AutoFormulaRewriterTests extends AbstractFormulaRewriterTests {

	private static final IFormulaRewriter rewriter = new AutoRewriterImpl();

	public AutoFormulaRewriterTests() {
		super(rewriter);
	}

	@Test
	public void test_overwriting() {
		expressionTest("(∅ ⦂ ℙ(T))", "f[(∅ ⦂ ℙ(S))]", "f", "ℙ(S×T)");
		expressionTest("(∅ ⦂ ℙ(T))", "(∅ ⦂ ℙ(S×T))[s]", "s", "ℙ(S)");
		expressionTest("(∅ ⦂ ℙ(T))", "(∅ ⦂ ℙ(S×T))[(∅ ⦂ ℙ(S))]");
	}

}
