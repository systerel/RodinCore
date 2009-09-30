/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
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
 * Common protocol for carrier sets in Event-B statically checked (SC) contexts.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * An SC carrier set is a carrier set that has been statically checked. An SC
 * carrier set has a name that is returned by
 * {@link org.rodinp.core.IRodinElement#getElementName()} and contains a type
 * that is accessed and manipulated via
 * {@link ISCIdentifierElement}. This interface itself does not
 * contribute any method.
 * </p>
 * 
 * @see org.rodinp.core.IRodinElement#getElementName()
 * @see ISCIdentifierElement#getType(FormulaFactory)
 * @see ISCIdentifierElement#setType(Type, IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 * @since 1.0
 */
public interface ISCCarrierSet extends ITraceableElement, ISCIdentifierElement {

	IInternalElementType<ISCCarrierSet> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".scCarrierSet"); //$NON-NLS-1$

	// No additional method

}
