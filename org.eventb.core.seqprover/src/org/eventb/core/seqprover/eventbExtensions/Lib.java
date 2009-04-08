package org.eventb.core.seqprover.eventbExtensions;

import java.math.BigInteger;
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
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
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

	private static void postConstructionCheck(Formula<?> f) {
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
	
	/**
	 * Makes an implication from a collection of predicates and a predicate.
	 * 
	 * <p>
	 * The left hand side of the implication is the conjunction of the predicates in the given collection. 
	 * In case the collection is empty, the given rignt hand side predicate is simply returned. 
	 * </p>
	 * 
	 * @param left
	 * 		the collection of predicates to use for the left hannd side of the
	 * 		inplications
	 * @param right
	 * 		the predicate to use for the right hand side of the implication
	 * @return
	 * 		the resulting implication
	 */
	public static Predicate makeImpl(Collection<Predicate> left, Predicate right){
		if (left.isEmpty()){
			return right;
		}
		else{
			return Lib.makeImp(makeConj(left), right);
		}	
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
	
	
	/**
	 * Constructs a universally quantified predicate form a given predicate
	 * by binding the free identifiers provided.
	 * 
	 * <p>
	 * If no free identifiers are provided (<code>null</code> or an array of length 0) then
	 * the identical predicate is returned.
	 * </p>
	 * 
	 * @param freeIdents
	 * 			the free identifiers to bind
	 * @param pred
	 * 			the predicate to quantify over
	 * @return
	 * 			the quantified predicate
	 */
	public static Predicate makeUnivQuant(FreeIdentifier[] freeIdents,
			Predicate pred) {
		
		if (freeIdents == null || freeIdents.length == 0)
			return pred;
		
		// Bind the given free identifiers 
		Predicate boundPred = pred.bindTheseIdents(Arrays.asList(freeIdents), ff);
		// Generate bound identifier declarations.
		BoundIdentDecl[] boundIdentDecls = new BoundIdentDecl[freeIdents.length];
		for (int i = 0; i < freeIdents.length; i++) {
			FreeIdentifier freeIdent = freeIdents[i];
			boundIdentDecls[i] = ff.makeBoundIdentDecl(freeIdent.getName(), null, freeIdent.getType());
		}
	
		return makeUnivQuant(boundIdentDecls, boundPred);
	}	

	public static Predicate makeExQuant(BoundIdentDecl[] boundIdents,
			Predicate boundPred) {
		Predicate result = ff.makeQuantifiedPredicate(Formula.EXISTS,
				boundIdents, boundPred, null);
		postConstructionCheck(result);
		return result;

	}
	
	/**
	 * Constructs an existentially quantified predicate form a given predicate
	 * by binding the free identifiers provided.
	 * 
	 * <p>
	 * If no free identifiers are provided (<code>null</code> or an array of length 0) then
	 * the identical predicate is returned.
	 * </p>
	 * 
	 * @param freeIdents
	 * 			the free identifiers to bind
	 * @param pred
	 * 			the predicate to quantify over
	 * @return
	 * 			the quantified predicate
	 */
	public static Predicate makeExQuant(FreeIdentifier[] freeIdents,
			Predicate pred) {
		
		if (freeIdents == null || freeIdents.length == 0)
			return pred;
		
		// Bind the given free identifiers 
		Predicate boundPred = pred.bindTheseIdents(Arrays.asList(freeIdents), ff);
		// Generate bound identifier declarations.
		BoundIdentDecl[] boundIdentDecls = new BoundIdentDecl[freeIdents.length];
		for (int i = 0; i < freeIdents.length; i++) {
			FreeIdentifier freeIdent = freeIdents[i];
			boundIdentDecls[i] = ff.makeBoundIdentDecl(freeIdent.getName(), null, freeIdent.getType());
		}
	
		return makeExQuant(boundIdentDecls, boundPred);
	}

	public static Predicate WD(Formula<?> f) {
		Predicate result = f.getWDPredicate(ff);
		postConstructionCheck(result);
		return result;
	}

	public static Predicate WD(Collection<Formula<?>> formulae) {
		Set<Predicate> WD = new HashSet<Predicate>(formulae.size());
		for (Formula<?> formula : formulae) {
			WD.add(WD(formula));
		}
		return makeConj(WD);
	}

	public static Predicate WD(Formula<?>[] formulae) {
		Set<Predicate> WD = new HashSet<Predicate>(formulae.length);
		for (Formula<?> formula : formulae) {
			if (formula != null)
				WD.add(WD(formula));
		}
		return makeConj(WD);
	}

	public static Expression parseExpression(String str) {
		IParseResult plr = ff.parseExpression(str);
		if (plr.hasProblem())
			return null;
		return plr.getParsedExpression();
	}

	public static Type parseType(String str) {
		IParseResult plr = ff.parseType(str);
		if (plr.hasProblem())
			return null;
		return plr.getParsedType();
	}

	public static Expression typeToExpression(Type type) {
		Expression result = type.toExpression(ff);
		postConstructionCheck(result);
		return result;
	}

	public static Assignment parseAssignment(String str) {
		IParseResult plr = ff.parseAssignment(str);
		if (plr.hasProblem())
			return null;
		return plr.getParsedAssignment();
	}

	public static Predicate parsePredicate(String str) {
		IParseResult plr = ff.parsePredicate(str);
		if (plr.hasProblem())
			return null;
		return plr.getParsedPredicate();
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

	private static class EqualityRewriter extends FixedRewriter<Expression> {

		public EqualityRewriter(Expression from, Expression to) {
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
				newChildren[i] = to;
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

	private static class FixedRewriter<T extends Formula<T>> extends DefaultRewriter {
		final T from;

		final T to;

		// TODO add check of compatibility between from and to
		// rather than breaking later when the rewriting is done.
		public FixedRewriter(T from, T to) {
			super(true, Lib.ff);
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
			return this.<Expression>doRewrite(expression);
		}

		@Override
		public Predicate rewrite(AssociativePredicate predicate) {
			return this.<Predicate>doRewrite(predicate);
		}

		@Override
		public Expression rewrite(AtomicExpression expression) {
			return this.<Expression>doRewrite(expression);
		}

		@Override
		public Expression rewrite(BinaryExpression expression) {
			return this.<Expression>doRewrite(expression);
		}

		@Override
		public Predicate rewrite(BinaryPredicate predicate) {
			return this.<Predicate>doRewrite(predicate);
		}

		@Override
		public Expression rewrite(BoolExpression expression) {
			return this.<Expression>doRewrite(expression);
		}

		@Override
		public Expression rewrite(BoundIdentifier identifier) {
			return this.<Expression>doRewrite(identifier);
		}

		@Override
		public Expression rewrite(FreeIdentifier identifier) {
			return this.<Expression>doRewrite(identifier);
		}

		@Override
		public Expression rewrite(IntegerLiteral literal) {
			return this.<Expression>doRewrite(literal);
		}

		@Override
		public Predicate rewrite(LiteralPredicate predicate) {
			return this.<Predicate>doRewrite(predicate);
		}

		@Override
		public Expression rewrite(QuantifiedExpression expression) {
			return this.<Expression>doRewrite(expression);
		}

		@Override
		public Predicate rewrite(QuantifiedPredicate predicate) {
			return this.<Predicate>doRewrite(predicate);
		}

		@Override
		public Predicate rewrite(RelationalPredicate predicate) {
			return this.<Predicate>doRewrite(predicate);
		}

		@Override
		public Expression rewrite(SetExtension expression) {
			return this.<Expression>doRewrite(expression);
		}

		@Override
		public Predicate rewrite(SimplePredicate predicate) {
			return this.<Predicate>doRewrite(predicate);
		}

		@Override
		public Expression rewrite(UnaryExpression expression) {
			return this.<Expression>doRewrite(expression);
		}

		@Override
		public Predicate rewrite(UnaryPredicate predicate) {
			return this.<Predicate>doRewrite(predicate);
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
	public static boolean typeCheckClosed(Formula<?> formula,
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
	public static ITypeEnvironment typeCheck(Formula<?> formula) {
		ITypeCheckResult tcr = formula.typeCheck(ff.makeTypeEnvironment());
		if (!tcr.isSuccess())
			return null;
		return tcr.getInferredEnvironment();
	}

	public static ITypeEnvironment makeTypeEnvironment() {
		return ff.makeTypeEnvironment();
	}

	public static boolean isFunApp(Formula<?> formula) {
		if (formula instanceof BinaryExpression
				&& formula.getTag() == Expression.FUNIMAGE) {
			return true;
		}
		return false;
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
		if (expression instanceof AssociativeExpression
				&& expression.getTag() == Expression.OVR) {
			return true;
		}
		return false;
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
		if (expression instanceof BinaryExpression
				&& expression.getTag() == Expression.PFUN) {
			return true;
		}
		return false;
	}
	
	/**
	 * Check if an expression is a functional binary expression
	 * <p>
	 * 
	 * @param expression
	 *            any expression
	 * @return <code>true</code> iff the expression is a functional expression
	 *         (binary expression with tag PFUN or TFUN or PINJ or TINJ or PSUR or TSUR or TBIJ)
	 */
	public static boolean isFun(Expression expression) {
		if (expression instanceof BinaryExpression
				&& (
						expression.getTag() == Expression.PFUN ||
						expression.getTag() == Expression.TFUN ||
						expression.getTag() == Expression.PINJ ||
						expression.getTag() == Expression.TINJ ||
						expression.getTag() == Expression.PSUR ||
						expression.getTag() == Expression.TSUR ||
						expression.getTag() == Expression.TBIJ )) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * Returns the right hand side of a binary expression.
	 * 
	 * @param expr
	 * 			the given binary expression.
	 * @return
	 * 			the right hand side of the given binary expression, or <code>null</code> in case
	 * 			the given expression is not a binary expression.
	 */
	public static Expression getRight(Expression expr){
		if (expr instanceof BinaryExpression) {
			return ((BinaryExpression) expr).getRight();
		}
		return null;
	}
	
	/**
	 * Returns the left hand side of a binary expression.
	 * 
	 * @param expr
	 * 			the given binary expression.
	 * @return
	 * 			the left hand side of the given binary expression, or <code>null</code> in case
	 * 			the given expression is not a binary expression.
	 */
	public static Expression getLeft(Expression expr){
		if (expr instanceof BinaryExpression) {
			return ((BinaryExpression) expr).getLeft();
		}
		return null;
	}

	public static boolean isSetExtension(Expression expression) {
		if (expression instanceof SetExtension) {
			return true;
		}
		return false;
	}


	/**
	 * Test if the formula is a set intersection "S ∩ ... ∩ T".
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a set intersection.
	 *         Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isInter(Expression expression) {
		return expression instanceof AssociativeExpression
				&& expression.getTag() == Expression.BINTER;
	}


	/**
	 * Test if the formula is a set union "S ∪ ... ∪ T".
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a set union. Return
	 *         <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isUnion(Expression expression) {
		return expression instanceof AssociativeExpression
				&& expression.getTag() == Expression.BUNION;
	}

	public static boolean isConv(Expression expression) {
		return expression instanceof UnaryExpression
				&& expression.getTag() == Expression.CONVERSE;
	}

	public static boolean isRelImg(Formula<?> formula) {
		if (formula instanceof BinaryExpression
				&& formula.getTag() == Expression.RELIMAGE) {
			return true;
		}
		return false;
	}

	public static boolean isSetMinus(Formula<?> formula) {
		return formula instanceof BinaryExpression
				&& formula.getTag() == Expression.SETMINUS;
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
		return formula instanceof BinaryExpression
				&& formula.getTag() == Expression.MAPSTO;
	}


	/**
	 * Test if the formula is a singleton set "{E}".
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a singleton set. Return
	 *         <code>false</code> otherwise.
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
		return formula instanceof BinaryExpression
				&& formula.getTag() == Expression.DPROD;
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
		return formula instanceof BinaryExpression
				&& formula.getTag() == Expression.PPROD;
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

	/**
	 * Contruct an integer literal ({@link IntegerLiteral} from an integer.
	 * <p>
	 * 
	 * @param n
	 *            an integer to construct the integer literal
	 * @return the literal with the value the same as the integer input. 
	 * @author htson
	 */
	public static IntegerLiteral makeIntegerLiteral(int n) {
		BigInteger literal = new BigInteger("" + n);
		return ff.makeIntegerLiteral(literal, null);
	}


	/**
	 * Test if the formula is a finiteness "finite(S)".
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a finiteness.
	 *         Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isFinite(Formula<?> formula) {
		return (formula instanceof SimplePredicate && formula.getTag() == Predicate.KFINITE);
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
			Expression r = (Expression) formula;
			Type type = r.getType();
			if (type instanceof PowerSetType) {
				Type baseType = type.getBaseType();
				if (baseType instanceof ProductType) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Test if the formula is a set of all relation "S ↔ T" for some S and T
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a set of all
	 *         relations. Return <code>false</code> otherwise.
	 * @author htson
	 */
	public static boolean isSetOfRelation(Formula<?> formula) {
		return (formula instanceof BinaryExpression && formula.getTag() == Expression.REL);
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
		return (formula instanceof UnaryExpression && formula.getTag() == Expression.KRAN);
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
		return (formula instanceof UnaryExpression && formula.getTag() == Expression.KDOM);
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
		return (formula instanceof BinaryExpression && formula.getTag() == Expression.PFUN);
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
		return (formula instanceof BoundIdentifier);
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
		if (!(formula instanceof Expression)) {
			return false;
		}
		Expression expression = (Expression) formula;
		Type type = expression.getType();
		if (type == null) {
			return false;
		}
		return type.getBaseType() instanceof IntegerType;
	}


	/**
	 * Test if the formula is a cardinality expression (card(S))
	 * <p>
	 * 
	 * @param formula
	 *            any formula
	 * @return <code>true</code> if the input formula is a cardinality expression.
	 *         Return <code>false</code> otherwise.
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
}