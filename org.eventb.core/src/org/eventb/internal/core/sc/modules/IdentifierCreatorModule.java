/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.sc.ProcessorModule;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.AbstractCarrierSetSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.AbstractConstantSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.MachineVariableSymbolInfo;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class IdentifierCreatorModule extends ProcessorModule {
	
	protected interface IIdentifierSymbolInfoCreator {
		public IIdentifierSymbolInfo createIdentifierSymbolInfo(
				String symbol,
				ISCIdentifierElement element, 
				IInternalElement pointerElement);
	}
	
	protected IIdentifierSymbolInfoCreator abstractCarrierSetCreator = new IIdentifierSymbolInfoCreator() {

		public IIdentifierSymbolInfo createIdentifierSymbolInfo(
				String symbol, 
				ISCIdentifierElement element, 
				IInternalElement pointerElement) {
			return new AbstractCarrierSetSymbolInfo(
					symbol, 
					pointerElement.getHandleIdentifier(), 
					pointerElement, 
					EventBAttributes.TARGET_ATTRIBUTE, 
					element.getParent().getElementName());
		}
		
	};

	protected IIdentifierSymbolInfoCreator abstractConstantCreator = new IIdentifierSymbolInfoCreator() {

		public IIdentifierSymbolInfo createIdentifierSymbolInfo(
				String symbol, 
				ISCIdentifierElement element, 
				IInternalElement pointerElement) {
			return new AbstractConstantSymbolInfo(
					symbol, 
					pointerElement.getHandleIdentifier(), 
					pointerElement, 
					EventBAttributes.TARGET_ATTRIBUTE, 
					element.getParent().getElementName());
		}
		
	};

	protected IIdentifierSymbolInfoCreator abstractVariableCreator = new IIdentifierSymbolInfoCreator() {

		public IIdentifierSymbolInfo createIdentifierSymbolInfo(
				String symbol, 
				ISCIdentifierElement element, 
				IInternalElement pointerElement) {
			return new MachineVariableSymbolInfo(
					symbol, 
					pointerElement.getHandleIdentifier(), 
					pointerElement, 
					EventBAttributes.TARGET_ATTRIBUTE, 
					element.getParent().getElementName());
		}
		
	};

}
