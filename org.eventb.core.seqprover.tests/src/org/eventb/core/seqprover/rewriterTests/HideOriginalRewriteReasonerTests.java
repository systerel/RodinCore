/*******************************************************************************
 * Copyright (c) 2007, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
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
				new SuccessfulTest(" ⊤ ;H;   ;S; ⊤ |- 1 = x", "{x=ℤ}[][][⊤] |- 2=x"),
				// Single recursive rewrite in goal
				new SuccessfulTest(" ⊤ ;H;   ;S; ⊤ |- 0 = x", "{x=ℤ}[][][⊤] |- 2=x"),

				
				// Single simple rewrite in selected hypothesis (HIDE the original hypothesis)
				new SuccessfulTest(" 1 = x ;H;   ;S; 1 = x |- ⊤", "{x=ℤ}[1=x][][2=x] |- ⊤"),
				// Single recursive rewrite in selected hypothesis (HIDE the original hypothesis)l
				new SuccessfulTest(" 0 = x ;H;   ;S; 0 = x |- ⊤", "{x=ℤ}[0=x][][2=x] |- ⊤"),

				// Multilple simple rewrite in selected hypotheses (HIDE the original hypothesis)
				new SuccessfulTest(" 1 = x ;; y = 1 ;H;   ;S; 1 = x ;; y = 1 |- ⊤", "{y=ℤ, x=ℤ}[1=x;; y=1][][2=x;; y=2] |- ⊤"),
				// Multilple recursive rewrite in selected hypothesis (HIDE the original hypothesis)
				new SuccessfulTest(" 0 = x ;; y = 0 ;H;   ;S; 0 = x ;; y = 0 |- ⊤", "{y=ℤ, x=ℤ}[0=x;; y=0][][2=x;; y=2] |- ⊤"),
				// Multilple single/recursive rewrite in selected hypothesis
				new SuccessfulTest(" 1 = x ;; y = 0 ;H;   ;S; 1 = x ;; y = 0 |- ⊤", "{y=ℤ, x=ℤ}[1=x;; y=0][][2=x;; y=2] |- ⊤"),

				// Single simple rewrite in visible non-selected hypothesis (HIDE the original hypothesis)
				new SuccessfulTest(" 1 = x ;H;   ;S; |- ⊤", "{x=ℤ}[1=x][2=x][] |- ⊤"),
				// Single recursive rewrite in visible non-selected  hypothesis (HIDE the original hypothesis)l
				new SuccessfulTest(" 0 = x ;H;   ;S; |- ⊤", "{x=ℤ}[0=x][2=x][] |- ⊤"),

				// Multilple simple rewrite in visible non-selected  hypotheses (HIDE the original hypothesis)
				new SuccessfulTest(" 1 = x ;; y = 1 ;H;   ;S; |- ⊤", "{y=ℤ, x=ℤ}[1=x;; y=1][2=x;; y=2][] |- ⊤"),
				// Multilple recursive rewrite in visible non-selected  hypothesis (HIDE the original hypothesis)
				new SuccessfulTest(" 0 = x ;; y = 0 ;H;   ;S; |- ⊤", "{y=ℤ, x=ℤ}[0=x;; y=0][2=x;; y=2][] |- ⊤"),
				// Multilple single/recursive rewrite in visible non-selected  hypothesis
				new SuccessfulTest(" 1 = x ;; y = 0 ;H;   ;S; |- ⊤", "{y=ℤ, x=ℤ}[1=x;; y=0][2=x;; y=2][] |- ⊤"),

				// Rewriting hypothesis to true is equivalent to hiding the selected hypothesis
				new SuccessfulTest(" finite(ℕ) ;H;   ;S; finite(ℕ) |- ⊤", "{}[finite(ℕ)][][] |- ⊤"), 
				new SuccessfulTest(" finite(ℕ) ;H;   ;S; |- ⊤", "{}[finite(ℕ)][][] |- ⊤"), 
				
				// The most general case
				new SuccessfulTest(
						" 1 = x ;; y = 0 ;; finite(ℕ) ;H;  ;S; 1 = x ;; finite(ℕ) |- 2 = z",
						"{z=ℤ, y=ℤ, x=ℤ}[1=x;; y=0;; finite(ℕ)][y=2][2=x] |- 2=z"), };
	}

	@Override
	protected String[] getUnsuccessfulTests() {
		return new String [] {
				// No rewriting in non-visible hypothesis.
				" 1 = x ;H;   1 = x ;S; |- ⊤",
				" finite(ℕ) ;H;  finite(ℕ) ;S;  |- ⊤",
				// No rewriting in goal or hypothesis
				" 4 = y ;; 5 = z ;H;   ;S; 4 = y ;; 5 = z |- 3 = x"
		};	
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.tests.hideOriginalRewrites";
	}

}
