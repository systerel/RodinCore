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
package org.eventb.ui.eventbeditor.operation.tests.utils;

import static org.eventb.core.EventBAttributes.CONFIGURATION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.CONVERGENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXTENDED_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.IConfigurationElement.DEFAULT_CONFIGURATION;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;

import org.eclipse.core.commands.ExecutionException;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.tests.utils.EventBUITest;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

public abstract class OperationTest extends EventBUITest {

	protected IMachineRoot mch;
	protected IEventBEditor<IMachineRoot> machineEditor;
	protected IContextRoot ctx;
	protected IEventBEditor<IContextRoot> contextEditor;

	protected Element mchElement;
	protected Element ctxElement;

	@SuppressWarnings("unchecked")
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mch = createMachine("mch");
		mch.getRodinFile().save(null, true);
		machineEditor = (IEventBEditor<IMachineRoot>) openEditor(mch);
		mchElement = getMachineElement("mch");

		ctx = createContext("ctx");
		ctx.getRodinFile().save(null, true);
		contextEditor = (IEventBEditor<IContextRoot>) openEditor(ctx);
		ctxElement = getContextElement("ctx");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Utility method to get an element representing a IMachineRoot created with
	 * createMachine(String)
	 * 
	 * @param bareName
	 *            not used
	 * @return an element.
	 */
	protected Element getMachineElement(String bareName) {
		final Element result = new Element(IMachineRoot.ELEMENT_TYPE);
		result.addAttribute(getStringAttribute(CONFIGURATION_ATTRIBUTE,
				DEFAULT_CONFIGURATION));
		return result;
	}

	/**
	 * Utility method to get an element representing a IContextRoot created with
	 * createContext(String)
	 * 
	 * @param bareName
	 *            not used
	 * @return an element.
	 */
	protected Element getContextElement(String bareName) {
		final Element result = new Element(IContextRoot.ELEMENT_TYPE);
		result.addAttribute(getStringAttribute(CONFIGURATION_ATTRIBUTE,
				DEFAULT_CONFIGURATION));
		return result;
	}

	private Attribute getStringAttribute(IAttributeType.String type,
			String value) {
		return new Attribute(type, value);
	}

	private Attribute getBooleanAttribute(IAttributeType.Boolean type,
			boolean value) {
		return new Attribute(type, value);
	}

	private Attribute getIntAttribute(IAttributeType.Integer type, int value) {
		return new Attribute(type, value);
	}

	/**
	 * Utility method to get an element representing a IEvent created with
	 * createEvent(IMachineRoot, String);
	 * 
	 * @param parent
	 *            the parent of the new element. <code>parent.getType()</code>
	 *            must return <code>IMachineRoot.ELEMENT_TYPE</code>
	 * @param eventLabel
	 *            the label of the new event.
	 * @return the newly created element.
	 */
	protected Element addEventElement(Element parent, String eventLabel) {
		assert parent.getType() == IMachineRoot.ELEMENT_TYPE;
		final Element result = new Element(IEvent.ELEMENT_TYPE);
		result.addAttribute(getStringAttribute(LABEL_ATTRIBUTE, eventLabel));
		result.addAttribute(getBooleanAttribute(EXTENDED_ATTRIBUTE, false));
		result.addAttribute(getIntAttribute(CONVERGENCE_ATTRIBUTE, ORDINARY
				.getCode()));
		parent.addChild(result, null);
		return result;
	}

	protected Element addInvariant(Element parent, String label,
			String predicate) {
		return addElementWithLabelPredicate(parent, IInvariant.ELEMENT_TYPE,
				label, predicate, false);
	}

	protected Element addVariant(Element parent, String expression) {
		return addElementWithStringAttribute(parent, IVariant.ELEMENT_TYPE,
				EventBAttributes.EXPRESSION_ATTRIBUTE, expression);
	}

