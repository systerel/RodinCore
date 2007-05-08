package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

public class FormulaSimplification {

	private static FormulaFactory ff = FormulaFactory.getDefault();

	private static <T extends Formula<T>> Collection<T> simplifiedAssociativeFormula(
			T[] children, T neutral, T determinant, boolean eliminateDuplicate) {
		Collection<T> formulas;
		if (eliminateDuplicate)
			formulas = new LinkedHashSet<T>();
		else
			formulas = new ArrayList<T>();

		for (T child : children) {
			if (child.equals(determinant)) {
				formulas = new ArrayList<T>();
				formulas.add(determinant);
				return formulas;
			}

			if (!child.equals(neutral)) {
				if (child instanceof Predicate) {
					Predicate pred = (Predicate) child;
					Predicate negation;
					if (pred instanceof UnaryPredicate
							&& pred.getTag() == Predicate.NOT) {
						negation = ((UnaryPredicate) pred).getChild();
					} else {
						negation = ff.makeUnaryPredicate(Predicate.NOT,
								(Predicate) child, null);
					}
					if (formulas.contains(negation)) {
						formulas = new ArrayList<T>();
						formulas.add(determinant);
						return formulas;
					} else {
						formulas.add(child);
					}
				} else {
					formulas.add(child);
				}
			}
		}
		return formulas;
	}

	public static Predicate simplifyAssociativePredicate(
			AssociativePredicate predicate, Predicate[] children,
			Predicate neutral, Predicate determinant) {

		Collection<Predicate> predicates = simplifiedAssociativeFormula(
				children, neutral, determinant, true);
		if (predicates.size() != children.length) {
			if (predicates.size() == 0)
				return neutral;

			if (predicates.size() == 1)
				return predicates.iterator().next();

			AssociativePredicate newPred = ff.makeAssociativePredicate(
					predicate.getTag(), predicates, null);
			return newPred;
		}
		return predicate;
	}

	@Deprecated
	public static Predicate splitQuantifiedPredicate(int tag, int subTag,
			BoundIdentDecl[] boundIdentifiers, Predicate[] children) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		for (Predicate child : children) {
			Predicate qPred = ff
					.makeQuantifiedPredicate(tag, boundIdentifiers, child, null);
			predicates.add(qPred);
		}

