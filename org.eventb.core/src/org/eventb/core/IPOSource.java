/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Specifies a source element of a proof obligation.
 * The name of an <code>IPOSource</code> describes the role of the element
 * and the contents contains a handle identifier for the element.
 * 
 * @author Stefan Hallerstede
 *
 */
public interface IPOSource extends IInternalElement, ITraceableElement {
	
	IInternalElementType ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".poSource"); //$NON-NLS-1$
	
	String getSourceRole(IProgressMonitor monitor) throws RodinDBException;
	
	void setSourceRole(String role, IProgressMonitor monitor) throws RodinDBException;
	
	String getSourceHandleIdentifier() throws RodinDBException;
	
	IRodinElement getSource(IProgressMonitor monitor) throws RodinDBException;
}
