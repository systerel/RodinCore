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

import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.state.ILabelSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.ContextLabelSymbolTable;
import org.eventb.internal.core.sc.symbolTable.LabelSymbolInfo;
import org.eventb.internal.core.sc.symbolTable.SymbolTable;
import org.junit.Before;
import org.rodinp.core.IInternalElementType;

/**
 * Unit tests for a simple sub-class of {@link SymbolTable} that does not add
 * any behavior. This corresponds for instance to an implementation such as
 * {@link ContextLabelSymbolTable}.
 * 
 * @author Laurent Voisin
 */
public class LabelSymbolTableTests extends AbstractSymbolTableTests< //
		ILabeledElement, //
		IInternalElementType<? extends ILabeledElement>, //
		ILabelSymbolInfo, //
		LabelSymbolTable> {

	@Before
	@Override
	public void setUp() {
		super.setUp();
		table = new LabelSymbolTable();
	}

	@Override
	protected ILabelSymbolInfo getInfo(String symbol) {
		return new TestLabelSymbolInfo(symbol);
	}

	/**
	 * Dummy label symbol info for tests.
	 */
	private static class TestLabelSymbolInfo extends LabelSymbolInfo {

		public TestLabelSymbolInfo(String symbol) {
			super(symbol, null, false, null, null, null, null);
		}

	}

}
