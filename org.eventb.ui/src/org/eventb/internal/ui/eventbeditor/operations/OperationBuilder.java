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

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.CONVERGENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;

import java.util.Collection;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IConvergenceElement;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

class OperationBuilder {

	public OperationTree deleteElement(IInternalElement element, boolean force) {
		final OperationTree cmdCreate = getCommandCreateElement(element);
		return new DeleteElementLeaf(element, cmdCreate, force);
	}

	public OperationTree deleteElement(IInternalElement[] elements,
			boolean force) {
		OperationNode op = new OperationNode();
		for (IInternalElement element : elements) {
			op.addCommande(deleteElement(element, force));
		}
		return op;
	}

	public OperationTree createAxiom(IEventBEditor<?> editor, String[] labels,
			String[] predicates) {
		assertLengthEquals(labels, predicates);
		return createElementLabelPredicate(editor, editor.getRodinInput(),
				IAxiom.ELEMENT_TYPE, labels, predicates);
	}

	public OperationTree createConstant(IEventBEditor<?> editor,
			String identifier, String[] labels, String[] predicates) {
		final OperationNode cmd = new OperationNode();
		cmd.addCommande(createConstant(editor, identifier));
		cmd.addCommande(createAxiom(editor, labels, predicates));
		return cmd;
	}

	/**
	 * @param label
	 *            null to set a default label
	 * @param predicate
	 */
	public OperationTree createAxiom(IEventBEditor<?> editor, String label,
			String predicate) {
		return createElementLabelPredicate(editor, editor.getRodinInput(),
				IAxiom.ELEMENT_TYPE, label, predicate);
	}

	public OperationTree createConstant(IEventBEditor<?> editor,
			String identifier) {
		return createElementOneStringAttribute(editor, editor.getRodinInput(),
				IConstant.ELEMENT_TYPE, null, IDENTIFIER_ATTRIBUTE, identifier);
	}

	public OperationTree createCarrierSet(IEventBEditor<?> editor,
			String identifier) {
		return createElementOneStringAttribute(editor, editor.getRodinInput(),
				ICarrierSet.ELEMENT_TYPE, null, IDENTIFIER_ATTRIBUTE,
				identifier);
	}

	public OperationTree createCarrierSet(IEventBEditor<?> editor,
			String[] identifier) {
		return createElementOneStringAttribute(editor,
				ICarrierSet.ELEMENT_TYPE, IDENTIFIER_ATTRIBUTE, identifier);
	}

	public OperationTree createEnumeratedSet(IEventBEditor<?> editor,
			String identifier, String[] elements) {
		OperationNode cmd = new OperationNode();
		cmd.addCommande(createCarrierSet(editor, identifier));
		if (elements.length > 0) {
			cmd.addCommande(createAxiomDefinitionOfEnumeratedSet(editor,
					identifier, elements));
			cmd.addCommande(createElementsOfEnumeratedSet(editor, elements));
			cmd.addCommande(createAxiomElementsDifferentsOfEnumeratedSet(
					editor, elements));
		}
		return cmd;
	}

	public OperationTree createVariant(IEventBEditor<?> editor, String predicate) {
		return createElementOneStringAttribute(editor, editor.getRodinInput(),
				IVariant.ELEMENT_TYPE, null, EXPRESSION_ATTRIBUTE, predicate);
	}

	/**
	 * create an axiom which define an enumerated set. the axiom is "identifier = {
	 * element1,..., elementN }
	 * 
	 * @param identifier
	 *            identifier of the enumerated set
	 * @param elements
	 *            elements in the set
	 */
	private OperationTree createAxiomDefinitionOfEnumeratedSet(
			IEventBEditor<?> editor, String identifier, String[] elements) {
		final StringBuilder axmPred = new StringBuilder(identifier);
		axmPred.append(" = {");
		String axmSep = "";
		for (String element : elements) {

			axmPred.append(axmSep);
			axmSep = ", ";
			axmPred.append(element);
		}
		axmPred.append("}");
		return createAxiom(editor, null, axmPred.toString());
	}

	/**
	 * return a command which create a constant for each element in elements
	 */
	private OperationTree createElementsOfEnumeratedSet(
			IEventBEditor<?> editor, String[] elements) {
		final OperationNode cmd = new OperationNode();
		for (String element : elements) {
			cmd.addCommande(createConstant(editor, element));
		}
		return cmd;
	}

