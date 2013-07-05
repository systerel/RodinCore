/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.core.tests.registry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;
import org.rodinp.internal.keyboard.core.symbols.Symbol;
import org.rodinp.internal.keyboard.core.symbols.SymbolRegistry;
import org.rodinp.internal.keyboard.core.symbols.Symbols;
import org.rodinp.keyboard.core.ISymbol;
import org.rodinp.keyboard.core.RodinKeyboardCore;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class SymbolRegistryTests {

	private static final SymbolRegistry registry = (SymbolRegistry) RodinKeyboardCore
			.getSymbolRegistry();

	private void assertNoDuplicate(String combo1, String combo2) {
		final Collection<Collection<ISymbol>> textSymbols = registry
				.getTextSymbols().values();
		int dup1Count = 0;
		int dup2Count = 0;
		for (Collection<ISymbol> collection : textSymbols) {
			for (ISymbol symbol : collection) {
				if (symbol.getCombo().equals(combo1)) {
					dup1Count++;
				} else if (symbol.getCombo().equals(combo2)) {
					dup2Count++;
				}
			}
		}
		assertEquals("exactly one of the duplicates should be loaded", 1,
				dup1Count + dup2Count);
	}

	@Test
	public void testDuplicateIDs() throws Exception {
		assertNoDuplicate("dupID1", "dupID2");
	}

	@Test
	public void testDuplicateCombos() throws Exception {
		assertNoDuplicate("dupCombo", "dupCombo");
	}

	@Test
	public void testAddContainsRawSymbol() throws Exception {
		final Symbols symbols = new Symbols();
		final String combo = "testSymbol";
		symbols.addRawSymbol(new Symbol(combo, "s"));
		assertTrue("Added raw symbol not contained afterwards", symbols
				.containRawCombo(combo));
	}
}
