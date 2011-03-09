/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.rewriterTests;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;
import static org.junit.Assert.assertEquals;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.Rewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypePredRewriter;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class TypePredRewriterTests {

	private final static FormulaFactory ff = FormulaFactory.getDefault();
	private Rewriter r = new TypePredRewriter();
	// private ITypeEnvironment te = Lib.makeTypeEnvironment();
	
	@Test
	public void testApply(){
		final DLib lib = mDLib(ff);
		assertEquals(
				r.apply(TestLib.genPred("ℤ≠ ∅"), ff),
				lib.True());
		assertEquals(
				r.apply(TestLib.genPred("∅≠ ℤ"), ff),
				lib.True());
		assertEquals(
				r.apply(TestLib.genPred("1+1 ∈ℤ"), ff),
				lib.True());
		assertEquals(
				r.apply(TestLib.genPred("1+1 ∉ℤ"), ff),
				lib.False());
	}
}
