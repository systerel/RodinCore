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

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.CONVERGENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.THEOREM_ATTRIBUTE;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;

import java.util.Arrays;
import java.util.Collection;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.Pair;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

class OperationBuilder {

	private boolean[] makeFilledArray(int length, boolean value) {
		final boolean[] result = new boolean[length];
		Arrays.fill(result, value);
		return result;
	}

	private static final String KEYWORD_PARTITION = "partition";
	
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

	public OperationTree createAxiom(IInternalElement root, String[] labels,
			String[] predicates, boolean[] isTheorem) {
		assertLengthEquals(labels, predicates);
		return createElementLabelPredicate(root, IAxiom.ELEMENT_TYPE, labels,
				predicates, isTheorem);
	}

	public OperationTree createConstant(IInternalElement root,
			String identifier, String[] labels, String[] predicates) {
		final OperationNode cmd = new OperationNode();
		cmd.addCommande(createConstant(root, identifier));
		final boolean[] isTheorem = makeFilledArray(labels.length, false);
		cmd.addCommande(createAxiom(root, labels, predicates, isTheorem));
		return cmd;
	}

	/**
	 * @param label
	 *            null to set a default label
	 * @param predicate
	 */
	public OperationTree createAxiom(IInternalElement root, String label,
			String predicate, boolean isTheorem) {
		return createElementLabelPredicate(root, IAxiom.ELEMENT_TYPE, label,
				predicate, isTheorem);
	}

	public OperationTree createConstant(IInternalElement root, String identifier) {
		return createElementOneStringAttribute(root, IConstant.ELEMENT_TYPE,
				null, IDENTIFIER_ATTRIBUTE, identifier);
	}

	public OperationTree createCarrierSet(IInternalElement root,
			String identifier) {
		return createElementOneStringAttribute(root, ICarrierSet.ELEMENT_TYPE,
				null, IDENTIFIER_ATTRIBUTE, identifier);
	}

	public OperationTree createCarrierSet(IInternalElement root,
			String[] identifier) {
		return createElementOneStringAttribute(root, ICarrierSet.ELEMENT_TYPE,
				IDENTIFIER_ATTRIBUTE, identifier);
	}

	public OperationTree createEnumeratedSet(IInternalElement root,
			String identifier, String[] elements) {
		OperationNode cmd = new OperationNode();
		cmd.addCommande(createCarrierSet(root, identifier));
		if (elements.length > 0) {
			cmd.addCommande(createElementsOfEnumeratedSet(root, elements));
			cmd.addCommande(createPartition(root, identifier, elements));
		}
		return cmd;
	}

	public OperationTree createVariant(IInternalElement root, String predicate) {
		return createElementOneStringAttribute(root, IVariant.ELEMENT_TYPE,
				null, EXPRESSION_ATTRIBUTE, predicate);
	}

	/**
	 * create an axiom which define an enumerated set. the axiom is
	 * "partition(Set, {element1},..., {elementN})
	 * 
	 * @param identifier
	 *            identifier of the enumerated set
	 * @param elements
	 *            elements in the set
	 */
	private OperationTree createPartition(IInternalElement root,
			String identifier, String[] elements) {
		final StringBuilder axmPred = new StringBuilder(KEYWORD_PARTITION);
		axmPred.append("(");
		axmPred.append(identifier);
		for (String element : elements) {
			axmPred.append(", {");
			axmPred.append(element);
			axmPred.append('}');
		}
		axmPred.append(")");
		return createAxiom(root, null, axmPred.toString(), false);
	}

	/**
	 * return a command which create a constant for each element in elements
	 */
	private OperationTree createElementsOfEnumeratedSet(IInternalElement root,
			String[] elements) {
		final OperationNode cmd = new OperationNode();
		for (String element : elements) {
			cmd.addCommande(createConstant(root, element));
		}
		return cmd;
	}

	public OperationTree createVariable(IMachineRoot root, String varName,
			Collection<Pair<String, String>> invariant, String actName,
			String actSub) {
		OperationNode cmd = new OperationNode();
		cmd.addCommande(createVariable(root, varName));
		cmd.addCommande(createInvariantList(root, invariant));
		cmd.addCommande(createInitialisation(root, actName, actSub));
		return cmd;
	}

	// TODO changer par operation multiple event ?
	private OperationTree createInitialisation(IMachineRoot root,
			String actName, String actSub) {
		return new CreateInitialisation(root, actName, actSub);
	}

