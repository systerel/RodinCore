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
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class FormulaFreeIdentsModule extends SCFilterModule {

	protected IParsedFormula parsedFormula;
	protected IIdentifierSymbolTable symbolTable;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.Module#initModule(org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		symbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
		parsedFormula = (IParsedFormula) repository
				.getState(IParsedFormula.STATE_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.IAcceptorModule#accept(org.rodinp.core.IRodinElement,
	 * org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		boolean ok = true;

		IInternalElement internalElement = (IInternalElement) element;

		FreeIdentifier[] freeIdentifiers = getFreeIdentifiers();

		for (FreeIdentifier freeIdentifier : freeIdentifiers) {

			IIdentifierSymbolInfo symbolInfo = getSymbolInfo(internalElement,
					freeIdentifier, monitor);

			if (symbolInfo == null) {
				ok = false;
			}
		}
		return ok;
	}

	protected FreeIdentifier[] getFreeIdentifiers() {
		FreeIdentifier[] freeIdentifiers = parsedFormula.getFormula()
				.getFreeIdentifiers();
		return freeIdentifiers;
	}

	protected abstract IAttributeType.String getAttributeType();

	protected IIdentifierSymbolInfo getSymbolInfo(IInternalElement element,
			FreeIdentifier freeIdentifier, IProgressMonitor monitor)
			throws CoreException {
		IIdentifierSymbolInfo symbolInfo = symbolTable
				.getSymbolInfo(freeIdentifier.getName());
		if (symbolInfo == null) {
			createProblemMarker(element, getAttributeType(), freeIdentifier
					.getSourceLocation().getStart(), freeIdentifier
					.getSourceLocation().getEnd(),
					GraphProblem.UndeclaredFreeIdentifierError, freeIdentifier
							.getName());
		} else if (symbolInfo.hasError()) {
			createProblemMarker(element, getAttributeType(), freeIdentifier
					.getSourceLocation().getStart(), freeIdentifier
					.getSourceLocation().getEnd(),
					GraphProblem.FreeIdentifierFaultyDeclError, freeIdentifier
							.getName());
			symbolInfo = null;
		}
		return symbolInfo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.Module#endModule(org.eventb.core.sc.IStateRepository,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		symbolTable = null;
		parsedFormula = null;
	}

}
