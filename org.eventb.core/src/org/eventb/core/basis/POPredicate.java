/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPOPredicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * Implementation of Event-B PO predicate as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPOPredicate</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class POPredicate extends POAnyPredicate implements IPOPredicate {

	public POPredicate(IRodinElement parent) {
		super(ELEMENT_TYPE, parent);
	}
	
	public String getName() {
		return null;
	}

	public String getPredicate() throws RodinDBException {
		return getContents();
	}

}
