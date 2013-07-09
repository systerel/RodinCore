/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.keyboard.tests.ui;

import org.eventb.keyboard.tests.core.Text2EventBMathExpressionTestCase;
import org.junit.After;
import org.junit.Before;
import org.rodinp.keyboard.core.tests.IKeyboardTranslationTester;
import org.rodinp.keyboard.ui.tests.KeyboardUITestContext;

/**
 * This class reuses the test cases provided by
 * {@link Text2EventBMathExpressionTestCase} and realize the test in UI.
 * 
 * @author htson
 */
public class EventBKeyboardExpressionTestCase extends Text2EventBMathExpressionTestCase {
	
	protected KeyboardUITestContext context = new KeyboardUITestContext();

	@Before
	public void setUp() throws Exception {
		context.setUp();
	}

	@After
	public void tearDown() throws Exception {
		context.tearDown();
	}

	@Override
	protected IKeyboardTranslationTester getTranslatorTester() {
		return context.getTranslationTester();
	}
	
}
