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
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.AcceptorModule;
import org.eventb.core.sc.IIdentifierSymbolTable;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.IParsedFormula;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class FormulaFreeIdentsModule extends AcceptorModule {

	protected IParsedFormula parsedFormula;
	protected IIdentifierSymbolTable symbolTable;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(
			IStateRepository repository, 
			IProgressMonitor monitor) throws CoreException {
		symbolTable = (IIdentifierSymbolTable)
			repository.getState(IIdentifierSymbolTable.STATE_TYPE);
		parsedFormula = (IParsedFormula)
			repository.getState(IParsedFormula.STATE_TYPE);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IAcceptorModule#accept(org.rodinp.core.IRodinElement, org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean accept(
			IRodinElement element, 
			IStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		
		boolean ok = true;	
		
		FreeIdentifier[] freeIdentifiers = getFreeIdentifiers();
		
		for (FreeIdentifier freeIdentifier : freeIdentifiers) {
			
			IIdentifierSymbolInfo symbolInfo = getSymbolInfo(element, freeIdentifier, monitor);
			
			if (symbolInfo == null || symbolInfo.hasError() || !symbolInfo.isVisible()) {
				ok = false;
				String message = 
					symbolInfo == null && symbolTable.containsKey(freeIdentifier.getName()) ?
					declaredFreeIdentifierErrorMessage() :
					Messages.scuser_UndeclaredFreeIdentifierError;
				issueMarkerWithLocation(
						IMarkerDisplay.SEVERITY_ERROR, 
						element, 
						message, 
						freeIdentifier.getSourceLocation().getStart(), 
						freeIdentifier.getSourceLocation().getEnd(), 
						freeIdentifier.getName());
			}
		}
		return ok;
	}

	protected FreeIdentifier[] getFreeIdentifiers() {
		FreeIdentifier[] freeIdentifiers =
			parsedFormula.getFormula().getFreeIdentifiers();
		return freeIdentifiers;
	}
	
	protected abstract String declaredFreeIdentifierErrorMessage();

	protected IIdentifierSymbolInfo getSymbolInfo(
			IRodinElement element, 
			FreeIdentifier freeIdentifier,
			IProgressMonitor monitor) throws CoreException {
		IIdentifierSymbolInfo symbolInfo = (IIdentifierSymbolInfo) 
			symbolTable.getSymbolInfo(freeIdentifier.getName());
		return symbolInfo;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		symbolTable = null;
		parsedFormula = null;
	}

}
