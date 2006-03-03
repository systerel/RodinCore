/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.IEventBFormText;

/**
 * @author htson
 * <p>
 * This is the class that hold FormText that used Event-B Text Font.
 */
public class EventBFormText 
	implements IEventBFormText
{
	
	// The actual FormText.
	FormText formText;
	

	/**
	 * Contructor.
	 * <p>
	 * @param parent the composite parent of the FormText
	 * @param toolkit the FormToolkit used to create the FormText
	 */
	public EventBFormText(Composite parent, FormToolkit toolkit) {
		formText = toolkit.createFormText(parent, true);
		
		toolkit.paintBordersFor(formText);
		Font font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		formText.setFont(font);

		// Register as a listener to the font registry
		JFaceResources.getFontRegistry().addListener(this);
	}

	
	/**
	 * This call back is used when the font is change in the Preferences.
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.EVENTB_MATH_FONT)) {
			Font font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
			formText.setFont(font);
		}
	}

	
	/**
	 * Setting the content of the Form Text.
	 * <p>
	 * @param str Any string
	 */
	public void setText(String str) {formText.setText(str, true, false);}

	
	/**
	 * Adding a hyperlink listener to the Form Text.
	 * <p>
	 * @param listener Any HyperlinkAdapter
	 */
	public void addHyperlinkListener(HyperlinkAdapter listener) {formText.addHyperlinkListener(listener);}

	
	/**
	 * Removing a hyperlink listener from the FormText.
	 * <p>
	 * @param listener Any HyperlinkAdapter
	 */
	public void removeHyperlinkListener(HyperlinkAdapter listener) {formText.removeHyperlinkListener(listener);}

}
