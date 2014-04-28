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

import static org.junit.Assert.assertSame;

import org.eventb.internal.core.sc.symbolTable.IdentifierSymbolTable;
import org.eventb.internal.core.sc.symbolTable.StackedIdentifierSymbolTable;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link StackedIdentifierSymbolTable}.
 * 
 * @author Laurent Voisin
 */
public class StackedIdentifierSymbolTableTests extends
		IdentifierSymbolTableTests {

	private IdentifierSymbolTable parent;

	@Before
	@Override
	public void setUp() {
		super.setUp();
		parent = new IdentifierSymbolTable(10, ff);
		table = new StackedIdentifierSymbolTable(parent, 10, ff);
	}

	@Override
	public void parentTable() {
		assertSame(parent, table.getParentTable());
	}

	@Test
	public void putOneSymbolInParent() throws Exception {
		parent.putSymbolInfo(foo);
		assertContents(NO_INFOS, list(foo));
	}

	@Test
	public void putOneSymbolInParentAndTop() throws Exception {
		parent.putSymbolInfo(foo);
		table.putSymbolInfo(bar);
		assertContents(list(bar), list(foo));
	}

	@Test
	public void putConflictBetweenParentAndTop() throws Exception {
		parent.putSymbolInfo(foo);
		assertContents(NO_INFOS, list(foo));
		assertPutFails(foo2);
		assertContents(NO_INFOS, list(foo));
	}

}
