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
package org.eventb.keyboard.ui.tests;

import org.junit.Test;
import org.rodinp.keyboard.tests.AbstractRodinKeyboardTestCase;

/**
 * @author htson
 *         <p>
 *         This class contains some test cases for Event-B Keyboard. This test
 *         the Keyboard on some large expressions where there are some jumpings.
 *         The text is taken from Prof. Jean-Raymond Abrial's SHWT development.
 */
public class EventBKeyboardLaTeXJumpingTestCase extends AbstractRodinKeyboardTestCase {

	@Test
	public void testSHWTInvariant() {
		widget.setText("");
		insert("bm \\subset NODE");
		widget.setSelection(4);
		insert("eq");
		compare("SHWTInvariant ", "bm \u2286 NODE");
	}

	@Test
	public void testMarkActions() {
		widget.setText("");
		insert("bm \\meq cl[{tp}]");
		widget.setSelection(4);
		insert("bc");
		compare("SHWTInvariant ", "bm \u2254 cl[{tp}]");
	}

	@Test
	public void testProg11Actions() {
		widget.setText("");
		insert("bl eq bl");
		widget.setSelection(3);
		insert("\\bcm");
		widget.setSelection(7);
		insert(" \\ {yy}");
		widget.setSelection(9);
		insert("bunion");
		compare("SHWTInvariant ", "bl \u2254 bl \u222a {yy}");
	}
}
