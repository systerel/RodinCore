/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * This used to be abstract class AbstractSymbols. 
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored symbol definitions
 *******************************************************************************/
package org.rodinp.internal.keyboard.translators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.rodinp.internal.keyboard.KeyboardUtils;

public class Symbols {

	private final Collection<Symbol> rawSymbols;
	
	private Collection<Symbol> newSymbols;

	private HashMap<String, Collection<Symbol>> tempSymbols = null;

	private HashMap<String, Collection<Symbol>> symbols = null;

	public int maxSize = 0;

	public Symbols() {
		this.rawSymbols = new ArrayList<Symbol>();
	}
	
	public HashMap<String, Collection<Symbol>> getSymbols() {
		if (symbols == null) {
			int count = 0;
			tempSymbols = new HashMap<String, Collection<Symbol>>();
			for (Symbol symbol : rawSymbols) {
				final int length = symbol.getCombo().length();
				final String key = generateKey(length);
				Collection<Symbol> collection = tempSymbols.get(key);
				if (collection == null) {
					// KeyboardUtils.debugMath("New size: " + length);
					collection = new HashSet<Symbol>();
					collection.add(symbol);
					if (length > maxSize)
						maxSize = length;
					tempSymbols.put(key, collection);
				} else {
					collection.add(symbol);
				}
				count++;
			}
			if (KeyboardUtils.MATH_DEBUG)
				KeyboardUtils.debugMath("Original Symbols: " + count);
			mutateSymbols();

			printSymbols();
		}
		return symbols;
	}

	private void mutateSymbols() {
		symbols = new HashMap<String, Collection<Symbol>>();
		Symbol symbol = popNextSymbol();
		while (symbol != null) {
			pushSymbol(symbol);
			generateNewSymbol(symbol);
			symbol = popNextSymbol();
		}
	}

	private void generateNewSymbol(Symbol symbol) {
		newSymbols = new HashSet<Symbol>();
		for (int i = symbol.getCombo().length() + 1; i <= maxSize; i++) {
			String key = generateKey(i);
			Collection<Symbol> collection = tempSymbols.get(key);
			if (collection != null) {
				for (Symbol oldSymbol : collection) {
					generateNewSymbol(symbol, oldSymbol);
				}
			}
		}

		for (Symbol newSymbol : newSymbols) {
			pushTempSymbol(newSymbol);
		}
		return;

	}

	private void pushTempSymbol(Symbol symbol) {
		String key = generateKey(symbol.getCombo().length());
		// KeyboardUtils.debugMath("Push Temp: " + symbol.getCombo());
		Collection<Symbol> collection = tempSymbols.get(key);
		if (collection == null) {
			collection = new HashSet<Symbol>();
			tempSymbols.put(key, collection);
		}
		if (!collection.contains(symbol)) {
			collection.add(symbol);
		}
	}

	private void generateNewSymbol(Symbol symbol, Symbol oldSymbol) {
		String combo = symbol.getCombo();
		String oldCombo = oldSymbol.getCombo();

		int i = oldCombo.indexOf(combo);
		while (i != -1) {
			if (KeyboardUtils.MATH_DEBUG)
				KeyboardUtils.debugMath("New Symbol from: \"" + combo
						+ "\" and \"" + oldCombo + "\"");
			String newCombo = oldCombo.substring(0, i) + symbol.getTranslation()
					+ oldCombo.substring(i + combo.length(), oldCombo.length());
			Symbol newSymbol = new Symbol(newCombo, oldSymbol.getTranslation());
			newSymbols.add(newSymbol);
			generateNewSymbol(symbol, newSymbol);
			if (KeyboardUtils.MATH_DEBUG)
				KeyboardUtils.debugMath("New Symbol: " + newSymbol.getCombo()
						+ " ===> " + newSymbol.getTranslation());
			i = oldCombo.indexOf(combo, i + 1);
		}
	}

	private Symbol popNextSymbol() {
		for (int i = 1; i <= maxSize; i++) {
			String key = generateKey(i);
			Collection<Symbol> collection = tempSymbols.get(key);
			if ((collection != null) && collection.size() != 0) {
				Symbol symbol = (Symbol) collection.toArray()[0];
				collection.remove(symbol);
				if (KeyboardUtils.MATH_DEBUG)
					KeyboardUtils.debugMath("Pop: " + symbol.getCombo());
				return symbol;
			}
		}
		return null;
	}

	private void printSymbols() {
		if (KeyboardUtils.MATH_DEBUG)
			KeyboardUtils.debugMath("Max Size: " + maxSize);
		int count = 0;
		for (int i = 1; i <= maxSize; i++) {
			String key = generateKey(i);
			Collection<Symbol> collection = symbols.get(key);
			if (collection != null) {
				for (Symbol symbol : collection) {
					count++;
					if (KeyboardUtils.MATH_DEBUG)
						KeyboardUtils.debugMath("Symbol: " + symbol.toString());
				}
			}
		}
		if (KeyboardUtils.MATH_DEBUG)
			KeyboardUtils.debugMath("Total Symbols: " + count);
	}

	public static String generateKey(int length) {
		final char[] temp = new char[length];
		Arrays.fill(temp, '*');
		return new String(temp);
	}

	private void pushSymbol(Symbol symbol) {
		String key = generateKey(symbol.getCombo().length());

		Collection<Symbol> collection = symbols.get(key);
		if (collection == null) {
			collection = new HashSet<Symbol>();
			symbols.put(key, collection);
		}
		if (!collection.contains(symbol)) {
			collection.add(symbol);
		}
	}

	public void addRawSymbol(Symbol symbol) {
		rawSymbols.add(symbol);
	}
	
	public int getMaxSize() {
		return maxSize;
	}

	public boolean containRawCombo(String combo) {
		for (Symbol symbol : rawSymbols) {
			if (symbol.getCombo().equals(combo)) {
				return true;
			}
		}
		return false;
	}

}
