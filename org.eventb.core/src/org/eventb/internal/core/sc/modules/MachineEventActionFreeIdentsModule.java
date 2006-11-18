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
import org.eventb.core.IEvent;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.state.ICurrentEvent;
import org.eventb.core.sc.state.IStateSC;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.core.state.IStateRepository;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventActionFreeIdentsModule extends FormulaFreeIdentsModule {

	private boolean isInitialisation;
	
	@Override
	public void initModule(
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		ICurrentEvent currentEvent = (ICurrentEvent) repository.getState(ICurrentEvent.STATE_TYPE);
		isInitialisation = 
			currentEvent.getCurrentEvent().getLabel().equals(IEvent.INITIALISATION);
	}

	@Override
	public void endModule(
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		super.endModule(repository, monitor);
	}

	@Override
	protected IIdentifierSymbolInfo getSymbolInfo(
			IInternalElement element, 
			FreeIdentifier freeIdentifier, 
			IProgressMonitor monitor) throws CoreException {
		IIdentifierSymbolInfo symbolInfo = super.getSymbolInfo(element, freeIdentifier, monitor);
		if (isInitialisation && symbolInfo != null && symbolInfo instanceof IVariableSymbolInfo) {
			createProblemMarker(
					element, 
					getAttributeType(), 
					GraphProblem.InitialisationActionRHSError,
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
			IStateRepository<IStateSC> repository, 
			IProgressMonitor monitor) throws CoreException {
		boolean ok = super.accept(element, repository, monitor);
		
		ok &= checkAssignedIdentifiers(
				(IInternalElement) element, 
				(Assignment) parsedFormula.getFormula(), 
				monitor);
		
		return ok;
	}
	
	private boolean checkAssignedIdentifiers(
			IInternalElement element, 
			Assignment assignment, 
			IProgressMonitor monitor) throws CoreException {
		
		FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();
		
		for (FreeIdentifier identifier : identifiers) {
			String name = identifier.getName();
			IIdentifierSymbolInfo symbolInfo = symbolTable.getSymbolInfo(name);
			if (symbolInfo instanceof IVariableSymbolInfo) {
				IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
				if (variableSymbolInfo.isForbidden()) {
					createProblemMarker(
							element, 
							getAttributeType(), 
							GraphProblem.UndeclaredFreeIdentifierError,
							name);
					return false;
				} else if (variableSymbolInfo.isImported() && !variableSymbolInfo.isConcrete()) {
					createProblemMarker(
							element, 
							getAttributeType(), 
							GraphProblem.VariableHasDisappearedError,
							name);
					return false;
				} else if (variableSymbolInfo.isLocal()) {
					createProblemMarker(
							element, 
							getAttributeType(), 
							GraphProblem.AssignmentToLocalVariableError,
							name);
					return false;
				}
			} else {
				createProblemMarker(
						element, 
						getAttributeType(), 
						GraphProblem.AssignedIdentifierNotVariableError,
						name);
				return false;
			}
		}
		return true;
	}

	@Override
	protected IRodinProblem declaredFreeIdentifierError() {
		return GraphProblem.InitialisationActionRHSError;
	}

	@Override
	protected FreeIdentifier[] getFreeIdentifiers() {
		FreeIdentifier[] freeIdentifiers =
			((Assignment) parsedFormula.getFormula()).getUsedIdentifiers();
		return freeIdentifiers;
	}

	@Override
	protected IAttributeType.String getAttributeType() {
		return EventBAttributes.ASSIGNMENT_ATTRIBUTE;
	}

}
