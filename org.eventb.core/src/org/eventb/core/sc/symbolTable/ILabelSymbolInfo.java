/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eventb.core.sc.state.IEventLabelSymbolTable;
import org.eventb.core.sc.state.ILabelSymbolTable;
import org.eventb.core.sc.state.IMachineLabelSymbolTable;



/**
 * Common protocol for labeled elements stored in a label symbol table.
 * <p>
 * Clients that need to contribute symbols to a label symbol table, 
 * {@link ILabelSymbolTable}, must implement this interface.
 * </p>
 * 
 * @see ILabelSymbolTable
 * @see IMachineLabelSymbolTable
 * @see IEventLabelSymbolTable
 * 
 * @author Stefan Hallerstede
 *
 */
public interface ILabelSymbolInfo extends ISymbolInfo {
	// Common protocol for labeled elements stored in a label symbol table
}
