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
public class IdentifierSymbolTable extends SymbolTable<IIdentifierSymbolInfo> implements
		IIdentifierSymbolTable {
	
	private final Set<FreeIdentifier> freeIdentifiers;
	
	private final FormulaFactory factory;
	
	public IdentifierSymbolTable(int identSize, FormulaFactory factory) {
		super(identSize);
		freeIdentifiers = new HashSet<FreeIdentifier>(identSize);
		this.factory = factory;
	}

	public String getStateType() {
		return STATE_TYPE;
	}
	
	FormulaFactory getFormulaFactory() {
		return factory;
	}

	public Collection<FreeIdentifier> getFreeIdentifiers() {
		return freeIdentifiers;
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.symbolTable.SymbolTable#putSymbolInfo(org.eventb.core.sc.symbolTable.ISymbolInfo)
	 */
	@Override
	public void putSymbolInfo(IIdentifierSymbolInfo symbolInfo) throws CoreException {
		super.putSymbolInfo(symbolInfo);
		freeIdentifiers.add(
				factory.makeFreeIdentifier(symbolInfo.getSymbol(), null));
	}

}
