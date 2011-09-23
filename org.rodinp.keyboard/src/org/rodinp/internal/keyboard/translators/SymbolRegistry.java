/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - modified loadRegistry() to handle programmatic contributions
 *     Systerel - refactored to support programmatic contributions at runtime
 *******************************************************************************/
package org.rodinp.internal.keyboard.translators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rodinp.internal.keyboard.KeyboardUtils;
import org.rodinp.keyboard.ExtensionSymbol;
import org.rodinp.keyboard.ISymbolsProvider;
import org.rodinp.keyboard.RodinKeyboardPlugin;

public class SymbolRegistry {

	private static final String SYMBOLS_ID = RodinKeyboardPlugin.PLUGIN_ID + ".symbols";

	private static final String SYMBOL_EXTENSION = "symbol";
	
	private static final String SYMBOL_PROVIDER_EXTENSION = "symbolProvider";
	
	private static SymbolRegistry instance;
	
	private SymbolRegistry() {
		// Hide the constructor.
	}
	
	public static SymbolRegistry getDefault() {
		if (instance == null)
			instance = new SymbolRegistry();
		return instance;
	}
	
	private Symbols mathSymbols = null;
	
	private Symbols textSymbols = null;
	
	private List<ISymbolsProvider> symbolProviders = null;
	
	public Map<String, Collection<Symbol>> getMathSymbols() {
		loadRegistry();
		return mathSymbols.getSymbols(getMathProviderSymbols());
	}
	
	public Map<String, Collection<Symbol>> getTextSymbols() {
		loadRegistry();
		return textSymbols.getSymbols(getTextProviderSymbols());
	}
	
	private synchronized void loadRegistry() {
		if (mathSymbols != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}
		mathSymbols = new Symbols();
		textSymbols = new Symbols();
		symbolProviders = new ArrayList<ISymbolsProvider>();

		final List<String> registeredIDs = new ArrayList<String>();

		final IExtensionRegistry reg = Platform.getExtensionRegistry();
		final IExtensionPoint extensionPoint = reg
				.getExtensionPoint(SYMBOLS_ID);
		final IConfigurationElement[] elements = extensionPoint
				.getConfigurationElements();

		for (IConfigurationElement element : elements) {
			if (element.getName().equals(SYMBOL_EXTENSION)) {

				assert element.getName().equals(SYMBOL_EXTENSION);
				final String id = element.getNamespaceIdentifier() + "."
						+ element.getAttribute("id");
				final String combo = element.getAttribute("combo");
				final String translation = element.getAttribute("translation");
				addSymbol(id, combo, translation, registeredIDs);
				continue;

			}
			if (element.getName().equals(SYMBOL_PROVIDER_EXTENSION)) {
				assert element.getName().equals(SYMBOL_PROVIDER_EXTENSION);
				final ISymbolsProvider provider = getSymbolProvider(element);
				if (provider == null) {
					continue;
				}
				symbolProviders.add(provider);
			}
		}
	}
	
	private List<ExtensionSymbol> getTextProviderSymbols() {
		final List<ExtensionSymbol> math = new ArrayList<ExtensionSymbol>();
		final List<ExtensionSymbol> text = new ArrayList<ExtensionSymbol>();
		addProviderSymbols(text, math);
		return text;
	}
	
	private List<ExtensionSymbol> getMathProviderSymbols() {
		final List<ExtensionSymbol> math = new ArrayList<ExtensionSymbol>();
		final List<ExtensionSymbol> text = new ArrayList<ExtensionSymbol>();
		addProviderSymbols(text, math);
		return math;
	}
	
	private void addProviderSymbols(List<ExtensionSymbol> text,
			List<ExtensionSymbol> math) {
		for (ISymbolsProvider provider : symbolProviders) {
			final List<ExtensionSymbol> symbols = provider
					.getExtensionSymbols();
			for (ExtensionSymbol symbol : symbols) {
				final String combo = symbol.getCombo();
				if (!isValid(symbol)) {
					continue;
				}
				if (isTextSymbol(combo)) {
					text.add(symbol);
				} else {
					math.add(symbol);
				}
			}
		}
	}
	
	private static boolean isValid(ExtensionSymbol symbol) {
		return symbol.getCombo() != null && symbol.getTranslation() != null;
	}

	private boolean addSymbol(String id, String combo, String translation,
			List<String> registeredIds) {
		if (!isNotRegistered(registeredIds, id)) {
			return false;
		}
		if (!isNotNull(id, combo, translation)) {
			return false;
		}
		final Symbols symbols = getCorrespondingSymbols(combo);
		if (!isNewCombo(symbols, combo)) {
			return false;
		}
		registeredIds.add(id);
		final Symbol symbol = new Symbol(combo, translation);
		symbols.addRawSymbol(symbol);
		return true;
	}
	
	private boolean isNotRegistered(List<String> registeredIds, String id) {
		if (registeredIds.contains(id)) {
			if (KeyboardUtils.DEBUG)
				KeyboardUtils.debug("Duplicate id " + id
						+ ": ignored this configuration.");
			return false;
		}
		return true;
	}
	
	private boolean isNotNull(String id, String combo, String translation) {
		if (combo == null) {
			if (KeyboardUtils.DEBUG)
				KeyboardUtils.debug("Configuration with id " + id
						+ " does not have any combo value,"
						+ " ignored this configuration.");
			return false;
		}
		if (translation == null) {
			if (KeyboardUtils.DEBUG)
				KeyboardUtils.debug("Configuration with id " + id
						+ " does not have any translation value,"
						+ " ignored this configuration.");
			return false;
		}
		return true;
	}
	
	private Symbols getCorrespondingSymbols(String combo){
		if (isTextSymbol(combo)) {
			return textSymbols;
		}
		return mathSymbols;		
	}
	
	private boolean isNewCombo(Symbols symbols, String combo) {
		if (symbols.containRawCombo(combo)) {
			if (KeyboardUtils.DEBUG)
				KeyboardUtils
						.debug("Translation already exists for combination "
								+ combo + ", ignored this configuration.");
			return false;
		}
		return true;
	}
	
	private boolean isTextSymbol(String combo) {
		for (int i = 0; i < combo.length(); i++) {
			char c = combo.charAt(i);
			if (!KeyboardUtils.isTextCharacter(c))
				return false;
		}
		return true;
	}

	private ISymbolsProvider getSymbolProvider(IConfigurationElement element) {
		try {
			return (ISymbolsProvider) element
					.createExecutableExtension("class");
		} catch (CoreException e) {
			if (KeyboardUtils.DEBUG)
				KeyboardUtils
						.debug("Could not retrieve the symbols provider for element"
								+ element.toString() + ".");
		}
		return null;
	}

	public int getMaxMathSymbolSize() {
		return mathSymbols.getMaxSize();
	}

	public int getMaxTextSymbolSize() {
		return textSymbols.getMaxSize();
	}
	
}
