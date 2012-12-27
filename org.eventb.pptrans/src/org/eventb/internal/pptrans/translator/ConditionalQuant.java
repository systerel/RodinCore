/*******************************************************************************
 * Copyright (c) 2006, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.pptrans.translator;

import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;


/**
 * @author mkonrad
 * Implements the conditional quantification c∀/c∃. The * operator is
 * implemented by the method condSubstitute. Sinse ConditionalQuant
 * specializes Decomp2PhaseQuant, the substitutions need to be done twice,
 * separated by a call to startPhase2. 
 */
public class ConditionalQuant extends Decomp2PhaseQuant {

	final List<Expression> substitutes = new LinkedList<Expression>();
	final List<Predicate> bindings = new LinkedList<Predicate>();
	
	public ConditionalQuant(FormulaFactory ff) {
		super(ff);
	}
	
	/**
	 * Implements the * operator. See documentation.
	 * @param expr the expression to be *ed
	 * @return The substitute for expr
	 */
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
	
	/**
	 * Conditionally generates a new quantified predicate
	 * @param tag either Formula.EXISTS or Formula.FORALL
	 * @param pred the quantified predicate
	 * @param translator if present is used to translate the bindings.
	 * @return a new quantified predicate
	 */
	public  Predicate conditionalQuantify(
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