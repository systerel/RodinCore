/*******************************************************************************
 * Copyright (c) 2007, 2024 ETH Zurich and others.
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

import static org.eventb.core.seqprover.tests.TestLib.mTypeEnvironment;

import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.reasonerExtensionTests.AbstractReasonerTests;
import org.eventb.core.seqprover.reasonerInputs.SingleExprInput;
import org.eventb.internal.core.seqprover.eventbExtensions.AbstrExpr;
import org.junit.Test;

/**
 * Unit tests for the Abstract expression reasoner
 * 
 * TODO : test that WD lemmas are also added to the hyps
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
		assertReasonerFailure("⊤ |- ⊤", makeInput("@unparsable@"), "Parse error for expression: @unparsable@");
		// Expression not typecheckable
		assertReasonerFailure("⊤ |- ⊤", makeInput("x"), "Type check failed for expression: x");
	}

	@Test
	public void success() throws Exception {
		// hyp not univ quantified implication, but still univ quantified
		assertReasonerSuccess("x=1 ;; x+1 = 2 |- (x+1)+1 = 3", makeInput("x+1", mTypeEnvironment("x=ℤ")),
				"{x=ℤ}[][][x=1;; x+1=2] |- ⊤", //
				"{ae=ℤ; x=ℤ}[][][x=1;; x+1=2;; ae=x+1] |- (x+1)+1=3");
	}

	private IReasonerInput makeInput(String input) {
		return new SingleExprInput(input, ff.makeTypeEnvironment());
	}

	private IReasonerInput makeInput(String input, ITypeEnvironment typeEnv) {
		return new SingleExprInput(input, typeEnv);
	}
	
}
