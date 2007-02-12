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
import org.eventb.internal.core.tool.state.State;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class SymbolTable<I extends ISymbolInfo> extends State implements ISymbolTable<I> {

	private final Hashtable<String, I> table;
	
	// the tableValues variable is a cache that holds the value of table.values()
	private final Collection<I> tableValues;
	
	public SymbolTable(int size) {
		table = new Hashtable<String, I>(size);
		tableValues = new TreeSet<I>();
	}
	
	public boolean containsKey(String symbol) {
		return table.containsKey(symbol);
	}
	
	public I getSymbolInfo(String symbol) {
		return table.get(symbol);
	}
	
	public void putSymbolInfo(I symbolInfo) throws CoreException {
		
		String key = symbolInfo.getSymbol();
		
		I ocell = table.put(key, symbolInfo);
		if (ocell != null) {
			// revert to old symbol table and throw exception
			table.put(key, ocell);
			throw Util.newCoreException(Messages.symtab_SymbolConflict);
		}
		tableValues.add(symbolInfo);
	}

	public Iterator<I> iterator() {
		return tableValues.iterator();
	}

	@Override
	public void makeImmutable() {
		for(I info : tableValues) {
			info.makeImmutable();
		}
		super.makeImmutable();
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
	
	public I getSymbolInfoFromTop(String symbol) {
		return getSymbolInfo(symbol);
	}

	public ISymbolTable<I> getParentTable() {
		return null;
	}

}
