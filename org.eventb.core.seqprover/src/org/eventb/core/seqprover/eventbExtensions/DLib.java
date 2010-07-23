/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
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
 *     Systerel - separated from Lib
 ******************************************************************************/
package org.eventb.core.seqprover.eventbExtensions;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.ITypeCheckResult;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.Type;

/**
 * This is a collection of constants and methods that are used often in relation
 * to the sequent prover.
 * <p>
 * Note that they are public but not published and are subject to change. They
 * are to be used at one own's risk. Making references to the functions inside
 * it is highly discouraged since their implementation may change without
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
 */
public class DLib {
	
	private final FormulaFactory ff;

	private DLib(FormulaFactory ff) {
		this.ff = ff;
	}
	
	public static DLib mDLib(FormulaFactory ff) {
		return new DLib(ff);
	}
	
	public final Predicate True() {
		return ff.makeLiteralPredicate(Formula.BTRUE, null);
	}
	
	public final Predicate False() {
		return ff.makeLiteralPredicate(Formula.BFALSE, null);
	}
	
	public final Expression TRUE() {
		return ff.makeAtomicExpression(Expression.TRUE, null);
	}
	
	public final Expression FALSE() {
		return ff.makeAtomicExpression(Expression.FALSE, null);
	}

	public boolean removeTrue(Set<Predicate> preds){
		return preds.remove(True());
	}

	public Predicate makeNeg(Predicate P) {
		// If the predicate is already negated, remove the negation.
		if (Lib.isNeg(P))
			return Lib.negPred(P);
	
		Predicate result = ff.makeUnaryPredicate(Formula.NOT, P, null);
		Lib.postConstructionCheck(result);
		return result;
	}

	public Predicate[] makeNeg(Predicate[] Ps) {
		Predicate[] result = new Predicate[Ps.length];
		for (int i = 0; i < Ps.length; i++)
			result[i] = makeNeg(Ps[i]);
		return result;
	}

	public Predicate makeDisj(Predicate... disjuncts) {
		if (disjuncts.length == 0)
			return False();
		if (disjuncts.length == 1)
			return disjuncts[0];
		Predicate result = ff.makeAssociativePredicate(Formula.LOR, disjuncts,
				null);
		Lib.postConstructionCheck(result);
		return result;
	}

	public Predicate makeConj(Predicate... conjuncts) {
		if (conjuncts.length == 0)
			return True();
		if (conjuncts.length == 1)
			return conjuncts[0];
		Predicate result = ff.makeAssociativePredicate(Formula.LAND, conjuncts,
				null);
		Lib.postConstructionCheck(result);
		return result;
	}

	public Predicate makeImp(Predicate left, Predicate right) {
		Predicate result = ff.makeBinaryPredicate(Formula.LIMP, left, right,
				null);
		Lib.postConstructionCheck(result);
		return result;
	}

