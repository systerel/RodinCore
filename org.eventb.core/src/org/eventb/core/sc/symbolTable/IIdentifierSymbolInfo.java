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
import org.rodinp.core.RodinDBException;

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
	 * Sets the type of the identifier if possible.
	 * If the identifier has already a type or is erroneous,
	 * a <code>CoreException</code> is thrown.
	 * @param type The type to be assigned to the identifier.
	 */
	void setType(Type type) throws CoreException;
	
	/**
	 * Returns the ID of the pointer via which this identifier symbol is reachable.
	 * @return the ID of the pointer
	 */
	String getPointer();
	
	/**
	 * Returns whether this symbol was imported via a pointer.
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
	void setVisible() throws CoreException;
	
	/**
	 * Create a statically checked Element for this symbol with parent <codeparent</code>.
	 * 
	 * @param parent the parent of the element to create
	 * @param monitor a progress monitor
	 * @throws RodinDBException if there was a database problem while creating the element
	 */
	void createSCElement(IInternalParent parent, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Attaches an appropriate name conflict error messages to the source element associated
	 * with this symbol. This can be a pointer or the element corresponding to the symbol itself.
	 * @param markerDisplay the marker display to use
	 */
	void issueNameConflictMarker(IMarkerDisplay markerDisplay);
	
	/**
	 * Attaches an appropriate error message to the source element if this identifier 
	 * could not be typed. This message only makes sense for identifiers that have not
	 * been imported.
	 * @param markerDisplay the marker display to use
	 */
	void issueUntypedErrorMarker(IMarkerDisplay markerDisplay);

}
