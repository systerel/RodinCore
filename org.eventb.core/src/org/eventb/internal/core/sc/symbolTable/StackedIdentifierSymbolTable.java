/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;

/**
 * @author Stefan Hallerstede
 *
 */
public class StackedIdentifierSymbolTable extends StackedSymbolTable<IIdentifierSymbolInfo> implements
		IIdentifierSymbolTable {
	
	private final Set<FreeIdentifier> freeIdentifiers;
	
	private final FormulaFactory factory;
	
	private final Collection<FreeIdentifier> allFreeIdentifiers;
	
	public StackedIdentifierSymbolTable(
			IIdentifierSymbolTable parentTable, 
			int size,
			FormulaFactory factory) {
		super(parentTable, size);
		freeIdentifiers = new HashSet<FreeIdentifier>(size);
		this.factory = factory;
		allFreeIdentifiers = 
			new StackedCollection<FreeIdentifier>(
					parentTable.getFreeIdentifiers(), 
					freeIdentifiers);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IState#getStateType()
	 */
	public String getStateType() {
		return IIdentifierSymbolTable.STATE_TYPE;
	}

	public Collection<FreeIdentifier> getFreeIdentifiers() {
		return allFreeIdentifiers;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.symbolTable.StackedSymbolTable#putSymbolInfo(org.eventb.core.sc.symbolTable.ISymbolInfo)
	 */
	@Override
	public void putSymbolInfo(IIdentifierSymbolInfo symbolInfo) throws CoreException {
		super.putSymbolInfo(symbolInfo);
		freeIdentifiers.add(
				factory.makeFreeIdentifier(symbolInfo.getSymbol(), null));
	}

}
