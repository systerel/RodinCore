/*******************************************************************************
 * Copyright (c) 2009, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.tests;

import junit.framework.TestCase;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.rodinp.keyboard.RodinKeyboardPlugin;

/**
 * @author htson
 *         <p>
 *         This class contains some expression test cases for Event-B Keyboard.
 *         This test the Keyboard on some large expressions taken from Prof.
 *         Jean-Raymond Abrial's Marriage and SHWT developments
 */
public abstract class AbstractRodinKeyboardTestCase extends TestCase {

	protected Text widget;

	protected ModifyListener listener;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		RodinKeyboardPlugin keyboardPlugin = RodinKeyboardPlugin.getDefault();
		widget = keyboardPlugin.getRodinKeyboardViewWidget();

		listener = keyboardPlugin.getRodinModifyListener();

		// Remove the listener
		// In order to simulate user's input, we have to manually setup the
		// listener.
		widget.removeModifyListener(listener);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		widget.addModifyListener(listener);
	}

	/**
	 * We use this method to simulate the action of typing a character into the
	 * text area
	 */
	protected void insert(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			insert(c);
		}
	}

	private void insert(char c) {
		String tmp = "" + c;
		widget.insert(tmp);
		Event e = new Event();
		e.widget = widget;
		// Force the listener to modify the text then remove it again
		listener.modifyText(new ModifyEvent(e));
		widget.removeModifyListener(listener);
	}
	
	protected void doTest(String input, String expected) {
		doTest(input, input, expected);
	}
	
	protected void doTest(String message, String input, String expected) {
		widget.setText("");
		insert(input);
		compare(message, expected);
	}
	
	// Compare the result with the expected result
	protected void compare(String message, String expect) {
		String actual = widget.getText();
		// replace all platform specific delimiters
		actual = actual.replaceAll(Text.DELIMITER, "\n");
		assertEquals(message, expect, actual);
	}


}
