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
	@Override
	protected Sub2QuantTranslator create() {
		return new ExpressionPurifier();
	}	

	public static Predicate purify(Predicate P, FormulaFactory ff) {
		return Sub2QuantTranslator.translate(P, new ExpressionPurifier(), ff);
	}
	
	%include {Formula.tom}
	@Override
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		
	    %match (Predicate pred) {
	       	Lt(AE1, AE2) | Le(AE1, AE2) | Gt(AE1, AE2) | Ge(AE1, AE2) -> {
				return super.translate(pred, ff);
			}
			In(ME1, SE1) -> {
				Expression purifiedME1 = translateMaplet(`ME1, ff);
				Expression purifiedSE1 = translateSet(`SE1, ff);
				
				if(`ME1 == purifiedME1 && `SE1 == purifiedSE1)
					return pred;
				else
					return ff.makeRelationalPredicate(Formula.IN, purifiedME1, purifiedSE1, loc);
			}	
			Equal(E1, E2) -> {
				Expression purifiedE1 = null;
				Expression purifiedE2 = null;
				
				if(`E1.getType().getBaseType() != null) {
					purifiedE1 = translateSet(`E1, ff);
					purifiedE2 = translateSet(`E2, ff);
				}
				else {
					purifiedE1 = translate(`E1, ff);
					purifiedE2 = translate(`E2, ff);
				}
				
				if(`E1 == purifiedE1 && `E2 == purifiedE2) return pred;
				else
					return ff.makeRelationalPredicate(pred.getTag(), purifiedE1, purifiedE2, loc);
			}	
			_ -> {
	   	   		//throw new AssertionError("Undefined: " + pred);
	    		return super.translate(pred, ff);
	    	}
	    }
	}
	
	protected Expression translateMaplet(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		%match(Expression expr) {
			Mapsto(l, r) -> {
				Expression nl = translateMaplet(`l, ff);
				Expression nr = translateMaplet(`r, ff);
				
				if(nl == `l && nr == `r) return expr;
				else
					return ff.makeBinaryExpression(Formula.MAPSTO, nl, nr, loc);
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
}