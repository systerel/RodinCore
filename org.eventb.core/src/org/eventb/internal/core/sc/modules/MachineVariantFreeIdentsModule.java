/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.eventb.core.sc.symbolTable.IVariableSymbolInfo;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class MachineVariantFreeIdentsModule extends MachinePredicateFreeIdentsModule {
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#getSymbolInfo(org.eventb.core.ast.FreeIdentifier)
	 */
	@Override
	protected IIdentifierSymbolInfo getSymbolInfo(
			IRodinElement element, 
			FreeIdentifier freeIdentifier,
			IProgressMonitor monitor) throws CoreException {
		IIdentifierSymbolInfo symbolInfo = super.getSymbolInfo(element, freeIdentifier, monitor);
		if (symbolInfo != null && symbolInfo instanceof IVariableSymbolInfo) {
			IVariableSymbolInfo variableSymbolInfo = 
				(IVariableSymbolInfo) symbolInfo;
			if (!variableSymbolInfo.isPreserved())
				return null;
		}
		return symbolInfo;
	}

}
