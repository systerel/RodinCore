/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.actions.operations;

import static fr.systerel.editor.internal.editors.RodinEditorUtils.showInfo;
import static org.eventb.internal.ui.utils.Messages.dialogs_readOnlyElement;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eventb.core.IEventBRoot;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.eventb.ui.manipulation.ElementManipulationFacade;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.emf.api.itf.ILElement;

import fr.systerel.editor.EditorPlugin;
import fr.systerel.editor.internal.editors.RodinEditorUtils;

/**
 *
 */
public class RodinOperationUtils {

	/**
	 * 
	 * @param target
	 * @param elements
	 */
	public static void pasteElements(IInternalElement target,
			IRodinElement[] elements) {
		if (checkAndShowReadOnly(target)) {
			return;
		}
		final IElementType<?> typeNotAllowed = elementTypeNotAllowed(elements,
				target);
		if (typeNotAllowed == null) {
			ElementManipulationFacade.copyElements(elements, target, null);
		} else if (haveSameType(elements, target)) {
			try {
				ElementManipulationFacade.copyElements(elements,
						target.getParent(), target.getNextSibling());
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		} else {
			RodinEditorUtils.showError("Cannot Paste", "Cannot paste a "
					+ typeNotAllowed.getName() + " element in a "
					+ target.getElementType().getName() + " element.");
			return;
		}
		if (EditorPlugin.DEBUG)
			RodinEditorUtils.debug("PASTE SUCCESSFULLY");
	}

	/**
	 * Returns whether the given element is read only. Additionally, if the
	 * given element is read only, this method informs the user through an info
	 * window.
	 * 
	 * @param element
	 *            an element to check
	 * @return true iff the given element is read only
	 */
	public static boolean checkAndShowReadOnly(IRodinElement element) {
		if (!(element instanceof IInternalElement)) {
			return false;
		}
		final boolean readOnly = ElementManipulationFacade
				.isReadOnly((IInternalElement) element);
		if (readOnly) {
			showInfo(dialogs_readOnlyElement(getDisplayName(element)));
		}
		return readOnly;
	}

	private static String getDisplayName(IRodinElement element) {
		try {
			if (element instanceof ILabeledElement) {
				return ((ILabeledElement) element).getLabel();
			} else if (element instanceof IIdentifierElement) {
				return ((IIdentifierElement) element).getIdentifierString();
			} else if (element instanceof IEventBRoot) {
				return element.getElementName();
			}
		} catch (RodinDBException e) {
			RodinEditorUtils.log(e, "when checking for read-only element");
		}
		return "";
	}

	/**
	 * Returns the type of an element that is not allowed to be pasted as child
	 * of target.
	 * 
	 * @return the type that is not allowed to be pasted or <code>null</code> if
	 *         all elements to paste can become valid children
	 * */
	private static IElementType<?> elementTypeNotAllowed(
			IRodinElement[] toPaste, IRodinElement target) {
		final Set<IElementType<?>> allowedTypes = getAllowedChildTypes(target);
		for (IRodinElement e : toPaste) {
			final IElementType<?> type = e.getElementType();
			if (!allowedTypes.contains(type)) {
				return type;
			}
		}
		return null;
	}

	private static Set<IElementType<?>> getAllowedChildTypes(
			IRodinElement target) {
		final IElementType<?> targetType = target.getElementType();
		final IElementType<?>[] childTypes = ElementDescRegistry.getInstance()
				.getChildTypes(targetType);
		final Set<IElementType<?>> allowedTypes = new HashSet<IElementType<?>>(
				Arrays.asList(childTypes));
		return allowedTypes;
	}

	private static boolean haveSameType(IRodinElement[] toPaste,
			IRodinElement target) {
		final IElementType<?> targetType = target.getElementType();
		for (IRodinElement e : toPaste) {
			if (targetType != e.getElementType()) {
				return false;
			}
		}
		return true;
	}

	public static void changeAttribute(IInternalElement element,
			IAttributeType.String type, String textValue) {
		final IAttributeValue.String newValue = type.makeValue(textValue);
		try {
			if (!element.hasAttribute(type)
					|| !element.getAttributeValue(type).equals(newValue)) {
				ElementManipulationFacade.changeAttribute(element, newValue);
			}
		} catch (RodinDBException e) {
			System.err
					.println("Problems occured when updating the database after attribute edition"
							+ e.getMessage());
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
			if (value.equals(oldValue)) {
				return;
			}
			ElementManipulationFacade.changeAttribute(ielement, manip, value);
		} catch (RodinDBException e) {
			System.err.println("Problems occured when updating the dat" + ""
					+ "" + "" + "abase after attribute edition"
					+ e.getMessage());
		}
	}

	public static void move(IInternalElement parent, IInternalElement element,
			IInternalElement nextSibling) {
		ElementManipulationFacade.move(parent, element, nextSibling);
	}

}
