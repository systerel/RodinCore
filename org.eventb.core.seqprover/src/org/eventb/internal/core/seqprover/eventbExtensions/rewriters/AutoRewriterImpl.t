/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;

/**
 * Basic automated rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class AutoRewriterImpl extends DefaultRewriter {

	public AutoRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}

	%include {Formula.tom}

	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
	    %match (Predicate predicate) {
	    	Limp(BTRUE(), P) -> {
	    		return `P;
	    	}
	    	Limp(BFALSE(), _) -> {
	    		return Lib.True;
	    	}
	    	Limp(_, BTRUE()) -> {
	    		return predicate.getRight();
	    	}
	    	Limp(P, BFALSE()) -> {
	    		return Lib.makeNeg(`P);
	    	}
	    	Limp(P, P) -> {
	    		return Lib.True;
	    	}
	    	Leqv(P, BTRUE()) -> {
	    		return `P;
	    	}
	    	Leqv(BTRUE(), P) -> {
	    		return `P;
	    	}
	    	Leqv(P, BFALSE()) -> {
	    		return Lib.makeNeg(`P);
	    	}
	    	Leqv(BFALSE(), P) -> {
	    		return Lib.makeNeg(`P);
	    	}
	    	Leqv(P, P) -> {
	    		return Lib.True;
	    	}
	    }
	    return predicate;
	}
	
	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
	    %match (Predicate predicate) {
	    	(Land | Lor) (children) -> {
				boolean isAnd = predicate.getTag() == Formula.LAND;

				return FormulaSimplification.simplifiedAssociativePredicate(predicate, `children, isAnd ? Lib.True : Lib.False,
    				isAnd ? Lib.False : Lib.True);
			}
	    }
	    return predicate;
	}

	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
	    %match (Predicate predicate) {
	    	Not(BTRUE()) -> {
				return Lib.False;
			}
			Not(BFALSE()) -> {
				return Lib.True;
			}
			Not(Not(P)) -> {
				return `P;
			}
	    }
	    return predicate;
	}

	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
	    %match (Predicate predicate) {
	    	ForAll(idents, Land(children)) -> {
	    		return FormulaSimplification.splitQuantifiedPredicate(predicate.getTag(), predicate.getPredicate().getTag(), `idents, `children);
	    	}
			Exists(idents, Lor(children)) -> {
	    		return FormulaSimplification.splitQuantifiedPredicate(predicate.getTag(), predicate.getPredicate().getTag(), `idents, `children);
	    	}
	    }
	    return predicate;
	}
}
