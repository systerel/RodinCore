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
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.ITypeEnvironment;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for Event-B checked actions.
 * <p>
 * An SC action is an action that has been statically checked. An SC action has
 * a label that is accessed and manipulated via
 * {@link ILabeledElement} and contains an
 * assignment that is accessed and manipulated via
 * {@link ISCAssignmentElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * @see ISCAssignmentElement#getAssignment(ITypeEnvironment)
 * @see ISCAssignmentElement#setAssignment(Assignment, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * 
 * @since 1.0
 */
public interface ISCAction extends ITraceableElement, ILabeledElement, ISCAssignmentElement {

	IInternalElementType<ISCAction> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scAction"); //$NON-NLS-1$

	// No additional method

}
