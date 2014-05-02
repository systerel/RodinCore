/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
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
 *     Systerel - got factory from repository
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.EventBAttributes;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
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

	protected ITypeEnvironmentBuilder typeEnvironment;

	protected IIdentifierSymbolTable identifierSymbolTable;

	/**
	 * Makes a free identifier from the given name.
	 * <p>
	 * Returns <code>null</code> if one of the following happens:
	 * <li>the given name cannot be parsed as an expression</li>
	 * <li>the given name is not a valid free identifier name</li>
	 * <li>the given name is primed and prime is not allowed</li>
	 * <li>the given name contains leading or trailing spaces</li>
	 * For the first problem encountered, if any, a problem marker is added and
	 * <code>null</code> is returned immediately.
	 * </p>
	 * 
	 * @param name
	 *            a name to parse
	 * @param element
	 *            the element associated the the name
	 * @param attrType
	 *            the attribute type of the element where the name is located
	 * @param factory
	 *            a formula factory to parse the name
	 * @param display
	 *            a marker display for problem markers
	 * @param primeAllowed
	 *            <code>true</code> if primed names are allowed,
	 *            <code>false</code> otherwise
	 * @return a free identifier with the given name, or <code>null</code> if
	 *         there was a problem making the identifier
	 * @throws RodinDBException
	 *             if there is a problem accessing the Rodin database
	 */
	protected static FreeIdentifier parseIdentifier(String name,
			IInternalElement element, IAttributeType.String attrType,
			FormulaFactory factory, IMarkerDisplay display, boolean primeAllowed)
			throws RodinDBException {

		IParseResult pResult = factory.parseExpression(name, element);
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

	/**
	 * Creates a new instance of IIdentifierSymbolInfo
	 * 
	 * @param name
	 *            the name of the identifier
	 * @param element
	 *            an element to which to attach problem markers
	 * @return a new instance of IIdentifierSymbolInfo
	 */
	protected abstract IIdentifierSymbolInfo createIdentifierSymbolInfo(
			String name, IIdentifierElement element);

	/**
	 * Adds type information about the given identifier symbol to the given type
	 * environment.
	 * <p>
	 * This method is intended to be overridden by identifier modules that
	 * contribute to the type environment.
	 * </p>
	 * 
	 * @param newSymbolInfo
	 *            a new identifier symbol info
	 * @param environment
	 *            the type environment of the state repository
	 * @throws CoreException
	 *             if there was a problem accessing the symbol table
	 */
	protected void typeIdentifierSymbol(IIdentifierSymbolInfo newSymbolInfo,
			final ITypeEnvironmentBuilder environment) throws CoreException {
		// by default no type information for the identifier is generated
	}

	/**
	 * Inserts the given new identifier symbol info for the given identifier
	 * element into the symbol table.
	 * <p>
	 * In case the symbol is already present in the symbol table, a conflict
	 * marker is created for conflicting symbols.
	 * </p>
	 * 
	 * @param element
	 *            an identifier element
	 * @param newSymbolInfo
	 *            a new symbol info related to the element
	 * @return <code>true</code> iff the symbol has been correctly added
	 * @throws CoreException
	 *             if there was a problem accessing conflicting symbol infos
	 */
	protected boolean insertIdentifierSymbol(IIdentifierElement element,
			IIdentifierSymbolInfo newSymbolInfo) throws CoreException {

		if (!identifierSymbolTable.tryPutSymbolInfo(newSymbolInfo)) {

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
		factory = repository.getFormulaFactory();
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
