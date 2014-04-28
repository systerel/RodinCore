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

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.eventb.core.sc.state.IIdentifierSymbolTable;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.junit.Before;
import org.junit.Test;
import org.rodinp.core.IInternalElementType;

/**
 * Unit tests for {@link IdentifierSymbolTable}.
 * 
 * @author Laurent Voisin
 */
public class IdentifierSymbolTableTests extends AbstractSymbolTableTests< //
		ISCIdentifierElement, //
		IInternalElementType<? extends ISCIdentifierElement>, //
		IIdentifierSymbolInfo, //
		IIdentifierSymbolTable> {

	protected static final FormulaFactory ff = FormulaFactory.getDefault();

	protected static final List<IIdentifierSymbolInfo> NO_INFOS = emptyList();

	@Before
	@Override
	public void setUp() {
		super.setUp();
		table = new IdentifierSymbolTable(10, ff);
	}

	@Override
	protected IIdentifierSymbolInfo getInfo(String symbol) {
		return new TestIdentifierSymbolInfo(symbol);
	}

	@Test
	public void parentTable() {
		assertNull(table.getParentTable());
	}

	@Override
	protected final void assertContents(List<IIdentifierSymbolInfo> infos) {
		assertContents(infos, NO_INFOS);
	}

	protected void assertContents(List<IIdentifierSymbolInfo> topInfos,
			List<IIdentifierSymbolInfo> parentInfos) {
		final List<IIdentifierSymbolInfo> infos;
		infos = new ArrayList<IIdentifierSymbolInfo>();
		infos.addAll(topInfos);
		infos.addAll(parentInfos);
		super.assertContents(infos);
		assertFreeIdentifiers(infos);
		assertSymbolInfosFromTop(topInfos);
	}

	private void assertFreeIdentifiers(List<IIdentifierSymbolInfo> infos) {
		final Set<FreeIdentifier> expected = new HashSet<FreeIdentifier>();
		for (final IIdentifierSymbolInfo info : infos) {
			expected.add(ff.makeFreeIdentifier(info.getSymbol(), null));
		}
		final Set<FreeIdentifier> actual = new HashSet<FreeIdentifier>();
		actual.addAll(table.getFreeIdentifiers());
		assertEquals(expected, actual);
	}

	private void assertSymbolInfosFromTop(List<IIdentifierSymbolInfo> infos) {
		for (final IIdentifierSymbolInfo info : infos) {
			assertSame(info, table.getSymbolInfoFromTop(info.getSymbol()));
		}
		assertEquals(new HashSet<IIdentifierSymbolInfo>(infos),
				table.getSymbolInfosFromTop());
	}

	/**
	 * Dummy identifier symbol info for tests.
	 */
	protected static class TestIdentifierSymbolInfo extends
			IdentifierSymbolInfo {

		public TestIdentifierSymbolInfo(String symbol) {
			super(symbol, null, false, null, null, null, null);
		}

	}

}