	/**
	 * return a command which create a list of axiom to specify elements of the
	 * set are all different
	 */
	private OperationTree createAxiomElementsDifferentsOfEnumeratedSet(
			IEventBEditor<?> editor, String[] elements) {
		final OperationNode cmd = new OperationNode();
		for (int i = 0; i < elements.length; ++i) {
			for (int j = i + 1; j < elements.length; ++j) {
				final String predicate = "\u00ac " + elements[i] + " = "
						+ elements[j];
				cmd.addCommande(createAxiom(editor, null, predicate));

			}
		}
		return cmd;
	}

	public OperationTree createVariable(IEventBEditor<IMachineRoot> editor,
			String varName, Collection<Pair<String, String>> invariant,
			String actName, String actSub) {
		OperationNode cmd = new OperationNode();
		cmd.addCommande(createVariable(editor, varName));
		cmd.addCommande(createInvariantList(editor, invariant));
		cmd.addCommande(createInitialisation(editor, actName, actSub));
		return cmd;
	}

	// TODO changer par operation multiple event ?
	private OperationTree createInitialisation(
			IEventBEditor<IMachineRoot> editor, String actName, String actSub) {
		return new CreateInitialisation(editor, actName, actSub);
	}

	private OperationNode createInvariantList(IEventBEditor<?> editor,
			Collection<Pair<String, String>> invariants) {
		OperationNode cmd = new OperationNode();

		if (invariants != null) {
			for (Pair<String, String> pair : invariants) {
				cmd.addCommande(createInvariant(editor, pair.getFirst(), pair
						.getSecond()));
				// new CreateInvariantWizard(editor, pair.getFirst(),
				// pair.getSecond()));
			}
		}
		return cmd;
	}

	private OperationCreateElement createVariable(IEventBEditor<?> editor,
			String identifier) {
		return createElementOneStringAttribute(editor, editor.getRodinInput(),
				IVariable.ELEMENT_TYPE, null, IDENTIFIER_ATTRIBUTE, identifier);
	}

	public OperationTree createTheorem(IEventBEditor<?> editor, String label,
			String predicate) {
		return createElementLabelPredicate(editor, editor.getRodinInput(),
				ITheorem.ELEMENT_TYPE, label, predicate);
	}

	public OperationTree createTheorem(IEventBEditor<?> editor,
			String[] labels, String[] predicates) {
		assertLengthEquals(labels, predicates);
		return createElementLabelPredicate(editor, editor.getRodinInput(),
				ITheorem.ELEMENT_TYPE, labels, predicates);
	}

	/*
	 * private <T extends IInternalElement> OperationCreateElement
	 * getOperationCreateElementWithAttribut( IEventBEditor<?> editor,
	 * IInternalElementType<T> type, EventBAttributesManager manager, boolean
	 * defaultLabel) { OperationCreateElement op = new OperationCreateElement(
	 * new CreateElementGeneric<T>(editor, editor.getRodinInput(), type,
	 * null)); op.addCommande(new ChangeAttribute(manager)); if (defaultLabel) {
	 * op.addCommande(new ChangeDefaultLabel(editor)); }
	 * 
	 * return op; }
	 */

	private <T extends IInternalElement> OperationCreateElement getCreateElement(
			IEventBEditor<?> editor, IInternalParent parent,
			IInternalElementType<T> type, IInternalElement sibling,
			EventBAttributesManager manager) {
		OperationCreateElement op = new OperationCreateElement(
				new CreateElementGeneric<T>(editor, parent, type, sibling));
		op.addSubCommande(new ChangeAttribute(manager));
		return op;
	}

	public OperationTree createInvariant(IEventBEditor<?> editor, String label,
			String predicate) {
		return createElementLabelPredicate(editor, editor.getRodinInput(),
				IInvariant.ELEMENT_TYPE, label, predicate);
	}

	public OperationNode createInvariant(IEventBEditor<?> editor,
			String[] labels, String[] predicates) {
		assertLengthEquals(labels, predicates);
		return createElementLabelPredicate(editor, editor.getRodinInput(),
				IInvariant.ELEMENT_TYPE, labels, predicates);
	}

	private OperationCreateElement createEvent(IEventBEditor<?> editor,
			String label) {
		EventBAttributesManager manager = new EventBAttributesManager();
		manager.addAttribute(LABEL_ATTRIBUTE, label);
		manager.addAttribute(CONVERGENCE_ATTRIBUTE,
				IConvergenceElement.Convergence.ORDINARY.getCode());
		return getCreateElement(editor, editor.getRodinInput(),
				IEvent.ELEMENT_TYPE, null, manager);
	}

