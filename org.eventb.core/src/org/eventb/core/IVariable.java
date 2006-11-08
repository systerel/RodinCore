/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;


/**
 * Common protocol for Event-B variables (occurring in both machines and
 * events).
 * <p>
 * A variable has a name that is accessed and manipulated via
 * {@link org.eventb.core.IIdentifierElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.eventb.core.IIdentifierElement#getIdentifierString()
 * @see org.eventb.core.IIdentifierElement#setIdentifierString(String)
 * 
 * @author Laurent Voisin
 */
public interface IVariable extends ICommentedElement, IIdentifierElement {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".variable"); //$NON-NLS-1$

	// No additional method

}
