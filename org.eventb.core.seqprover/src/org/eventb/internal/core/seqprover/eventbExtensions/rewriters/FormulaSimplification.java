package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

public class FormulaSimplification {

	public static Predicate simplifiedAssociativePredicate(
			AssociativePredicate predicate, Predicate[] children,
			Predicate neutral, Predicate determinant) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		boolean rewrite = false;
		for (Predicate subPred : children) {
			if (subPred.equals(determinant))
				return determinant;
			if (subPred.equals(neutral)) {
				rewrite = true;
			} else {
				predicates.add(subPred);
			}
		}

		if (rewrite) {
			if (predicates.size() == 0)
				return neutral;

			if (predicates.size() == 1)
				return predicates.get(0);

			AssociativePredicate newPred = FormulaFactory.getDefault()
					.makeAssociativePredicate(predicate.getTag(), predicates,
							null);
			return newPred;
		}
		return predicate;
	}

	public static Predicate splitQuantifiedPredicate(int tag, int subTag,
			BoundIdentDecl[] boundIdentifiers, Predicate[] children) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		for (Predicate child : children) {
			Predicate qPred = FormulaFactory
					.getDefault()
					.makeQuantifiedPredicate(tag, boundIdentifiers, child, null);
			predicates.add(qPred);
		}

		return FormulaFactory.getDefault().makeAssociativePredicate(subTag,
				predicates, null);
	}

	public static Predicate rewriteMapsto(Expression E, Expression F,
			Expression G, Expression H) {
		FormulaFactory ff = FormulaFactory.getDefault();
		Predicate pred1 = ff.makeRelationalPredicate(Formula.EQUAL, E, G, null);
		Predicate pred2 = ff.makeRelationalPredicate(Formula.EQUAL, F, H, null);
		return ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] {
				pred1, pred2 }, null);
	}

	public static Expression simplifiedAssociativeExpression(
			AssociativeExpression expression, Expression[] children) {
		List<Expression> expressions = new ArrayList<Expression>();
		Expression empty = FormulaFactory.getDefault().makeAtomicExpression(Formula.EMPTYSET, null);
		boolean rewrite = false;
		int tag = expression.getTag();
		for (Expression child : children) {
			if (child.equals(empty)) {
				if (tag == Formula.BINTER) {
					return empty;
				}
				else {
					rewrite = true;					
				}
			} else {
				expressions.add(child);
			}
		}

		if (rewrite) {
			if (expressions.size() == 0)
				return empty;

			if (expressions.size() == 1)
				return expressions.get(0);

			AssociativeExpression newExpression = FormulaFactory.getDefault()
					.makeAssociativeExpression(tag, expressions,
							null);
			return newExpression;
		}
		return expression;
	}

}