	/**
	 * Return an Operation to create a list of invariants.
	 * <p>
	 * The theorem attribute is implicitly <code>false</code>.
	 * </p>
	 */
	private OperationNode createInvariantList(IMachineRoot root,
			Collection<Pair<String, String>> invariants) {
		OperationNode cmd = new OperationNode();

		if (invariants != null) {
			for (Pair<String, String> pair : invariants) {
				cmd.addCommande(createInvariant(root, pair.getFirst(), pair
						.getSecond(), false));
			}
		}
		return cmd;
	}

	private OperationCreateElement createVariable(IMachineRoot root,
			String identifier) {
		return createElementOneStringAttribute(root, IVariable.ELEMENT_TYPE,
				null, IDENTIFIER_ATTRIBUTE, identifier);
	}

	private <T extends IInternalElement> OperationCreateElement getCreateElement(
			IInternalElement parent, IInternalElementType<T> type,
			IInternalElement sibling, IAttributeValue[] values) {
		OperationCreateElement op = new OperationCreateElement(
				createDefaultElement(parent, type, sibling));
		op.addSubCommande(new ChangeAttribute(values));
		return op;
	}

	public OperationTree createInvariant(IMachineRoot root, String label,
			String predicate, boolean isTheorem) {
		return createElementLabelPredicate(root, IInvariant.ELEMENT_TYPE,
				label, predicate, isTheorem);
	}

	public OperationNode createInvariant(IMachineRoot root, String[] labels,
			String[] predicates, boolean[] isTheorem) {
		assertLengthEquals(labels, predicates);
		return createElementLabelPredicate(root, IInvariant.ELEMENT_TYPE,
				labels, predicates, isTheorem);
	}

	private OperationCreateElement createEvent(IMachineRoot root, String label) {
		final IAttributeValue[] values = new IAttributeValue[] {
				LABEL_ATTRIBUTE.makeValue(label),
				CONVERGENCE_ATTRIBUTE.makeValue(ORDINARY.getCode()), //
		};
		return getCreateElement(root, IEvent.ELEMENT_TYPE, null, values);
	}

	private void assertLengthEquals(Object[] tab1, Object[] tab2) {
		assert tab1 != null && tab2 != null;
		assert tab1.length == tab2.length;
	}

	public OperationTree createEvent(IMachineRoot root, String name,
			String[] varIdentifiers, String[] grdLabels,
			String[] grdPredicates, String[] actLabels,
			String[] actSubstitutions) {
		OperationCreateElement op = createEvent(root, name);
		op.addSubCommande(createParameter(root, varIdentifiers));
		final boolean[] isTheorem = makeFilledArray(grdLabels.length, false);
		op.addSubCommande(createElementLabelPredicate(root,
				IGuard.ELEMENT_TYPE, grdLabels, grdPredicates, isTheorem));
		op.addSubCommande(createElementTwoStringAttribute(root,
				IAction.ELEMENT_TYPE, LABEL_ATTRIBUTE, ASSIGNMENT_ATTRIBUTE,
				actLabels, actSubstitutions));
		return op;
	}

	/**
	 * Return an operation to create an IInternalElement with a string attribute
	 */
	public <T extends IInternalElement> OperationCreateElement createElementOneStringAttribute(
			IInternalElement parent, IInternalElementType<T> typeElement,
			IInternalElement sibling, IAttributeType.String type, String string) {
		final IAttributeValue[] values = new IAttributeValue[] { type
				.makeValue(string), //
		};
		return getCreateElement(parent, typeElement, sibling, values);
	}

	/**
	 * retourne une operation pour plusieurs elements avec un attribut de type
	 * String
	 * 
	 */
	private <T extends IInternalElement> OperationNode createElementOneStringAttribute(
			IInternalElement root, IInternalElementType<T> typeElement,
			IAttributeType.String type, String[] string) {
		assert string != null;
		OperationNode op = new OperationNode();
		for (int i = 0; i < string.length; i++) {
			op.addCommande(createElementOneStringAttribute(root, typeElement,
					null, type, string[i]));
		}
		return op;
	}

	/**
	 * retourne une operation pour creer un element avec deux attributs de type
	 * String
	 * 
	 */
	private <T extends IInternalElement> OperationCreateElement createElementTwoStringAttribute(
			IInternalElement parent, IInternalElementType<T> typeElement,
			IAttributeType.String type1, IAttributeType.String type2,
			String string1, String string2) {
		final IAttributeValue[] values = new IAttributeValue[] {
				type1.makeValue(string1), //
				type2.makeValue(string2), //
		};
		return getCreateElement(parent, typeElement, null, values);
	}

