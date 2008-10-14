/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.model;

import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IEventBFile;
import org.eventb.core.IInvariant;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;

import fr.systerel.explorer.navigator.IElementNode;

/**
 * This is a helper class to show a parent node for all invariants,
 * theorems, events etc. in the navigator tree.
 *
 */
public class ModelElementNode implements IModelElement, IElementNode{
	public ModelElementNode(IInternalElementType<?> type, IModelElement parent) {
		this.type = type;
		this.parent = parent;
		if (parent instanceof ModelMachine) {
			this.parentFile = ((ModelMachine) parent).getInternalMachine();
		}
		if (parent instanceof ModelContext) {
			this.parentFile = ((ModelContext) parent).getInternalContext();
		}
	}
	
	private IInternalElementType<?> type;
	private IModelElement parent;
	private IEventBFile parentFile;	

	public IModelElement getModelParent() {
		return parent;
	}

	public IInternalElementType<?> getChildrenType() {
		return type;
	}

	public String getLabel() {
		if (type.equals(IInvariant.ELEMENT_TYPE)) {
			return INVARIANT_TYPE;
		}
		if (type.equals(ITheorem.ELEMENT_TYPE)) {
			return THEOREM_TYPE;
		}
		if (type.equals(IEvent.ELEMENT_TYPE)) {
			return EVENT_TYPE;
		}
		if (type.equals(IVariable.ELEMENT_TYPE)) {
			return VARIABLE_TYPE;
		}
		if (type.equals(IAxiom.ELEMENT_TYPE)) {
			return AXIOM_TYPE;
		}
		if (type.equals(ICarrierSet.ELEMENT_TYPE)) {
			return CARRIER_TYPE;
		}
		if (type.equals(IConstant.ELEMENT_TYPE)) {
			return CONSTANT_TYPE;
		}
		if (type.equals(IPSStatus.ELEMENT_TYPE)) {
			return PO_TYPE;
		}

		return null;
	}
	
	
	private static String INVARIANT_TYPE = "Invariants";
	private static String AXIOM_TYPE = "Axioms";
	private static String EVENT_TYPE = "Events";
	private static String CONSTANT_TYPE = "Constants";
	private static String THEOREM_TYPE = "Theorems";
	private static String CARRIER_TYPE = "Carrier Sets";
	private static String VARIABLE_TYPE = "Variables";
	private static String PO_TYPE = "Proof Obligations";


	public IEventBFile getParent() {
		return parentFile;
	}

	/**
	 * does not have an internal element
	 */
	public IRodinElement getInternalElement() {
		return null;
	}
	

}