	/**
	 * Created a new Element representing an IAction with the given label and
	 * assignment.
	 * <p>
	 * The new Element is added to parent's children.
	 * 
	 * @param parent
	 *            An Element. Created element is added to parent's children.
	 * 
	 * <code>parent.getType()</code> must return
	 * <code>IEvent.ELEMENT_TYPE</code>.
	 * 
	 * Must not be <code>null</code>.
	 * @param label
	 *            A string. Value of
	 *            <code>EventBAttributes.LABEL_ATTRIBUTE</code>.
	 * 
	 * Must not be <code>null</code>.
	 * @param assignment
	 *            A string. Value of
	 *            <code>EventBAttributes.ASSIGNMENT_ATTRIBUTE</code>.
	 * 
	 * Must not be <code>null</code>.
	 * @return the created element
	 */
	protected Element addAction(Element parent, String label, String assignment) {
		assert parent.getType() == IEvent.ELEMENT_TYPE;
		Element result = new Element(IAction.ELEMENT_TYPE);
		result.addAttribute(getStringAttribute(
				EventBAttributes.LABEL_ATTRIBUTE, label));
		result.addAttribute(getStringAttribute(
				EventBAttributes.ASSIGNMENT_ATTRIBUTE, assignment));
		parent.addChild(result, null);
		return result;
	}

	protected void addAction(Element parent, String[] labels,
			String[] assignements) {
		assert labels.length == assignements.length;
		for (int i = 0; i < labels.length; i++) {
			addAction(parent, labels[i], assignements[i]);
		}
	}

	/**
	 * Created a new Element representing an IInternalElement of the given type.
	 * <p>
	 * The created element has a label and a predicate, and is added to parent's
	 * children
	 * 
	 * @param parent
	 *            An Element. Created element is added to parent's children.
	 * 
	 * Must not be <code>null</code>.
	 * @param type
	 *            An IInternalElementType. Must not be <code>null</code>.
	 * @param label
	 *            A string. Value of
	 *            <code>EventBAttributes.LABEL_ATTRIBUTE</code>.
	 * 
	 * Must not be <code>null</code>.
	 * @param predicate
	 *            A string. Value of
	 *            <code>EventBAttributes.PREDICATE_ATTRIBUTE</code>. Must not
	 *            be <code>null</code>.
	 * @param isTheorem
	 *            A boolean. Value of
	 *            <code>EventBAttributes.THEOREM_ATTRIBUTE</code>. Must not
	 *            be <code>null</code>.
	 * @return the created Element
	 */
	protected Element addElementWithLabelPredicate(Element parent,
			IInternalElementType<?> type, String label, String predicate,
			boolean isTheorem) {
		final Element result = new Element(type);
		result.addAttribute(getStringAttribute(
				EventBAttributes.LABEL_ATTRIBUTE, label));
		result.addAttribute(getStringAttribute(
				EventBAttributes.PREDICATE_ATTRIBUTE, predicate));
		result.addAttribute(getBooleanAttribute(
				EventBAttributes.THEOREM_ATTRIBUTE, isTheorem));
		parent.addChild(result, null);
		return result;
	}

	/**
	 * Created a new Element representing an IInternalElement of the given type.
	 * The created element has a identifier and is added to parent's children
	 * 
	 * @param parent
	 *            An Element. Created element is added to parent's children.
	 *            Must not be <code>null</code>
	 * @param type
	 *            An IInternalElementType. Must not be <code>null</code>
	 * @param identifier
	 *            A string. Value of
	 *            <code>EventBAttributes.IDENTIFIER_ATTRIBUTE</code>. Must
	 *            not be <code>null</code>
	 * @return the created Element
	 */
	protected Element addElementWithIdentifier(Element parent,
			IInternalElementType<?> type, String identifier) {
		Element result = new Element(type);
		result.addAttribute(getStringAttribute(
				EventBAttributes.IDENTIFIER_ATTRIBUTE, identifier));
		parent.addChild(result, null);
		return result;
	}

	/**
	 * Created a new Element representing an IInternalElement of the given type.
	 * The created element has a string attribute and is added to parent's
	 * children
	 * 
	 * @param parent
	 *            An Element. Created element is added to parent's children.
	 *            Must not be <code>null</code>
	 * @param type
	 *            An IInternalElementType. Must not be <code>null</code>
	 * @param attribute
	 *            a IAttributeType.String. Must not be <code>null</code>
	 * @param value
	 *            A string. Value of the given attribute. Must not be
	 *            <code>null</code>
	 * @return the created Element
	 */
	protected Element addElementWithStringAttribute(Element parent,
			IInternalElementType<?> type, IAttributeType.String attribute,
			String value) {
		Element result = new Element(type);
		result.addAttribute(getStringAttribute(attribute, value));
		parent.addChild(result, null);
		return result;
	}

