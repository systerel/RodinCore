/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
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

import static org.eventb.core.ast.tests.FastFactory.mInferredTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IInferredTypeEnvironment;
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

	private static GivenType ty_S = ff.makeGivenType("S");
	private static GivenType ty_T = ff.makeGivenType("T");

	/**
	 * Main test routine.
	 */
	public void testExpTypeChecker() {
		testExpression("x", "S",//
				mTypeEnvironment(),//
				mTypeEnvironment("x", ty_S));
		testExpression("x", "S",//
				mTypeEnvironment("x", ty_S),//
				mTypeEnvironment());
		testExpression("{}", "S",//
				mTypeEnvironment(),//
				null);
		testExpression("{}", "ℙ(S)",//
				mTypeEnvironment(),//
				mTypeEnvironment());
		testExpression("{}", "ℙ(ℙ(S))",//
				mTypeEnvironment(),//
				mTypeEnvironment());
		testExpression("{}", "ℙ(S × T)",//
				mTypeEnvironment(),//
				mTypeEnvironment());
		testExpression("x ↦ y", "S",//
				mTypeEnvironment(),//
				null);
		testExpression("x ↦ y", "S × T",//
				mTypeEnvironment("x", ty_S, "y", ty_T),//
				mTypeEnvironment());
		testExpression("x ↦ {}", "S × ℙ(T)",//
				mTypeEnvironment("x", ty_S),//
				mTypeEnvironment());
	}

	private void testExpression(String image, String typeImage,
			ITypeEnvironment initialEnv, ITypeEnvironment inferredEnv) {
		final Expression expr = parseExpression(image);
		final Type expectedType = parseType(typeImage);
		final ITypeCheckResult actualResult = expr.typeCheck(initialEnv,
				expectedType);
		IInferredTypeEnvironment inferredTypEnv = null;
		if (inferredEnv != null) {
			inferredTypEnv = mInferredTypeEnvironment(initialEnv);
			ITypeEnvironment.IIterator iter = inferredEnv.getIterator();
			while (iter.hasNext()) {
				iter.advance();
				inferredTypEnv.addName(iter.getName(), iter.getType());
			}
		}
		assertEquals(
				"\nTest failed on: " + image + "\nExpected type: "
						+ expectedType + "\nParser result: " + expr
						+ "\nType check results:\n" + actualResult
						+ "\nInitial type environment:\n"
						+ actualResult.getInitialTypeEnvironment() + "\n",
				inferredTypEnv != null, actualResult.isSuccess());
		assertEquals("\nResult typenv differ for: " + image + "\n",
				inferredTypEnv, actualResult.getInferredEnvironment());
		if (inferredTypEnv != null) {
			assertTrue(expr.isTypeChecked());
			assertEquals(expectedType, expr.getType());
		}
	}

}
