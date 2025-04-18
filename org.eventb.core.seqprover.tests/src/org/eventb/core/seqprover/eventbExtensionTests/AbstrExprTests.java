/*******************************************************************************
 * Copyright (c) 2007, 2025 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - adapted to common test framework
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.ProverFactory.makeProofTree;
import static org.eventb.core.seqprover.ProverLib.deepEquals;
import static org.eventb.core.seqprover.eventbExtensions.Tactics.abstrExpr;
import static org.eventb.core.seqprover.proofBuilder.ProofBuilder.replay;
import static org.eventb.core.seqprover.tests.TestLib.genSeq;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstrExpr;
import org.junit.Test;

/**
 * Unit tests for the Abstract expression reasoner
 * 
 * @author Farhad Mehta
 */
public class AbstrExprTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return new AbstrExpr().getReasonerID();
	}

	@Test
	public void failure() throws Exception {
		// Expression not parsable
		assertReasonerFailure("⊤ |- ⊤", makeInput("@unparsable@"), "Failed parsing input @unparsable@");
		// Expression not typecheckable
		assertReasonerFailure("⊤ |- ⊤", makeInput("x"), "Failed type checking input: Variable has an unknown type");
		// Input is a predicate, but not an equality
		assertReasonerFailure("⊤ |- ⊤", makeInput("x>2"), "Expect an expression or a predicate in the form ident=expr");
		// Expression instead of identifier for name
		assertReasonerFailure("⊤ |- ⊤", makeInput("1=2"), "Expect an expression or a predicate in the form ident=expr");
	}

	@Test
	public void success() throws Exception {
		assertReasonerSuccess("x=1 ;; x+1 = 2 |- (x+1)+1 = 3", makeInput("x+1", "x=ℤ"),
				"{x=ℤ}[][][x=1;; x+1=2] |- ⊤", //
				"{ae=ℤ; x=ℤ}[][][x=1;; x+1=2;; ae=x+1] |- (x+1)+1=3");
		// WD added as hypothesis
		assertReasonerSuccess("|- a÷b = c", makeInput("a÷b", "a=ℤ;b=ℤ;c=ℤ"),
				"{a=ℤ; b=ℤ; c=ℤ}[][][] |- b ≠ 0", //
				"{}[][][b ≠ 0 ;; ae=a÷b] |- a÷b = c");
		// Fresh name provided
		assertReasonerSuccess("|- 1+1 = 2", makeInput("two=1+1"),
				"{}[][][] |- ⊤", //
				"{}[][][two = 1+1] |- 1+1 = 2");
		// Fresh name provided conflicts with type environment: fresh one generated
		assertReasonerSuccess("|- x+1 = 2", makeInput("x=1"), //
				"{x=ℤ}[][][] |- ⊤", //
				"{}[][][x0 = 1] |- x+1 = 2");
	}

	@Test
	public void replays() throws Exception {
		// Replay on same sequent
		assertReplay("|- ⊤", "x = 1", "|- ⊤", "x = 1 |- ⊤");
		// Replay with additional hypothesis, no conflict
		assertReplay("|- ⊤", "x = 1", "y = 0 |- ⊤", "y = 0 ;; x = 1 |- ⊤");
		// Replay with additional hypothesis and conflict: ae generates a fresh ident
		assertReplay("|- ⊤", "x = 1", "x = 0 |- ⊤", "x = 0 ;; x0 = 1 |- ⊤");
		// Replay with additional hypothesis and conflict (different types): ae generates a fresh ident
		assertReplay("|- ⊤", "x = 1", "x = TRUE |- ⊤", "x = TRUE ;; x0 = 1 |- ⊤");
	}

	/*
	 * Applies ae(input) on firstSequent, then replays it on secondSequent and
	 * checks that the result is equal to expectedReplayedSequent.
	 */
	private void assertReplay(String firstSequent, String input, String secondSequent, String expectedReplayedSequent) {
		IProofTreeNode proofTree = makeProofTree(genSeq(firstSequent), null).getRoot();
		assertNull(abstrExpr(input).apply(proofTree, null));
		IProofTreeNode proofTree2 = makeProofTree(genSeq(secondSequent), null).getRoot();
		assertTrue(replay(proofTree2, proofTree, null));
		assertTrue(deepEquals(genSeq(expectedReplayedSequent), proofTree2.getChildNodes()[1].getSequent()));
	}

	private IReasonerInput makeInput(String input) {
		return new AbstrExpr.Input(input, ff.makeTypeEnvironment());
	}

	private IReasonerInput makeInput(String input, String typeEnv) {
		return new AbstrExpr.Input(input, mTypeEnvironment(typeEnv));
	}
	
}
