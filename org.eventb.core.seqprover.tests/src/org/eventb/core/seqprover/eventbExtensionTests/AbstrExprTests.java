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

import org.eventb.core.ast.datatype.IDatatype;
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

	private static final IDatatype RECORD1_DT; // Record with one destructor
	private static final IDatatype RECORD2_DT; // Record with two destructors
	private static final IDatatype RECORD3_DT; // Generic record with one destructor
	static {
		var ff = DEFAULT_FACTORY;
		var integerType = ff.makeIntegerType();
		var builder = ff.makeDatatypeBuilder("Record1");
		var cons = builder.addConstructor("mkRecord1");
		cons.addArgument("field", integerType);
		RECORD1_DT = builder.finalizeDatatype();
		builder = ff.makeDatatypeBuilder("Record2");
		cons = builder.addConstructor("mkRecord2");
		cons.addArgument("field1", integerType);
		cons.addArgument("field2", integerType);
		RECORD2_DT = builder.finalizeDatatype();
		var genericT = ff.makeGivenType("T");
		builder = ff.makeDatatypeBuilder("Record3", genericT);
		cons = builder.addConstructor("mkRecord3");
		cons.addArgument("field3", genericT);
		RECORD3_DT = builder.finalizeDatatype();
	}

	public AbstrExprTests() {
		super(DT_FAC.withExtensions(RECORD1_DT.getExtensions()) //
				.withExtensions(RECORD2_DT.getExtensions()) //
				.withExtensions(RECORD3_DT.getExtensions()));
	}

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
		assertReasonerFailure("⊤ |- ⊤", makeInput("x>2"), "Expect an expression or a predicate in the form pattern=expr");
		// Expression instead of identifier for name
		assertReasonerFailure("⊤ |- ⊤", makeInput("1=2"), "Expect an expression or a predicate in the form pattern=expr");
		// Pattern matching with incompatible mapsto
		assertReasonerFailure("⊤ |- ⊤", makeInput("a↦b=0"), "Type check failed for pattern a ↦ b and expression 0");
		// Pattern matching with duplicate names
		assertReasonerFailure("⊤ |- ⊤", makeInput("a↦a=x", "x=ℤ×ℤ"), "Identifier a appears twice in pattern");
		// Pattern matching with mapsto and arbitrary expression
		assertReasonerFailure("⊤ |- ⊤", makeInput("0↦a=0↦1"), "Patterns with mapsto must only contain free identifiers");
		// Pattern matching of datatypes requires a single constructor
		assertReasonerFailure("⊤ |- ⊤", makeInput("cons1(x)=sd", "sd=SD"),
				"Pattern constructor(...)=expr is only valid for datatypes with a single constructor");
		// Pattern matching of wrong datatype
		assertReasonerFailure("⊤ |- ⊤", makeInput("mkRecord1(x)=mkRecord2(0, 1)"),
				"Type check failed for pattern mkRecord1(x) and expression mkRecord2(0,1)");
		// Pattern matching of datatypes requires identifiers, not concrete values
		assertReasonerFailure("⊤ |- ⊤", makeInput("mkRecord1(0)=x", "x=Record1"),
				"In pattern, constructor parameters should be unique identifiers");
		// Pattern matching of datatypes with duplicate names
		assertReasonerFailure("⊤ |- ⊤", makeInput("mkRecord2(a, a)=x", "x=Record2"),
				"Identifier a appears twice in pattern");
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
		// Simple pattern matching with pair
		assertReasonerSuccess("|- x=0↦0", makeInput("a↦b=x", "x=ℤ×ℤ"), //
				"{x=ℤ×ℤ}[][][] |- ⊤", //
				"{x=ℤ×ℤ}[][][a↦b=x] |- x=0↦0");
		// Pattern matching with conflict
		assertReasonerSuccess("y=1 |- x=0↦0", makeInput("x↦y=x", "x=ℤ×ℤ"), //
				"{x=ℤ×ℤ}[][][y=1] |- ⊤", //
				"{x=ℤ×ℤ}[][][y=1 ;; x0↦y0=x] |- x=0↦0");
		// Pattern matching more complex expression
		assertReasonerSuccess("|- x=(0↦TRUE)↦((0↦FALSE)↦1)", makeInput("(a↦b)↦(c↦d)=x", "x=(ℤ×BOOL)×((ℤ×BOOL)×ℤ)"), //
				"{x=(ℤ×BOOL)×((ℤ×BOOL)×ℤ)}[][][] |- ⊤", //
				"{x=(ℤ×BOOL)×((ℤ×BOOL)×ℤ)}[][][(a↦b)↦(c↦d)=x] |- x=(0↦TRUE)↦((0↦FALSE)↦1)");
		// Pattern matching with a single constructor and one destructor
		assertReasonerSuccess("|- field(x)=0", makeInput("mkRecord1(f)=x", "x=Record1"), //
				"{x=Record1}[][][] |- ⊤", //
				"{}[][][mkRecord1(f)=x] |- field(x)=0");
		// Pattern matching with a single generic constructor and one destructor
		assertReasonerSuccess("|- field3(x)=0", makeInput("mkRecord3(f)=x", "x=Record3(ℤ)"), //
				"{x=Record3(ℤ)}[][][] |- ⊤", //
				"{x=Record3(ℤ)}[][][mkRecord3(f)=x] |- field3(x)=0");
		// Pattern matching with a single constructor and two destructors
		assertReasonerSuccess("|- field1(x)=field2(x)", makeInput("mkRecord2(f1, f2)=x", "x=Record2"), //
				"{x=Record2}[][][] |- ⊤", //
				"{}[][][mkRecord2(f1, f2)=x] |- field1(x)=field2(x)");
		// Pattern matching with conflicts
		assertReasonerSuccess("a=0 ;; b=1 ;; b0=2 |- field1(x)=field2(x)", makeInput("mkRecord2(a, b)=x", "x=Record2"), //
				"{x=Record2}[][][a=0 ;; b=1 ;; b0=2] |- ⊤", //
				"{}[][][a=0 ;; b=1 ;; b0=2 ;; mkRecord2(a0, b1)=x] |- field1(x)=field2(x)");
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
		return new AbstrExpr.Input(input, mTypeEnvironment(typeEnv, ff));
	}
	
}
