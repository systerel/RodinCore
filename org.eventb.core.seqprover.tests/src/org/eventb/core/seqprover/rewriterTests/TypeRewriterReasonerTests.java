package org.eventb.core.seqprover.rewriterTests;

import org.eventb.core.seqprover.SequentProver;

public class TypeRewriterReasonerTests extends AbstractAutomaticReasonerTests {

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest [] {
				// Single rewrite in goal
				new SuccessfulTest("⊤ |- ℤ = ∅", "{}[][][⊤] |- ⊥"),
				// Single rewrite in hypothesis (do not hide the original hypothesis)
				new SuccessfulTest("ℤ = ∅|- ⊤", "{}[][][ℤ=∅, ⊥] |- ⊤")
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String [] {
				// Rewriting hypothesis to true is not done
				"x ∈ ℤ |- ⊤", 
				"⊤ |- x ∈ ℕ"
		};	
	}

	@Override
	public String getReasonerID() {
		return SequentProver.PLUGIN_ID + ".typeRewrites";
	}

}
