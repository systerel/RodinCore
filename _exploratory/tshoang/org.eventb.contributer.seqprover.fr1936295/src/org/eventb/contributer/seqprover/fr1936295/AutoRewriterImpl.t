/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.contributer.seqprover.fr1936295;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.*;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Generated formula rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class AutoRewriterImpl extends DefaultRewriter {

	protected AssociativeExpression makeAssociativeExpression(int tag, Collection<Expression> children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	protected AtomicExpression makeEmptySet(Type type) {
		return ff.makeEmptySet(type, null);
	}

	public AutoRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {Formula.tom}
	
	@Override
	public Expression rewrite(AssociativeExpression expression) {
	    %match (Expression expression) {
	    	/**
			 * Overriding with empty set(s):  ∅  r  ∅  =  r
			 */
	    	Ovr(children) -> {
	    		ArrayList<Expression> remain = new ArrayList<Expression>(`children.length);
	    		boolean found = false;
	    		for (Expression child : `children) 
	    		{
				    if (child.equals(makeEmptySet(child.getType())))
						found = true;
					else
						remain.add(child);   		
	    		}
	    		
	    		if (found)
	    		{
	    			if (remain.size() == 1)
	    			{
	    				return remain.get(0);
	    			}
	    			else
	    			{
	    				return makeAssociativeExpression(Expression.OVR, remain);
	    			}
	    			
	    		}

    		}

	    }
	    return expression;
	}
}