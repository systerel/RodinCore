/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofSimplifierTests;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.seqprover.tactics.tests.TreeShape;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test class for simplifications performing proof reorganization.
 * 
 * @author Nicolas Beauger
 * 
 */
public class ReorganizationTests extends AbstractSimplificationTests {

	public ReorganizationTests(String sequent, TreeShape initial,
			TreeShape expected) {
		super(sequent, initial, expected);
	}

	@Parameters
	public static List<Object[]> getTestCases() throws Exception {
		return Arrays.<Object[]> asList(
//				/*
//				 * Special case where a needed hypothesis is created redundantly.
//				 */
//				test("⊥ ;; ¬¬⊥ |- ⊥",
//						// initial
//						rn(p("¬¬⊥"), "",
//								falseHyp()),
//						// expected
//						falseHyp())
		);
	}

}
