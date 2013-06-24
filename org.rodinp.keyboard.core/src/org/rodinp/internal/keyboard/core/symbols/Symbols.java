/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored symbol definitions
 *     Systerel - refactored to support programmatic contributions at runtime
 *******************************************************************************/
package org.rodinp.internal.keyboard.core.symbols;

import static org.rodinp.keyboard.core.KeyboardUtils.debug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.rodinp.internal.keyboard.core.KeyboardDebugConstants;
import org.rodinp.keyboard.core.ExtensionSymbol;
import org.rodinp.keyboard.core.ISymbol;

public class Symbols {

	private final Collection<Symbol> rawSymbols = new ArrayList<Symbol>();
	
	private final Map<String, Collection<ISymbol>> tempSymbols = new HashMap<String, Collection<ISymbol>>();

	private final Map<String, Collection<ISymbol>> permanentSymbols = new HashMap<String, Collection<ISymbol>>();

	private int maxSize = 0;

	/**
	 * Returns all symbols, from those that have been added plus additional
	 * symbols from the given symbol providers.
	 * 
	 * @param symbolProviders
	 * @return
	 */
	public Map<String, Collection<ISymbol>> getSymbols(Collection<ExtensionSymbol> additionalSymbols) {
		if (additionalSymbols.isEmpty() && !permanentSymbols.isEmpty()) {
			return permanentSymbols;
		}
		maxSize = 0;
		for (Symbol symbol : rawSymbols) {
			pushSymbol(symbol, tempSymbols);
		}
		if (KeyboardDebugConstants.MATH_DEBUG)
			debug("Original Symbols: " + rawSymbols.size());
		for (ExtensionSymbol symbol : additionalSymbols) {
			pushSymbol(symbol, tempSymbols);
		}
		final Map<String, Collection<ISymbol>> allSymbols;
		if (additionalSymbols.isEmpty()) {
			allSymbols = permanentSymbols;
		} else {
			allSymbols = new HashMap<String, Collection<ISymbol>>();
		}
		mutateSymbols(allSymbols);
		printSymbols(allSymbols);
		return allSymbols;
	}

	private void mutateSymbols(Map<String, Collection<ISymbol>> symbs) {
		ISymbol symbol = popNextSymbol();
		while (symbol != null) {
			pushSymbol(symbol, symbs);
			generateNewSymbol(symbol);
			symbol = popNextSymbol();
		}
	}

	private void generateNewSymbol(ISymbol symbol) {
		final Set<Symbol> newSymbols = new HashSet<Symbol>();
		for (int i = symbol.getCombo().length() + 1; i <= maxSize; i++) {
			String key = generateKey(i);
			Collection<ISymbol> collection = tempSymbols.get(key);
			if (collection != null) {
				for (ISymbol oldSymbol : collection) {
					generateNewSymbol(symbol, oldSymbol, newSymbols);
				}
			}
		}

		for (Symbol newSymbol : newSymbols) {
			pushSymbol(newSymbol, tempSymbols);
		}
	}

	private void pushSymbol(ISymbol symbol, Map<String, Collection<ISymbol>> symbs) {
		final int length = symbol.getCombo().length();
		if (length > maxSize)
			maxSize = length;
		String key = generateKey(length);
		// KeyboardUtils.debugMath("Push Temp: " + symbol.getCombo());
		Collection<ISymbol> collection = symbs.get(key);
		if (collection == null) {
			collection = new HashSet<ISymbol>();
			symbs.put(key, collection);
		}
		if (!collection.contains(symbol)) {
			collection.add(symbol);
		}
	}

	private static void generateNewSymbol(ISymbol symbol, ISymbol oldSymbol, Set<Symbol> newSymbols) {
		String combo = symbol.getCombo();
		String oldCombo = oldSymbol.getCombo();

		int i = oldCombo.indexOf(combo);
		while (i != -1) {
			if (KeyboardDebugConstants.MATH_DEBUG)
				debug("New Symbol from: \"" + combo
						+ "\" and \"" + oldCombo + "\"");
			String newCombo = oldCombo.substring(0, i) + symbol.getTranslation()
					+ oldCombo.substring(i + combo.length(), oldCombo.length());
			Symbol newSymbol = new Symbol(newCombo, oldSymbol.getTranslation());
			newSymbols.add(newSymbol);
			generateNewSymbol(symbol, newSymbol, newSymbols);
			if (KeyboardDebugConstants.MATH_DEBUG)
				debug("New Symbol: " + newSymbol.getCombo()
						+ " ===> " + newSymbol.getTranslation());
			i = oldCombo.indexOf(combo, i + 1);
		}
	}

	private ISymbol popNextSymbol() {
		for (int i = 1; i <= maxSize; i++) {
			String key = generateKey(i);
			Collection<ISymbol> collection = tempSymbols.get(key);
			if ((collection != null) && collection.size() != 0) {
				ISymbol symbol = collection.iterator().next();
				collection.remove(symbol);
				if (KeyboardDebugConstants.MATH_DEBUG)
					debug("Pop: " + symbol.getCombo());
				return symbol;
			}
		}
		return null;
	}

	private void printSymbols(Map<String, Collection<ISymbol>> symbs) {
		if (!KeyboardDebugConstants.MATH_DEBUG) {
			return;
		}
		debug("Max Size: " + maxSize);
		int count = 0;
		for (int i = 1; i <= maxSize; i++) {
			String key = generateKey(i);
			Collection<ISymbol> collection = symbs.get(key);
			if (collection != null) {
				for (ISymbol symbol : collection) {
					count++;
					debug("Symbol: " + symbol.toString());
				}
			}
		}
		debug("Total Symbols: " + count);
	}

	public static String generateKey(int length) {
		final char[] temp = new char[length];
		Arrays.fill(temp, '*');
		return new String(temp);
	}

	public void addRawSymbol(Symbol symbol) {
		rawSymbols.add(symbol);
	}
	
	public int getMaxSize() {
		return maxSize;
	}

	public boolean containRawCombo(String combo) {
		for (ISymbol symbol : rawSymbols) {
			if (symbol.getCombo().equals(combo)) {
				return true;
			}
		}
		return false;
	}

}
