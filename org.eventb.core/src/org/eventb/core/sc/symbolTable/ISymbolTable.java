/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;

/**
 * @author Stefan Hallerstede
 *
 */
public interface ISymbolTable extends Iterable<ISymbolInfo> {
	
	/**
	 * Returns whether a symbol is contained in the symbol table.
	 * 
	 * @param symbol the symbol to be looked up.
	 * @return <code>true</code> if the symbol is present, <code>false</code> otherwise
	 */
	boolean containsKey(String symbol);
	
	/**
	 * Returns the symbol info for a symbol. If the symbol is not present 
	 * <code>null</code> is returned. 
	 * 
	 * @param symbol the symbol corresponding to the symbol info
	 * @return the symbol info
	 */
	ISymbolInfo getSymbolInfo(String symbol);
	
	/**
	 * Inserts a symbol info into the symbol table. Multiple insertions are not allowed.
	 * The symbol corresponding symbol is <code>symbolInfo.getSymbol()</code>.
	 * 
	 * @param symbolInfo the symbol info for the symbol
	 * @throws CoreException if the symbol is already present in the symbol table
	 * 
	 * @see ISymbolInfo#getSymbol()
	 */
	void putSymbolInfo(ISymbolInfo symbolInfo) throws CoreException;
		
	/**
	 * Turns all symbols of the symbol table immutable
	 */
	void setImmutable();
	
	/**
	 * Returns the number of symbols in this symbol table
	 * 
	 * @return the number of symbols in this symbol table
	 */
	int size();
}
