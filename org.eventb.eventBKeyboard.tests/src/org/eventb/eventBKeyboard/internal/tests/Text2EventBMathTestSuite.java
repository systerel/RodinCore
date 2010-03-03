/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.eventBKeyboard.internal.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author htson
 *         <p>
 *         JUnit Testing Suite contain all the Test Case for Text2EventBMath
 *         translator.
 * @deprecated use org.eventb.keyboard.tests instead
 */
@Deprecated
public class Text2EventBMathTestSuite {

	/**
	 * Creating a new TestSuite
	 * <p>
	 * 
	 * @return a new TestSuite for Text2EventBMath translator
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eventb.eventBKeyboard.internal.Text2EventBMathTransltation");
		// $JUnit-BEGIN$
		suite.addTestSuite(Text2EventBMathSimpleTestCase.class);
		suite.addTestSuite(Text2EventBMathExpressionTestCase.class);
		// $JUnit-END$
		return suite;
	}

}
