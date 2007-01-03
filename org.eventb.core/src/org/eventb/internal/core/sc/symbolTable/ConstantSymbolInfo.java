/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.sc.symbolTable.IConstantSymbolInfo;
import org.eventb.core.sc.util.GraphProblem;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ConstantSymbolInfo 
	extends IdentifierSymbolInfo 
	implements IConstantSymbolInfo {

	public ConstantSymbolInfo(
			String symbol, 
			boolean imported, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, imported, element, attribute, component);
	}

	@Override
	public IRodinProblem getConflictWarning() {
		if (isImported())
			return GraphProblem.ConstantNameImportConflictWarning;
		else
			return GraphProblem.ConstantNameConflictWarning;
	}

	@Override
	public IRodinProblem getConflictError() {
		if (isImported())
			return GraphProblem.ConstantNameImportConflictError;
		else
			return GraphProblem.ConstantNameConflictError;
	}

	@Override
	public IRodinProblem getUntypedError() {
		return GraphProblem.UntypedConstantError;
	}

}
