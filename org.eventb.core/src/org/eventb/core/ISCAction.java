/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * Common protocol for Event-B checked actions.
 * <p>
 * An SC action is an action that has been statically checked. An SC action has
 * a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()} and contains an
 * assignment that is accessed and manipulated via
 * {@link org.eventb.core.ISCAssignmentElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see org.eventb.core.ISCAssignmentElement#getAssignment(FormulaFactory,
 *      ITypeEnvironment)
 * @see org.eventb.core.ISCAssignmentElement#setAssignment(Assignment)
 * 
 * @author Stefan Hallerstede
 * 
 */
public interface ISCAction extends ITraceableElement, ILabeledElement, ISCAssignmentElement {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".scAction"; //$NON-NLS-1$

	// No additional method

}
