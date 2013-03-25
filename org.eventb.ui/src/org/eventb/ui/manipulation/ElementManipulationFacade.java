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
package org.eventb.ui.manipulation;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.eventbeditor.handlers.CopyHandler;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.internal.ui.preferences.PreferenceUtils;
import org.eventb.ui.eventbeditor.IRodinHistory;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

/**
 * Static high-level methods to headlessly manipulate the rodin model from
 * outside the Event-B editor.
 * 
 * @author Thomas Muller
 * @since 2.4
 */
public class ElementManipulationFacade {

	/**
	 * Method to retrieve the {@link IUndoContext} associated to the given root.
	 */
	public static IUndoContext getRodinFileUndoContext(IEventBRoot root) {
		return OperationFactory.getRodinFileUndoContext(root);
	}

	public static IRodinHistory getHistory() {
		return History.getInstance();
	}

	public static Transfer getRodinHandleTransfer() {
		return RodinHandleTransfer.getInstance();
	}

	/**
	 * Copies both names and handles of the given elements in a clipboard build
	 * with the given display.
	 * 
	 * @param elementsToCopy
	 *            the elements to copy
	 * @param clipboard
	 *            the clipboard to get the copied elements
	 */
	public static void copyElementsToClipboard(
			final Collection<IRodinElement> elementsToCopy,
			final Clipboard clipboard) {
		Collection<IRodinElement> elements = new ArrayList<IRodinElement>();
		for (IRodinElement element : elementsToCopy) {
			elements = UIUtils.addToTreeSet(elements, element);
		}
		CopyHandler.copyToClipboard(elements, clipboard);
	}

	/**
	 * Method to rename automatically all the elements of the given root of a
	 * given type with the prefix set for this element type through the
	 * preferences.
	 */
	public static void autoRenameElements(IEventBRoot root,
			IInternalElementType<?> type) {
		final String prefix = PreferenceUtils.getAutoNamePrefix(root, type);
		final IAttributeDesc desc = ElementDescRegistry.getInstance()
				.getElementDesc(type).getAutoNameAttribute();
		History.getInstance().addOperation(
				OperationFactory.renameElements(root, type,
						desc.getManipulation(), prefix));
	}

	/**
	 * Perform a copy operation through the undo history.
	 */
	public static void copyElements(IRodinElement[] handleData,
			IRodinElement target, IRodinElement nextSibling) {
		History.getInstance().addOperation(
				OperationFactory.copyElements((IInternalElement) target,
						handleData, (IInternalElement) nextSibling));
	}

	/**
	 * Creates a generic element from a given type in the given parent before a
	 * given sibling that shall have the same type. The operation is added in
	 * the Event-B UI history.
	 * 
	 * @param parent
	 *            the parent of the element created
	 * @param type
	 *            the type of the element created
	 * @param sibling
	 *            the next sibling of the created element
	 */
	public static void createElementGeneric(IInternalElement parent,
			IInternalElementType<?> type, IInternalElement sibling) {
		final AtomicOperation op = OperationFactory.createElementGeneric(
				parent, type, sibling);
		History.getInstance().addOperation(op);
	}

	/**
	 * Changes the value of a given element in an atomic step (operation) which
	 * is added to the Event-B UI history.
	 * 
	 * @param element
	 *            the element owner of the attribute
	 * @param manip
	 *            attribute manipulation of the element
	 * @param value
	 *            the string corresponding to the new value of the element
	 * @since 3.0
	 */
	public static void changeAttribute(IInternalElement element,
			IAttributeManipulation manip, String value) {
		final AtomicOperation op = OperationFactory.changeAttribute(manip,
				element, value);
		History.getInstance().addOperation(op);
	}

	/**
	 * Changes the value of a given element in an atomic step (operation) which
	 * is added to the Event-B UI history.
	 * 
	 * @param element
	 *            the element owner of the attribute
	 * @param value
	 *            the new value of the element
	 */
	public static void changeAttribute(IInternalElement element,
			IAttributeValue value) {
		final AtomicOperation op = OperationFactory.changeAttribute(element,
				value);
		History.getInstance().addOperation(op);
	}

	/**
	 * Deletes the given elements in an atomic step (operation) which is added
	 * to the Event-B UI history.
	 * 
	 * @param elements
	 *            the internal elements to delete
	 * @param force
	 *            a flag controlling whether underlying resources that are not
	 *            in sync with the local file system will be tolerated (same as
	 *            the force flag in IResource operations).
	 * @return <code>true</code> if elements have been deleted.
	 */
	public static boolean deleteElement(IInternalElement[] elements,
			boolean force) {
		final AtomicOperation op = OperationFactory.deleteElement(elements,
				force);
		History.getInstance().addOperation(op);
		return op != null;
	}

	/**
	 * Deletes the given element in an atomic step (operation) which is added to
	 * the Event-B UI history.
	 * 
	 * @param element
	 *            the internal element to delete
	 * @return <code>true</code> if element has been deleted.
	 */
	public static boolean deleteElement(IInternalElement element) {
		return deleteElement(new IInternalElement[] { element }, true);
	}

	/**
	 * Create an Operation to move an element in an atomic step (operation)
	 * which is added to the Event-B UI history.
	 * <p>
	 * After execute and redo :
	 * <ul>
	 * <li><code>movedElement.getParent() equals newParent</code></li>
	 * <li> <code>movedElement.getNextSibling() equals newSibling</code></li>
	 * </ul>
	 * <p>
	 * After undo :
	 * <ul>
	 * <li><code>movedElement.getParent()</code> equals oldParent</li>
	 * <li><code>movedElement.getNextSibling()</code> equals oldSibling</li>
	 * </ul>
	 * 
	 * @param element
	 *            the element to move
	 * @param targetParent
	 *            the new parent of moved element
	 * @param nextSibling
	 *            the new next sibling element of moved element
	 */
	public static void move(IInternalElement targetParent,
			IInternalElement element, IInternalElement nextSibling) {
		final AtomicOperation op = OperationFactory.move(targetParent, element,
				targetParent, nextSibling);
		History.getInstance().addOperation(op);
	}

	/**
	 * Tells if the given element is read-only.
	 * 
	 * @param element
	 *            the current element
	 * @return <code>true</code> iff the element is read-only
	 */
	public static boolean isReadOnly(IInternalElement element) {
		return EventBUtils.isReadOnly(element);
	}

	/**
	 * Returns whether the given element is read only. Additionally, if the
	 * given element is read only, this method informs the user through an info
	 * window.
	 * 
	 * @param element
	 *            an element to check
	 * @return <code>true</code> iff the given element is read only
	 * @since 3.0
	 */
	public static boolean checkAndShowReadOnly(IRodinElement element) {
		return EventBEditorUtils.checkAndShowReadOnly(element);
	}

}
