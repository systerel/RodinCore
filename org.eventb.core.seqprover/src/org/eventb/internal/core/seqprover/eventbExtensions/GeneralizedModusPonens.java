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
package org.eventb.internal.core.seqprover.eventbExtensions;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.SequentProver;

/**
 * Simplifies the visible hypotheses and goal in a sequent by replacing
 * sub-predicates <code>P</code> by <code>⊤</code> (or <code>⊥</code>) if
 * <code>P</code> (or <code>¬P</code>) appears as hypothesis (global and local).
 * 
 * @author Emmanuel Billaud
 */
public class GeneralizedModusPonens extends AbstractGenMP {
	public static final String REASONER_ID = SequentProver.PLUGIN_ID + ".genMP";

	public static enum Level {
		L0, L1;

		public static final Level LATEST = Level.latest();

		private static final Level latest() {
			final Level[] values = Level.values();
			return values[values.length - 1];
		}

		public boolean from(Level other) {
			return this.ordinal() >= other.ordinal();
		}

	}

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule({ "GENMP_HYP_HYP", "GENMP_NOT_HYP_HYP", "GENMP_HYP_GOAL",
			"GENMP_NOT_HYP_GOAL" })
	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		return super.apply(seq, input, pm, Level.L0);
	}

}