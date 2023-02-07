/*******************************************************************************
 * Copyright (c) 2006, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.keyboard.ui;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.rodinp.internal.keyboard.ui.translators.IRodinKeyboardTranslator;
import org.rodinp.internal.keyboard.ui.translators.LegacyRodinKeyboardTranslator.LegacyMathTranslator;
import org.rodinp.internal.keyboard.ui.translators.LegacyRodinKeyboardTranslator.LegacyTextTranslator;

/**
 * @author htson
 *         <p>
 *         The main class for translating ASCII into mathematical language of
 *         Event-B. This is done by using all the translators found in the
 *         extension registry.
 */
public class RodinModifyListener implements ModifyListener {

	/**
	 * Collection of translators.
	 */
	private Collection<IRodinKeyboardTranslator> translators;

	private boolean enable = true;
	
	/**
	 * Main method for the listener. This is call when the text in the widget
	 * has been changed. This gets all the translator from the extension register
	 * and invokes them.
	 * <p>
	 * 
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		if (!enable)
			return;
		getTranslators();
		Widget widget = e.widget;
		
		// Disable the listener.
		if (widget instanceof Text)
			((Text) widget).removeModifyListener(this);
		else if (widget instanceof StyledText)
			((StyledText) widget).removeModifyListener(this);
		
		try {
			// Translate the content of the widget using the list of translators.
			for (IRodinKeyboardTranslator translator : translators) {
				translator.translate(widget);
			}
		} finally {
			 // Re-enable the listener, even if an exception was thrown.
			if (widget instanceof Text)
				((Text) widget).addModifyListener(this);
			else if (widget instanceof StyledText)
				((StyledText) widget).addModifyListener(this);
		}
	}

	/**
	 * Method to get all translators (as extensions).
	 */
	private void getTranslators() {
		if (translators != null)
			return;
		else {
			translators = new ArrayList<IRodinKeyboardTranslator>();
			translators.add(new LegacyMathTranslator());
			translators.add(new LegacyTextTranslator());
		}
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}
