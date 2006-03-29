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
public class IdentityTranslator {

	%include {Formula.tom}
	
	protected Expression translate(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
	    %match (Expression expr) {
	    	AtomicExpression() | BoundIdentifier(_) | FreeIdentifier(_) | IntegerLiteral(_) -> { 
	    		return expr; 
	    	}
	    	BinaryExpression(l, r) -> {
	    		return ff.makeBinaryExpression(
	    			expr.getTag(),
	    			translate(`l, ff),
	    			translate(`r, ff),
	    			loc);
	    	}
	    	Bool(P) -> {
	    		return ff.makeBoolExpression(
	    			translate(`P, ff),
	    			loc);
	    	}
	    	Cset(is, P, E) | Qinter(is, P, E) | Qunion(is, P, E) -> {
	    		return ff.makeQuantifiedExpression(
	    			expr.getTag(),
	    			`is,
	    			translate(`P, ff),
	    			translate(`E, ff),
	    			loc,
	    			QuantifiedExpression.Form.Explicit);	    			
	    	}
	    	SetExtension(children) -> {
	    		ArrayList<Expression> newChildren = new ArrayList<Expression>();
	    		for (Expression child: `children) {
	    			newChildren.add(translate(child, ff));
	    		}
	    		return ff.makeSetExtension(newChildren, loc);
	    	}
	    	UnaryExpression(child) -> {
	    		return ff.makeUnaryExpression(
	    			expr.getTag(), 
	    			translate(`child, ff),
	    			loc);
	    	}
	    	_ -> {
	    		throw new AssertionError("Unknown expression: " + expr);
	    	}
	    	
	    }
	}
	
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
	    %match (Predicate pred) {
	    	Land(children) | Lor(children) -> {
	    		ArrayList<Predicate> newChildren = new ArrayList<Predicate>();
	    		for (Predicate child: `children) {
	    			newChildren.add(translate(child, ff));
	    		}
		    	return ff.makeAssociativePredicate(pred.getTag(), newChildren, loc);
	    	}
	    	Limp(left, right) | Leqv(left, right)  -> {
	    		return ff.makeBinaryPredicate(
	    			pred.getTag(),
	    			translate(`left, ff),
	    			translate(`right, ff),
	    			loc);
	    	}
	      	Not(P)-> {
	    		return ff.makeUnaryPredicate(Formula.NOT, translate(`P, ff), loc);
	    	}
	    	Finite(P)-> {
	    		return ff.makeSimplePredicate(Formula.KFINITE, translate(`P, ff), loc);
	    	}
			RelationalPredicate(l, r) -> 
			{
	    		return  ff.makeRelationalPredicate(
	    			pred.getTag(),
	    			translate(`l, ff),
	    			translate(`r, ff),
	    			loc);
	    	}
	    	ForAll (is, P) | Exists (is, P) -> {
	    		return ff.makeQuantifiedPredicate(
	    			pred.getTag(),
	    			`is,
	    			translate(`P, ff),
	    			loc);	    			
	    	}
	    	BTRUE() | BFALSE() -> { return pred; }
	       	P -> {
	    		throw new AssertionError("Unknown Predicate: " + `P);
	    	}
	    }
	}
}
