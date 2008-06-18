/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.contributer.seqprover.fr1834715;


import java.math.BigInteger;
import java.util.Collection;
import java.util.ArrayList;

import org.eventb.core.ast.*;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Generated formula rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class AutoRewriterImpl extends DefaultRewriter {

	protected AssociativePredicate makeAssociativePredicate(int tag, Collection<Predicate> children) {
		return ff.makeAssociativePredicate(tag, children, null);
	}

	protected RelationalPredicate makeRelationalPredicate(int tag, Expression left,
			Expression right) {
		return ff.makeRelationalPredicate(tag, left, right, null);
	}

	protected UnaryExpression makeUnaryExpression(int tag, Expression child) {
		return ff.makeUnaryExpression(tag, child, null);
	}

	public AutoRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {Formula.tom}
	
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		%match (Predicate predicate) {
			/**
			 * Rewrite subset:  A ⊂ B  ==  A ∈ ℙ(B) ∧ A ≠ B
			 */
			Subset(left, right) -> {

				ArrayList<Predicate> and_children = new ArrayList<Predicate>(2);
				and_children.add(0, makeRelationalPredicate(Formula.IN, `left,
									makeUnaryExpression(Formula.POW, `right)));
				and_children.add(1, makeRelationalPredicate(Formula.NOTEQUAL, `left, `right));
				return makeAssociativePredicate(Formula.LAND, and_children);
				
			}
		}
		return predicate;
	}

} 