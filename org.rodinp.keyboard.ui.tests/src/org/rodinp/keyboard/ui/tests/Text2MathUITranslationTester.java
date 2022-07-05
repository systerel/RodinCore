/*******************************************************************************
 * Copyright (c) 2009, 2022 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - port to JUnit 4
 *     Systerel - extracted to make test cases reusable
 *******************************************************************************/
package org.rodinp.keyboard.ui.tests;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.rodinp.keyboard.core.tests.IKeyboardTranslationTester;

/**
 * Tester class for the UI symbol translation.
 * 
 * @author htson
 */
public class Text2MathUITranslationTester implements IKeyboardTranslationTester {

	private Text widget;

	private ModifyListener listener;

	public Text2MathUITranslationTester(Text widget, ModifyListener listener) {
		this.widget = widget;
		this.listener = listener;
	}

	/**
	 * We use this method to simulate the action of typing a character into the
	 * text area.
	 */
	public void insert(String s, int caretOffset) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			insert(c, caretOffset);
		}
	}

	public void insert(String s) {
		insert(s, -1);
	}

	private void insert(char c, int caretOffset) {
		String tmp = "" + c;
		if (caretOffset >= 0) {
			// Insert at the end
			widget.setSelection(widget.getCharCount());
			widget.insert(tmp);
			// Try to put back cursor at its desired position if provided
			widget.setSelection(Math.min(caretOffset, widget.getCharCount()));
		} else {
			widget.insert(tmp);
		}
		Event e = new Event();
		e.widget = widget;
		// Force the listener to modify the text then remove it again
		listener.modifyText(new ModifyEvent(e));
		widget.removeModifyListener(listener);
	}

	public void doTest(String expected, String input, int caretOffset) {
		doTest(input, expected, input, caretOffset);
	}

	public void doTest(String message, String expected, String input, int caretOffset) {
		widget.setText("");
		insert(input, caretOffset);
		compare(message, expected);
	}

	// Compare the result with the expected result
	public void compare(String message, String expect) {
		String actual = widget.getText();
		// replace all platform specific delimiters
		actual = actual.replaceAll(Text.DELIMITER, "\n");
		assertEquals(message, expect, actual);
	}

	@Override
	public void doTest(String... strings) {

	}

}
