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

package org.eventb.ui;

import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.widgets.FormText;
import org.eventb.internal.ui.EventBControl;

/**
 * @author htson
 *         <p>
 *         This is the decorator class to the FormText that used the Event-B
 *         Math Font.
 * @since 1.0
 */
public class EventBFormText extends EventBControl implements IEventBFormText {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param formText
	 *            The actual FormText which the Event-B font is attached to.
	 * 
	 */
	public EventBFormText(FormText formText) {
		super(formText);

		// Set the hyperlink style to underline only when the mouse is over the
		// link
		HyperlinkSettings hyperlinkSettings = new HyperlinkSettings(formText
				.getDisplay());
		hyperlinkSettings
				.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
		formText.setHyperlinkSettings(hyperlinkSettings);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.IEventBFormText#getFormText()
	 */
	@Override
	public FormText getFormText() {
		return (FormText) getControl();
	}

}
