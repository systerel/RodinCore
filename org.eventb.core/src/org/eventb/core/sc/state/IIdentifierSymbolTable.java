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
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.ISymbolTable;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IIdentifierSymbolTable extends ISymbolTable<IIdentifierSymbolInfo>, ISCState {

	final static String STATE_TYPE = EventBPlugin.PLUGIN_ID + ".identifierSymbolTable";
	
	Collection<FreeIdentifier> getFreeIdentifiers();
}
