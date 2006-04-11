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
 * Implements the border translation
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class BorderTranslator extends IdentityTranslator {

	protected Predicate translatePred2Expr(Predicate pred, Expression left, Expression right, FormulaFactory ff) {
		return super.translate(pred, ff);
	}
	protected Predicate translatePred2Set(Predicate pred, Expression set, FormulaFactory ff) {
		return super.translate(pred, ff);
	}
	protected Expression translateExpr2Pred(Expression expr, Predicate pred, FormulaFactory ff) {
		return super.translate(expr, ff);
	}
	protected Expression translateBool2Pred(Expression bexpr, Predicate pred, FormulaFactory ff) {
		return translateExpr2Pred(bexpr, pred, ff);
	}
	protected Expression translateSet2Pred(Expression set, Predicate pred, FormulaFactory ff) {
		return translateExpr2Pred(set, pred, ff);
	}
	
	protected Expression idTransExpression(Expression expr, FormulaFactory ff) {
		return super.translate(expr, ff);
	}
	
	protected Predicate idTransPredicate(Predicate pred, FormulaFactory ff) {
		return super.translate(pred, ff);
	}

	%include {Formula.tom}
	
	protected Expression translate(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
	    %match (Expression expr) {
	    	Bool(P) -> {
	    		return translateBool2Pred(expr, `P, ff);
	    	}
	    	Cset(_, P, _) | Qinter(_, P, _) | Qunion(_, P, _) -> {
	    		return translateSet2Pred(expr, `P, ff);
	    	}
	    	_ -> {
	    		return super.translate(expr, ff);
	    	}
	    }
	}
	
	protected Predicate translate(Predicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
	    %match (Predicate pred) {
	    	Finite(E) -> {
	    		return translatePred2Set(pred, `E, ff);
	    	}
	    	RelationalPredicate(l, r)-> {
	    		Type intType = ff.makeIntegerType();
	    		if(`l.equals(intType) && `r.equals(intType)) {
	    			
	    		}
	    		
	    		return translatePred2Expr(pred, `l, `r, ff);
	    	}
	       	_ -> {
	    		return super.translate(pred, ff);
	    	}
	    }
	}
}
