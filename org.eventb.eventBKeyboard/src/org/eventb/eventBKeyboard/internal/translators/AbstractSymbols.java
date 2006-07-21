package org.eventb.eventBKeyboard.internal.translators;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import org.eventb.internal.eventBKeyboard.KeyboardUtils;

public abstract class AbstractSymbols {
	
	private Collection<Symbol> newSymbols;

	private HashMap<String, Collection<Symbol>> tempSymbols = null;

	private HashMap<String, Collection<Symbol>> symbols = null;

	public int maxSize = 0;
	
	public HashMap<String, Collection<Symbol>> getSymbols() {
		if (symbols == null) {
			int count = 0;
			tempSymbols = new HashMap<String, Collection<Symbol>>();
			String [] mathCombo = getCombo();
			String [] mathComboTranslation = getTranslation();
			
			for (int i = 0; i < mathCombo.length; i++) {
				String combo = mathCombo[i];
				String translation = mathComboTranslation[i];
				Symbol symbol = new Symbol(combo, translation);
				// KeyboardUtils.debugMath("Combo: \"" + combo + "\" --> \""
				// + translation + "\"");
				int length = combo.length();

				String key = generateKey(length);

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

			KeyboardUtils.debugMath("Original Symbols: " + count);
			mutateSymbols();

			printSymbols();
		}
		return symbols;
	}
	
	protected abstract String[] getTranslation();
	
	protected abstract String[] getCombo();

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
//		KeyboardUtils.debugMath("Push Temp: " + symbol.getCombo());
		Collection<Symbol> collection = tempSymbols.get(key);
		if (collection == null) {
			collection = new HashSet<Symbol>();
			collection.add(symbol);
			tempSymbols.put(key, collection);
		} else {
			if (collection.contains(symbol)) {

			} else {
				collection.add(symbol);
			}
		}

	}
	
	
	private void generateNewSymbol(Symbol symbol, Symbol oldSymbol) {
		String combo = symbol.getCombo();
		String oldCombo = oldSymbol.getCombo();
		
		int i = oldCombo.indexOf(combo);
		while (i != -1) {
			KeyboardUtils.debugMath("New Symbol from: \"" + combo + "\" and \"" + oldCombo + "\"");
			oldCombo = oldCombo.substring(0, i) + symbol.getTranslation()
					+ oldCombo.substring(i + combo.length(), oldCombo.length());
			newSymbols.add(new Symbol(oldCombo, oldSymbol.getTranslation()));
			KeyboardUtils.debugMath("New Symbol: " + oldCombo);
			i = oldCombo.indexOf(combo);
		}
	}

	
	private Symbol popNextSymbol() {
		for (int i = 1; i <= maxSize; i++) {
			String key = generateKey(i);
			Collection<Symbol> collection = tempSymbols.get(key);
			if ((collection != null) && collection.size() != 0) {
				Symbol symbol = (Symbol) collection.toArray()[0];
				collection.remove(symbol);
				KeyboardUtils.debugMath("Pop: " + symbol.getCombo());
				return symbol;
			}
		}
		return null;
	}

	private void printSymbols() {
		KeyboardUtils.debugMath("Max Size: " + maxSize);
		int count = 0;
		for (int i = 1; i <= maxSize; i++) {
			String key = generateKey(i);
			Collection<Symbol> collection = symbols.get(key);
			if (collection != null) {
				for (Symbol symbol : collection) {
					count++;
					KeyboardUtils.debugMath("Symbol: " + symbol.toString());
				}
			}
		}
		KeyboardUtils.debugMath("Total Symbols: " + count);
	}

	public static String generateKey(int i) {
		String key = "";
		for (int j = 0; j < i; j++)
			key += "*";
		return key;
	}

	private void pushSymbol(Symbol symbol) {
		String key = generateKey(symbol.getCombo().length());
		
		Collection<Symbol> collection = symbols.get(key);
		if (collection == null) {
			collection = new HashSet<Symbol>();
			collection.add(symbol);
			symbols.put(key, collection);
		} else {
			if (collection.contains(symbol)) {

			} else {
				collection.add(symbol);
			}
		}

	}

	public int getMaxSize() {
		return maxSize;
	}


}
