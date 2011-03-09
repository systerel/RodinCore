/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.state;

import java.util.Collection;

import org.eventb.core.EventBPlugin;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.SCCore;
import org.eventb.core.tool.IStateType;
import org.rodinp.core.IInternalElementType;

/**
 * State component for identifiers declared in a context or a machine, or in and
 * abstractions and seen contexts.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public interface IIdentifierSymbolTable
		extends
		ISymbolTable<ISCIdentifierElement, IInternalElementType<? extends ISCIdentifierElement>, IIdentifierSymbolInfo>,
		ISCState {

	final static IStateType<IIdentifierSymbolTable> STATE_TYPE = SCCore
			.getToolStateType(EventBPlugin.PLUGIN_ID + ".identifierSymbolTable");


	/**
	 * This method allows to access only the top level of a stacked symbol
	 * table. Returns the collection of symbol infos of the top symbol table.
	 * 
	 * @return the collection of symbol infos of the top level
	 */
	Collection<IIdentifierSymbolInfo> getSymbolInfosFromTop();

	/**
	 * This method allows to access only the top level of a stacked symbol
	 * table. Returns the symbol info of the top symbol table for a symbol. If
	 * the symbol is not present <code>null</code> is returned.
	 * 
	 * @param symbol
	 *            the symbol corresponding to the symbol info
	 * @return the symbol info
	 */
	IIdentifierSymbolInfo getSymbolInfoFromTop(String symbol);
	/**
	 * Returns a collection of <b>untyped</b> free identifiers of all
	 * identifiers stored in this symbol table. This collection is useful in
	 * filter modules of formulas because the filter modules are invoked
	 * <b>before</b> type-checking.
	 * 
	 * @return a collection of untyped free identifiers of all identifiers
	 *         stored in this symbol table
	 */
	Collection<FreeIdentifier> getFreeIdentifiers();
	
	/**
	 * Returns the parent symbol table for a stacked symbol table, and
	 * <code>null</code> if the symbol table is not stacked.
	 * 
	 * @return the parent symbol table or <code>null</code>
	 * 
	 */
	IIdentifierSymbolTable getParentTable();

}
