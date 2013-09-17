/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
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
 * Common protocol for Event-B carrier sets.
 * <p>
 * A carrier set has a name that is accessed and manipulated via
 * {@link IIdentifierElement}. This interface itself does not
 * contribute any method.
 * </p>
 *
 * @see IIdentifierElement#getIdentifierString()
 * @see IIdentifierElement#setIdentifierString(String,IProgressMonitor)
 * 
 * @author Laurent Voisin
 * @author Stefan Hallerstede
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ICarrierSet extends ICommentedElement, IIdentifierElement {

	IInternalElementType<ICarrierSet> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".carrierSet"); //$NON-NLS-1$

	// No additional method

}