	/**
	 * Created new Elements representing IInternalElement of the given type. The
	 * created elements have an identifier and are added to parent's children
	 * 
	 * @param parent
	 *            An Element. Created elements are added to parent's children.
	 *            Must not be <code>null</code>
	 * @param type
	 *            An IInternalElementType. Must not be <code>null</code>
	 * @param identifiers
	 *            An array of string. Value of
	 *            <code>EventBAttributes.IDENTIFIER_ATTRIBUTE</code>. Must
	 *            not be <code>null</code>
	 */
	protected void addElementWithIdentifier(Element parent,
			IInternalElementType<?> type, String[] identifiers) {
		for (String id : identifiers) {
			addElementWithIdentifier(parent, type, id);
		}
	}

	/**
	 * Created new Elements representing IInternalElement of the given type.
	 * Created elements have a label and a predicate, and are added to parent's
	 * children
	 * 
	 * @param parent
	 *            An Element. Created elements are added to parent's children.
	 *            Must not be <code>null</code>
	 * @param type
	 *            An IInternalElementType. Must not be <code>null</code>
	 * @param labels
	 *            An array of String. Value of
	 *            <code>EventBAttributes.LABEL_ATTRIBUTE</code>. Must have
	 *            the same length as predicates and must not be
	 *            <code>null</code>
	 * @param predicates
	 *            An array of String. Value of
	 *            <code>EventBAttributes.PREDICATE_ATTRIBUTE</code>. Must
	 *            have the same length as labels and must not be
	 *            <code>null</code>
	 * @param isTheorem
	 *            An array of booleans. Value of
	 *            <code>EventBAttributes.THEOREM_ATTRIBUTE</code>. Must
	 *            have the same length as labels and must not be
	 *            <code>null</code>
	 */
	protected void addElementWithLabelPredicate(Element parent,
			IInternalElementType<?> type, String[] labels, String[] predicates,
			boolean[] isTheorem) {
		assert labels.length == predicates.length;
		for (int i = 0; i < labels.length; i++) {
			addElementWithLabelPredicate(parent, type, labels[i],
					predicates[i], isTheorem[i]);
		}
	}

	/**
	 * ensures that element is equivalent to root. An Element and a
	 * IInternalElement is equivalent if they have the same type, the same
	 * attributes and all children are equivalents.
	 * 
	 * @param msg
	 *            message to print if an error occurs
	 * @param root
	 *            an IInternalElement
	 * @param expected
	 *            an Element representing the expected value
	 * @throws RodinDBException
	 */
	protected void assertEquivalent(String msg, IInternalElement root,
			Element expected) throws RodinDBException {
		Element actual = Element.valueOf(root);
		if (!expected.equals(actual))
			fail(msg + "\nexpected :\n" + expected + "\nbut was :\n" + actual);
	}

	/**
	 * ensures that element is not equivalent to root. An Element and a
	 * IInternalElement is equivalent if they have the same type, the same
	 * attributes and all children are equivalents.
	 * 
	 * @param msg
	 *            message to print if an error occurs
	 * @param root
	 *            an IInternalElement
	 * @param element
	 *            an Element is a tree that represents an IInternalElement
	 * @throws RodinDBException
	 */
	protected void assertNotEquivalent(String msg, IInternalElement root,
			Element element) throws RodinDBException {
		assertFalse(msg, element.equals(Element.valueOf(root)));
	}

	protected void verifyOperation(AtomicOperation op, IInternalElement root,
			Element element) throws RodinDBException, ExecutionException {

		Element elementUndo = Element.valueOf(root);

		op.execute(null, null);
		assertEquivalent("Error when execute an operation", root, element);

		op.undo(null, null);
		assertEquivalent("Error when undo an operation", root, elementUndo);

		op.redo(null, null);
		assertEquivalent("Error when redo an operation", root, element);
	}
}
