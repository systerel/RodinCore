/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Southampton - maintenance
 *     Systerel - added formula factory in constructor
 *     Systerel - add tryPutSymbolInfo
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import java.util.Collection;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;

/**
 * @author Stefan Hallerstede
 * 
 */
public class StackedIdentifierSymbolTable extends IdentifierSymbolTable {

	private final Collection<FreeIdentifier> allFreeIdentifiers;

	protected final IIdentifierSymbolTable parentTable;

	public StackedIdentifierSymbolTable(IIdentifierSymbolTable parentTable,
			int size, FormulaFactory factory) {
		super(size, factory);
		this.parentTable = parentTable;
		allFreeIdentifiers = new StackedCollection<FreeIdentifier>(
				parentTable.getFreeIdentifiers(), super.getFreeIdentifiers());
	}

	@Override
	public IIdentifierSymbolTable getParentTable() {
		return parentTable;
	}

	@Override
	public boolean containsKey(String symbol) {
		boolean r = super.containsKey(symbol);
		if (r == false)
			return parentTable.containsKey(symbol);
		else
			return r;
	}

	@Override
	public IIdentifierSymbolInfo getSymbolInfo(String symbol) {
		IIdentifierSymbolInfo s = super.getSymbolInfo(symbol);
		if (s == null)
			return parentTable.getSymbolInfo(symbol);
		else
			return s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.core.sc.symbolTable.SymbolTable#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + parentTable.toString();
	}

	@Override
	public IIdentifierSymbolInfo getSymbolInfoFromTop(String symbol) {
		return super.getSymbolInfoFromTop(symbol);
	}

	@Override
	public Collection<FreeIdentifier> getFreeIdentifiers() {
		return allFreeIdentifiers;
	}

	@Override
	public boolean tryPutSymbolInfo(IIdentifierSymbolInfo symbolInfo) {
		final String symbol = symbolInfo.getSymbol();
		if (parentTable.containsKey(symbol)) {
			return false;
		}
		return super.tryPutSymbolInfo(symbolInfo);
	}

}
