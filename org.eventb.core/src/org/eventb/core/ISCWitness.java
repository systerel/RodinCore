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
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for Event-B SC witnesses.
 * <p>
 * An SC witness is a witness that has been statically checked. An SC witness
 * has a label that is accessed and manipulated via
 * {@link ILabeledElement}. It contains a
 * predicate that is accessed and manipulated via
 * {@link ISCPredicateElement}. 
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * @see ISCPredicateElement#getPredicate(ITypeEnvironment)
 * @see ISCPredicateElement#setPredicate(org.eventb.core.ast.Predicate, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface ISCWitness extends ITraceableElement, ILabeledElement, ISCPredicateElement {

	IInternalElementType<ISCWitness> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scWitness"); //$NON-NLS-1$

}
