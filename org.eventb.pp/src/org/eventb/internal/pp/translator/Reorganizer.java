package org.eventb.internal.pp.translator;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.DefaultVisitor;
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
	
	public static class OffsetCalculator extends IdentityVisitor {
		protected int count;
		protected FormulaFactory ff;
		private boolean inEquality;
		
		public static int calculate(Expression expr, boolean inEquality) {
			OffsetCalculator calc = new OffsetCalculator();
			calc.inEquality = inEquality;
			expr.accept(calc);
			return calc.count;
		}		
		
		@Override
		public boolean visitExpression(Expression expr) {
			inEquality = false;
			return true;
		}
		
		private void condCount() {
			if(inEquality)
				inEquality = false;
			else
				count++;
		}
		
		@Override
		public boolean enterKCARD(UnaryExpression expr) {
			condCount();
			return false;			
		}
		
		@Override
		public boolean enterFUNIMAGE(BinaryExpression expr) {
			condCount();
			return false;			
		}
		
		@Override
		public boolean enterKMIN(UnaryExpression expr) {
			condCount();
			return false;			
		}
		
		@Override
		public boolean enterKMAX(UnaryExpression expr) {
			condCount();
			return false;			
		}
		
	}
	
	public class ExpressionExtractor extends IdentityTranslator {
		private int boundIdentOffset = 0;
		public List<Predicate> bindings = new LinkedList<Predicate>();
		public List<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
		private QuantMapletBuilder mb = new QuantMapletBuilder();
		public boolean inEquality;
		
		private Expression addBoundIdentifier(Type type, SourceLocation loc, FormulaFactory ff, String name) {
			
			mb.calculate(type, boundIdentOffset, name, loc, ff);
			identDecls.addAll(mb.X());
			boundIdentOffset += mb.offset();
			
			return mb.V();
		}
			
		protected Expression bindExpression(Expression expr, FormulaFactory ff, String name) {
			SourceLocation loc = expr.getSourceLocation();
			
			Expression ident = addBoundIdentifier(expr.getType(), loc, ff, name);
			bindings.add(ff.makeRelationalPredicate(Formula.EQUAL, ident, expr, loc));
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
	
	@Override
	protected Predicate translateArithmeticBorder(RelationalPredicate pred, FormulaFactory ff) {
		SourceLocation loc = pred.getSourceLocation();
		
		boolean isEquality = isIdentifierEquality(pred);
		
		int leftShift = OffsetCalculator.calculate(pred.getLeft(), isEquality);
		int rightShift = OffsetCalculator.calculate(pred.getRight(), isEquality);
		int totalShift = leftShift + rightShift;
		
		if(totalShift == 0)
			return pred;
		else {			
			ExpressionExtractor extractor = new ExpressionExtractor();
			extractor.inEquality = isEquality;
			Expression left = leftShift > 0 ? 
				extractor.translate(pred.getLeft().shiftBoundIdentifiers(totalShift, ff), ff) :
				pred.getLeft().shiftBoundIdentifiers(totalShift, ff);
			
			extractor.inEquality = isEquality;
			Expression right = rightShift > 0 ?
				extractor.translate(pred.getRight().shiftBoundIdentifiers(totalShift, ff), ff) :
				pred.getRight().shiftBoundIdentifiers(totalShift, ff);
			
			return ff.makeQuantifiedPredicate(
				Formula.FORALL, 
				extractor.identDecls, 
				ff.makeBinaryPredicate(
					Formula.LIMP,
					FormulaConstructor.makeLandPredicate(ff, extractor.bindings, loc),
					ff.makeRelationalPredicate(pred.getTag(), left, right, loc),
					loc),
				loc);
		}
	}
	
	@Override
	protected Predicate translateSetBorder(RelationalPredicate pred, FormulaFactory ff) {
		return pred;
	}
}
