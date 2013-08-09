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

import org.junit.Test;

/**
 * This class contains some test cases for the Event-B Keyboard translation with
 * dynamic insertions.
 * <p>
 * It checks keyboard symbols translation on large expressions where text is
 * dynamically inserted (i.e. "jumping").<br />
 * The text is taken from Prof. Jean-Raymond Abrial's SHWT development.
 * </p>
 * 
 * @author htson
 */
public class EventBKeyboardJumpingTestCase extends AbstractKeyboardJumpingTestCase {

	/**
	 * Checks that insertion of ":" after "⊂" is translated to "⊆"
	 */
	@Test
	public void testSHWTInvariant() {
		testJumping("bm < NODE", 4, ":", "bm \u2286 NODE");
	}

	/**
	 * Checks that insertion of "=" after ":" is translated to "≔"
	 */
	@Test
	public void testMarkActions() {
		testJumping("bm : cl[{tp}]", 4, "=", "bm \u2254 cl[{tp}]");
	}

	/**
	 * Checks that insertion of ":" after "=" is translated to "≔" and "/" after
	 * "\ {yy} is translated to "∪ {yy}".
	 */
	@Test
	public void testProg11Actions() {
		testJumping("bl = bl", new int[] { 3, 7, 9 }, new String[] { ":",
				" \\ {yy}", "/" }, "bl \u2254 bl \u222a {yy}");
	}
	
	/**
	 * Checks that insertion of "r" after "o" is translated to "∨" if substring
	 * "or" is already present in the widget string contents.
	 */
	@Test
	public void testBug686Translation() {
		testJumping("order=2 o order=3", new int[] { 9 }, new String[] { "r" },
				"order=2 \u2228 order=3");
	}

}
