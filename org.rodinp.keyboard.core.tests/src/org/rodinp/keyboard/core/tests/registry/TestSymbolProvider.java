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
package org.rodinp.keyboard.core.tests.registry;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.rodinp.keyboard.core.ExtensionSymbol;
import org.rodinp.keyboard.core.ISymbolsProvider;
import org.rodinp.internal.keyboard.core.RodinKeyboardCorePlugin;

public class TestSymbolProvider implements ISymbolsProvider{

	private static final String PLUGIN_NAMESPACE = RodinKeyboardCorePlugin.PLUGIN_ID;

	private static final ExtensionSymbol[] INIT_SYMBOLS = {
		new ExtensionSymbol("alpha", "alpha", "alpha", "α"),
		new ExtensionSymbol("beta", "beta", "beta", "β"),
		
		//Test to add a duplicate...
		new ExtensionSymbol("alpha", "alpha", "alpha", "α"),

		//Test to add a symbol with identical combo
		new ExtensionSymbol("other", "other", "alpha", "α"),
		
		//Test to add a wrong defined symbol...
		new ExtensionSymbol("alpha", "null", null, null),
	};

	private static final List<ExtensionSymbol> symbols = new ArrayList<ExtensionSymbol>(
			asList(INIT_SYMBOLS));
	
	public List<ExtensionSymbol> getExtensionSymbols() {
		return symbols;
	}

	public String getNamespaceIdentifier() {
		return PLUGIN_NAMESPACE;
	}

	public static void addSymbol(String combo, String translation) {
		symbols.add(new ExtensionSymbol(combo, combo, combo, translation));
	}
	
	public static void reset() {
		symbols.clear();
		symbols.addAll(asList(INIT_SYMBOLS));
	}
}
