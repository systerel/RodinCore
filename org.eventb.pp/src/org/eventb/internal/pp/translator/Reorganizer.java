package org.eventb.internal.pp.translator;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;

public class Reorganizer extends BorderTranslator {
	
	public class ExpressionExtractor extends IdentityTranslator {
		private final ConditionalQuant quantification;

		public boolean inEquality;

		public ExpressionExtractor(ConditionalQuant quantification) {
			this.quantification = quantification;
		}
		
		@Override
		protected Expression translate(Expression expr, FormulaFactory ff) {
			if(inEquality) {
				inEquality = false;
				return super.translate(expr, ff);
			}
			else {
				switch(expr.getTag()) {
				case Formula.KCARD:
				case Formula.FUNIMAGE:
				case Formula.KMIN:
				case Formula.KMAX:
					return  quantification.condSubstitute(expr);
				case Formula.BOUND_IDENT:
					return quantification.push(expr);
				default:
					return super.translate(expr, ff);
				}
			}
		}
		
		@Override
		protected Predicate translate(Predicate pred, FormulaFactory ff) {
			return pred;
		}
	}
	
	public static boolean isIdentifierEquality(Predicate pred) {
		if(pred instanceof RelationalPredicate) {
			RelationalPredicate relPred = (RelationalPredicate)pred;
			if( pred.getTag() == Formula.EQUAL || pred.getTag() == Formula.NOTEQUAL) {
				return 
					relPred.getLeft() instanceof Identifier || 
					relPred.getRight() instanceof Identifier;
			}
			else
				return false;
		}
		else
			return false;
	}
	
	
	protected RelationalPredicate doPhase(
			RelationalPredicate pred, ExpressionExtractor extractor, FormulaFactory ff) {
	
		boolean isEquality = isIdentifierEquality(pred);
		
		extractor.inEquality = isEquality;
		Expression left = extractor.translate(pred.getLeft(), ff);
		extractor.inEquality = isEquality;
		Expression right = extractor.translate(pred.getRight(), ff);
		if(left != pred.getLeft() || right != pred.getRight())
			return ff.makeRelationalPredicate(pred.getTag(), left, right, pred.getSourceLocation());
		else
			return pred;
	}
	
	@Override
	protected Predicate translateArithmeticBorder(RelationalPredicate pred, FormulaFactory ff) {
		ConditionalQuant forall = new ConditionalQuant(ff);

		doPhase(pred, new ExpressionExtractor(forall), ff);
		forall.startPhase2();
		pred = doPhase(pred, new ExpressionExtractor(forall), ff);
			
		return forall.conditionalQuantify(
			Formula.FORALL,
			pred,
			null);
	}
	
	@Override
	protected Predicate translateSetBorder(RelationalPredicate pred, FormulaFactory ff) {
		return pred;
	}
}
