/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPROOF;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.UnnamedInternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class Proof extends UnnamedInternalElement implements IPROOF {

	public Proof(IRodinElement parent) {
		super(ELEMENT_TYPE, parent);
	}

	public Status getStatus() throws RodinDBException {
		if (getContents().compareToIgnoreCase("PENDING") == 0) return Status.PENDING;
		if (getContents().compareToIgnoreCase("DISCHARGED") == 0) return Status.DISCHARGED;
		return null;
	}

}
