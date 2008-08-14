/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.symbolTable.IIdentifierSymbolInfo;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class IdentifierSymbolInfo 
	extends SymbolInfo 
	implements IIdentifierSymbolInfo {

	public IdentifierSymbolInfo(
			String symbol, 
			boolean imported,
			IInternalElement element, 
			IAttributeType attribute, 
			String component) {
		super(symbol, element, attribute, component);
		
		type = null;
		this.imported = imported;
	}
	
	// whether this symbol is contained in an abstraction, or is "seen"
	private final boolean imported; 
	
	private Type type;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#isAbstract()
	 */
	public final boolean isImported() {
		return imported;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#getType()
	 */
	public final Type getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#setType(org.eventb.core.ast.Type)
	 */
	public final void setType(Type type) throws CoreException {
		assertMutable();
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#issueUntypedErrorMarker(org.eventb.core.sc.IMarkerDisplay)
	 */
	public final void createUntypedErrorMarker(IMarkerDisplay markerDisplay) throws CoreException {
		
		markerDisplay.createProblemMarker(
				getElement(), 
				getSourceAttributeType(), 
				getUntypedError(), 
				getSymbol());
		
	}

	public abstract IRodinProblem getUntypedError();
}
