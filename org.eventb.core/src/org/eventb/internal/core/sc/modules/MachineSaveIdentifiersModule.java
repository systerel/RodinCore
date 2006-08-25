/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.IIdentifierSymbolTable;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.ProcessorModule;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.internal.core.sc.symbolTable.VariableSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineSaveIdentifiersModule extends ProcessorModule {

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		
		IIdentifierSymbolTable identifierSymbolTable = 
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		
		for(ISymbolInfo symbolInfo : identifierSymbolTable) {
			
			IIdentifierSymbolInfo identifierSymbolInfo = (IIdentifierSymbolInfo) symbolInfo;
			
			if (identifierSymbolInfo instanceof VariableSymbolInfo) {
				
				Type type = identifierSymbolInfo.getType();
				
				if(type == null) { // identifier could not be typed
					
					identifierSymbolInfo.issueUntypedErrorMarker(this);
					
					identifierSymbolInfo.setError();
					
				} else if (!identifierSymbolInfo.hasError()) {
					
					identifierSymbolInfo.createSCElement(target, null);
				}
				
				identifierSymbolInfo.setImmutable();
			}
		}
	}

}
