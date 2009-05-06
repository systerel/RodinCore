/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
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
 * Common protocol for Event-B actions.
 * <p>
 * An action has a label that is accessed and manipulated via
 * {@link ILabeledElement}, and contains an
 * assignment that is accessed and manipulated via
 * {@link IAssignmentElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * @see IAssignmentElement#getAssignmentString()
 * @see IAssignmentElement#setAssignmentString(String,IProgressMonitor)
 * 
 * @author Laurent Voisin
 * @author Stefan Hallerstede
 */
public interface IAction extends ICommentedElement, ILabeledElement, IAssignmentElement {

	IInternalElementType<IAction> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".action"); //$NON-NLS-1$

	// No additional method

}
