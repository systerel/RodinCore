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

/**
 * @author halstefa
 *
 * A predicate has a name associated as its attribute 
 * and the "predicate value" in the contents. 
 */
public class POPredicate extends POAnyPredicate implements IPOPredicate {

	public POPredicate(IRodinElement parent) {
		super(ELEMENT_TYPE, parent);
	}
	
	public String getName() {
		return null;
	}

}
