package org.eventb.core.seqprover.eventbExtensions;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Assignment;
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
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
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
 * @author Farhad Mehta
 * 
 */
public final class Lib {

	public final static FormulaFactory ff = FormulaFactory.getDefault();

	public final static Predicate True = ff.makeLiteralPredicate(Formula.BTRUE,
			null);

	public final static Predicate False = ff.makeLiteralPredicate(
			Formula.BFALSE, null);

	public final static Expression TRUE = ff.makeAtomicExpression(
			Expression.TRUE, null);

	public final static Expression FALSE = ff.makeAtomicExpression(
			Expression.FALSE, null);

	public static boolean isTrue(Predicate P) {
		if (P instanceof LiteralPredicate && P.getTag() == Formula.BTRUE)
			return true;
		return false;
	}

	public static boolean isFalse(Predicate P) {
		if (P instanceof LiteralPredicate && P.getTag() == Formula.BFALSE)
			return true;
		return false;
	}

	public static boolean isEmptySet(Expression e) {
		if (e instanceof AtomicExpression && e.getTag() == Formula.EMPTYSET)
			return true;
		return false;
	}
	
	public static boolean isFreeIdent(Expression e) {
		if (e instanceof FreeIdentifier && e.getTag() == Formula.FREE_IDENT)
			return true;
		return false;
	}

	public static boolean isUnivQuant(Predicate P) {
		if (P instanceof QuantifiedPredicate && P.getTag() == Formula.FORALL)
			return true;
		return false;
	}

	public static boolean isDisj(Predicate P) {
		if (P instanceof AssociativePredicate && P.getTag() == Formula.LOR)
			return true;
		return false;
	}

	public static boolean isNeg(Predicate P) {
		if (P instanceof UnaryPredicate && P.getTag() == Formula.NOT)
			return true;
		return false;
	}

	public static Predicate negPred(Predicate P) {
		if (!isNeg(P))
			return null;
		return ((UnaryPredicate) P).getChild();
	}

	public static boolean isConj(Predicate P) {
		if (P instanceof AssociativePredicate && P.getTag() == Formula.LAND)
			return true;
		return false;
	}

	public static boolean isExQuant(Predicate P) {
		if (P instanceof QuantifiedPredicate && P.getTag() == Formula.EXISTS)
			return true;
		return false;
	}

