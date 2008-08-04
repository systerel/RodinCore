/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;


/**
 * <code>IVariableSymbolInfo</code> represents an Event-B machine variable in the symbol table.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @author Stefan Hallerstede
 *
 */
public interface IVariableSymbolInfo extends IIdentifierSymbolInfo {
	
	/**
	 * Marks the variable as concrete. This method can be called even
	 * when the symbol info is immutable.
	 */
	public void setAbstract();
	
	/**
	 * Returns whether this is a variable of the current machine.
	 * 
	 * @return whether this is a variable of the current machine
	 */
	boolean isAbstract();

	/**
	 * Marks the variable as concrete. This method can be called even
	 * when the symbol info is immutable.
	 */
	public void setConcrete();
	
	/**
	 * Returns whether this is a variable of the current machine.
	 * 
	 * @return whether this is a variable of the current machine
	 */
	boolean isConcrete();
	
}
