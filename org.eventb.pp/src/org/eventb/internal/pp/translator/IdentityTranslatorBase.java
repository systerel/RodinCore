package org.eventb.internal.pp.translator;
import java.util.ArrayList;
import java.util.Arrays;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
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
			if(newChild.getTag() == expr.getTag()) {
				AssociativeExpression assChild = (AssociativeExpression)newChild;
				newChildren.addAll(Arrays.asList(assChild.getChildren()));
				hasChanged = true;
			}
			else {
				newChildren.add(newChild);
				hasChanged = hasChanged || (newChild != child);
			}
		}
		if(hasChanged)
    		return ff.makeAssociativeExpression(expr.getTag(), newChildren, loc);
    	else
    		return expr;		    	
	}
	
	
	protected Expression idTransBinaryExpression(
			Expression expr, FormulaFactory ff, Expression l, Expression r) {
		SourceLocation loc = expr.getSourceLocation();

		Expression nl = translate(l, ff);
		Expression nr = translate(r, ff);

		if(nl == l && nr == r) return expr;
		else
    		return ff.makeBinaryExpression(expr.getTag(), nl, nr, loc);
	}
	
	protected Expression idTransBoolExpression(
			Expression expr, FormulaFactory ff, Predicate P) {

		SourceLocation loc = expr.getSourceLocation();
		Predicate nP = translate(P, ff);
		
		if(nP == P) return expr;
		else 
			return ff.makeBoolExpression(nP, loc);
	}

	protected Expression idTransQuantifiedExpression(
			Expression expr, FormulaFactory ff, BoundIdentDecl[] is, Predicate P, Expression E) {

		SourceLocation loc = expr.getSourceLocation();
		Predicate nP = translate(P, ff);
		Expression nE = translate(E, ff);

		if(nP == P && nE == E) return expr;
		else
			return ff.makeQuantifiedExpression(
				expr.getTag(), is, nP, nE, loc, QuantifiedExpression.Form.Explicit);	    			
	}
	
	protected Expression idTransSetExtension(
		Expression expr, FormulaFactory ff, Expression[] children) {

		SourceLocation loc = expr.getSourceLocation();
		boolean hasChanged = false;
		ArrayList<Expression> newChildren = new ArrayList<Expression>();
		for (Expression child: children) {
			Expression newChild = translate(child, ff);
			newChildren.add(newChild);
			hasChanged = hasChanged || (newChild != child);
		}
		if(hasChanged)
    		return ff.makeSetExtension(newChildren, loc);
    	else
    		return expr;
	}
	protected Expression idTransUnaryExpression(
			Expression expr, FormulaFactory ff, Expression child) {

		SourceLocation loc = expr.getSourceLocation();
		Expression newChild = translate(child, ff);
		if(newChild == child) return expr;
		else
    		return ff.makeUnaryExpression(expr.getTag(), newChild, loc);
	}
	
	protected Predicate idTransAssociativePredicate(
			Predicate pred, FormulaFactory ff, Predicate[] children) {

		SourceLocation loc = pred.getSourceLocation();

		ArrayList<Predicate> newChildren = new ArrayList<Predicate>();
		boolean hasChanged = false;
		for (Predicate child: children) {
			Predicate newChild = translate(child, ff);
			if(newChild.getTag() == pred.getTag()) {
				AssociativePredicate assPred = (AssociativePredicate)newChild;
				newChildren.addAll(Arrays.asList(assPred.getChildren()));
				hasChanged = true;
			}
			else {
				newChildren.add(newChild);
				hasChanged = hasChanged || (newChild != child);
			}
		}
		if(hasChanged)
			return ff.makeAssociativePredicate(pred.getTag(), newChildren, loc);
		else
			return pred;
	}

	protected Predicate idTransBinaryPredicate(
			Predicate pred, FormulaFactory ff, Predicate l, Predicate r) {

		SourceLocation loc = pred.getSourceLocation();

		Predicate nl = translate(l, ff);
		Predicate nr = translate(r, ff);
		
		if(nl == l && nr == r) return pred;
		else
			return ff.makeBinaryPredicate(pred.getTag(),nl, nr, loc);
	}
	
	protected Predicate idTransUnaryPredicate(
			Predicate pred, FormulaFactory ff, Predicate P) {

		SourceLocation loc = pred.getSourceLocation();

  		Predicate nP = translate(P, ff);
  		if(nP == P) return pred;
  		else
			return ff.makeUnaryPredicate(Formula.NOT, nP, loc);
	}
	
	protected Predicate idTransSimplePredicate(
			Predicate pred, FormulaFactory ff, Expression E) {

		SourceLocation loc = pred.getSourceLocation();

  		Expression nE = translate(E, ff);
  		if(nE == E) return pred;
  		else
			return ff.makeSimplePredicate(Formula.KFINITE, nE, loc);
	}
	
	protected Predicate idTransRelationalPredicate(
			Predicate pred, FormulaFactory ff, Expression l, Expression r) {

		SourceLocation loc = pred.getSourceLocation();

		Expression nl = translate(l, ff);
		Expression nr = translate(r, ff);

		if(nl == l && nr == r) return pred;
		else
			return  ff.makeRelationalPredicate(pred.getTag(), nl, nr, loc);
	}
	
	protected Predicate idTransQuantifiedPredicate(
			Predicate pred, FormulaFactory ff, BoundIdentDecl[] is, Predicate P) {

		SourceLocation loc = pred.getSourceLocation();

		Predicate nP = translate(P, ff);
  		
  		if(nP == P) return pred;
  		else
			return ff.makeQuantifiedPredicate(pred.getTag(), is, nP, loc);	
	}
	
}
