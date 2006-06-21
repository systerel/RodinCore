/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import org.eventb.core.ISCInternalMachine;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCTheorem;
import org.eventb.core.ISCVariable;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B SC internal machines as an extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>ISCInternalMachine</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class SCInternalMachine extends InternalElement implements
		ISCInternalMachine {

	/**
	 *  Constructor used by the Rodin database. 
	 */
	public SCInternalMachine(String name, IRodinElement parent) {
		super(name, parent);
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.basis.RodinElement#getElementType()
	 */
	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCMachine#getSCVariables()
	 */
	public ISCVariable[] getSCVariables() throws RodinDBException {
		return SCMachineUtil.getSCVariables(this);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCMachine#getSCInvariants()
	 */
	public ISCInvariant[] getSCInvariants() throws RodinDBException {
		return SCMachineUtil.getSCInvariants(this);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISCMachine#getSCTheorems()
	 */
	public ISCTheorem[] getSCTheorems() throws RodinDBException {
		return SCMachineUtil.getSCTheorems(this);
	}

}
