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

package org.eventb.internal.ui;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.widgets.FormText;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;

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
	public EventBFormText(FormText formText) {
		this.formText = formText;
		
		Font font = JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		formText.setFont(font);
		
		HyperlinkSettings hyperlinkSettings = new HyperlinkSettings(EventBUIPlugin.getActiveWorkbenchWindow().getWorkbench().getDisplay());
		hyperlinkSettings.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
		formText.setHyperlinkSettings(hyperlinkSettings);
		
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


	public FormText getFormText() {return formText;}

}
