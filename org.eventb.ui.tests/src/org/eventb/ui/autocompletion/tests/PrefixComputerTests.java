/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.ui.autocompletion.tests;

import org.eventb.internal.ui.autocompletion.PrefixComputer;
import org.eventb.ui.tests.utils.EventBUITest;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class PrefixComputerTests extends EventBUITest {

	private void doPrefixTest(String toComplete, int position,
			String expectedPrefix) {
		final PrefixComputer pc = new PrefixComputer(toComplete, position);
		final String prefix = pc.getPrefix();
		assertEquals("bad prefix", expectedPrefix, prefix);
	}

	public void testLength0() throws Exception {
		doPrefixTest("", 0, "");
	}

	public void testLength1() throws Exception {
		doPrefixTest("c", 1, "c");
	}

	public void testLength1Space() throws Exception {
		doPrefixTest(" ", 1, "");
	}

	public void testLength2() throws Exception {
		doPrefixTest("   consta", 8, "const");
	}

	public void testLength3Space2() throws Exception {
		doPrefixTest("abcd+efghijklmnopqrstuv", 23, "efghijklmnopqrstuv");
	}

	// prefix="" when the bug is present
	public void testBug2808375() throws Exception {
		doPrefixTest("c1", 2, "c1");
	}
}
