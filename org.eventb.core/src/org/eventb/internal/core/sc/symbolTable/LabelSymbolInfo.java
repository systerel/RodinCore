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
import org.eventb.core.ILabeledElement;
import org.eventb.core.sc.symbolTable.ILabelSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;

/**
 * @author Stefan Hallerstede
 * 
 */
class LabelSymbolInfo extends SymbolInfo<ILabeledElement, IInternalElementType<? extends ILabeledElement>, ISymbolProblem> implements
		ILabelSymbolInfo {

public LabelSymbolInfo(String symbol,
			IInternalElementType<? extends ILabeledElement> elementType,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component,
			ISymbolProblem conflictProblem) {
		super(symbol, elementType, persistent, problemElement, problemAttributeType,
				component, conflictProblem);
	}

	public ILabeledElement createSCElement(IInternalParent parent,
			String elementName, IProgressMonitor monitor) throws CoreException {
		checkPersistence();
		ILabeledElement element = parent.getInternalElement(getSymbolType(),
				elementName);
		createAttributes(element, monitor);
		return element;
	}

}
