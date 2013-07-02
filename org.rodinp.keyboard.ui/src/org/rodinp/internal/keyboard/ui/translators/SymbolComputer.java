/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.internal.keyboard.ui.translators;

import java.util.Collection;

import org.rodinp.keyboard.core.ISymbol;
import org.rodinp.keyboard.core.ISymbolRegistry;
import org.rodinp.keyboard.core.RodinKeyboardCore;

/**
 * Intermediary class to retrieve symbols and maximum size of keyboard keys
 * combinations to enter symbols indifferently for text symbols and mathematical
 * symbols.
 * 
 * @author Thomas Muller
 */
public abstract class SymbolComputer {

	protected final static ISymbolRegistry registry = RodinKeyboardCore.getSymbolRegistry();
	
	/**
	 * Returns the maximal size of the key that maps to considered symbols.
	 * 
	 * @return the longest key that maps to considered symbols
	 */
	public abstract int getMaxSymbolSize();

	/**
	 * Returns the collection of symbols that corresponds to the keys of the
	 * given <code>length</code>.
	 * 
	 * @return the collection of symbols that corresponds to the keys of the
	 *         given <code>length</code>
	 */
	public abstract Collection<ISymbol> getSymbols(int length);
	
	
	/**
	 *	The intermediary class dedicated to text symbols.
	 */
	public static class TextSymbolComputer extends SymbolComputer {

		@Override
		public int getMaxSymbolSize() {
			return registry.getMaxTextSymbolSize();
		}

		@Override
		public Collection<ISymbol> getSymbols(int i) {
			return registry.getTextSymbols(i);
		}
		
	}
	
	/**
	 *	The intermediary class dedicated to math symbols.
	 */
	public static class MathSymbolComputer extends SymbolComputer {

		@Override
		public int getMaxSymbolSize() {
			return registry.getMaxMathSymbolSize();
		}

		@Override
		public Collection<ISymbol> getSymbols(int i) {
			return registry.getMathSymbols(i);
		}
		
	}
	
}
