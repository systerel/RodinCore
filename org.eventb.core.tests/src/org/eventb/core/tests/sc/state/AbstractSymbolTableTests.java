/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.tests.sc.state;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.state.ISymbolInfo;
import org.eventb.core.sc.state.ISymbolTable;
import org.eventb.internal.core.sc.symbolTable.SymbolTable;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

/**
 * Common unit tests for all sub-classes of {@link SymbolTable}.
 * 
 * @author Laurent Voisin
 */
public abstract class AbstractSymbolTableTests< //
E extends IInternalElement, //
T extends IInternalElementType<? extends E>, //
I extends ISymbolInfo<E, T>, //
S extends ISymbolTable<E, T, I>> {

	protected S table;
	protected I foo;
	protected I bar;
	protected I foo2;

	@Before
	public void setUp() {
		foo = getInfo("foo");
		bar = getInfo("bar");
		foo2 = getInfo("foo");
	}

	/**
	 * Returns a new symbol information for the given symbol.
	 */
	protected abstract I getInfo(String name);

	@Test
	public void emptyTable() {
		final String symbol = foo.getSymbol();
		assertFalse(table.containsKey(symbol));
		assertNull(table.getSymbolInfo(symbol));
		assertContents(list());
	}

	@Test
	public void putOneSymbol() throws Exception {
		table.putSymbolInfo(foo);
		assertContents(list(foo));
	}

	@Test
	public void putTwoSymbols() throws Exception {
		table.putSymbolInfo(foo);
		table.putSymbolInfo(bar);
		assertContents(list(foo, bar));
	}

	@Test
	public void putConflict() throws Exception {
		table.putSymbolInfo(foo);
		assertContents(list(foo));
		assertPutFails(foo2);
		assertContents(list(foo));
	}

	/**
	 * Verifies that the table contains the expected information. Sub-classes
	 * can override to add more checks.
	 */
	protected void assertContents(List<I> infos) {
		for (final I info : infos) {
			assertGetSymbolInfo(info);
		}
	}

	private void assertGetSymbolInfo(I expected) {
		final String symbol = expected.getSymbol();
		assertTrue(table.containsKey(symbol));
		assertSame(expected, table.getSymbolInfo(symbol));
	}

	protected final void assertPutFails(I info) {
		assertFalse(table.tryPutSymbolInfo(info));
		try {
			table.putSymbolInfo(info);
			fail("Should have raised an exception");
		} catch (CoreException e) {
			// ok
		}
	}

	// Go through an Object vararg to avoid a warning when calling
	// We cannot use Arrays.asList() directly because it gets annoying in
	// presence of empty lists.
	@SuppressWarnings("unchecked")
	protected List<I> list(Object... infos) {
		return (List<I>) Arrays.asList(infos);
	}

}
