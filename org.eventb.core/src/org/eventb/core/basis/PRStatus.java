/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPRStatus;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.UnnamedInternalElement;

/**
 * @author Farhad Mehta
 *
 */
public class PRStatus extends UnnamedInternalElement implements IPRStatus {

	public PRStatus(IRodinElement parent) {
		super(ELEMENT_TYPE, parent);
	}

	public Overview getOverview() throws RodinDBException {
		if (getContents().compareToIgnoreCase("PENDING") == 0) return Overview.PENDING;
		if (getContents().compareToIgnoreCase("DISCHARGED") == 0) return Overview.DISCHARGED;
		return null;
	}

}
