package org.eventb.internal.pp.translator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;

public class CondQuantifier {
	QuantMapletBuilder mb = new QuantMapletBuilder();
	FormulaFactory ff;
	List<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>(); 
	ArrayList<Expression> substitutes = new ArrayList<Expression>();
	ArrayList<Expression> substituted = new ArrayList<Expression>();

	public CondQuantifier(FormulaFactory ff) {
		this.ff = ff;
	}

	
	public Expression condSubstitute(Expression expr) {
		if(GoalChecker.isMapletExpression(expr, ff)) {
			return expr;
		}
		else {
			SourceLocation loc = expr.getSourceLocation();
			mb.calculate(expr.getType(), identDecls.size(), loc, ff);
			Expression substitute = mb.getMaplet();
			identDecls.addAll(0, mb.getIdentDecls());
			substitutes.add(substitute);
			substituted.add(expr);
			return substitute;			
		}
	}
	
	public Expression condShift(Expression expr) {
		for(Expression subsistute : substitutes) {
			if(expr == subsistute)
				return expr;
		}
		return expr.shiftBoundIdentifiers(offset(), ff);
	}
	
	public int offset() {
		return identDecls.size();
	}
	
	public Predicate conditionalQuantify(Predicate pred, Translator translator) {
		if(identDecls.size() == 0) return pred;
		else {
			SourceLocation loc = pred.getSourceLocation();
			List<Predicate> bindings = new LinkedList<Predicate>();
			for(int i = 0; i < substitutes.size(); i++) { 
				bindings.add(
					translator.translateEqual(
						ff.makeRelationalPredicate(
								Formula.EQUAL, 
								substitutes.get(i), 
								substituted.get(i).shiftBoundIdentifiers(identDecls.size(), ff), 
								loc), 
						ff));
			}
			
			return ff.makeQuantifiedPredicate(
					Formula.FORALL,
					identDecls,
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