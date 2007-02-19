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
import org.eventb.core.EventBAttributes;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.tool.IModuleType;
import org.eventb.internal.core.sc.Messages;
import org.eventb.internal.core.sc.symbolTable.ConcreteVariableSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariableModule extends IdentifierModule {

	public static final IModuleType<MachineVariableModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineVariableModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	public void process(
			IRodinElement element, 
			IInternalParent target,
			ISCStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {

		IMachineFile machineFile = (IMachineFile) element;
		
		IVariable[] variables = machineFile.getVariables();
		
		if(variables.length == 0)
			return;
		
		monitor.subTask(Messages.bind(Messages.progress_MachineVariables));
		
		fetchSymbols(
				variables,
				target,
				repository, 
				monitor);
	}
	
	@Override
	protected boolean insertIdentifierSymbol(
			IIdentifierElement element,
			IIdentifierSymbolInfo newSymbolInfo) throws CoreException {
		
		try {
			
			identifierSymbolTable.putSymbolInfo(newSymbolInfo);
			((IVariableSymbolInfo) newSymbolInfo).setPreserved();
			((IVariableSymbolInfo) newSymbolInfo).setFresh();
			
		} catch (CoreException e) {
			
			IIdentifierSymbolInfo symbolInfo = 
				identifierSymbolTable.getSymbolInfo(newSymbolInfo.getSymbol());
			
			if (symbolInfo instanceof IVariableSymbolInfo) {
				IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
				variableSymbolInfo.setPreserved();
				variableSymbolInfo.setSourceElement(element);
				return true;
			}
			
			newSymbolInfo.createConflictMarker(this);
			
			if(symbolInfo.hasError())
				return false; // do not produce too many error messages
			
			symbolInfo.createConflictMarker(this);
			
			if (symbolInfo.isMutable())
				symbolInfo.setError();
			
			return false;
		}
		return true;
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(
			String name, IIdentifierElement element) {
		return new ConcreteVariableSymbolInfo(
				name, element, 
				EventBAttributes.IDENTIFIER_ATTRIBUTE, element.getParent().getElementName());
	}

}
