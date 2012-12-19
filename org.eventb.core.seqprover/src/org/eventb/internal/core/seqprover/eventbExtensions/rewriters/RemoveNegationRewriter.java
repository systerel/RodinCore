/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static org.eventb.core.seqprover.eventbExtensions.DLib.mDLib;
import static org.eventb.core.seqprover.eventbExtensions.Lib.conjuncts;
import static org.eventb.core.seqprover.eventbExtensions.Lib.disjuncts;
import static org.eventb.core.seqprover.eventbExtensions.Lib.eqLeft;
import static org.eventb.core.seqprover.eventbExtensions.Lib.eqRight;
import static org.eventb.core.seqprover.eventbExtensions.Lib.getBoundIdents;
import static org.eventb.core.seqprover.eventbExtensions.Lib.getBoundPredicate;
import static org.eventb.core.seqprover.eventbExtensions.Lib.getElement;
import static org.eventb.core.seqprover.eventbExtensions.Lib.getSet;
import static org.eventb.core.seqprover.eventbExtensions.Lib.impLeft;
import static org.eventb.core.seqprover.eventbExtensions.Lib.impRight;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isConj;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isDisj;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isEq;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isExQuant;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isFalse;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isImp;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isInclusion;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNeg;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNotEq;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isNotInclusion;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isTrue;
import static org.eventb.core.seqprover.eventbExtensions.Lib.isUnivQuant;
import static org.eventb.core.seqprover.eventbExtensions.Lib.negPred;
import static org.eventb.core.seqprover.eventbExtensions.Lib.notEqLeft;
import static org.eventb.core.seqprover.eventbExtensions.Lib.notEqRight;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.DLib;

/**
 * Deprecated implementation of a rewriter that simplifies negations.
 * <p>
 * This class must not be used in new code but is kept to ensure backward
 * compatibility of proof trees.
 * </p>
 * @author fmehta
 *	
 * @deprecated use {@link RemoveNegation} instead
 */
@Deprecated
public class RemoveNegationRewriter implements Rewriter {

	public String getRewriterID() {
		return "removeNegation";
	}
	
	public String getName() {
		return "rewrite ¬";
	}
	
	public boolean isApplicable(Predicate p) {
		if (! (isNeg(p))) return false;
		Predicate negP = negPred(p);
		
		if (isTrue(negP))
			return true;
		if (isFalse(negP))
			return true;
		if (isNeg(negP))
			return true;
		if (isConj(negP))
			return true;
		if (isDisj(negP))
			return true;
		if (isImp(negP))
			return true;
		if (isExQuant(negP))
			return true;
		if (isUnivQuant(negP))
			return true;
		
		if (isEq(negP))
			return true;
		if (isNotEq(negP))
			return true;
		
		if (isInclusion(negP))
			return true;
		if (isNotInclusion(negP))
			return true;
		
		return false;
	}

	public Predicate apply(Predicate p, FormulaFactory ff) {
		final DLib lib = mDLib(ff);
		if (! (isNeg(p))) return null;
		Predicate negP = negPred(p);
		
		// - T == F
		if (isTrue(negP))
			return lib.False();
		// - F == T
		if (isFalse(negP))
			return lib.True();
		// - - P == P
		if (isNeg(negP))
			return negPred(negP);
		// - (P & Q &..)  = (-P or -Q or ..) 
		if (isConj(negP))
			return lib.makeDisj(lib.makeNeg(conjuncts(negP)));
		// - (P or Q &..) = (-P & -Q &..)
		if (isDisj(negP))
			return lib.makeConj(lib.makeNeg(disjuncts(negP)));
		// - ( P => Q) = ( P & -Q)
		if (isImp(negP))
			return lib.makeConj(impLeft(negP),lib.makeNeg(impRight(negP)));
		// -(#x . Px) == !x. -Px
		if (isExQuant(negP))
			return lib.makeUnivQuant(getBoundIdents(negP),
					lib.makeNeg(getBoundPredicate(negP)));
		// -(!x. Px) == #x. - Px
		if (isUnivQuant(negP))
			return lib.makeExQuant(getBoundIdents(negP),
					lib.makeNeg(getBoundPredicate(negP)));
		// -(a=b) == a/=b
		if (isEq(negP))
			return lib.makeNotEq(eqLeft(negP),eqRight(negP));
		// -(a/=b) == (a=b)
		if (isNotEq(negP))
			return lib.makeEq(notEqLeft(negP),notEqRight(negP));
		// -(a:A) == a/:A
		if (isInclusion(negP))
			return lib.makeNotInclusion(getElement(negP),getSet(negP));
		// -(a/:A) == a:A
		if (isNotInclusion(negP))
			return lib.makeInclusion(getElement(negP),getSet(negP));
		
		return null;
	}

}
