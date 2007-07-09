package org.eventb.core.seqprover.rewriterTests;

import static org.junit.Assert.assertEquals;

import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.core.seqprover.tests.TestLib;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.Rewriter;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.TypePredRewriter;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class TypePredRewriterTests {

	private Rewriter r = new TypePredRewriter();
	// private ITypeEnvironment te = Lib.makeTypeEnvironment();
	
	@Test
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
