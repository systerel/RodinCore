/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

/**
 * Common protocol for Event-B variants.
 * <p>
 * A variant has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()} and contains an
 * expression that is accessed and manipulated via
 * {@link org.eventb.core.IExpressionElement}. This interface itself does not
 * contribute any methods.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see org.eventb.core.IExpressionElement#getExpressionString()
 * @see org.eventb.core.IExpressionElement#setExpressionString(String)
 * 
 * @author Stefan Hallerstede
 */
public interface IVariant extends IExpressionElement {

	String ELEMENT_TYPE = EventBPlugin.PLUGIN_ID + ".variant"; //$NON-NLS-1$

	// No additional method

}
