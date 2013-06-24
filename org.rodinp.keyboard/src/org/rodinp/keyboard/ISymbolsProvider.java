/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard;

import java.util.List;

import org.rodinp.keyboard.core.ExtensionSymbol;

/**
 * 
 * Interface kept for backward compatibility which maps to
 * {@link org.rodinp.keyboard.core.ISymbolsProvider}. Users shall use this
 * latter class instead.
 * 
 * @author Thomas Muller
 * @since 1.1
 * @deprecated This interface shall not be used. Use
 *             {@link org.rodinp.keyboard.core.ISymbolsProvider} instead.
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
