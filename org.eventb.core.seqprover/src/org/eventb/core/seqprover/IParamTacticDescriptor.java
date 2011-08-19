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

import org.eventb.core.seqprover.IAutoTacticRegistry.ITacticDescriptor;

/**
 * Common protocol for instantiated parameterized tactic descriptors.
 * 
 * @author Nicolas Beauger
 * @since 2.3
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IParamTacticDescriptor extends ITacticDescriptor {

	/**
	 * Returns the id of the parameterizer used to instantiate this tactic.
	 * 
	 * @return a combinator id
	 */
	String getParameterizerId();

	/**
	 * Returns the valuation of the described parameterized tactic.
	 * 
	 * @return a parameter valuation
	 */
	IParameterValuation getValuation();
}