/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IAction;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IInvariant;
import org.eventb.core.ILabeledElement;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCConstant;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCVariable;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IWitness;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
public final class SymbolInfoFactory {

	private SymbolInfoFactory() {
		// do not create an instance of this class
	}
	
	static private IIdentifierSymbolInfo createCarrierSetSymbolInfo(
			String symbol,
			String pointer,
			ISCCarrierSet set, 
			IInternalElement contextPointer,
			IAttributeType.String attribute, 
			String component) {
		return new CarrierSetSymbolInfo(symbol, pointer, contextPointer, attribute, component);
	}
	
	static private IIdentifierSymbolInfo createConstantSymbolInfo(
			String symbol,
			String pointer,
			ISCConstant constant, 
			IInternalElement contextPointer,
			IAttributeType.String attribute, 
			String component) {
		return new ConstantSymbolInfo(symbol, pointer, contextPointer, attribute, component);
	}

	static private IIdentifierSymbolInfo createVariableSymbolInfo(
			String symbol,
			String pointer,
			ISCVariable variable, 
			IInternalElement contextPointer,
			IAttributeType.String attribute, 
			String component) {
		return new VariableSymbolInfo(symbol, pointer, contextPointer, attribute, component);
	}

	static public IIdentifierSymbolInfo createCarrierSetSymbolInfo(
			String symbol,
			ICarrierSet set,
			String component) {
		return new CarrierSetSymbolInfo(
				symbol, null, set, EventBAttributes.IDENTIFIER_ATTRIBUTE, component);
	}
	
	static public IIdentifierSymbolInfo createConstantSymbolInfo(
			String symbol,
			IConstant constant,
			String component) {
		return new ConstantSymbolInfo(
				symbol, null, constant, EventBAttributes.IDENTIFIER_ATTRIBUTE, component);
	}
	
	static public IIdentifierSymbolInfo createVariableSymbolInfo(
			String symbol,
			IVariable variable,
			String component) {
		return new VariableSymbolInfo(
				symbol, null, variable, EventBAttributes.IDENTIFIER_ATTRIBUTE, component);
	}
	
	static public IIdentifierSymbolInfo createIdentifierSymbolInfo(
			String symbol,
			ISCIdentifierElement element, 
			IInternalElement pointerElement,
			IAttributeType.String attribute, 
			String component) {
		
		if (element instanceof ISCCarrierSet) {
			return createCarrierSetSymbolInfo(
					symbol, 
					pointerElement.getHandleIdentifier(), 
					(ISCCarrierSet) element, 
					pointerElement, 
					attribute, 
					component);
		} else if (element instanceof ISCConstant) {
			return createConstantSymbolInfo(
					symbol, 
					pointerElement.getHandleIdentifier(), 
					(ISCConstant) element, 
					pointerElement, 
					attribute, 
					component);
		} else if (element instanceof ISCVariable) {
			return createVariableSymbolInfo(
					symbol, 
					pointerElement.getHandleIdentifier(), 
					(ISCVariable) element, 
					pointerElement, 
					attribute, 
					component);
		}
		
		assert false;
		return null;
		
	}

	static public IIdentifierSymbolInfo createIdentifierSymbolInfo(
			String symbol,
			IIdentifierElement element,
			String component) {
		
		if (element instanceof ICarrierSet) {
			return createCarrierSetSymbolInfo(symbol, (ICarrierSet) element, component);
		} else if (element instanceof IConstant) {
			return createConstantSymbolInfo(symbol, (IConstant) element, component);
		} else if (element instanceof IVariable) {
			return createVariableSymbolInfo(symbol, (IVariable) element, component);
		}
		
		assert false;
		return null;
		
	}
	
	static public ILabelSymbolInfo createLabelSymbolInfo(
			String symbol,
			ILabeledElement element,
			String component) {
		
		if (element instanceof IAxiom) {
			return new AxiomSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
		} else if (element instanceof ITheorem) {
			return new TheoremSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
		} else if (element instanceof IInvariant) {
			return new InvariantSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
		} else if (element instanceof IEvent) {
			return new EventSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
		} else if (element instanceof ISCEvent) {
			return new EventSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
		} else if (element instanceof IWitness) {
			return new WitnessSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
		} else if (element instanceof IGuard) {
			return new GuardSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
		} else if (element instanceof IAction) {
			return new ActionSymbolInfo(symbol, element, EventBAttributes.LABEL_ATTRIBUTE, component);
		}
		
		assert false;
		return null;
	}
	
}
