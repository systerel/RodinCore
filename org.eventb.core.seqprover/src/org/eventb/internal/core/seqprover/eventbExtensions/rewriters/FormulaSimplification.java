package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;

public class FormulaSimplification {

	private static <T extends Formula<T>> Collection<T> simplifiedAssociativeFormula(
			T[] children, T neutral, T determinant) {
		Collection<T> formulas = new LinkedHashSet<T>();
		for (T child : children) {
			if (child.equals(determinant)) {
				formulas = new ArrayList<T>();
				formulas.add(determinant);
				return formulas;
			}
			if (!child.equals(neutral)) {
				formulas.add(child);
			}
		}
		return formulas;
	}

	public static Predicate simplifiedAssociativePredicate(
			AssociativePredicate predicate, Predicate[] children,
			Predicate neutral, Predicate determinant) {

		Collection<Predicate> predicates = simplifiedAssociativeFormula(
				children, neutral, determinant);
		if (predicates.size() != children.length) {
			if (predicates.size() == 0)
				return neutral;

			if (predicates.size() == 1)
				return predicates.iterator().next();

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
		int tag = expression.getTag();
		Expression neutral = tag == Formula.BUNION ? Lib.emptySet : null;
		Expression determinant = tag == Formula.BINTER ? Lib.emptySet : null;
		Collection<Expression> expressions = simplifiedAssociativeFormula(children, neutral, determinant);

		if (expressions.size() != children.length) {
			if (expressions.size() == 0)
				return Lib.emptySet;

			if (expressions.size() == 1)
				return expressions.iterator().next();

			AssociativeExpression newExpression = FormulaFactory.getDefault()
					.makeAssociativeExpression(tag, expressions, null);
			return newExpression;
		}
		return expression;
	}

	public static Predicate simplifySetMember(Predicate predicate,
			Expression E, Expression[] members) {
		for (Expression member : members) {
			if (member.equals(E)) {
				return Lib.True;
			}
		}
		return predicate;
	}

}
