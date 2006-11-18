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
import org.eventb.core.IEvent;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.IVariable;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IFilterModule;
import org.eventb.core.sc.IModuleManager;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IEventRefinesInfo;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.state.IStateRepository;
import org.eventb.internal.core.sc.ModuleManager;
import org.eventb.internal.core.sc.symbolTable.EventVariableSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventVariableModule extends IdentifierModule {

	public static final String MACHINE_EVENT_VARIABLE_ACCEPTOR = 
		EventBPlugin.PLUGIN_ID + ".machineEventVariableAcceptor";

	private IFilterModule[] modules;

	public MachineEventVariableModule() {
		IModuleManager manager = ModuleManager.getModuleManager();
		modules = manager.getFilterModules(MACHINE_EVENT_VARIABLE_ACCEPTOR);
	}
	
	protected IEventRefinesInfo eventRefinesInfo;
	protected boolean isInitialisation;

	public void process(
			IRodinElement element, 
			IInternalParent target,
			IStateRepository<IStateSC> repository,
			IProgressMonitor monitor)
			throws CoreException {

		IEvent event = (IEvent) element;
		
		IVariable[] variables = event.getVariables();
		
		if(variables.length != 0)
			fetchSymbols(
					variables,
					target,
					modules,
					repository, 
					monitor);
		
		patchTypeEnvironment();
	}

	/**
	 * add abstract local variables to type environment
	 * that are not also local variables of the refined event
	 */
	private void patchTypeEnvironment() {
		if (eventRefinesInfo.isEmpty())
			return;
		IAbstractEventInfo abstractEventInfo = eventRefinesInfo.getAbstractEventInfos().get(0);
		ITypeEnvironment typeEnvironment = typingState.getTypeEnvironment();
		for (FreeIdentifier freeIdentifier : abstractEventInfo.getIdentifiers()) {
			String name = freeIdentifier.getName();
			if (identifierSymbolTable.getSymbolInfoFromTop(name) != null)
				continue;
			Type type = freeIdentifier.getType();
			typeEnvironment.addName(name, type);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.IdentifierModule#insertIdentifierSymbol(org.eventb.core.sc.IIdentifierSymbolTable, org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo)
	 */
	@Override
	protected boolean insertIdentifierSymbol(
			IIdentifierElement element,
			IIdentifierSymbolInfo newSymbolInfo) throws CoreException {
		if (super.insertIdentifierSymbol(element, newSymbolInfo)) {
			if (isInitialisation) {
				createProblemMarker(element, GraphProblem.InitialisationVariableError);
				newSymbolInfo.setError();
				return false;
			} else
				return true;
		} else
			return false;
	}

	@Override
	public void initModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(element, repository, monitor);
		eventRefinesInfo = (IEventRefinesInfo) repository.getState(IEventRefinesInfo.STATE_TYPE);
		isInitialisation = ((IEvent) element).getLabel().contains(IEvent.INITIALISATION);
	}

	@Override
	public void endModule(
			IRodinElement element, 
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		eventRefinesInfo = null;
		super.endModule(element, repository, monitor);
	}

	@Override
	protected IIdentifierSymbolInfo createIdentifierSymbolInfo(String name, IIdentifierElement element) {
		return new EventVariableSymbolInfo(
				name, null, element, 
				EventBAttributes.IDENTIFIER_ATTRIBUTE, element.getParent().getElementName());
	}

}
