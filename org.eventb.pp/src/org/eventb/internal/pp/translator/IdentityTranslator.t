/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.math.BigInteger;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;


/**
 * Implements the identity translation
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class IdentityTranslator extends IdentityTranslatorBase {

	%include {Formula.tom}
	
	@Override
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
	
	@Override
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
