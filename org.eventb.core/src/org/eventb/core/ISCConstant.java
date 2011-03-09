/*******************************************************************************
 * Copyright (c) 2005, 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for constants in Event-B statically checked (SC) contexts.
 * <p>
 * An SC constant is a constant that has been statically checked. An SC constant
 * has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()} and contains a type
 * that is accessed and manipulated via
 * {@link ISCIdentifierElement}. This interface itself does not
 * contribute any method.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see ISCIdentifierElement#getType(FormulaFactory)
 * @see ISCIdentifierElement#setType(Type, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface ISCConstant extends ITraceableElement, ISCIdentifierElement {

	IInternalElementType<ISCConstant> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scConstant"); //$NON-NLS-1$

	// No additional method

}
