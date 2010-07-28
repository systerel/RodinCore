/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/

package org.eventb.internal.pptrans.translator;

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
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;


/**
 * Implements the identity translation
 * Contains the pattern matching part of the identity translation. 
 * The idea behind identity translation is the following: The  pp 
 * package contains several recursively implemented translators. 
 * These translators do just in few cases transform a formula part but 
 * more often call themselves  on the child formulas. This work is factorized
 * to the IdentityTranslator.
 * @author Matthias Konrad
 */
@SuppressWarnings({"unused", "cast"})
public class IdentityTranslator extends IdentityTranslatorBase {

	protected IdentityTranslator(FormulaFactory ff) {
		super(ff);
	}

	%include {FormulaV2.tom}
	
	@Override
	protected Expression translate(Expression expr) {
		SourceLocation loc = expr.getSourceLocation();
	    %match (Expression expr) {
	    	AssociativeExpression(children) -> {
	    		return idTransAssociativeExpression(expr, `children);
	    	}
	    	AtomicExpression() -> { 
	    		return expr; 
	    	}
			Identifier() -> { 
	    		return expr; 
	    	}
	    	IntegerLiteral(_) -> { 
	    		return expr; 
	    	}
	    	BinaryExpression(l, r) -> {
	    		return idTransBinaryExpression(expr, `l, `r);
	    	}
	    	Bool(P) -> {
	    		return idTransBoolExpression(expr, `P);
	    	}
	    	Cset(is, P, E) -> {
	    		return idTransQuantifiedExpression(expr, `is, `P, `E);
	    	}
	    	Qinter(is, P, E) -> {
	    		return idTransQuantifiedExpression(expr, `is, `P, `E);
	    	}
	    	Qunion(is, P, E) -> {
	    		return idTransQuantifiedExpression(expr, `is, `P, `E);
	    	}
	    	SetExtension(children) -> {
	    		return idTransSetExtension(expr, `children);
	    	}
	    	UnaryExpression(child) -> {
	    		return idTransUnaryExpression(expr, `child);
	    	}
	    	_ -> {
	    		throw new AssertionError("Unknown expression: " + expr);
	    	}
	    }
	}
	
	@Override
	protected Predicate translate(Predicate pred) {
		SourceLocation loc = pred.getSourceLocation();
	    %match (Predicate pred) {
	    	Land(children) -> {
	    		return idTransAssociativePredicate(pred, `children);
	    	}
	    	Lor(children) -> {
	    		return idTransAssociativePredicate(pred, `children);
	    	}
	    	Limp(l, r) -> {
	    		return idTransBinaryPredicate(pred, `l, `r);
	    	}
	      	Leqv(l, r) -> {
	    		return idTransBinaryPredicate(pred, `l, `r);
	    	}
	      	Not(P)-> {
	    		return idTransUnaryPredicate(pred, `P);
	    	}
	    	Finite(E)-> {
	    		return idTransSimplePredicate(pred, `E);
	    	}
	    	MultiplePredicate(children) ->
	    	{
	    		return idTransMultiplePredicate(pred, `children);
	    	}
			RelationalPredicate(l, r) -> 
			{
	    		return idTransRelationalPredicate(pred, `l, `r);
	    	}
	    	ForAll(is, P) -> {
	    		return idTransQuantifiedPredicate(pred, `is, `P);
	    	}
	    	Exists(is, P) -> {
	    		return idTransQuantifiedPredicate(pred, `is, `P);
	    	}
	    	BTRUE() -> {
	    		return pred;
	    	}
	       	BFALSE() -> {
	    		return pred;
	    	}
	       	_ -> {
	    		throw new AssertionError("Unknown Predicate: " + pred);
	    	}
	    }
	}
}
