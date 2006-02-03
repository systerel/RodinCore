/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPOAnyPredicate;
import org.eventb.core.IPOModifiedPredicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.UnnamedInternalElement;

/**
 * Implementation of Event-B PO modified predicate as an extension of the Rodin database.
 * <p>
 * This class is intended to be implemented by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it
 * in a database extension. In particular, clients should not use it,
 * but rather use its associated interface <code>IPOModifiedPredicate</code>.
 * </p>
 *
 * @author Stefan Hallerstede
 *
 */
public class POModifiedPredicate extends UnnamedInternalElement implements IPOModifiedPredicate {

	public POModifiedPredicate(IRodinElement parent) {
		super(ELEMENT_TYPE, parent);
	}

	public String getSubstitution() throws RodinDBException {
		return getContents();
	}
	
	public IPOAnyPredicate getPredicate() throws RodinDBException {
		assert getChildren().length == 1;
		IPOAnyPredicate anyPredicate = (IPOAnyPredicate) getChildren()[0];
		return anyPredicate;
	}

}
