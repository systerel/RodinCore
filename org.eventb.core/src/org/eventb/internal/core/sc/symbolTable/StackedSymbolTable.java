/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolTable;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.sc.Messages;

/**
 * @author Stefan Hallerstede
 *
 */
public class StackedSymbolTable extends SymbolTable {

	protected final ISymbolTable parentTable;
	
	public StackedSymbolTable(ISymbolTable parentTable, int size) {
		super(size);
		this.parentTable = parentTable;
	}
	
	@Override
	public ISymbolTable getParentTable() {
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
	public ISymbolInfo getSymbolInfo(String symbol) {
		ISymbolInfo s = super.getSymbolInfo(symbol);
		if (s == null)
			return parentTable.getSymbolInfo(symbol);
		else
			return s;
	}
	
	@Override
	public void putSymbolInfo(ISymbolInfo symbolInfo) throws CoreException {
		
		String symbol = symbolInfo.getSymbol();
		
		if (parentTable.containsKey(symbol)) {
			throw Util.newCoreException(Messages.symtab_SymbolConflict);
		}
		
		super.putSymbolInfo(symbolInfo);
		
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.symbolTable.SymbolTable#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + parentTable.toString();
	}
	
	@Override
	public ISymbolInfo getSymbolInfoFromTop(String symbol) {
		return super.getSymbolInfoFromTop(symbol);
	}

}
