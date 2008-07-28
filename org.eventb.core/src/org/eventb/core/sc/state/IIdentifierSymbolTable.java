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
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.SCCore;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolTable;
import org.eventb.core.tool.IStateType;

/**
 * State component for identifiers declared in a context or a machine, or in 
 * and abstractions and seen contexts.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IIdentifierSymbolTable extends ISymbolTable<IIdentifierSymbolInfo>, ISCState {

	final static IStateType<IIdentifierSymbolTable> STATE_TYPE = 
		SCCore.getToolStateType(EventBPlugin.PLUGIN_ID + ".identifierSymbolTable");
	
	/**
	 * Returns a collection of <b>untyped</b> free identifiers of all identifiers stored 
	 * in this symbol table. This collection is useful in filter modules of formulas
	 * because the filter modules are invoked <b>before</b> type-checking.
	 * 
	 * @return a collection of untyped free identifiers of all identifiers stored 
	 * in this symbol table
	 */
	Collection<FreeIdentifier> getFreeIdentifiers();
}
