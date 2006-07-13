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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.widgets.FormText;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;

/**
 * @author htson
 *         <p>
 *         This is the decorator class to the FormText that used the Event-B
 *         Math Font.
 */
public class EventBFormText implements IEventBFormText {

	// The actual FormText.
	FormText formText;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param formText
	 *            The actual FormText which the Event-B font is attached to.
	 * 
	 */
	public EventBFormText(FormText formText) {
		this.formText = formText;

		Font font = JFaceResources
				.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		formText.setFont(font);

		// Set the hyperlink style to underline only
		// when the mouse is over the link
		HyperlinkSettings hyperlinkSettings = new HyperlinkSettings(Display
				.getCurrent());
		hyperlinkSettings
				.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
		formText.setHyperlinkSettings(hyperlinkSettings);

		// Register as a listener to the font registry
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
			formText.setFont(font);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.IEventBFormText#getFormText()
	 */
	public FormText getFormText() {
		return formText;
	}

	public void dispose() {
		JFaceResources.getFontRegistry().removeListener(this);
		formText.dispose();
	}
	
}
