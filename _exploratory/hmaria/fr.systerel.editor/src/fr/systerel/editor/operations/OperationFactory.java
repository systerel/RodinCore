/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.operations;

import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;

public class OperationFactory {

	private OperationFactory() {
		// non instanciable class
	}

	/**
	 * return an Operation to create an Element with default name and label
	 * 
	 * @param parent
	 *            the element where the new element is inserted
	 * @param type
	 *            the type of the new element
	 * @param sibling
	 *            the new element is inserted before sibling. If sibling is
	 *            null, the new element is inserted after the last element in
	 *            parent.
	 * 
	 */
	public static <T extends IInternalElement> AtomicOperation createElementGeneric(
			IInternalElement parent, final IInternalElementType<T> type,
			final IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(parent.getRoot()),
				builder.createDefaultElement(parent, type, sibling));
		op.setLabel("Create Element");
		return op;
	}

	public static AtomicOperation deleteElement(IInternalElement element) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(element), builder.deleteElement(
						element, true));
		op.setLabel("Delete Element");
		return op;
	}

	public static AtomicOperation deleteElement(IInternalElement[] elements,
			boolean force) {
		assert elements != null;
		assert elements.length > 0;
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(elements[0]), builder.deleteElement(
						elements, force));
		op.setLabel("Delete Element");
		return op;
	}

	public static RodinFileUndoContext getRodinFileUndoContext(
			IRodinFile rodinFile) {
		return new RodinFileUndoContext(rodinFile);
	}

	public static RodinFileUndoContext getRodinFileUndoContext(
			IInternalElement root) {
		return getRodinFileUndoContext(root.getRodinFile());
	}

	/**
	 * Change the attribute of a element with a factory
	 * 
	 * @param value
	 *            if value is null, the attribute is removed. Else it is changed
	 */
	public static <E extends IInternalElement> AtomicOperation changeAttribute(
			IAttributeManipulation manipulation, E element, String value) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(element.getRoot()),
				builder.changeAttribute(manipulation, element, value));
		op.setLabel("Change Attribute");
		return op;
	}

	/**
	 * Change the attribute of an element
	 * 
	 * @param value
	 *            if value is null, the attribute is removed. Else it is changed
	 */
	public static <E extends IInternalElement> AtomicOperation changeAttribute(
			E element, IAttributeValue value) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(element.getRoot()),
				builder.changeAttribute(element, value));
		op.setLabel("Change Attribute");
		return op;
	}
	
//
//	public static <T extends IInternalElement> AtomicOperation renameElements(
//			IInternalElement root, IInternalElementType<T> type,
//			IAttributeManipulation factory, String prefix) {
//		final OperationBuilder builder = new OperationBuilder();
//		final AtomicOperation op = new AtomicOperation(
//				getRodinFileUndoContext(root), builder.renameElement(root,
//						type, factory, prefix));
//		op.setLabel("Rename Element");
//		return op;
//	}
//
	
//
//	/**
//	 * Return an operation to copy elements into parent.
//	 * 
//	 * @param parent
//	 *            The parent of the new elements
//	 * 
//	 * @param elements
//	 *            an IInternalElement array. Elements to copy
//	 * 
//	 * @param sibling
//	 *            the sibling element before which the copy should be inserted,
//	 *            or <code>null</code> if the copy should be inserted as the
//	 *            last child of the container
//	 */
//	public static AtomicOperation copyElements(IInternalElement parent,
//			IRodinElement[] elements, IInternalElement sibling) {
//		final OperationBuilder builder = new OperationBuilder();
//		final AtomicOperation op = new AtomicOperation(
//				getRodinFileUndoContext(parent), builder.copyElements(parent,
//						elements, sibling));
//		op.setLabel("Copy Element");
//		return op;
//	}
//
//
	/**
	 * Create an Operation to move an element.
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
	 * @param root
	 *            the root element to get context of the Operation
	 * @param movedElement
	 *            the element to move
	 * @param newParent
	 *            the new parent of moved element
	 * @param nextSibling
	 *            the new next sibling element of moved element
	 */
	public static AtomicOperation move(IInternalElement root,
			IInternalElement movedElement, IInternalElement newParent,
			IInternalElement nextSibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.move(movedElement,
						newParent, nextSibling));
		op.setLabel("Move");
		return op;

	}
}
