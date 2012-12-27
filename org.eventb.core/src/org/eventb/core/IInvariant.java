/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
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
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for Event-B invariants.
 * <p>
 * An invariant has a label that is accessed and manipulated via
 * {@link ILabeledElement} and contains a
 * predicate that is accessed and manipulated via
 * {@link IPredicateElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * @see IPredicateElement#getPredicateString()
 * @see IPredicateElement#setPredicateString(String, IProgressMonitor)
 * 
 * @author Laurent Voisin
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface IInvariant extends ICommentedElement, ILabeledElement, IDerivedPredicateElement {

	IInternalElementType<IInvariant> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".invariant"); //$NON-NLS-1$

	// No additional method

}
