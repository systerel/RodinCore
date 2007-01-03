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
import org.eventb.core.ISCVariable;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;

/**
 * @author Stefan Hallerstede
 *
 */
public class EventVariableSymbolInfo extends VariableSymbolInfo {

	public EventVariableSymbolInfo(
			String symbol, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, false, element, attribute, component);
	}
	
	public void createSCElement(
			IInternalParent parent, 
			IProgressMonitor monitor) throws CoreException {
		
		if (parent == null)
			return;
		
		ISCVariable variable = ((ISCEvent) parent).getSCVariable(getSymbol());
		variable.create(null, monitor);
		variable.setType(getType(), null);
		variable.setSource(getSourceElement(), monitor);
	}

	public boolean isLocal() {
		return true;
	}

}
