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
public class IdentityTranslator extends IdentityTranslatorBase {

	%include {Formula.tom}
	
	protected Expression translate(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
	    %match (Expression expr) {
	    	AssociativeExpression(children) -> {
	    		return idTransAssociativeExpression(expr, ff, `children);
	    	}
	    	AtomicExpression() | BoundIdentifier(_) | FreeIdentifier(_) | IntegerLiteral(_) -> { 
	    		return expr; 
	    	}
	    	BinaryExpression(l, r) -> {
	    		return idTransBinaryExpression(expr, ff, `l, `r);
	    	}
	    	Bool(P) -> {
	    		return idTransBoolExpression(expr, ff, `P);
	    	}
	    	Cset(is, P, E) | Qinter(is, P, E) | Qunion(is, P, E) -> {
	    		return idTransQuantifiedExpression(expr, ff, `is, `P, `E);
	    	}
	    	SetExtension(children) -> {
	    		return idTransSetExtension(expr, ff, `children);
	    	}
	    	
	    	UnaryExpression(child) -> {
	    		return idTransUnaryExpression(expr, ff, `child);
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
	    		return idTransAssociativePredicate(pred, ff, `children);
	    	}
	    	Limp(l, r) | Leqv(l, r)  -> {
	    		return idTransBinaryPredicate(pred, ff, `l, `r);
	    	}
	      	Not(P)-> {
	    		return idTransUnaryPredicate(pred, ff, `P);
	    	}
	    	Finite(E)-> {
	    		return idTransSimplePredicate(pred, ff, `E);
	    	}
			RelationalPredicate(l, r) -> 
			{
	    		return idTransRelationalPredicate(pred, ff, `l, `r);
	    	}
	    	ForAll (is, P) | Exists (is, P) -> {
	    		return idTransQuantifiedPredicate(pred, ff, `is, `P);
	    	}
	    	BTRUE() | BFALSE() -> { return pred; }
	       	P -> {
	    		throw new AssertionError("Unknown Predicate: " + `P);
	    	}
	    }
	}
}
