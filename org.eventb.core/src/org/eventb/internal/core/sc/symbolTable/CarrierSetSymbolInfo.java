/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCCarrierSet;
import org.eventb.core.sc.symbolTable.ICarrierSetSymbolInfo;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author Stefan Hallerstede
 *
 */
public class CarrierSetSymbolInfo 
	extends IdentifierSymbolInfo
	implements ICarrierSetSymbolInfo {

	/**
	 * Do not use this constructor.
	 * Use the <code>SymbolInfoFactory</code> instead!
	 * 
	 * {@link SymbolInfoFactory}
	 */
	public CarrierSetSymbolInfo(
			String symbol, 
			String link, 
			IRodinElement element, 
			String component) {
		super(symbol, link, element, component);
	}

	@Override
	public String getNameImportConflictMessage() {
		return Messages.scuser_CarrierSetNameImportConflict;
	}

	@Override
	public String getNameConflictMessage() {
		return Messages.scuser_CarrierSetNameConflict;
	}

	public void createSCElement(
			IInternalParent parent, 
			IProgressMonitor monitor) throws RodinDBException {
		ISCCarrierSet set = 
			(ISCCarrierSet) parent.createInternalElement(
					ISCCarrierSet.ELEMENT_TYPE, getSymbol(), null, monitor);
		set.setType(getType());
		set.setSource(getReferenceElement(), monitor);
	}

	@Override
	public String getUntypedErrorMessage() {
		return Messages.scuser_UntypedCarrierSetError;
	}

}
