/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.rodinp.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.rodinp.core.IRodinDBMarker;
import org.rodinp.internal.core.util.Util;

/**
 * Temporary helper class for marker management.
 * <p>
 * This class will disappear in later releases of this plugin (when marker
 * support will be implemented in the Rodin database. To be used with caution.
 * </p>
 * 
 * @author Laurent Voisin
 */

// TODO delete this class once marker are supported by the Rodin DB
public abstract class TempMarkerHelper {

	private TempMarkerHelper() {
		super();
	}

	public static void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(IRodinDBMarker.RODIN_PROBLEM_MARKER);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
			Util.log(e, "when adding a marker");
		}
	}

	public static void addMarker(IFile file, int severity, String message) {
		try {
			IMarker marker = file.createMarker(IRodinDBMarker.RODIN_PROBLEM_MARKER);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
		} catch (CoreException e) {
			Util.log(e, "when adding a marker");
		}
	}
	
}
