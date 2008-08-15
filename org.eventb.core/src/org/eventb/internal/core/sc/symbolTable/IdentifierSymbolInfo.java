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
import org.eventb.core.ISCIdentifierElement;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;

/**
 * @author Stefan Hallerstede
 * 
 */
class IdentifierSymbolInfo
		extends
		SymbolInfo<ISCIdentifierElement, IInternalElementType<? extends ISCIdentifierElement>, ITypedSymbolProblem>
		implements IIdentifierSymbolInfo {

	public IdentifierSymbolInfo(String symbol,
			IInternalElementType<? extends ISCIdentifierElement> elementType,
			boolean persistent, IInternalElement problemElement,
			IAttributeType problemAttributeType, String component,
			ITypedSymbolProblem conflictProblem) {
		super(symbol, elementType, persistent, problemElement,
				problemAttributeType, component, conflictProblem);
		this.imported = false;
	}

	// whether this symbol is contained in an abstraction, or is "seen"
	private final boolean imported;

	private Type type;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#isAbstract()
	 */
	@Deprecated
	public final boolean isImported() {
		return imported;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#getType()
	 */
	public final Type getType() {
		return type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.IIdentifierSymbolInfo#setType(org.eventb.core.ast.
	 * Type)
	 */
	public final void setType(Type type) throws CoreException {
		assertMutable();
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.sc.IIdentifierSymbolInfo#issueUntypedErrorMarker(org.
	 * eventb.core.sc.IMarkerDisplay)
	 */
	public final void createUntypedErrorMarker(IMarkerDisplay markerDisplay)
			throws CoreException {

		markerDisplay.createProblemMarker(getElement(),
				getSourceAttributeType(), getConflictProblem()
						.getUntypedError(), getSymbol());

	}

	public ISCIdentifierElement createSCElement(IInternalParent parent,
			IProgressMonitor monitor) throws CoreException {
		checkPersistence();
		ISCIdentifierElement element = parent.getInternalElement(getSymbolType(),
				getSymbol());
		element.create(null, monitor);
		createAttributes(element, monitor);
		element.setType(getType(), monitor);
		return element;
	}
}
