/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
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
 ******************************************************************************/
package org.eventb.internal.ui;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;
import org.eventb.eventBKeyboard.EventBTextModifyListener;
import org.rodinp.keyboard.RodinKeyboardPlugin;

/**
 * @author htson
 *         <p>
 *         This is the class that holds a Text widget to display and to retrieve
 *         expressions which are in the mathematical language of Event-B.
 */
@SuppressWarnings("deprecation")  // TODO 2.0: use new keyboard plug-in here
public class EventBMath extends EventBControl implements IEventBInputText {

	boolean translate;
	
	final EventBTextModifyListener listener = new EventBTextModifyListener();
	
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
		text.addModifyListener(listener);
		text.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				// Do nothing
			}

			public void focusLost(FocusEvent e) {
				if (text.getEditable() && translate) {
					String translateStr = RodinKeyboardPlugin.getDefault().translate(text
							.getText());
					if (!text.getText().equals(translateStr)) {
						text.setText(translateStr);
					}
				}
				commit();
			}

		});
	}

	protected void commit() {
		// Do nothing. Clients should override this method to implement the
		// intended behaviour.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.IEventBInputText#getTextWidget()
	 */
	public Text getTextWidget() {
		return (Text) getControl();
	}

}