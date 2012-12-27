/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.eventb.core.ast.tests.FastFactory.mInferredTypeEnvironment;
import static org.eventb.core.ast.tests.FastFactory.mTypeEnvironment;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.IInferredTypeEnvironment;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.junit.Test;

/**
 * Unit test of the mathematical formula Type-Checker for expressions with an
 * expected type.
 * 
 * @author Laurent Voisin
 */
public class TestExprTypeChecker extends AbstractTests {

	/**
	 * Main test routine.
	 */
	@Test 
	public void testExpTypeChecker() {
		testExpression("x", "S",//
				mTypeEnvironment(),//
				mTypeEnvironment("x=S", ff));
		testExpression("x", "S",//
				mTypeEnvironment("x=S", ff),//
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
				mTypeEnvironment("x=S; y=T", ff),//
				mTypeEnvironment());
		testExpression("x ↦ {}", "S × ℙ(T)",//
				mTypeEnvironment("x=S", ff),//
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
			inferredTypEnv.addAll(inferredEnv);
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
