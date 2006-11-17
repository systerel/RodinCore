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
import org.eventb.internal.core.Util;
import org.eventb.internal.core.sc.Messages;
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
			String pointer,
			IInternalElement element, 
			IAttributeType.String attribute, 
			String component) {
		super(symbol, element, attribute, component);
		
		type = null;
		this.imported = pointer != null;
		visible = !imported;
		this.pointer = pointer;
	}
	
	// whether this symbol is contained in an abstraction, or is "seen"
	private final boolean imported; 
	
	// the name of the pointer via which the symbol is reachable
	// if the symbol is abstract
	private final String pointer; 
	
	private Type type;
	
	private boolean visible;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#isAbstract()
	 */
	public boolean isImported() {
		return imported;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#getType()
	 */
	public Type getType() {
		return type;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#setType(org.eventb.core.ast.Type)
	 */
	public void setType(Type type) throws CoreException {
		if (!isMutable())
			throw Util.newCoreException(Messages.symtab_ImmutableSymbolViolation);
		this.type = type;
	}

	/**
	 * @return whether the the identifier should be considered declared or not.
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Set the visibility status of the identifier to true.
	 */
	public void setVisible() throws CoreException {
		if (!isMutable())
			throw Util.newCoreException(Messages.symtab_ImmutableSymbolViolation);
		this.visible = true;
	}

	/**
	 * @return Returns the pointer.
	 */
	public String getPointer() {
		return pointer;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IIdentifierSymbolInfo#issueUntypedErrorMarker(org.eventb.core.sc.IMarkerDisplay)
	 */
	public void createUntypedErrorMarker(IMarkerDisplay markerDisplay) throws CoreException {
		
		markerDisplay.createProblemMarker(
				getReferenceElement(), 
				getReferenceAttributeType(), 
				getUntypedError(), 
				getSymbol());
		
	}

	public abstract IRodinProblem getUntypedError();
}
