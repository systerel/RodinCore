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
import org.eventb.core.IEvent;
import org.eventb.core.IIdentifierElement;
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
public class MachineEventVariableModule extends IdentifierModule {

	public static final String MACHINE_EVENT_VARIABLE_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".machineEventVariableAcceptor";

	private IAcceptorModule[] modules;

	public MachineEventVariableModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = manager.getAcceptorModules(MACHINE_EVENT_VARIABLE_ACCEPTOR);
	}

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository repository,
			IProgressMonitor monitor)
			throws CoreException {

		IEvent event = (IEvent) element;
		
		IVariable[] variables = event.getVariables();
		
		if(variables.length == 0)
			return;
		
		fetchSymbols(
				variables,
				target,
				modules,
				repository, 
				monitor);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.IdentifierModule#insertIdentifierSymbol(org.eventb.core.sc.IIdentifierSymbolTable, org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo)
	 */
	@Override
	protected boolean insertIdentifierSymbol(
			IIdentifierElement element,
			IIdentifierSymbolTable identifierSymbolTable, 
			IIdentifierSymbolInfo newSymbolInfo) throws CoreException {
		((IVariableSymbolInfo) newSymbolInfo).setLocal();
		return super.insertIdentifierSymbol(element, identifierSymbolTable, newSymbolInfo);
	}

}
