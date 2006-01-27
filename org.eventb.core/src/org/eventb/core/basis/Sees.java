/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.ISees;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.UnnamedInternalElement;

/**
 * @author halstefa
 *
 */
public class Sees extends UnnamedInternalElement implements ISees {

	public Sees(IRodinElement parent) {
		super(ELEMENT_TYPE, parent);
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.ISees#getSeenContext()
	 */
	public String getSeenContext() throws RodinDBException {
		return getContents();
	}

}
