/*******************************************************************************
 * Copyright (c) 2009, 2024 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;

import org.eventb.core.ast.*;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;

/**
 * Basic manual rewriter for the Event-B sequent prover.
 */
@SuppressWarnings({"unused", "cast"})
public class StrictInclusionRewriterImpl extends AutoRewriterImpl {

	/*
	 * CAUTION
	 *
	 * When this class gets modified to add new rules, please create a new level for
	 * this reasoner and disconnect it from the AutoRewriterImpl class, like what
	 * has been done in level L2 of RemoveMembership. It was indeed a big mistake to
	 * run the auto-rewriter within this reasoner.
	 */

	public StrictInclusionRewriterImpl() {
		super(Level.L0);
	}
	
	%include {FormulaV2.tom}
	
	@ProverRule("DEF_SUBSET")
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;

		FormulaFactory ff = predicate.getFactory();
	    %match (Predicate predicate) {
	    	    	
	    	/**
	    	 * Set Theory: A ⊂ B == A ⊆ B ∧ ¬ A = B
	    	 */
	    	Subset(S, T) -> {
	    		return new FormulaUnfold(ff).subset(`S, `T);
	    	}
	    }
	    return predicate;
	}

}
