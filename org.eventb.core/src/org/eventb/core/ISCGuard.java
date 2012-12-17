/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for Event-B SC guards.
 * <p>
 * An SC guard is a guard that has been statically checked. An SC guard has a
 * label that is accessed and manipulated via
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
 * @see ISCPredicateElement#getPredicate(ITypeEnvironment)
 * @see ISCPredicateElement#setPredicate(Predicate, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface ISCGuard extends ITraceableElement, ILabeledElement, ISCPredicateElement, IDerivedPredicateElement {

	IInternalElementType<ISCGuard> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scGuard"); //$NON-NLS-1$

	// No additional method

}
