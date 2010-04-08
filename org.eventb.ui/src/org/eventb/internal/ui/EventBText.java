/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - changed double click behavior
 ******************************************************************************/

package org.eventb.internal.ui;

import org.eclipse.swt.widgets.Text;

/**
 * @author htson
 *         <p>
 *         This is the class that hold a Text that used Event-B Text Font.
 */
public class EventBText extends EventBControl implements IEventBInputText {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param text
	 *            a Text widget
	 */
	public EventBText(Text text) {
		super(text);
		text.addMouseListener(new DoubleClickTextListener(text));
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
