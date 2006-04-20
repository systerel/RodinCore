/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.math.BigInteger;
import java.util.*;

import org.eventb.core.ast.*;


/**
 * Implements the identity translation
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class IdentityVisitor extends IdentityVisitorBase {

	%include {Formula.tom}
	
	protected Expression visit(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
	    %match (Expression expr) {
	    	AssociativeExpression(_) -> {
	    		visitAssociativeExpression((AssociativeExpression)expr);
	    	}
	    	AtomicExpression() -> { 
	    		visitAtomicExpression((AtomicExpression)expr);
	    	}
	    	BoundIdentifier() -> {
	    		visitBoundIdentifier((BoundIdentifier)expr);
	    	}
	    	FreeIdentifier() -> {
	    		visitFreeIdentifier((FreeIdentifier)expr);
	    	}
	    	BinaryExpression(l, r) -> {
	    		visitBinaryExpression((BinaryExpression)expr);
	    	}
	    	Bool(P) -> {
	    		visitBoolxpression((BoolExpression)expr);
	    	}
	    	Cset(_) | Qinter(_) | Qunion(_) -> {
	    		visitQuantifiedExpression((QuantifiedExpression)expr);
	    	}
	    	SetExtension(_) -> {
	    		visitSetExtension((SetExtension)expr);
	    	}
	    	
	    	UnaryExpression(_) -> {
	    		visitUnaryExpression((UnaryExpression)expr);
	    	}
	    	_ -> {
	    		throw new AssertionError("Unknown expression: " + expr);
	    	}
	    }
	}
	
	protected Predicate visit(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
	    %match (Predicate pred) {
	    	Land(_) | Lor(_) -> {
	    		visitAssociativePredicate((AssociativePredicate)pred);
	    	}
	    	Limp(_, _) | Leqv(_, _)  -> {
	    		visitAssociativePredicate((AssociativePredicate)pred);
	    	}
	      	Not(_)-> {
	    		visitUnaryPredicatePredicate((UnaryPredicate)pred);
	    	}
	    	Finite(_)-> {
	    		visitSimplePredicate((SimplePredicate)pred);
	    	}
			RelationalPredicate(_, _) -> 
			{
	    		visitRelationalPredicate((RelationalPredicate)pred);
	    	}
	    	ForAll (_, _) | Exists (_, _) -> {
	    		visitQuantifiedPredicate((QuantifiedPredicate)pred);
	    	}
	    	BTRUE() -> {
	    		visit
	    	}| BFALSE() -> { return pred; }
	       	P -> {
	    		throw new AssertionError("Unknown Predicate: " + `P);
	    	}
	    }
	}
}
