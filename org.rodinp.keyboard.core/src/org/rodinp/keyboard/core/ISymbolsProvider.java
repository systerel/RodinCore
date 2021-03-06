/*******************************************************************************
 * Copyright (c) 2010, 2020 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - Remove unused import
 *******************************************************************************/
package org.rodinp.keyboard.core;

import java.util.List;

/**
 * Interface used to contribute programatically the new symbols to the keyboard.
 * 
 * @author Thomas Muller
 * @since 1.1
 * 
 */
public interface ISymbolsProvider {

	/**
	 * Returns the nameSpace identifier for this symbol provider.
	 * 
	 * @return a string identifying the namespace of this provider
	 */
	public String getNamespaceIdentifier();

	/**
	 * Method to retrieve a list of symbols defined programmatically and that
	 * will be added to the keyboard.
	 * 
	 * @return the list of extension symbol to add to the keyboard
	 */
	public List<ExtensionSymbol> getExtensionSymbols();

}
