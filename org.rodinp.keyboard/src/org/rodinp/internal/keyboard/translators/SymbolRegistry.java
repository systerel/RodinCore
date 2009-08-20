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
 *******************************************************************************/
package org.rodinp.internal.keyboard.translators;

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.rodinp.internal.keyboard.KeyboardUtils;
import org.rodinp.keyboard.RodinKeyboardPlugin;
public class SymbolRegistry {
	
	private String SYMBOLS_ID = RodinKeyboardPlugin.PLUGIN_ID + ".symbols"; 

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
	
	public Map<String, Collection<Symbol>> getMathSymbols() {
		loadRegistry();
		return mathSymbols.getSymbols();
	}
	
	public Map<String, Collection<Symbol>> getTextSymbols() {
		loadRegistry();
		return textSymbols.getSymbols();
	}
	
	private synchronized void loadRegistry() {
		if (mathSymbols != null) {
			// avoid to read the registry at the same time in different threads
			return;
		}
		mathSymbols = new Symbols();
		textSymbols = new Symbols();
		
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg.getExtensionPoint(SYMBOLS_ID);
		IConfigurationElement[] configurations = extensionPoint
				.getConfigurationElements();
		
		for (IConfigurationElement configuration : configurations) {
			String id = configuration.getAttribute("id"); //$NON-NLS-1$
			
			if (configuration.getName().equals("symbol")) {
				String combo = configuration.getAttribute("combo");
				if (combo == null) {
					if (KeyboardUtils.DEBUG) {
						KeyboardUtils.debug("Configuration with id " + id
								+ " does not have any combo value,"
								+ " ignore this configuration.");
						continue;
					}
				}
				boolean isText = isTextSymbol(combo);
				Symbols symbols;
				if (isText) {
					symbols = textSymbols;
				} else {
					symbols = mathSymbols;
				}
				
				if (symbols.containRawCombo(combo)) {
					if (KeyboardUtils.DEBUG) {
						KeyboardUtils
								.debug("Translation already exists for combination "
										+ combo
										+ ", ignore this configuration.");
					}
					continue;
				}
				else {
					String translation = configuration
							.getAttribute("translation");
					if (translation == null) {
						if (KeyboardUtils.DEBUG) {
							KeyboardUtils.debug("Configuration with id " + id
									+ " does not have any translation value,"
									+ " ignore this configuration.");
						}
						continue;						
					}
					Symbol symbol = new Symbol(combo, translation);
					symbols.addRawSymbol(symbol);
				}
			}
		}
	}

	private boolean isTextSymbol(String combo) {
		for (int i = 0; i < combo.length(); i++) {
			char c = combo.charAt(i);
			if (!KeyboardUtils.isTextCharacter(c))
				return false;
		}
		return true;
	}

	public int getMaxMathSymbolSize() {
		return mathSymbols.getMaxSize();
	}

	public int getMaxTextSymbolSize() {
		return textSymbols.getMaxSize();
	}
	
}
