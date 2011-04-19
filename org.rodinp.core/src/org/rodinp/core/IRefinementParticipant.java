/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.rodinp.core;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Common protocol for refinement participants.
 * 
 * @author Nicolas Beauger
 * @since 1.4
 * 
 */
public interface IRefinementParticipant {

	/**
	 * Modifies the given target root in order to make it (partially or
	 * entirely) a refinement of the given source root. The source root is not
	 * modified by this operation.
	 * 
	 * @param targetRoot
	 *            the target root
	 * @param sourceRoot
	 *            the source of the refinement
	 * @param monitor
	 *            a progress monitor, or <code>null</code>
	 * @throws RodinDBException
	 *             if a database operation fails
	 */
	void process(IInternalElement targetRoot, IInternalElement sourceRoot,
			IProgressMonitor monitor) throws RodinDBException;

}
