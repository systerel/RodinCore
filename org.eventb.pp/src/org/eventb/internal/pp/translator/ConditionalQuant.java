package org.eventb.internal.pp.translator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;

public class ConditionalQuant extends DecomposedQuant {

	final ArrayList<Expression> substitutes = new ArrayList<Expression>();
	final ArrayList<Expression> substituted = new ArrayList<Expression>();

	
	public ConditionalQuant(FormulaFactory ff) {
		super(ff);
	}
	
	public Expression condSubstitute(Expression expr) {
		if(GoalChecker.isMapletExpression(expr)) {
			return expr;
		}
		else {
			SourceLocation loc = expr.getSourceLocation();
			Expression substitute = addQuantifier(expr.getType(), loc); 
			substitutes.add(substitute);
			substituted.add(expr);
			return substitute;			
		}
	}
	
	@Override
	public Expression push(Expression expr) {
		for(Expression subsistute : substitutes) {
			if(expr == subsistute)
				return expr;
		}
		return super.push(expr);
	}
	
	public Predicate conditionalQuantify(Predicate pred, Translator translator) {
		if(substitutes.size() == 0) return pred;
		else {
			SourceLocation loc = pred.getSourceLocation();
			List<Predicate> bindings = new LinkedList<Predicate>();
			for(int i = 0; i < substitutes.size(); i++) { 
				bindings.add(
					translator.translateEqual(
						ff.makeRelationalPredicate(
								Formula.EQUAL, 
								substitutes.get(i), 
								push(substituted.get(i)), 
								loc), 
						ff));
			}
			
			return makeQuantifiedPredicate(
					Formula.FORALL,
					ff.makeBinaryPredicate(
							Formula.LIMP,
							FormulaConstructor.makeLandPredicate(
									ff, bindings, loc), 
							pred,
							loc),
					loc);	
		}
	}

}