package org.eventb.core.seqprover.rewriterTests;

import junit.framework.TestCase;

import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.Rewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypePredRewriter;

public class TypePredRewriterTests extends TestCase {

	private Rewriter r = new TypePredRewriter();
	// private ITypeEnvironment te = Lib.makeTypeEnvironment();
	
	public void testApply(){
		assertEquals(
				r.apply(TestLib.genPred("ℤ≠ ∅")),
				Lib.True);
		assertEquals(
				r.apply(TestLib.genPred("∅≠ ℤ")),
				Lib.True);
		assertEquals(
				r.apply(TestLib.genPred("1+1 ∈ℤ")),
				Lib.True);
		assertEquals(
				r.apply(TestLib.genPred("1+1 ∉ℤ")),
				Lib.False);
		
	}
}
