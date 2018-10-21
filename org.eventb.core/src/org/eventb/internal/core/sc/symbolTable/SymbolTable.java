/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     University of Southampton - redesign of symbol table
 *     Systerel - add tryPutSymbolInfo
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import static java.util.Collections.unmodifiableCollection;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.state.ISymbolInfo;
import org.eventb.core.sc.state.ISymbolTable;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.tool.state.State;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class SymbolTable<E extends IInternalElement, T extends IInternalElementType<? extends E>, I extends ISymbolInfo<E, T>>
		extends State implements ISymbolTable<E, T, I> {

	private final HashMap<String, I> table;

	public SymbolTable(int size) {
		table = new HashMap<String, I>(size);
	}

	@Override
	public boolean containsKey(String symbol) {
		return table.containsKey(symbol);
	}

	@Override
	public I getSymbolInfo(String symbol) {
		return table.get(symbol);
	}

	protected void throwSymbolConflict() throws CoreException {
		throw Util
				.newCoreException("Attempt to insert symbol into symbol table more than once");
	}

	@Override
	public void putSymbolInfo(I symbolInfo) throws CoreException {
		final boolean ok = tryPutSymbolInfo(symbolInfo);
		if (!ok) {
			throwSymbolConflict();
		}
	}

	@Override
	public boolean tryPutSymbolInfo(I symbolInfo) {
		final String key = symbolInfo.getSymbol();
		final I oldSymbol = table.put(key, symbolInfo);
		if (oldSymbol != null) {
			// revert to old symbol and fail
			table.put(key, oldSymbol);
			return false;
		}
		return true;
	}

	@Override
	public void makeImmutable() {
		for (I info : table.values()) {
			info.makeImmutable();
		}
		super.makeImmutable();
	}

	public Collection<I> getSymbolInfos() {
		return unmodifiableCollection(table.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return table.toString();
	}

}
