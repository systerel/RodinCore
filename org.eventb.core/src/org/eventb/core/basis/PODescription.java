/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core.basis;

import java.util.ArrayList;

import org.eventb.core.IPODescription;
import org.eventb.core.IPOHint;
import org.eventb.core.IPOSource;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Implementation of Event-B PO description as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPODescription</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public class PODescription extends InternalElement implements IPODescription {

	public PODescription(String name, IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public String getElementType() {
		return ELEMENT_TYPE;
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getName()
	 */
	public String getName() {
		return getElementName();
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getSources()
	 */
	public IPOSource[] getSources() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOSource.ELEMENT_TYPE);
		IPOSource[] sources = new IPOSource[list.size()];
		list.toArray(sources);
		return sources;
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.IPODescription#getHints()
	 */
	public IPOHint[] getHints() throws RodinDBException {
		ArrayList<IRodinElement> list = getFilteredChildrenList(IPOHint.ELEMENT_TYPE);
		IPOHint[] hints = new IPOHint[list.size()];
		list.toArray(hints);
		return hints;
	}

}
