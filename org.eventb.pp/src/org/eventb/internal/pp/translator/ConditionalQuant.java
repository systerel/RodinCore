package org.eventb.internal.pp.translator;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;

public class ConditionalQuant extends Decomp2PhaseQuant {

	final List<Expression> substitutes = new LinkedList<Expression>();
	final List<Predicate> bindings = new LinkedList<Predicate>();
	
	public ConditionalQuant(FormulaFactory ff) {
		super(ff);
	}
	
	public Expression condSubstitute(Expression expr) {
		if(GoalChecker.isMapletExpression(expr)) {
			return expr;
		}
		else {
			List<Predicate> newBindings = new LinkedList<Predicate>(); 
			Expression substitute = purifyMaplet(expr, newBindings);
			bindings.addAll(newBindings);
			substitutes.add(substitute);
			return substitute;			
		}
	}
	
	@Override
	public Expression push(Expression expr) {
		for(Expression substitute : substitutes) {
			if(expr == substitute)
				return expr;
		}
		return super.push(expr);
	}
	
	protected Predicate conditionalQuantify(
			int tag, Predicate pred, Translator translator) {
		if(substitutes.size() == 0) return pred;
		else {
			SourceLocation loc = pred.getSourceLocation();
			List<Predicate> translatedBindings = new LinkedList<Predicate>();
			if(translator == null)
				translatedBindings = bindings;
			else { 
				for(Predicate binding : bindings) { 
					translatedBindings.add(
						translator.translateEqual(binding));
				}
			}
			if(tag == Formula.FORALL) {
				return makeQuantifiedPredicate(
					Formula.FORALL,
					ff.makeBinaryPredicate(
							Formula.LIMP,
							FormulaConstructor.makeLandPredicate(
									ff, translatedBindings, loc), 
							pred,
							loc),
					loc);
			}
			else {
				translatedBindings.add(pred);
				return makeQuantifiedPredicate(
						Formula.EXISTS,
						FormulaConstructor.makeLandPredicate(ff, translatedBindings, loc),
						loc);
			}	
		}
	}
	
	@Override
	public void startPhase2() {
		substitutes.clear();
		bindings.clear();
		super.startPhase2();
	}
	
	private Expression purifyMaplet(Expression expr, List<Predicate> newBindings) {
			SourceLocation loc = expr.getSourceLocation();
			
			if(GoalChecker.isMapletExpression(expr))
				return push(expr);
			switch(expr.getTag()) {
			case Formula.MAPSTO:
				BinaryExpression bexpr = (BinaryExpression)expr;
						
				Expression nr = purifyMaplet(bexpr.getRight(), newBindings);
				Expression nl = purifyMaplet(bexpr.getLeft(), newBindings);

				if(nr == bexpr.getLeft() && nl == bexpr.getRight()) return expr;
				else
					return ff.makeBinaryExpression(Formula.MAPSTO, nl, nr, loc);

			default:
				Expression substitute = addQuantifier(expr.getType(), loc);
				newBindings.add(0, 
					ff.makeRelationalPredicate(
						Formula.EQUAL, 
						substitute, 
						push(expr), 
						loc));
				return substitute;
			}
		}
}