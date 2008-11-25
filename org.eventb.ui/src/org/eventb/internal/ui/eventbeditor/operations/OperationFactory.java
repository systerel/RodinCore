/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.operations;

import java.util.Collection;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

public class OperationFactory {

	private OperationFactory() {
		// non instanciable class
	}

	public static AtomicOperation createTheoremWizard(IInternalElement parent,
			String label, String content) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createTheorem(
				parent, label, content));
		cmd.addContext(getContext(parent));
		return cmd;
	}

	public static AtomicOperation createTheoremWizard(IInternalElement root,
			String[] labels, String[] contents) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createTheorem(
				root, labels, contents));
		cmd.addContext(getContext(root));
		return cmd;
	}

	/**
	 * @param label
	 *            null to set a default label
	 * @param predicate
	 */
	public static AtomicOperation createAxiomWizard(IContextRoot root,
			String label, String predicate) {
		final OperationBuilder builder = new OperationBuilder();
		AtomicOperation cmd = new AtomicOperation(builder.createAxiom(root,
				label, predicate));
		cmd.addContext(getContext(root));
		return cmd;
	}

	public static AtomicOperation createAxiomWizard(IContextRoot root,
			String[] labels, String[] predicates) {
		final OperationBuilder builder = new OperationBuilder();
		AtomicOperation cmd = new AtomicOperation(builder.createAxiom(root,
				labels, predicates));
		cmd.addContext(getContext(root));
		return cmd;
	}

	public static AtomicOperation createConstantWizard(IContextRoot root,
			String identifier, String[] labels, String[] predicates) {
		final AtomicOperation cmd;
		final OperationBuilder builder = new OperationBuilder();
		cmd = new AtomicOperation(builder.createConstant(root, identifier,
				labels, predicates));
		cmd.addContext(getContext(root));
		return cmd;
	}

	/**
	 * Elements are added in the following order : carrier set, constant, axiom
	 * set = {element1,...}, axiom element are not equals
	 */
	public static AtomicOperation createEnumeratedSetWizard(IContextRoot root,
			String identifier, String[] elements) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createEnumeratedSet(root, identifier, elements));
		cmd.addContext(getContext(root));
		return cmd;

	}

	public static AtomicOperation createVariantWizard(IMachineRoot root,
			String expression) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createVariant(
				root, expression));
		cmd.addContext(getContext(root));
		return cmd;
	}

	public static AtomicOperation createVariableWizard(final IMachineRoot root,
			final String varName,
			final Collection<Pair<String, String>> invariant,
			final String actName, final String actSub) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createVariable(
				root, varName, invariant, actName, actSub));
		cmd.addContext(getContext(root));
		return cmd;
	}

	/**
	 * return an Operation to create an Element with default name and label
	 * 
	 * @param root
	 *            root element the new element is inserted
	 * @param label
	 *            if null the label of created element is the next free label.
	 * @param content
	 *            the predicate
	 */
	public static AtomicOperation createInvariantWizard(IMachineRoot root,
			String label, String content) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createInvariant(root, label, content));
		cmd.addContext(getContext(root));
		return cmd;
	}

	public static AtomicOperation createInvariantWizard(IMachineRoot root,
			String[] labels, String[] contents) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createInvariant(root, labels, contents));
		cmd.addContext(getContext(root));
		return cmd;
	}

	public static AtomicOperation createCarrierSetWizard(IContextRoot root,
			String identifier) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createCarrierSet(root, identifier));
		cmd.addContext(getContext(root));
		return cmd;
	}

	public static AtomicOperation createCarrierSetWizard(IContextRoot root,
			String[] identifier) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.createCarrierSet(root, identifier));
		cmd.addContext(getContext(root));
		return cmd;
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
		final AtomicOperation cmd = new AtomicOperation(builder
				.createDefaultElement(parent, type, sibling));
		cmd.addContext(getContext(parent));
		return cmd;
	}

	/**
	 * 
	 * grNames and grdPredicates must be not null and have the same length
	 * <p>
	 * varNames and varSubstitutions must be not null and have the same length
	 * 
	 * @param root
	 *            root element of parent file
	 * @param name
	 *            name of the element
	 * @param varNames
	 *            variables name
	 * @param grdNames
	 *            guards name
	 * @param grdPredicates
	 *            guards predicate
	 * @param actNames
	 *            actions name
	 * @param actSubstitutions
	 *            actions substitution
	 */
	public static AtomicOperation createEvent(IMachineRoot root, String name,
			String[] varNames, String[] grdNames, String[] grdPredicates,
			String[] actNames, String[] actSubstitutions) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createEvent(
				root, name, varNames, grdNames, grdPredicates, actNames,
				actSubstitutions));
		cmd.addContext(getContext(root));
		return cmd;
	}

	public static AtomicOperation deleteElement(IInternalElement element) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.deleteElement(
				element, true));
		cmd.addContext(getContext(element));
		return cmd;
	}

	public static AtomicOperation deleteElement(IInternalElement[] elements,
			boolean force) {
		assert elements != null;
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.deleteElement(
				elements, force));
		if (elements.length > 0) {
			cmd.addContext(getContext(elements[0]));
		}
		return cmd;
	}

	private static IUndoContext getContext(IRodinFile rodinFile) {
		final String bareName = rodinFile.getBareName();

		return new IUndoContext() {

			public String getLabel() {
				return bareName;
			}

			public boolean matches(IUndoContext context) {
				return getLabel().equals(context.getLabel());
			}

		};
	}

	public static IUndoContext getContext(IInternalElement root) {
		return getContext(root.getRodinFile());
	}

	/**
	 * Change the attribute of a element with a factory
	 * 
	 * @param value
	 *            if value is null, the attribute is removed. Else it is changed
	 */
	public static <E extends IAttributedElement> AtomicOperation changeAttribute(
			IRodinFile file, IAttributeFactory<E> factory, E element,
			String value) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder
				.changeAttribute(factory, element, value));
		cmd.addContext(getContext(file));
		return cmd;
	}

	public static <E extends IInternalElement, T extends E> AtomicOperation renameElements(
			IInternalElement parent, IInternalElementType<T> type,
			IAttributeFactory<E> factory, String prefix) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.renameElement(
				parent, type, factory, prefix));
		op.addContext(getContext(parent));
		return op;
	}

	public static AtomicOperation createGuard(IInternalElement event,
			String label, String predicate, IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createGuard(
				event, label, predicate, sibling));
		cmd.addContext(getContext(event));
		return cmd;
	}

	public static AtomicOperation createAction(IInternalElement event,
			String label, String assignement, IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createAction(
				event, label, assignement, sibling));
		cmd.addContext(getContext(event));
		return cmd;
	}

	public static AtomicOperation createAction(IInternalElement event,
			String label[], String predicate[], IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.createAction(
				event, label, predicate, sibling));
		cmd.addContext(getContext(event));
		return cmd;
	}

	/**
	 * If all the element into parent
	 */
	public static AtomicOperation copyElements(IInternalElement parent,
			IRodinElement[] elements) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation cmd = new AtomicOperation(builder.copyElements(
				parent, elements));
		cmd.addContext(getContext(parent));
		return cmd;
	}

	/**
	 * Return an operation to create an IInternalElement with the given type and
	 * a string attribute.
	 * 
	 * @param parent
	 *            The parent of the new element is
	 * 
	 * 
	 * @param internalElementType
	 *            an IInternalElementType\<T\>. Type of the element to create
	 * @param attribute
	 *            an IAttributeType.String
	 * @param value
	 *            a String. The value of the attribute
	 */
	public static <T extends IInternalElement> AtomicOperation createElement(
			IInternalElement parent,
			IInternalElementType<T> internalElementType,
			IAttributeType.String attribute, String value) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder
				.createElementOneStringAttribute(parent, internalElementType,
						null, attribute, value));
		op.addContext(getContext(parent));
		return op;
	}

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
			IInternalElement movedElement, IInternalParent newParent,
			IInternalElement nextSibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.move(
				movedElement, newParent, nextSibling));
		op.addContext(getContext(root));
		return op;

	}
}
