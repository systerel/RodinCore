/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for internal contexts in Event-B statically checked (SC)
 * files.
 * <p>
 * Internal SC contexts are the internalized form of statically checked context.
 * They are used to remove (transitive) dependencies between contexts (and
 * machines). SC contexts on which a context (resp. machine) depends are simply
 * copied inside the SC context (resp. SC machine).
 * </p>
 * <p>
 * An internal SC context has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()}. Its child elements
 * can be manipulated via interface {@link org.eventb.core.ISCContext}. This
 * interface itself does not contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see org.eventb.core.ISCContext
 * 
 * @author Stefan Hallerstede
 */
public interface ISCInternalContext extends IInternalElement, ISCContext {

	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scInternalContext"); //$NON-NLS-1$

	// No additional method

}
