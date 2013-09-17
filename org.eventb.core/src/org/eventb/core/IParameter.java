/*******************************************************************************
 * Copyright (c) 2008, 2013 University of Southampton and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University of Southampton - initial API and implementation
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

/**
 * Common protocol for Event-B event parameters.
 * <p>
 * A parameter has a name that is accessed and manipulated via
 * {@link IIdentifierElement}. This interface itself does not
 * contribute any method.
 * </p>
 *
 * @see IIdentifierElement#getIdentifierString()
 * @see IIdentifierElement#setIdentifierString(String,IProgressMonitor)
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IParameter extends ICommentedElement, IIdentifierElement {

	IInternalElementType<IParameter> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".parameter"); //$NON-NLS-1$

	// No additional method

}
