/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for Event-B SC invariants.
 * <p>
 * An SC invariant is an invariant that has been statically checked. An SC
 * invariant has a label that is accessed and manipulated via
 * {@link ILabeledElement} and contains a
 * predicate that is accessed and manipulated via
 * {@link ISCPredicateElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * @see ISCPredicateElement#getPredicate(FormulaFactory,
 *      ITypeEnvironment)
 * @see ISCPredicateElement#setPredicate(Predicate, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public interface ISCInvariant 
extends ITraceableElement, ILabeledElement, ISCPredicateElement, IDerivedPredicateElement {

	IInternalElementType<ISCInvariant> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scInvariant"); //$NON-NLS-1$

	// No additional method

}
