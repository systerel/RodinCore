/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolTable;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.sc.Messages;

/**
 * @author Stefan Hallerstede
 *
 */
public class SymbolTable implements ISymbolTable {

	private final Hashtable<String, ISymbolInfo> table;
	
	// the tableValues variable is a cache that holds the value of table.values()
	private final Collection<ISymbolInfo> tableValues;
	
	public SymbolTable(int size) {
		table = new Hashtable<String, ISymbolInfo>(size);
		tableValues = new TreeSet<ISymbolInfo>();
	}
	
	public boolean containsKey(String symbol) {
		return table.containsKey(symbol);
	}
	
	public ISymbolInfo getSymbolInfo(String symbol) {
		return table.get(symbol);
	}
	
	public void putSymbolInfo(ISymbolInfo ISymbolInfo) throws CoreException {
		
		String key = ISymbolInfo.getSymbol();
		
		ISymbolInfo ocell = table.put(key, ISymbolInfo);
		if (ocell != null) {
			// revert to old symbol table and throw exception
			table.put(key, ocell);
			throw Util.newCoreException(Messages.symtab_SymbolConflict);
		}
		tableValues.add(ISymbolInfo);
	}

	public Iterator<ISymbolInfo> iterator() {
		return tableValues.iterator();
	}

	public void setImmutable() {
		for(ISymbolInfo info : tableValues) {
			info.setImmutable();
		}
	}

	public int size() {
		return tableValues.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return table.toString();
	}
	
	public ISymbolInfo getSymbolInfoFromTop(String symbol) {
		return getSymbolInfo(symbol);
	}

}
