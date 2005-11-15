/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.RodinElement;
import org.rodinp.core.UnnamedInternalElement;

/**
 * @author halstefa
 * 
 * Abstract class to represent POPredicates and POPredicateForms uniformly
 *
 */
public abstract class POAnyPredicate extends UnnamedInternalElement {

	public POAnyPredicate(String type, RodinElement parent) {
		super(type, parent);
	}

}
