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
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.sc.symbolTable.SymbolTable;
import org.rodinp.core.IInternalElementType;

/**
 * Dummy label symbol table for tests.
 */
public class LabelSymbolTable extends SymbolTable<ILabeledElement, //
		IInternalElementType<? extends ILabeledElement>, ILabelSymbolInfo> {

	public LabelSymbolTable() {
		super(10);
	}

	@Override
	public IStateType<LabelSymbolTable> getStateType() {
		return null;
	}

}
