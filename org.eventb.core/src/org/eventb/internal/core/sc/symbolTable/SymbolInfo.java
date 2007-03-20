/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.IMarkerDisplay;
import org.eventb.core.sc.symbolTable.ISymbolInfo;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.sc.Messages;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinProblem;
import org.rodinp.core.RodinDBException;


/**
 * @author Stefan Hallerstede
 *
 */
public abstract class SymbolInfo implements ISymbolInfo {

	private final String symbol;
	
	private boolean error;
	
	private boolean mutable;
	
	private IAttributeType.String sourceAttributeType;
	
	private IInternalElement sourceElement;

	protected final String component; 
	
	public SymbolInfo(
			String symbol, 
			IInternalElement sourceElement, 
			IAttributeType.String srcAttribute, 
			String component) {
		this.sourceAttributeType = srcAttribute;
		this.sourceElement = sourceElement;
		this.component = component;
		mutable = true;
		error = false;
		this.symbol = symbol;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ISymbolInfo#hasError()
	 */
	public boolean hasError() {
		return error;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ISymbolInfo#setError()
	 */
	public void setError() throws CoreException {
		if (mutable)
			error = true;
		else
			throw Util.newCoreException(Messages.symtab_ImmutableSymbolViolation);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ISymbolInfo#isMutable()
	 */
	public boolean isMutable() {
		return mutable;
	}

	/**
	 * Make this symbol info immutable.
	 * It cannot be made mutable again.
	 */
	public void makeImmutable() {
		mutable = false;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.ISymbolInfo#getSymbol()
	 */
	public String getSymbol() {
		return symbol;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object o) {
		SymbolInfo that = (SymbolInfo) o;
		return this.symbol.compareTo(that.symbol);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof SymbolInfo && symbol.equals(((SymbolInfo) obj).getSymbol());
	}

	@Override
	public int hashCode() {
		return symbol.hashCode();
	}

	public String getComponentName() {
		return component;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return symbol;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.sc.symbolTable.ISymbolInfo#getSourceElement()
	 */
	public IInternalElement getSourceElement() {
		return sourceElement;
	}

	/**
	 * Sets the source element for this symbol.
	 * 
	 * @param sourceElement the source element
	 * @param sourceAttributeType the attribute in the source element to which attach markers 
	 */
	public final void setSourceElement(
			IInternalElement sourceElement, IAttributeType.String sourceAttributeType) {
		this.sourceElement = sourceElement;
		this.sourceAttributeType = sourceAttributeType;
	}
	
	public void createConflictMarker(IMarkerDisplay markerDisplay) throws RodinDBException {
		if (isMutable())
			markerDisplay.createProblemMarker(
					getSourceElement(), 
					getSourceAttributeType(), 
					getConflictError(), 
					getSymbol(), getComponentName());
		else
			markerDisplay.createProblemMarker(
					getSourceElement(), 
					getSourceAttributeType(), 
					getConflictWarning(), 
					getSymbol(), getComponentName());
	}
	
	public abstract IRodinProblem getConflictWarning();
	public abstract IRodinProblem getConflictError();
	
	public final IAttributeType.String getSourceAttributeType() {
		return sourceAttributeType;
	}

}
