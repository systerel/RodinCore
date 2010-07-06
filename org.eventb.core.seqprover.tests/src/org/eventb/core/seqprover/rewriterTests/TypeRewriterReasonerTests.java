package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.seqprover.SequentProver;

public class TypeRewriterReasonerTests extends AbstractAutomaticReasonerTests {

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		// No need to test this. This should be guaranteed by testing the
		// abstract automatic rewrite reasoner and the formula rewriter itself.
		return new SuccessfulTest [] {
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		// No need to test this. This should be guaranteed by testing the
		// abstract automatic rewrite reasoner and the formula rewriter itself.
		return new String [] {
		};	
	}

	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".typeRewrites";
	}

}