	private void assertLengthEquals(Object[] tab1, Object[] tab2) {
		assert tab1 != null && tab2 != null;
		assert tab1.length == tab2.length;
	}

	public OperationTree createEvent(IEventBEditor<?> editor, String name,
			String[] varIdentifiers, String[] grdLabels,
			String[] grdPredicates, String[] actLabels,
			String[] actSubstitutions) {
		OperationCreateElement op = createEvent(editor, name);
		op.addSubCommande(createParameter(editor, varIdentifiers));
		op
				.addSubCommande(createElementLabelPredicate(editor, editor
						.getRodinInput(), IGuard.ELEMENT_TYPE, grdLabels,
						grdPredicates));
		op.addSubCommande(createElementTwoStringAttribute(editor, editor
				.getRodinInput(), IAction.ELEMENT_TYPE, LABEL_ATTRIBUTE,
				ASSIGNMENT_ATTRIBUTE, actLabels, actSubstitutions));
		return op;
	}

	/**
	 * Return an operation to create an IInternalElement with a string attribute
	 */
	public <T extends IInternalElement> OperationCreateElement createElementOneStringAttribute(
			IEventBEditor<?> editor, IInternalParent parent,
			IInternalElementType<T> typeElement, IInternalElement sibling,
			IAttributeType.String type, String string) {
		EventBAttributesManager manager = new EventBAttributesManager();
		manager.addAttribute(type, string);
		return getCreateElement(editor, parent, typeElement, sibling, manager);
	}

	/**
	 * retourne une operation pour plusieurs elements avec un attribut de type
	 * String
	 * 
	 */
	private <T extends IInternalElement> OperationNode createElementOneStringAttribute(
			IEventBEditor<?> editor, IInternalElementType<T> typeElement,
			IAttributeType.String type, String[] string) {
		assert string != null;
		OperationNode op = new OperationNode();
		for (int i = 0; i < string.length; i++) {
			op.addCommande(createElementOneStringAttribute(editor, editor
					.getRodinInput(), typeElement, null, type, string[i]));
		}
		return op;
	}

	/**
	 * retourne une operation pour creer un element avec deux attributs de type
	 * String
	 * 
	 */
	private <T extends IInternalElement> OperationCreateElement createElementTwoStringAttribute(
			IEventBEditor<?> editor, IInternalParent parent,
			IInternalElementType<T> typeElement, IAttributeType.String type1,
			IAttributeType.String type2, String string1, String string2) {
		EventBAttributesManager manager = new EventBAttributesManager();
		manager.addAttribute(type1, string1);
		manager.addAttribute(type2, string2);
		return getCreateElement(editor, parent, typeElement, null, manager);
	}

	/**
	 * retourne une operation pour creer plusieurs elements d'un meme type et
	 * deux attributs de type String
	 * 
	 */
	private <T extends IInternalElement> OperationNode createElementTwoStringAttribute(
			IEventBEditor<?> editor, IInternalParent parent,
			IInternalElementType<T> typeElement, IAttributeType.String type1,
			IAttributeType.String type2, String[] string1, String[] string2) {
		assertLengthEquals(string1, string2);
		OperationNode op = new OperationNode();
		for (int i = 0; i < string1.length; i++) {
			op.addCommande(createElementTwoStringAttribute(editor, parent,
					typeElement, type1, type2, string1[i], string2[i]));
		}
		return op;
	}

	/**
	 * @param label
	 *            if null the label of created element is the next free label.
	 */
	private <T extends IInternalElement> OperationCreateElement createElementLabelPredicate(
			IEventBEditor<?> editor, IInternalParent parent,
			IInternalElementType<T> type, String label, String predicate) {
		return createElementTwoStringAttribute(editor, parent, type,
				LABEL_ATTRIBUTE, PREDICATE_ATTRIBUTE, label, predicate);

	}

	private <T extends IInternalElement> OperationNode createElementLabelPredicate(
			IEventBEditor<?> editor, IInternalParent parent,
			IInternalElementType<T> type, String[] labels, String[] predicates) {
		return createElementTwoStringAttribute(editor, parent, type,
				LABEL_ATTRIBUTE, PREDICATE_ATTRIBUTE, labels, predicates);
	}

