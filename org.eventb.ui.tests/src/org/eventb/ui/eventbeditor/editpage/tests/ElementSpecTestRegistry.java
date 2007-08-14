package org.eventb.ui.eventbeditor.editpage.tests;

import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IContextFile;
import org.eventb.core.IEvent;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.core.IWitness;
import org.eventb.internal.ui.elementSpecs.AttributeRelationship;
import org.eventb.internal.ui.elementSpecs.ElementRelationship;
import org.eventb.internal.ui.elementSpecs.IAttributeRelationship;
import org.eventb.internal.ui.elementSpecs.IElementRelationship;
import org.eventb.internal.ui.elementSpecs.IElementSpecRegistry;

public class ElementSpecTestRegistry implements IElementSpecRegistry {

	private static ElementSpecTestRegistry instance;

	private ElementSpecTestRegistry() {
		// Singleton: private to hide constructor
	}

	public static ElementSpecTestRegistry getDefault() {
		if (instance == null)
			instance = new ElementSpecTestRegistry();
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
			EventBPlugin.PLUGIN_ID + ".contextTheorem"
	};

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

	public IElementRelationship getElementRelationship(String id) {
		// IMachineFile
		if (id.equals(EventBPlugin.PLUGIN_ID + ".refinesMachine")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".refinesMachine", IMachineFile.ELEMENT_TYPE,
					IRefinesMachine.ELEMENT_TYPE);
		}

		if (id.equals(EventBPlugin.PLUGIN_ID + ".seesContext")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".seesContext", IMachineFile.ELEMENT_TYPE,
					ISeesContext.ELEMENT_TYPE);
		}

		if (id.equals(EventBPlugin.PLUGIN_ID + ".variable")) {
			return new ElementRelationship(
					EventBPlugin.PLUGIN_ID + ".variable",
					IMachineFile.ELEMENT_TYPE, IVariable.ELEMENT_TYPE);
		}

		if (id.equals(EventBPlugin.PLUGIN_ID + ".invariant")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".invariant", IMachineFile.ELEMENT_TYPE,
					IInvariant.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".machineTheorem")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".machineTheorem", IMachineFile.ELEMENT_TYPE,
					ITheorem.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".event")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID + ".event",
					IMachineFile.ELEMENT_TYPE, IEvent.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".variant")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID + ".variant",
					IMachineFile.ELEMENT_TYPE, IVariant.ELEMENT_TYPE);
		}

		// IContextFile
		if (id.equals(EventBPlugin.PLUGIN_ID + ".extendsContext")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".extendsContext", IContextFile.ELEMENT_TYPE,
					IExtendsContext.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".carrierSet")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".carrierSet", IContextFile.ELEMENT_TYPE,
					ICarrierSet.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".constant")) {
			return new ElementRelationship(
					EventBPlugin.PLUGIN_ID + ".constant",
					IContextFile.ELEMENT_TYPE, IConstant.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".axiom")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID + ".axiom",
					IContextFile.ELEMENT_TYPE, IConstant.ELEMENT_TYPE);
		}
		if (id.equals(EventBPlugin.PLUGIN_ID + ".contextThoerem")) {
			return new ElementRelationship(EventBPlugin.PLUGIN_ID
					+ ".contextTheorem", IContextFile.ELEMENT_TYPE,
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
					+ ".parameter", IEvent.ELEMENT_TYPE, IVariable.ELEMENT_TYPE);
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

		return null;
	}

	private static final String[] validAttributeRelationshipIDs = new String[] {
			EventBPlugin.PLUGIN_ID + ".variableIdentifier",
			EventBPlugin.PLUGIN_ID + ".invariantLabel",
			EventBPlugin.PLUGIN_ID + ".invariantPredicate",
			EventBPlugin.PLUGIN_ID + ".eventLabel",
			EventBPlugin.PLUGIN_ID + ".eventInherited",
			EventBPlugin.PLUGIN_ID + ".eventConvergence",
			EventBPlugin.PLUGIN_ID + ".guardLabel",
			EventBPlugin.PLUGIN_ID + ".actionLabel"
	};

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

	public IAttributeRelationship getAttributeRelationship(String id) {

		// Variable's identifier
		if (id.equals(EventBPlugin.PLUGIN_ID + ".variableIdentifier")) {
			return new AttributeRelationship(id, IVariable.ELEMENT_TYPE,
					"org.eventb.core.identifier");
		}

		// Invariant's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".invariantLabel")) {
			return new AttributeRelationship(id, IInvariant.ELEMENT_TYPE,
					"org.eventb.core.label");
		}

		// Invariant's predicate
		if (id.equals(EventBPlugin.PLUGIN_ID + ".invariantPredicate")) {
			return new AttributeRelationship(id, IInvariant.ELEMENT_TYPE,
					"org.eventb.core.predicate");
		}

		// Event's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".eventLabel")) {
			return new AttributeRelationship(id, IEvent.ELEMENT_TYPE,
					"org.eventb.core.label");
		}

		// Event's inherited
		if (id.equals(EventBPlugin.PLUGIN_ID + ".eventInherited")) {
			return new AttributeRelationship(id, IEvent.ELEMENT_TYPE,
					"org.eventb.core.inherited");
		}

		// Event's convergence
		if (id.equals(EventBPlugin.PLUGIN_ID + ".eventConvergence")) {
			return new AttributeRelationship(id, IEvent.ELEMENT_TYPE,
					"org.eventb.core.convergence");
		}

		// Guard's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".guardLabel")) {
			return new AttributeRelationship(id, IGuard.ELEMENT_TYPE,
					"org.eventb.core.label");
		}

		// Action's label
		if (id.equals(EventBPlugin.PLUGIN_ID + ".actionLabel")) {
			return new AttributeRelationship(id, IAction.ELEMENT_TYPE,
					"org.eventb.core.label");
		}

		return null;
	}

}