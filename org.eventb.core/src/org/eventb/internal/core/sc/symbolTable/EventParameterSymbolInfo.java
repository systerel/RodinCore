/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ISCParameter;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.symbolTable.IParameterSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class EventParameterSymbolInfo extends IdentifierSymbolInfo implements IParameterSymbolInfo {

	public EventParameterSymbolInfo(
			String symbol, 
			IInternalElement element, 
			IAttributeType attribute, 
			String component) {
		super(symbol, false, element, attribute, component);
	}
	
	public ISCIdentifierElement createSCElement(
			IInternalParent parent, 
			IProgressMonitor monitor) throws CoreException {
		
		if (parent == null)
			return null;
		
		ISCParameter parameter = ((ISCEvent) parent).getSCParameter(getSymbol());
		parameter.create(null, monitor);
		parameter.setType(getType(), null);
		parameter.setSource(getElement(), monitor);
		return parameter;
	}

	@Override
	protected void createConflictError(IMarkerDisplay markerDisplay) throws RodinDBException {
		markerDisplay.createProblemMarker(
				getElement(), 
				getSourceAttributeType(), 
				GraphProblem.EventParameterNameConflictError, 
				getSymbol());
	}

	@Override
	protected void createConflictWarning(IMarkerDisplay markerDisplay) throws RodinDBException {
		markerDisplay.createProblemMarker(
				getElement(), 
				getSourceAttributeType(), 
				GraphProblem.EventParameterNameConflictWarning, 
				getSymbol());
	}

	@Override
	public IRodinProblem getUntypedError() {
		return GraphProblem.UntypedParameterError;
	}

}
