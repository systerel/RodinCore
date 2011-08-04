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
 * Common protocol for making parameterized tactics. This interface is intended
 * to be implemented by clients who contribute tactics with parameters.
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * 
 */
public interface ITacticParameterizer {

	/**
	 * Returns an instance of the parameterized tactic using the given
	 * parameters.
	 * 
	 * @param parameters
	 *            tactic parameters, as described by the extension
	 * @return a tactic, or <code>null</code> if the tactic could not be built
	 *         because of the parameters
	 */
	ITactic getTactic(IParameterValuation parameters);
}
