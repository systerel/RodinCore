/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - changed double click behavior
 *     ETH Zurich - adapted to org.rodinp.keyboard
 *     Systerel - now exporting method translate()
 ******************************************************************************/
package org.eventb.internal.ui;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Text;
import org.rodinp.keyboard.RodinKeyboardPlugin;

/**
 * @author htson
 *         <p>
 *         This is the class that holds a Text widget to display and to retrieve
 *         expressions which are in the mathematical language of Event-B.
 */
public class EventBMath extends EventBControl implements IEventBInputText {
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param text
	 *            a Text widget
	 */
	public EventBMath(final Text text) {
		super(text);
		text.addMouseListener(new DoubleClickTextListener(text));
		final RodinKeyboardPlugin keyBPlugin = RodinKeyboardPlugin.getDefault();
		text.addModifyListener(keyBPlugin.createRodinModifyListener());
		text.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				translate();
				commit();
			}

		});
	}

	public void translate() {
		final Text text = getTextWidget();
		if (text.getEditable()) {
			final String before = text.getText();
			final String after = RodinKeyboardPlugin.getDefault().translate(before);
			if (!before.equals(after)) {
				text.setText(after);
			}
		}
	}

	protected void commit() {
		// Do nothing. Clients should override this method to implement the
		// intended behaviour.
	}

	@Override
	public Text getTextWidget() {
		return (Text) getControl();
	}

}