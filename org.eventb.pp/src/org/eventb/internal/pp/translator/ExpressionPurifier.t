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
public class ExpressionPurifier extends Sub2QuantTranslator {
	protected enum ExpressionState { Floating, Maplet, Set, Arithmetic};
	protected ExpressionState exprState;
	
	@Override
	protected Sub2QuantTranslator create() {
		return new ExpressionPurifier();
	}	

	public static Predicate purify(Predicate P, FormulaFactory ff) {
		return Sub2QuantTranslator.translate(P, new ExpressionPurifier(), ff);
	}
	
	%include {Formula.tom}
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		
	    %match (Predicate pred) {
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
					return ff.makeRelationalPredicate(Formula.IN, purifiedME1, purifiedSE1, loc);
			}	
			Equal(E1, E2) -> {
				exprState = ExpressionState.Floating;
				Expression purifiedE1 = translate(`E1, ff);
				Expression purifiedE2 = translate(`E2, ff);
				
				if(`E1 == purifiedE1 && `E2 == purifiedE2)
					return pred;
				else
					return ff.makeRelationalPredicate(pred.getTag(), purifiedE1, purifiedE2, loc);
			}	
			_ -> {
	   	   		//throw new AssertionError("Undefined: " + pred);
	    		return super.translate(pred, ff);
	    	}
	    }
	}
	
	protected Expression translate(Expression expr, FormulaFactory ff) {
		if(exprState == ExpressionState.Floating) {
			if(expr.getType() instanceof ProductType)
				exprState = ExpressionState.Maplet;
			else if(expr.getType() instanceof PowerSetType)
				exprState = ExpressionState.Set;
			else
				exprState = ExpressionState.Arithmetic;
		}
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
			_ -> {	
				return super.translate(expr, ff);
			}
		}
	}
}