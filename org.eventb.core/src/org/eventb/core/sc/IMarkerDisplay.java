/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import org.eclipse.core.resources.IMarker;
import org.rodinp.core.IRodinElement;

/**
 * @author Stefan Hallerstede
 *
 */
public interface IMarkerDisplay {
	
	static int SEVERITY_ERROR = IMarker.SEVERITY_ERROR;
	
	static int SEVERITY_WARNING = IMarker.SEVERITY_WARNING;
	
	void issueMarker(
			int severity, 
			IRodinElement element, 
			String message, 
			Object... objects);
	
	void issueMarkerWithLocation(
			int severity, 
			IRodinElement element, 
			String message, 
			int startLocation,
			int endLocation,
			Object... objects);

}
