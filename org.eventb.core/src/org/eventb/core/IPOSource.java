/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - removed deprecated method getSourceHandleIdentifier()
 *******************************************************************************/
package org.eventb.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Specifies a source element of a proof obligation.
 * Each proof obligation is usually generated (mainly) from
 * a small selection of source elements. These are associated
 * with the proof obligations by means of <code>IPOSource</code>
 * elements.
 * 
 * @author Stefan Hallerstede
 *
 * @since 1.0
 */
public interface IPOSource extends IInternalElement, ITraceableElement {
	
	IInternalElementType<IPOSource> ELEMENT_TYPE =
		RodinCore.getInternalElementType(EventBPlugin.PLUGIN_ID + ".poSource"); //$NON-NLS-1$
	
	
	/**
	 * Predefined roles for Event-B source elements
	 */
	String DEFAULT_ROLE = "DEFAULT";
	String ABSTRACT_ROLE = "ABSTRACT";
	String CONCRETE_ROLE = "CONCRETE";
	
	/**
	 * Returns the role description of this source element.
	 * 
	 * @return the role description of this source element
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	String getRole() throws RodinDBException;
	
	/**
	 * Sets the role description of this source element.
	 * 
	 * @param role the role description of this source element
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress
	 *            reporting is not desired
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	void setRole(String role, IProgressMonitor monitor) throws RodinDBException;
	
}
