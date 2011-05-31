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
package org.eventb.core.seqprover;

/**
 * Common protocol for reasoners that bear a signature.
 * <p>
 * A signature is used to determine proof reusability. For a given reasoner, the
 * signature may change over time.
 * </p>
 * <p>
 * When a reasoner with a signature is used in a proof, the signature is
 * serialized therein. When updating the proof status, if the current reasoner
 * signature is different from the serialized one, then the proof is considered
 * broken.
 * </p>
 * <p>
 * This interface is intended to be implemented by contributers whose reasoner
 * behaviour evolves during a session (as opposed to reasoner version, which is
 * fixed over a given session).
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.2
 * 
 */
public interface ISignatureReasoner extends IReasoner {

	/**
	 * Returns the signature of this reasoner, at the moment of the call.
	 * <p>
	 * The returned signature should be as small as possible. It must not be
	 * more than 40 characters long.
	 * </p>
	 * 
	 * @return a String
	 */
	String getSignature();

}
