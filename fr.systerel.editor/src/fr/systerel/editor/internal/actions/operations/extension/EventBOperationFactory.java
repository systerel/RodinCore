/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.actions.operations.extension;

import static org.eventb.core.EventBAttributes.ASSIGNMENT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.CONVERGENCE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.EXPRESSION_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.IDENTIFIER_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.LABEL_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.PREDICATE_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.THEOREM_ATTRIBUTE;
import static org.eventb.core.IConvergenceElement.Convergence.ORDINARY;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.eventbeditor.Triplet;
import org.rodinp.core.IAttributeValue;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

import fr.systerel.editor.actions.OperationBuilder;
import fr.systerel.editor.actions.OperationTree;
import fr.systerel.editor.internal.actions.operations.OperationCreateElement;
import fr.systerel.editor.internal.actions.operations.OperationNode;

/**
 * Static methods customizing the OperationFactory for Event-B elements. This is
 * an adaptation of {@link EventBUtils} and
 * {@link org.eventb.internal.ui.eventbeditor.operations.OperationFactory}
 * 
 * @author "Thomas Muller"
 */
public class EventBOperationFactory {

	private static final String KEYWORD_PARTITION = "partition";

	private static void assertLengthEquals(Object[] tab1, Object[] tab2) {
		assert tab1 != null && tab2 != null;
		assert tab1.length == tab2.length;
	}

	public static OperationTree createConstant(IInternalElement root,
			String identifier) {
		return OperationBuilder.getDefault().createElementOneStringAttribute(
				root, IConstant.ELEMENT_TYPE, null, IDENTIFIER_ATTRIBUTE,
				identifier);
	}

	public static OperationTree createCarrierSet(IInternalElement root,
			String[] identifier) {
		return OperationBuilder.getDefault().createElementOneStringAttribute(
				root, ICarrierSet.ELEMENT_TYPE, IDENTIFIER_ATTRIBUTE,
				identifier);
	}

	public static OperationTree createCarrierSet(IInternalElement root,
			String identifier) {
		return OperationBuilder.getDefault().createElementOneStringAttribute(
				root, ICarrierSet.ELEMENT_TYPE, null, IDENTIFIER_ATTRIBUTE,
				identifier);
	}

	private static <T extends IInternalElement> OperationNode createElementLabelPredicate(
			IInternalElement parent, IInternalElementType<T> type,
			String[] labels, String[] predicates, boolean[] isTheorem) {
		final OperationNode op = new OperationNode();
		for (int i = 0; i < labels.length; i++) {
			op.addCommand(createElementLabelPredicate(parent, type, labels[i],
					predicates[i], isTheorem[i]));
		}
		return op;
	}

	/**
	 * @param label
	 *            if null the label of created element is the next free label.
	 */
	private static <T extends IInternalElement> OperationCreateElement createElementLabelPredicate(
			IInternalElement parent, IInternalElementType<T> type,
			String label, String predicate, boolean isTheorem) {
		final List<IAttributeValue> values = new LinkedList<IAttributeValue>();
		if (label != null) {
			values.add(LABEL_ATTRIBUTE.makeValue(label));
		}
		values.add(PREDICATE_ATTRIBUTE.makeValue(predicate));
		if (isTheorem) {
			values.add(THEOREM_ATTRIBUTE.makeValue(isTheorem));
		}
		final IAttributeValue[] valuesArray = values
				.toArray(new IAttributeValue[values.size()]);
		return OperationBuilder.getDefault().getCreateElement(parent, type,
				null, valuesArray);
	}

	public static OperationTree createAxiom(IInternalElement root,
			String[] labels, String[] predicates, boolean[] isTheorem) {
		assertLengthEquals(labels, predicates);
		return createElementLabelPredicate(root, IAxiom.ELEMENT_TYPE, labels,
				predicates, isTheorem);
	}

	/**
	 * @param label
	 *            null to set a default label
	 * @param predicate
	 */
	public static OperationTree createAxiom(IInternalElement root,
			String label, String predicate, boolean isTheorem) {
		return createElementLabelPredicate(root, IAxiom.ELEMENT_TYPE, label,
				predicate, isTheorem);
	}

	public static OperationTree createInvariant(IInternalElement root,
			String[] labels, String[] predicates, boolean[] isTheorem) {
		assertLengthEquals(labels, predicates);
		return createElementLabelPredicate(root, IInvariant.ELEMENT_TYPE,
				labels, predicates, isTheorem);
	}

	public static OperationTree createEnumeratedSet(IContextRoot root,
			String name, String[] elements) {
		final OperationNode cmd = new OperationNode();
		cmd.addCommand(createCarrierSet(root, name));
		if (elements.length > 0) {
			cmd.addCommand(createElementsOfEnumeratedSet(root, elements));
			cmd.addCommand(createPartition(root, name, elements));
		}
		return cmd;
	}

