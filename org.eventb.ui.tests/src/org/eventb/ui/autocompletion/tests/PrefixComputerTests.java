/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.autocompletion.tests;

import static org.junit.Assert.assertEquals;

import org.eventb.internal.ui.autocompletion.PrefixComputer;
import org.eventb.ui.tests.utils.EventBUITest;
import org.junit.Test;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class PrefixComputerTests extends EventBUITest {

	private void doPrefixTest(String toComplete, int position,
			String expectedPrefix) {
		final PrefixComputer pc = new PrefixComputer(toComplete, position, ff);
		final String prefix = pc.getPrefix();
		assertEquals("bad prefix", expectedPrefix, prefix);
	}

	@Test
	public void testLength0() throws Exception {
		doPrefixTest("", 0, "");
	}

	@Test
	public void testLength1() throws Exception {
		doPrefixTest("c", 1, "c");
	}

	@Test
	public void testLength1Space() throws Exception {
		doPrefixTest(" ", 1, "");
	}
	
	@Test
	public void testLength1Number() throws Exception {
		doPrefixTest("1", 1, "");
	}

	@Test
	public void testSpacesBefore() throws Exception {
		doPrefixTest("  c", 3, "c");
	}
	
	@Test
	public void testPosNotEnd() throws Exception {
		doPrefixTest("consta", 5, "const");
	}

	@Test
	public void testSuffixNotPrefix() throws Exception {
		doPrefixTest("  1234", 6, "");
	}

	@Test
	public void testLongNoSpace() throws Exception {
		doPrefixTest("abcd+efghijklmnopqrstuv", 23, "efghijklmnopqrstuv");
	}

	// prefix="" when the bug is present
	@Test
	public void testBug2808375() throws Exception {
		doPrefixTest("c1", 2, "c1");
	}

	@Test
	public void testBug2808375_2() throws Exception {
		doPrefixTest("c123456", 7, "c123456");
	}
	
}