	/**
	 * retourne une operation pour creer plusieurs elements d'un meme type et
	 * deux attributs de type String
	 * 
	 */
	private <T extends IInternalElement> OperationNode createElementTwoStringAttribute(
			IInternalElement parent, IInternalElementType<T> typeElement,
			IAttributeType.String type1, IAttributeType.String type2,
			String[] string1, String[] string2) {
		OperationNode op = new OperationNode();
		for (int i = 0; i < string1.length; i++) {
			op.addCommande(createElementTwoStringAttribute(parent, typeElement,
					type1, type2, string1[i], string2[i]));
		}
		return op;
	}

	/**
	 * @param label
	 *            if null the label of created element is the next free label.
	 */
	private <T extends IInternalElement> OperationCreateElement createElementLabelPredicate(
			IInternalElement parent, IInternalElementType<T> type,
			String label, String predicate, boolean isTheorem) {
		final IAttributeValue[] values;
		if (label == null) {
			values = new IAttributeValue[] {
					PREDICATE_ATTRIBUTE.makeValue(predicate), //
					THEOREM_ATTRIBUTE.makeValue(isTheorem), //
			};
		} else {
			values = new IAttributeValue[] {
					LABEL_ATTRIBUTE.makeValue(label), //
					PREDICATE_ATTRIBUTE.makeValue(predicate), //
					THEOREM_ATTRIBUTE.makeValue(isTheorem), //
			};
		}
		return getCreateElement(parent, type, null, values);
	}

	private <T extends IInternalElement> OperationNode createElementLabelPredicate(
			IInternalElement parent, IInternalElementType<T> type,
			String[] labels, String[] predicates, boolean[] isTheorem) {
		final OperationNode op = new OperationNode();
		for (int i = 0; i < labels.length; i++) {
			op.addCommande(createElementLabelPredicate(parent, type, labels[i],
					predicates[i], isTheorem[i]));
		}
		return op;
	}

	private OperationCreateElement createParameter(IMachineRoot root,
			String identifier) {
		return createElementOneStringAttribute(root, IParameter.ELEMENT_TYPE,
				null, IDENTIFIER_ATTRIBUTE, identifier);
	}

	private OperationNode createParameter(IMachineRoot root,
			String[] varIdentifiers) {
		OperationNode op = new OperationNode();
		for (String identifier : varIdentifiers) {
			op.addCommande(createParameter(root, identifier));
		}
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
	public <T extends IInternalElement> CreateElementGeneric<T> createDefaultElement(
			IInternalElement parent, IInternalElementType<T> type,
			IInternalElement sibling) {
		return new CreateElementGeneric<T>(parent, type, sibling);
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

	public <E extends IInternalElement> OperationTree changeAttribute(
			IAttributeManipulation factory, E element, String value) {
		final ChangeAttributeWithManipulation op = new ChangeAttributeWithManipulation(
				factory, element, value);
		return op;
	}

	public OperationTree createGuard(IInternalElement event, String label,
			String predicate, IInternalElement sibling) {
		return createElementLabelPredicate(event, IGuard.ELEMENT_TYPE, label,
				predicate, false);
	}

	public OperationTree createAction(IInternalElement event, String label,
			String assignement, IInternalElement sibling) {
		return createElementTwoStringAttribute(event, IAction.ELEMENT_TYPE,
				EventBAttributes.LABEL_ATTRIBUTE,
				EventBAttributes.ASSIGNMENT_ATTRIBUTE, label, assignement);
	}

	public OperationTree createAction(IInternalElement event, String[] label,
			String[] assignement, IInternalElement sibling) {
		return createElementTwoStringAttribute(event, IAction.ELEMENT_TYPE,
				EventBAttributes.LABEL_ATTRIBUTE,
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
			IInternalElement newParent, IInternalElement newSibling) {
		return new Move(movedElement, newParent, newSibling);
	}

	public <T extends IInternalElement> OperationTree renameElement(
			IInternalElement root, IInternalElementType<T> type,
			IAttributeManipulation factory, String prefix) {
		final OperationNode op = new OperationNode();
		int counter = 1;
		try {
			for (IInternalElement element : root.getChildrenOfType(type)) {
				op.addCommande(changeAttribute(factory, element, prefix
						+ counter));
				counter++;
			}
			for (IRodinElement element : root.getChildren()) {
				final IInternalElement ie = (IInternalElement) element;
				op.addCommande(renameElement(ie, type, factory, prefix));
			}
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return op;
	}
}
