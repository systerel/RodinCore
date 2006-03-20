package org.eventb.core.prover.rewriterTests;

import org.eventb.core.prover.Lib;
import org.eventb.core.prover.externalReasoners.rewriter.Rewriter;
import org.eventb.core.prover.externalReasoners.rewriter.TypeExpRewrites;
import org.eventb.core.prover.tests.TestLib;

import junit.framework.TestCase;

public class TypeExpRewritesTest extends TestCase {

	private Rewriter r = new TypeExpRewrites();
	// private ITypeEnvironment te = Lib.makeTypeEnvironment();
	
	public void testApply(){
		assertEquals(
				r.apply(TestLib.genPredicate("ℤ≠ ∅")),
				Lib.True);
		assertEquals(
				r.apply(TestLib.genPredicate("∅≠ ℤ")),
				Lib.True);
		assertEquals(
				r.apply(TestLib.genPredicate("1+1 ∈ℤ")),
				Lib.True);
		assertEquals(
				r.apply(TestLib.genPredicate("1+1 ∉ℤ")),
				Lib.False);
		
	}
}
