/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.sc.GraphProblem;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public class GuardSymbolInfo extends LabelSymbolInfo {

	public GuardSymbolInfo(
			String symbol, 
			IInternalElement element,
			IAttributeType.String attribute, 
			String component) {
		super(symbol, element, attribute, component);
	}

	@Override
	public IRodinProblem getConflictWarning() {
		return GraphProblem.GuardLabelConflictWarning;
	}

	@Override
	public IRodinProblem getConflictError() {
		return GraphProblem.GuardLabelConflictError;
	}

}
