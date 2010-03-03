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
 * <p>
 * JUnit Testing Suite contain all the Test Case for Event-B Keyboard
 * 
 * @deprecated use org.eventb.keyboard.tests instead
 */
@Deprecated
public class EventBKeyboardTestSuite {

	/**
	 * Creating the TestSuite
	 * <p>
	 * @return a new TestSuite
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eventb.eventBKeyboard");
		//$JUnit-BEGIN$
		suite.addTestSuite(EventBKeyboardSimpleTestCase.class);
		suite.addTestSuite(EventBKeyboardExpressionTestCase.class);
		suite.addTestSuite(EventBKeyboardJumpingTestCase.class);
		//$JUnit-END$
		return suite;
	}

}
