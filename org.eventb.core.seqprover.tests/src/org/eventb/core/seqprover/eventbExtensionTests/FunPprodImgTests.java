/*******************************************************************************
 * Copyright (c) 2025 INP Toulouse and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     INP Toulouse - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.eventbExtensions.Tactics.funPprodImgGetPositions;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;

import java.util.List;

import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Predicate;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunPprodImg;
import org.junit.Test;

/**
 * Tests for the "parallel product fun. image" reasoner.
 *
 * @author Guillaume Verdier
 */
public class FunPprodImgTests extends AbstractManualRewriterTests {

	public FunPprodImgTests() {
		super(new FunPprodImg());
	}

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.funPprodImg";
	}

	@Override
	protected List<IPosition> getPositions(Predicate predicate) {
		return funPprodImgGetPositions(predicate);
	}

	@Test
	public void testPositions() throws Exception {
		var typeEnv = mTypeEnvironment("f=ℙ(ℤ×ℤ);g=ℙ(ℤ×ℤ)");
		// Cases where the reasoner is applicable
		assertGetPositions(typeEnv, "(f ∥ g)(x) = x", "0");
		assertGetPositions(typeEnv, "∀ x · (f ∥ g)(x) = x", "1.0");
		assertGetPositions(typeEnv, "(f ∥ g)(x) = (g ∥ f)(x)", "0", "1");
		// Cases where the reasoner is not applicable
		assertGetPositions(typeEnv, "f(0) = 0");
		assertGetPositions(typeEnv, "(f ∥ g) = (g ∥ f)");
	}

	@Test
	public void testReasoner() throws Exception {
		// This is just an integration test: most tests should go in testRewriter()
		assertReasonerSuccess("(f ∥ g)(0 ↦ 1) = 0 ↦ 1", "0", "f(0) ↦ g(1) = 0 ↦ 1");
		assertReasonerFailure("(f ∥ g)(0 ↦ 1) = 0 ↦ 1", "1");
	}

	@Test
	public void testRewriter() throws Exception {
		// Simple case
		rewritePred("(f ∥ g)(x) = x", "0", "f(prj1(x)) ↦ g(prj2(x)) = x", "f=ℙ(ℤ×ℤ);g=ℙ(ℤ×ℤ)");
		// Inside a quantification
		rewritePred("∀ x · (f ∥ g)(x) = x", "1.0", "∀ x · f(prj1(x)) ↦ g(prj2(x)) = x", "f=ℙ(ℤ×ℤ);g=ℙ(ℤ×ℤ)");
		// Several application positions
		rewritePred("(f ∥ g)(x) = (g ∥ f)(x)", "0", "f(prj1(x)) ↦ g(prj2(x)) = (g ∥ f)(x)", "f=ℙ(ℤ×ℤ);g=ℙ(ℤ×ℤ)");
		rewritePred("(f ∥ g)(x) = (g ∥ f)(x)", "1", "(f ∥ g)(x) = g(prj1(x)) ↦ f(prj2(x))", "f=ℙ(ℤ×ℤ);g=ℙ(ℤ×ℤ)");
		// Simplification when applied to a maplet
		rewritePred("(f ∥ g)(a ↦ b) = a ↦ b", "0", "f(a) ↦ g(b) = a ↦ b", "f=ℙ(ℤ×ℤ);g=ℙ(ℤ×ℤ)");
		// Wrong position
		noRewritePred("(f ∥ g)(x) = x", "1", "f=ℙ(ℤ×ℤ);g=ℙ(ℤ×ℤ)");
		// Function application without parallel product
		noRewritePred("f(0) = 0", "0");
		// Parallel product without function application
		noRewritePred("(f ∥ g) = (g ∥ f)", "0", "f=ℙ(ℤ×ℤ);g=ℙ(ℤ×ℤ)");
	}

}
