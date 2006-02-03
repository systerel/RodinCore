/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.basis;

import org.rodinp.core.IRodinElement;
import org.rodinp.core.basis.UnnamedInternalElement;

/**
 * @author Stefan Hallerstede
 * 
 * Abstract class to represent @link org.eventb.core.basis.POPredicate 
 * and @link org.eventb.core.basis.POModifiedPredicate uniformly
 *
 * <p>
 * This class is not intended to be implemented by clients.
 * Please, extend the classes @link org.eventb.core.IPOPredicate
 * and @link org.eventb.core.IPOModifiedPredicate.
 * </p>
 */
public abstract class POAnyPredicate extends UnnamedInternalElement {

	public POAnyPredicate(String type, IRodinElement parent) {
		super(type, parent);
	}

}
