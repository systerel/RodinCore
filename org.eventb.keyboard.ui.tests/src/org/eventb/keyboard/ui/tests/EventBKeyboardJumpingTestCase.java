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
public class EventBKeyboardJumpingTestCase extends AbstractRodinKeyboardTestCase {

	@Test
	public void testSHWTInvariant() {
		widget.setText("");
		String input = "bm < NODE";
		for (int i = 0; i < input.length(); i++)
			insert("" + input.charAt(i));
		widget.setSelection(4);
		insert(":");
		compare("SHWTInvariant ", "bm \u2286 NODE");
	}

	@Test
	public void testMarkActions() {
		widget.setText("");
		String input = "bm : cl[{tp}]";
		for (int i = 0; i < input.length(); i++)
			insert("" + input.charAt(i));
		widget.setSelection(4);
		insert("=");
		compare("SHWTInvariant ", "bm \u2254 cl[{tp}]");
	}

	@Test
	public void testProg11Actions() {
		widget.setText("");
		String input = "bl = bl";
		for (int i = 0; i < input.length(); i++)
			insert("" + input.charAt(i));
		widget.setSelection(3);
		insert(":");
		widget.setSelection(7);
		input = " \\ {yy}";
		for (int i = 0; i < input.length(); i++)
			insert("" + input.charAt(i));
		widget.setSelection(9);
		insert("/");
		compare("SHWTInvariant ", "bl \u2254 bl \u222a {yy}");
	}
}
