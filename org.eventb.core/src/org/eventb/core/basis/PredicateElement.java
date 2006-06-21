/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.eventb.core.IPredicateElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

/**
 * Common implementation of Event-B elements that contain a predicate, as an
 * extension of the Rodin database.
 * <p>
 * This class is intended to be subclassed by clients that want to extend this
 * internal element type.
 * </p>
 * <p>
 * This class should not be used in any other way than subclassing it in a
 * database extension. In particular, clients should not use it, but rather use
 * its associated interface <code>IPredicateElement</code>.
 * </p>
 * 
 * @author Laurent Voisin
 */
public abstract class PredicateElement extends InternalElement
		implements IPredicateElement {
	
	public PredicateElement(String name, IRodinElement parent) {
		super(name, parent);
	}
	
	public String getPredicateString() throws RodinDBException {
		return getContents();
	}

	public void setPredicateString(String predicate) throws RodinDBException {
		setContents(predicate);
	}

}
