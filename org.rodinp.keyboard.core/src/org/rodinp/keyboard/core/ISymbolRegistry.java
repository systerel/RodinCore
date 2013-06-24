/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.core;

import java.util.Collection;


/**
 * Public interface to retrieve registered symbols.
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISymbolRegistry {

	/**
	 * Returns the association of keys and the corresponding collection of
	 * mathematical symbols.
	 * 
	 * @return a map associating keys and the collection of symbols which
	 *         correspond to it
	 */
	public Collection<ISymbol> getMathSymbols(int i);

	/**
	 * Returns the association of keys and the corresponding collection of
	 * mathematical symbols.
	 * 
	 * @return a map associating keys and the collection of symbols which
	 *         correspond to it
	 */
	public Collection<ISymbol> getTextSymbols(int i);
	
	/**
	 * Returns the maximal size of the key that maps to mathematical symbols.
	 * 
	 * @return the longest key that maps to mathematical symbols
	 */
	public int getMaxMathSymbolSize();
	
	/**
	 * Returns the maximal size of the key that maps to text symbols.
	 * 
	 * @return the longest key that maps to text symbols
	 */
	public int getMaxTextSymbolSize();

}