	public static boolean isImp(Predicate P) {
		if (P instanceof BinaryPredicate && P.getTag() == Formula.LIMP)
			return true;
		return false;
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

	private static Set<Predicate> singeleton(Predicate P) {
		HashSet<Predicate> result = new HashSet<Predicate>(1);
		result.add(P);
		return result;
	}

	public static Set<Predicate> breakPossibleConjunct(Predicate P) {
		if (!isConj(P))
			return singeleton(P);
		return new LinkedHashSet<Predicate>(Arrays.asList(((AssociativePredicate) P)
				.getChildren()));
	}
	
	public static boolean removeTrue(Set<Predicate> preds){
		return preds.remove(True);
	}

	public static Predicate[] disjuncts(Predicate P) {
		if (!isDisj(P))
			return null;
		return ((AssociativePredicate) P).getChildren();
	}

	public static boolean isEq(Predicate P) {
		if (P instanceof RelationalPredicate && P.getTag() == Formula.EQUAL)
			return true;
		return false;
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
		if (P instanceof RelationalPredicate && P.getTag() == Formula.NOTEQUAL)
			return true;
		return false;
	}

	public static boolean isInclusion(Predicate P) {
		if (P instanceof RelationalPredicate && P.getTag() == Formula.IN)
			return true;
		return false;
	}

	public static boolean isNotInclusion(Predicate P) {
		if (P instanceof RelationalPredicate && P.getTag() == Formula.NOTIN)
			return true;
		return false;
	}

	public static Expression getElement(Predicate P) {
		if (!(P instanceof RelationalPredicate && (P.getTag() == Formula.NOTIN || P
				.getTag() == Formula.IN)))
			return null;
		return ((RelationalPredicate) P).getLeft();
	}

	public static Expression getSet(Predicate P) {
		if (!(P instanceof RelationalPredicate && (P.getTag() == Formula.NOTIN || P
				.getTag() == Formula.IN)))
			return null;
		return ((RelationalPredicate) P).getRight();
	}

	public static boolean isSubset(Predicate P) {
		if (P instanceof RelationalPredicate && P.getTag() == Formula.SUBSET)
			return true;
		return false;
	}

	public static boolean isNotSubset(Predicate P) {
		if (P instanceof RelationalPredicate && P.getTag() == Formula.NOTSUBSET)
			return true;
		return false;
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

	private static void postConstructionCheck(Formula f) {
		assert f.isTypeChecked();
	}

	public static Predicate makeNeg(Predicate P) {
		// If the predicate is already negated, remove the negation.
		if (isNeg(P))
			return negPred(P);

		Predicate result = ff.makeUnaryPredicate(Formula.NOT, P, null);
		postConstructionCheck(result);
		return result;
	}

	public static Predicate[] makeNeg(Predicate[] Ps) {
		Predicate[] result = new Predicate[Ps.length];
		for (int i = 0; i < Ps.length; i++)
			result[i] = makeNeg(Ps[i]);
		return result;
	}

	public static Predicate makeConj(Predicate... conjuncts) {
		if (conjuncts.length == 0)
			return True;
		if (conjuncts.length == 1)
			return conjuncts[0];
		Predicate result = ff.makeAssociativePredicate(Formula.LAND, conjuncts,
				null);
		postConstructionCheck(result);
		return result;
	}

	public static Predicate makeDisj(Predicate... disjuncts) {
		if (disjuncts.length == 0)
			return False;
		if (disjuncts.length == 1)
			return disjuncts[0];
		Predicate result = ff.makeAssociativePredicate(Formula.LOR, disjuncts,
				null);
		postConstructionCheck(result);
		return result;
	}

	public static Predicate makeConj(Collection<Predicate> conjuncts) {
		Predicate[] conjunctsArray = new Predicate[conjuncts.size()];
		conjuncts.toArray(conjunctsArray);
		return makeConj(conjunctsArray);
	}

	public static Predicate makeImp(Predicate left, Predicate right) {
		Predicate result = ff.makeBinaryPredicate(Formula.LIMP, left, right,
				null);
		postConstructionCheck(result);
		return result;
	}

	public static Predicate makeEq(Expression left, Expression right) {
		Predicate result = ff.makeRelationalPredicate(Formula.EQUAL, left,
				right, null);
		postConstructionCheck(result);
		return result;
	}

	public static Predicate makeNotEq(Expression left, Expression right) {
		Predicate result = ff.makeRelationalPredicate(Formula.NOTEQUAL, left,
				right, null);
		postConstructionCheck(result);
		return result;
	}

	public static Predicate makeInclusion(Expression element, Expression set) {
		Predicate result = ff.makeRelationalPredicate(Formula.IN, element, set,
				null);
		postConstructionCheck(result);
		return result;
	}

	public static Predicate makeNotInclusion(Expression element, Expression set) {
		Predicate result = ff.makeRelationalPredicate(Formula.NOTIN, element,
				set, null);
		postConstructionCheck(result);
		return result;
	}

	public static Predicate instantiateBoundIdents(Predicate P,
			Expression[] instantiations) {
		if (!(P instanceof QuantifiedPredicate))
			return null;
		QuantifiedPredicate qP = (QuantifiedPredicate) P;
		Predicate result = qP.instantiate(instantiations, ff);
		postConstructionCheck(result);
		return result;
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
		QuantifiedPredicate qP = (QuantifiedPredicate) P;
		return qP.getPredicate();
	}

	public static Predicate makeUnivQuant(BoundIdentDecl[] boundIdents,
			Predicate boundPred) {
		Predicate result = ff.makeQuantifiedPredicate(Formula.FORALL,
				boundIdents, boundPred, null);
		postConstructionCheck(result);
		return result;

	}

	public static Predicate makeExQuant(BoundIdentDecl[] boundIdents,
			Predicate boundPred) {
		Predicate result = ff.makeQuantifiedPredicate(Formula.EXISTS,
				boundIdents, boundPred, null);
		postConstructionCheck(result);
		return result;

	}

	public static Predicate WD(Formula f) {
		Predicate result = f.getWDPredicate(ff);
		postConstructionCheck(result);
		return result;
	}

	public static Predicate WD(Collection<? extends Formula> formulae) {
		Set<Predicate> WD = new HashSet<Predicate>(formulae.size());
		for (Formula formula : formulae) {
			WD.add(WD(formula));
		}
		return makeConj(WD);
	}

	public static Predicate WD(Formula[] formulae) {
		Set<Predicate> WD = new HashSet<Predicate>(formulae.length);
		for (Formula formula : formulae) {
			if (formula != null)
				WD.add(WD(formula));
		}
		return makeConj(WD);
	}

	public static Expression parseExpression(String str) {
		IParseResult plr = ff.parseExpression(str);
		if (plr.isSuccess())
			return plr.getParsedExpression();
		else
			return null;
	}

	public static Type parseType(String str) {
		IParseResult plr = ff.parseType(str);
		if (plr.isSuccess())
			return plr.getParsedType();
		else
			return null;
	}

	public static Expression typeToExpression(Type type) {
		Expression result = type.toExpression(ff);
		postConstructionCheck(result);
		return result;
	}

	public static Assignment parseAssignment(String str) {
		IParseResult plr = ff.parseAssignment(str);
		if (plr.isSuccess())
			return plr.getParsedAssignment();
		else
			return null;
	}

	public static Predicate parsePredicate(String str) {
		IParseResult plr = ff.parsePredicate(str);
		if (plr.isSuccess())
			return plr.getParsedPredicate();
		// else System.out.println(str+" : "+plr.getProblems());
		return null;
	}

	@Deprecated
	public static Predicate rewrite(Predicate P, FreeIdentifier from,
			Expression to) {
		if (!Arrays.asList(P.getFreeIdentifiers()).contains(from))
			return P;
		Map<FreeIdentifier, Expression> subst = new HashMap<FreeIdentifier, Expression>();
		subst.put(from, to);
		return P.substituteFreeIdents(subst, ff);
	}

	public static Predicate rewrite(Predicate P, Expression from, Expression to) {
		IFormulaRewriter rewriter = new EqualityRewriter(from, to);
		return P.rewrite(rewriter);
	}

	private static class EqualityRewriter extends FixedRewriter {

		public EqualityRewriter(Formula from, Formula to) {
			super(from, to);
		}

		@Override
		public Expression rewrite(AssociativeExpression expression) {
			int tag = expression.getTag();
			if (from instanceof AssociativeExpression && from.getTag() == tag) {
				AssociativeExpression aExp = (AssociativeExpression) from;
				Expression[] children = expression.getChildren();
				Expression[] rewriteChildren = aExp.getChildren();

				// i will be index of the first rewritten child
				int i;
				for (i = 0; i < children.length; ++i) {
					if (children[i].equals(rewriteChildren[0])) {
						break;
					}
				}

				if (i + rewriteChildren.length > children.length)
					return expression;

				for (int j = 1; j < rewriteChildren.length; ++j) {
					if (!rewriteChildren[j].equals(children[i + j])) {
						return expression;
					}
				}

				// Replace "rewriteChildren.length" children from index i by
				// "to"
				Expression[] newChildren = new Expression[children.length
						- rewriteChildren.length + 1];
				System.arraycopy(children, 0, newChildren, 0, i);
				newChildren[i] = (Expression) to;
				System.arraycopy(children, i + rewriteChildren.length,
						newChildren, i + 1, children.length - i
								- rewriteChildren.length);
				
				if (newChildren.length == 1) {
					return newChildren[0];
				}
				AssociativeExpression result = ff.makeAssociativeExpression(tag, newChildren, null);
				return result.flatten(ff);
			}
			return super.rewrite(expression);
		}

	}

	private static class FixedRewriter extends DefaultRewriter {
		final Formula from;

		final Formula to;

		// TODO add check of compatibility between from and to
		// rather than breaking later when the rewriting is done.
		public FixedRewriter(Formula from, Formula to) {
			super(true, Lib.ff);
			this.from = from;
			this.to = to;
		}

		private Formula doRewrite(Formula formula) {
			if (formula.equals(from)) {
				return to;
			}
			return formula;
		}

		@Override
		public Expression rewrite(AssociativeExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Predicate rewrite(AssociativePredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Expression rewrite(AtomicExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Expression rewrite(BinaryExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Predicate rewrite(BinaryPredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Expression rewrite(BoolExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Expression rewrite(BoundIdentifier identifier) {
			return (Expression) doRewrite(identifier);
		}

		@Override
		public Expression rewrite(FreeIdentifier identifier) {
			return (Expression) doRewrite(identifier);
		}

		@Override
		public Expression rewrite(IntegerLiteral literal) {
			return (Expression) doRewrite(literal);
		}

		@Override
		public Predicate rewrite(LiteralPredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Expression rewrite(QuantifiedExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Predicate rewrite(QuantifiedPredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Predicate rewrite(RelationalPredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Expression rewrite(SetExtension expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Predicate rewrite(SimplePredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}

		@Override
		public Expression rewrite(UnaryExpression expression) {
			return (Expression) doRewrite(expression);
		}

		@Override
		public Predicate rewrite(UnaryPredicate predicate) {
			return (Predicate) doRewrite(predicate);
		}
	}

	/**
	 * Type checks a formula and returns <code>true</code> iff no new type
	 * information was infreed from this type check (i.e. the formula contains
	 * only free identifiers present in the type environment provided).
	 * 
	 * @param formula
	 *            The formula to type check
	 * @param typEnv
	 *            The type environemnt to use for this check
	 * @return <code>true</code> iff the type check was successful and no new
	 *         type information was infered from this type check
	 */
	public static boolean typeCheckClosed(Formula formula,
			ITypeEnvironment typEnv) {
		ITypeCheckResult tcr = formula.typeCheck(typEnv);
		// new free variables introduced?
		if (tcr.isSuccess()) {
			return tcr.getInferredEnvironment().isEmpty();
		}
		return false;
	}

	public static boolean isWellTypedInstantiation(Expression e, Type expT,
			ITypeEnvironment te) {
		ITypeCheckResult tcr = e.typeCheck(te, expT);
		// new free variables introduced?
		if (tcr.isSuccess()) {
			return tcr.getInferredEnvironment().isEmpty();
		}
		return false;
	}

	/**
	 * Type checks a formula assuming all typing information can be infered from
	 * the formula itself.
	 * 
	 * @param formula
	 *            The formula to type check
	 * @return
	 * 
	 * @deprecated use {@link #typeCheckClosed(Formula, ITypeEnvironment)} with an
	 * empty type environment, or the AST methods directly instead.
	 */
	@Deprecated
	public static ITypeEnvironment typeCheck(Formula formula) {
		ITypeCheckResult tcr = formula.typeCheck(ff.makeTypeEnvironment());
		if (!tcr.isSuccess())
			return null;
		return tcr.getInferredEnvironment();
	}

	public static ITypeEnvironment makeTypeEnvironment() {
		return ff.makeTypeEnvironment();
	}

	public static boolean isFunApp(Formula formula) {
		if (formula instanceof BinaryExpression
				&& formula.getTag() == Expression.FUNIMAGE) {
			return true;
		}
		return false;
	}

	public static boolean isOrv(Expression expression) {
		if (expression instanceof AssociativeExpression
				&& expression.getTag() == Expression.OVR) {
			return true;
		}
		return false;
	}

	public static boolean isSetExtension(Expression expression) {
		if (expression instanceof SetExtension) {
			return true;
		}
		return false;
	}

	// /**
	// * Typechecks a formula assuming an initial type environment
	// *
	// * @param f
	// * The formula to type check
	// * @param initialTypeEnvironment
	// * The initial type environment to use while type checking the formula.
	// * This type environment is not modified
	// * @return The type environment enriched with the extra type information
	// inferred
	// * from the input formula
	// */
	// private static ITypeEnvironment typeCheck(Formula f,ITypeEnvironment
	// initialTypeEnvironment) {
	// ITypeCheckResult tcr = f.typeCheck(initialTypeEnvironment);
	// if (! tcr.isSuccess()) return null;
	// if (tcr.getInferredEnvironment().isEmpty()) return
	// tcr.getInitialTypeEnvironment();
	// ITypeEnvironment result = initialTypeEnvironment.clone();
	// result.addAll(tcr.getInferredEnvironment());
	// return result;
	// }

	// public static ITypeEnvironment typeCheck(Formula... formulae) {
	// ITypeEnvironment typeEnvironment = ff.makeTypeEnvironment();
	// for (Formula f:formulae)
	// {
	// typeEnvironment = typeCheck(f,typeEnvironment);
	// if (typeEnvironment == null) return null;
	// }
	// return typeEnvironment;
	// }

	// public Predicate disprovedCase(Map<FreeIdentifier,Expression>
	// counterExample){
	// List<Predicate> predicates = new
	// ArrayList<Predicate>(counterExample.size());
	// for (Map.Entry<FreeIdentifier, Expression> assignment :
	// counterExample.entrySet())
	// {
	// predicates.add(makeEq(assignment.getKey(), assignment.getValue()));
	// }
	// return makeConj(predicates);
	// }

}