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
import org.eventb.core.IWitness;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.IStateRepository;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.SymbolInfoFactory;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineEventWitnessFreeIdentsModule extends MachinePredicateFreeIdentsModule {

	FormulaFactory factory;
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#initModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.initModule(repository, monitor);
		factory = repository.getFormulaFactory();
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#getSymbolInfo(org.eventb.core.ast.FreeIdentifier)
	 */
	@Override
	protected IIdentifierSymbolInfo getSymbolInfo(
			IRodinElement element, 
			FreeIdentifier freeIdentifier,
			IProgressMonitor monitor) throws CoreException {
		boolean primed = freeIdentifier.isPrimed();
		FreeIdentifier identifier = primed ? 
				freeIdentifier.withoutPrime(factory) : 
				freeIdentifier;
		IIdentifierSymbolInfo symbolInfo = super.getSymbolInfo(element, identifier, monitor);
		if (symbolInfo != null && symbolInfo instanceof IVariableSymbolInfo) {
			IVariableSymbolInfo variableSymbolInfo = (IVariableSymbolInfo) symbolInfo;
			if (!variableSymbolInfo.isLocal() && !variableSymbolInfo.isPreserved()) {
				String label = ((IWitness) element).getLabel(monitor);
				if (!label.equals(freeIdentifier.getName()))
					return null;
			}
		}
		if (symbolInfo == null) { // abstract local variables are not contained in the symbol table
			String label = ((IWitness) element).getLabel(monitor);
			if (label.equals(freeIdentifier.getName()))
				return SymbolInfoFactory.createVariableSymbolInfo(label, null, null);
		}
		return symbolInfo;
	}
	

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#endModule(org.eventb.core.sc.IStateRepository, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(IStateRepository repository, IProgressMonitor monitor) throws CoreException {
		super.endModule(repository, monitor);
		factory = null;
	}

}
