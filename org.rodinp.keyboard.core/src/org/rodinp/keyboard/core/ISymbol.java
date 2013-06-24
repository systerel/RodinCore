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

/**
 * Public interface for keyboard symbols.
 */
public interface ISymbol {

	/**
	 * Returns the string corresponding to the ASCII key combination to be
	 * translated into the current symbol.
	 * 
	 * @return the ASCII key combination corresponding to the symbol
	 */
	String getCombo();

	/**
	 * Returns the string representation of the symbol.
	 * 
	 * @return the string of the symbol
	 */
	String getTranslation();

}