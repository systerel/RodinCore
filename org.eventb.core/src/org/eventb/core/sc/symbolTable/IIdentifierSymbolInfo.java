/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc.symbolTable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.Type;
import org.eventb.core.sc.IMarkerDisplay;
import org.rodinp.core.IInternalParent;

/**
 * <code>IIdentifierSymbolInfo</code> is a symbol table symbol info for identifiers, 
 * i.e. carrier sets, constants, and variables. It stores the type of an identifier. 
 * The name of the identifier is the key of this symbol info.
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IIdentifierSymbolInfo extends ISymbolInfo {

	/**
	 * Returns the type of the identifier.
	 * 
	 * @return the type of the identifier
	 */
	Type getType();
	
	/**
	 * Sets the type of the identifier.
	 * @param type The type to be assigned to the identifier.
	 * @throws CoreException if this symbol is not mutable
	 */
	void setType(Type type) throws CoreException;
	
	/**
	 * Returns whether this symbol was imported via a pointer to another Event-B component.
	 * @return whether this symbol was imported via a pointer
	 */
	boolean isImported();
	
	/**
	 * Returns whether this symbol is visible, i.e. can be used in formulas.
	 * If all pointers via which this identifier symbol is reachable are in an error
	 * state, this identifier symbol is marked invisible.
	 * <p>
	 * Identifiers that are not imported are always visible.
	 * </p>
	 * @return whether this symbol is visible
	 */
	boolean isVisible();
	
	/**
	 * Set this identifier symbol's visibility to <code>true</code>.
	 * @throws CoreException if this symbol is not mutable
	 */
	void makeVisible() throws CoreException;
	
	/**
	 * Create a statically checked Element for this symbol with the specified parent.
	 * 
	 * @param parent the parent of the element to create
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws CoreException if there was a problem creating the element
	 */
	void createSCElement(IInternalParent parent, IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Attaches an appropriate error message to the source element if this identifier 
	 * could not be typed. This message only makes sense for identifiers that have not
	 * been imported.
	 * @param markerDisplay the marker display to use
	 * @throws CoreException if there was a problem creating the element
	 */
	void createUntypedErrorMarker(IMarkerDisplay markerDisplay) throws CoreException;

}
