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
import org.eventb.core.IEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.ICurrentEvent;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventActionFreeIdentsModule extends FormulaFreeIdentsModule {

	private boolean isInitialisation;
	
	@Override
	public void initModule(IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		ICurrentEvent currentEvent = (ICurrentEvent) repository.getState(ICurrentEvent.STATE_TYPE);
		isInitialisation = 
			currentEvent.getCurrentEvent().getLabel(monitor).equals(IEvent.INITIALISATION);
	}

	@Override
	public void endModule(IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		super.endModule(repository, monitor);
	}

	@Override
	protected IIdentifierSymbolInfo getSymbolInfo(
			IRodinElement element, 
			FreeIdentifier freeIdentifier, 
			IProgressMonitor monitor) throws CoreException {
		IIdentifierSymbolInfo symbolInfo = super.getSymbolInfo(element, freeIdentifier, monitor);
		if (isInitialisation && symbolInfo instanceof IVariableSymbolInfo) {
			issueMarker(
					IMarkerDisplay.SEVERITY_ERROR, 
					element, 
					Messages.scuser_InitialisationActionRHSError, 
					freeIdentifier.getName());
			return null;
		}
		return symbolInfo;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.FormulaFreeIdentsModule#accept(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public boolean accept(
			IRodinElement element, 
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		boolean ok = super.accept(element, repository, monitor);
		
		ok &= checkAssignedIdentifiers(element, (Assignment) parsedFormula.getFormula(), monitor);
		
		return ok;
	}
	
	private boolean checkAssignedIdentifiers(
			IRodinElement element, 
			Assignment assignment, 
			IProgressMonitor monitor) throws CoreException {
		
		FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();
		
		for (FreeIdentifier identifier : identifiers) {
			String name = identifier.getName();
			IIdentifierSymbolInfo symbolInfo = (IIdentifierSymbolInfo)
				symbolTable.getSymbolInfo(name);
			if (symbolInfo instanceof IVariableSymbolInfo) {
				IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
				if (variableSymbolInfo.isForbidden()) {
					issueMarker(IMarkerDisplay.SEVERITY_ERROR, element, 
							Messages.scuser_UndeclaredFreeIdentifierError, name);
					return false;
				} else if (variableSymbolInfo.isImported() && !variableSymbolInfo.isConcrete()) {
					issueMarker(IMarkerDisplay.SEVERITY_ERROR, element, 
							Messages.scuser_VariableHasDisappearedError, name);
					return false;
				} else if (variableSymbolInfo.isLocal()) {
					issueMarker(IMarkerDisplay.SEVERITY_ERROR, element, 
							Messages.scuser_AssignmentToLocalVariable, name);
					return false;
				}
			} else {
				issueMarker(IMarkerDisplay.SEVERITY_ERROR, element, 
						Messages.scuser_AssignedIdentifierNotVariable, name);
				return false;
			}
		}
		return true;
	}

	@Override
	protected String declaredFreeIdentifierErrorMessage() {
		return Messages.scuser_InitialisationActionRHSError;
	}

	@Override
	protected FreeIdentifier[] getFreeIdentifiers() {
		FreeIdentifier[] freeIdentifiers =
			((Assignment) parsedFormula.getFormula()).getUsedIdentifiers();
		return freeIdentifiers;
	}

}
