/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.ui.tests;

import org.junit.After;
import org.junit.Before;
import org.rodinp.keyboard.core.tests.IKeyboardTranslationTester;
import org.rodinp.keyboard.core.tests.Text2MathExtensionTestCase;

/**
 * Tests the UI keyboard translation with programmatic contributions reusing
 * test cases of {@link Text2MathExtensionTestCase}.
 */
public class RodinKeyboardExtensionTestCase extends Text2MathExtensionTestCase {


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
