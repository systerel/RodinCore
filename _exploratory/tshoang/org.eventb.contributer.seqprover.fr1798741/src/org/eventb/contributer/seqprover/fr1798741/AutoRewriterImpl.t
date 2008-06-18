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
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Generated formula rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class AutoRewriterImpl extends DefaultRewriter {

	protected AtomicExpression makeEmptySet(Type type) {
		return ff.makeEmptySet(type, null);
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
			 * 									∅[a]  =  ∅
			 */
	    	RelImage(left, right) -> {

	    		if (`left.equals(makeEmptySet(`left.getType()))
	    			|| `right.equals(makeEmptySet(`right.getType())))
	    		{
	    			// left has a PowerSetType. the base type of that
	    			// is a ProductType of which we need the right type
	    			Type rangeType = ((PowerSetType) `left.getType()).getBaseType();
	    			rangeType = ((ProductType) rangeType).getRight();
	    			
	    			return makeEmptySet(ff.makePowerSetType(rangeType));
	    		}
	    		
    		}
	    }
	    return expression;
	}
}
