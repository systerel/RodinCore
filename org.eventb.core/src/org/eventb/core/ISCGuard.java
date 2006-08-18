/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;

/**
 * Common protocol for Event-B SC guards.
 * <p>
 * An SC guard is a guard that has been statically checked. An SC guard has a
 * name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()} and contains a
 * predicate that is accessed and manipulated via
 * {@link org.eventb.core.ISCPredicateElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see org.eventb.core.ISCPredicateElement#getPredicate(FormulaFactory,
 *      ITypeEnvironment)
 * @see org.eventb.core.ISCPredicateElement#setPredicate(Predicate)
 * 
 * @author Stefan Hallerstede
 */
public interface ISCGuard extends ITraceableElement, ILabeledElement, ISCPredicateElement {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scGuard"; //$NON-NLS-1$

	// No additional method

}
