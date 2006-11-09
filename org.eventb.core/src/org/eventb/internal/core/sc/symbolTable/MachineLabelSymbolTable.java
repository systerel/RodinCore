/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.sc.state.IMachineLabelSymbolTable;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineLabelSymbolTable extends SymbolTable<ILabelSymbolInfo> implements IMachineLabelSymbolTable {

	public MachineLabelSymbolTable(int size) {
		super(size);
	}

	public String getStateType() {
		return STATE_TYPE;
	}

}
