/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;

/**
 * Symbol table template used in various forms in the static checker.
 * 
 * @see ILabelSymbolTable
 * @see IIdentifierSymbolTable
 * 
 *<p>
 * This interface is not intended to be implemented by clients.
 *</p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface ISymbolTable<I extends ISymbolInfo> extends Iterable<I> {
	
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
	I getSymbolInfo(String symbol);
	
	/**
	 * This method allows to access only the top level of a stacked symbol table.
	 * Returns the symbol info of the top symbol table for a symbol. 
	 * If the symbol is not present <code>null</code> is returned. 
	 * 
	 * @param symbol the symbol corresponding to the symbol info
	 * @return the symbol info
	 */
	I getSymbolInfoFromTop(String symbol);
	
	/**
	 * Inserts a symbol info into the symbol table. Multiple insertions are not allowed.
	 * The key of the corresponding symbol is <code>symbolInfo.getSymbol()</code>. If the
	 * symbol table is stacked, the symbol is inserted in the top level symbol table.
	 * 
	 * @param symbolInfo the symbol info for the symbol
	 * @throws CoreException if the symbol is already present in the symbol table
	 * 
	 * @see ISymbolInfo#getSymbol()
	 */
	void putSymbolInfo(I symbolInfo) throws CoreException;
		
	/**
	 * Returns the parent symbol table for a stacked symbol table,
	 * and <code>null</code> if the symbol table is not stacked.
	 * 
	 * @return the parent symbol table or <code>null</code>
	 * 
	 */
	ISymbolTable<I> getParentTable();
		
	/**
	 * Turns all symbols of the symbol table immutable
	 */
	void makeImmutable();
	
	/**
	 * Returns the number of symbols in this symbol table
	 * 
	 * @return the number of symbols in this symbol table
	 */
	int size();
}
