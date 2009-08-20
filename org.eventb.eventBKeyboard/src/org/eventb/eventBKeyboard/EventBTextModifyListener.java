/*******************************************************************************
 * Copyright (c) 2005, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.eventBKeyboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;
import org.eventb.internal.eventBKeyboard.KeyboardUtils;
import org.rodinp.keyboard.RodinKeyboardPlugin;

/**
 * @author htson
 *         <p>
 *         The main class for translating ASCII into mathematical language of
 *         Event-B. This is done by using all the translators found in the
 *         extension registry.
 *         </p>
 * @deprecated use {@link RodinKeyboardPlugin#getRodinModifyListener()}.
 */
@Deprecated
public class EventBTextModifyListener implements ModifyListener {

	// The extension identifier.
	private static final String translatorId = "org.eventb.eventBKeyboard.translators";

	/**
	 * Collection of translators.
	 */
	private Collection<IEventBKeyboardTranslator> translators;

	private boolean enable = true;
	
	/**
	 * Main method for the listener. This is call when the text in the widget
	 * has been changed. This gets all the transator from the extension register
	 * and invokes them.
	 * <p>
	 * 
	 * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
	 */
	public void modifyText(ModifyEvent e) {
		if (!enable)
			return;
		
		Text widget = (Text) e.widget;

		getTranslators();

		widget.removeModifyListener(this); // Disable the listener
		for (Iterator<IEventBKeyboardTranslator> it = translators.iterator(); it
				.hasNext();) {
			IEventBKeyboardTranslator translator = it.next();
			translator.translate(widget);
		}
		widget.addModifyListener(this); // Enable the listener
		KeyboardUtils.debugMath("Caret position: " + widget.getCaretPosition());
	}

	/**
	 * Method to get all translators (as extensions) from extension registry.
	 */
	private void getTranslators() {
		if (translators != null)
			return;
		else {
			translators = new ArrayList<IEventBKeyboardTranslator>();
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint extensionPoint = registry
					.getExtensionPoint(translatorId);
			IExtension[] extensions = extensionPoint.getExtensions();

			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] elements = extensions[i]
						.getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					try {
						Object translator = elements[j]
								.createExecutableExtension("class");
						if (translator instanceof IEventBKeyboardTranslator) {
							translators
									.add((IEventBKeyboardTranslator) translator);
						}
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

}
