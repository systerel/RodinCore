package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.seqprover.SequentProver;

public class AutoRewriterReasonerTests extends AbstractAutomaticReasonerTests {

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest [] {
				// Single rewrite in goal
				new SuccessfulTest("⊤ |- 1 = 0", "{}[][][⊤] |- ⊥"),
				// Single rewrite in hypothesis (do not hide the original hypothesis)
				new SuccessfulTest("1 = 0|- ⊤", "{}[1=0][][⊥] |- ⊤"),
				// Rewriting hypothesis to true is not done
				new SuccessfulTest("1 = 1|- ⊤", "{}[1=1][][] |- ⊤")
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String [] {
				"⊤ |- x ∈ ℕ"
		};	
	}

	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".autoRewrites";
	}

}
