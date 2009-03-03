/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 ******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPRProof;
import org.eventb.core.IPRRoot;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B (unchecked) contexts as an extension of the Rodin database.
 * <p>
 * This class should not be used directly by any client except the Rodin
 * database. In particular, clients should not use it, but rather use its
 * associated interface <code>IPRRoot</code>.
 * </p>
 *
 * @author Laurent Voisin
 */
public class PRRoot extends EventBRoot implements IPRRoot{
	
	/**
	 *  Constructor used by the Rodin database. 
	 */
	public PRRoot(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public IInternalElementType<IPRRoot> getElementType() {
		return ELEMENT_TYPE;
	}

	
	public IPRProof[] getProofs() throws RodinDBException {
		return getChildrenOfType(IPRProof.ELEMENT_TYPE);
	}

	public IPRProof getProof(String name) {
		return getInternalElement(IPRProof.ELEMENT_TYPE,name);
	}
}
