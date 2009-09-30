/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.core.seqprover;

/**
 * Common protocol for reasoners that bear a version.
 * <p>
 * When a reasoner has a version, this version is stored in proofs that use the
 * reasoner. Subsequently, when the proof is restored, if the reasoner version
 * has changed, the conflict between the current reasoner and the one from the
 * proof will be detected and managed by proof tools.
 * </p>
 * <p>
 * The version number is used by the tools as a way of getting aware of
 * reasoners evolution. Thus, implementors of this interface are required to
 * increment the version number whenever the reasoner code changes.
 * </p>
 * <p>
 * This interface is intended to be implemented by clients who wish to
 * contribute reasoners and have their evolution managed through versioning.
 * </p>
 * 
 * @author "Nicolas Beauger"
 * 
 * @since 1.1
 */
public interface IVersionedReasoner extends IReasoner {

	/**
	 * Returns the current version of this reasoner.
	 * <p>
	 * The version number is incremented whenever the reasoner code changes.
	 * </p>
	 * 
	 * @return a non-negative integer
	 */
	int getVersion();

}
