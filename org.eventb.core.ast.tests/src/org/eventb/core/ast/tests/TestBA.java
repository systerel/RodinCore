/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *     Systerel - mathematical language v2
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.junit.Test;

public class TestBA extends AbstractTests {

	private ITypeEnvironment defaultTEnv = mTypeEnvironment(
			"x=ℤ; y=ℤ; A=ℙ(ℤ); B=ℙ(ℤ); f=ℤ↔ℤ; Y=ℙ(BOOL)", ff);
	
	private class TestItem {
		String input;
		String expected;
		ITypeEnvironment tenv;
		TestItem(String input, String expected, ITypeEnvironment tenv) {
			this.input = input;
			this.expected = expected;
			this.tenv = tenv;
		}
		
		public void doTest() throws Exception {
			Assignment inA = parseAssignment(input);
			ITypeEnvironment newEnv = typeCheck(inA, tenv);
			
			Predicate inBA = inA.getBAPredicate(ff);
			assertTrue(input + "\n" + inBA.toString() + "\n"
					+ inBA.getSyntaxTree() + "\n",
					inBA.isTypeChecked());
			
			Predicate exP = parsePredicate(expected).flatten();
			typeCheck(exP, newEnv);
			assertEquals(input, exP, inBA);
		}
	}

	private TestItem[] testItems = new TestItem[] {
			new TestItem("x≔x+y", "x'=x+y", defaultTEnv),
			new TestItem("x:∈A", "x'∈A", defaultTEnv),
			new TestItem("x:\u2223 x'∈A", "x'∈A", defaultTEnv)
	};
	
	@Test 
	public void testBA() throws Exception {
		for (TestItem item : testItems)
			item.doTest();
	}

	
}
