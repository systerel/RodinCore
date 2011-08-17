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
import org.eventb.core.EventBAttributes;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.GraphProblem;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class MachineFormulaFreeIdentsModule extends
		FormulaFreeIdentsModule {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.internal.core.sc.modules.PredicateFreeIdentsModule#getSymbolInfo
	 * (org.eventb.core.ast.FreeIdentifier)
	 */
	@Override
	protected IIdentifierSymbolInfo getSymbolInfo(IInternalElement element,
			FreeIdentifier freeIdentifier, IProgressMonitor monitor)
			throws CoreException {
		IIdentifierSymbolInfo symbolInfo = super.getSymbolInfo(element,
				freeIdentifier, monitor);
		if (symbolInfo != null
				&& symbolInfo.getSymbolType() == ISCVariable.ELEMENT_TYPE) {
			if (!symbolInfo
					.getAttributeValue(EventBAttributes.ABSTRACT_ATTRIBUTE)
					&& !symbolInfo
							.getAttributeValue(EventBAttributes.CONCRETE_ATTRIBUTE)) {
				createProblemMarker(element, getAttributeType(),
						GraphProblem.VariableHasDisappearedError, symbolInfo
								.getSymbol());
				return null;
			}
		}
		return symbolInfo;
	}

}
