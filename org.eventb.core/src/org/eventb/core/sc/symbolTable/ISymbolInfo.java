/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinElement;


/**
 * @author Stefan Hallerstede
 *
 */
public interface ISymbolInfo extends Comparable {
	
	/**
	 * Returns the symbol sting of this symbol info.
	 * 
	 * @return the symbol string
	 */
	String getSymbol();
	
	/**
	 * Returns the name of the component that contains this symbol.
	 * 
	 * @return name of the component that contains this symbol
	 */
	String getComponentName();
	
	/**
	 * Returns the reference element to which to attach error messages concerning this symbol info
	 * 
	 * @return the reference element of this symbol info
	 */
	IRodinElement getReferenceElement();
	
	/**
	 * Returns the source element. By default this returns the reference element unless
	 * the source element is set explicity with <code>setSourceElement</code>.
	 * 
	 * @return the source element of this symbol info
	 */
	IRodinElement getSourceElement();
	
	/**
	 * Sets the source element for this symbol.
	 * 
	 * @param source the source element
	 */
	void setSourceElement(IRodinElement source);
	
	/**
	 * Returns whether this symbol is errorneous.
	 * 
	 * @return <code>true</code> if the symbol is erroneous, <code>false</code> otherwise
	 */
	boolean hasError();
	
	/**
	 * Marks this symbol as erroneous.
	 * 
	 * @throws CoreException if the symbol info is not mutable
	 */
	void setError() throws CoreException;
	
	/**
	 * Returns whether the symbol info is (still) mutable.
	 * The static checker can mark symbol infos as immutable.
	 * In particular, this removes the possibility to mark a symbol as erroneous.
	 * 
	 * @return whether or not the symbol info is mutable.
	 */
	boolean isMutable();
	
	/**
	 * Turns the symbol immutable
	 */
	void setImmutable();
}
