/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added translator for multiple predicates (math V2)
 *******************************************************************************/
package org.eventb.internal.pptrans.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.SourceLocation;

/**
 *  Contains the logic of the IdentityTranslator. For more details see the IdentityTranslator.t
 * @author mkonrad
 *
 */
public abstract class IdentityTranslatorBase {
	
	protected final FormulaFactory ff;
	
	protected IdentityTranslatorBase(FormulaFactory ff) {
		this.ff = ff;
	}

	protected abstract Expression translate(Expression expr);
	
	protected abstract Predicate translate(Predicate expr);
	
	protected Expression idTransAssociativeExpression(
			Expression expr, Expression[] children) {

		SourceLocation loc = expr.getSourceLocation();
		ArrayList<Expression> newChildren = new ArrayList<Expression>();
		boolean hasChanged = false;
		for (Expression child: children) {
			Expression newChild = translate(child);
			if (newChild.getTag() == expr.getTag()) {
				AssociativeExpression assChild = (AssociativeExpression) newChild;
				newChildren.addAll(Arrays.asList(assChild.getChildren()));
				hasChanged = true;
			} else {
				newChildren.add(newChild);
				hasChanged |= newChild != child;
			}
		}
		if (! hasChanged) {
    		return expr;
		}
		return ff.makeAssociativeExpression(expr.getTag(), newChildren, loc);
	}
	
	
	protected Expression idTransBinaryExpression(
			Expression expr, Expression l, Expression r) {

		SourceLocation loc = expr.getSourceLocation();
		Expression nl = translate(l);
		Expression nr = translate(r);

		if (nl == l && nr == r) {
			return expr;
		}
		return ff.makeBinaryExpression(expr.getTag(), nl, nr, loc);
	}
	
	protected Expression idTransBoolExpression(
			Expression expr, Predicate P) {

		SourceLocation loc = expr.getSourceLocation();
		Predicate nP = translate(P);
		
		if (nP == P) {
			return expr;
		}
		return ff.makeBoolExpression(nP, loc);
	}

	protected Expression idTransQuantifiedExpression(
			Expression expr, BoundIdentDecl[] is, Predicate P, Expression E) {

		SourceLocation loc = expr.getSourceLocation();
		Predicate nP = translate(P);
		Expression nE = translate(E);

		if (nP == P && nE == E) {
			return expr;
		}
		return ff.makeQuantifiedExpression(expr.getTag(),
				is, nP, nE, loc, QuantifiedExpression.Form.Explicit);	    			
	}
	
	protected Expression idTransSetExtension(
		Expression expr, Expression[] children) {

		SourceLocation loc = expr.getSourceLocation();
		boolean hasChanged = false;
		ArrayList<Expression> newChildren = new ArrayList<Expression>();
		for (Expression child: children) {
			Expression newChild = translate(child);
			newChildren.add(newChild);
			hasChanged |= newChild != child;
		}
		if (! hasChanged) {
			return expr;
		}
		return ff.makeSetExtension(newChildren, loc);
	}
	
	protected Expression idTransUnaryExpression(
			Expression expr, Expression child) {

		SourceLocation loc = expr.getSourceLocation();
		Expression newChild = translate(child);
		if (newChild == child) {
			return expr;
		}
		return ff.makeUnaryExpression(expr.getTag(), newChild, loc);
	}
	
	protected Predicate idTransAssociativePredicate(
			Predicate pred, Predicate[] children) {

		SourceLocation loc = pred.getSourceLocation();

		ArrayList<Predicate> newChildren = new ArrayList<Predicate>();
		boolean hasChanged = false;
		for (Predicate child: children) {
			Predicate newChild = translate(child);
			if (newChild.getTag() == pred.getTag()) {
				AssociativePredicate assPred = (AssociativePredicate) newChild;
				newChildren.addAll(Arrays.asList(assPred.getChildren()));
				hasChanged = true;
			}
			else {
				newChildren.add(newChild);
				hasChanged |= newChild != child;
			}
		}
		if (! hasChanged) {
			return pred;
		}
		return ff.makeAssociativePredicate(pred.getTag(), newChildren, loc);
	}

	protected Predicate idTransBinaryPredicate(
			Predicate pred, Predicate l, Predicate r) {

		SourceLocation loc = pred.getSourceLocation();
		Predicate nl = translate(l);
		Predicate nr = translate(r);
		
		if (nl == l && nr == r) {
			return pred;
		}
		return ff.makeBinaryPredicate(pred.getTag(),nl, nr, loc);
	}
	
	protected Predicate idTransUnaryPredicate(
			Predicate pred, Predicate P) {

		SourceLocation loc = pred.getSourceLocation();
  		Predicate nP = translate(P);
  		
  		if (nP == P) {
  			return pred;
  		}
  		return ff.makeUnaryPredicate(pred.getTag(), nP, loc);
	}
	
	protected Predicate idTransSimplePredicate(
			Predicate pred, Expression E) {

		SourceLocation loc = pred.getSourceLocation();
  		Expression nE = translate(E);
  		
  		if (nE == E) {
  			return pred;
  		}
  		return ff.makeSimplePredicate(pred.getTag(), nE, loc);
	}
	
	protected Predicate idTransMultiplePredicate(
			Predicate pred, Expression[] children) {

		SourceLocation loc = pred.getSourceLocation();

		final List<Expression> newChildren = new ArrayList<Expression>();
		boolean hasChanged = false;
		for (Expression child: children) {
			Expression newChild = translate(child);
				newChildren.add(newChild);
				hasChanged |= newChild != child;
		}
		if (! hasChanged) {
			return pred;
		}
		return ff.makeMultiplePredicate(pred.getTag(), newChildren, loc);
	}
	
	protected Predicate idTransRelationalPredicate(
			Predicate pred, Expression l, Expression r) {

		SourceLocation loc = pred.getSourceLocation();
		Expression nl = translate(l);
		Expression nr = translate(r);

		if (nl == l && nr == r) {
			return pred;
		}
		return  ff.makeRelationalPredicate(pred.getTag(), nl, nr, loc);
	}
	
	protected Predicate idTransQuantifiedPredicate(
			Predicate pred, BoundIdentDecl[] is, Predicate P) {

		SourceLocation loc = pred.getSourceLocation();
		Predicate nP = translate(P);
  		
  		if (nP == P) {
  			return pred;
  		}
  		return ff.makeQuantifiedPredicate(pred.getTag(), is, nP, loc);	
	}
	
}
