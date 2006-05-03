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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Text;
import org.eventb.eventBKeyboard.EventBTextModifyListener;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;

/**
 * @author htson
 *         <p>
 *         This is the class that hold a Text using to display and to retrive
 *         expressions which are in the mathematical language of Event-B.
 */
public class EventBMath implements IEventBInputText {

	// The actual Text.
	private Text text;

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
		this.text = text;
		Font font = JFaceResources
				.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		text.setFont(font);
		text.addModifyListener(new EventBTextModifyListener());

		JFaceResources.getFontRegistry().addListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.EVENTB_MATH_FONT)) {
			Font font = JFaceResources
					.getFont(PreferenceConstants.EVENTB_MATH_FONT);
			text.setFont(font);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.IEventBInputText#setFocus()
	 */
	public void setFocus() {
		text.setFocus();
		text.selectAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.IEventBInputText#getTextWidget()
	 */
	public Text getTextWidget() {
		return text;
	}

}