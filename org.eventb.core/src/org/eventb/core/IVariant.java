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
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for Event-B variants.
 * <p>
 * A variant has a name that is returned by
 * {@link IRodinElement#getElementName()} and contains an
 * expression that is accessed and manipulated via
 * {@link IExpressionElement}. This interface itself does not
 * contribute any methods.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see IRodinElement#getElementName()
 * @see IExpressionElement#getExpressionString()
 * @see IExpressionElement#setExpressionString(String,IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface IVariant extends ICommentedElement, IExpressionElement {

	IInternalElementType<IVariant> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".variant"); //$NON-NLS-1$

	// No additional method

}
