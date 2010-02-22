/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added abstract test class
 *******************************************************************************/
package org.eventb.core.ast.tests;

import static org.eventb.core.ast.tests.FastFactory.mList;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;

/**
 * Unit test of the mathematical formula Type-Checker for expressions with an
 * expected type.
 * 
 * @author Laurent Voisin
 */
public class TestExprTypeChecker extends AbstractTests {
	
	private static class TestItem {
		public final String formula;
		public final String expectedType;
		public final ITypeEnvironment initialEnv;
		public final boolean result;
		public final ITypeEnvironment inferredEnv;
		
		TestItem(String formula,
				String expectedType,
				ITypeEnvironment initialEnv,
				ITypeEnvironment finalEnv) {

			this.formula = formula;
			this.expectedType = expectedType;
			this.initialEnv = initialEnv;
			this.result = finalEnv != null;
			this.inferredEnv = finalEnv;
		}
	}
	
	private static GivenType ty_S = ff.makeGivenType("S");
	private static GivenType ty_T = ff.makeGivenType("T");

	private TestItem[] testItems = new TestItem[] {
			new TestItem(
					"x",
					"S",
					mTypeEnvironment(),
					mTypeEnvironment(mList("x"), mList(ty_S))
			), new TestItem(
					"x",
					"S",
					mTypeEnvironment(mList("x"), mList(ty_S)),
					mTypeEnvironment()
			), new TestItem(
					"{}",
					"S",
					mTypeEnvironment(),
					null
			), new TestItem(
					"{}",
					"ℙ(S)",
					mTypeEnvironment(),
					mTypeEnvironment()
			), new TestItem(
					"{}",
					"ℙ(ℙ(S))",
					mTypeEnvironment(),
					mTypeEnvironment()
			), new TestItem(
					"{}",
					"ℙ(S × T)",
					mTypeEnvironment(),
					mTypeEnvironment()
			), new TestItem(
					"x ↦ y",
					"S",
					mTypeEnvironment(),
					null
			), new TestItem(
					"x ↦ y",
					"S × T",
					mTypeEnvironment(mList("x", "y"), mList(ty_S, ty_T)),
					mTypeEnvironment()
			), new TestItem(
					"x ↦ {}",
					"S × ℙ(T)",
					mTypeEnvironment(mList("x"), mList(ty_S)),
					mTypeEnvironment()
			),
	};
	
	/**
	 * Main test routine.
	 */
	public void testExpTypeChecker() {
		for (TestItem item: testItems) {
			Expression expr = parseExpression(item.formula);
			Type expectedType = parseType(item.expectedType);
			ITypeCheckResult result = 
				expr.typeCheck(item.initialEnv, expectedType);
			
			assertEquals("\nTest failed on: " + item.formula
					+ "\nExpected type: " + expectedType.toString()
					+ "\nParser result: " + expr.toString()
					+ "\nType check results:\n" + result.toString()
					+ "\nInitial type environment:\n"
					+ result.getInitialTypeEnvironment() + "\n",
					item.result, result.isSuccess());
			assertEquals("\nResult typenv differ for: " + item.formula + "\n",
						item.inferredEnv, result.getInferredEnvironment());
			
			if (result.isSuccess()) {
				assertTrue(expr.isTypeChecked());
				assertEquals(expectedType, expr.getType());
			}
		}
	}
}
