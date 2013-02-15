/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *     Systerel - various cleanup
 *     Systerel - added applyTypeSimplification()
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensions;

import static org.eventb.core.ast.LanguageVersion.V2;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultRewriter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.PredicateVariable;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * This is a collection of static constants and methods that are used often in
 * relation to the sequent prover.
 * <p>
 * Note that they are public but not published and are subject to change. They
 * are to be used at one own's risk. Making referencs to the static functions
 * inside it is highly discouraged since their implementation may change without
 * notice, leaving your code in an uncompilable state.
 * </p>
 * 
 * <p>
 * This does not however prevent you from having your own local copies of the
 * functions that you need, assuming that they do the intended job.
 * </p>
 * 
 * 
 * @author Farhad Mehta, htson
 * 
 * @since 1.0
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class Lib {

	static final LanguageVersion LANGUAGE_VERSION = V2;
	static final FormulaFactory ff = FormulaFactory.getDefault();

	/**
	 * @since 2.0
	 */
	public static final FreeIdentifier[] NO_FREE_IDENT = new FreeIdentifier[0]; 
	
	/**
	 * @since 2.3
	 */
	public static final Predicate[] NO_PREDICATE = new Predicate[0];
	
	public static boolean isTrue(Predicate P) {
		return P.getTag() == Formula.BTRUE;
	}

	public static boolean isFalse(Predicate P) {
		return P.getTag() == Formula.BFALSE;
	}

	public static boolean isEmptySet(Expression e) {
		return e.getTag() == Formula.EMPTYSET;
	}

	public static boolean isFreeIdent(Expression e) {
		return e.getTag() == Formula.FREE_IDENT;
	}

	public static boolean isUnivQuant(Predicate P) {
		return P.getTag() == Formula.FORALL;
	}

	public static boolean isDisj(Predicate P) {
		return P.getTag() == Formula.LOR;
	}

	public static boolean isNeg(Predicate P) {
		return P.getTag() == Formula.NOT;
	}

	public static Predicate negPred(Predicate P) {
		if (!isNeg(P))
			return null;
		return ((UnaryPredicate) P).getChild();
	}

	public static boolean isConj(Predicate P) {
		return P.getTag() == Formula.LAND;
	}

	public static boolean isExQuant(Predicate P) {
		return P.getTag() == Formula.EXISTS;
	}

	public static boolean isImp(Predicate P) {
		return P.getTag() == Formula.LIMP;
	}

	public static Predicate impRight(Predicate P) {
		if (!isImp(P))
			return null;
		return ((BinaryPredicate) P).getRight();
	}

	public static Predicate impLeft(Predicate P) {
		if (!isImp(P))
			return null;
		return ((BinaryPredicate) P).getLeft();
	}

	public static Predicate[] conjuncts(Predicate P) {
		if (!isConj(P))
			return null;
		return ((AssociativePredicate) P).getChildren();
	}

	/**
	 * Returns a set of conjuncts of <code>P</code> when it is a conjunction,
	 * otherwise a singleton set containing <code>P</code>. The returned set is
	 * mutable.
	 * 
	 * @param P
	 *            a predicate
	 * @return a mutable set of conjuncts of the given predicate
	 */
	public static Set<Predicate> breakPossibleConjunct(Predicate P) {
		final List<Predicate> list;
		if (isConj(P))
			list = Arrays.asList(conjuncts(P));
		else
			list = Arrays.asList(P);
		return new LinkedHashSet<Predicate>(list);
	}

	public static Predicate[] disjuncts(Predicate P) {
		if (!isDisj(P))
			return null;
		return ((AssociativePredicate) P).getChildren();
	}

	public static boolean isEq(Predicate P) {
		return P.getTag() == Formula.EQUAL;
	}

	public static Expression eqLeft(Predicate P) {
		if (!isEq(P))
			return null;
		return ((RelationalPredicate) P).getLeft();
	}

	public static Expression eqRight(Predicate P) {
		if (!isEq(P))
			return null;
		return ((RelationalPredicate) P).getRight();
	}

	public static boolean isNotEq(Predicate P) {
		return P.getTag() == Formula.NOTEQUAL;
	}

	public static boolean isInclusion(Predicate P) {
		return P.getTag() == Formula.IN;
	}

	public static boolean isNotInclusion(Predicate P) {
		return P.getTag() == Formula.NOTIN;
	}

	public static Expression getElement(Predicate P) {
		if (!isInclusion(P) && !isNotInclusion(P))
			return null;
		return ((RelationalPredicate) P).getLeft();
	}

	public static Expression getSet(Predicate P) {
		if (!isInclusion(P) && !isNotInclusion(P))
			return null;
		return ((RelationalPredicate) P).getRight();
	}

	public static boolean isSubset(Predicate P) {
		return P.getTag() == Formula.SUBSET;
	}

	public static boolean isNotSubset(Predicate P) {
		return P.getTag() == Formula.NOTSUBSET;
	}

	public static Expression subset(Predicate P) {
		if ((!isSubset(P)) || (!isNotSubset(P)))
			return null;
		return ((RelationalPredicate) P).getLeft();
	}

	public static Expression superset(Predicate P) {
		if ((!isSubset(P)) || (!isNotSubset(P)))
			return null;
		return ((RelationalPredicate) P).getRight();
	}

	public static Expression notEqRight(Predicate P) {
		if (!isNotEq(P))
			return null;
		return ((RelationalPredicate) P).getRight();
	}

	public static Expression notEqLeft(Predicate P) {
		if (!isNotEq(P))
			return null;
		return ((RelationalPredicate) P).getLeft();
	}

	/**
	 * @since 2.0
	 */
	public static void postConstructionCheck(Formula<?> f) {
		assert f.isTypeChecked();
	}

	public static BoundIdentDecl[] getBoundIdents(Predicate P) {
		if (!(P instanceof QuantifiedPredicate))
			return null;
		QuantifiedPredicate qP = (QuantifiedPredicate) P;
		return qP.getBoundIdentDecls();
	}

	// Note returned predicate will have bound variables.
	// Always use in conjunction with makeUnivQuant() or makeExQuant()
	public static Predicate getBoundPredicate(Predicate P) {
		if (!(P instanceof QuantifiedPredicate))
			return null;
		final QuantifiedPredicate qP = (QuantifiedPredicate) P;
		return qP.getPredicate();
	}

	static class EquivalenceRewriter extends FixedRewriter<Predicate> {
		// TODO consider associative matching as in EqualityRewriter
		public EquivalenceRewriter(Predicate from, Predicate to) {
			super(from, to);
		}

	}

	static class EqualityRewriter extends FixedRewriter<Expression> {

		public EqualityRewriter(Expression from, Expression to) {
			super(from, to);
		}

		private static Expression[] associativeRewrite(Expression[] exprs, Expression[] match, Expression into) {
			// i will be index of the first rewritten child
			int i;
			for (i = 0; i < exprs.length; ++i) {
				if (exprs[i].equals(match[0])) {
					break;
				}
			}
			
			if (i + match.length > exprs.length)
				return exprs;
			
			for (int j = 1; j < match.length; ++j) {
				if (!match[j].equals(exprs[i + j])) {
					return exprs;
				}
			}
			
			// Replace "rewriteChildren.length" children from index i by "to"
			Expression[] newChildren = new Expression[exprs.length
			                                          - match.length + 1];
			System.arraycopy(exprs, 0, newChildren, 0, i);
			newChildren[i] = into;
			System.arraycopy(exprs, i + match.length,
					newChildren, i + 1, exprs.length - i
					- match.length);
			return newChildren;
		}
		
		// given expression is considered associative and has same type as from
		private Expression associativeRewrite(Expression expr,
				Expression[] exprChildren, Expression[] fromChildren) {
			final FormulaFactory ff = expr.getFactory();
			final Expression[] newChildren = associativeRewrite(exprChildren,
					fromChildren, to);
			if (newChildren == exprChildren) {
				return expr;
			}
			if (newChildren.length == 1) {
				return newChildren[0];
			}
			final Expression result;
			if (expr instanceof ExtendedExpression) {
				result = ff.makeExtendedExpression(
						((ExtendedExpression) expr).getExtension(),
						newChildren, NO_PREDICATE, null, expr.getType());
			} else {
				result = ff.makeAssociativeExpression(expr.getTag(),
						newChildren, null);
			}
			return result.flatten();
		}
		
		@Override
		public Expression rewrite(AssociativeExpression expression) {
			final int tag = expression.getTag();
			if (from.getTag() == tag) {
				final Expression[] exprChildren = expression.getChildren();
				final Expression[] fromChildren = ((AssociativeExpression) from)
						.getChildren();
				return associativeRewrite(expression, exprChildren,
						fromChildren);
			}
			return super.rewrite(expression);
		}

		@Override
		public Expression rewrite(ExtendedExpression expression) {
			final int tag = expression.getTag();
			final boolean associative = expression.getExtension().getKind()
					.getProperties().isAssociative();
			if (from.getTag() == tag && associative) {
				return associativeRewrite(expression,
						expression.getChildExpressions(),
						((ExtendedExpression) from).getChildExpressions());
			}
			return super.rewrite(expression);
		}

	}

	private static class FixedRewriter<T extends Formula<T>> extends
			DefaultRewriter {
		final T from;

		final T to;

		// TODO add check of compatibility between from and to
		// rather than breaking later when the rewriting is done.
		public FixedRewriter(T from, T to) {
			super(true);
			this.from = from;
			this.to = to;
		}

		@SuppressWarnings("unchecked")
		private <U extends Formula<U>> U doRewrite(U formula) {
			if (formula.equals(from)) {
				return (U) to;
			}
			return formula;
		}

		@Override
		public Expression rewrite(AssociativeExpression expression) {
			return this.<Expression> doRewrite(expression);
		}

		@Override
		public Predicate rewrite(AssociativePredicate predicate) {
			return this.<Predicate> doRewrite(predicate);
		}

		@Override
		public Expression rewrite(AtomicExpression expression) {
			return this.<Expression> doRewrite(expression);
		}

		@Override
		public Expression rewrite(BinaryExpression expression) {
			return this.<Expression> doRewrite(expression);
		}

		@Override
		public Predicate rewrite(BinaryPredicate predicate) {
			return this.<Predicate> doRewrite(predicate);
		}

		@Override
		public Expression rewrite(BoolExpression expression) {
			return this.<Expression> doRewrite(expression);
		}

		@Override
		public Expression rewrite(BoundIdentifier identifier) {
			return this.<Expression> doRewrite(identifier);
		}

		@Override
		public Expression rewrite(ExtendedExpression expression) {
			return this.<Expression> doRewrite(expression);
		}

		@Override
		public Predicate rewrite(ExtendedPredicate predicate) {
			return this.<Predicate> doRewrite(predicate);
		}
		
		@Override
		public Expression rewrite(FreeIdentifier identifier) {
			return this.<Expression> doRewrite(identifier);
		}

		@Override
		public Expression rewrite(IntegerLiteral literal) {
			return this.<Expression> doRewrite(literal);
		}

		@Override
		public Predicate rewrite(LiteralPredicate predicate) {
			return this.<Predicate> doRewrite(predicate);
		}

		@Override
		public Predicate rewrite(MultiplePredicate predicate) {
			return this.<Predicate> doRewrite(predicate);
		}

		@Override
		public Predicate rewrite(PredicateVariable predVar) {
			return this.<Predicate> doRewrite(predVar);
		}

		@Override
		public Expression rewrite(QuantifiedExpression expression) {
			return this.<Expression> doRewrite(expression);
		}

		@Override
		public Predicate rewrite(QuantifiedPredicate predicate) {
			return this.<Predicate> doRewrite(predicate);
		}

		@Override
		public Predicate rewrite(RelationalPredicate predicate) {
			return this.<Predicate> doRewrite(predicate);
		}

		@Override
		public Expression rewrite(SetExtension expression) {
			return this.<Expression> doRewrite(expression);
		}

		@Override
		public Predicate rewrite(SimplePredicate predicate) {
			return this.<Predicate> doRewrite(predicate);
		}

		@Override
		public Expression rewrite(UnaryExpression expression) {
			return this.<Expression> doRewrite(expression);
		}

		@Override
		public Predicate rewrite(UnaryPredicate predicate) {
			return this.<Predicate> doRewrite(predicate);
		}
	}

	/**
	 * Type checks a formula and returns <code>true</code> iff no new type
	 * information was inferred from this type check (i.e. the formula contains
	 * only free identifiers present in the type environment provided).
	 * 
	 * @param formula
	 *            The formula to type check
	 * @param typEnv
	 *            The type environment to use for this check
	 * @return <code>true</code> iff the type check was successful and no new
	 *         type information was inferred from this type check
	 */
	public static boolean typeCheckClosed(Formula<?> formula,
			ITypeEnvironment typEnv) {
		final ITypeCheckResult tcr = formula.typeCheck(typEnv);
		return typeCheckClosed(tcr);
	}

	public static boolean isWellTypedInstantiation(Expression e, Type expT,
			ITypeEnvironment te) {
		final ITypeCheckResult tcr = e.typeCheck(te, expT);
		return typeCheckClosed(tcr);
	}
	
	private static boolean typeCheckClosed(ITypeCheckResult tcr) {
		return tcr.isSuccess() && tcr.getInferredEnvironment().isEmpty();
	}

	public static boolean isFunApp(Formula<?> formula) {
		return formula.getTag() == Expression.FUNIMAGE;
	}

	/**
	 * Check if an expression is a function overriding or not
	 * <p>
	 * 
	 * @param expression
	 *            any expression
	 * @return <code>true</code> if the expression is a function overriding
	 *         (associative expression with tag OVR), return <code>false</code>
	 *         otherwise.
	 */
	public static boolean isOvr(Expression expression) {
		return expression.getTag() == Expression.OVR;
	}

	/**
	 * Check if an expression is a partial function
	 * <p>
	 * 
	 * @param expression
	 *            any expression
	 * @return <code>true</code> iff the expression is a partial function
	 *         (binary expression with tag PFUN)
	 */
	public static boolean isPFun(Expression expression) {
		return expression.getTag() == Expression.PFUN;
	}

	/**
	 * Check if an expression is a functional binary expression
	 * <p>
	 * 
	 * @param expression
	 *            any expression
	 * @return <code>true</code> iff the expression is a functional expression
	 *         (binary expression with tag PFUN or TFUN or PINJ or TINJ or PSUR
	 *         or TSUR or TBIJ)
	 */
	public static boolean isFun(Expression expression) {
		return expression.getTag() == Expression.PFUN
				|| expression.getTag() == Expression.TFUN
				|| expression.getTag() == Expression.PINJ
				|| expression.getTag() == Expression.TINJ
				|| expression.getTag() == Expression.PSUR
				|| expression.getTag() == Expression.TSUR
				|| expression.getTag() == Expression.TBIJ;
	}

	/**
	 * Check if an expression is a relational binary expression
	 * <p>
	 * 
	 * @param expression
	 *            any expression
	 * @return <code>true</code> iff the expression is a functional expression
	 *         (binary expression with tag REL or TREL or SREL or TREL)
	 * @since 2.0
	 */
	public static boolean isRel(Expression expression) {
		return expression.getTag() == Expression.REL
				|| expression.getTag() == Expression.TREL
				|| expression.getTag() == Expression.SREL
				|| expression.getTag() == Expression.STREL;
	}

	/**
	 * Returns the right hand side of a binary expression.
	 * 
	 * @param expr
	 *            the given binary expression.
	 * @return the right hand side of the given binary expression, or
	 *         <code>null</code> in case the given expression is not a binary
	 *         expression.
	 */
	public static Expression getRight(Expression expr) {
		if (expr instanceof BinaryExpression) {
			return ((BinaryExpression) expr).getRight();
		}
		return null;
	}

	/**
	 * Returns the left hand side of a binary expression.
	 * 
	 * @param expr
	 *            the given binary expression.
	 * @return the left hand side of the given binary expression, or
	 *         <code>null</code> in case the given expression is not a binary
	 *         expression.
	 */
	public static Expression getLeft(Expression expr) {
		if (expr instanceof BinaryExpression) {
			return ((BinaryExpression) expr).getLeft();
		}
		return null;
	}

	public static boolean isSetExtension(Expression expression) {
		return expression.getTag() == Formula.SETEXT;
	}

	/**
	 * Test if the formula is a set intersection "S ∩ ... ∩ T".
	 * <p>
	 * 
	 * @param expression
	 *            any expression
	 * @return <code>true</code> if the given expression is a set intersection.
	 *         Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isInter(Expression expression) {
		return expression.getTag() == Expression.BINTER;
	}

	/**
	 * Test if the formula is a set union "S ∪ ... ∪ T".
	 * <p>
	 * 
	 * @param expression
	 *            any expression
	 * @return <code>true</code> if the given expression is a set union. Return
	 *         <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isUnion(Expression expression) {
		return expression.getTag() == Expression.BUNION;
	}

	public static boolean isConv(Expression expression) {
		return expression.getTag() == Expression.CONVERSE;
	}

	public static boolean isRelImg(Formula<?> formula) {
		return formula.getTag() == Expression.RELIMAGE;
	}

	public static boolean isSetMinus(Formula<?> formula) {
		return formula.getTag() == Expression.SETMINUS;
	}

	/**
	 * Test if the formula is a mapping "a ↦ b".
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a mapping. Return
	 *         <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isMapping(Formula<?> formula) {
		return formula.getTag() == Expression.MAPSTO;
	}

	/**
	 * Test if the formula is a singleton set "{E}".
	 * <p>
	 * 
	 * @param expression
	 *            any expression
	 * @return <code>true</code> if the given expression is a singleton set.
	 *         Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isSingletonSet(Expression expression) {
		if (isSetExtension(expression)) {
			return ((SetExtension) expression).getMembers().length == 1;
		}
		return false;
	}

	/**
	 * Test if the formula is a direct product "p ⊗ q".
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a direct product.
	 *         Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isDirectProduct(Formula<?> formula) {
		return formula.getTag() == Expression.DPROD;
	}

	/**
	 * Test if the formula is a parallel product "p ∥ q".
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a parallel product.
	 *         Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isParallelProduct(Formula<?> formula) {
		return formula.getTag() == Expression.PPROD;
	}

	/**
	 * Test if the formula is a finiteness "finite(S)".
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a finiteness. Return
	 *         <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isFinite(Formula<?> formula) {
		return formula.getTag() == Predicate.KFINITE;
	}

	/**
	 * Test if the formula is a relation "r" (i.e. formula of type ℙ(S × T) for
	 * some S and T
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a relation. Return
	 *         <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isRelation(Formula<?> formula) {
		if (formula instanceof Expression) {
			final Expression expr = (Expression) formula;
			final Type type = expr.getType();
			return type != null && type.getSource() != null;
		}
		return false;
	}

	/**
	 * Test if the formula is a set of all relation "S ↔ T" for some S and T
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a set of all relations.
	 *         Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isSetOfRelation(Formula<?> formula) {
		return formula.getTag() == Expression.REL;
	}

	/**
	 * Test if the formula is the range of a relation "ran(r)"
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is the range of a
	 *         relation. Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isRan(Formula<?> formula) {
		return formula.getTag() == Expression.KRAN;
	}

	/**
	 * Test if the formula is the domain of a relation "dom(r)"
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is the domain of a
	 *         relation. Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isDom(Formula<?> formula) {
		return formula.getTag() == Expression.KDOM;
	}

	/**
	 * Test if the formula is a set of all partial functions "S ⇸ T" for some S
	 * and T
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a set of all partial
	 *         functions. Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isSetOfPartialFunction(Formula<?> formula) {
		return formula.getTag() == Expression.PFUN;
	}

	/**
	 * Test if the formula is a bound identifier
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is bound identifier.
	 *         Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isBoundIdentifier(Formula<?> formula) {
		return formula.getTag() == Formula.BOUND_IDENT;
	}

	/**
	 * Test if the formula is a bound identifier
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is bound identifier.
	 *         Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isSetOfIntegers(Formula<?> formula) {
		if (formula instanceof Expression) {
			final Expression expr = (Expression) formula;
			final Type type = expr.getType();
			return type != null && type.getBaseType() instanceof IntegerType;
		}
		return false;
	}

	/**
	 * Test if the formula is a cardinality expression (card(S))
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a cardinality
	 *         expression. Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isCardinality(Formula<?> formula) {
		return formula.getTag() == Expression.KCARD;
	}

	/**
	 * Return the range type of a relation input. Return <code>null</code> if
	 * the input is not a relation, i.e. of type POW(S x T) for some sets S and
	 * T.
	 * 
	 * @param r
	 *            an relation expression.
	 * @return the type of the range of the input relation or <code>null</code>.
	 */
	public static Type getRangeType(Expression r) {
		return r.getType().getTarget();
	}

	/**
	 * Return the domain type of a relation input. Return <code>null</code> if
	 * the input is not a relation, i.e. of type POW(S x T) for some sets S and
	 * T.
	 * 
	 * @param r
	 *            an relation expression.
	 * @return the type of the range of the input relation or <code>null</code>.
	 */
	public static Type getDomainType(Expression r) {
		return r.getType().getSource();
	}

	public static boolean isPartition(Predicate p) {
		return p.getTag() == Formula.KPARTITION;
	}

	/**
	 * Applies the equality rewriter to the given predicate.
	 * 
	 * @param predicate
	 *            a predicate to rewrite
	 * @param from
	 *            the expression that will be replaced
	 * @param to           
	 *            the substitute 
	 * @return the rewritten predicate
	 * @since 3.0
	 */ 
	public static Predicate equalityRewrite(Predicate pred, Expression from,
			Expression to) {
		return pred.rewrite(new EqualityRewriter(from, to));
	}

	/**
	 * Removes all positions in the given list that are not WD strict in the
	 * given predicate.
	 * 
	 * @param positions
	 *            a list of positions in the given predicate
	 * @param predicate
	 *            some predicate
	 * @since 2.0
	 */
	public static void removeWDUnstrictPositions(List<IPosition> positions,
			Predicate predicate) {
		final Iterator<IPosition> iter = positions.iterator();
		while (iter.hasNext()) {
			if (!predicate.isWDStrict(iter.next())) {
				iter.remove();
			}
		}
	}
	
	/**
	 * Tells if the formula at the given position in the given predicate is WD
	 * strict.
	 * 
	 * @param positions
	 *            a position in the given predicate
	 * @param predicate
	 *            some predicate
	 * @since 2.0
	 */
	public static boolean isWDStrictPosition(Predicate pred, IPosition pos) {
		return pred.isWDStrict(pos);
	}

	/**
	 * Re-write the predicate <code>pred</code> by replacing every occurrence of
	 * <code>from</code> by <code>to</code>
	 * 
	 * @param pred
	 *            the re-written predicate
	 * @param from
	 *            the replaced predicate
	 * @param to
	 *            the replacing predicate
	 * @return the predicate <code>pred</code> re-written
	 * @since 3.0
	 */
	public static Predicate equivalenceRewrite(Predicate pred, Predicate from,
			Predicate to) {
		return pred.rewrite(new EquivalenceRewriter(from, to));
	}

}