package org.eventb.core.seqprover.rewriterTests;

import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractAutoRewrites;

/**
 * @author htson
 *         <p>
 *         This is the class for testing abstract formula rewriter reasoner
 *         {@link AbstractAutoRewrites}. The purpose is to test the abstract
 *         reasoner with a simple formula rewriter. The tests include:
 *         <ul>
 *         <li>If a formula is recursively rewritten.
 *         <li>If more than one formulas are rewritten.
 *         </ul>
 *         There is no attempt to test if the reasoner detect a loop in the
 *         rewriter itself. The reasoner itself does not check for loop either.
 */
public class HideOriginalRewriteReasonerTests extends AbstractAutomaticReasonerTests {

	@Override
	protected SuccessfulTest[] getSuccessfulTests() {
		return new SuccessfulTest [] {
				// Single simple rewrite in goal
				new SuccessfulTest("⊤ |- 1 = x", "{x=ℤ}[][][⊤] |- 2=x"),
				// Single simple rewrite in hypothesis (HIDE the original hypothesis)
				new SuccessfulTest("1 = x |- ⊤", "{x=ℤ}[1=x][][2=x] |- ⊤"),
				// Single recursive rewrite in goal
				new SuccessfulTest("⊤ |- 0 = x", "{x=ℤ}[][][⊤] |- 2=x"),
				// Single recursive rewrite in hypothesis (HIDE the original hypothesis)l
				new SuccessfulTest("0 = x |- ⊤", "{x=ℤ}[0=x][][2=x] |- ⊤"),
				// Multilple simple rewrite in goal
				new SuccessfulTest("1 = x ;; y = 1 |- ⊤", "{y=ℤ, x=ℤ}[1=x, y=1][][2=x, y=2] |- ⊤"),
				// Multilple recursive rewrite in hypothesis
				new SuccessfulTest("0 = x ;; y = 0 |- ⊤", "{y=ℤ, x=ℤ}[0=x, y=0][][2=x, y=2] |- ⊤"),
				// Multilple single/recursive rewrite in hypothesis
				new SuccessfulTest("1 = x ;; y = 0 |- ⊤", "{y=ℤ, x=ℤ}[1=x, y=0][][2=x, y=2] |- ⊤"),
				// Rewriting hypothesis to true is equivalent to hiding the hypothesis
				new SuccessfulTest("finite(ℕ) |- ⊤", "{}[finite(ℕ)][][] |- ⊤"),
				// TODO No rewriting in non-selected hypothesis
				
				// The most general case
				new SuccessfulTest("1 = x ;; y = 0 |- 2 = z", "{z=ℤ, y=ℤ, x=ℤ}[1=x, y=0][][2=x, y=2] |- 2=z"),
		};
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String [] {
				// TODO No rewriting in non-selected hypothesis
				
				// No rewriting in goal or hypothesis
				"4 = y ;; 5 = z |- 3 = x"
		};	
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.tests.hideOriginalRewrites";
	}

}
