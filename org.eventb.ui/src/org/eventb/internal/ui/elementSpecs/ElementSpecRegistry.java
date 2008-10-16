/*******************************************************************************
 * Copyright (c) 2007, 2008 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced inherited by extended, added parameters
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.elementSpecs;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IParameter;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;

public class ElementSpecRegistry implements IElementSpecRegistry {

	private static ElementSpecRegistry instance;

	private ElementSpecRegistry() {
		// Singleton: private to hide constructor
	}

	public static ElementSpecRegistry getDefault() {
		if (instance == null)
			instance = new ElementSpecRegistry();
		return instance;
	}

	private static final String[] validElementRelationshipIDs = new String[] {
			EventBPlugin.PLUGIN_ID + ".refinesMachine",
			EventBPlugin.PLUGIN_ID + ".seesContext",
			EventBPlugin.PLUGIN_ID + ".variable",
			EventBPlugin.PLUGIN_ID + ".invariant",
			EventBPlugin.PLUGIN_ID + ".machineTheorem",
			EventBPlugin.PLUGIN_ID + ".event",
			EventBPlugin.PLUGIN_ID + ".refinesEvent",
			EventBPlugin.PLUGIN_ID + ".parameter",
			EventBPlugin.PLUGIN_ID + ".guard",
			EventBPlugin.PLUGIN_ID + ".witness",
			EventBPlugin.PLUGIN_ID + ".action",
			EventBPlugin.PLUGIN_ID + ".variant",
			EventBPlugin.PLUGIN_ID + ".extendsContext",
			EventBPlugin.PLUGIN_ID + ".carrierSet",
			EventBPlugin.PLUGIN_ID + ".constant",
			EventBPlugin.PLUGIN_ID + ".axiom",
			EventBPlugin.PLUGIN_ID + ".contextTheorem" };

	public boolean isValidElementRelationship(String id) {
		for (String validID : validElementRelationshipIDs) {
			if (id.equals(validID))
				return true;
		}
		return false;
	}

	public String[] getElementRelationshipIDs() {
		return validElementRelationshipIDs;
	}

	public IElementRelationship getElementRelationship(String id)
			throws IllegalArgumentException {
		// IMachineRoot
		if (id.equals(EventBPlugin.PLUGIN_ID + ".refinesMachine")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".refinesMachine", IMachineRoot.ELEMENT_TYPE,
					IRefinesMachine.ELEMENT_TYPE);
		}

		if (id.equals(EventBPlugin.PLUGIN_ID + ".seesContext")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".seesContext", IMachineRoot.ELEMENT_TYPE,
					ISeesContext.ELEMENT_TYPE);
		}

		if (id.equals(EventBPlugin.PLUGIN_ID + ".variable")) {
			return new ElementRelationship(
					EventBPlugin.PLUGIN_ID + ".variable",
					IMachineRoot.ELEMENT_TYPE, IVariable.ELEMENT_TYPE);
		}

		if (id.equals(EventBPlugin.PLUGIN_ID + ".invariant")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".invariant", IMachineRoot.ELEMENT_TYPE,
					IInvariant.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".machineTheorem")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".machineTheorem", IMachineRoot.ELEMENT_TYPE,
					ITheorem.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".event")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID + ".event",
					IMachineRoot.ELEMENT_TYPE, IEvent.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".variant")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID + ".variant",
					IMachineRoot.ELEMENT_TYPE, IVariant.ELEMENT_TYPE);
		}

		// IContextRoot
		if (id.equals(EventBPlugin.PLUGIN_ID + ".extendsContext")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".extendsContext", IContextRoot.ELEMENT_TYPE,
					IExtendsContext.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".carrierSet")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".carrierSet", IContextRoot.ELEMENT_TYPE,
					ICarrierSet.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".constant")) {
			return new ElementRelationship(
					EventBPlugin.PLUGIN_ID + ".constant",
					IContextRoot.ELEMENT_TYPE, IConstant.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".axiom")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID + ".axiom",
					IContextRoot.ELEMENT_TYPE, IAxiom.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".contextTheorem")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".contextTheorem", IContextRoot.ELEMENT_TYPE,
					ITheorem.ELEMENT_TYPE);
		}

		// IEvent
		if (id.equals(EventBPlugin.PLUGIN_ID + ".refinesEvent")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".refinesEvent", IEvent.ELEMENT_TYPE,
					IRefinesEvent.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".parameter")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".parameter", IEvent.ELEMENT_TYPE, IParameter.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".guard")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID + ".guard",
					IEvent.ELEMENT_TYPE, IGuard.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".witness")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID + ".witness",
					IEvent.ELEMENT_TYPE, IWitness.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".action")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID + ".action",
					IEvent.ELEMENT_TYPE, IAction.ELEMENT_TYPE);
		}

		throw new IllegalArgumentException("Invalid element relationship id");
	}

	private static final String[] validAttributeRelationshipIDs = new String[] {
			EventBPlugin.PLUGIN_ID + ".refinesMachineAbstractMachineName",
			EventBPlugin.PLUGIN_ID + ".seesContextName",
			EventBPlugin.PLUGIN_ID + ".variableIdentifier",
			EventBPlugin.PLUGIN_ID + ".variableComment",
			EventBPlugin.PLUGIN_ID + ".invariantLabel",
			EventBPlugin.PLUGIN_ID + ".invariantPredicate",
			EventBPlugin.PLUGIN_ID + ".invariantComment",
			EventBPlugin.PLUGIN_ID + ".theoremLabel",
			EventBPlugin.PLUGIN_ID + ".theoremPredicate",
			EventBPlugin.PLUGIN_ID + ".theoremComment",
			EventBPlugin.PLUGIN_ID + ".eventLabel",
			EventBPlugin.PLUGIN_ID + ".eventExtended",
			EventBPlugin.PLUGIN_ID + ".eventConvergence",
			EventBPlugin.PLUGIN_ID + ".eventComment",
			EventBPlugin.PLUGIN_ID + ".refinesEventAbstractEventLabel",
			EventBPlugin.PLUGIN_ID + ".parameterIdentifier",
			EventBPlugin.PLUGIN_ID + ".parameterComment",
			EventBPlugin.PLUGIN_ID + ".guardLabel",
			EventBPlugin.PLUGIN_ID + ".guardPredicate",
			EventBPlugin.PLUGIN_ID + ".guardComment",
			EventBPlugin.PLUGIN_ID + ".witnessLabel",
			EventBPlugin.PLUGIN_ID + ".witnessPredicate",
			EventBPlugin.PLUGIN_ID + ".witnessComment",
			EventBPlugin.PLUGIN_ID + ".actionLabel",
			EventBPlugin.PLUGIN_ID + ".actionAssignment",
			EventBPlugin.PLUGIN_ID + ".actionComment",
			EventBPlugin.PLUGIN_ID + ".variantExpression",
			EventBPlugin.PLUGIN_ID + ".extendsContextAbstractContextName",
			EventBPlugin.PLUGIN_ID + ".carrierSetIdentifier",
			EventBPlugin.PLUGIN_ID + ".carrierSetComment",
			EventBPlugin.PLUGIN_ID + ".constantIdentifier",
			EventBPlugin.PLUGIN_ID + ".constantComment",
			EventBPlugin.PLUGIN_ID + ".axiomLabel",
			EventBPlugin.PLUGIN_ID + ".axiomPredicate",
			EventBPlugin.PLUGIN_ID + ".axiomComment" };

	public boolean isValidAttributeRelationship(String id) {
		for (String validID : validAttributeRelationshipIDs) {
			if (id.equals(validID))
				return true;
		}
		return false;
	}

	public String[] getAttributeRelationshipIDs() {
		return validAttributeRelationshipIDs;
	}

	AttributeRelationship refinesMachineAbstractMachineName;
	AttributeRelationship seesContextName;
	AttributeRelationship variableIdentifier;
	AttributeRelationship variableComment;
	AttributeRelationship invariantLabel;
	AttributeRelationship invariantPredicate;
	AttributeRelationship invariantComment;
	AttributeRelationship theoremLabel;
	AttributeRelationship theoremPredicate;
	AttributeRelationship theoremComment;
	AttributeRelationship eventLabel;
	AttributeRelationship eventExtended;
	AttributeRelationship eventConvergence;
	AttributeRelationship eventComment;
	AttributeRelationship refinesEventAbstractEventLabel;
	AttributeRelationship parameterIdentifier;
	AttributeRelationship parameterComment;
	AttributeRelationship guardLabel;
	AttributeRelationship guardPredicate;
	AttributeRelationship guardComment;
	AttributeRelationship witnessLabel;
	AttributeRelationship witnessPredicate;
	AttributeRelationship witnessComment;
	AttributeRelationship actionLabel;
	AttributeRelationship actionAssignment;
	AttributeRelationship actionComment;
	AttributeRelationship variantExpression;
	AttributeRelationship extendsContextAbstractContextName;
	AttributeRelationship carrierSetIdentifier;
	AttributeRelationship carrierSetComment;
	AttributeRelationship constantIdentifier;
	AttributeRelationship constantComment;
	AttributeRelationship axiomLabel;
	AttributeRelationship axiomPredicate;
	AttributeRelationship axiomComment;

	public IAttributeRelationship getAttributeRelationship(String id) {

		// Refines Machine's abstract machine name
		if (id.equals(EventBPlugin.PLUGIN_ID
				+ ".refinesMachineAbstractMachineName")) {
			if (refinesMachineAbstractMachineName == null)
				refinesMachineAbstractMachineName = new AttributeRelationship(
						id, IRefinesMachine.ELEMENT_TYPE,
						"org.eventb.core.target");
			return refinesMachineAbstractMachineName;
		}

		// Sees Context's name
		if (id.equals(EventBPlugin.PLUGIN_ID + ".seesContextName")) {
			if (seesContextName == null)
				seesContextName = new AttributeRelationship(id,
						ISeesContext.ELEMENT_TYPE, "org.eventb.core.target");
			return seesContextName;
		}

		// Variable's identifier
		if (id.equals(EventBPlugin.PLUGIN_ID + ".variableIdentifier")) {
			if (variableIdentifier == null)
				variableIdentifier = new AttributeRelationship(id,
						IVariable.ELEMENT_TYPE, "org.eventb.core.identifier");
			return variableIdentifier;
		}

		// Variable's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".variableComment")) {
			if (variableComment == null)
				variableComment = new AttributeRelationship(id,
						IVariable.ELEMENT_TYPE, "org.eventb.core.comment");
			return variableComment;
		}

		// Invariant's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".invariantLabel")) {
			if (invariantLabel == null)
				invariantLabel = new AttributeRelationship(id,
						IInvariant.ELEMENT_TYPE, "org.eventb.core.label");
			return invariantLabel;
		}

		// Invariant's predicate
		if (id.equals(EventBPlugin.PLUGIN_ID + ".invariantPredicate")) {
			if (invariantPredicate == null)
				invariantPredicate = new AttributeRelationship(id,
						IInvariant.ELEMENT_TYPE, "org.eventb.core.predicate");
			return invariantPredicate;
		}

		// Invariant's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".invariantComment")) {
			if (invariantComment == null)
				invariantComment = new AttributeRelationship(id,
						IInvariant.ELEMENT_TYPE, "org.eventb.core.comment");
			return invariantComment;
		}

		// Theorem's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".theoremLabel")) {
			if (theoremLabel == null)
				theoremLabel = new AttributeRelationship(id,
						ITheorem.ELEMENT_TYPE, "org.eventb.core.label");
			return theoremLabel;
		}

		// Theorem's predicate
		if (id.equals(EventBPlugin.PLUGIN_ID + ".theoremPredicate")) {
			if (theoremPredicate == null)
				theoremPredicate = new AttributeRelationship(id,
						ITheorem.ELEMENT_TYPE, "org.eventb.core.predicate");
			return theoremPredicate;
		}

		// Theorem's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".theoremComment")) {
			if (theoremComment == null)
				theoremComment = new AttributeRelationship(id,
						ITheorem.ELEMENT_TYPE, "org.eventb.core.comment");
			return theoremComment;
		}

		// Event's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".eventLabel")) {
			if (eventLabel == null)
				eventLabel = new AttributeRelationship(id, IEvent.ELEMENT_TYPE,
						"org.eventb.core.label");
			return eventLabel;
		}

		// Event's extended
		if (id.equals(EventBPlugin.PLUGIN_ID + ".eventExtended")) {
			if (eventExtended == null)
				eventExtended = new AttributeRelationship(id,
						IEvent.ELEMENT_TYPE, "org.eventb.core.extended");
			return eventExtended;
		}

		// Event's convergence
		if (id.equals(EventBPlugin.PLUGIN_ID + ".eventConvergence")) {
			if (eventConvergence == null)
				eventConvergence = new AttributeRelationship(id,
						IEvent.ELEMENT_TYPE, "org.eventb.core.convergence");
			return eventConvergence;
		}

		// Event's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".eventComment")) {
			if (eventComment == null)
				eventComment = new AttributeRelationship(id,
						IEvent.ELEMENT_TYPE, "org.eventb.core.comment");
			return eventComment;
		}

		// Refines Event's abstract event label
		if (id.equals(EventBPlugin.PLUGIN_ID
				+ ".refinesEventAbstractEventLabel")) {
			if (refinesEventAbstractEventLabel == null)
				refinesEventAbstractEventLabel = new AttributeRelationship(id,
						IRefinesEvent.ELEMENT_TYPE, "org.eventb.core.target");
			return refinesEventAbstractEventLabel;
		}

		// Parameter's identifier
		if (id.equals(EventBPlugin.PLUGIN_ID + ".parameterIdentifier")) {
			if (parameterIdentifier == null)
				parameterIdentifier = new AttributeRelationship(id,
						IParameter.ELEMENT_TYPE, "org.eventb.core.identifier");
			return parameterIdentifier;
		}

		// Parameter's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".parameterComment")) {
			if (parameterComment == null)
				parameterComment = new AttributeRelationship(id,
						IParameter.ELEMENT_TYPE, "org.eventb.core.comment");
			return parameterComment;
		}

		// Guard's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".guardLabel")) {
			if (guardLabel == null)
				guardLabel = new AttributeRelationship(id, IGuard.ELEMENT_TYPE,
						"org.eventb.core.label");
			return guardLabel;
		}

		// Guard's predicate
		if (id.equals(EventBPlugin.PLUGIN_ID + ".guardPredicate")) {
			if (guardPredicate == null)
				guardPredicate = new AttributeRelationship(id,
						IGuard.ELEMENT_TYPE, "org.eventb.core.predicate");
			return guardPredicate;
		}

		// Guard's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".guardComment")) {
			if (guardComment == null)
				guardComment = new AttributeRelationship(id,
						IGuard.ELEMENT_TYPE, "org.eventb.core.comment");
			return guardComment;
		}

		// Witness's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".witnessLabel")) {
			if (witnessLabel == null)
				witnessLabel = new AttributeRelationship(id,
						IWitness.ELEMENT_TYPE, "org.eventb.core.label");
			return witnessLabel;
		}

		// Witness's predicate
		if (id.equals(EventBPlugin.PLUGIN_ID + ".witnessPredicate")) {
			if (witnessPredicate == null)
				witnessPredicate = new AttributeRelationship(id,
						IWitness.ELEMENT_TYPE, "org.eventb.core.predicate");
			return witnessPredicate;
		}

		// Witness's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".witnessComment")) {
			if (witnessComment == null)
				witnessComment = new AttributeRelationship(id,
						IWitness.ELEMENT_TYPE, "org.eventb.core.comment");
			return witnessComment;
		}

		// Action's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".actionLabel")) {
			if (actionLabel == null)
				actionLabel = new AttributeRelationship(id,
						IAction.ELEMENT_TYPE, "org.eventb.core.label");
			return actionLabel;
		}

		// Action's assignment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".actionAssignment")) {
			if (actionAssignment == null)
				actionAssignment = new AttributeRelationship(id,
						IAction.ELEMENT_TYPE, "org.eventb.core.assignment");
			return actionAssignment;
		}

		// Action's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".actionComment")) {
			if (actionComment == null)
				actionComment = new AttributeRelationship(id,
						IAction.ELEMENT_TYPE, "org.eventb.core.comment");
			return actionComment;
		}

		// Variant's expression
		if (id.equals(EventBPlugin.PLUGIN_ID + ".variantExpression")) {
			if (variantExpression == null)
				variantExpression = new AttributeRelationship(id,
						IVariant.ELEMENT_TYPE, "org.eventb.core.expression");
			return variantExpression;
		}

		// Extends Context's abstract context name
		if (id.equals(EventBPlugin.PLUGIN_ID
				+ ".extendsContextAbstractContextName")) {
			if (extendsContextAbstractContextName == null)
				extendsContextAbstractContextName = new AttributeRelationship(
						id, IExtendsContext.ELEMENT_TYPE,
						"org.eventb.core.target");
			return extendsContextAbstractContextName;
		}

		// Carrier Set's identifier
		if (id.equals(EventBPlugin.PLUGIN_ID + ".carrierSetIdentifier")) {
			if (carrierSetIdentifier == null)
				carrierSetIdentifier = new AttributeRelationship(id,
						ICarrierSet.ELEMENT_TYPE, "org.eventb.core.identifier");
			return carrierSetIdentifier;
		}

		// Carrier Set's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".carrierSetComment")) {
			if (carrierSetComment == null)
				carrierSetComment = new AttributeRelationship(id,
						ICarrierSet.ELEMENT_TYPE, "org.eventb.core.comment");
			return carrierSetComment;
		}

		// Constant's identifier
		if (id.equals(EventBPlugin.PLUGIN_ID + ".constantIdentifier")) {
			if (constantIdentifier == null)
				constantIdentifier = new AttributeRelationship(id,
						IConstant.ELEMENT_TYPE, "org.eventb.core.identifier");
			return constantIdentifier;
		}

		// Constant's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".constantComment")) {
			if (constantComment == null)
				constantComment = new AttributeRelationship(id,
						IConstant.ELEMENT_TYPE, "org.eventb.core.comment");
			return constantComment;
		}

		// Axiom's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".axiomLabel")) {
			if (axiomLabel == null)
				axiomLabel = new AttributeRelationship(id, IAxiom.ELEMENT_TYPE,
						"org.eventb.core.label");
			return axiomLabel;
		}

		// Axiom's predicate
		if (id.equals(EventBPlugin.PLUGIN_ID + ".axiomPredicate")) {
			if (axiomPredicate == null)
				axiomPredicate = new AttributeRelationship(id,
						IAxiom.ELEMENT_TYPE, "org.eventb.core.predicate");
			return axiomPredicate;
		}

		// Axiom's comment
		if (id.equals(EventBPlugin.PLUGIN_ID + ".axiomComment")) {
			if (axiomComment == null)
				axiomComment = new AttributeRelationship(id,
						IAxiom.ELEMENT_TYPE, "org.eventb.core.comment");
			return axiomComment;
		}

		throw new IllegalArgumentException("Invalid attribute relationship id");
	}
}