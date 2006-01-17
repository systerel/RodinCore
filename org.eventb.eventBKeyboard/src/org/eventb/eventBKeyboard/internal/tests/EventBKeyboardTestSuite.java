/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.eventBKeyboard.internal.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author htson
 * JUnit Testing Suite contain all the Test Case for Event-B Keyboard
 */
public class EventBKeyboardTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for org.eventb.eventBKeyboard.internal.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(EventBKeyboardSimpleTestCase.class);
		suite.addTestSuite(EventBKeyboardExpressionTestCase.class);
		suite.addTestSuite(EventBKeyboardJumpingTestCase.class);
		//$JUnit-END$
		return suite;
	}

}
