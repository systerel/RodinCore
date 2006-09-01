/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for variables in Event-B statically checked (SC) machines.
 * <p>
 * An SC variable is a variable that has been statically checked. An SC variable
 * has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()} and contains a type
 * that is accessed and manipulated via
 * {@link org.eventb.core.ISCIdentifierElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see org.eventb.core.ISCIdentifierElement#getType(FormulaFactory)
 * @see org.eventb.core.ISCIdentifierElement#setType(Type)
 * 
 * @author Stefan Hallerstede
 * 
 */
public interface ISCVariable extends ITraceableElement, ISCIdentifierElement {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scVariable"; //$NON-NLS-1$

	/**
	 * A variable name that has been used in an abstraction but not in some refinement
	 * cannot be used again. It is "forbidden".
	 * 
	 * @param value the "forbidden" status of the variable name
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setForbidden(boolean value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns whether the variable name is forbidden or not.
	 * 
	 * @return whether the variable name is forbidden or not
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean isForbidden(IProgressMonitor monitor) throws RodinDBException;
	/**
	 * A variable name that has been used in the abstraction and the refinement
	 * are called "preserved". A forbidden variable must not be preserved.
	 * 
	 * @param value the "preserved" status of the variable name
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setPreserved(boolean value, IProgressMonitor monitor) throws RodinDBException;
	
	/**
	 * Returns whether the variable name is preserved or not.
	 * 
	 * @return whether the variable name is preserved or not
	 * 
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	boolean isPreserved(IProgressMonitor monitor) throws RodinDBException;

}
