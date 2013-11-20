/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.seqprover.core.perf.app.tactics;

import org.eventb.core.seqprover.IParameterSetting;
import org.eventb.core.seqprover.ITacticDescriptor;

/**
 * Builds simple tactics for running some external prover which is accessible
 * through a parameterizer. There is one sub-class for each supported external
 * prover.
 * 
 * @author Laurent Voisin
 */
public abstract class XProverTacticBuilder extends TacticBuilder {

	protected XProverTacticBuilder(String id) {
		super(id);
	}

	/**
	 * Returns a simple tactic for running the external prover.
	 */
	@Override
	public ITacticDescriptor makeTactic(boolean restricted) {
		return REGISTRY.getTacticDescriptor(getParameterizerId());
	}

	/**
	 * Returns the id of the parameterizer which can run the external prover.
	 * The parameterizer must have at least one parameter named
	 * <code>restricted</code> (as provided by the XProver API) and another
	 * parameter for setting the timeout.
	 */
	protected abstract String getParameterizerId();

	/**
	 * Returns the name of the parameter for setting the timeout, e.g.,
	 * <code>timeout</code> or <code>timeoutDelay</code>
	 */
	protected abstract String getTimeoutParamName();

	/**
	 * Adds parameters to the given setting, if needed.
	 */
	protected abstract void completeParameters(IParameterSetting setting);

}
