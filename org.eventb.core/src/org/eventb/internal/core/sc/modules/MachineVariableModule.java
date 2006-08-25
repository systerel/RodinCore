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
import org.eventb.core.EventBPlugin;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IMachineFile;
import org.eventb.core.IVariable;
import org.eventb.core.sc.IAcceptorModule;
import org.eventb.core.sc.IIdentifierSymbolTable;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.internal.core.sc.ModuleManager;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariableModule extends IdentifierModule {

	public static final String MACHINE_VARIABLE_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".machineVariableAcceptor";

	private IAcceptorModule[] modules;

	public MachineVariableModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = manager.getAcceptorModules(MACHINE_VARIABLE_ACCEPTOR);
	}

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {

		IMachineFile machineFile = (IMachineFile) element;
		
		IVariable[] variables = machineFile.getVariables();
		
		if(variables.length == 0)
			return;
		
		fetchSymbols(
				variables,
				target,
				modules,
				repository, 
				monitor);
	}
	
	@Override
	protected boolean insertIdentifierSymbol(
			IIdentifierElement element,
			IIdentifierSymbolTable identifierSymbolTable, 
			IIdentifierSymbolInfo newSymbolInfo) throws CoreException {
		
		try {
			
			identifierSymbolTable.putSymbolInfo(newSymbolInfo);
			((IVariableSymbolInfo) newSymbolInfo).setPreserved();
			
		} catch (CoreException e) {
			
			IIdentifierSymbolInfo symbolInfo = 
				(IIdentifierSymbolInfo) identifierSymbolTable.getSymbolInfo(newSymbolInfo.getSymbol());
			
			if (symbolInfo instanceof IVariableSymbolInfo) {
				IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
				variableSymbolInfo.setPreserved();
				variableSymbolInfo.setSourceElement(element);
				return true;
			}
			
			newSymbolInfo.issueNameConflictMarker(this);
			
			if(symbolInfo.hasError())
				return false; // do not produce too many error messages
			
			symbolInfo.issueNameConflictMarker(this);
			
			if (symbolInfo.isMutable())
				symbolInfo.setError();
			
			return false;
		}
		return true;
	}

}
