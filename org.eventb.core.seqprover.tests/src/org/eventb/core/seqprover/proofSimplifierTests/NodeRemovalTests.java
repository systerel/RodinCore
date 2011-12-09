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

import static org.eventb.core.seqprover.tactics.tests.TreeShape.*;

import java.util.Arrays;
import java.util.List;

import org.eventb.core.seqprover.tactics.tests.TreeShape;
import org.junit.runners.Parameterized.Parameters;

/**
 * Simplify proof trees containing unneeded steps. No reordering is expected.
 * 
 * @author Nicolas Beauger
 */
public class NodeRemovalTests extends AbstractSimplificationTests {

	private static Object[] test(String sequent, TreeShape shape,
			TreeShape expected) {
		return new Object[] { sequent, shape, expected };
	}

	public NodeRemovalTests(String sequent, TreeShape initial,
			TreeShape expected) {
		super(sequent, initial, expected);
	}

	@Parameters
	public static List<Object[]> getTestCases() throws Exception {
		return Arrays.<Object[]> asList(
				
				//////////////////
				// 2 nodes test //
				//////////////////
				/**
				 * Proof tree:
				 * 0
				 * 1
				 * Dependencies:
				 * {}
				 * Expected:
				 * 1
				 */
				test("¬¬x=0|- ⊤",
						// initial
						rn(p("¬¬x=0"), "",
								trueGoal()),
						// expected		
						trueGoal())
				

				);
	}

}
