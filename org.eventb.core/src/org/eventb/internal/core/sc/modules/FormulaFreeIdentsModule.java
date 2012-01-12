/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - code refactoring
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import static org.eventb.core.sc.GraphProblem.FreeIdentifierFaultyDeclError;
import static org.eventb.core.sc.GraphProblem.UndeclaredFreeIdentifierError;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.sc.SCFilterModule;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.IParsedFormula;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class FormulaFreeIdentsModule extends SCFilterModule {

	protected IParsedFormula parsedFormula;
	protected IIdentifierSymbolTable symbolTable;

	@Override
	public void initModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		symbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
		parsedFormula = (IParsedFormula) repository
				.getState(IParsedFormula.STATE_TYPE);
	}

	@Override
	public boolean accept(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		boolean ok = true;
		final IInternalElement internalElement = (IInternalElement) element;
		for (final FreeIdentifier freeIdentifier : getFreeIdentifiers()) {
			final IIdentifierSymbolInfo symbolInfo = getSymbolInfo(
					internalElement, freeIdentifier, monitor);
			if (symbolInfo == null) {
				ok = false;
			}
		}
		return ok;
	}

	protected FreeIdentifier[] getFreeIdentifiers() {
		return parsedFormula.getFormula().getFreeIdentifiers();
	}

	protected abstract IAttributeType.String getAttributeType();

	protected IIdentifierSymbolInfo getSymbolInfo(IInternalElement element,
			FreeIdentifier freeIdentifier, IProgressMonitor monitor)
			throws CoreException {
		final IIdentifierSymbolInfo symbolInfo = symbolTable
				.getSymbolInfo(freeIdentifier.getName());
		if (symbolInfo == null) {
			createProblemMarker(element, freeIdentifier,
					UndeclaredFreeIdentifierError);
			return null;
		}
		if (symbolInfo.hasError()) {
			createProblemMarker(element, freeIdentifier,
					FreeIdentifierFaultyDeclError);
			return null;
		}
		return symbolInfo;
	}

	public void createProblemMarker(IInternalElement element,
			FreeIdentifier ident, IRodinProblem problem)
			throws RodinDBException {
		final SourceLocation location = ident.getSourceLocation();
		createProblemMarker(element, getAttributeType(), location.getStart(),
				location.getEnd(), problem, ident.getName());
	}

	@Override
	public void endModule(ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		symbolTable = null;
		parsedFormula = null;
	}

}
