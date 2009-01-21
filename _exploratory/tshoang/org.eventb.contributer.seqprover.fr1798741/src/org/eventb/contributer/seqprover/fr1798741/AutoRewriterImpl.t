/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.contributer.seqprover.fr1798741;

import java.math.BigInteger;

import org.eventb.core.ast.*;

/**
 * Generated formula rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class AutoRewriterImpl extends DefaultRewriter {

	/**
	 * Returns the empty subset of <code>T</code>, given a relation
	 * <code>rel</code> of type <code>S ↔ T</code>.
	 * 
	 * @param rel
	 *            an expression denoting a relation
	 * @return the empty subset of the relation range
	 */
	private AtomicExpression makeEmptyRange(Expression rel) {
		final Type rangeType = rel.getType().getTarget();
		return ff.makeEmptySet(ff.makePowerSetType(rangeType), null);
	}

	public AutoRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {Formula.tom}
	
	@Override
	public Expression rewrite(BinaryExpression expression) {
	    %match (Expression expression) {
	    	/**
			 * Relation image with empty set:	r[∅]  =  ∅
			 */
	    	RelImage(rel, EmptySet()) -> {
				return makeEmptyRange(`rel);
			}

	    	/**
			 * Relation image with empty set:	∅[a]  =  ∅
			 */
	    	RelImage(rel@EmptySet(), _) -> {
				return makeEmptyRange(`rel);
    		}
	    }
	    return expression;
	}
}
