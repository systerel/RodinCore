/*******************************************************************************
 * Copyright (c) 2022 Université de Lorraine and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Université de Lorraine - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.bcompDefGetPositions;
import static org.eventb.core.seqprover.tests.TestLib.genPred;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AbstractManualRewrites;
import org.junit.Test;

/**
 * Unit tests for the "backward composition definition" rewriter.
 *
 * @author Guillaume Verdier
 */
public class BCompDefTests extends AbstractManualReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.bcompDefRewrites";
	}

	/**
	 * Applications of the rewriter on the goal that should succeed.
	 */
	@Test
	public void successInGoal() throws Exception {
		// Simple rewrite
		assertReasonerSuccess("|- ({1 ↦ 2} ∘ {0 ↦ 1})(0) = 2", input("0.0"), //
				"{}[][][] |- ({0 ↦ 1} ; {1 ↦ 2})(0) = 2");
		// Two possible rewrites
		assertReasonerSuccess("|- {1 ↦ 2} ∘ {0 ↦ 1} = {1 ↦ 2} ∘ {0 ↦ 1}", input("0"), //
				"{}[][][] |- {0 ↦ 1} ; {1 ↦ 2} = {1 ↦ 2} ∘ {0 ↦ 1}");
		assertReasonerSuccess("|- {1 ↦ 2} ∘ {0 ↦ 1} = {1 ↦ 2} ∘ {0 ↦ 1}", input("1"), //
				"{}[][][] |- {1 ↦ 2} ∘ {0 ↦ 1} = {0 ↦ 1} ; {1 ↦ 2}");
		// Rewrite in complex expression
		assertReasonerSuccess("|- ∀ f, g, h · f ∪ g ∪ h ⊆ ℤ × ℤ ∧ h ∘ g ∘ f = f ; g ; h", input("3.1.0"), //
				"{}[][][] |- ∀ f, g, h · f ∪ g ∪ h ⊆ ℤ × ℤ ∧ f ; g ; h = f ; g ; h");
	}

	/**
	 * Applications of the rewriter on a hypothesis that should succeed.
	 */
	@Test
	public void successInHypothesis() throws Exception {
		// Simple rewrite
		assertReasonerSuccess("({1 ↦ 2} ∘ {0 ↦ 1})(0) = 2 |- ⊥", input("({1 ↦ 2} ∘ {0 ↦ 1})(0) = 2", "0.0"), //
				"{}[({1 ↦ 2} ∘ {0 ↦ 1})(0) = 2][][({0 ↦ 1} ; {1 ↦ 2})(0) = 2] |- ⊥");
		// Two possible rewrites
		assertReasonerSuccess("{1 ↦ 2} ∘ {0 ↦ 1} = {1 ↦ 2} ∘ {0 ↦ 1} |- ⊥",
				input("{1 ↦ 2} ∘ {0 ↦ 1} = {1 ↦ 2} ∘ {0 ↦ 1}", "0"), //
				"{}[{1 ↦ 2} ∘ {0 ↦ 1} = {1 ↦ 2} ∘ {0 ↦ 1}][][{0 ↦ 1} ; {1 ↦ 2} = {1 ↦ 2} ∘ {0 ↦ 1}] |- ⊥");
		assertReasonerSuccess("{1 ↦ 2} ∘ {0 ↦ 1} = {1 ↦ 2} ∘ {0 ↦ 1} |- ⊥",
				input("{1 ↦ 2} ∘ {0 ↦ 1} = {1 ↦ 2} ∘ {0 ↦ 1}", "1"), //
				"{}[{1 ↦ 2} ∘ {0 ↦ 1} = {1 ↦ 2} ∘ {0 ↦ 1}][][{1 ↦ 2} ∘ {0 ↦ 1} = {0 ↦ 1} ; {1 ↦ 2}] |- ⊥");
		// Rewrite in complex expression
		assertReasonerSuccess("∀ f, g, h · f ∪ g ∪ h ⊆ ℤ × ℤ ∧ h ∘ g ∘ f = f ; g ; h |- ⊥",
				input("∀ f, g, h · f ∪ g ∪ h ⊆ ℤ × ℤ ∧ h ∘ g ∘ f = f ; g ; h", "3.1.0"), //
				"{}[∀ f, g, h · f ∪ g ∪ h ⊆ ℤ × ℤ ∧ h ∘ g ∘ f = f ; g ; h][]"
						+ "[∀ f, g, h · f ∪ g ∪ h ⊆ ℤ × ℤ ∧ f ; g ; h = f ; g ; h] |- ⊥");
	}

	/**
	 * Applications of the rewriter on a goal that should fail.
	 */
	@Test
	public void failureInGoal() throws Exception {
		// Wrong position
		assertReasonerFailure("|- ({1 ↦ 2} ∘ {0 ↦ 1})(0) = 2", input("0.1"), //
				"Rewriter " + getReasonerID() + " is inapplicable for goal ({1 ↦ 2}∘{0 ↦ 1})(0)=2 at position 0.1");
		// Not a bcomp
		assertReasonerFailure("|- ({0 ↦ 1} ; {1 ↦ 2})(0) = 2", input("0.0"), //
				"Rewriter " + getReasonerID() + " is inapplicable for goal ({0 ↦ 1};{1 ↦ 2})(0)=2 at position 0.0");
	}

	/**
	 * Applications of the rewriter on a hypothesis that should fail.
	 */
	@Test
	public void failureInHypothesis() throws Exception {
		// Wrong position
		assertReasonerFailure("({1 ↦ 2} ∘ {0 ↦ 1})(0) = 2 |- ⊥", input("({1 ↦ 2} ∘ {0 ↦ 1})(0) = 2", "0.1"), //
				"Rewriter " + getReasonerID()
						+ " is inapplicable for hypothesis ({1 ↦ 2}∘{0 ↦ 1})(0)=2 at position 0.1");
		// Not a bcomp
		assertReasonerFailure("({0 ↦ 1} ; {1 ↦ 2})(0) = 2 |- ⊥", input("({0 ↦ 1} ; {1 ↦ 2})(0) = 2", "0.0"), //
				"Rewriter " + getReasonerID()
						+ " is inapplicable for hypothesis ({0 ↦ 1};{1 ↦ 2})(0)=2 at position 0.0");
	}

	@Test
	public void testPositions() {
		// Acceptable cases
		assertGetPositions("({1 ↦ 2} ∘ {0 ↦ 1})(0) = 2", "0.0");
		assertGetPositions("{1 ↦ 2} ∘ {0 ↦ 1} = {1 ↦ 2} ∘ {0 ↦ 1}", "0", "1");
		assertGetPositions("∀ f, g, h · f ∪ g ∪ h ⊆ ℤ × ℤ ∧ h ∘ g ∘ f = f ; g ; h", "3.1.0");
		// Cases on which the reasoner can't be applied
		assertGetPositions("({0 ↦ 1} ; {1 ↦ 2})(0) = 2");
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return bcompDefGetPositions(predicate);
	}

	protected IReasonerInput input(String position) {
		return new AbstractManualRewrites.Input(null, makePosition(position));
	}

	protected IReasonerInput input(String hyp, String position) {
		return new AbstractManualRewrites.Input(genPred(hyp), makePosition(position));
	}

}