/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;

/**
 * This class is only used internally to be able to reuse the
 * <code>LabeledFormulaModule</code> for variants. Instances of
 * this class are never stored in the label symbol table.
 * 
 * @author Stefan Hallerstede
 *
 */
public class VariantSymbolInfo extends LabelSymbolInfo {

	public VariantSymbolInfo(String symbol, IRodinElement element, String component) {
		super(symbol, element, component);
	}

	@Override
	public IRodinProblem getConflictWarning() {
		return null;
	}

	@Override
	public IRodinProblem getConflictError() {
		return null;
	}

}
