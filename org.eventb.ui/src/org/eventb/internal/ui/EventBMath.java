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
import org.eventb.eventBKeyboard.EventBTextModifyListener;

/**
 * @author htson
 *         <p>
 *         This is the class that hold a Text using to display and to retrive
 *         expressions which are in the mathematical language of Event-B.
 */
public class EventBMath extends EventBControl implements IEventBInputText {

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
	public EventBMath(Text text) {
		super(text);
		text.addModifyListener(new EventBTextModifyListener());
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