/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCConstant;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.symbolTable.IConstantSymbolInfo;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class ConstantSymbolInfo 
	extends IdentifierSymbolInfo 
	implements IConstantSymbolInfo {

	/**
	 * Do not use this constructor.
	 * Use the <code>SymbolInfoFactory</code> instead!
	 * 
	 * {@link SymbolInfoFactory}
	 */
	public ConstantSymbolInfo(
			String symbol, 
			String link, 
			IRodinElement element, 
			String component) {
		super(symbol, link, element, component);
	}

	public void createSCElement(
			IInternalParent parent, 
			IProgressMonitor monitor) throws RodinDBException {
		ISCConstant constant = 
			(ISCConstant) parent.createInternalElement(
					ISCConstant.ELEMENT_TYPE, getSymbol(), null, monitor);
		constant.setType(getType());
		constant.setSource(getReferenceElement(), monitor);
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
