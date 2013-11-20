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

import static org.eventb.core.seqprover.SequentProver.getAutoTacticRegistry;

import org.eventb.core.seqprover.IAutoTacticRegistry;
import org.eventb.core.seqprover.ITacticDescriptor;

/**
 * Builds tactics for running some external prover. There is one sub-class for
 * each supported external prover or set of external provers.
 * 
 * @author Laurent Voisin
 */
public abstract class TacticBuilder {

	// Common time-out in milliseconds
	protected static long TIMEOUT = 3000;

	public static void setTimeout(int value) {
		TIMEOUT = value;
	}

	// Auto-tactic registry
	protected static final IAutoTacticRegistry REGISTRY = getAutoTacticRegistry();

	// Base id for tactic descriptor creation
	private final String id;

	protected TacticBuilder(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	/**
	 * Returns a tactic for running the external prover in the given mode.
	 */
	public abstract ITacticDescriptor makeTactic(boolean restricted);

	protected String getTacticId(boolean restricted) {
		return (restricted ? "red " : "full ") + id;
	}

}
