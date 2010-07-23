/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Southampton - maintenance
 *     Systerel - added formula factory field
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 * 
 */
public class IdentifierSymbolTable
		extends
		SymbolTable<ISCIdentifierElement, IInternalElementType<? extends ISCIdentifierElement>, IIdentifierSymbolInfo>
		implements IIdentifierSymbolTable {

	private final Set<FreeIdentifier> freeIdentifiers;
	private final FormulaFactory factory;

	public IdentifierSymbolTable(int identSize, FormulaFactory factory) {
		super(identSize);
		this.factory = factory;
		freeIdentifiers = new HashSet<FreeIdentifier>(identSize);
	}

	@Override
	public IStateType<?> getStateType() {
		return STATE_TYPE;
	}

	@Override
	public Collection<FreeIdentifier> getFreeIdentifiers() {
		return freeIdentifiers;
	}

	@Override
	public IIdentifierSymbolTable getParentTable() {
		return null;
	}

	@Override
	public void putSymbolInfo(IIdentifierSymbolInfo symbolInfo)
			throws CoreException {
		super.putSymbolInfo(symbolInfo);
		freeIdentifiers.add(factory.makeFreeIdentifier(symbolInfo.getSymbol(),
				null));
	}

	@Override
	public IIdentifierSymbolInfo getSymbolInfoFromTop(String symbol) {
		return getSymbolInfo(symbol);
	}

	@Override
	public Collection<IIdentifierSymbolInfo> getSymbolInfosFromTop() {
		return Collections.unmodifiableSet(tableValues);
	}

}
