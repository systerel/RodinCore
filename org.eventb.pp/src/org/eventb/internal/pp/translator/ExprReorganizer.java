package org.eventb.internal.pp.translator;

import java.util.LinkedList;

import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryExpression;

public class ExprReorganizer extends Sub2QuantTranslator {

	private ExprReorganizer(){
	}
	
	public static Predicate reorganize(Predicate P, FormulaFactory ff) {
		return Sub2QuantTranslator.translate(P, new ExprReorganizer(), ff);
	}
	
	@Override
	protected Sub2QuantTranslator create() {
		return new ExprReorganizer();
	}
	
	@Override
	protected Expression translate(Expression expr, FormulaFactory ff) {
		SourceLocation loc = expr.getSourceLocation();
		
		switch(expr.getTag()){
		case Formula.KCARD: {
			UnaryExpression unaryExpr = (UnaryExpression)expr;
			Expression newChild = translate(unaryExpr.getChild(), ff);
			if(newChild == unaryExpr.getChild())
				return bindExpression(expr, ff);
			else
				return bindExpression(
					ff.makeUnaryExpression(Formula.KCARD, newChild, loc), ff);
		}
		case Formula.KBOOL: {
			BoolExpression boolExpr = (BoolExpression)expr;
			Predicate newPredicate = translate(boolExpr.getPredicate(), ff);
			if(newPredicate == boolExpr.getPredicate())
				return bindExpression(expr, ff);
			else
				return bindExpression(
					ff.makeBoolExpression(newPredicate, loc), ff);
		}
		default:
			return super.translate(expr, ff);
		}
	}
}
