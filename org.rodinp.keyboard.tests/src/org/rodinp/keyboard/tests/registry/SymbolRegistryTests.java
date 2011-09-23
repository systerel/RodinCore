/*******************************************************************************
 * Copyright (c) 2009, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.keyboard.tests.registry;

import java.util.Collection;

import junit.framework.TestCase;

import org.rodinp.internal.keyboard.translators.Symbol;
import org.rodinp.internal.keyboard.translators.SymbolRegistry;
import org.rodinp.internal.keyboard.translators.Symbols;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class SymbolRegistryTests extends TestCase {

	private static final SymbolRegistry registry = SymbolRegistry.getDefault();

	private void assertNoDuplicate(String combo1, String combo2) {
		final Collection<Collection<Symbol>> textSymbols = registry
				.getTextSymbols().values();
		int dup1Count = 0;
		int dup2Count = 0;
		for (Collection<Symbol> collection : textSymbols) {
			for (Symbol symbol : collection) {
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

	public void testDuplicateIDs() throws Exception {
		assertNoDuplicate("dupID1", "dupID2");
	}

	public void testDuplicateCombos() throws Exception {
		assertNoDuplicate("dupCombo", "dupCombo");
	}

	public void testAddContainsRawSymbol() throws Exception {
		final Symbols symbols = new Symbols();
		final String combo = "testSymbol";
		symbols.addRawSymbol(new Symbol(combo, "s"));
		assertTrue("Added raw symbol not contained afterwards", symbols
				.containRawCombo(combo));
	}
}
