/*******************************************************************************
 * Copyright (c) 2021 Université de Lorraine.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensionTests;

import static org.eventb.core.seqprover.tests.TestLib.genPred;

import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.internal.core.seqprover.eventbExtensions.NegEnum;
import org.junit.Test;

/**
 * Acceptance tests for reasoner NegEnum.
 *
 * @author Guillaume Verdier
 */
public class NegEnumTests extends AbstractReasonerTests {

	@Override
	public String getReasonerID() {
		return "org.eventb.core.seqprover.negEnum";
	}

	protected IReasonerInput input(String in1, String in2) {
		return new NegEnum.Input(new Predicate[] { genPred(in1), genPred(in2) });
	}

	@Test
	public void success() throws Exception {
		// Neg in left
		assertReasonerSuccess("x∈{1,2,3} ;; ¬x=2 |- ⊥", input("x∈{1,2,3}", "¬x=2"),
				"{}[][x∈{1,2,3};;¬x=2][x∈{1,3}] |- ⊥");
		// Neg in right
		assertReasonerSuccess("x∈{1,2,3} ;; ¬2=x |- ⊥", input("x∈{1,2,3}", "¬2=x"),
				"{}[][x∈{1,2,3};;¬2=x][x∈{1,3}] |- ⊥");
		// With an expression rather than a simple variable
		assertReasonerSuccess("x+y∈{1,2,3} ;; ¬x+y=2 |- ⊥", input("x+y∈{1,2,3}", "¬x+y=2"),
				"{}[][x+y∈{1,2,3};;¬x+y=2][x+y∈{1,3}] |- ⊥");
	}

	@Test
	public void failure() throws Exception {
		// Value missing from set
		assertReasonerFailure("x∈{1,3} ;; ¬x=2 |- ⊥", input("x∈{1,3}", "¬x=2"),
				"Negation enumeration is not applicable for hypotheses x∈{1,3} and ¬x=2");
		// Mismatched variable name
		assertReasonerFailure("x∈{1,3} ;; ¬y=2 |- ⊥", input("x∈{1,3}", "¬y=2"),
				"Negation enumeration is not applicable for hypotheses x∈{1,3} and ¬y=2");
		// First hypothesis missing
		assertReasonerFailure("¬x=2 |- ⊥", input("x∈{1,2,3}", "¬x=2"), "Input x∈{1,2,3} is not an hypothesis");
		// Second hypothesis missing
		assertReasonerFailure("x∈{1,2,3} |- ⊥", input("x∈{1,2,3}", "¬x=2"), "Input ¬x=2 is not an hypothesis");
	}

}
