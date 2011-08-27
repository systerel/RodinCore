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
 * Common protocol for parameterizer descriptors.
 * <p>
 * Parameterizers are contributed by providing tactic parameters to auto tactic
 * extensions.
 * </p>
 * 
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.3
 */
public interface IParameterizerDescriptor {

	/**
	 * Returns the descriptor of the tactic to instantiate.
	 * <p>
	 * Returned descriptor can NOT be instantiated. Any attempt to call
	 * {@link ITacticDescriptor#getTacticInstance()} on returned object will
	 * throw an {@link UnsupportedOperationException}.
	 * </p>
	 * 
	 * @return a tactic descriptor
	 */
	ITacticDescriptor getTacticDescriptor();

	/**
	 * Returns a parameter setting initialized with default parameter values.
	 * 
	 * @return a parameter setting
	 * @since 2.3
	 */
	IParameterSetting makeParameterSetting();

	/**
	 * Returns an instance of the tactic with the given parameters and the given
	 * id. Returns a failure tactic in case there is a problem calling the
	 * parameterizer.
	 * 
	 * @param valuation
	 *            a parameter valuation
	 * @param id
	 *            the id of the resulting tactic
	 * @return a tactic
	 * @throws IllegalArgumentException
	 *             in case there is a problem instantiating the parameterizer
	 * @since 2.3
	 */
	IParamTacticDescriptor instantiate(IParameterValuation valuation, String id)
			throws IllegalArgumentException;

}