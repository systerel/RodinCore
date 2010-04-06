/*******************************************************************************
 * Copyright (c) 2006-2008 ETH Zurich, 2008 University of Southampton
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;

/**
 * @author Stefan Hallerstede
 * 
 */
public class StackedIdentifierSymbolTable extends IdentifierSymbolTable
		implements IIdentifierSymbolTable {

	private final Set<FreeIdentifier> freeIdentifiers;

	private final Collection<FreeIdentifier> allFreeIdentifiers;

	protected final IIdentifierSymbolTable parentTable;

	public StackedIdentifierSymbolTable(IIdentifierSymbolTable parentTable,
			int size) {
		super(size);
		this.parentTable = parentTable;
		freeIdentifiers = new HashSet<FreeIdentifier>(size);
		allFreeIdentifiers = new StackedCollection<FreeIdentifier>(parentTable
				.getFreeIdentifiers(), freeIdentifiers);
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
	public void putSymbolInfo(IIdentifierSymbolInfo symbolInfo)
			throws CoreException {
		String symbol = symbolInfo.getSymbol();

		if (parentTable.containsKey(symbol)) {
			throwSymbolConflict();
		}

		super.putSymbolInfo(symbolInfo);
	}

}
