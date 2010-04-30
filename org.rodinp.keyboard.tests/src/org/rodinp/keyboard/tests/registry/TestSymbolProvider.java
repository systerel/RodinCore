/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
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
package org.rodinp.keyboard.tests.registry;

import java.util.Arrays;
import java.util.List;

import org.rodinp.keyboard.ExtensionSymbol;
import org.rodinp.keyboard.ISymbolsProvider;
import org.rodinp.keyboard.RodinKeyboardPlugin;

public class TestSymbolProvider implements ISymbolsProvider{

	private static final String PLUGIN_NAMESPACE = RodinKeyboardPlugin.PLUGIN_ID;

	private static final ExtensionSymbol[] symbols = {
		new ExtensionSymbol("alpha", "alpha", "alpha", "α"),
		new ExtensionSymbol("beta", "beta", "beta", "β"),
		
		//Test to add a duplicate...
		new ExtensionSymbol("alpha", "alpha", "alpha", "α"),

		//Test to add a symbol with identical combo
		new ExtensionSymbol("other", "other", "alpha", "α"),
		
		//Test to add a wrong defined symbol...
		new ExtensionSymbol("alpha", "null", null, null),
	};
	
	public List<ExtensionSymbol> getExtensionSymbols() {
		return Arrays.asList(symbols);
	}

	public String getNamespaceIdentifier() {
		return PLUGIN_NAMESPACE;
	}

}
