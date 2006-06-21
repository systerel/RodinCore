/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

/**
 * Common protocol for Event-B axioms.
 * <p>
 * An axiom has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()} and contains a
 * predicate that is accessed and manipulated via
 * {@link org.eventb.core.IPredicateElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see org.eventb.core.IPredicateElement#getPredicateString()
 * @see org.eventb.core.IPredicateElement#setPredicateString(String)
 * 
 * @author Laurent Voisin
 */
public interface IAxiom extends ILabeledElement, IPredicateElement {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".axiom"; //$NON-NLS-1$

	// No additional method

}
