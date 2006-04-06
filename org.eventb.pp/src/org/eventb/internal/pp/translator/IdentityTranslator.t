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
	    	AssociativeExpression(children) -> {
	    		ArrayList<Expression> newChildren = new ArrayList<Expression>();
	    		boolean hasChanged = false;
	    		for (Expression child: `children) {
	    			Expression newChild = translate(child, ff);
	    			newChildren.add(newChild);
	    			hasChanged = hasChanged || (newChild != child);
	    		}
	    		if(hasChanged)
		    		return ff.makeAssociativeExpression(expr.getTag(), newChildren, loc);
		    	else
		    		return expr;		    	
	    	}
	    	AtomicExpression() | BoundIdentifier(_) | FreeIdentifier(_) | IntegerLiteral(_) -> { 
	    		return expr; 
	    	}
	    	BinaryExpression(l, r) -> {
	    		Expression nl = translate(`l, ff);
	    		Expression nr = translate(`r, ff);

	    		if(nl == `l && nr == `r) return expr;
	    		else
		    		return ff.makeBinaryExpression(expr.getTag(), nl, nr, loc);
	    	}
	    	Bool(P) -> {
	    		Predicate nP = translate(`P, ff);
	    		
	    		if(nP == `P) return expr;
	    		else 
	    			return ff.makeBoolExpression(nP, loc);
	    	}
	    	Cset(is, P, E) | Qinter(is, P, E) | Qunion(is, P, E) -> {
	    		Predicate nP = translate(`P, ff);
	    		Expression nE = translate(`E, ff);

	    		if(nP == `P && nE == `E) return expr;
	    		else
	    			return ff.makeQuantifiedExpression(
	    				expr.getTag(), `is, nP, nE, loc, QuantifiedExpression.Form.Explicit);	    			
	    	}
	    	SetExtension(children) -> {
	    		boolean hasChanged = false;
	    		ArrayList<Expression> newChildren = new ArrayList<Expression>();
	    		for (Expression child: `children) {
	    			Expression newChild = translate(child, ff);
	    			newChildren.add(newChild);
	    			hasChanged = hasChanged || (newChild != child);
	    		}
	    		if(hasChanged)
		    		return ff.makeSetExtension(newChildren, loc);
		    	else
		    		return expr;
	    	}
	    	UnaryExpression(child) -> {
	    		Expression newChild = translate(`child, ff);
	    		
	    		if(newChild == child) return expr;
	    		else
		    		return ff.makeUnaryExpression(expr.getTag(), newChild, loc);
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
	    		boolean hasChanged = false;
	    		for (Predicate child: `children) {
	    			Predicate newChild = translate(child, ff);
	    			newChildren.add(newChild);
	    			hasChanged = hasChanged || (newChild != child);
	    		}
	    		if(hasChanged)
	    			return ff.makeAssociativePredicate(pred.getTag(), newChildren, loc);
	    		else
	    			return pred;
	    	}
	    	Limp(l, r) | Leqv(l, r)  -> {
				Predicate nl = translate(`l, ff);
				Predicate nr = translate(`r, ff);
				
				if(nl == `l && nr == `r) return pred;
				else
					return ff.makeBinaryPredicate(pred.getTag(),nl, nr, loc);
	    	}
	      	Not(P)-> {
	      		Predicate nP = translate(`P, ff);
	      		if(nP == `P) return pred;
	      		else
	    			return ff.makeUnaryPredicate(Formula.NOT, nP, loc);
	    	}
	    	Finite(E)-> {
	      		Expression nE = translate(`E, ff);
	      		if(nE == `E) return pred;
	      		else
	    			return ff.makeSimplePredicate(Formula.KFINITE, nE, loc);
	    	}
			RelationalPredicate(l, r) -> 
			{
				Expression nl = translate(`l, ff);
	    		Expression nr = translate(`r, ff);

	    		if(nl == `l && nr == `r) return pred;
	    		else
					return  ff.makeRelationalPredicate(pred.getTag(), nl, nr, loc);
	    	}
	    	ForAll (is, P) | Exists (is, P) -> {
	      		Predicate nP = translate(`P, ff);
	      		
	      		if(nP == `P) return pred;
	      		else
	    			return ff.makeQuantifiedPredicate(pred.getTag(), `is, nP, loc);	
	    	}
	    	BTRUE() | BFALSE() -> { return pred; }
	       	P -> {
	    		throw new AssertionError("Unknown Predicate: " + `P);
	    	}
	    }
	}
}
