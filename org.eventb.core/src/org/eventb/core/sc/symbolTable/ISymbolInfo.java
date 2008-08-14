/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.sc.IMarkerDisplay;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;


/**
 * Basic class for symbols to be stored in a symbol table, {@link ISymbolTable}.
 * 
 * @author Stefan Hallerstede
 *
 */
public interface ISymbolInfo extends Comparable<ISymbolInfo>, IAttributedSymbol {
	
	/**
	 * Returns the symbol sting of this symbol info.
	 * 
	 * @return the symbol string
	 */
	String getSymbol();
	
	/**
	 * Returns the name of the component that contains this symbol.
	 * 
	 * @return name of the component that contains this symbol
	 */
	String getComponentName();
	
	/**
	 * Returns an element to which to attach problem markers. This is not
	 * necessarily the source element from which the symbol was extracted.
	 * <p>
	 * For example, a constant <code>IConstant</code> <i>c</i> of an Event-B
	 * context <code>IContextFile</code> <i>C</i> will be represented by an
	 * identifier symbol. In context <i>C</i> the element returned is the
	 * element <code>IConstant</code> <i>c</i>. In a context <i>D</i> that sees
	 * (<code>ISeesContext</code> <i>s</i>) context <i>C</i> the element
	 * returned for the identifier symbol is <i>s</i>.
	 * </p>
	 * 
	 * @return the element associated with this symbol info
	 */
	IInternalElement getElement();
	
	/**
	 * Returns whether this symbol is erroneous.
	 * 
	 * @return <code>true</code> if the symbol is erroneous, <code>false</code> otherwise
	 */
	boolean hasError();
	
	/**
	 * Marks this symbol as erroneous.
	 * 
	 * @throws CoreException if the symbol info is not mutable
	 */
	void setError() throws CoreException;
	
	/**
	 * Returns whether the symbol info is (still) mutable.
	 * The static checker can mark symbol infos as immutable:
	 * this removes the possibility to mark a symbol as erroneous.
	 * 
	 * @return whether or not the symbol info is mutable.
	 */
	boolean isMutable();
	
	/**
	 * Makes the symbol immutable
	 */
	void makeImmutable();
	
	/**
	 * Returns whether the symbol is persistent. That is whether it can be
	 * serialized by means of
	 * <code>ISCIdentifierElement createSCElement(IInternalParent,IProgressMonitor)</code>.
	 * 
	 * @return whether the symbol is persistent
	 */
// TODO	boolean isPersistent();
	
	/**
	 * Create a statically checked element for this symbol with the specified parent.
	 * 
	 * @param parent the parent of the element to create
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @return the created statically checked identifier element
	 * @throws CoreException if there was a problem creating the element
	 */
// TODO	ISCIdentifierElement createSCElement(IInternalParent parent, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Creates a suitable marker for collisions in the symbol table.
	 * 
	 * @param markerDisplay a place to put the marker
	 * @throws RodinDBException if there was problem creating the marker
	 */
	void createConflictMarker(IMarkerDisplay markerDisplay) throws RodinDBException;
}
