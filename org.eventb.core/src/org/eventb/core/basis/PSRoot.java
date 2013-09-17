/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IPSRoot</code>.
 * </p>
 *
 * @author Laurent Voisin
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class PSRoot extends EventBRoot implements IPSRoot{
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public PSRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPSRoot> getElementType() {
		return ELEMENT_TYPE;
	}

	@Override
	public IPSStatus[] getStatuses() throws RodinDBException {
		return getChildrenOfType(PSStatus.ELEMENT_TYPE);
	}

	@Override
	public IPSStatus getStatus(String name) {
		return getInternalElement(IPSStatus.ELEMENT_TYPE, name);
	}
}
