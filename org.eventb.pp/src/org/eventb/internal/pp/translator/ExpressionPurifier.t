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
 * Implements the translator from set-theory to predicate calculus.
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class ExpressionPurifier extends IdentityTranslator {
	private static final shiftOffset = Integer.maxValue / 2;
	private List<Predicate> bindings = new LinkedList<Predicate>();
	private List<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
	
	protected enum ExpressionState { Floating, Maplet, Set, Arithmetic};
	protected ExpressionState exprState;
	
	protected ExpressionPurifier(List<BoundIdentDecls> alreadyBound){
		identDecls.addAll(alreadyBound);
	}
	
	protected BoundIdentifier addBoundIdentifier(
		Type type, SourceLocation loc, FormulaFactory ff) {
		identDecls.add(
			ff.makeBoundIdentDecl(
				type.getBaseType() == null ? "x" : "X",
				loc,
				type));

		return ff.makeBoundIdentifier(identDecls.size() - 1, loc, type);	
	}
	
	protected BoundIdentifier bindExpression(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		BoundIdentifier ident = addBoundIdentifier(expr.getType(), loc, ff);
		bindings.add(ff.makeBinaryPredicate(Formula.EQUAL, ident, expr, loc));
		return ident;		
	}
	
	%include {Formula.tom}
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		
	    %match (Predicate pred) {
	    	ForAll(is, P) -> {
		    	`P = `P.shiftBoundIdentifiers(shiftOffset, ff);
		    	
		    	ExpressionPurifier purifier = new ExpressionPurifier(`is);
				Predicate purifiedP = purifier.translate(`P, ff);
				LinkedList<Predicate> purifiedBindings = new LinkedList<Predicate>();
				
				while(purifier.bindings.size() > 0) { 
					Predicate act = purifier.bindings.remove(0);
					purifiedBindings.add(purifier.translate(act, ff));
				}
				Predicate result = ff.makeQuantifiedPredicate(
					Formula.FORALL,
					purifier.identDecls,
					ff.makeBinaryPredicate(
						Formula.LIMP,
						FormulaConstructor.makeLandPredicate(ff, purifiedBindings, loc),
						purifiedP,
						loc),
					loc);
				
				return result.shiftBoundIdentifiers(-shiftOffset, ff);	    		
	    	}
	    	Lt(AE1, AE2) | Le(AE1, AE2) | Gt(AE1, AE2) | Ge(AE1, AE2) -> {
				exprState = ExpressionState.Arithmetic;
				return super.translate(pred, ff);
			}
			In(ME1, SE1) -> {
				exprState = ExpressionState.Maplet;
				Expression purifiedME1 = translate(`ME1, ff);
				
				exprState = ExpressionState.Set;
				Expression purifiedSE1 = translate(`SE1, ff);
				
				if(`ME1 == purifiedME1 && `SE1 == purifiedSE1)
					return pred;
				else
					return ff.makeBinaryPredicate(Formula.IN, purifiedME1, purifiedSE1, loc);
			}	
			Equal(E1, E2) | NotEqual(E1, E2) -> {
				exprState = ExpressionState.Floating;
				Expression purifiedE1 = translate(`E1, ff);
				Expression purifiedE2 = translate(`E2, ff);
				
				if(`E1 == purifiedE1 && `E2 == purifiedE2)
					return pred;
				else
					return ff.makeBinaryPredicate(Formula.EQUAL, purifiedE1, purifiedE1, loc);
			}	
	   	   	_ -> {
	    		return super.translate(pred, ff);
	    	}
	    }
	}
	
	protected Expression translate(Expression expr, FormulaFactory ff) {
		switch(exprState) {
			case Maplet: 
				return translateMaplet(expr, ff);
			case Set:
				return translateSet(expr, ff);
			case Arithmetic:
				return translateArithmetic(expr, ff);
			default:
				throw new AssertionError("Unknown state: " + exprState);
		}
	}
	
	protected Expression translateMaplet(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		%match(Expression expr) {
			Mapsto(l, r) -> {
				return ff.makeBinaryExpression(
					Formula.MAPSTO,
					translate(`l, ff),
					translate(`r, ff),
					loc);
			}
			BoundIdentifier(_) | FreeIdentifier(_) | INTEGER() | BOOL() -> { 
				return expr;
			}
			_ -> {
				return bindExpression(expr, ff);
			}			
		} 
	}
	
	protected Expression translateSet(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		%match(Expression expr) {
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
				super.translate(expr, ff);
			}
			BoundIdentifier(_) | FreeIdentifier(_) | INTEGER() | BOOL() -> { 
				return expr;
			}
			_ -> {
				return bindExpression(expr, ff);
			}			
		} 
	}
	
	protected Expression translateArithmetic(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();

		%match(Expression expr) {
			Card(children) -> {
				return bindExpression(expr, ff);
			}
			_ -> {	
				return super.translate(expr, ff);
			}
		}
		
	}
}