	// TODO recuperer les changements avec IParameter ( voir mail de stefan
	// Hallerstede )
	private OperationCreateElement createParameter(IEventBEditor<?> editor,
			String identifier) {
		return createElementOneStringAttribute(editor, editor.getRodinInput(),
				IParameter.ELEMENT_TYPE, null, IDENTIFIER_ATTRIBUTE, identifier);
	}

	private OperationNode createParameter(IEventBEditor<?> editor,
			String[] varIdentifiers) {
		OperationNode op = new OperationNode();
		for (String identifier : varIdentifiers) {
			op.addCommande(createParameter(editor, identifier));
		}
		return op;
	}

	/**
	 * return an Operation to create an Element with default name and label
	 * 
	 * @param editor
	 *            IEventBEditor where the new element is inserted
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
	public <T extends IInternalElement> CreateElementGeneric<T> createDefaultElement(
			IEventBEditor<?> editor, IInternalParent parent,
			IInternalElementType<T> type, IInternalElement sibling) {
		return new CreateElementGeneric<T>(editor, parent, type, sibling);
	}

	/**
	 * return a Command to create the element in parameter. if the element is
	 * delete, the Command create an identical element
	 * 
	 * @param element
	 *            an existing element
	 * @return a Command to create element
	 */
	private OperationTree getCommandCreateElement(IInternalElement element) {
		final OperationNode cmd = new OperationNode();
		cmd.addCommande(new CreateIdenticalElement(element));
		try {
			if (element.hasChildren()) {
				cmd
						.addCommande(getCommandCreateChildren(element
								.getChildren()));
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cmd;

	}

	private OperationTree getCommandCreateChildren(IRodinElement[] children)
			throws RodinDBException {
		IInternalElement element;
		final OperationNode cmd = new OperationNode();
		for (IRodinElement rodinElement : children) {
			element = (IInternalElement) rodinElement;
			cmd.addCommande(getCommandCreateElement(element));
		}
		return cmd;
	}

	public OperationTree changeAttribute(IAttributedElement element,
			EventBAttributesManager manager) {
		final ChangeAttribute op = new ChangeAttribute(element, manager);
		return op;
	}

	public <E extends IAttributedElement> OperationTree changeAttribute(
			IAttributeFactory<E> factory, E element, String value) {
		final ChangeAttributeWithFactory<E> op = new ChangeAttributeWithFactory<E>(
				factory, element, value);
		return op;
	}

	public OperationTree createGuard(IEventBEditor<?> editor,
			IInternalElement event, String label, String predicate,
			IInternalElement sibling) {
		return createElementLabelPredicate(editor, event, IGuard.ELEMENT_TYPE,
				label, predicate);
	}

	public OperationTree createAction(IEventBEditor<?> editor,
			IInternalElement event, String label, String assignement,
			IInternalElement sibling) {
		return createElementTwoStringAttribute(editor, event,
				IAction.ELEMENT_TYPE, EventBAttributes.LABEL_ATTRIBUTE,
				EventBAttributes.ASSIGNMENT_ATTRIBUTE, label, assignement);
	}

	public OperationTree createAction(IEventBEditor<?> editor,
			IInternalElement event, String[] label, String[] assignement,
			IInternalElement sibling) {
		return createElementTwoStringAttribute(editor, event,
				IAction.ELEMENT_TYPE, EventBAttributes.LABEL_ATTRIBUTE,
				EventBAttributes.ASSIGNMENT_ATTRIBUTE, label, assignement);
	}

	private OperationTree copyElement(IInternalElement parent,
			IInternalElement element) {
		return new CopyElement(parent, element);
	}

	public OperationTree copyElements(IInternalElement parent,
			IRodinElement[] elements) {
		OperationNode op = new OperationNode();
		for (IRodinElement element : elements) {
			op.addCommande(copyElement(parent, (IInternalElement) element));
		}
		return op;
	}

	public OperationTree move(IInternalElement movedElement,
			IInternalParent newParent, IInternalElement newSibling) {
		return new Move(movedElement, newParent, newSibling);
	}

	public <E extends IInternalElement> OperationTree renameElement(
			IInternalElement root, IInternalElementType<E> type,
			IAttributeFactory<E> factory, String prefix) {
		final OperationNode op = new OperationNode();
		int counter = 1;
		try {
			for (E element : root.getChildrenOfType(type)) {
				op.addCommande(changeAttribute(factory, element, prefix
						+ counter));
				counter++;
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return op;
	}
}
