package org.eventb.internal.pp.translator;

import java.util.ArrayList;
import java.util.Arrays;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.SourceLocation;

public abstract class IdentityTranslatorBase {
 	
	protected abstract Expression translate(Expression expr, FormulaFactory ff);
	protected abstract Predicate translate(Predicate expr, FormulaFactory ff);
	
	protected Expression idTransAssociativeExpression(
			Expression expr, FormulaFactory ff, Expression[] children) {

		SourceLocation loc = expr.getSourceLocation();
		ArrayList<Expression> newChildren = new ArrayList<Expression>();
		boolean hasChanged = false;
		for (Expression child: children) {
			Expression newChild = translate(child, ff);
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
			Expression expr, FormulaFactory ff, Expression l, Expression r) {

		SourceLocation loc = expr.getSourceLocation();
		Expression nl = translate(l, ff);
		Expression nr = translate(r, ff);

		if (nl == l && nr == r) {
			return expr;
		}
		return ff.makeBinaryExpression(expr.getTag(), nl, nr, loc);
	}
	
	protected Expression idTransBoolExpression(
			Expression expr, FormulaFactory ff, Predicate P) {

		SourceLocation loc = expr.getSourceLocation();
		Predicate nP = translate(P, ff);
		
		if (nP == P) {
			return expr;
		}
		return ff.makeBoolExpression(nP, loc);
	}

	protected Expression idTransQuantifiedExpression(
			Expression expr, FormulaFactory ff, BoundIdentDecl[] is, Predicate P, Expression E) {

		SourceLocation loc = expr.getSourceLocation();
		Predicate nP = translate(P, ff);
		Expression nE = translate(E, ff);

		if (nP == P && nE == E) {
			return expr;
		}
		return ff.makeQuantifiedExpression(expr.getTag(),
				is, nP, nE, loc, QuantifiedExpression.Form.Explicit);	    			
	}
	
	protected Expression idTransSetExtension(
		Expression expr, FormulaFactory ff, Expression[] children) {

		SourceLocation loc = expr.getSourceLocation();
		boolean hasChanged = false;
		ArrayList<Expression> newChildren = new ArrayList<Expression>();
		for (Expression child: children) {
			Expression newChild = translate(child, ff);
			newChildren.add(newChild);
			hasChanged |= newChild != child;
		}
		if (! hasChanged) {
			return expr;
		}
		return ff.makeSetExtension(newChildren, loc);
	}
	
	protected Expression idTransUnaryExpression(
			Expression expr, FormulaFactory ff, Expression child) {

		SourceLocation loc = expr.getSourceLocation();
		Expression newChild = translate(child, ff);
		if (newChild == child) {
			return expr;
		}
		return ff.makeUnaryExpression(expr.getTag(), newChild, loc);
	}
	
	protected Predicate idTransAssociativePredicate(
			Predicate pred, FormulaFactory ff, Predicate[] children) {

		SourceLocation loc = pred.getSourceLocation();

		ArrayList<Predicate> newChildren = new ArrayList<Predicate>();
		boolean hasChanged = false;
		for (Predicate child: children) {
			Predicate newChild = translate(child, ff);
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
			Predicate pred, FormulaFactory ff, Predicate l, Predicate r) {

		SourceLocation loc = pred.getSourceLocation();
		Predicate nl = translate(l, ff);
		Predicate nr = translate(r, ff);
		
		if (nl == l && nr == r) {
			return pred;
		}
		return ff.makeBinaryPredicate(pred.getTag(),nl, nr, loc);
	}
	
	protected Predicate idTransUnaryPredicate(
			Predicate pred, FormulaFactory ff, Predicate P) {

		SourceLocation loc = pred.getSourceLocation();
  		Predicate nP = translate(P, ff);
  		
  		if (nP == P) {
  			return pred;
  		}
  		return ff.makeUnaryPredicate(pred.getTag(), nP, loc);
	}
	
	protected Predicate idTransSimplePredicate(
			Predicate pred, FormulaFactory ff, Expression E) {

		SourceLocation loc = pred.getSourceLocation();
  		Expression nE = translate(E, ff);
  		
  		if (nE == E) {
  			return pred;
  		}
  		return ff.makeSimplePredicate(pred.getTag(), nE, loc);
	}
	
	protected Predicate idTransRelationalPredicate(
			Predicate pred, FormulaFactory ff, Expression l, Expression r) {

		SourceLocation loc = pred.getSourceLocation();
		Expression nl = translate(l, ff);
		Expression nr = translate(r, ff);

		if (nl == l && nr == r) {
			return pred;
		}
		return  ff.makeRelationalPredicate(pred.getTag(), nl, nr, loc);
	}
	
	protected Predicate idTransQuantifiedPredicate(
			Predicate pred, FormulaFactory ff, BoundIdentDecl[] is, Predicate P) {

		SourceLocation loc = pred.getSourceLocation();
		Predicate nP = translate(P, ff);
  		
  		if (nP == P) {
  			return pred;
  		}
  		return ff.makeQuantifiedPredicate(pred.getTag(), is, nP, loc);	
	}
	
}
