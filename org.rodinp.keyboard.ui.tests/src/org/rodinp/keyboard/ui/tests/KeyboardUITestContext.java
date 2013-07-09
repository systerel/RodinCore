/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.ui.tests;

import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.rodinp.keyboard.ui.RodinKeyboardUIPlugin;

/**
 * Basic class used by test cases to initiate an UI context (i.e. retrieve a
 * widget to translate text, and the Rodin modify listener to allow dynamic
 * translation) for graphical translation tests.
 */
public class KeyboardUITestContext {

	private Text widget;

	private ModifyListener listener;

	public void setUp() throws Exception {
		final RodinKeyboardUIPlugin keyboardPlugin = RodinKeyboardUIPlugin
				.getDefault();
		widget = keyboardPlugin.getRodinKeyboardViewWidget();
		listener = keyboardPlugin.getRodinModifyListener();
		// Remove the listener in order to simulate user's input, we have to
		// manually setup the listener.
		widget.removeModifyListener(listener);
	}

	public void tearDown() throws Exception {
		widget.addModifyListener(listener);
	}

	public Text2MathUITranslationTester getTranslationTester() {
		return new Text2MathUITranslationTester(widget, listener);
	}

	public ModifyListener getListener() {
		return listener;
	}

	public Text getWidget() {
		return widget;
	}

	public void setListener(ModifyListener listener) {
		this.listener = listener;
	}

	public void setWidget(Text widget) {
		this.widget = widget;
	}

}