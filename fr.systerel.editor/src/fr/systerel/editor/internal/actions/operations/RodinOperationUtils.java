/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.actions.operations;

import static fr.systerel.editor.internal.editors.RodinEditorUtils.log;
import static fr.systerel.editor.internal.editors.RodinEditorUtils.showError;

import org.eventb.ui.manipulation.ElementManipulationFacade;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

/**
 *
 */
public class RodinOperationUtils {

	/**
	 * Paste the given elements as children or siblings of the given target. If
	 * possible the elements are copied as children, otherwise as siblings. If
	 * none is possible, an error is displayed in a pop-up.
	 * 
	 * @param target
	 *            the element where to paste into or after
	 * @param elements
	 *            the elements to paste
	 */
	public static void pasteElements(IInternalElement target,
			IRodinElement[] elements) {
		new PasteOperation(elements, target).execute();
	}

	public static void changeAttribute(IInternalElement element,
			IAttributeType.String type, String textValue) {
		final IAttributeValue.String newValue = type.makeValue(textValue);
		try {
			if (!element.hasAttribute(type)
					|| !element.getAttributeValue(type).equals(newValue.getValue())) {
				ElementManipulationFacade.changeAttribute(element, newValue);
			}
		} catch (RodinDBException e) {
			logAndInformAttributeChangeError(e);
		}
	}

	public static void changeAttribute(ILElement element,
			IAttributeManipulation manip, String value) {
		final IInternalElement ielement = element.getElement();
		final String oldValue;
		try {
			if (manip.hasValue(ielement, null)) {
				oldValue = manip.getValue(ielement, null);
			} else {
				oldValue = null;
			}
			if ((oldValue == null && value.isEmpty()) || value.equals(oldValue)) {
				return;
			}
			ElementManipulationFacade.changeAttribute(ielement, manip, value);
		} catch (RodinDBException e) {
			logAndInformAttributeChangeError(e);
		}
	}

	private static void logAndInformAttributeChangeError(RodinDBException e) {
		log(e.getStatus());
		showError("Error when changing an attribute value", //
				"An error occured. Check the log file for"
						+ " detailed information.");
	}

	public static void move(IInternalElement parent, IInternalElement element,
			IInternalElement nextSibling) {
		ElementManipulationFacade.move(parent, element, nextSibling);
	}

	public static boolean isReadOnly(ILElement element) {
		return ElementManipulationFacade.isReadOnly(element.getElement());
	}

}