		return ff.makeAssociativePredicate(subTag, predicates, null);
	}

	@Deprecated
	public static Predicate rewriteMapsto(Expression E, Expression F,
			Expression G, Expression H) {

		Predicate pred1 = ff.makeRelationalPredicate(Expression.EQUAL, E, G,
				null);
		Predicate pred2 = ff.makeRelationalPredicate(Expression.EQUAL, F, H,
				null);
		return ff.makeAssociativePredicate(Predicate.LAND, new Predicate[] {
				pred1, pred2 }, null);
	}

	public static Expression simplifyAssociativeExpression(
			AssociativeExpression expression, Expression[] children) {
		int tag = expression.getTag();

		Expression neutral = null;
		Expression determinant = null;
		IntegerLiteral number0 = ff.makeIntegerLiteral(new BigInteger("0"),
				null);
		IntegerLiteral number1 = ff.makeIntegerLiteral(new BigInteger("1"),
				null);
		switch (tag) {
		case Expression.BUNION:
			neutral = ff.makeEmptySet(expression.getType(), null);
			determinant = null;
			break;
		case Expression.BINTER:
			neutral = null;
			determinant = ff.makeEmptySet(expression.getType(), null);
			break;
		case Expression.PLUS:
			neutral = number0;
			determinant = null;
			break;
		}
		boolean eliminateDuplicate = (tag == Expression.BUNION || tag == Expression.BINTER);

		Collection<Expression> expressions = simplifiedAssociativeFormula(
				children, neutral, determinant, eliminateDuplicate);

		if (expressions.size() != children.length) {
			if (expressions.size() == 0)
				if (tag == Expression.BUNION || tag == Expression.BINTER)
					return ff.makeEmptySet(expression.getType(), null);
				else if (tag == Expression.PLUS)
					return number0;
				else if (tag == Expression.MUL)
					return number1;

			if (expressions.size() == 1)
				return expressions.iterator().next();

			AssociativeExpression newExpression = ff.makeAssociativeExpression(
					tag, expressions, null);
			return newExpression;
		}
		return expression;
	}

	@Deprecated
	public static Predicate simplifySetComprehension(Predicate predicate,
			final Expression E, BoundIdentDecl[] idents, Predicate guard,
			Expression expression) {
		if (idents.length == 1) {
			if (expression instanceof BoundIdentifier) {
				BoundIdentifier boundIdent = (BoundIdentifier) expression;
				if (boundIdent.getBoundIndex() == 0) {
					QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(
							Predicate.FORALL, idents, guard, null);
					return qPred.instantiate(new Expression[] { E }, ff);
				}
			}
		}
		return predicate;
	}

	@Deprecated
	public static Expression getDomain(Expression expression,
			Expression[] members) {
		Collection<Expression> domain = new LinkedHashSet<Expression>();

		for (Expression member : members) {
			if (member instanceof BinaryExpression
					&& member.getTag() == Expression.MAPSTO) {
				BinaryExpression bExp = (BinaryExpression) member;
				domain.add(bExp.getLeft());
			} else {
				return expression;
			}
		}

		return ff.makeSetExtension(domain, null);
	}

	@Deprecated
	public static Expression getRange(Expression expression,
			Expression[] members) {
		Collection<Expression> range = new LinkedHashSet<Expression>();

		for (Expression member : members) {
			if (member instanceof BinaryExpression
					&& member.getTag() == Expression.MAPSTO) {
				BinaryExpression bExp = (BinaryExpression) member;
				range.add(bExp.getRight());
			} else {
				return expression;
			}
		}

		return ff.makeSetExtension(range, null);
	}

	@Deprecated
	public static Expression simplifyFunctionOvr(Expression expression,
			Expression[] children, Expression applyTo) {
		Expression lastExpression = children[children.length - 1];
		if (lastExpression instanceof SetExtension) {
			SetExtension sExt = (SetExtension) lastExpression;
			Expression[] members = sExt.getMembers();
			if (members.length == 1) {
				Expression child = members[0];
				if (child instanceof BinaryExpression
						&& child.getTag() == Expression.MAPSTO) {
					if (((BinaryExpression) child).getLeft().equals(applyTo)) {
						return ((BinaryExpression) child).getRight();
					}
				}
			}
		}

		return expression;
	}

	@Deprecated
	public static Predicate simplifySetEquality(Predicate predicate,
			Expression[] membersE, Expression[] membersF) {
		if (membersE.length == 1 && membersF.length == 1) {
			return ff.makeRelationalPredicate(Predicate.EQUAL, membersE[0],
					membersF[0], null);
		}
		return predicate;
	}

	@Deprecated
	public static Expression simplifyMinusArithmetic(Expression expression,
			Expression E, Expression F) {
		if (F.equals(ff.makeIntegerLiteral(new BigInteger("0"), null))) {
			return E;
		} else if (E.equals(ff.makeIntegerLiteral(new BigInteger("0"), null))) {
			return ff.makeUnaryExpression(Expression.UNMINUS, F, null);
		}
		return expression;
	}

	public static Expression getFaction(Expression E, Expression F) {
		return ff.makeBinaryExpression(Expression.DIV, E, F, null);
	}

	public static Expression getFaction(Expression expression, BigInteger E,
			Expression F) {
		BigInteger number0 = new BigInteger("0");
		if (E.compareTo(number0) == 0)
			return ff.makeIntegerLiteral(number0, null);
		if (E.compareTo(number0) < 0) {
			Expression minusE = ff.makeIntegerLiteral(E.abs(), null);
			return ff.makeBinaryExpression(Expression.DIV, minusE, F, null);
		}
		return expression;
	}

	public static Expression getFaction(Expression expression, 
			Expression E, BigInteger F) {
		BigInteger number0 = new BigInteger("0");
		if (F.compareTo(number0) < 0) {
			Expression minusF = ff.makeIntegerLiteral(F.abs(), null);
			return ff.makeBinaryExpression(Expression.DIV, E, minusF, null);
		}
		BigInteger number1 = new BigInteger("1");
		if (F.compareTo(number1) == 0) {
			return ff.makeUnaryExpression(Expression.UNMINUS, E, null);
		}
		return expression;
	}

	public static Expression getFaction(Expression expression, 
			BigInteger E, BigInteger F) {
		BigInteger number0 = new BigInteger("0");
		if (E.compareTo(number0) == 0)
			return ff.makeIntegerLiteral(number0, null);
		BigInteger number1 = new BigInteger("1");
		if (F.compareTo(number1) == 0) {
			return ff.makeIntegerLiteral(E, null);
		}
		if (E.compareTo(number0) < 0 && F.compareTo(number0) < 0) {
			Expression minusE = ff.makeIntegerLiteral(E.abs(), null);
			Expression minusF = ff.makeIntegerLiteral(F.abs(), null);
			return ff.makeBinaryExpression(Expression.DIV, minusE, minusF, null);
		}
		return expression;
	}

	@Deprecated
	public static Expression simplifyExpnArithmetic(
			BinaryExpression expression, Expression E, Expression F) {
		if (F.equals(ff.makeIntegerLiteral(new BigInteger("1"), null))) {
			return E;
		} else if (F.equals(ff.makeIntegerLiteral(new BigInteger("0"), null))) {
			return ff.makeIntegerLiteral(new BigInteger("1"), null);
		} else if (E.equals(ff.makeIntegerLiteral(new BigInteger("1"), null))) {
			return ff.makeIntegerLiteral(new BigInteger("1"), null);
		}
		return expression;
	}

	public static Expression simplifyMulArithmetic(
			AssociativeExpression expression, Expression[] children) {
		Expression number1 = ff.makeIntegerLiteral(new BigInteger("1"), null);
		boolean positive = true;
		boolean change = false;
		// Expression neutral = number1;
		Expression number0 = ff.makeIntegerLiteral(new BigInteger("0"), null);
		Expression numberMinus1 = ff.makeUnaryExpression(Expression.UNMINUS,
				number1, null);
		Expression literalMinus1 = ff.makeIntegerLiteral(new BigInteger("-1"), null);
		// Expression determinant = number0;
		Collection<Expression> expressions = new ArrayList<Expression>();

		for (Expression child : children) {
			if (child.equals(number0)) {
				return number0;
			}

			if (child.equals(numberMinus1) || child.equals(literalMinus1)) {
				positive = !positive;
				change = true;
			} else if (!child.equals(number1)) {
				if (child instanceof UnaryExpression
						&& child.getTag() == Expression.UNMINUS) {
					expressions.add(((UnaryExpression) child).getChild());
					positive = !positive;
					change = true;
				}
				else if (child instanceof IntegerLiteral) {
					
						BigInteger value = ((IntegerLiteral) child).getValue();
						if (value.compareTo(new BigInteger("0")) < 0) {
							expressions.add(ff
								.makeIntegerLiteral(value.abs(), null));
						positive = !positive;
							change = true;
						}
						else {
							expressions.add(child);
						}
				} else {
					expressions.add(child);
				}
			} else {
				change = true;
			}
		}
		if (change) {
			if (expressions.size() == 0)
				return number1;

			if (expressions.size() == 1)
				return expressions.iterator().next();

			Expression newExpression = ff.makeAssociativeExpression(
					Expression.MUL, expressions, null);
			if (!positive)
				newExpression = ff.makeUnaryExpression(Expression.UNMINUS,
						newExpression, null);
			return newExpression;
		}
		return expression;
	}

	@Deprecated
	public static Expression simplifySetSubtraction(
			BinaryExpression expression, Expression S, Expression T) {
		if (T.equals(ff.makeEmptySet(S.getType(), null))) {
			return S;
		}
		return expression;
	}

	public static Predicate checkForAllOnePointRule(Predicate predicate,
			BoundIdentDecl[] identDecls, Predicate[] children, Predicate R) {
		for (Predicate child : children) {
			if (child instanceof RelationalPredicate
					&& child.getTag() == Predicate.EQUAL) {
				RelationalPredicate rPred = (RelationalPredicate) child;
				Expression left = rPred.getLeft();
				if (left instanceof BoundIdentifier) {
					BoundIdentifier y = (BoundIdentifier) left;
					Expression right = rPred.getRight();
					BoundIdentifier[] boundIdentifiers = right
							.getBoundIdentifiers();
					if (contain(identDecls, y.getDeclaration(identDecls))
							&& !contain(boundIdentifiers, y)) {
						// TODO Do the subtitution here
						return predicate;
						// return subtitute(rPred, identDecls, children, R);

					}
				}
			}
		}
		return predicate;
	}

	// private static Predicate subtitute(RelationalPredicate pred,
	// BoundIdentDecl[] identDecls, Predicate[] children, Predicate r) {
	// Collection<Predicate> predicates = new ArrayList<Predicate>();
	//
	// for (Predicate child : children) {
	// if (!child.equals(pred)) {
	// predicates.add(child);
	// }
	// }
	// AssociativePredicate left = ff.makeAssociativePredicate(Predicate.LAND,
	// predicates, null);
	//
	// BinaryPredicate innerPred = ff.makeBinaryPredicate(Predicate.LIMP,
	// left, r, null);
	// BoundIdentifier y = ((BoundIdentifier) pred.getLeft());
	// QuantifiedPredicate qPred = ff.makeQuantifiedPredicate(
	// Predicate.FORALL, new BoundIdentDecl[] { y
	// .getDeclaration(identDecls) }, innerPred, null);
	// Predicate instantiate = qPred.instantiate(new Expression[] { pred
	// .getRight() }, ff);
	// return ff.makeQuantifiedPredicate(Predicate.FORALL, remove(identDecls,
	// y.getDeclaration(identDecls)), instantiate, null);
	// }


	private static <T extends Object> boolean contain(T[] array, T element) {
		for (T member : array) {
			if (member.equals(element))
				return true;
		}
		return false;
	}

	@Deprecated
	public static Expression getEmptySetOfType(Expression S) {
		return ff.makeEmptySet(S.getType(), null);
	}

}