	/**
	 * return a command which create a constant for each element in elements
	 */
	private static OperationTree createElementsOfEnumeratedSet(
			IInternalElement root, String[] elements) {
		final OperationNode cmd = new OperationNode();
		for (String element : elements) {
			cmd.addCommand(createConstant(root, element));
		}
		return cmd;
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
	private static OperationTree createPartition(IInternalElement root,
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

	public static OperationTree[] createEvent(IInternalElement root,
			String name, String[] paramNames, String[] grdLabels,
			String[] grdPredicates, boolean[] grdIsTheorem, String[] actLabels,
			String[] actSubstitutions) {
		final IMachineRoot mRoot = (IMachineRoot) root;
		final OperationTree[] t = {
				createEvent(mRoot, name), //
				createParameter(mRoot, paramNames),
				createElementLabelPredicate(mRoot, IGuard.ELEMENT_TYPE,
						grdLabels, grdPredicates, grdIsTheorem),
				OperationBuilder.getDefault().createElementTwoStringAttribute(
						mRoot, IAction.ELEMENT_TYPE, LABEL_ATTRIBUTE,
						ASSIGNMENT_ATTRIBUTE, actLabels, actSubstitutions) };
		return t;
	}

	private static OperationTree createEvent(IMachineRoot root, String label) {
		final List<IAttributeValue> values = new LinkedList<IAttributeValue>();
		if (label != null) {
			values.add(LABEL_ATTRIBUTE.makeValue(label));
		}
		values.add(CONVERGENCE_ATTRIBUTE.makeValue(ORDINARY.getCode()));
		final IAttributeValue[] array = values
				.toArray(new IAttributeValue[values.size()]);
		return OperationBuilder.getDefault().getCreateElement(root,
				IEvent.ELEMENT_TYPE, null, array);
	}

	private static OperationTree createParameter(IMachineRoot root,
			String identifier) {
		return OperationBuilder.getDefault().createElementOneStringAttribute(
				root, IParameter.ELEMENT_TYPE, null, IDENTIFIER_ATTRIBUTE,
				identifier);
	}

	private static OperationTree createParameter(IMachineRoot root,
			String[] varIdentifiers) {
		OperationNode op = new OperationNode();
		for (String identifier : varIdentifiers) {
			op.addCommand(createParameter(root, identifier));
		}
		return op;
	}
	
	public static OperationTree[] createVariable(IMachineRoot root, String varName,
			Collection<Triplet<String, String, Boolean>> invariant,
			String actName, String actSub) {
		final List<OperationTree> cmd = new ArrayList<OperationTree>();
		cmd.add(createVariable(root, varName));
		if (!invariant.isEmpty()) {
			cmd.add(createInvariantList(root, invariant));
		}
		if (actName != null && actSub != null) {
			cmd.add(createInitialisation(root, actName, actSub));
		}
		return cmd.toArray(new OperationTree[cmd.size()]);
	}
	
	private static OperationTree createVariable(IMachineRoot root,
			String identifier) {
		return OperationBuilder.getDefault().createElementOneStringAttribute(
				root, IVariable.ELEMENT_TYPE, null, IDENTIFIER_ATTRIBUTE,
				identifier);
	}
	
	/**
	 * Return an Operation to create a list of invariants.
	 * <p>
	 * The theorem attribute is implicitly <code>false</code>.
	 * </p>
	 */
	private static OperationTree createInvariantList(IMachineRoot root,
			Collection<Triplet<String, String, Boolean>> invariants) {
		OperationNode cmd = new OperationNode();

		if (invariants != null) {
			for (Triplet<String, String, Boolean> triplet : invariants) {
				cmd.addCommand(createInvariant(root, triplet.getFirst(),
						triplet.getSecond(), triplet.getThird()));
			}
		}
		return cmd;
	}

	public static OperationTree createInvariant(IMachineRoot root,
			String label, String predicate, boolean isTheorem) {
		return createElementLabelPredicate(root, IInvariant.ELEMENT_TYPE,
				label, predicate, isTheorem);
	};
	
	// TODO change by operation multiple event ?
	private static OperationTree createInitialisation(IMachineRoot root,
			String actName, String actSub) {
		return new CreateInitialisation(root, actName, actSub);
	}
	
	public static OperationTree createVariant(IInternalElement root,
			String predicate) {
		return OperationBuilder.getDefault().createElementOneStringAttribute(
				root, IVariant.ELEMENT_TYPE, null, EXPRESSION_ATTRIBUTE,
				predicate);
	}

}
