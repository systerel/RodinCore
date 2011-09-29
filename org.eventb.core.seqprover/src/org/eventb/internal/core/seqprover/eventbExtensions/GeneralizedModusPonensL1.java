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
import org.eventb.internal.core.seqprover.eventbExtensions.GeneralizedModusPonens.Level;

/**
 * Enhancement of the reasoner GeneralizedModusPonens. This reasoner implements
 * 4 more rules than the previous one :
 * <ul>
 * <li>(H,φ(G)⊢G) ≡ (H,φ(⊥)⊢G</li>
 * <li>(H,φ(G)⊢¬G) ≡ (H,φ(⊤)⊢¬G</li>
 * <li>(H,φ(Gi)⊢G1 ∨ ... ∨ Gi ∨ ... ∨ Gn) ≡ (H,φ(⊥)⊢G1 ∨ ... ∨ Gi ∨ ... ∨ Gn</li>
 * <li>(H,φ(Gi)⊢G1 ∨ ... ∨ ¬Gi ∨ ... ∨ Gn) ≡ (H,φ(⊤)⊢G1 ∨ ... ∨ ¬Gi ∨ ... ∨ Gn</li>
 * </ul>
 * 
 * @author Emmanuel Billaud
 */
public class GeneralizedModusPonensL1 extends AbstractGenMP {
	private static final String REASONER_ID = GeneralizedModusPonens.REASONER_ID
			+ "L1";

	@Override
	public String getReasonerID() {
		return REASONER_ID;
	}

	@ProverRule({ "GENMP_HYP_HYP", "GENMP_NOT_HYP_HYP", "GENMP_HYP_GOAL",
			"GENMP_NOT_HYP_GOAL", "GENMP_GOAL_HYP", "GENMP_NOT_GOAL_HYP",
			"GENMP_OR_GOAL_HYP", "GENMP_OR_NOT_GOAL_HYP" })
	@Override
	public IReasonerOutput apply(IProverSequent seq, IReasonerInput input,
			IProofMonitor pm) {
		return super.apply(seq, input, pm, Level.L1);
	}

}
