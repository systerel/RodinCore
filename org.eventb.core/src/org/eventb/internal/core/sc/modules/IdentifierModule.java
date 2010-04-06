/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - ensure that all AST problems are reported
 *     Systerel - mathematical language V2
 *     Systerel - added check on primed identifiers
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import static org.eventb.core.ast.LanguageVersion.V2;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.SCProcessorModule;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ISCStateRepository;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class IdentifierModule extends SCProcessorModule {

	protected FormulaFactory factory;

	protected ITypeEnvironment typeEnvironment;

	protected IIdentifierSymbolTable identifierSymbolTable;

	protected static FreeIdentifier parseIdentifier(String name,
			IInternalElement element, IAttributeType.String attrType,
			FormulaFactory factory, IMarkerDisplay display, boolean primeAllowed)
			throws RodinDBException {

		IParseResult pResult = factory.parseExpression(name, V2, element);
		Expression expr = pResult.getParsedExpression();
		if (pResult.hasProblem() || !(expr instanceof FreeIdentifier)) {
			display.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		FreeIdentifier identifier = (FreeIdentifier) expr;
		if (!(primeAllowed)&&identifier.isPrimed()){
			display.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierError, name);
			return null;
		}
		if (!name.equals(identifier.getName())) {
			display.createProblemMarker(element, attrType,
					GraphProblem.InvalidIdentifierSpacesError, name);
			return null;
		}
		return identifier;
	}

	/**
	 * Parse the identifier element
	 * 
	 * @param element
	 *            the element to be parsed
	 * @return a <code>FreeIdentifier</code> in case of success,
	 *         <code>null</code> otherwise
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	protected FreeIdentifier parseIdentifier(IIdentifierElement element,
			IProgressMonitor monitor) throws RodinDBException {

		if (element.hasIdentifierString()) {

			return parseIdentifier(element.getIdentifierString(), element,
					EventBAttributes.IDENTIFIER_ATTRIBUTE, factory, this, false);
		} else {

			createProblemMarker(element, EventBAttributes.IDENTIFIER_ATTRIBUTE,
					GraphProblem.IdentifierUndefError);
			return null;
		}
	}

	/**
	 * Fetch identifiers from component, parse them and add them to the symbol
	 * table.
	 * 
	 * @param elements
	 *            the identifier elements to fetch
	 * @param target
	 *            the target static checked container
	 * @param repository
	 *            the state repository
	 * @throws CoreException
	 *             if there was a problem accessing the symbol table
	 */
	protected void fetchSymbols(IIdentifierElement[] elements,
			IInternalElement target, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {

		initFilterModules(repository, null);

		for (IIdentifierElement element : elements) {
			FreeIdentifier identifier = parseIdentifier(element, monitor);

			if (identifier == null)
				continue;
			String name = identifier.getName();

			IIdentifierSymbolInfo newSymbolInfo = createIdentifierSymbolInfo(
					name, element);
			newSymbolInfo.setAttributeValue(EventBAttributes.SOURCE_ATTRIBUTE,
					element);

			boolean ok = insertIdentifierSymbol(element, newSymbolInfo);

			if (!ok || !filterModules(element, repository, null))
				continue;

			typeIdentifierSymbol(newSymbolInfo, typeEnvironment);

			monitor.worked(1);

		}

		endFilterModules(repository, null);
		
	}

	protected abstract IIdentifierSymbolInfo createIdentifierSymbolInfo(
			String name, IIdentifierElement element);

	protected void typeIdentifierSymbol(IIdentifierSymbolInfo newSymbolInfo,
			final ITypeEnvironment environment) throws CoreException {
		// by default no type information for the identifier is generated
	}

	protected boolean insertIdentifierSymbol(IIdentifierElement element,
			IIdentifierSymbolInfo newSymbolInfo) throws CoreException {

		try {

			identifierSymbolTable.putSymbolInfo(newSymbolInfo);

		} catch (CoreException e) {

			IIdentifierSymbolInfo symbolInfo = identifierSymbolTable
					.getSymbolInfo(newSymbolInfo.getSymbol());

			newSymbolInfo.createConflictMarker(this);

			if (symbolInfo.hasError())
				return false; // do not produce too many error messages

			symbolInfo.createConflictMarker(this);

			if (symbolInfo.isMutable())
				symbolInfo.setError();

			return false;
		}
		return true;
	}

	@Override
	public void initModule(IRodinElement element,
			ISCStateRepository repository, IProgressMonitor monitor)
			throws CoreException {
		super.initModule(element, repository, monitor);
		factory = FormulaFactory.getDefault();
		typeEnvironment = repository.getTypeEnvironment();

		identifierSymbolTable = (IIdentifierSymbolTable) repository
				.getState(IIdentifierSymbolTable.STATE_TYPE);
	}

	@Override
	public void endModule(IRodinElement element, ISCStateRepository repository,
			IProgressMonitor monitor) throws CoreException {
		factory = null;
		identifierSymbolTable = null;
		typeEnvironment = null;
		super.endModule(element, repository, monitor);
	}

}
