/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog;

import org.eventb.core.ast.Predicate;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public class POGPredicate {
	
	private final IRodinElement source;
	private final Predicate predicate;
	
	public POGPredicate(IRodinElement source, Predicate predicate) {
		this.source = source;
		this.predicate = predicate;
	}
	
	IRodinElement getSource() {
		return source;
	}
	
	Predicate getPredicate() {
		return predicate;
	}

}
