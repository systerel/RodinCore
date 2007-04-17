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

package org.eventb.core.tests.pm;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author htson
 *         <p>
 *         JUnit Testing Suite contain all the Test Cases for testing User Supports 
 *         translator.
 */
public class PMTestSuite {

	/**
	 * Creating a new TestSuite
	 * <p>
	 * 
	 * @return a new TestSuite for User Supports
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for User Support");
		// $JUnit-BEGIN$
		suite.addTestSuite(TestUserSupportManagers.class);
		suite.addTestSuite(TestUserSupportManagerDeltas.class);
		suite.addTestSuite(TestUserSupports.class);
		suite.addTestSuite(TestUserSupportDeltas.class);
		suite.addTestSuite(TestUserSupportChanges.class);
		suite.addTestSuite(TestUserSupportChangeDeltas.class);
		// $JUnit-END$
		return suite;
	}

}
