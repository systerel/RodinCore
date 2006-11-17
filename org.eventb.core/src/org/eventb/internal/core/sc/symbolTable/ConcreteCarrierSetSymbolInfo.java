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
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.ISCContextFile;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;

/**
 * @author Stefan Hallerstede
 *
 */
public class ConcreteCarrierSetSymbolInfo extends CarrierSetSymbolInfo {

	public ConcreteCarrierSetSymbolInfo(
			String symbol, 
			String link, 
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, link, element, attribute, component);
	}
	
	public void createSCElement(
			IInternalParent parent, 
			IProgressMonitor monitor) throws CoreException {
		ISCCarrierSet set = ((ISCContextFile) parent).getSCCarrierSet(getSymbol());
		set.create(null, monitor);
		set.setType(getType(), null);
		set.setSource(getReferenceElement(), monitor);
	}


}
