/*******************************************************************************
 * Copyright (c) 2011, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.ast.FormulaFactory.makePosition;
import static org.eventb.core.seqprover.tests.TestLib.genPred;
import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;

import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.EmptyInput;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.LocalEqRewrite;
import org.junit.Test;

/**
 * Unit tests for the reasoner LocalEqRewrite.
 * 
 * @author Emmanuel Billaud
 */
public class LocalEqRewriteTests extends AbstractReasonerTests {

	private final ITypeEnvironmentBuilder typeEnv = mTypeEnvironment("A=ℙ(ℤ); B=ℙ(ℤ); C=ℙ(ℤ); D=ℙ(ℤ); x=ℙ(ℤ)");

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.locEq";
	}

	@Test
	public void success() throws Exception {
		// Apply in the hypothesis 1/5
		assertReasonerSuccess("A∈ℙ(ℤ) ;; x=A∪B ;; x∩C⊆D |- ⊤", input("x∩C⊆D", "0.0", "x=A∪B"),
				"{A=ℙ(ℤ); C=ℙ(ℤ)}[x∩C⊆D][][A∈ℙ(ℤ) ;; x=A∪B ;; (A∪B)∩C⊆D] |- ⊤");
		// Apply in the hypothesis 2/5
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A∪B=x ;; x∩C⊆D |- ⊤", input("x∩C⊆D", "0.0", "A∪B=x"),
				"{A=ℙ(ℤ); C=ℙ(ℤ)}[x∩C⊆D][][A∈ℙ(ℤ) ;; A∪B=x ;; (A∪B)∩C⊆D] |- ⊤");
		// Apply in the hypothesis 3/5
		assertReasonerSuccess("A∈ℙ(ℤ) ;; D∈ℙ(ℤ) ;; x=A∪B ;; y=D∪x ;; y∩C⊆D |- ⊤", input("y=D∪x", "1.1", "x=A∪B"),
				"{A=ℙ(ℤ); C=ℙ(ℤ); D=ℙ(ℤ)}[y=D∪x][][A∈ℙ(ℤ) ;; D∈ℙ(ℤ) ;; x=A∪B ;; y=D∪(A∪B) ;; y∩C⊆D] |- ⊤");
		// Apply in the hypothesis 4/5
		assertReasonerSuccess("A∈ℙ(ℤ) ;; x=A∪B ;; x=y |- ⊤", input("x=y", "0", "x=A∪B"),
				"{A=ℙ(ℤ); x=ℙ(ℤ)}[x=y][][A∈ℙ(ℤ) ;; x=A∪B ;; A∪B=y] |- ⊤");
		// Apply in the hypothesis 5/5
		assertReasonerSuccess("A∈ℙ(ℤ) ;; x=A∪B ;; y=x |- ⊤", input("y=x", "1", "x=A∪B"),
				"{A=ℙ(ℤ); x=ℙ(ℤ)}[y=x][][A∈ℙ(ℤ) ;; x=A∪B ;; y=A∪B] |- ⊤");
		// Apply in the goal 1/4
		assertReasonerSuccess("A∈ℙ(ℤ) ;; x=A∪B |- x∩C⊆D", input(null, "0.0", "x=A∪B"),
				"{A=ℙ(ℤ); C=ℙ(ℤ)}[][][A∈ℙ(ℤ) ;; x=A∪B] |- (A∪B)∩C⊆D");
		// Apply in the goal 2/4
		assertReasonerSuccess("A∈ℙ(ℤ) ;; A∪B=x |- x∩C⊆D", input(null, "0.0", "A∪B=x"),
				"{A=ℙ(ℤ); C=ℙ(ℤ)}[][][A∈ℙ(ℤ) ;; A∪B=x] |- (A∪B)∩C⊆D");
		// Apply in the goal 3/4
		assertReasonerSuccess("A∈ℙ(ℤ) ;; x=A∪B |- x=y", input(null, "0", "x=A∪B"),
				"{A=ℙ(ℤ)}[][][A∈ℙ(ℤ) ;; x=A∪B] |- A∪B=y");
		// Apply in the goal 4/4
		assertReasonerSuccess("A∈ℙ(ℤ) ;; x=A∪B |- y=x", input(null, "1", "x=A∪B"),
				"{A=ℙ(ℤ)}[][][A∈ℙ(ℤ) ;; x=A∪B] |- y=A∪B");
	}

	@Test
	public void failure() throws Exception {
		// Bad input
		assertReasonerFailure("⊤ |- ⊤", new EmptyInput(), "Wrong input's class.");
		// Should denote an equality
		assertReasonerFailure("A∈ℙ(ℤ) ;; x⊆A∪B ;; D⊆x∩C |- ⊤", input("D⊆x∩C", "0.0", "x⊆A∪B"),
				"x⊆A∪B does not denote an equality");
		// Should be a hypothesis of the sequent 1/2
		assertReasonerFailure("C∈ℙ(ℤ) ;; D⊆x∩C |- ⊤", input("D⊆x∩C", "0.0", "x=A∪B"),
				"x=A∪B is not a hypothesis of the given sequent");
		// Should be a hypothesis of the sequent 2/2
		assertReasonerFailure("A∈ℙ(ℤ) ;; x=A∪B |- ⊤", input("D⊆x∩C", "0.0", "x=A∪B"),
				"D⊆x∩C is not a hypothesis of the given sequent");
		// Equality predicate is not related to a free identifier
		assertReasonerFailure("A∈ℙ(ℤ) ;; A∩B=A∪B ;; (A∩B)∩C⊆D |- ⊤", input("(A∩B)∩C⊆D", "0.0", "A∩B=A∪B"),
				"The hypothesis cannot be re-written with the given input.");
	}

	private IReasonerInput input(String pred, String position, String equality) {
		return new LocalEqRewrite.Input(pred == null ? null : genPred(typeEnv, pred), makePosition(position),
				genPred(typeEnv, equality));
	}

}
