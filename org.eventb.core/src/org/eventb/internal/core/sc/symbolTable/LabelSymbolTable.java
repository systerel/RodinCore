/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.sc.ILabelSymbolTable;

/**
 * @author Stefan Hallerstede
 *
 */
public class LabelSymbolTable extends SymbolTable implements ILabelSymbolTable {

	public LabelSymbolTable(int size) {
		super(size);
	}

	public String getStateType() {
		return STATE_TYPE;
	}

}
