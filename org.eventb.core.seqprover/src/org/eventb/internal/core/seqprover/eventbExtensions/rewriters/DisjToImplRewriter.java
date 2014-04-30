/*******************************************************************************
 * Copyright (c) 2006, 2014 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.seqprover.eventbExtensions.Lib.disjuncts;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isDisj;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * Deprecated implementation that rewrites a disjunction into an implication.
 * <p>
 * This class must not be used in new code but is kept to ensure backward
 * compatibility of proof trees.
 * </p>
 * 
 * @author fmehta
 * @deprecated use {@link DisjunctionToImplicationRewriter} instead
 */
@Deprecated
public class DisjToImplRewriter implements Rewriter{
	
	@Override
	public String getRewriterID() {
		return "disjToImpl";
	}
	
	@Override
	public String getName() {
		return "∨ to ⇒";
	}
	
	@Override
	public boolean isApplicable(Predicate p) {
		if (isDisj(p)) return true;
		
		return false;
	}

	@Override
	public Predicate apply(Predicate p, FormulaFactory ff) {
		// (P or Q or ...) == (-P => (Q or ..))
		if (isDisj(p))
		{
			Predicate[] disjuncts = disjuncts(p);
			assert disjuncts.length >= 2;
			Predicate firstDisjunct = disjuncts[0];
			Predicate[] restDisjuncts = new Predicate[disjuncts.length - 1];
			System.arraycopy(disjuncts,1,restDisjuncts,0,disjuncts.length - 1);
			return DLib.makeImp(DLib.makeNeg(firstDisjunct),
					DLib.makeDisj(ff, restDisjuncts)
					);
		}

		return null;
	}

}
