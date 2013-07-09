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
 * This class contains some test cases for the Event-B Keyboard LaTeX
 * translation with dynamic insertions.
 * <p>
 * It checks LaTeX symbols translation on large expressions where text is
 * dynamically inserted (i.e. "jumping").<br />
 * The text is taken from Prof. Jean-Raymond Abrial's SHWT development.
 * </p>
 * 
 * @author htson
 */
public class EventBKeyboardLaTeXJumpingTestCase extends AbstractKeyboardJumpingTestCase {

	/**
	 * Checks that insertion of "eq" (i.e. =) after "\\subset" (i.e. "⊂") is
	 * correclty translated as "\\subseteq" (i.e. "⊆")
	 */
	@Test
	public void testSHWTInvariant() {
		testJumping("bm \\subset NODE", 4, "eq", "bm \u2286 NODE");
	}

	/**
	 * Checks that insertion of "bc" after "\\meq" is translated to "\\bcmeq"
	 * (i.e. "≔").
	 */
	@Test
	public void testMarkActions() {
		testJumping("bm \\meq cl[{tp}]", 4, "bc", "bm \u2254 cl[{tp}]");
	}

	/**
	 * Checks that insertion of "\bcm" after "eq" is translated to "\\bcmeq"
	 * (i.e. "≔") and "bunion" after "\ {yy} is translated to "\bunion" (i.e.
	 * "∪ {yy}").
	 */
	@Test
	public void testProg11Actions() {
		testJumping("bl eq bl", new int[] { 3, 7, 9 }, new String[] { "\\bcm",
				" \\ {yy}", "bunion" }, "bl \u2254 bl \u222a {yy}");
	}

}
