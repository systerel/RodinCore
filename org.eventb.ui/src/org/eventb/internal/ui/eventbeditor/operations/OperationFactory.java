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

	/**
	 * return an Operation to create an axiom
	 * 
	 * @param root
	 *            root element in which the new element is inserted
	 * @param label
	 *            if null the label of created element is the next free label.
	 * @param predicate
	 *            the predicate
	 * @param isTheorem
	 *            true if the axiom is a theorem
	 */
	public static AtomicOperation createAxiomWizard(IContextRoot root,
			String label, String predicate, boolean isTheorem) {
		final OperationBuilder builder = new OperationBuilder();
		AtomicOperation op = new AtomicOperation(getRodinFileUndoContext(root),
				builder.createAxiom(root, label, predicate, isTheorem));
		op.setLabel("Create Axiom");
		return op;
	}

	/**
	 * return an Operation to create a list of axioms
	 * 
	 * @param root
	 *            root element in which the new element is inserted
	 * @param labels
	 *            the labels.
	 * @param predicates
	 *            the predicates
	 * @param isTheorem
	 *            true if the axioms is a theorem
	 */
	public static AtomicOperation createAxiomWizard(IContextRoot root,
			String[] labels, String[] predicates, boolean[] isTheorem) {
		final OperationBuilder builder = new OperationBuilder();
		AtomicOperation op = new AtomicOperation(getRodinFileUndoContext(root),
				builder.createAxiom(root, labels, predicates, isTheorem));
		op.setLabel("Create Axiom");
		return op;
	}

	public static AtomicOperation createConstantWizard(IContextRoot root,
			String identifier, String[] labels, String[] predicates) {
		final AtomicOperation op;
		final OperationBuilder builder = new OperationBuilder();
		op = new AtomicOperation(getRodinFileUndoContext(root), builder
				.createConstant(root, identifier, labels, predicates));
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
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.createEnumeratedSet(
						root, identifier, elements));
		op.setLabel("Create Enumerated Set");
		return op;

	}

	public static AtomicOperation createVariantWizard(IMachineRoot root,
			String expression) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.createVariant(root,
						expression));
		op.setLabel("Create Variant");
		return op;
	}

	public static AtomicOperation createVariableWizard(final IMachineRoot root,
			final String varName,
			final Collection<Pair<String, String>> invariant,
			final String actName, final String actSub) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.createVariable(root,
						varName, invariant, actName, actSub));
		op.setLabel("Create Constant");
		return op;
	}

	/**
	 * return an Operation to create an invariant
	 * 
	 * @param root
	 *            root element in which the new element is inserted
	 * @param label
	 *            if null the label of created element is the next free label.
	 * @param content
	 *            the predicate
	 * @param isTheorem
	 *            true if the invariant is a theorem
	 */
	public static AtomicOperation createInvariantWizard(IMachineRoot root,
			String label, String content, boolean isTheorem) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.createInvariant(root,
						label, content, isTheorem));
		op.setLabel("Create Invariant");
		return op;
	}

	/**
	 * return an Operation to create a list of invariants
	 * 
	 * @param root
	 *            root element in which the new element is inserted
	 * @param labels
	 *            the labels.
	 * @param contents
	 *            the predicates
	 * @param isTheorem
	 *            true if the invariant is a theorem
	 */
	public static AtomicOperation createInvariantWizard(IMachineRoot root,
			String[] labels, String[] contents, boolean[] isTheorem) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.createInvariant(root,
						labels, contents, isTheorem));
		op.setLabel("Create Invariant");
		return op;
	}

	public static AtomicOperation createCarrierSetWizard(IContextRoot root,
			String identifier) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.createCarrierSet(root,
						identifier));
		op.setLabel("Create Carrier set");
		return op;
	}

	public static AtomicOperation createCarrierSetWizard(IContextRoot root,
			String[] identifier) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.createCarrierSet(root,
						identifier));
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
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(parent), builder.createDefaultElement(
						parent, type, sibling));
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
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.createEvent(root, name,
						varNames, grdNames, grdPredicates, actNames,
						actSubstitutions));
		op.setLabel("Create Event");
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

	private static RodinFileUndoContext getRodinFileUndoContext(
			IRodinFile rodinFile) {
		return new RodinFileUndoContext(rodinFile);
	}

	private static RodinFileUndoContext getRodinFileUndoContext(
			IInternalElement root) {
		return getRodinFileUndoContext(root.getRodinFile());
	}

	public static IUndoContext getContext(IInternalElement root) {
		return getRodinFileUndoContext(root.getRodinFile());
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
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(file), builder.changeAttribute(factory,
						element, value));
		op.setLabel("Change Attribute");
		return op;
	}

	public static <T extends IInternalElement> AtomicOperation renameElements(
			IInternalElement root, IInternalElementType<T> type,
			IAttributeManipulation factory, String prefix) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.renameElement(root,
						type, factory, prefix));
		op.setLabel("Rename Element");
		return op;
	}

	public static AtomicOperation createGuard(IInternalElement event,
			String label, String predicate, IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(event), builder.createGuard(event,
						label, predicate, sibling));
		op.setLabel("Create Guard");
		return op;
	}

	public static AtomicOperation createAction(IInternalElement event,
			String label, String assignement, IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(event), builder.createAction(event,
						label, assignement, sibling));
		op.setLabel("Create Action");
		return op;
	}

	public static AtomicOperation createAction(IInternalElement event,
			String label[], String predicate[], IInternalElement sibling) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(event), builder.createAction(event,
						label, predicate, sibling));
		op.setLabel("Create Action");
		return op;
	}

	/**
	 * If all the element into parent
	 */
	public static AtomicOperation copyElements(IInternalElement parent,
			IRodinElement[] elements) {
		final OperationBuilder builder = new OperationBuilder();
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(parent), builder.copyElements(parent,
						elements));
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
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(parent), builder
						.createElementOneStringAttribute(parent,
								internalElementType, null, attribute, value));
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
		final AtomicOperation op = new AtomicOperation(
				getRodinFileUndoContext(root), builder.move(movedElement,
						newParent, nextSibling));
		op.setLabel("Move");
		return op;

	}
}
