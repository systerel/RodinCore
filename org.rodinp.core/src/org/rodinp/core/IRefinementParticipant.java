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

/**
 * @author Nicolas Beauger
 * 
 */
public interface IRefinementParticipant {

	/**
	 * Modifies the given refined root in order to make it (partially or
	 * entirely) a refinement of the given source root. The source root is not
	 * modified by this operation.
	 * 
	 * @param refinedRoot
	 *            the refined root
	 * @param sourceRoot
	 *            the source of the refinement
	 */
	void process(IInternalElement refinedRoot, IInternalElement sourceRoot);
	
}
