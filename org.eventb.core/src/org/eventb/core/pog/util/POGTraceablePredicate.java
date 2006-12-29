/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.pog.util;

import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * In statically checked files source reference are available by an indirection 
 * (<code>ITraceableElement</code>). Class <code>POGTraceablePredicate</code> should
 * be used for these elements to resolve the indirection.
 * <p>
 * For the described case this class should be prefered over <code>POGPredicate</code>.
 * </p>
 * 
 * @author Stefan Hallerstede
 *
 */
public class POGTraceablePredicate extends POGPredicate {
	
	/**
	 * Creates a predicate with an associated source reference to be stored in a PO file.
	 * 
	 * @param predicate the predicate
	 * @param source the source accessible via one indirection, i.e.
	 * <p>
	 * 	<par>
	 * 	((ITraceableElement) source).getSource(monitor)
	 * 	</par>
	 * <p>
	 * 	yields proper source reference
	 */
	public POGTraceablePredicate(Predicate predicate, IRodinElement source) {
		super(predicate, source);
	}

	@Override
	public IRodinElement getSource() throws RodinDBException {
		
		ITraceableElement element = (ITraceableElement) super.getSource();
		
		return element.getSource();
	}

}