	public Predicate makeConj(Collection<Predicate> conjuncts) {
		final Predicate[] conjunctsArray = new Predicate[conjuncts.size()];
		conjuncts.toArray(conjunctsArray);
		return makeConj(conjunctsArray);
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
	public Predicate makeImpl(Collection<Predicate> left, Predicate right){
		if (left.isEmpty()){
			return right;
		}
		else{
			return makeImp(makeConj(left), right);
		}	
	}

	public Predicate makeEq(Expression left, Expression right) {
		Predicate result = ff.makeRelationalPredicate(Formula.EQUAL, left,
				right, null);
		Lib.postConstructionCheck(result);
		return result;
	}

	public Predicate makeNotEq(Expression left, Expression right) {
		Predicate result = ff.makeRelationalPredicate(Formula.NOTEQUAL, left,
				right, null);
		Lib.postConstructionCheck(result);
		return result;
	}

	public Predicate makeInclusion(Expression element, Expression set) {
		Predicate result = ff.makeRelationalPredicate(Formula.IN, element, set,
				null);
		Lib.postConstructionCheck(result);
		return result;
	}

	public Predicate makeNotInclusion(Expression element, Expression set) {
		Predicate result = ff.makeRelationalPredicate(Formula.NOTIN, element,
				set, null);
		Lib.postConstructionCheck(result);
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
	public Predicate makeUnivQuant(FreeIdentifier[] freeIdents, Predicate pred) {
		if (freeIdents == null || freeIdents.length == 0)
			return pred;
		// Bind the given free identifiers
		final List<FreeIdentifier> idents = Arrays.asList(freeIdents);
		final Predicate boundPred = pred.bindTheseIdents(idents, ff);
		// Generate bound identifier declarations.
		BoundIdentDecl[] boundIdentDecls = new BoundIdentDecl[freeIdents.length];
		for (int i = 0; i < freeIdents.length; i++) {
			final String name = freeIdents[i].getName();
			final Type type = freeIdents[i].getType();
			boundIdentDecls[i] = ff.makeBoundIdentDecl(name, null, type);
		}
		return makeUnivQuant(boundIdentDecls, boundPred);
	}

	public Predicate makeUnivQuant(BoundIdentDecl[] boundIdents,
			Predicate boundPred) {
		final Predicate result = ff.makeQuantifiedPredicate(Formula.FORALL,
				boundIdents, boundPred, null);
		Lib.postConstructionCheck(result);
		return result;
	}

	public Predicate makeExQuant(BoundIdentDecl[] boundIdents,
			Predicate boundPred) {
		final Predicate result = ff.makeQuantifiedPredicate(Formula.EXISTS,
				boundIdents, boundPred, null);
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * Constructs an existentially quantified predicate form a given predicate
	 * by binding the free identifiers provided.
	 * 
	 * <p>
	 * If no free identifiers are provided (<code>null</code> or an array of
	 * length 0) then the identical predicate is returned.
	 * </p>
	 * 
	 * @param freeIdents
	 *            the free identifiers to bind
	 * @param pred
	 *            the predicate to quantify over
	 * @return the quantified predicate
	 */
	public Predicate makeExQuant(FreeIdentifier[] freeIdents, Predicate pred) {
		if (freeIdents == null || freeIdents.length == 0)
			return pred;
		// Bind the given free identifiers
		Predicate boundPred = pred.bindTheseIdents(Arrays.asList(freeIdents),
				ff);
		// Generate bound identifier declarations.
		BoundIdentDecl[] boundIdentDecls = new BoundIdentDecl[freeIdents.length];
		for (int i = 0; i < freeIdents.length; i++) {
			final String name = freeIdents[i].getName();
			final Type type = freeIdents[i].getType();
			boundIdentDecls[i] = ff.makeBoundIdentDecl(name, null, type);
		}
		return makeExQuant(boundIdentDecls, boundPred);
	}

	public Predicate WD(Formula<?> f) {
		final Predicate result = f.getWDPredicate(ff);
		Lib.postConstructionCheck(result);
		return result;
	}

	public Predicate WD(Formula<?>[] formulae) {
		final Set<Predicate> WD = new HashSet<Predicate>(formulae.length);
		for (Formula<?> formula : formulae) {
			if (formula != null)
				WD.add(WD(formula));
		}
		return makeConj(WD);
	}

	public Predicate WD(Collection<Formula<?>> formulae, FormulaFactory ff) {
		final Set<Predicate> WD = new HashSet<Predicate>(formulae.size());
		for (Formula<?> formula : formulae) {
			WD.add(WD(formula));
		}
		return makeConj(WD);
	}

	public Predicate rewrite(Predicate P, Expression from, Expression to) {
		final IFormulaRewriter rewriter = new Lib.EqualityRewriter(from, to, ff);
		return P.rewrite(rewriter);
	}

	@Deprecated
	public Predicate rewrite(Predicate P, FreeIdentifier from,
			Expression to) {
		if (!Arrays.asList(P.getFreeIdentifiers()).contains(from))
			return P;
		Map<FreeIdentifier, Expression> subst = new HashMap<FreeIdentifier, Expression>();
		subst.put(from, to);
		return P.substituteFreeIdents(subst, ff);
	}

	public Predicate parsePredicate(String str) {
		IParseResult plr = ff.parsePredicate(str, Lib.LANGUAGE_VERSION, null);
		if (plr.hasProblem())
			return null;
		return plr.getParsedPredicate();
	}

	public Assignment parseAssignment(String str) {
		IParseResult plr = ff.parseAssignment(str, Lib.LANGUAGE_VERSION, null);
		if (plr.hasProblem())
			return null;
		return plr.getParsedAssignment();
	}

	public Expression typeToExpression(Type type) {
		Expression result = type.toExpression(ff);
		Lib.postConstructionCheck(result);
		return result;
	}

	public Type parseType(String str) {
		IParseResult plr = ff.parseType(str, Lib.LANGUAGE_VERSION);
		if (plr.hasProblem())
			return null;
		return plr.getParsedType();
	}

	public Expression parseExpression(String str) {
		final IParseResult plr = ff.parseExpression(str, Lib.LANGUAGE_VERSION, null);
		if (plr.hasProblem())
			return null;
		return plr.getParsedExpression();
	}

	/**
	 * Contruct an integer literal ({@link IntegerLiteral} from an integer.
	 * <p>
	 * 
	 * @param n
	 *            an integer to construct the integer literal
	 * @return the literal with the value the same as the integer input. 
	 * @author htson
	 */
	public IntegerLiteral makeIntegerLiteral(int n) {
		return ff.makeIntegerLiteral(BigInteger.valueOf(n), null);
	}

	public ITypeEnvironment makeTypeEnvironment() {
		return ff.makeTypeEnvironment();
	}

	/**
	 * Type checks a formula assuming all typing information can be infered from
	 * the formula itself.
	 * 
	 * @param formula
	 *            The formula to type check
	 * @return
	 * 
	 * @deprecated use {@link Lib#typeCheckClosed(Formula, ITypeEnvironment)} with an
	 * empty type environment, or the AST methods directly instead.
	 */
	@Deprecated
	public ITypeEnvironment typeCheck(Formula<?> formula) {
		ITypeCheckResult tcr = formula.typeCheck(ff.makeTypeEnvironment());
		if (!tcr.isSuccess())
			return null;
		return tcr.getInferredEnvironment();
	}

	public Predicate instantiateBoundIdents(Predicate P,
			Expression[] instantiations) {
		if (!(P instanceof QuantifiedPredicate))
			return null;
		QuantifiedPredicate qP = (QuantifiedPredicate) P;
		Predicate result = qP.instantiate(instantiations, ff);
		Lib.postConstructionCheck(result);
		return result;
	}
	
	
	

}
