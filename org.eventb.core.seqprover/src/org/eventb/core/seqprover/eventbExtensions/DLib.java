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
 *     Systerel - separated from Lib
 *******************************************************************************/
package org.eventb.core.seqprover.eventbExtensions;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IFormulaRewriter;
import org.eventb.core.ast.IParseResult;
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
 * @since 2.0
 */
public class DLib {
	
	/**
	 * @since 3.0
	 */
	public static final Predicate True(FormulaFactory ff) {
		return ff.makeLiteralPredicate(Formula.BTRUE, null);
	}
	
	/**
	 * @since 3.0
	 */
	public static final Predicate False(FormulaFactory ff) {
		return ff.makeLiteralPredicate(Formula.BFALSE, null);
	}
	
	/**
	 * @since 3.0
	 */
	public static final Expression TRUE(FormulaFactory ff) {
		return ff.makeAtomicExpression(Expression.TRUE, null);
	}
	
	/**
	 * @since 3.0
	 */
	public static final Expression FALSE(FormulaFactory ff) {
		return ff.makeAtomicExpression(Expression.FALSE, null);
	}

	/**
	 * @since 3.0
	 */
	public static boolean removeTrue(FormulaFactory ff, Set<Predicate> preds){
		return preds.remove(True(ff));
	}

