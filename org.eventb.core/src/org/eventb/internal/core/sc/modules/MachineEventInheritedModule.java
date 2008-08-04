/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IEvent;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IAbstractEventInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.eventb.core.sc.symbolTable.IEventSymbolInfo;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.tool.IModuleType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventInheritedModule extends SCFilterModule {
	
	public static final IModuleType<MachineEventInheritedModule> MODULE_TYPE = 
		SCCore.getModuleType(EventBPlugin.PLUGIN_ID + ".machineEventInheritedModule"); //$NON-NLS-1$
	
	public IModuleType<?> getModuleType() {
		return MODULE_TYPE;
	}

	protected ILabelSymbolTable labelSymbolTable;
	protected IIdentifierSymbolTable identifierSymbolTable;

	@Override
	public void initModule(ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		labelSymbolTable = 
			(ILabelSymbolTable) repository.getState(IMachineLabelSymbolTable.STATE_TYPE);
		identifierSymbolTable =
			(IIdentifierSymbolTable) repository.getState(IIdentifierSymbolTable.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IFilterModule#accept(org.rodinp.core.IRodinElement, org.eventb.core.state.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean accept(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		
		IEvent event = (IEvent) element;
		String eventLabel = event.getLabel();
		
		IEventSymbolInfo eventSymbolInfo = (IEventSymbolInfo) labelSymbolTable.getSymbolInfo(eventLabel);
		
		if (eventSymbolInfo == null || !eventSymbolInfo.isInherited() || eventSymbolInfo.getRefinesInfo() == null)
			return true;
		
		IAbstractEventInfo abstractEventInfo = eventSymbolInfo.getRefinesInfo().getAbstractEventInfos().get(0);
		
		return checkFormula(event, eventSymbolInfo, abstractEventInfo, abstractEventInfo.getGuards())
			&& checkFormula(event, eventSymbolInfo, abstractEventInfo, abstractEventInfo.getActions());
	}
	
	private <T extends Formula<T>> boolean checkFormula(
			IEvent event,
			IEventSymbolInfo eventSymbolInfo, 
			IAbstractEventInfo abstractEventInfo, 
			List<T> formulas) throws CoreException {
		
		boolean ok = true;
		
		LinkedList<String> seenFaultyIdents = new LinkedList<String>();
		
		for (T formula : formulas) {
			for (FreeIdentifier identifier : formula.getFreeIdentifiers()) {
				String name = identifier.getName();
				boolean found = abstractEventInfo.getVariable(name) != null;
				if (!found) {
					IIdentifierSymbolInfo symbolInfo = identifierSymbolTable.getSymbolInfo(name);
					if (symbolInfo == null || symbolInfo.hasError()) {
						createIdentProblem(event, name, seenFaultyIdents, GraphProblem.UndeclaredFreeIdentifierError);
						ok = false;
					}
					else if (symbolInfo instanceof IVariableSymbolInfo) {
						IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
						if (!variableSymbolInfo.isConcrete()) {
							createIdentProblem(event, name, seenFaultyIdents, GraphProblem.VariableHasDisappearedError);
							ok = false;
						}
					}
				}
			}
		}
		
		return ok;
	}

	private void createIdentProblem(
			IEvent event, 
			String name, 
			LinkedList<String> seenFaultyIdents,
			IRodinProblem problem) throws RodinDBException {
		if (!seenFaultyIdents.contains(name)) {
			seenFaultyIdents.add(name);
			createProblemMarker(
					event, 
					GraphProblem.UndeclaredFreeIdentifierError, 
					name);
		}
	}

	@Override
	public void endModule(ISCStateRepository repository, IProgressMonitor monitor) throws CoreException {
		labelSymbolTable = null;
		identifierSymbolTable = null;
		super.endModule(repository, monitor);
	}

}
