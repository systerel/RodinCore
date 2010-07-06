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
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;

import org.eventb.core.ast.*;
import org.eventb.core.seqprover.ProverRule;

/**
 * Basic manual rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class StrictInclusionRewriterImpl extends AutoRewriterImpl {

	%include {FormulaV2.tom}
	
	@ProverRule("DEF_SUBSET")
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		Predicate newPredicate = super.rewrite(predicate);
		if (!newPredicate.equals(predicate))
			return newPredicate;

	    %match (Predicate predicate) {
	    	    	
	    	/**
	    	 * Set Theory: A ⊂ B == A ⊆ B ∧ ¬ A = B
	    	 */
	    	Subset(S, T) -> {
	    		return FormulaUnfold.subset(`S, `T);
	    	}
	    }
	    return predicate;
	}

}
