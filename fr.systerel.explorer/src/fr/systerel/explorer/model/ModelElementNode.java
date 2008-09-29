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
import org.eventb.core.IInvariant;
import org.eventb.core.IPSStatus;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.rodinp.core.IInternalElementType;

/**
 * This is a helper class to show a parent node for all invariants,
 * theorems, events etc. in the navigator tree.
 *
 */
public class ModelElementNode implements IModelElement{
	public ModelElementNode(IInternalElementType<?> type, IModelElement parent) {
		this.type = type;
		this.parent = parent;
	}
	
	private IInternalElementType<?> type;
	private IModelElement parent;
	

	public IModelElement getParent() {
		return parent;
	}

	public IInternalElementType<?> getType() {
		return type;
	}

	public String getName() {
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
		
		return null;
	}
	
	
	public static String INVARIANT_TYPE = "Invariants";
	public static String AXIOM_TYPE = "Axioms";
	public static String EVENT_TYPE = "Events";
	public static String CONSTANT_TYPE = "Constants";
	public static String THEOREM_TYPE = "Theorems";
	public static String CARRIER_TYPE = "Carrier Sets";
	public static String VARIABLE_TYPE = "Variables";
	

}
