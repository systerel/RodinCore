/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.eventBKeyboard.internal.tests;

import junit.framework.TestCase;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eventb.eventBKeyboard.EventBKeyboardPlugin;
import org.eventb.eventBKeyboard.EventBTextModifyListener;
import org.eventb.eventBKeyboard.internal.views.EventBKeyboardView;

/**
 * @author htson
 *         <p>
 *         This class contains some test cases for Event-B Keyboard. This test
 *         the Keyboard on some large expressions where there are some jumpings.
 *         The text is taken from Prof. Jean-Raymond Abrial's SHWT development.
 */
@SuppressWarnings("deprecation")
public class EventBKeyboardJumpingTestCase extends TestCase {

	private Text formula;

	private EventBTextModifyListener listener;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		IWorkbenchPage page = EventBKeyboardPlugin.getActivePage();

		EventBKeyboardView view = (EventBKeyboardView) page
				.findView(EventBKeyboardPlugin.EventBKeyboardView_ID);

		if (view == null)
			view = (EventBKeyboardView) page
					.showView(EventBKeyboardPlugin.EventBKeyboardView_ID);

		formula = view.getFormula();
		listener = view.getListener();

		// Remove the listener
		// In order to simulate user's input, we have to manually setup the
		// listener.
		formula.removeModifyListener(listener);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		formula.addModifyListener(listener);
	}

	/**
	 * We use this method to similate the action of typing a character into the
	 * text area
	 */
	private void insert(String s) {
		formula.insert(s);
		Event e = new Event();
		e.widget = formula;
		// Force the listener to modify the text then remove it again
		listener.modifyText(new ModifyEvent(e));
		formula.removeModifyListener(listener);
	}

	// Compare the result with the expected result
	private void compare(String message, String expect) {
		String actual = formula.getText();
		assertEquals(message, expect, actual);
	}

	public void testSHWTInvariant() {
		formula.setText("");
		String input = "bm < NODE";
		for (int i = 0; i < input.length(); i++)
			insert("" + input.charAt(i));
		formula.setSelection(4);
		insert(":");
		compare("SHWTInvariant ", "bm \u2286 NODE");
	}

	public void testMarkActions() {
		formula.setText("");
		String input = "bm : cl[{tp}]";
		for (int i = 0; i < input.length(); i++)
			insert("" + input.charAt(i));
		formula.setSelection(4);
		insert("=");
		compare("SHWTInvariant ", "bm \u2254 cl[{tp}]");
	}

	public void testProg11Actions() {
		formula.setText("");
		String input = "bl = bl";
		for (int i = 0; i < input.length(); i++)
			insert("" + input.charAt(i));
		formula.setSelection(3);
		insert(":");
		formula.setSelection(7);
		input = " \\ {yy}";
		for (int i = 0; i < input.length(); i++)
			insert("" + input.charAt(i));
		formula.setSelection(9);
		insert("/");
		compare("SHWTInvariant ", "bl \u2254 bl \u222a {yy}");
	}
}
