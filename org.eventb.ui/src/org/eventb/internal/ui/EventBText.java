/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
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
	 * @param parent
	 *            the Composite parent of this
	 * @param toolkit
	 *            the FormToolkit used to creat the Text
	 * @param style
	 *            the style used to create the Text
	 */
	public EventBText(Text text) {
		super(text);
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
