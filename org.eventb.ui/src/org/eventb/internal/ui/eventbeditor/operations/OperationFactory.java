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
package org.eventb.internal.ui.eventbeditor.operations;

import java.util.Collection;

import org.eclipse.core.commands.operations.IUndoContext;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

public class OperationFactory {

	private OperationFactory() {
		// non instanciable class
	}

	public static AtomicOperation createTheoremWizard(IInternalElement parent,
			String label, String content) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.createTheorem(
				parent, label, content));
		op.addContext(getContext(parent));
		op.setLabel("Create Theorem");
		return op;
	}

	public static AtomicOperation createTheoremWizard(IInternalElement root,
			String[] labels, String[] contents) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.createTheorem(
				root, labels, contents));
		op.addContext(getContext(root));
		op.setLabel("Create Theorem");
		return op;
	}

	/**
	 * @param label
	 *            null to set a default label
	 * @param predicate
	 */
	public static AtomicOperation createAxiomWizard(IContextRoot root,
			String label, String predicate) {
		final OperationBuilder builder = new OperationBuilder();
		AtomicOperation op = new AtomicOperation(builder.createAxiom(root,
				label, predicate));
		op.addContext(getContext(root));
		op.setLabel("Create Theorem");
		return op;
	}

	public static AtomicOperation createAxiomWizard(IContextRoot root,
			String[] labels, String[] predicates) {
		final OperationBuilder builder = new OperationBuilder();
		AtomicOperation op = new AtomicOperation(builder.createAxiom(root,
				labels, predicates));
		op.addContext(getContext(root));
		op.setLabel("Create Axiom");
		return op;
	}

	public static AtomicOperation createConstantWizard(IContextRoot root,
			String identifier, String[] labels, String[] predicates) {
		final AtomicOperation op;
		final OperationBuilder builder = new OperationBuilder();
		op = new AtomicOperation(builder.createConstant(root, identifier,
				labels, predicates));
		op.addContext(getContext(root));
		op.setLabel("Create Constant");
		return op;
	}

	/**
	 * Elements are added in the following order : carrier set, constant, axiom
	 * set = {element1,...}, axiom element are not equals
	 */
	public static AtomicOperation createEnumeratedSetWizard(IContextRoot root,
			String identifier, String[] elements) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder
				.createEnumeratedSet(root, identifier, elements));
		op.addContext(getContext(root));
		op.setLabel("Create Enumerated Set");
		return op;

	}

	public static AtomicOperation createVariantWizard(IMachineRoot root,
			String expression) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.createVariant(
				root, expression));
		op.addContext(getContext(root));
		op.setLabel("Create Variant");
		return op;
	}

	public static AtomicOperation createVariableWizard(final IMachineRoot root,
			final String varName,
			final Collection<Pair<String, String>> invariant,
			final String actName, final String actSub) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.createVariable(
				root, varName, invariant, actName, actSub));
		op.addContext(getContext(root));
		op.setLabel("Create Constant");
		return op;
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
		final AtomicOperation op = new AtomicOperation(builder.createInvariant(
				root, label, content));
		op.addContext(getContext(root));
		op.setLabel("Create Invariant");
		return op;
	}

	public static AtomicOperation createInvariantWizard(IMachineRoot root,
			String[] labels, String[] contents) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.createInvariant(
				root, labels, contents));
		op.addContext(getContext(root));
		op.setLabel("Create Invariant");
		return op;
	}

	public static AtomicOperation createCarrierSetWizard(IContextRoot root,
			String identifier) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder
				.createCarrierSet(root, identifier));
		op.addContext(getContext(root));
		op.setLabel("Create Carrier set");
		return op;
	}

	public static AtomicOperation createCarrierSetWizard(IContextRoot root,
			String[] identifier) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder
				.createCarrierSet(root, identifier));
		op.addContext(getContext(root));
		op.setLabel("Create Carrier Set");
		return op;
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
		final AtomicOperation op = new AtomicOperation(builder
				.createDefaultElement(parent, type, sibling));
		op.addContext(getContext(parent));
		op.setLabel("Create Element");
		return op;
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
		final AtomicOperation op = new AtomicOperation(builder.createEvent(
				root, name, varNames, grdNames, grdPredicates, actNames,
				actSubstitutions));
		op.addContext(getContext(root));
		op.setLabel("Create Event");
		return op;
	}

	public static AtomicOperation deleteElement(IInternalElement element) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.deleteElement(
				element, true));
		op.addContext(getContext(element));
		op.setLabel("Delete Element");
		return op;
	}

	public static AtomicOperation deleteElement(IInternalElement[] elements,
			boolean force) {
		assert elements != null;
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.deleteElement(
				elements, force));
		if (elements.length > 0) {
			op.addContext(getContext(elements[0]));
		}
		op.setLabel("Delete Element");
		return op;
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
	public static <E extends IInternalElement> AtomicOperation changeAttribute(
			IRodinFile file, IAttributeManipulation factory, E element,
			String value) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.changeAttribute(
				factory, element, value));
		op.addContext(getContext(file));
		op.setLabel("Change Attribute");
		return op;
	}

	public static <T extends IInternalElement> AtomicOperation renameElements(
			IInternalElement parent, IInternalElementType<T> type,
			IAttributeManipulation factory, String prefix) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.renameElement(
				parent, type, factory, prefix));
		op.addContext(getContext(parent));
		op.setLabel("Rename Element");
		return op;
	}

	public static AtomicOperation createGuard(IInternalElement event,
			String label, String predicate, IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.createGuard(
				event, label, predicate, sibling));
		op.addContext(getContext(event));
		op.setLabel("Create Guard");
		return op;
	}

	public static AtomicOperation createAction(IInternalElement event,
			String label, String assignement, IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.createAction(
				event, label, assignement, sibling));
		op.addContext(getContext(event));
		op.setLabel("Create Action");
		return op;
	}

	public static AtomicOperation createAction(IInternalElement event,
			String label[], String predicate[], IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.createAction(
				event, label, predicate, sibling));
		op.addContext(getContext(event));
		op.setLabel("Create Action");
		return op;
	}

	/**
	 * If all the element into parent
	 */
	public static AtomicOperation copyElements(IInternalElement parent,
			IRodinElement[] elements) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.copyElements(
				parent, elements));
		op.addContext(getContext(parent));
		op.setLabel("Copy Element");
		return op;
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
		op.setLabel("Create Element");
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
			IInternalElement movedElement, IInternalElement newParent,
			IInternalElement nextSibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(builder.move(
				movedElement, newParent, nextSibling));
		op.addContext(getContext(root));
		op.setLabel("Move");
		return op;

	}
}