	/**
	 * @since 3.0
	 */
	public static Predicate makeNeg(Predicate P) {
		// If the predicate is already negated, remove the negation.
		if (Lib.isNeg(P))
			return Lib.negPred(P);
	
		Predicate result = P.getFactory().makeUnaryPredicate(Formula.NOT, P,
				null);
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * @since 3.0
	 */
	public static Predicate[] makeNeg(Predicate[] Ps) {
		Predicate[] result = new Predicate[Ps.length];
		for (int i = 0; i < Ps.length; i++)
			result[i] = makeNeg(Ps[i]);
		return result;
	}

	/**
	 * @since 3.0
	 */
	public static Predicate makeDisj(FormulaFactory ff, Predicate... disjuncts) {
		if (disjuncts.length == 0)
			return False(ff);
		if (disjuncts.length == 1)
			return disjuncts[0];
		Predicate result = ff.makeAssociativePredicate(Formula.LOR, disjuncts,
				null);
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * @since 3.0
	 */
	public static Predicate makeConj(FormulaFactory ff, Predicate... conjuncts) {
		if (conjuncts.length == 0)
			return True(ff);
		if (conjuncts.length == 1)
			return conjuncts[0];
		Predicate result = ff.makeAssociativePredicate(Formula.LAND, conjuncts,
				null);
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * Returns an implication with given left and right hand sides.
	 * <p> 
	 * Left and right hand sides must have the same formula factory.
	 * </p>
	 * @param left left predicate of implication
	 * @param right right predicate of implication
	 * @return the implication predicate
	 */
	public static Predicate makeImp(Predicate left, Predicate right) {
		Predicate result = left.getFactory().makeBinaryPredicate(Formula.LIMP, left, right,
				null);
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * @since 3.0
	 */
	public static Predicate makeConj(FormulaFactory ff, Collection<Predicate> conjuncts) {
		final Predicate[] conjunctsArray = new Predicate[conjuncts.size()];
		conjuncts.toArray(conjunctsArray);
		return makeConj(ff, conjunctsArray);
	}

	/**
	 * Makes an implication from a collection of predicates and a predicate.
	 * 
	 * <p>
	 * The left hand side of the implication is the conjunction of the
	 * predicates in the given collection. In case the collection is empty, the
	 * given rignt hand side predicate is simply returned.
	 * </p>
	 * <p>
	 * If the left hand side predicates collection is not empty, the left hand
	 * side predicates must have the same formula factory than the right hand
	 * side predicate.
	 * </p>
	 * 
	 * @param left
	 *            the collection of predicates to use for the left hannd side of
	 *            the inplications
	 * @param right
	 *            the predicate to use for the right hand side of the
	 *            implication
	 * @return the resulting implication
	 */
	public static Predicate makeImpl(Collection<Predicate> left, Predicate right){
		if (left.isEmpty()){
			return right;
		}
		else{
			return makeImp(makeConj(right.getFactory(), left), right);
		}	
	}

	/**
	 * Returns an equality with given left and right hand sides.
	 * <p> 
	 * Left and right hand sides must have the same formula factory.
	 * </p>
	 * @param left left expression of equality
	 * @param right right expression of equality
	 * @return the equality predicate
	 */
	public static Predicate makeEq(Expression left, Expression right) {
		Predicate result = left.getFactory().makeRelationalPredicate(Formula.EQUAL, left,
				right, null);
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * Returns an inequality with given left and right hand sides.
	 * <p> 
	 * Left and right hand sides must have the same formula factory.
	 * </p>
	 * @param left left expression of inequality
	 * @param right right expression of inequality
	 * @return the inequality predicate
	 */
	public static Predicate makeNotEq(Expression left, Expression right) {
		Predicate result = left.getFactory().makeRelationalPredicate(
				Formula.NOTEQUAL, left, right, null);
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * Returns an inclusion predicate with given element and set.
	 * <p> 
	 * The element and set must have the same formula factory.
	 * </p>
	 * @param element the element of the inclusion
	 * @param set the set of the inclusion
	 * @return the inclusion predicate
	 */
	public static Predicate makeInclusion(Expression element, Expression set) {
		Predicate result = element.getFactory().makeRelationalPredicate(Formula.IN, element, set,
				null);
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * Returns an non-inclusion predicate with given element and set.
	 * <p> 
	 * The element and set must have the same formula factory.
	 * </p>
	 * @param element the element of the non-inclusion
	 * @param set the set of the non-inclusion
	 * @return the non-inclusion predicate
	 * @since 3.0
	 */
	public static Predicate makeNotInclusion(FormulaFactory ff, Expression element, Expression set) {
		Predicate result = ff.makeRelationalPredicate(Formula.NOTIN, element,
				set, null);
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * Constructs a universally quantified predicate form a given predicate by
	 * binding the free identifiers provided.
	 * 
	 * <p>
	 * If no free identifiers are provided (<code>null</code> or an array of
	 * length 0) then the identical predicate is returned.
	 * </p>
	 * <p>
	 * If free identifiers are provided they must have the same formula factory
	 * than the given predicate.
	 * </p>
	 * 
	 * @param freeIdents
	 *            the free identifiers to bind
	 * @param pred
	 *            the predicate to quantify over
	 * @return the quantified predicate
	 */
	public static Predicate makeUnivQuant(FreeIdentifier[] freeIdents, Predicate pred) {
		if (freeIdents == null || freeIdents.length == 0)
			return pred;
		final FormulaFactory ff = pred.getFactory();
		// Bind the given free identifiers
		final List<FreeIdentifier> idents = Arrays.asList(freeIdents);
		final Predicate boundPred = pred.bindTheseIdents(idents);
		// Generate bound identifier declarations.
		BoundIdentDecl[] boundIdentDecls = new BoundIdentDecl[freeIdents.length];
		for (int i = 0; i < freeIdents.length; i++) {
			final String name = freeIdents[i].getName();
			final Type type = freeIdents[i].getType();
			boundIdentDecls[i] = ff.makeBoundIdentDecl(name, null, type);
		}
		return makeUnivQuant(boundIdentDecls, boundPred);
	}

	/**
	 * @since 3.0
	 */
	public static Predicate makeUnivQuant(BoundIdentDecl[] boundIdents,
			Predicate boundPred) {
		final Predicate result = boundPred.getFactory().makeQuantifiedPredicate(Formula.FORALL,
				boundIdents, boundPred, null);
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * @since 3.0
	 */
	public static Predicate makeExQuant(BoundIdentDecl[] boundIdents,
			Predicate boundPred) {
		final Predicate result = boundPred.getFactory()
				.makeQuantifiedPredicate(Formula.EXISTS, boundIdents,
						boundPred, null);
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
	 * <p>
	 * If free identifiers are provided they must have the same formula factory
	 * than the given predicate.
	 * </p>
	 * @param freeIdents
	 *            the free identifiers to bind
	 * @param pred
	 *            the predicate to quantify over
	 * @return the quantified predicate
	 * @since 3.0
	 */
	public static Predicate makeExQuant(FreeIdentifier[] freeIdents, Predicate pred) {
		if (freeIdents == null || freeIdents.length == 0)
			return pred;
		final FormulaFactory ff = pred.getFactory();
		// Bind the given free identifiers
		Predicate boundPred = pred.bindTheseIdents(Arrays.asList(freeIdents));
		// Generate bound identifier declarations.
		BoundIdentDecl[] boundIdentDecls = new BoundIdentDecl[freeIdents.length];
		for (int i = 0; i < freeIdents.length; i++) {
			final String name = freeIdents[i].getName();
			final Type type = freeIdents[i].getType();
			boundIdentDecls[i] = ff.makeBoundIdentDecl(name, null, type);
		}
		return makeExQuant(boundIdentDecls, boundPred);
	}

	/**
	 * @since 3.0
	 */
	public static Predicate WD(Formula<?> f) {
		final Predicate result = f.getWDPredicate(f.getFactory());
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * @since 3.0
	 */
	public static Predicate WD(FormulaFactory ff, Formula<?>[] formulae) {
		final Set<Predicate> WD = new HashSet<Predicate>(formulae.length);
		for (Formula<?> formula : formulae) {
			if (formula != null)
				WD.add(WD(formula));
		}
		return makeConj(ff, WD);
	}

	/**
	 * @since 3.0
	 */
	public static Predicate WD(Collection<Formula<?>> formulae, FormulaFactory ff) {
		final Set<Predicate> WD = new HashSet<Predicate>(formulae.size());
		for (Formula<?> formula : formulae) {
			WD.add(WD(formula));
		}
		return makeConj(ff, WD);
	}

	/**
	 * @since 3.0
	 */
	public static Predicate rewrite(Predicate P, Expression from, Expression to) {
		final IFormulaRewriter rewriter = new Lib.EqualityRewriter(from, to);
		return P.rewrite(rewriter);
	}

	/**
	 * @since 3.0
	 */
	public static Predicate parsePredicate(FormulaFactory ff, String str) {
		IParseResult plr = ff.parsePredicate(str, Lib.LANGUAGE_VERSION, null);
		if (plr.hasProblem())
			return null;
		return plr.getParsedPredicate();
	}

	/**
	 * @since 3.0
	 */
	public static Assignment parseAssignment(FormulaFactory ff, String str) {
		IParseResult plr = ff.parseAssignment(str, Lib.LANGUAGE_VERSION, null);
		if (plr.hasProblem())
			return null;
		return plr.getParsedAssignment();
	}

	/**
	 * @since 3.0
	 */
	public static Expression typeToExpression(FormulaFactory ff, Type type) {
		Expression result = type.toExpression();
		Lib.postConstructionCheck(result);
		return result;
	}

	/**
	 * @since 3.0
	 */
	public static Type parseType(FormulaFactory ff, String str) {
		IParseResult plr = ff.parseType(str, Lib.LANGUAGE_VERSION);
		if (plr.hasProblem())
			return null;
		return plr.getParsedType();
	}

	/**
	 * @since 3.0
	 */
	public static Expression parseExpression(FormulaFactory ff, String str) {
		final IParseResult plr = ff.parseExpression(str, Lib.LANGUAGE_VERSION, null);
		if (plr.hasProblem())
			return null;
		return plr.getParsedExpression();
	}

	/**
	 * Construct an integer literal ({@link IntegerLiteral} from an integer.
	 * <p>
	 * 
	 * @param n
	 *            an integer to construct the integer literal
	 * @return the literal with the value the same as the integer input. 
	 * @author htson
	 * @since 3.0
	 */
	public static IntegerLiteral makeIntegerLiteral(FormulaFactory ff, int n) {
		return ff.makeIntegerLiteral(BigInteger.valueOf(n), null);
	}

	/**
	 * @since 3.0
	 */
	public static Predicate instantiateBoundIdents(Predicate P,
			Expression[] instantiations) {
		if (!(P instanceof QuantifiedPredicate))
			return null;
		QuantifiedPredicate qP = (QuantifiedPredicate) P;
		Predicate result = qP.instantiate(instantiations, qP.getFactory());
		Lib.postConstructionCheck(result);
		return result;
	}

}
