/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;


/**
 * Common protocol for Event-B witnesses.
 * <p>
 * A witness has a label that is accessed and manipulated via
 * {@link ILabeledElement} and contains a
 * predicate that is accessed and manipulated via
 * {@link IPredicateElement}. This interface itself does not
 * contribute any methods.
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
 */
public interface IWitness extends ICommentedElement, ILabeledElement, IPredicateElement {
	
	IInternalElementType<IWitness> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".witness"); //$NON-NLS-1$
	
	// No additional method

}
