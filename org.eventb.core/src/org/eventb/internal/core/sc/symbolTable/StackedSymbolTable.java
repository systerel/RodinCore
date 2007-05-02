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

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class StackedSymbolTable<I extends ISymbolInfo> extends SymbolTable<I> {

	protected final ISymbolTable<I> parentTable;
	
	public StackedSymbolTable(ISymbolTable<I> parentTable, int size) {
		super(size);
		this.parentTable = parentTable;
	}
	
	@Override
	public ISymbolTable<I> getParentTable() {
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
	public I getSymbolInfo(String symbol) {
		I s = super.getSymbolInfo(symbol);
		if (s == null)
			return parentTable.getSymbolInfo(symbol);
		else
			return s;
	}
	
	@Override
	public void putSymbolInfo(I symbolInfo) throws CoreException {
		
		String symbol = symbolInfo.getSymbol();
		
		if (parentTable.containsKey(symbol)) {
			throwSymbolConflict();
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
	public I getSymbolInfoFromTop(String symbol) {
		return super.getSymbolInfoFromTop(symbol);
	}

}
