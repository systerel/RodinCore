/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - code refactoring
 *******************************************************************************/
package org.eventb.internal.core.sc.modules;

import static org.eventb.core.EventBAttributes.ABSTRACT_ATTRIBUTE;
import static org.eventb.core.EventBAttributes.CONCRETE_ATTRIBUTE;
import static org.eventb.core.sc.GraphProblem.VariableHasDisappearedError;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.sc.state.IIdentifierSymbolInfo;
import org.rodinp.core.IInternalElement;

/**
 * @author Stefan Hallerstede
 * 
 */
public abstract class MachineFormulaFreeIdentsModule extends
		FormulaFreeIdentsModule {

	@Override
	protected IIdentifierSymbolInfo getSymbolInfo(IInternalElement element,
			FreeIdentifier freeIdentifier, IProgressMonitor monitor)
			throws CoreException {
		final IIdentifierSymbolInfo symbolInfo = super.getSymbolInfo(element,
				freeIdentifier, monitor);
		if (isDisappearedVariable(symbolInfo)) {
			createProblemMarker(element, freeIdentifier,
					VariableHasDisappearedError);
			return null;
		}
		return symbolInfo;
	}

	private boolean isDisappearedVariable(IIdentifierSymbolInfo symbolInfo) {
		return symbolInfo != null
				&& symbolInfo.getSymbolType() == ISCVariable.ELEMENT_TYPE
				&& !symbolInfo.getAttributeValue(ABSTRACT_ATTRIBUTE)
				&& !symbolInfo.getAttributeValue(CONCRETE_ATTRIBUTE);
	}

}
