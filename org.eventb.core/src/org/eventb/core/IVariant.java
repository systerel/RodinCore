/*******************************************************************************
 * Copyright (c) 2006, 2018 ETH Zurich and others.
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
 * Common protocol for Event-B variants.
 * <p>
 * A variant has a label that is accessed and manipulated via
 * {@link ILabeledElement} and contains an expression that is accessed and
 * manipulated via {@link IExpressionElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * In order to ensure backward compatibility with plug-in versions prior to 3.4,
 * the absence of a label is processed in the same way as if it was present with
 * the value {@link #DEFAULT_LABEL}.
 * </p>
 *
 * @see ILabeledElement#getLabel()
 * @see ILabeledElement#setLabel(String, IProgressMonitor)
 * @see IExpressionElement#getExpressionString()
 * @see IExpressionElement#setExpressionString(String,IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IVariant extends ICommentedElement, ILabeledElement, IExpressionElement {

	IInternalElementType<IVariant> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".variant"); //$NON-NLS-1$

	/**
	 * Default value for the label, used for backward compatibility with previous
	 * versions.
	 * 
	 * @since 3.4
	 */
	String DEFAULT_LABEL = "vrn";

	// No additional method

}
