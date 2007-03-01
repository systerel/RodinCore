/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eventb.core.EventBAttributes;
import org.eventb.core.IEventBFile;
import org.eventb.core.ISCContext;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.CarrierSetSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.ConstantSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.MachineVariableSymbolInfo;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class IdentifierCreatorModule extends SCProcessorModule {
	
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
			ISCContext context = (ISCContext) element.getParent();
			return CarrierSetSymbolInfo.makeAbstractCarrierSetSymbolInfo(
					symbol, 
					pointerElement, 
					EventBAttributes.TARGET_ATTRIBUTE, 
					context.getComponentName());
		}
		
	};

	protected IIdentifierSymbolInfoCreator abstractConstantCreator = new IIdentifierSymbolInfoCreator() {

		public IIdentifierSymbolInfo createIdentifierSymbolInfo(
				String symbol, 
				ISCIdentifierElement element, 
				IInternalElement pointerElement) {
			ISCContext context = (ISCContext) element.getParent();
			return ConstantSymbolInfo.makeAbstractConstantSymbolInfo(
					symbol, 
					pointerElement, 
					EventBAttributes.TARGET_ATTRIBUTE, 
					context.getComponentName());
		}
		
	};

	protected IIdentifierSymbolInfoCreator abstractVariableCreator = new IIdentifierSymbolInfoCreator() {

		public IIdentifierSymbolInfo createIdentifierSymbolInfo(
				String symbol, 
				ISCIdentifierElement element, 
				IInternalElement pointerElement) {
			IEventBFile file = (IEventBFile) element.getParent();
			return MachineVariableSymbolInfo.makeAbstractVariableSymbolInfo(
					symbol, 
					pointerElement, 
					EventBAttributes.TARGET_ATTRIBUTE, 
					file.getComponentName());
		}
		
	};

}
