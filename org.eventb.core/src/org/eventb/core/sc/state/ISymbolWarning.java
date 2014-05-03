/*******************************************************************************
 * Copyright (c) 2014 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.sc.state;

import org.eventb.core.sc.IMarkerDisplay;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for creating a problem marker of severity warning.
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * 
 * @author Laurent Voisin
 * @since 3.1
 */
public interface ISymbolWarning {

	/**
	 * Creates a warning marker with the given display.
	 * 
	 * @param markerDisplay
	 *            some display for creating the marker
	 * @throws RodinDBException
	 *             in case of error creating the marker
	 */
	void createConflictWarning(IMarkerDisplay markerDisplay)
			throws RodinDBException;

}
