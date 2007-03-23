/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.symbolTable.IActionSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class ActionSymbolInfo extends LabelSymbolInfo implements IActionSymbolInfo {

	public ActionSymbolInfo(
			String symbol, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, element, attribute, component);
	}

	@Override
	protected void createConflictError(IMarkerDisplay markerDisplay) throws RodinDBException {
		markerDisplay.createProblemMarker(
				getSourceElement(), 
				getSourceAttributeType(), 
				GraphProblem.ActionLabelConflictError, 
				getSymbol());
	}

	@Override
	protected void createConflictWarning(IMarkerDisplay markerDisplay) throws RodinDBException {
		markerDisplay.createProblemMarker(
				getSourceElement(), 
				getSourceAttributeType(), 
				GraphProblem.ActionLabelConflictWarning, 
				getSymbol());
	}

}
