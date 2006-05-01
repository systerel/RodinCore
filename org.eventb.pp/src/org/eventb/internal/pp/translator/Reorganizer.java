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
		public final List<Predicate> bindings = new LinkedList<Predicate>();
		private final DecomposedQuant quantification;

		public boolean inEquality;

		public ExpressionExtractor(DecomposedQuant quantification) {
			this.quantification = quantification;
		}
		
		protected Expression bindExpression(Expression expr, FormulaFactory ff, String name) {
			SourceLocation loc = expr.getSourceLocation();
			
			Expression ident = quantification.addQuantifier(expr.getType(), name, loc);
			bindings.add(
					ff.makeRelationalPredicate(
							Formula.EQUAL, 
							ident, 
							quantification.push(expr), 
							loc));
			return ident;		
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
					return bindExpression(expr, ff, "cd");
				case Formula.FUNIMAGE:
					return bindExpression(expr, ff, "fi");
				case Formula.KMIN:
					return bindExpression(expr, ff, "mi");
				case Formula.KMAX:
					return bindExpression(expr, ff, "ma");
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
		return ff.makeRelationalPredicate(pred.getTag(), left, right, pred.getSourceLocation());
	}
	
	@Override
	protected Predicate translateArithmeticBorder(RelationalPredicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		
		Decomp2PhaseQuant forall = new Decomp2PhaseQuant(ff);

		ExpressionExtractor extractor = new ExpressionExtractor(forall);
		doPhase(pred, extractor, ff);

		if(extractor.bindings.size() == 0)
			return pred;
		else {
			forall.startPhase2();
			extractor = new ExpressionExtractor(forall);
			pred = doPhase(pred, extractor, ff);
			
			return forall.makeQuantifiedPredicate(
				Formula.FORALL, 
				ff.makeBinaryPredicate(
					Formula.LIMP,
					FormulaConstructor.makeLandPredicate(ff, extractor.bindings, loc),
					pred,
					loc),
				loc);
		}
	}
	
	@Override
	protected Predicate translateSetBorder(RelationalPredicate pred, FormulaFactory ff) {
		return pred;
	}
}
