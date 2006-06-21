/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IAssignmentElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Common implementation of Event-B elements that contain an assignment as an
 * extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>IAssignmentElement</code>.
 * </p>
 * 
 * @author Laurent Voisin
 */
public abstract class AssignmentElement extends InternalElement
		implements IAssignmentElement {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public AssignmentElement(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IAssignmentElement#getAssignment()
	 */
	public String getAssignmentString() throws RodinDBException {
		return getContents();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IAssignmentElement#setAssignment(java.lang.String)
	 */
	public void setAssignmentString(String assignment) throws RodinDBException {
		setContents(assignment);
	}
	
}
