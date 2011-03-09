/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *     Systerel - SIMP_IN_COMPSET_*, SIMP_SPECIAL_OVERL, SIMP_FUNIMAGE_LAMBDA
 *     Systerel - Added tracing mechanism
 *     Systerel - SIMP_EQUAL_CONSTR*, SIMP_DESTR_CONSTR
 *     Systerel - move to tom-2.8
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BINTER;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.BUNION;
import static org.eventb.core.ast.Formula.CONVERSE;
import static org.eventb.core.ast.Formula.CPROD;
import static org.eventb.core.ast.Formula.CSET;
import static org.eventb.core.ast.Formula.DOMRES;
import static org.eventb.core.ast.Formula.DOMSUB;
import static org.eventb.core.ast.Formula.EQUAL;
import static org.eventb.core.ast.Formula.EXISTS;
import static org.eventb.core.ast.Formula.EXPN;
import static org.eventb.core.ast.Formula.FALSE;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.GE;
import static org.eventb.core.ast.Formula.GT;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.KDOM;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KMAX;
import static org.eventb.core.ast.Formula.KMIN;
import static org.eventb.core.ast.Formula.KRAN;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LIMP;
import static org.eventb.core.ast.Formula.LOR;
import static org.eventb.core.ast.Formula.LT;
import static org.eventb.core.ast.Formula.MAPSTO;
import static org.eventb.core.ast.Formula.MINUS;
import static org.eventb.core.ast.Formula.NOT;
import static org.eventb.core.ast.Formula.PLUS;
import static org.eventb.core.ast.Formula.POW;
import static org.eventb.core.ast.Formula.RANRES;
import static org.eventb.core.ast.Formula.RELIMAGE;
import static org.eventb.core.ast.Formula.SETMINUS;
import static org.eventb.core.ast.Formula.SUBSET;
import static org.eventb.core.ast.Formula.SUBSETEQ;
import static org.eventb.core.ast.Formula.TRUE;
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyComp;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyInter;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyLand;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyLor;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyMult;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyOvr;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyPlus;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyUnion;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.FunctionalCheck.functionalCheck;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.PartialLambdaPatternCheck.partialLambdaPatternCheck;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.SetExtensionSimplifier.simplifyMax;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.SetExtensionSimplifier.simplifyMin;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

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
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedExpression.Form;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.datatype.IDatatype;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointProcessorRewriting;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;

/**
 * Basic automated rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class AutoRewriterImpl extends DefaultRewriter {

	public static boolean DEBUG;

	private final DivisionUtils du;
	
	private final DLib dLib;
	
	private final AutoRewrites.Level level;

	// Cached enabled levels
	private final boolean level1;
	private final boolean level2;
	
	protected final IntegerLiteral number0 = ff.makeIntegerLiteral(ZERO, null);
	
	protected final IntegerLiteral number1 = ff.makeIntegerLiteral(ONE, null);

	private final IntegerLiteral number2 = ff.makeIntegerLiteral(new BigInteger("2"), null);

	@ProverRule( { "SIMP_FORALL", "SIMP_EXISTS", "SIMP_LIT_MINUS" })
	/*
	 * Rules SIMP_FORALL, SIMP_EXISTS, and SIMP_LIT_MINUS are implemented by the
	 * fact that this rewriter is auto-flattening (first parameter is true in
	 * the call to the abstract constructor). Unfortunately, it is not possible
	 * to trace auto-flattening.
	 */
	public AutoRewriterImpl(FormulaFactory ff, Level level) {
		super(true, ff);
		dLib = DLib.mDLib(ff);
		du = new DivisionUtils(dLib);
		this.level = level;
		this.level1 = level.from(Level.L1);
		this.level2 = level.from(Level.L2);
	}

	public AutoRewrites.Level getLevel() {
		return level;
	}

	protected UnaryPredicate makeUnaryPredicate(int tag, Predicate child) {
		return ff.makeUnaryPredicate(tag, child, null);
	}

	protected RelationalPredicate makeRelationalPredicate(int tag, Expression left,
			Expression right) {
		return ff.makeRelationalPredicate(tag, left, right, null);
	}
	
	protected AssociativePredicate makeAssociativePredicate(int tag, Predicate... children) {
		return ff.makeAssociativePredicate(tag, children, null);
	}
	
	protected QuantifiedPredicate makeQuantifiedPredicate(int tag, BoundIdentDecl[] boundIdentifiers, Predicate child) {
		return ff.makeQuantifiedPredicate(tag, boundIdentifiers, child, null);
	}

	protected SetExtension makeSetExtension(Collection<Expression> expressions) {
		return ff.makeSetExtension(expressions, null);
	}
	
	protected SetExtension makeSetExtension(Expression... expressions) {
		return ff.makeSetExtension(expressions, null);
	}

	protected UnaryExpression makeUnaryExpression(int tag, Expression child) {
		return ff.makeUnaryExpression(tag, child, null);
	}

	protected BinaryExpression makeBinaryExpression(int tag, Expression left, Expression right) {
		return ff.makeBinaryExpression(tag, left, right, null);
	}

	protected BinaryPredicate makeBinaryPredicate(int tag, Predicate left, Predicate right) {
		return ff.makeBinaryPredicate(tag, left, right, null);
	}
	
	protected AtomicExpression makeEmptySet(Type type) {
		return ff.makeEmptySet(type, null);
	}
		
	protected AssociativeExpression makeAssociativeExpression(int tag, Expression... children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	protected AssociativeExpression makeAssociativeExpression(int tag, Collection<Expression> children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	protected SimplePredicate makeSimplePredicate(int tag, Expression expression) {
		return ff.makeSimplePredicate(tag, expression, null);
	}
	
	protected QuantifiedExpression makeQuantifiedExpression(int tag, BoundIdentDecl[] boundIdentifiers, Predicate pred, Expression expr, Form form) {
		return ff.makeQuantifiedExpression(tag, boundIdentifiers, pred, expr, null, form);
	}
	
	protected BoundIdentifier makeBoundIdentifier(int index, Type type) {
		return ff.makeBoundIdentifier(index, null, type);
	}

	protected <T> boolean contains(T[] array, T key) {
		for (T element : array) {
			if (element.equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean notLocallyBound(Formula<?> form, int nbBound) {
		for (BoundIdentifier ident : form.getBoundIdentifiers()) {
			if (ident.getBoundIndex() < nbBound) {
				return false;
			}
		}
		return true;
	}

	// Removes exactly one occurrence of the given child from the given
	// associative expression, if possible. Otherwise, returns the given
	// associative expression unchanged.
	protected Expression removeChild(AssociativeExpression parent,
				Expression toRemove) {
		final int tag = parent.getTag();
		final Expression[] children = parent.getChildren();
		final int length = children.length;
		if (length == 2) {
			if (toRemove.equals(children[0])) {
				return children[1];
			}
			if (toRemove.equals(children[1])) {
				return children[0];
			}
			return parent;
		}
		final int index = Arrays.asList(children).indexOf(toRemove);
		if (index == -1) {
			return parent;
		}
		final Expression[] newChildren = new Expression[length - 1];
		System.arraycopy(children, 0, newChildren, 0, index);
		System.arraycopy(children, index+1, newChildren, index, length - index - 1);
		return ff.makeAssociativeExpression(tag, newChildren, null);
	}
	
	private Expression simplifyExtremumOfUnion(Expression[] children, int tag) {
		final int length = children.length;
		final Expression[] newChildren = new Expression[length];
		boolean changed = false;
		for (int i = 0; i < length; i++) {
			final Expression child = children[i];
			final Expression newChild = extractSingletonWithTag(child, tag);
			if (newChild != null) {
				newChildren[i] = newChild;
				changed = true;
			} else {
				newChildren[i] = child;
			}
		}
		if (!changed) {
			return null;
		}
		return makeUnaryExpression(tag,	makeAssociativeExpression(BUNION, newChildren));
	}
	
	private Expression extractSingletonWithTag(Expression expression, int tag) {
	    %match (Expression expression) {
	    	SetExtension(eList(op@(Min | Max)(T))) -> {
				if (`op.getTag() == tag) {
					return `T;
				}
			}
	    }
	    return null;
	}
	
	private Expression simplifySetextOfMapsto(Expression[] children, Expression image) {
		for (Expression child : children) {
			if (child.getTag() != MAPSTO) {
				return null;
			}
			final BinaryExpression maplet = ((BinaryExpression) child);
			if (!maplet.getRight().equals(image)) {
				return null;
			}
		}
		return image;
	}
	
	private Expression convertSetextOfMapsto(Expression[] children) {
		final Expression[] newChildren = new Expression[children.length];
		for (int i = 0 ; i < children.length ; i++) {
			final Expression child = children[i];
			if (child.getTag() == MAPSTO) {
				final BinaryExpression bExp = (BinaryExpression) child;
				newChildren[i] = makeBinaryExpression(MAPSTO, bExp.getRight(), bExp.getLeft());
			} else {
				return null;
			}
		}
		return makeSetExtension(newChildren);
	}

	private static <T extends Formula<T>> void trace(T from, T to, String rule,
			String... otherRules) {
		if (!DEBUG) {
			return;
		}
		if (from == to) {
			return;
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("AutoRewriter: ");
		sb.append(from);
		sb.append("  \u219d  ");
		sb.append(to);

		sb.append("   (");
		sb.append(rule);
		for (final String r : otherRules) {
			sb.append(" | ");
			sb.append(r);
		}
		sb.append(")");

		System.out.println(sb);
	}
	
	protected RelationalPredicate makeIsEmpty(Expression set) {
		return makeRelationalPredicate(EQUAL, set,
			makeEmptySet(set.getType()));
	}
	
	protected SimplePredicate makeFinite(Expression set) {
		return makeSimplePredicate(KFINITE, set);
	}
	
	protected UnaryExpression makeCard(Expression set) {
		return makeUnaryExpression(KCARD, set);
	}
	
	protected Predicate makeNotEqual(Expression left, Expression right) {
		return makeUnaryPredicate(NOT,
			makeRelationalPredicate(EQUAL, left, right));
	}

	%include {FormulaV2.tom}
	
	@ProverRule( { "SIMP_SPECIAL_FINITE", "SIMP_FINITE_SETENUM",
			"SIMP_FINITE_BUNION", "SIMP_FINITE_POW", "DERIV_FINITE_CPROD",
			"SIMP_FINITE_CONVERSE", "SIMP_FINITE_UPTO",
			"SIMP_FINITE_NATURAL", "SIMP_FINITE_NATURAL1",
			"SIMP_FINITE_INTEGER", "SIMP_FINITE_ID", "SIMP_FINITE_LAMBDA",
			"SIMP_FINITE_ID_DOMRES", "SIMP_FINITE_PRJ1", "SIMP_FINITE_PRJ2",
			"SIMP_FINITE_PRJ1_DOMRES", "SIMP_FINITE_PRJ2_DOMRES",
			"SIMP_FINITE_BOOL" })
	@Override
	public Predicate rewrite(SimplePredicate predicate) {
		final Predicate result;
	    %match (Predicate predicate) {

			/**
             * SIMP_SPECIAL_FINITE
	    	 * Finite: finite(∅) = ⊤
	    	 */
			Finite(EmptySet()) -> {
				result = dLib.True();
				trace(predicate, result, "SIMP_SPECIAL_FINITE");
				return result;
			}
			
			/**
			 * SIMP_FINITE_NATURAL
			 *    finite(ℕ) == ⊥
			 */
			Finite(Natural()) -> {
				if (level2) {
					result = dLib.False();
					trace(predicate, result, "SIMP_FINITE_NATURAL");
					return result;
				}
			}
			
			/**
			 * SIMP_FINITE_NATURAL1
			 *    finite(ℕ1) == ⊥
			 */
			Finite(Natural1()) -> {
				if (level2) {
					result = dLib.False();
					trace(predicate, result, "SIMP_FINITE_NATURAL1");
					return result;
				}
			}
			
			/**
			 * SIMP_FINITE_INTEGER
			 *    finite(ℤ) == ⊥
			 */
			Finite(INTEGER()) -> {
				if (level2) {
					result = dLib.False();
					trace(predicate, result, "SIMP_FINITE_INTEGER");
					return result;
				}
			}
			
			/**
			 * SIMP_FINITE_BOOL
			 *    finite(BOOL) == ⊤
			 */
			Finite(BOOL()) -> {
				if (level2) {
					result = dLib.True();
					trace(predicate, result, "SIMP_FINITE_BOOL");
					return result;
				}
			}
			
			/**
			 * SIMP_FINITE_ID
			 *    finite(id) == finite(S) (where id has type S↔S)
			 */
			Finite(id@IdGen()) -> {
				if (level2) {
					final Type s = `id.getType().getSource();
					result = makeFinite(s.toExpression(ff));
					trace(predicate, result, "SIMP_FINITE_ID");
					return result;
				}
			}

			/**
             * SIMP_FINITE_SETENUM
	    	 * Finite: finite({a, ..., b}) = ⊤
	    	 */
			Finite(SetExtension(_)) -> {
				result = dLib.True();
				trace(predicate, result, "SIMP_FINITE_SETENUM");
				return result;
			}

			/**
             * SIMP_FINITE_BUNION
	    	 * Finite: finite(S ∪ ... ∪ ⊤) == finite(S) ∧ ... ∧ finite(T)
	    	 */
			Finite(BUnion(children)) -> {
				Predicate [] newChildren = new Predicate[`children.length];
				for (int i = 0; i < `children.length; ++i) {
					newChildren[i] = makeFinite(`children[i]);
				}
				result = makeAssociativePredicate(LAND, newChildren);
				trace(predicate, result, "SIMP_FINITE_BUNION");
				return result;
			}

			/**
             * SIMP_FINITE_POW
	    	 * Finite: finite(ℙ(S)) == finite(S)
	    	 */
			Finite(Pow(S)) -> {
				result = makeFinite(`S);
				trace(predicate, result, "SIMP_FINITE_POW");
				return result;
			}

			/**
             * DERIV_FINITE_CPROD
	    	 * Finite: finite(S × ⊤) == S = ∅ ∨ T = ∅ ∨ (finite(S) ∧ finite(T))
	    	 */
			Finite(Cprod(S, T)) -> {
				Predicate [] children = new Predicate[3];
				children[0] = makeIsEmpty(`S);
				children[1] = makeIsEmpty(`T);
				Predicate [] subChildren = new Predicate[2];
				subChildren[0] = makeFinite(`S);
				subChildren[1] = makeFinite(`T);
				children[2] = makeAssociativePredicate(LAND,
						subChildren);
				result = makeAssociativePredicate(LOR, children);
				trace(predicate, result, "DERIV_FINITE_CPROD");
				return result;
			}

			/**
             * SIMP_FINITE_CONVERSE
	    	 * Finite: finite(r∼) == finite(r)
	    	 */
			Finite(Converse(r)) -> {
				result = makeFinite(`r);
				trace(predicate, result, "SIMP_FINITE_CONVERSE");
				return result;
			}

			/**
             * SIMP_FINITE_UPTO
	    	 * Finite: finite(a‥b) == ⊤
	    	 */
			Finite(UpTo(_,_)) -> {
				result = dLib.True();
				trace(predicate, result, "SIMP_FINITE_UPTO");
				return result;
			}
			
			/**
			 * SIMP_FINITE_LAMBDA
			 *    finite({x · P ∣ E ↦ F}) == finite({x · P ∣ E})
			 */
			Finite(lambda@Cset(bil, P, Mapsto(E,_))) -> {
				if (level2 && functionalCheck((QuantifiedExpression) `lambda)) {
					result = makeFinite(makeQuantifiedExpression(
								CSET, `bil, `P, `E, Form.Explicit));
					trace(predicate, result, "SIMP_FINITE_LAMBDA");
					return result;
				}
			}
			
			/**
			 * SIMP_FINITE_ID_DOMRES
			 *    finite(E ◁ id) == finite(E)
			 */
			Finite(DomRes(E, IdGen())) -> {
				if (level2) {
					result = makeFinite(`E);
					trace(predicate, result, "SIMP_FINITE_ID_DOMRES");
					return result;
				}
			} 
			 
			/**
			 * SIMP_FINITE_PRJ1
			 *    finite(prj1) == finite(S × T) where prj1 has type ℙ(S×T×S)
			 */
			Finite(prj1@Prj1Gen()) -> {
				if (level2) {
					result = makeFinite(`prj1.getType().getSource().toExpression(ff));
					trace(predicate, result, "SIMP_FINITE_PRJ1");
					return result;
				}
			}
			 
			/**
			 * SIMP_FINITE_PRJ2
			 *    finite(prj2) == finite(S × T) where prj2 has type ℙ(S×T×T)
			 */
			Finite(prj2@Prj2Gen()) -> {
				if (level2) {
					result = makeFinite(`prj2.getType().getSource().toExpression(ff));
					trace(predicate, result, "SIMP_FINITE_PRJ2");
					return result;
				}
			}

			/**
			 * SIMP_FINITE_PRJ1_DOMRES
			 *    finite(E ◁ prj1) == finite(E)
			 */
			Finite(DomRes(E, Prj1Gen())) -> {
				if (level2) {
					result = makeFinite(`E);
					trace(predicate, result, "SIMP_FINITE_PRJ1_DOMRES");
					return result;
				}
			}

			/**
			 * SIMP_FINITE_PRJ2_DOMRES
			 *    finite(E ◁ prj2) == finite(E)
			 */
			Finite(DomRes(E, Prj2Gen())) -> {
				if (level2) {
					result = makeFinite(`E);
					trace(predicate, result, "SIMP_FINITE_PRJ2_DOMRES");
					return result;
				}
			}

	    }
	    return predicate;
	}

    @ProverRule( { "SIMP_SPECIAL_AND_BTRUE", "SIMP_SPECIAL_AND_BFALSE",
    		"SIMP_MULTI_AND", "SIMP_MULTI_AND_NOT",
			"SIMP_SPECIAL_OR_BTRUE", "SIMP_SPECIAL_OR_BFALSE",
    		"SIMP_MULTI_OR", "SIMP_MULTI_OR_NOT" })
	@Override
	public Predicate rewrite(AssociativePredicate predicate) {
		final Predicate result;
	    %match (Predicate predicate) {
	    	/**
             * SIMP_SPECIAL_AND_BTRUE
	    	 * Conjunction 1: P ∧ ... ∧ ⊤ ∧ ... ∧ Q  == P ∧ ... ∧ Q
             * SIMP_SPECIAL_AND_BFALSE
	    	 * Conjunction 2: P ∧ ... ∧ ⊥ ∧ ... ∧ Q  == ⊥
             * SIMP_MULTI_AND
	    	 * Conjunction 3: P ∧ ... ∧ Q ∧ ... ∧ Q ∧ ... ∧ R  == P ∧ ... ∧ Q ∧ ... ∧ R
             * SIMP_MULTI_AND_NOT
	    	 * Conjunction 4: P ∧ ... ∧ Q ∧ ... ∧ ¬Q ∧ ... ∧ R  == P ∧ ... ∧ Q ∧ ... ∧ R
	    	 */
	    	Land(_) -> {
    			result = simplifyLand(predicate, dLib);
				trace(predicate, result, "SIMP_SPECIAL_AND_BTRUE", "SIMP_SPECIAL_AND_BFALSE",
						"SIMP_MULTI_AND", "SIMP_MULTI_AND_NOT");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_OR_BTRUE
	    	 * Disjunction 1: P ⋁ ... ⋁ ⊤ ⋁ ... ⋁ Q  == ⊤
             * SIMP_SPECIAL_OR_BFALSE
	    	 * Disjunction 2: P ⋁ ... ⋁ ⊥ ⋁ ... ⋁ Q  == P ⋁ ... ⋁ Q
             * SIMP_MULTI_OR
	    	 * Disjunction 3: P ⋁ ... ⋁ Q ⋁ ... ⋁ Q ⋁ ... ⋁ R  == P ⋁ ... ⋁ Q ⋁ ... ⋁ R
             * SIMP_MULTI_OR_NOT
	    	 * Disjunction 4: P ⋁ ... ⋁ Q ⋁ ... ⋁ ¬Q ⋁ ... ⋁ R  == P ⋁ ... ⋁ Q ⋁ ... ⋁ R
	    	 */
	    	Lor(_) -> {
    			result = simplifyLor(predicate, dLib);
				trace(predicate, result, "SIMP_SPECIAL_OR_BTRUE", "SIMP_SPECIAL_OR_BFALSE",
						"SIMP_MULTI_OR", "SIMP_MULTI_OR_NOT");
				return result;
			}
	    }
	    return predicate;
	}

    @ProverRule( { "SIMP_SPECIAL_IMP_BTRUE_L", "SIMP_SPECIAL_IMP_BFALSE_L",
			"SIMP_SPECIAL_IMP_BTRUE_R", "SIMP_SPECIAL_IMP_BFALSE_R",
			"SIMP_MULTI_IMP", "SIMP_MULTI_EQV", "SIMP_SPECIAL_EQV_BTRUE",
			"SIMP_SPECIAL_EQV_BFALSE", "SIMP_MULTI_IMP_OR",
			"SIMP_MULTI_IMP_AND_NOT_R", "SIMP_MULTI_IMP_AND_NOT_L",
			"SIMP_MULTI_EQV_NOT" })
	@Override
	public Predicate rewrite(BinaryPredicate predicate) {
		final Predicate result;
	    %match (Predicate predicate) {
	    	/**
             * SIMP_SPECIAL_IMP_BTRUE_L
	    	 * Implication 1: ⊤ ⇒ P == P
	    	 */
	    	Limp(BTRUE(), P) -> {
	    		result = `P;
	    		trace(predicate, result, "SIMP_SPECIAL_IMP_BTRUE_L");
				return result;
            }

	    	/**
             * SIMP_SPECIAL_IMP_BFALSE_L
	    	 * Implication 2: ⊥ ⇒ P == ⊤
	    	 */
	    	Limp(BFALSE(), _) -> {
	    		result = dLib.True();
	    		trace(predicate, result, "SIMP_SPECIAL_IMP_BFALSE_L");
				return result;
 	    	}

	    	/**
             * SIMP_SPECIAL_IMP_BTRUE_R
	    	 * Implication 3: P ⇒ ⊤ == ⊤
	    	 */
	    	Limp(_, BTRUE()) -> {
	    		result = predicate.getRight();
	    		trace(predicate, result, "SIMP_SPECIAL_IMP_BTRUE_R");
				return result;
 	    	}
	    	
	    	/**
             * SIMP_SPECIAL_IMP_BFALSE_R
	    	 * Implication 4: P ⇒ ⊥ == ¬P
	    	 */
	    	Limp(P, BFALSE()) -> {
	    		result = dLib.makeNeg(`P);
	    		trace(predicate, result, "SIMP_SPECIAL_IMP_BFALSE_R");
				return result;
 	    	}

	    	/**
             * SIMP_MULTI_IMP
	    	 * Implication 5: P ⇒ P == ⊤
	    	 */
	    	Limp(P, P) -> {
	    		result = dLib.True();
	    		trace(predicate, result, "SIMP_MULTI_IMP");
				return result;
 	    	}

	    	/**
             * SIMP_MULTI_EQV
	    	 * Equivalent 5: P ⇔ P == ⊤
	    	 */
	    	Leqv(P, P) -> {
	    		result = dLib.True();
	    		trace(predicate, result, "SIMP_MULTI_EQV");
				return result;
 	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BTRUE
	    	 * Equivalent 1: P ⇔ ⊤ == P
	    	 */
	    	Leqv(P, BTRUE()) -> {
	    		result = `P;
	    		trace(predicate, result, "SIMP_SPECIAL_EQV_BTRUE");
				return result;
 	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BTRUE
	    	 * Equivalent 2: ⊤ ⇔ P = P
	    	 */
	    	Leqv(BTRUE(), P) -> {
	    		result = `P;
	    		trace(predicate, result, "SIMP_SPECIAL_EQV_BTRUE");
				return result;
 	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BFALSE
	    	 * Equivalent 3: P ⇔ ⊥ = ¬P
	    	 */
	    	Leqv(P, BFALSE()) -> {
	    		result = dLib.makeNeg(`P);
	    		trace(predicate, result, "SIMP_SPECIAL_EQV_BFALSE");
				return result;
 	    	}

	    	/**
             * SIMP_SPECIAL_EQV_BFALSE
	    	 * Equivalent 4: ⊥ ⇔ P == ¬P
	    	 */
	    	Leqv(BFALSE(), P) -> {
	    		result = dLib.makeNeg(`P);
	    		trace(predicate, result, "SIMP_SPECIAL_EQV_BFALSE");
				return result;
 	    	}

			/**
			 * SIMP_MULTI_EQV_NOT
			 *     P ⇔ ¬P == ⊥
			 *    ¬P ⇔  P == ⊥
			 */
			Leqv(P, Not(P)) -> {
			 	if (level2) {
			 		result = dLib.False();
			 		trace(predicate, result, "SIMP_MULTI_EQV_NOT");
			 		return result;
			 	}
			 }
			Leqv(Not(P), P) -> {
			 	if (level2) {
			 		result = dLib.False();
			 		trace(predicate, result, "SIMP_MULTI_EQV_NOT");
			 		return result;
			 	}
			 }

	    	/**
             * SIMP_MULTI_IMP_OR
	    	 *    P ∧ ... ∧ Q ∧ ... ∧ R ⇒ Q == ⊤
	    	 */
	    	Limp(Land(pList(_*, Q, _*)), Q) -> {
	    		if (level2) {
		    		result = dLib.True();
		    		trace(predicate, result, "SIMP_MULTI_IMP_OR");
					return result;
				}
 	    	}

 	    	/**
             * SIMP_MULTI_IMP_AND_NOT_R
	    	 *    P ∧ ... ∧ Q ∧ ... ∧ R ⇒ ¬Q == ¬(P ∧ ... ∧ Q ∧ ... ∧ R)
	    	 *
             * SIMP_MULTI_IMP_AND_NOT_L
	    	 *    P ∧ ... ∧ ¬Q ∧ ... ∧ R ⇒ Q == ¬(P ∧ ... ∧ ¬Q ∧ ... ∧ R)
	    	 */
	    	Limp(and@Land(children), Q) -> {
	    		/* cannot do same as above because makeNeg(Q) =/= Not(Q)
	    		 * TODO tom doc says the following should work:
	    		 * Limp(and@Land(pList(_*, nQ, _*)), Q)
	    		 * && (nQ << Predicate dLib.makeNeg(Q))
	    		 */
	    		if (level2 && contains(`children, dLib.makeNeg(`Q))) {
		    		result = dLib.makeNeg(`and);
		    		trace(predicate, result, "SIMP_MULTI_IMP_AND_NOT_R",
		    				"SIMP_MULTI_IMP_AND_NOT_L");
					return result;
				}
 	    	}

	    }
	    return predicate;
	}

    @ProverRule( { "SIMP_SPECIAL_NOT_BTRUE", "SIMP_SPECIAL_NOT_BFALSE",
			"SIMP_NOT_NOT", "SIMP_NOT_LE", "SIMP_NOT_GE", "SIMP_NOT_GT",
			"SIMP_NOT_LT", "SIMP_SPECIAL_NOT_EQUAL_FALSE_R",
			"SIMP_SPECIAL_NOT_EQUAL_TRUE_R", "SIMP_SPECIAL_NOT_EQUAL_FALSE_L",
			"SIMP_SPECIAL_NOT_EQUAL_TRUE_L" })
	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
		final Predicate result;
	    %match (Predicate predicate) {

	    	/**
             * SIMP_SPECIAL_NOT_BTRUE
	    	 * Negation 1: ¬⊤ == ⊥
	    	 */
	    	Not(BTRUE()) -> {
	    		result = dLib.False();
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_BTRUE");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_BFALSE
	    	 * Negation 2: ¬⊥ == ⊤
	    	 */
			Not(BFALSE()) -> {
				result =  dLib.True();
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_BFALSE");
				return result;
			}

	    	/**
             * SIMP_NOT_NOT
	    	 * Negation 3: ¬¬P == P
	    	 */
			Not(Not(P)) -> {
				result =  `P;
	    		trace(predicate, result, "SIMP_NOT_NOT");
				return result;
			}

	    	/**
             * SIMP_NOT_LE
	    	 * Negation 8: ¬ a ≤ b == a > b
	    	 */
			Not(Le(a, b)) -> {
				result =  makeRelationalPredicate(GT, `a, `b);
	    		trace(predicate, result, "SIMP_NOT_LE");
				return result;
			}

	    	/**
             * SIMP_NOT_GE
	    	 * Negation 9: ¬ a ≥ b == a < b
	    	 */
			Not(Ge(a, b)) -> {
				result =  makeRelationalPredicate(LT, `a, `b);
	    		trace(predicate, result, "SIMP_NOT_GE");
				return result;
			}

	    	/**
             * SIMP_NOT_GT
	    	 * Negation 10: ¬ a > b == a ≤ b
	    	 */
			Not(Gt(a, b)) -> {
				result =  makeRelationalPredicate(LE, `a, `b);
	    		trace(predicate, result, "SIMP_NOT_GT");
				return result;
			}

	    	/**
             * SIMP_NOT_LT
	    	 * Negation 11: ¬ a < b == a ≥ b
	    	 */
			Not(Lt(a, b)) -> {
				result =  makeRelationalPredicate(GE, `a, `b);
	    		trace(predicate, result, "SIMP_NOT_LT");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_FALSE_R
	    	 * Negation 12: ¬ (E = FALSE) == E = TRUE
	    	 */
			Not(Equal(E, FALSE())) -> {
				result =  makeRelationalPredicate(EQUAL, `E, dLib.TRUE());
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_FALSE_R");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_TRUE_R
	    	 * Negation 13: ¬ (E = TRUE) == E = FALSE
	    	 */
			Not(Equal(E, TRUE())) -> {
				result =  makeRelationalPredicate(EQUAL, `E, dLib.FALSE());
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_TRUE_R");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_FALSE_L
	    	 * Negation 14: ¬ (FALSE = E) == TRUE = E
	    	 */
			Not(Equal(FALSE(), E)) -> {
				result =  makeRelationalPredicate(EQUAL, dLib.TRUE(), `E);
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_FALSE_L");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_TRUE_L
	    	 * Negation 15: ¬ (TRUE = E) == FALSE = E
	    	 */
			Not(Equal(TRUE(), E)) -> {
				result =  makeRelationalPredicate(EQUAL, dLib.FALSE(), `E);
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_TRUE_L");
				return result;
			}
			
	    }
	    return predicate;
	}

    @ProverRule({"SIMP_FORALL_AND", "SIMP_EXISTS_OR"}) 
	@Override
	public Predicate rewrite(QuantifiedPredicate predicate) {
		final Predicate result;
	    %match (Predicate predicate) {

	    	/**
             * SIMP_FORALL_AND
	    	 * Quantification 1: ∀x·(P ∧ ... ∧ Q) == (∀x·P) ∧ ... ∧ ∀(x·Q)
	    	 */
	    	ForAll(boundIdentifiers, Land(children)) -> {
	    		Predicate [] predicates = new Predicate[`children.length];
	    		for (int i = 0; i < `children.length; ++i) {
					Predicate qPred = makeQuantifiedPredicate(FORALL, `boundIdentifiers, `children[i]);
					predicates[i] = qPred;
				}

	    		result = makeAssociativePredicate(LAND, predicates);
	    		trace(predicate, result, "SIMP_FORALL_AND");
				return result;
	    	}

	    	/**
             * SIMP_EXISTS_OR
	    	 * Quantification 2: ∃x·(P ⋁ ... ⋁ Q) == (∃x·P) ⋁ ... ⋁ ∃(x·Q)
	    	 */
			Exists(boundIdentifiers, Lor(children)) -> {
	    		Predicate [] predicates = new Predicate[`children.length];
	    		for (int i = 0; i < `children.length; ++i) {
					Predicate qPred = makeQuantifiedPredicate(EXISTS, `boundIdentifiers, `children[i]);
					predicates[i] = qPred;
				}

	    		result = makeAssociativePredicate(LOR, predicates);
	    		trace(predicate, result, "SIMP_EXISTS_OR");
				return result;
	    	}
	    	
	    }
	    return predicate;
	}
	
    @ProverRule( { "SIMP_MULTI_EQUAL", "SIMP_MULTI_NOTEQUAL", "SIMP_MULTI_LE",
			"SIMP_MULTI_GE", "SIMP_MULTI_LT", "SIMP_MULTI_GT",
			"SIMP_EQUAL_MAPSTO", "SIMP_SPECIAL_EQUAL_TRUE", "SIMP_NOTEQUAL",
            "SIMP_NOTIN", "SIMP_NOTSUBSET", "SIMP_NOTSUBSETEQ",
            "SIMP_SPECIAL_SUBSETEQ", "SIMP_MULTI_SUBSETEQ",
            "SIMP_SUBSETEQ_BUNION",
            "SIMP_SUBSETEQ_BINTER", "DERIV_SUBSETEQ_BUNION",
            "DERIV_SUBSETEQ_BINTER", "SIMP_SPECIAL_IN", "SIMP_MULTI_IN",
            "SIMP_IN_SING", "SIMP_IN_COMPSET", "SIMP_IN_COMPSET_ONEPOINT",
            "SIMP_EQUAL_SING", "SIMP_LIT_EQUAL", "SIMP_LIT_LE", "SIMP_LIT_LT",
			"SIMP_LIT_GE", "SIMP_LIT_GT", "SIMP_SPECIAL_EQUAL_CARD",
			"SIMP_LIT_EQUAL_CARD_1", "SIMP_LIT_GT_CARD_0",
			"SIMP_LIT_LT_CARD_0", "SIMP_LIT_EQUAL_KBOOL_TRUE",
			"SIMP_LIT_EQUAL_KBOOL_FALSE", "SIMP_EQUAL_CONSTR",
			"SIMP_EQUAL_CONSTR_DIFF", "SIMP_SUBSETEQ_SING",
			"SIMP_SPECIAL_SUBSET_R", "SIMP_MULTI_SUBSET",
			"SIMP_SPECIAL_EQUAL_REL", "SIMP_SPECIAL_EQUAL_RELDOM",
			"SIMP_LIT_GE_CARD_1", "SIMP_LIT_LE_CARD_1", "SIMP_LIT_LE_CARD_0",
			"SIMP_LIT_GE_CARD_0", "SIMP_CARD_NATURAL", "SIMP_CARD_NATURAL1",
			"SIMP_LIT_IN_NATURAL", "SIMP_LIT_IN_NATURAL1",
			"SIMP_SPECIAL_IN_NATURAL1", "SIMP_LIT_IN_MINUS_NATURAL",
			"SIMP_LIT_IN_MINUS_NATURAL1", "SIMP_IN_FUNIMAGE",
			"SIMP_IN_FUNIMAGE_CONVERSE_L", "SIMP_IN_FUNIMAGE_CONVERSE_R",
			"SIMP_MULTI_EQUAL_BINTER", "SIMP_MULTI_EQUAL_BUNION",
			"SIMP_SPECIAL_SUBSET_L", "SIMP_SUBSETEQ_COMPSET_L",
			"SIMP_SPECIAL_EQUAL_COMPSET" })
    @Override
	public Predicate rewrite(RelationalPredicate predicate) {
		final Predicate result;
	    %match (Predicate predicate) {

	    	/**
             * SIMP_MULTI_EQUAL
	    	 * Equality: E = E == ⊤
	    	 */
	    	Equal(E, E) -> {
	    		result = dLib.True();
	    		trace(predicate, result, "SIMP_MULTI_EQUAL");
				return result;
	    	}

	    	/**
             * SIMP_MULTI_NOTEQUAL
	    	 * Equality: E ≠ E == ⊥
	    	 */
	    	NotEqual(E, E) -> {
	    		result = dLib.False();
	    		trace(predicate, result, "SIMP_MULTI_NOTEQUAL");
				return result;
	    	}

			/**
             * SIMP_MULTI_LE
	    	 * Arithmetic: E ≤ E == ⊤
	    	 */
	    	Le(E, E) -> {
				result = dLib.True();
	    		trace(predicate, result, "SIMP_MULTI_LE");
				return result;
	    	}
			
	    	/**
             * SIMP_MULTI_GE
	    	 * Arithmetic: E ≥ E == ⊤
	    	 */
	    	Ge(E, E) -> {
				result = dLib.True();
	    		trace(predicate, result, "SIMP_MULTI_GE");
				return result;
	    	}
			
			/**
             * SIMP_MULTI_LT
	    	 * Arithmetic: E < E == ⊥
	    	 */
	    	Lt(E, E) -> {
				result = dLib.False();
	    		trace(predicate, result, "SIMP_MULTI_LT");
				return result;
	    	}
			
			/**
             * SIMP_MULTI_GE
	    	 * Arithmetic: E > E == ⊥
	    	 */
	    	Gt(E, E) -> {
				result = dLib.False();
	    		trace(predicate, result, "SIMP_MULTI_GT");
				return result;
	    	}
			
			/**
             * SIMP_EQUAL_MAPSTO
	    	 * Equality 3: E ↦ F = G ↦ H == E = G ∧ F = H
	    	 */
	    	Equal(Mapsto(E, F) , Mapsto(G, H)) -> {
	    		Predicate pred1 = makeRelationalPredicate(EQUAL, `E, `G);
				Predicate pred2 = makeRelationalPredicate(EQUAL, `F, `H);
				result = makeAssociativePredicate(LAND, new Predicate[] {
						pred1, pred2 });
	    		trace(predicate, result, "SIMP_EQUAL_MAPSTO");
				return result;
	    	}
	    	
	    	/**
             * SIMP_SPECIAL_EQUAL_TRUE
	    	 * Equality 4: TRUE = FALSE == ⊥
	    	 */
	    	Equal(TRUE(), FALSE()) -> {
	    		result = dLib.False();
	    		trace(predicate, result, "SIMP_SPECIAL_EQUAL_TRUE");
				return result;
	    	}

	    	/**
             * SIMP_SPECIAL_EQUAL_TRUE
	    	 * Equality 5: FALSE = TRUE == ⊥
	    	 */
	    	Equal(FALSE(), TRUE()) -> {
	    		result = dLib.False();
	    		trace(predicate, result, "SIMP_SPECIAL_EQUAL_TRUE");
				return result;
	    	}

	    	/**
             * SIMP_NOTEQUAL
	    	 * Negation 4: E ≠ F == ¬ E = F
	    	 */
	    	NotEqual(E, F) -> {
	    		result = makeNotEqual(`E, `F);
	    		trace(predicate, result, "SIMP_NOTEQUAL");
				return result;
	    	}

	    	/**
             * SIMP_NOTIN
	    	 * Negation 5: E ∉ F == ¬ E ∈ F
	    	 */
	    	NotIn(E, F) -> {
	    		result = makeUnaryPredicate(
	    			NOT, makeRelationalPredicate(IN, `E, `F));
	    		trace(predicate, result, "SIMP_NOTIN");
				return result;
	    	}


	    	/**
             * SIMP_NOTSUBSET
	    	 * Negation 6: E ⊄ F == ¬ E ⊂ F
	    	 */
	    	NotSubset(E, F) -> {
	    		result = makeUnaryPredicate(
	    			NOT, makeRelationalPredicate(SUBSET, `E, `F));
	    		trace(predicate, result, "SIMP_NOTSUBSET");
				return result;
	    	}

	    	/**
             * SIMP_NOTSUBSETEQ
	    	 * Negation 7: E ⊈ F == ¬ E ⊆ F
	    	 */
	    	NotSubsetEq(E, F) -> {
	    		result = makeUnaryPredicate(
	    			NOT, makeRelationalPredicate(SUBSETEQ, `E, `F));
	    		trace(predicate, result, "SIMP_NOTSUBSETEQ");
				return result;
	    	}

	    	/**
             * SIMP_SPECIAL_SUBSETEQ
	    	 * Set Theory 5: ∅ ⊆ S == ⊤
	    	 */
	    	SubsetEq(EmptySet(), _) -> {
	    		result = dLib.True();
	    		trace(predicate, result, "SIMP_SPECIAL_SUBSETEQ");
				return result;
	    	}
	    	
	    	/**
             * SIMP_MULTI_SUBSETEQ
	    	 * Set Theory: S ⊆ S == ⊤
	    	 */
	    	SubsetEq(S, S) -> {
	    		result = dLib.True();
	    		trace(predicate, result, "SIMP_MULTI_SUBSETEQ");
				return result;
	    	}
			
	    	/**
             * SIMP_SUBSETEQ_BUNION
	    	 * Set Theory: S ⊆ A ∪ ... ∪ S ∪ ... ∪ B == ⊤
	    	 */
	    	SubsetEq(S, BUnion(eList(_*, S, _*))) -> {
	    		result = dLib.True();
	    		trace(predicate, result, "SIMP_SUBSETEQ_BUNION");
	    		return result;
	    	}
			
	    	/**
             * SIMP_SUBSETEQ_BINTER
	    	 * Set Theory: A ∩ ... ∩ S ∩ ... ∩ B ⊆ S == ⊤
	    	 */
	    	SubsetEq(BInter(eList(_*, S, _*)), S) -> {
				result = dLib.True();
				trace(predicate, result, "SIMP_SUBSETEQ_BINTER");
				return result;
	    	}
			
			/**
             * DERIV_SUBSETEQ_BUNION
	    	 * Set Theory: A ∪ ... ∪ B ⊆ S == A ⊆ S ∧ ... ∧ B ⊆ S
	    	 */
	    	SubsetEq(BUnion(children), S) -> {
	    		Predicate [] newChildren = new Predicate[`children.length];
	    		for (int i = 0; i < `children.length; ++i) {
	    			newChildren[i] = makeRelationalPredicate(SUBSETEQ,
	    					`children[i], `S);
	    		}
	    		result = makeAssociativePredicate(LAND, newChildren);
	    		trace(predicate, result, "DERIV_SUBSETEQ_BUNION");
				return result;
	    	}
			
			/**
             * DERIV_SUBSETEQ_BINTER
	    	 * Set Theory: S ⊆ A ∩ ... ∩ B  == S ⊆ A ∧ ... ∧ S ⊆ B
	    	 */
	    	SubsetEq(S, BInter(children)) -> {
	    		Predicate [] newChildren = new Predicate[`children.length];
	    		for (int i = 0; i < `children.length; ++i) {
	    			newChildren[i] = makeRelationalPredicate(SUBSETEQ,
	    					`S, `children[i]);
	    		}
	    		result = makeAssociativePredicate(LAND, newChildren);
	    		trace(predicate, result, "DERIV_SUBSETEQ_BINTER");
				return result;
	    	}
			
			/**
             * SIMP_SPECIAL_IN
	    	 * Set Theory 7: E ∈ ∅ == ⊥
	    	 */
	    	In(_, EmptySet()) -> {
	    		result = dLib.False();
	    		trace(predicate, result, "SIMP_SPECIAL_IN");
				return result;
	    	}	    	

			/**
	    	 * SIMP_MULTI_IN
             * Set Theory 9: B ∈ {A, ..., B, ..., C} == ⊤
	    	 */
	    	In(B, SetExtension(eList(_*, B, _*))) -> {
				result = dLib.True();
				trace(predicate, result, "SIMP_MULTI_IN");
				return result;
	    	}

			/**
	    	 * SIMP_IN_SING
             * Set Theory 18: E ∈ {F} == E = F (if F is a single expression)
	    	 */
	    	In(E, SetExtension(eList(F))) -> {
				result = makeRelationalPredicate(EQUAL, `E, `F);
				trace(predicate, result, "SIMP_IN_SING");
				return result;
	    	}

			/**
             * SIMP_IN_COMPSET
	    	 * Set Theory: F ∈ {x,y,... · P(x,y,...) | E(x,y,...)} == ∃x,y,...· P(x,y,...) ∧ E(x,y,...) = F
             * SIMP_IN_COMPSET_ONEPOINT
	    	 * Set Theory 10: E ∈ {x · P(x) | x} == P(E)
	    	 */
	    	 In(_, Cset(_, _, _)) -> {
	    	 	final OnePointProcessorRewriting opp = new OnePointProcessorRewriting(predicate, ff);
	    	 	opp.matchAndInstantiate();
	    	 	if (opp.wasSuccessfullyApplied()) {
	    	 		result = opp.getProcessedResult();
	    	 		trace(predicate, result, "SIMP_IN_COMPSET_ONEPOINT");
	    	 		return result;
	    	 	} else {
	    	 		result = opp.getQuantifiedPredicate();
	    	 		trace(predicate, result, "SIMP_IN_COMPSET");
	    	 		return result;
	    	 	}
	    	}
		
			/**
             * SIMP_EQUAL_SING
	    	 * Set Theory 19: {E} = {F} == E = F   if E, F is a single expression
	    	 */
	    	Equal(SetExtension(eList(E)), SetExtension(eList(F))) -> {
				result = makeRelationalPredicate(EQUAL, `E, `F);
				trace(predicate, result, "SIMP_EQUAL_SING");
				return result;
	    	}
	    	
	    	/**
             * SIMP_LIT_EQUAL
	    	 * Arithmetic 16: i = j == ⊤  or  i = j == ⊥ (by computation)
	    	 */
	    	Equal(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.equals(`j) ? dLib.True() : dLib.False();
	    		trace(predicate, result, "SIMP_LIT_EQUAL");
				return result;
	    	}

	    	/**
             * SIMP_LIT_LE
	    	 * Arithmetic 17: i ≤ j == ⊤  or  i ≤ j == ⊥ (by computation)
	    	 */
	    	Le(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) <= 0 ? dLib.True() : dLib.False();
	    		trace(predicate, result, "SIMP_LIT_LE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_LT
	    	 * Arithmetic 18: i < j == ⊤  or  i < j == ⊥ (by computation)
	    	 */
	    	Lt(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) < 0 ? dLib.True() : dLib.False();
	    		trace(predicate, result, "SIMP_LIT_LT");
				return result;
	    	}

	    	/**
             * SIMP_LIT_GE
	    	 * Arithmetic 19: i ≥ j == ⊤  or  i ≥ j == ⊥ (by computation)
	    	 */
	    	Ge(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) >= 0 ? dLib.True() : dLib.False();
	    		trace(predicate, result, "SIMP_LIT_GE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_GT
	    	 * Arithmetic 20: i > j == ⊤  or  i > j == ⊥ (by computation)
	    	 */
	    	Gt(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) > 0 ? dLib.True() : dLib.False();
	    		trace(predicate, result, "SIMP_LIT_GT");
				return result;
	    	}
	    	
	    	/**
	    	 * Cardinality:
             * SIMP_SPECIAL_EQUAL_CARD
             * card(S) = 0  ==  S = ∅
             * SIMP_LIT_EQUAL_CARD_1
	    	 * card(S) = 1  ==  ∃x·S = {x}
	    	 */
	    	Equal(Card(S), E) -> {
	    		if (`E.equals(number0)) {
	    			result = makeIsEmpty(`S);
	    			trace(predicate, result, "SIMP_SPECIAL_EQUAL_CARD");
	    			return result;
	    		}
	    		else if (`E.equals(number1)) {
	    			result = new FormulaUnfold(ff).makeExistSingletonSet(`S);
	    			trace(predicate, result, "SIMP_LIT_EQUAL_CARD_1");
	    			return result;
	    		}
	    	}

	    	/**
	    	 * Cardinality:
             * SIMP_SPECIAL_EQUAL_CARD
             * 0 = card(S)  ==  S = ∅
             * SIMP_LIT_EQUAL_CARD_1
	    	 * 1 = card(S)  ==  ∃x·S = {x}
	    	 */
	    	Equal(E, Card(S)) -> {
	    		if (`E.equals(number0)) {
	    			result = makeIsEmpty(`S);
	    			trace(predicate, result, "SIMP_SPECIAL_EQUAL_CARD");
	    			return result;
	    		}
	    		else if (`E.equals(number1)) {
	    			result = new FormulaUnfold(ff).makeExistSingletonSet(`S);
	    			trace(predicate, result, "SIMP_LIT_EQUAL_CARD_1");
	    			return result;
	    		}
	    	}

	    	/**
             * SIMP_LIT_GT_CARD_0
	    	 * Cardinality: card(S) > 0  ==  ¬(S = ∅)
	    	 */
	    	Gt(Card(S), E)-> {
	    		if (`E.equals(number0)) {
	    			Predicate equal = makeIsEmpty(`S);
	    			result = makeUnaryPredicate(NOT, equal);
	    			trace(predicate, result, "SIMP_LIT_GT_CARD_0");
	    			return result;
	    		}
	    	}

	    	/**
             * SIMP_LIT_LT_CARD_0
	    	 * Cardinality: 0 < card(S)  ==  ¬(S = ∅)
	    	 */
	    	Lt(E, Card(S)) -> {
	    		if (`E.equals(number0)) {
	    			Predicate equal = makeIsEmpty(`S);
	    			result = makeUnaryPredicate(NOT, equal);
	    			trace(predicate, result, "SIMP_LIT_LT_CARD_0");
	    			return result;
	    		}
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_TRUE
	    	 * Boolean: TRUE = bool(P) == P  
	    	 */
	    	Equal(TRUE(), Bool(P)) -> {
	    		result = `P;
	    		trace(predicate, result, "SIMP_LIT_EQUAL_KBOOL_TRUE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_TRUE
	    	 * Boolean: bool(P) = TRUE == P  
	    	 */
	    	Equal(Bool(P), TRUE()) -> {
	    		result = `P;
	    		trace(predicate, result, "SIMP_LIT_EQUAL_KBOOL_TRUE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_FALSE
	    	 * Boolean: FALSE = bool(P) == ¬P  
	    	 */
	    	Equal(FALSE(), Bool(P)) -> {
	    		result = makeUnaryPredicate(NOT, `P);
	    		trace(predicate, result, "SIMP_LIT_EQUAL_KBOOL_FALSE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_EQUAL_KBOOL_FALSE
	    	 * Boolean: bool(P) = FALSE == ¬P  
	    	 */
	    	Equal(Bool(P), FALSE()) -> {
	    		result = makeUnaryPredicate(NOT, `P);
	    		trace(predicate, result, "SIMP_LIT_EQUAL_KBOOL_FALSE");
				return result;
	    	}
	    	
            /**
             * SIMP_EQUAL_CONSTR
             * cons(a1, b1) = cons(a2, b2)  ==  a1 = a2 & b1 = b2
             * SIMP_EQUAL_CONSTR_DIFF
             * cons1(...) = cons2(...)  ==  false  [where cons1 /= cons2]
             */
            Equal(ext1@ExtendedExpression(args1, _), ext2@ExtendedExpression(args2, _)) -> {
                if (isDTConstructor((ExtendedExpression)`ext1)
                		&& isDTConstructor((ExtendedExpression)`ext2)) {
                	if (`ext1.getTag() != `ext2.getTag()) {
                		result = ff.makeLiteralPredicate(BFALSE, null);
                		trace(predicate, result, "SIMP_EQUAL_CONSTR_DIFF");
                		return result;
                	}
                	assert `args1.length == `args2.length;
                	final List<Predicate> equalPreds = new ArrayList<Predicate>();
                	for (int i=0; i<`args1.length; i++) {
                		equalPreds.add(ff.makeRelationalPredicate(EQUAL, `args1[i], `args2[i], null));
                	}
                	switch(equalPreds.size()) {
                	case 0:
                		result = ff.makeLiteralPredicate(BTRUE, null);
                		break;
                	case 1:
                		result = equalPreds.get(0);
                		break;
                	default:
                		result = ff.makeAssociativePredicate(LAND, equalPreds, null);
                		break;
                	}
               		trace(predicate, result, "SIMP_EQUAL_CONSTR");
               		return result;
                }
            }

            /**
			 * SIMP_SUBSETEQ_SING
			 *    {E} ⊆ S == E ∈ S (where E is a single expression)
			 */
			SubsetEq(SetExtension(eList(E)), S) -> {
				if (level2) {
					result = makeRelationalPredicate(IN, `E, `S);
					trace(predicate, result, "SIMP_SUBSETEQ_SING");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_SUBSET_R
			 *    S ⊂ ∅ == ⊥
			 */
			Subset(_, EmptySet()) -> {
				if (level2) {
					result = dLib.False();
					trace(predicate, result, "SIMP_SPECIAL_SUBSET_R");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_SUBSET
			 *    S ⊂ S == ⊥
			 */
			Subset(S, S) -> {
				if (level2) {
					result = dLib.False();
					trace(predicate, result, "SIMP_MULTI_SUBSET");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_EQUAL_REL
			 *    A ↔ B = ∅ == ⊥
			 */
			Equal((Rel | Pfun | Pinj)(_, _), EmptySet()) -> {
				if (level2) {
					result = dLib.False();
					trace(predicate, result, "SIMP_SPECIAL_EQUAL_REL");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_EQUAL_RELDOM
			 *    A  B = ∅ == ¬(A = ∅) ∧ B = ∅
			 *    A → B = ∅ == ¬(A = ∅) ∧ B = ∅
			 *    A ↣ B = ∅ == ¬(A = ∅) ∧ B = ∅
			 *    A ↠ B = ∅ == ¬(A = ∅) ∧ B = ∅
			 *    A ⤖ B = ∅ == ¬(A = ∅) ∧ B = ∅
			 */
			Equal((Tfun | Trel | Tinj | Tsur | Tbij)(A, B), EmptySet()) -> {
				if (level2) {
					result = makeAssociativePredicate(LAND,
								makeUnaryPredicate(NOT, makeIsEmpty(`A)),
								makeIsEmpty(`B));
					trace(predicate, result, "SIMP_SPECIAL_EQUAL_RELDOM");
					return result;
				}
			}
			
			/**
			 * SIMP_CARD_NATURAL
			 *    card(S) ∈ ℕ == ⊤
			 */
			In(Card(_), Natural()) -> {
				if (level2) {
					result = dLib.True();
					trace(predicate, result, "SIMP_CARD_NATURAL");
					return result;
				}
			}
			
			/**
			 * SIMP_CARD_NATURAL1
			 *    card(S) ∈ ℕ1 == ¬ S = ∅
			 */
			In(Card(S), Natural1()) -> {
				if (level2) {
					result = makeUnaryPredicate(NOT, makeIsEmpty(`S));
					trace(predicate, result, "SIMP_CARD_NATURAL1");
					return result;
				}
			}
			
			/**
			 * SIMP_LIT_IN_NATURAL
			 *    i ∈ ℕ == ⊤  (where i is a non−negative literal)
			 *
			 * SIMP_LIT_IN_MINUS_NATURAL
			 *    −i ∈ ℕ == ⊥ (where i is a positive literal)
			 */
			In(IntegerLiteral(i), Natural()) -> {
				if (level2) {
					if (`i.signum() >= 0) {
						result = dLib.True();
						trace(predicate, result, "SIMP_LIT_IN_NATURAL");
						return result;
					} else {
						result = dLib.False();
						trace(predicate, result, "SIMP_LIT_IN_MINUS_NATURAL");
						return result;
					}
				}
			}
			
			/**
			 * SIMP_LIT_IN_NATURAL1
			 *    i ∈ ℕ1 == ⊤ (where i is a positive literal)
			 * SIMP_SPECIAL_IN_NATURAL1
			 *    0 ∈ ℕ1 == ⊥
			 * SIMP_LIT_IN_MINUS_NATURAL1
			 *    −i ∈ ℕ1 == ⊥ (where i is a non−negative literal)
			 */
			In(IntegerLiteral(i), Natural1()) -> {
				if (level2) {
					switch (`i.signum()) {
					case 1:
						result = dLib.True();
						trace(predicate, result, "SIMP_LIT_IN_NATURAL1");
						return result;
					case 0:
						result = dLib.False();
						trace(predicate, result, "SIMP_SPECIAL_IN_NATURAL1");
						return result;
					case -1:
						result = dLib.False();
						trace(predicate, result, "SIMP_LIT_IN_MINUS_NATURAL1");
						return result;
					default:
						assert false;
					}
				}
			}

			/**
			 * SIMP_LIT_LE_CARD_0
			 *    0 ≤ card(S) == ⊤
 	    	 * SIMP_LIT_LE_CARD_1
 	    	 *    1 ≤ card(S) == ¬(S = ∅)
 	    	 */
 	    	Le(IntegerLiteral(i), Card(S)) -> {
 	    		if (level2) {
 	    			if (`i. equals(ZERO)) {
 	    				result = dLib.True();
						trace(predicate, result, "SIMP_LIT_LE_CARD_0");
						return result;
 	    			} else if (`i.equals(ONE)) {
	 	    			result = makeUnaryPredicate(NOT, makeIsEmpty(`S));
	 	    			trace(predicate, result, "SIMP_LIT_LE_CARD_1");
						return result;
 	    			}
 	    		}
 	    	}

			/**
			 * SIMP_LIT_GE_CARD_0
			 *    card(S) ≥ 0 == ⊤
 	    	 * SIMP_LIT_GE_CARD_1
 	    	 *    card(S) ≥ 1 == ¬(S = ∅)
 	    	 */
 	    	Ge(Card(S), IntegerLiteral(i)) -> {
 	    		if (level2) {
 	    			if (`i.equals(ZERO)) {
	 	    			result = dLib.True();
						trace(predicate, result, "SIMP_LIT_GE_CARD_0");
						return result;
 	    			} else if (`i.equals(ONE)) {
	 	    			result = makeUnaryPredicate(NOT, makeIsEmpty(`S));
	 	    			trace(predicate, result, "SIMP_LIT_GE_CARD_1");
						return result;
 	    			}
 	    		}
 	    	}
			
			/**
			 * SIMP_IN_FUNIMAGE
			 *    E ↦ F(E) ∈ F == ⊤
			 */
			In(Mapsto(E, FunImage(F, E)), F) -> {
				if (level2) {
					result = dLib.True();
					trace(predicate, result, "SIMP_IN_FUNIMAGE");
					return result;
				}
			}
			
			/**
			 * SIMP_IN_FUNIMAGE_CONVERSE_L
			 *    F∼(E) ↦ E ∈ F == ⊤
			 */
			In(Mapsto(FunImage(Converse(F), E), E), F) -> {
				if (level2) {
					result = dLib.True();
					trace(predicate, result, "SIMP_IN_FUNIMAGE_CONVERSE_L");
					return result;
				}
			}
			
			/**
			 * SIMP_IN_FUNIMAGE_CONVERSE_R
			 *    F(E) ↦ E ∈ F∼ == ⊤
			 */
			In(Mapsto(FunImage(F, E), E), Converse(F)) -> {
				if (level2) {
					result = dLib.True();
					trace(predicate, result, "SIMP_IN_FUNIMAGE_CONVERSE_R");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_EQUAL_BINTER
			 *    S ∩ .. ∩ T ∩ .. ∩ U = T == T ⊆ S ∩ .. ∩ U
			 */
			Equal(expr@BInter(eList(_*, T, _*)), T) -> {
				if (level2) {
					final Expression inter =
							removeChild((AssociativeExpression) `expr, `T);
					result = makeRelationalPredicate(SUBSETEQ, `T, inter);
					trace(predicate, result, "SIMP_MULTI_EQUAL_BINTER");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_EQUAL_BUNION
			 *    S ∪ .. ∪ T ∪ .. ∪ U = T == S ∪ .. ∪ U  ⊆ T
			 */
			Equal(expr@BUnion(eList(_*, T, _*)), T) -> {
				if (level2) {
					final Expression union =
							removeChild((AssociativeExpression) `expr, `T);
					result = makeRelationalPredicate(SUBSETEQ, union, `T);
					trace(predicate, result, "SIMP_MULTI_EQUAL_BUNION");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_SUBSET_L
			 *    ∅ ⊂ S == S ≠ ∅
			 */
			Subset(empty@EmptySet(), S) -> {
				if (level2) {
					result = makeNotEqual(`S, `empty);
					trace(predicate, result, "SIMP_SPECIAL_SUBSET_L");
					return result;
				}
			}
			
			/**
			 * SIMP_SUBSETEQ_COMPSET_L
			 *    {x · P(x) ∣ E(x)} ⊆ S == ∀y · P(y) ⇒ E(y) ∈ S
			 */
			SubsetEq(Cset(bidl, P, E), S) -> {
				if (level2) {
					final Predicate pred = makeBinaryPredicate(LIMP, `P,
												makeRelationalPredicate(IN,`E,
													`S.shiftBoundIdentifiers(`bidl.length, ff)));
					result = makeQuantifiedPredicate(FORALL, `bidl, pred);
					trace(predicate, result, "SIMP_SUBSETEQ_COMPSET_L");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_EQUAL_COMPSET
			 *    {x · P(x) ∣ E} = ∅ == ∀x · ¬P(x)
			 */
			Equal(Cset(bidl, P, _), EmptySet()) ->  {
				if (level2) {
					final Predicate pred = makeUnaryPredicate(NOT, `P);
					result = makeQuantifiedPredicate(FORALL, `bidl, pred);
					trace(predicate, result, "SIMP_SPECIAL_EQUAL_COMPSET");
					return result;
				}
			}

	    }
	    return predicate;
	}
    
    private static boolean isDTConstructor(ExtendedExpression expr) {
    	final IExpressionExtension extension = expr.getExtension();
    	final Object origin = extension.getOrigin();
    	if (!(origin instanceof IDatatype)) {
    		return false;
    	}
    	final IDatatype datatype = (IDatatype) origin;
    	return datatype.isConstructor(extension);
	}
	
	@ProverRule( { "SIMP_SPECIAL_BINTER", "SIMP_SPECIAL_BUNION",
			"SIMP_TYPE_BINTER", "SIMP_TYPE_BUNION","SIMP_MULTI_BINTER",
            "SIMP_MULTI_BUNION", "SIMP_SPECIAL_PLUS", "SIMP_SPECIAL_PROD_1",
            "SIMP_SPECIAL_PROD_0", "SIMP_SPECIAL_PROD_MINUS_EVEN",
            "SIMP_SPECIAL_PROD_MINUS_ODD", "SIMP_SPECIAL_FCOMP",
            "SIMP_SPECIAL_BCOMP", "SIMP_SPECIAL_OVERL", "SIMP_FCOMP_ID_L",
            "SIMP_FCOMP_ID_R",
            "SIMP_TYPE_FCOMP_R", "SIMP_TYPE_FCOMP_L", "SIMP_TYPE_BCOMP_L",
            "SIMP_TYPE_BCOMP_R", "SIMP_TYPE_OVERL_CPROD", "SIMP_TYPE_BCOMP_ID",
            "SIMP_TYPE_FCOMP_ID", "SIMP_FCOMP_ID", "SIMP_BCOMP_ID",
            "SIMP_MULTI_OVERL" })
	@Override
	public Expression rewrite(AssociativeExpression expression) {
		final Expression result;
	    %match (Expression expression) {

	    	/**
             * SIMP_SPECIAL_BINTER
	    	 * Set Theory: S ∩ ... ∩ ∅ ∩ ... ∩ T == ∅
	    	 * SIMP_TYPE_BINTER
             * Set Theory: S ∩ ... ∩ U ∩ ... ∩ T == S ∩ ... ∩ ... ∩ T
             * SIMP_MULTI_BINTER
             * Set Theory: S ∩ ... ∩ T ∩ T ∩ ... ∩ V == S ∩ ... ∩ T ∩ ... ∩ V
	    	 */
	    	BInter(_) -> {
	    		result = simplifyInter(expression, dLib);
	    		trace(expression, result, "SIMP_SPECIAL_BINTER", "SIMP_TYPE_BINTER", "SIMP_MULTI_BINTER");
				return result;
	    	}

	    	/**
	    	 * SIMP_SPECIAL_BUNION
             * Set Theory: S ∪ ... ∪ ∅ ∪ ... ∪ T == S ∪ ... ∪ T
	    	 * SIMP_TYPE_BUNION
             * Set Theory: S ∪ ... ∪ U ∪ ... ∪ T == U
             * SIMP_MULTI_BUNION
             * Set Theory: S ∪ ... ∪ T ∪ T ∪ ... ∪ V == S ∪ ... ∪ T ∪ ... ∪ V
	    	 */
	    	BUnion(_) -> {
	    		result = simplifyUnion(expression, dLib);
	    		trace(expression, result, "SIMP_SPECIAL_BUNION", "SIMP_TYPE_BUNION", "SIMP_MULTI_BUNION");
				return result;
	    	}

	    	/**
	    	 * SIMP_SPECIAL_PLUS
             * Arithmetic 1: E + ... + 0 + ... + F == E + ... + ... + F
	    	 */
	    	Plus(_) -> {
	    		final Expression rewritten = simplifyPlus(expression, dLib);
	    		if (rewritten != expression) {
	    			result = rewritten;
	    			trace(expression, result, "SIMP_SPECIAL_PLUS");
					return result;
				} else if (!level2) {
					// This case has to be considered for backward compatibility
					return expression;
				}
	    	}

	    	/**
	    	 * SIMP_SPECIAL_PROD_1
             * Arithmetic 5: E ∗ ... ∗ 1 ∗ ... ∗ F == E ∗ ... ∗ ... ∗ F
	    	 * SIMP_SPECIAL_PROD_0
             * Arithmetic 6: E ∗ ... ∗ 0 ∗ ... ∗ F == 0
	    	 * SIMP_SPECIAL_PROD_MINUS_EVEN
             * Arithmetic 7: (-E) ∗ (-F) == (E * F)
	    	 * SIMP_SPECIAL_PROD_MINUS_ODD
             * Arithmetic 7.1: (-E) ∗ F == -(E * F)
	    	 */
	    	Mul (_) -> {
	    		final Expression rewritten = simplifyMult(expression, dLib);
	    		if (rewritten != expression) {
	    			result = rewritten;
	    			trace(expression, result, "SIMP_SPECIAL_PROD_1", "SIMP_SPECIAL_PROD_0", "SIMP_SPECIAL_PROD_MINUS_EVEN", "SIMP_SPECIAL_PROD_MINUS_ODD");
					return result;
	    		} else if (!level2) {
	    			// This case has to be considered for backward compatibility
	    			return expression;
	    		}
	    	}
	    	
	    	/**
	    	 * SIMP_SPECIAL_FCOMP
             *    r ; .. ;  ∅ ; .. ; s == ∅
             * SIMP_TYPE_FCOMP_ID
	    	 *    r ; .. ; id ; .. ; s == r ; .. ; s
	    	 * SIMP_FCOMP_ID
	    	 *    r ; .. ; S ◁ id ; T ◁ id ; U ⩤ id ; .. ; s == r ; .. ; ((S ∩ T) ∖ U) ◁ id ; .. ; s
	    	 */
	    	Fcomp (_) -> {
	    		final Expression rewritten = simplifyComp(expression, dLib);
	    		if (rewritten != expression) {
	    			result = rewritten;
	    			trace(expression, result, "SIMP_SPECIAL_FCOMP", "SIMP_TYPE_FCOMP_ID", "SIMP_FCOMP_ID");
   					return result;
	    		}
	    	}
	
	    	/**
	    	 * SIMP_SPECIAL_BCOMP
             *    r ∘ .. ∘  ∅ ∘ .. ∘ s == ∅
             * SIMP_TYPE_BCOMP_ID
	    	 *    r ∘ .. ∘ id ∘ .. ∘ s == r ∘ .. ∘ s
	    	 * SIMP_BCOMP_ID
	    	 */
	    	Bcomp (_) -> {
	    		final Expression rewritten = simplifyComp(expression, dLib);
	    		if (rewritten != expression) {
		    		result = rewritten;
	   	    		trace(expression, result, "SIMP_SPECIAL_BCOMP", "SIMP_TYPE_BCOMP_ID", "SIMP_BCOMP_ID");
	   				return result;
	    		}
	    	}
			
            /**
             * SIMP_SPECIAL_OVERL
			 *    r  ..  ∅  ..  s  ==  r  ..  s
			 * SIMP_TYPE_OVERL_CPROD
			 *    r  ..  Ty  ..  s == Ty  ..  s (where Ty is a type expression)
			 * SIMP_MULTI_OVERL
			 *    r1  ‥  rn == r1  ‥  ri−1  ri+1  ‥  rn 
			 *		(where there is such j that 1 ≤ i < j ≤ n and ri and rj are syntactically equal)
			 */
			Ovr(_) -> {
	    		final Expression rewritten = simplifyOvr(expression, dLib);
	    		if (rewritten != expression) {
	    			result = rewritten;
	    			trace(expression, result, "SIMP_SPECIAL_OVERL", "SIMP_TYPE_OVERL_CPROD", "SIMP_MULTI_OVERL");
	    			return result;
	    		} else if (!level2) {
	    			// This case has to be considered for backward compatibility
	    			return expression;
	    		}
     		}
     		
     		/**
			 * SIMP_FCOMP_ID_L
			 *    (S ◁ id) ; r == S ◁ r
			 */
			Fcomp(eList(DomRes(S, IdGen()), r)) -> {
				if (level2) {
					result = makeBinaryExpression(DOMRES, `S, `r);
					trace(expression, result, "SIMP_FCOMP_ID_L");
					return result;
				}
			}
			
			/**
			 * SIMP_FCOMP_ID_R
			 *    r ; (S ◁ id) == r ▷ S
			 */
			Fcomp(eList(r, DomRes(S, IdGen()))) -> {
				if (level2) {
					result = makeBinaryExpression(RANRES, `r, `S);
					trace(expression, result, "SIMP_FCOMP_ID_R");
					return result;
				}
			}
	    	
	    	/**
	    	 * SIMP_TYPE_FCOMP_R
	    	 *    r ; Ty == dom(r) × Tb (where Ty is a type expression and Ty = Ta × Tb)
	    	 */
	    	Fcomp(eList(r, Ty@Cprod(_, Tb))) -> {
	    		if (level2 && `Ty.isATypeExpression()) {
	    			result = makeBinaryExpression(CPROD,
	    						makeUnaryExpression(KDOM, `r),
	    						`Tb);
	    			trace(expression, result, "SIMP_TYPE_FCOMP_R");
					return result;
	    		}
	    	}
	    	
	    	/**
	    	 * SIMP_TYPE_FCOMP_L
	    	 *    Ty ; r == Ta × ran(r) (where Ty is a type expression and Ty = Ta × Tb)
	    	 */
	    	Fcomp(eList(Ty@Cprod(Ta, _), r)) -> {
	    		if (level2 && `Ty.isATypeExpression()) {
	    			result = makeBinaryExpression(CPROD,
	    						`Ta,
	    						makeUnaryExpression(KRAN, `r));
	    			trace(expression, result, "SIMP_TYPE_FCOMP_L");
					return result;	
	    		}
	    	}
	    	
	    	/**
	    	 * SIMP_TYPE_BCOMP_L
	    	 *    Ty ∘ r == dom(r) × Tb (where Ty is a type expression and Ty = Ta × Tb)
	    	 */
	    	Bcomp(eList(Ty@Cprod(_, Tb), r)) -> {
	    		if (level2 && `Ty.isATypeExpression()) {
	    			result = makeBinaryExpression(CPROD,
	    						makeUnaryExpression(KDOM, `r),
	    						`Tb);
	    			trace(expression, result, "SIMP_TYPE_BCOMP_L");
					return result;	
	    		}
	    	}
	    	
	    	/**
	    	 * SIMP_TYPE_BCOMP_R
	    	 *    r ∘ Ty == Ta × ran(r) (where Ty is a type expression and Ty = Ta × Tb)
	    	 */
	    	Bcomp(eList(r, Ty@Cprod(Ta, _))) -> {
	    		if (level2 && `Ty.isATypeExpression()) {
	    			result = makeBinaryExpression(CPROD,
	    						`Ta,
	    						makeUnaryExpression(KRAN, `r));
	    			trace(expression, result, "SIMP_TYPE_BCOMP_R");
					return result;	
	    		}
	    	}
	    	
	    }
	    return expression;
	}

	@ProverRule( { "SIMP_MULTI_SETMINUS", "SIMP_SPECIAL_SETMINUS_L",
			"SIMP_SPECIAL_SETMINUS_R", "SIMP_TYPE_SETMINUS",
			"SIMP_TYPE_SETMINUS_SETMINUS", "SIMP_MULTI_MINUS",
			"SIMP_SPECIAL_MINUS_R", "SIMP_SPECIAL_MINUS_L", "SIMP_MULTI_DIV",
			"SIMP_SPECIAL_DIV_1", "SIMP_SPECIAL_DIV_0", "SIMP_MULTI_DIV_PROD",
			"SIMP_DIV_MINUS", "SIMP_SPECIAL_EXPN_1_R", "SIMP_SPECIAL_EXPN_0",
			"SIMP_SPECIAL_EXPN_1_L", "SIMP_FUNIMAGE_FUNIMAGE_CONVERSE",
			"SIMP_FUNIMAGE_CONVERSE_FUNIMAGE",
			"SIMP_MULTI_FUNIMAGE_OVERL_SETENUM",
			"SIMP_FUNIMAGE_FUNIMAGE_CONVERSE_SETENUM",
			"SIMP_SPECIAL_RELIMAGE_R", "SIMP_SPECIAL_RELIMAGE_L",
			"SIMP_FUNIMAGE_CPROD", "SIMP_FUNIMAGE_LAMBDA",
			"SIMP_SPECIAL_CPROD_R", "SIMP_SPECIAL_CPROD_L",
			"SIMP_SPECIAL_DOMRES_L", "SIMP_SPECIAL_DOMRES_R",
			"SIMP_TYPE_DOMRES", "SIMP_MULTI_DOMRES_DOM",
			"SIMP_MULTI_DOMRES_RAN", "SIMP_SPECIAL_RANRES_R",
			"SIMP_SPECIAL_RANRES_L", "SIMP_TYPE_RANRES",
			"SIMP_MULTI_RANRES_RAN", "SIMP_MULTI_RANRES_DOM",
			"SIMP_SPECIAL_DOMSUB_L", "SIMP_SPECIAL_DOMSUB_R",
			"SIMP_TYPE_DOMSUB", "SIMP_MULTI_DOMSUB_DOM",
			"SIMP_SPECIAL_RANSUB_R", "SIMP_SPECIAL_RANSUB_L",
			"SIMP_TYPE_RANSUB", "SIMP_MULTI_RANSUB_RAN",
			"SIMP_SPECIAL_DPROD_R", "SIMP_SPECIAL_DPROD_L",
			"SIMP_SPECIAL_PPROD_R", "SIMP_SPECIAL_PPROD_L",
			"SIMP_TYPE_RELIMAGE", "SIMP_MULTI_RELIMAGE_DOM",
			"SIMP_RELIMAGE_ID", "SIMP_MULTI_RELIMAGE_CPROD_SING",
			"SIMP_MULTI_RELIMAGE_SING_MAPSTO",
			"SIMP_MULTI_RELIMAGE_CONVERSE_RANSUB",
			"SIMP_MULTI_RELIMAGE_CONVERSE_RANRES",
			"SIMP_RELIMAGE_CONVERSE_DOMSUB", "SIMP_MULTI_RELIMAGE_DOMSUB",
			"SIMP_SPECIAL_REL_R", "SIMP_SPECIAL_REL_L",
			"SIMP_FUNIMAGE_PRJ1", "SIMP_FUNIMAGE_PRJ2", "SIMP_FUNIMAGE_ID",
			"SIMP_SPECIAL_EQUAL_RELDOMRAN",
			"SIMP_DPROD_CPROD", "SIMP_PPROD_CPROD",
			"SIMP_SPECIAL_MOD_0", "SIMP_SPECIAL_MOD_1", "SIMP_MULTI_MOD",
			"SIMP_LIT_UPTO", "SIMP_MULTI_FUNIMAGE_SETENUM_LL",
			"SIMP_MULTI_FUNIMAGE_SETENUM_LR",
			"SIMP_MULTI_FUNIMAGE_BUNION_SETENUM", "SIMP_DOMRES_DOMRES_ID",
			"SIMP_RANRES_DOMRES_ID", "SIMP_DOMSUB_DOMRES_ID",
			"SIMP_RANSUB_DOMRES_ID", "SIMP_DOMRES_DOMSUB_ID",
			"SIMP_RANRES_DOMSUB_ID", "SIMP_DOMSUB_DOMSUB_ID",
			"SIMP_RANSUB_DOMSUB_ID", "SIMP_RANRES_ID", "SIMP_RANSUB_ID",
			"SIMP_MULTI_DOMSUB_RAN", "SIMP_MULTI_RANSUB_DOM",
			"SIMP_RELIMAGE_DOMRES_ID", "SIMP_RELIMAGE_DOMSUB_ID" } )
	@Override
	public Expression rewrite(BinaryExpression expression) {
		final Expression result;
	    %match (Expression expression) {

			/**
             * SIMP_MULTI_SETMINUS
	    	 * Set Theory 11: S ∖ S == ∅
	    	 */
	    	SetMinus(S, S) -> {
	    		result = makeEmptySet(`S.getType());
	    		trace(expression, result, "SIMP_MULTI_SETMINUS");
	    		return result;
	    	}

			/**
	    	 * SIMP_SPECIAL_SETMINUS_L
             * Set Theory: ∅ ∖ S == ∅
	    	 */
	    	SetMinus(e@EmptySet(), _) -> {
				result = `e;
	    		trace(expression, result, "SIMP_SPECIAL_SETMINUS_L");
	    		return result;
	    	}

			/**
	    	 * SIMP_SPECIAL_SETMINUS_R
             * Set Theory: S ∖ ∅ == S
	    	 */
	    	SetMinus(S, EmptySet()) -> {
				result = `S;
	    		trace(expression, result, "SIMP_SPECIAL_SETMINUS_R");
	    		return result;
	    	}

			/**
	    	 * SIMP_TYPE_SETMINUS
             * Set Theory: S ∖ U == ∅
	    	 */
	    	SetMinus(_, T) -> {
				if (`T.isATypeExpression()) {
					result = makeEmptySet(`T.getType());
		    		trace(expression, result, "SIMP_TYPE_SETMINUS");
		    		return result;
				}
	    	}
	    	
			/**
	    	 * SIMP_TYPE_SETMINUS_SETMINUS
             * Set Theory: U ∖ (U ∖ S) == S
	    	 */
			SetMinus(U, SetMinus(U, S)) -> {
				if (`U.isATypeExpression()) {
					result = `S;
		    		trace(expression, result, "SIMP_TYPE_SETMINUS_SETMINUS");
		    		return result;
				}
			}
			
			/**
			 * SIMP_MULTI_MINUS
             * Arithmetic: E − E == 0
			 */
			Minus(E, E) -> {
				result = number0;
	    		trace(expression, result, "SIMP_MULTI_MINUS");
	    		return result;
			}

			/**
	    	 * SIMP_SPECIAL_MINUS_R
             * Arithmetic: E − 0 == E
	    	 * SIMP_SPECIAL_MINUS_L
             * Arithmetic: 0 − E == −E
	    	 */
	    	Minus(E, F) -> {
	    		if (`F.equals(number0)) {
					result = `E;
		    		trace(expression, result, "SIMP_SPECIAL_MINUS_R");
		    		return result;
				} else if (`E.equals(number0)) {
					result = makeUnaryExpression(UNMINUS, `F);
		    		trace(expression, result, "SIMP_SPECIAL_MINUS_L");
		    		return result;
				}
				return expression;
	    	}

			/**
	    	 * SIMP_MULTI_DIV
             * Arithmetic: E ÷ E = 1
	    	 */
	    	Div(E, E) -> {
	    		result = dLib.makeIntegerLiteral(1);
	    		trace(expression, result, "SIMP_MULTI_DIV");
	    		return result;
	    	}

			/**
			 * SIMP_SPECIAL_DIV_1
             * Arithmetic: E ÷ 1 = E
			 */
			Div(E, IntegerLiteral(F)) -> {
				if (`F.equals(ONE)) {
					result = `E;
		    		trace(expression, result, "SIMP_SPECIAL_DIV_1");
		    		return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_DIV_0
             * Arithmetic: 0 ÷ E = 0
			 */
			Div(IntegerLiteral(F), _) -> {
				if (`F.equals(ZERO)) {
					result = number0;
		    		trace(expression, result, "SIMP_SPECIAL_DIV_0");
		    		return result;
				}
			}

			/**
	    	 * SIMP_MULTI_DIV_PROD 
             * Arithmetic: (X ∗ ... ∗ E ∗ ... ∗ Y) ÷ E == X ∗ ... ∗ Y
	    	 */
	    	Div(mul@Mul(eList(_*, E, _*)), E) -> {
	    		result = removeChild((AssociativeExpression) `mul, `E);
	    		trace(expression, result, "SIMP_MULTI_DIV_PROD");
	    		return result;
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(UnMinus(E), UnMinus(F)) -> {
	    		result = du.getFaction(`E, `F);
	    		trace(expression, result, "SIMP_DIV_MINUS");
	    		return result;
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(UnMinus(E), IntegerLiteral(F)) -> {
	    		result = du.getFaction(`expression, `E, `F);
	    		trace(expression, result, "SIMP_DIV_MINUS");
	    		return result;
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic 10: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(IntegerLiteral(E), UnMinus(F)) -> {
	    		result = du.getFaction(`expression, `E, `F);
	    		trace(expression, result, "SIMP_DIV_MINUS");
	    		return result;
	    	}

			/**
	    	 * SIMP_SPECIAL_DIV_1
             * Arithmetic: E ÷ 1 = E
	    	 * SIMP_SPECIAL_DIV_0
             * Arithmetic: 0 ÷ E = 0
	    	 * SIMP_DIV_MINUS
             * Arithmetic: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(IntegerLiteral(E), IntegerLiteral(F)) -> {
	    		result = du.getFaction(`expression, `E, `F);
	    		trace(expression, result, "SIMP_SPECIAL_DIV_1", "SIMP_SPECIAL_DIV_0", "SIMP_DIV_MINUS");
	    		return result;
	    	}

			/**
	    	 * SIMP_SPECIAL_EXPN_1_R
             * Arithmetic: E^1 == E
	    	 * SIMP_SPECIAL_EXPN_0
             * Arithmetic: E^0 == 1
	    	 * SIMP_SPECIAL_EXPN_1_L
             * Arithmetic: 1^E == 1
	    	 */
	    	Expn (E, F) -> {
   				if (`F.equals(number1)) {
					result = `E;
		    		trace(expression, result, "SIMP_SPECIAL_EXPN_1_R");
		    		return result;
				} else if (`F.equals(number0)) {
					result = number1;
		    		trace(expression, result, "SIMP_SPECIAL_EXPN_0");
		    		return result;
				} else if (`E.equals(number1)) {
					result = number1;
		    		trace(expression, result, "SIMP_SPECIAL_EXPN_1_L");
		    		return result;
				}
				return expression;
	    	}
	    	
	    	/**
	    	 * SIMP_FUNIMAGE_FUNIMAGE_CONVERSE
             * Set Theory: f(f∼(E)) = E
	    	 */
	    	FunImage(f, FunImage(Converse(f), E)) -> {
				result = `E;
	    		trace(expression, result, "SIMP_FUNIMAGE_FUNIMAGE_CONVERSE");
	    		return result;
	    	}

	    	/**
	    	 * SIMP_FUNIMAGE_CONVERSE_FUNIMAGE
	    	 * Set Theory: f∼(f(E)) = E
	    	 */
	    	FunImage(Converse(f), FunImage(f, E)) -> {
				result = `E;
	    		trace(expression, result, "SIMP_FUNIMAGE_CONVERSE_FUNIMAGE");
	    		return result;
	    	}

	    	/**
	    	 * SIMP_MULTI_FUNIMAGE_OVERL_SETENUM
             * Set Theory 16: (f  {E↦ F})(E) == F
	    	 */
	    	FunImage(Ovr(eList(_*, SetExtension(ms@eList(_*, Mapsto(X, Y), _*)))), X) -> {
	    		if (level2) {
	    			result = `Y;
	    			trace(expression, result, "SIMP_MULTI_FUNIMAGE_OVERL_SETENUM");
	    			return result;
	    		} else if (`ms.length == 1) {
	    			// keeping this version for backward compatibility
	    			result = `Y;
	    			trace(expression, result, "SIMP_MULTI_FUNIMAGE_OVERL_SETENUM");
	    			return result;
	    		}
	    	}

	    	/**
	    	 * SIMP_FUNIMAGE_FUNIMAGE_CONVERSE_SETENUM
             * Set Theory: {x ↦ a, ..., y ↦ b}({a ↦ x, ..., b ↦ y}(E)) == E
	    	 */
	    	FunImage(SetExtension(children1), FunImage(SetExtension(children2), E)) -> {
				if (`children1.length != `children2.length)
					return expression;
				for (int i = 0; i < `children1.length; ++i) {
					Expression map1 = `children1[i];
					Expression map2 = `children2[i];
					if (!(Lib.isMapping(map1) && Lib.isMapping(map2)))
						return expression;	
					
					BinaryExpression bExp1 = (BinaryExpression) map1;
					BinaryExpression bExp2 = (BinaryExpression) map2;
					
					if (!(bExp1.getRight().equals(bExp2.getLeft()) &&
							 bExp2.getRight().equals(bExp1.getLeft())))
						return expression;
				}
				result = `E;
	    		trace(expression, result, "SIMP_FUNIMAGE_FUNIMAGE_CONVERSE_SETENUM");
	    		return result;
	    	}

			/**
			 * SIMP_SPECIAL_RELIMAGE_R
             * Set Theory: r[∅] == ∅
			 */
			RelImage(r, EmptySet()) -> {
				result = makeEmptySet(ff.makePowerSetType(Lib.getRangeType(`r)));
	    		trace(expression, result, "SIMP_SPECIAL_RELIMAGE_R");
	    		return result;
			}

			/**
			 * SIMP_SPECIAL_RELIMAGE_L
             * Set Theory: ∅[A] == ∅
			 */
			RelImage(r@EmptySet(), _) -> {
				result = makeEmptySet(ff.makePowerSetType(Lib.getRangeType(`r)));
	    		trace(expression, result, "SIMP_SPECIAL_RELIMAGE_L");
	    		return result;
			}
			
			/**
			 * SIMP_TYPE_RELIMAGE
			 *    r[Ty] == ran(r) (where Ty is a type expression)
			 */
			RelImage(r, Ty) -> {
				if (level2 && `Ty.isATypeExpression()) {
					result = makeUnaryExpression(KRAN, `r);
					trace(expression, result, "SIMP_TYPE_RELIMAGE");
	    			return result;
				}
			}
			
			/**
			 * SIMP_MULTI_RELIMAGE_DOM
			 *    r[dom(r)] == ran(r)
			 */
			RelImage(r, Dom(r)) -> {
				if (level2) {
					result = makeUnaryExpression(KRAN, `r);
					trace(expression, result, "SIMP_MULTI_RELIMAGE_DOM");
	    			return result;
				}
			}
			
			/**
			 * SIMP_RELIMAGE_ID
			 *    id[T] == T
			 */
			RelImage(IdGen(), T) -> {
				if (level2) {
					result = `T;
					trace(expression, result, "SIMP_RELIMAGE_ID");
	    			return result;
				}
			}
			
			/**
			 * SIMP_MULTI_RELIMAGE_CPROD_SING
			 *    ({E}×S)[{E}] == S (where E is a single expression)
			 */
			RelImage(Cprod(SetExtension(eList(E)), S), SetExtension(eList(E))) -> {
				if (level2) {
					result = `S;
					trace(expression, result, "SIMP_MULTI_RELIMAGE_CPROD_SING");
	    			return result;
				}
			}
			
			/**
			 * SIMP_MULTI_RELIMAGE_SING_MAPSTO
			 *    {E ↦ F}[{E}] == {F} (where E is a single expression)
			 */
			RelImage(SetExtension(eList(Mapsto(E, F))), SetExtension(eList(E))) -> {
				if (level2) {
					result = makeSetExtension(`F);
					trace(expression, result, "SIMP_MULTI_RELIMAGE_SING_MAPSTO");
	    			return result;
				}
			}
			
			/**
			 * SIMP_MULTI_RELIMAGE_CONVERSE_RANSUB
			 *    (r ⩥ S)∼[S] == ∅
			 */
			RelImage(Converse(RanSub(_, S)), S) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_MULTI_RELIMAGE_CONVERSE_RANSUB");
	    			return result;
				}
			}
			
			/**
			 * SIMP_MULTI_RELIMAGE_CONVERSE_RANRES
			 *    (r ▷ S)∼[S] == r∼[S]
			 */
			RelImage(Converse(RanRes(r, S)), S) -> {
				if (level2) {
					result = makeBinaryExpression(RELIMAGE,
								makeUnaryExpression(CONVERSE, `r),
								`S); 
					trace(expression, result, "SIMP_MULTI_RELIMAGE_CONVERSE_RANRES");
	    			return result;
				}
			}
			
			/**
			 * SIMP_RELIMAGE_CONVERSE_DOMSUB
			 *    (S ⩤ r)∼[T] == r∼[T]∖S
			 */
			RelImage(Converse(DomSub(S, r)), T) -> {
				if (level2) {
					result = makeBinaryExpression(SETMINUS,
								makeBinaryExpression(RELIMAGE,
									makeUnaryExpression(CONVERSE, `r),
									`T), `S); 
					trace(expression, result, "SIMP_RELIMAGE_CONVERSE_DOMSUB");
	    			return result;
				}
			}
			
			/**
			 * SIMP_MULTI_RELIMAGE_DOMSUB
			 *    (S ⩤ r)[S] == ∅
			 */
			RelImage(DomSub(S, _), S) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_MULTI_RELIMAGE_DOMSUB");
	    			return result;
				}
			}
			
			/**
			 * SIMP_FUNIMAGE_CPROD
             * Set Theory: (S × {E})(x) == E
			 */
			FunImage(Cprod(_, SetExtension(eList(E))), _) -> {
				result = `E;
		        trace(expression, result, "SIMP_FUNIMAGE_CPROD");
    	        return result;
			}

            /**
             * SIMP_FUNIMAGE_LAMBDA
             *
             */
            FunImage(Cset(_,_,Mapsto(_,_)),_) -> {
                final Expression instance = LambdaComputer.rewrite(expression, ff);
                if (instance != null) {
                	result = instance;
                	trace(expression, result, "SIMP_FUNIMAGE_LAMBDA");
                	return result;
                }
            }
            
            /**
			 * SIMP_SPECIAL_CPROD_R
			 *    S × ∅ == ∅
			 */
			Cprod(_, EmptySet()) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_SPECIAL_CPROD_R");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_CPROD_L
			 *    ∅ × S == ∅
			 */
			Cprod(EmptySet(), _) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_SPECIAL_CPROD_L");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_DOMRES_L
			 *    ∅ ◁ r == ∅
			 */
			DomRes(EmptySet(), _) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_SPECIAL_DOMRES_L");
					return result;
				}
			}
			 
			/**
			 * SIMP_SPECIAL_DOMRES_R
			 *    S ◁ ∅ == ∅
			 */
			DomRes(_, empty@EmptySet()) -> {
				if (level2) {
					result = `empty;
					trace(expression, result, "SIMP_SPECIAL_DOMRES_R");
					return result;
				}
			}
			
			/**
			 * SIMP_TYPE_DOMRES
			 *    Ty ◁ r == r (where Ty is a type expression)
			 */
			DomRes(Ty, r) -> {
				if (level2 && `Ty.isATypeExpression()) {
					result = `r;
					trace(expression, result, "SIMP_TYPE_DOMRES");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_DOMRES_DOM
			 *    dom(r) ◁ r == r
			 */
			DomRes(Dom(r), r) -> {
				if (level2) {
					result = `r;
					trace(expression, result, "SIMP_MULTI_DOMRES_DOM");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_DOMRES_RAN
			 *    ran(r) ◁ r∼ == r∼
			 */
			DomRes(Ran(r), conv@Converse(r)) -> {
				if (level2) {
					result = `conv;
					trace(expression, result, "SIMP_MULTI_DOMRES_RAN");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_RANRES_R
			 *    r ▷ ∅ == ∅
			 */
			RanRes(_, EmptySet()) ->  {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_SPECIAL_RANRES_R");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_RANRES_L
			 *    ∅ ▷ S == ∅
			 */
			 RanRes(empty@EmptySet(), _) -> {
			 	if (level2) {
			 		result = `empty;
			 		trace(expression, result, "SIMP_SPECIAL_RANRES_L");
					return result;
			 	}
			 }
			
			/**
			 * SIMP_TYPE_RANRES
			 *    r ▷ Ty == r (where Ty is a type expression)
			 */
			RanRes(r, Ty) -> {
				if (level2 && `Ty.isATypeExpression()) {
					result = `r;
					trace(expression, result, "SIMP_TYPE_RANRES");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_RANRES_RAN
			 *    r ▷ ran(r) == r
			 */
			RanRes(r, Ran(r)) -> {
				if (level2) {
					result = `r;
					trace(expression, result, "SIMP_MULTI_RANRES_RAN");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_RANRES_DOM
			 *    r∼ ▷ dom(r) == r∼
			 */
			RanRes(conv@Converse(r), Dom(r)) -> {
				if (level2) {
					result = `conv;
					trace(expression, result, "SIMP_MULTI_RANRES_DOM");
					return result;
				}
			}	
			
			/**
			 * SIMP_SPECIAL_DOMSUB_L
			 *    ∅ ⩤ r == r
			 */
			 DomSub(EmptySet(), r) -> {
			 	if (level2) {
			 		result = `r;
			 		trace(expression, result, "SIMP_SPECIAL_DOMSUB_L");
					return result;
			 	}
			 }
			 
			 /**
			  * SIMP_SPECIAL_DOMSUB_R
			  *    S ⩤ ∅ == ∅
			  */
			 DomSub(_, empty@EmptySet()) -> {
			 	if (level2) {
			 		result = `empty;
			 		trace(expression, result, "SIMP_SPECIAL_DOMSUB_R");
					return result;			 		
			 	}
			 }
			 
			 /**
			  * SIMP_TYPE_DOMSUB
			  *    Ty ⩤ r == ∅ (where Ty is a type expression)
			  */
			 DomSub(Ty, _) -> {
			 	if (level2 && `Ty.isATypeExpression()) {
			 		result = makeEmptySet(expression.getType());
			 		trace(expression, result, "SIMP_TYPE_DOMSUB");
					return result;		
			 	}
			 }
			 
			 /**
			  * SIMP_MULTI_DOMSUB_DOM
			  *    dom(r) ⩤ r == ∅
			  */
			 DomSub(Dom(r), r) -> {
			 	if (level2) {
			 		result = makeEmptySet(expression.getType());
			 		trace(expression, result, "SIMP_MULTI_DOMSUB_DOM");
					return result;	
			 	}
			 }
			 
			/**
			 * SIMP_SPECIAL_RANSUB_R
			 *    r ⩥ ∅ == r
			 */
			RanSub(r, EmptySet()) -> {
				if (level2) {
					result = `r;
					trace(expression, result, "SIMP_SPECIAL_RANSUB_R");
					return result;	
				}
			}
			
			/**
			 * SIMP_SPECIAL_RANSUB_L
			 *    ∅ ⩥ S == ∅
			 */
			RanSub(empty@EmptySet(), _) -> {
				if (level2) {
					result = `empty;
					trace(expression, result, "SIMP_SPECIAL_RANSUB_L");
					return result;	
				}
			}
			
			/**
			 * SIMP_TYPE_RANSUB
			 *    r ⩥ Ty == ∅ (where Ty is a type expression)
			 */
			RanSub(_, Ty) -> {
				if (level2 && `Ty.isATypeExpression()) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_TYPE_RANSUB");
					return result;	
				}
			}
			
			/**
			 * SIMP_MULTI_RANSUB_RAN
			 *    r ⩥ ran(r) == ∅
			 */
			RanSub(r, Ran(r)) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_MULTI_RANSUB_RAN");
					return result;						
				}
			}
			
			/**
			 * SIMP_SPECIAL_DPROD_R
			 *    r ⊗ ∅ == ∅
			 */
			Dprod(_, EmptySet()) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_SPECIAL_DPROD_R");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_DPROD_L
			 *    ∅ ⊗ r == ∅
			 */
			Dprod(EmptySet(), _) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_SPECIAL_DPROD_L");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_PPROD_R
			 *    r ∥ ∅ == ∅ (parallel product ||)
			 */
			Pprod(_, EmptySet()) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_SPECIAL_PPROD_R");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_PPROD_L
			 *    ∅ ∥ r == ∅ (parallel product ||)
			 */
			Pprod(EmptySet(), _) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_SPECIAL_PPROD_L");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_REL_R
			 *    S ↔ ∅ == {∅}
			 *    S  ∅ == {∅} surjective relation <->>
			 *    S ⇸ ∅ == {∅}
			 *    S ⤔ ∅ == {∅}
			 *    S ⤀ ∅ == {∅}
			 */
			(Rel | Srel | Pfun | Pinj | Psur)(_, EmptySet()) -> {
				if (level2) {			
					result = makeSetExtension(makeEmptySet(expression.getType().getBaseType()));
					trace(expression, result, "SIMP_SPECIAL_REL_R");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_REL_L
			 *    ∅ ↔ S == {∅}
			 *    ∅  S == {∅} total relation <<->
			 *    ∅ ⇸ S == {∅}
			 *    ∅ → S == {∅}
			 *    ∅ ⤔ S == {∅}
			 *    ∅ ↣ S == {∅}
			 */
			(Rel | Trel | Pfun | Tfun | Pinj | Tinj)(EmptySet(), _) -> {
				if (level2) {
					result = makeSetExtension(makeEmptySet(expression.getType().getBaseType()));
					trace(expression, result, "SIMP_SPECIAL_REL_L");
					return result;
				}
			}
			
			/**
			 * SIMP_FUNIMAGE_PRJ1
			 *    prj1(E ↦ F) == E
			 */
			FunImage(Prj1Gen(), Mapsto(E, _)) -> {
				if (level2) {
					result = `E;
					trace(expression, result, "SIMP_FUNIMAGE_PRJ1");
					return result;
				}
			}

			/**
			 * SIMP_FUNIMAGE_PRJ2
			 *    prj2(E ↦ F) == F
			 */
			FunImage(Prj2Gen(), Mapsto(_, F)) -> {
				if (level2) {
					result = `F;
					trace(expression, result, "SIMP_FUNIMAGE_PRJ2");
					return result;
				}
			}
			 
			/**
			 * SIMP_FUNIMAGE_ID
			 *    id(x) = x
			 */
			FunImage(IdGen(), x) -> {
				if (level2) {
					result = `x;
					trace(expression, result, "SIMP_FUNIMAGE_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_EQUAL_RELDOMRAN
			 *    ∅  ∅
			 *    ∅ ↠ ∅
			 *    ∅ ⤖ ∅
			 */
			(Strel | Tsur | Tbij)(EmptySet(), EmptySet()) -> {
				if (level2) {
					result = makeSetExtension(makeEmptySet(expression.getType().getBaseType()));
					trace(expression, result, "SIMP_SPECIAL_EQUAL_RELDOMRAN");
					return result;
				}
			}
			
			/**
			 * SIMP_DPROD_CPROD
			 *    (S × T) ⊗ (U × V) == (S ∩ U) × (T × V)
			 */
			Dprod(Cprod(S, T), Cprod(U, V)) -> {
				if (level2) {
					result = makeBinaryExpression(CPROD,
								makeAssociativeExpression(BINTER, `S, `U),
								makeBinaryExpression(CPROD, `T, `V));
					trace(expression, result, "SIMP_DPROD_CPROD");
					return result;
				}
			}
			
			/**
			 * SIMP_PPROD_CPROD
			 *    (S × T) ∥ (U × V) == (S × U) × (T × V)
			 */
			Pprod(Cprod(S, T), Cprod(U, V)) -> {
				if (level2) {
					result = makeBinaryExpression(CPROD,
								makeBinaryExpression(CPROD, `S, `U),
								makeBinaryExpression(CPROD, `T, `V));
					trace(expression, result, "SIMP_PPROD_CPROD");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_MOD_0
			 *    0 mod E == 0
			 */
			Mod(zero@IntegerLiteral(z), _) -> {
				if (level2 && `z.equals(ZERO)) {
					result = `zero;
					trace(expression, result, "SIMP_SPECIAL_MOD_0");
					return result;
				}
			}		
			
			/**
			 * SIMP_SPECIAL_MOD_1
			 *    E mod 1 == 0
			 */
			Mod(_, IntegerLiteral(i)) -> {
				if (level2 && `i.equals(ONE)) {
					result = number0;
					trace(expression, result, "SIMP_SPECIAL_MOD_1");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_MOD
			 *    E mod E == 0
			 */
			Mod(E, E) -> {
				if (level2) {
					result = number0;
					trace(expression, result, "SIMP_MULTI_MOD");
					return result;
				}
			}
			
			/**
			 * SIMP_LIT_UPTO
			 *    i‥j == ∅ (where i and j are literals and j < i)
			 */
			UpTo(IntegerLiteral(i), IntegerLiteral(j)) -> {
				if (level2 && `i.compareTo(`j) > 0) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_LIT_UPTO");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_FUNIMAGE_SETENUM_LL
			 *    {A ↦ E, .. , B ↦ E}(x) == E
			 */
			FunImage(SetExtension(members@eList(Mapsto(_, E), _*)), _) -> {
				if (level2) {
					final Expression rewritten = simplifySetextOfMapsto(`members, `E);
					if (rewritten != null) {
						result = rewritten;
						trace(expression, result, "SIMP_MULTI_FUNIMAGE_SETENUM_LL");
						return result;
					}
				}
			}
			
			/**
			 * SIMP_MULTI_FUNIMAGE_SETENUM_LR
			 *    {E, .. , x ↦ y, .. , F}(x) == y
			 */
			FunImage(SetExtension(eList(_*, Mapsto(x, y), _*)), x) -> {
				if (level2) {
					result = `y;
					trace(expression, result, "SIMP_MULTI_FUNIMAGE_SETENUM_LR");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_FUNIMAGE_BUNION_SETENUM
			 *    {r ∪ .. ∪ {E, .. , x ↦ y, .. , F})(x) == y
			 */
			FunImage(BUnion(eList(_*, SetExtension(eList(_*, Mapsto(x, y), _*)), _*)), x) -> {
				if (level2) {
					result = `y;
					trace(expression, result, "SIMP_MULTI_FUNIMAGE_BUNION_SETENUM");
					return result;
				}
			}
			
			/**
			 * SIMP_DOMRES_DOMRES_ID
			 *    S ◁ (T ◁ id) == (S ∩ T) ◁ id
			 */
			DomRes(S, DomRes(T, id@IdGen())) -> {
				if (level2) {
					result = makeBinaryExpression(DOMRES,
								makeAssociativeExpression(BINTER, `S, `T), `id);
					trace(expression, result, "SIMP_DOMRES_DOMRES_ID");
					return result;
				}
			}

			/**
			 * SIMP_RANRES_DOMRES_ID
			 *    (S ◁ id) ▷ T == (S ∩ T) ◁ id
			 */
			RanRes(DomRes(S, id@IdGen()), T) -> {
				if (level2) {
					result = makeBinaryExpression(DOMRES,
								makeAssociativeExpression(BINTER, `S, `T), `id);
					trace(expression, result, "SIMP_RANRES_DOMRES_ID");
					return result;
				}
			}

			/**
			 * SIMP_DOMSUB_DOMRES_ID
			 *    S ⩤ (T ◁ id) == (T ∖ S) ◁ id
			 */
			DomSub(S, DomRes(T, id@IdGen())) -> {
				if (level2) {
					result = makeBinaryExpression(DOMRES,
								makeBinaryExpression(SETMINUS, `T, `S), `id);
					trace(expression, result, "SIMP_DOMSUB_DOMRES_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_RANSUB_DOMRES_ID
			 *    (S ◁ id) ⩥ T == (S ∖ T) ◁ id
			 */
			RanSub(DomRes(S, id@IdGen()), T) -> {
				if (level2) {
					result = makeBinaryExpression(DOMRES,
								makeBinaryExpression(SETMINUS, `S, `T), `id);
					trace(expression, result, "SIMP_RANSUB_DOMRES_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_DOMRES_DOMSUB_ID
			 *    S ◁ (T ⩤ id) == (S ∖ T) ◁ id
			 */
			DomRes(S, DomSub(T, id@IdGen())) -> {
				if (level2) {
					result = makeBinaryExpression(DOMRES,
								makeBinaryExpression(SETMINUS, `S, `T), `id);
					trace(expression, result, "SIMP_DOMRES_DOMSUB_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_RANRES_DOMSUB_ID
			 *    (S ⩤ id) ▷ T == (T ∖ S) ◁ id
			 */
			RanRes(DomSub(S, id@IdGen()), T) -> {
				if (level2) {
					result = makeBinaryExpression(DOMRES,
								makeBinaryExpression(SETMINUS, `T, `S), `id);
					trace(expression, result, "SIMP_RANRES_DOMSUB_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_DOMSUB_DOMSUB_ID
			 *    S ⩤ (T ⩤ id) == (S ∪ T) ⩤ id
			 */
			DomSub(S, DomSub(T, id@IdGen())) -> {
				if (level2) {
					result = makeBinaryExpression(DOMSUB,
								makeAssociativeExpression(BUNION, `S, `T), `id);
					trace(expression, result, "SIMP_DOMSUB_DOMSUB_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_RANSUB_DOMSUB_ID
			 *    (S ⩤ id) ⩥ T == (S ∪ T) ⩤ id
			 */
			RanSub(DomSub(S, id@IdGen()), T) -> {
				if (level2) {
					result = makeBinaryExpression(DOMSUB,
								makeAssociativeExpression(BUNION, `S, `T), `id);
					trace(expression, result, "SIMP_RANSUB_DOMSUB_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_RANRES_ID
			 *    id ▷ S == S ◁ id
			 */
			RanRes(id@IdGen(), S) -> {
				if (level2) {
					result = makeBinaryExpression(DOMRES, `S, `id);
					trace(expression, result, "SIMP_RANRES_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_RANSUB_ID
			 *    id ⩥ S == S ⩤ id
			 */
			RanSub(id@IdGen(), S) -> {
				if (level2) {
					result = makeBinaryExpression(DOMSUB, `S, `id);
					trace(expression, result, "SIMP_RANSUB_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_DOMSUB_RAN
			 *    ran(r) ⩤ r∼ == ∅
			 */
			DomSub(Ran(r), cr@Converse(r)) -> {
				if (level2) {
					result = makeEmptySet(`cr.getType());
					trace(expression, result, "SIMP_MULTI_DOMSUB_RAN");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_RANSUB_DOM
			 *    r∼ ⩥ dom(r) == ∅
			 */
			RanSub(cr@Converse(r), Dom(r)) -> {
				if (level2) {
					result = makeEmptySet(`cr.getType());
					trace(expression, result, "SIMP_MULTI_RANSUB_DOM");
					return result;
				}
			}
			
			/**
			 * SIMP_RELIMAGE_DOMRES_ID
			 *    (S ◁ id)[T] == S ∩ T
			 */
			RelImage(DomRes(S, IdGen()), T) -> {
				if (level2) {
					result = makeAssociativeExpression(BINTER, `S, `T);
					trace(expression, result, "SIMP_RELIMAGE_DOMRES_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_RELIMAGE_DOMSUB_ID
			 *    (S ⩤ id)[T] == T ∖ S
			 */
			RelImage(DomSub(S, IdGen()), T) -> {
				if (level2) {
					result = makeBinaryExpression(SETMINUS, `T, `S);
					trace(expression, result, "SIMP_RELIMAGE_DOMSUB_ID");
					return result;
				}
			}
			
		}
	    return expression;
	}

	@ProverRule( { "SIMP_CONVERSE_CONVERSE", "SIMP_CONVERSE_SETENUM",
			"SIMP_DOM_SETENUM", "SIMP_RAN_SETENUM", "SIMP_MINUS_MINUS",
			"SIMP_SPECIAL_CARD", "SIMP_CARD_SING", "SIMP_CARD_POW",
			"SIMP_CARD_BUNION", "SIMP_SPECIAL_DOM", "SIMP_SPECIAL_RAN",
			"SIMP_SPECIAL_POW", "SIMP_SPECIAL_POW1", "SIMP_DOM_CONVERSE",
			"SIMP_RAN_CONVERSE", "SIMP_CONVERSE_ID", "SIMP_SPECIAL_CONVERSE",
			"SIMP_MULTI_DOM_CPROD", "SIMP_MULTI_RAN_CPROD",
			"SIMP_KUNION_POW", "SIMP_KUNION_POW1", "SIMP_SPECIAL_KUNION",
			"SIMP_SPECIAL_KINTER", "SIMP_KINTER_POW",
			"SIMP_DOM_ID", "SIMP_RAN_ID", "SIMP_DOM_PRJ1", "SIMP_DOM_PRJ2",
			"SIMP_RAN_PRJ1", "SIMP_RAN_PRJ2", "SIMP_TYPE_DOM",
			"SIMP_TYPE_RAN", "SIMP_MIN_SING", "SIMP_MAX_SING",
			"SIMP_MIN_NATURAL", "SIMP_MIN_NATURAL1", "SIMP_MIN_UPTO",
			"SIMP_MAX_UPTO", "SIMP_CARD_CONVERSE", "SIMP_CARD_ID_DOMRES",
			"SIMP_CONVERSE_CPROD", "SIMP_CONVERSE_COMPSET",
			"SIMP_DOM_LAMBDA", "SIMP_RAN_LAMBDA", "SIMP_MIN_BUNION_SING",
			"SIMP_MAX_BUNION_SING", "SIMP_LIT_MIN", "SIMP_LIT_MAX",
			 "SIMP_CARD_ID", "SIMP_CARD_PRJ1", "SIMP_CARD_PRJ2",
			 "SIMP_CARD_PRJ1_DOMRES", "SIMP_CARD_PRJ2_DOMRES",
			 "SIMP_CARD_LAMBDA" })
	@Override
	public Expression rewrite(UnaryExpression expression) {
		final Expression result;
	    %match (Expression expression) {

			/**
             * SIMP_CONVERSE_CONVERSE
	    	 * Set Theory 14: r∼∼ == r
	    	 */
	    	Converse(Converse(r)) -> {
	    		result = `r;
	    		trace(expression, result, "SIMP_CONVERSE_CONVERSE");
	    		return result;
	    	}

			/**
             * SIMP_CONVERSE_SETENUM
	    	 * Set Theory: {x ↦ a, ..., y ↦ b}∼ == {a ↦ x, ..., b ↦ y}
	    	 */
	    	Converse(SetExtension(members)) -> {
	    		final Expression rewritten = convertSetextOfMapsto(`members);
	    		if (rewritten != null) {
	    			result = rewritten;
	    			trace(expression, result, "SIMP_CONVERSE_SETENUM");
	    			return result;
	    		} else if (!level2) {
	    			// This case has to be considered for backward compatibility
	    			return expression;
	    		}
	    	}

			/**
             * SIMP_DOM_SETENUM
	    	 * Set Theory 15: dom(x ↦ a, ..., y ↦ b) = {x, ..., y} 
	    	 *                (Also remove duplicate in the resulting set) 
	    	 */
	    	Dom(SetExtension(members)) -> {
   				Collection<Expression> domain = new LinkedHashSet<Expression>();

				for (Expression member : `members) {
					if (member.getTag() == MAPSTO) {
						BinaryExpression bExp = (BinaryExpression) member;
						domain.add(bExp.getLeft());
					} else {
						return expression;
					}
				}

				result = makeSetExtension(domain);
	    		trace(expression, result, "SIMP_DOM_SETENUM");
	    		return result;
	    	}
		
			/**
             * SIMP_RAN_SETENUM
	    	 * Set Theory 16: ran(x ↦ a, ..., y ↦ b) = {a, ..., b}
	    	 */
	    	Ran(SetExtension(members)) -> {
	    		Collection<Expression> range = new LinkedHashSet<Expression>();

				for (Expression member : `members) {
					if (member.getTag() == MAPSTO) {
						BinaryExpression bExp = (BinaryExpression) member;
						range.add(bExp.getRight());
					} else {
						return expression;
					}
				}

				result = makeSetExtension(range);
	    		trace(expression, result, "SIMP_RAN_SETENUM");
	    		return result;
	    	}

			/**
	    	 * SIMP_MINUS_MINUS
             * Arithmetic 4: −(−E) = E
	    	 */
	    	UnMinus(UnMinus(E)) -> {
	    		result = `E;
	    		trace(expression, result, "SIMP_MINUS_MINUS");
	    		return result;
	    	}
			
			/**
             * SIMP_SPECIAL_CARD
	    	 * Cardinality: card(∅) == 0
	    	 */
			Card(EmptySet()) -> {
				result = number0;
	    		trace(expression, result, "SIMP_SPECIAL_CARD");
	    		return result;
			}

			/**
             * SIMP_CARD_SING
	    	 * Cardinality: card({E}) == 1
	    	 */
			Card(SetExtension(eList(_))) -> {
				result = number1;
	    		trace(expression, result, "SIMP_CARD_SING");
	    		return result;
			}

			/**
             * SIMP_CARD_POW
	    	 * Cardinality: card(ℙ(S)) == 2^(card(S))
	    	 */
			Card(Pow(S)) -> {
				Expression cardS = makeCard(`S);
				result = makeBinaryExpression(EXPN, number2, cardS);
	    		trace(expression, result, "SIMP_CARD_POW");
	    		return result;
			}
			
			/**
             * SIMP_CARD_BUNION
	    	 * Cardinality:    card(S(1) ∪ ... ∪ S(n))
	    	 *               = card(S(1)) + ...  + card(S(n))
             *                 − (card(S(1) ∩ S(2)) + ... + card(S(n−1) ∩ S(n)))
             *                 + (card(S(1) ∩ S(2) ∩ S(3)) + ... + card(S(n−2) ∩ S(n−1) ∩ S(n)))
             *                 − ...                         
             *                 + ((−1)^(n-1) ∗ card(S(1) ∩ ... ∩ S(n)))
	    	 */
	    	Card(BUnion(children)) -> {
	    		int length = `children.length;
	    		Expression [] subFormulas = new Expression[length];
	    		for (int i = 1; i <= length; ++i) {
					List<List<Expression>> expressions = getExpressions(`children, 0, i);
					
					List<Expression> newChildren = new ArrayList<Expression>(expressions.size());
					for (List<Expression> list : expressions) {
						Expression inter;
						if (list.size() == 1)
							inter = list.iterator().next();
						else
							inter = makeAssociativeExpression(
									BINTER, list);
						Expression card = makeCard(inter);
						newChildren.add(card);
					}
					if (newChildren.size() != 1) 
						subFormulas[i-1] = makeAssociativeExpression(
								PLUS, newChildren);
					else
						subFormulas[i-1] = newChildren.iterator().next();
	    		} 
	    		Expression temp = subFormulas[0];
	    		boolean positive = false;
	    		for (int i = 1; i < length; ++i) {
	    			if (positive) {
						Expression [] newChildren = new Expression[2];
						newChildren[0] = temp;
						newChildren[1] = subFormulas[i];
						temp = makeAssociativeExpression(PLUS,
								newChildren);
	    			}
	    			else {
	    				temp = makeBinaryExpression(MINUS,
	    						temp, subFormulas[i]);
	    			}
	    			positive = !positive;
	    		}
	    		result = temp;
	    		trace(expression, result, "SIMP_CARD_BUNION");
	    		return result;
	    	}
			
			/**
             * SIMP_SPECIAL_DOM
			 * Set Theory: dom(∅) == ∅
			 */
			Dom(r@EmptySet()) -> {
				result = makeEmptySet(ff.makePowerSetType(Lib.getDomainType(`r)));
	    		trace(expression, result, "SIMP_SPECIAL_DOM");
	    		return result;
			}
			
			/**
             * SIMP_SPECIAL_RAN
			 * Set Theory: ran(∅) == ∅
			 */
			Ran(r@EmptySet()) -> {
				result = makeEmptySet(ff.makePowerSetType(Lib.getRangeType(`r)));
	    		trace(expression, result, "SIMP_SPECIAL_RAN");
	    		return result;
			}
						
			/**
			 * SIMP_SPECIAL_POW
			 *    ℙ(∅) == {∅}
			 */
			Pow(empty@EmptySet()) -> {
				if (level2) {
					result = makeSetExtension(`empty);
					trace(expression, result, "SIMP_SPECIAL_POW");
					return result; 
				}
			}
			
			/**
			 * SIMP_SPECIAL_POW1
			 *    ℙ1(∅) == ∅
			 */
			Pow1(EmptySet()) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_SPECIAL_POW1");
					return result; 
				}
			}
			
			/**
			 * SIMP_DOM_CONVERSE
			 *    dom(r∼) == ran(r)
			 */
			Dom(Converse(r)) -> {
				if (level2) {
					result = makeUnaryExpression(KRAN, `r);
					trace(expression, result, "SIMP_DOM_CONVERSE");
					return result;
				}
			}
	    
	    	/**
	     	 * SIMP_RAN_CONVERSE
	     	 *    ran(r∼) == dom(r)
	     	 */
	     	Ran(Converse(r)) -> {
	     		if (level2) {
	     			result = makeUnaryExpression(KDOM, `r);
	     			trace(expression, result, "SIMP_RAN_CONVERSE");
	     			return result;
	     		}
	     	}
	     	
	     	/**
	     	 * SIMP_CONVERSE_ID
	     	 *    id∼ == id
	     	 */
	     	Converse(id@IdGen()) -> {
	     		if (level2) {
	     			result = `id;
	     			trace(expression, result, "SIMP_CONVERSE_ID");
	     			return result;
	     		}
	     	}
	     	
	     	/**
			 * SIMP_SPECIAL_CONVERSE
			 *    ∅∼ == ∅
			 */
			Converse(EmptySet()) -> {
				if (level2) {
					result = makeEmptySet(expression.getType());
					trace(expression, result, "SIMP_SPECIAL_CONVERSE");
	    			return result;
				}
			}
			
			/**
			 * SIMP_MULTI_DOM_CPROD
			 *    dom(E×E) == E
			 */
			Dom(Cprod(E, E)) -> {
				if (level2) {
					result = `E;
					trace(expression, result, "SIMP_MULTI_DOM_CPROD");
					return result;
				}
			}
			
			/**
			 * SIMP_MULTI_RAN_CPROD
			 *    ran(E×E) == E
			 */
			Ran(Cprod(E, E)) -> {
				if (level2) {
					result = `E;
					trace(expression, result, "SIMP_MULTI_RAN_CPROD");
					return result;
				}
			}
			
			/**
			 * SIMP_KUNION_POW
			 *    union(ℙ(S)) == S
			 */
			Union(Pow(S)) -> {
				if (level2) {
					result = `S;
					trace(expression, result, "SIMP_KUNION_POW");
					return result;
				}
			}
			
			/**
			 * SIMP_KUNION_POW1
			 *    union(ℙ1(S)) == S
			 */
			Union(Pow1(S)) -> {
				if (level2) {
					result = `S;
					trace(expression, result, "SIMP_KUNION_POW1");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_KUNION
			 *    union({∅}) == ∅
			 */
			Union(SetExtension(eList(empty@EmptySet()))) -> {
				if (level2) {
					result = `empty;
					trace(expression, result, "SIMP_SPECIAL_KUNION");
					return result;
				}
			}
			
			/**
			 * SIMP_SPECIAL_KINTER
			 *    inter({∅}) == ∅
			 */
			Inter(SetExtension(eList(empty@EmptySet()))) -> {
				if (level2) {
					result = `empty;
					trace(expression, result, "SIMP_SPECIAL_KINTER");
					return result;
				}
			}

			/**
			 * SIMP_KINTER_POW
			 *    inter(ℙ(S)) == ∅
			 */
			Inter(Pow(S)) -> {
				if (level2) {
					result = makeEmptySet(`S.getType());
					trace(expression, result, "SIMP_KINTER_POW");
					return result;
				}
			}
			
			/**
			 * SIMP_DOM_ID
			 *    dom(id) == S (where id has type ℙ(S×S))
			 */
			Dom(id@IdGen()) -> {
				if (level2) {
					final Type s = `id.getType().getSource();
					result = s.toExpression(ff);
					trace(expression, result, "SIMP_DOM_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_RAN_ID
			 *    ran(id) == S (where id has type ℙ(S×S))
			 */
			Ran(id@IdGen()) -> {
				if (level2) {
					final Type s = `id.getType().getSource();
					result = s.toExpression(ff);
					trace(expression, result, "SIMP_RAN_ID");
					return result;
				}
			}
			
			/**
			 * SIMP_DOM_PRJ1
			 *    dom(prj1) == S × T (where prj1 has type ℙ(S×T×S))
			 */
			Dom(prj1@Prj1Gen()) -> {
				if (level2) {
					final Type st = `prj1.getType().getSource();
					result = st.toExpression(ff);
					trace(expression, result, "SIMP_DOM_PRJ1");
					return result;		
				}
			}
			
			/**
			 * SIMP_DOM_PRJ2
			 *    dom(prj2) == S × T (where prj2 has type ℙ(S×T×T))
			 */
			Dom(prj2@Prj2Gen()) -> {
				if (level2) {
					final Type st = `prj2.getType().getSource();
					result = st.toExpression(ff);
					trace(expression, result, "SIMP_DOM_PRJ2");
					return result;		
				}
			}
			
			/**
			 * SIMP_RAN_PRJ1
			 *    ran(prj1) == S (where prj1 has type ℙ(S×T×S))
			 */
			Ran(prj1@Prj1Gen()) -> {
				if (level2) {
					final Type st = `prj1.getType().getTarget();
					result = st.toExpression(ff);
					trace(expression, result, "SIMP_RAN_PRJ1");
					return result;		
				}
			}
			
			/**
			 * SIMP_RAN_PRJ2
			 *    ran(prj2) == T (where prj2 has type ℙ(S×T×T))
			 */
			Ran(prj2@Prj2Gen()) -> {
				if (level2) {
					final Type st = `prj2.getType().getTarget();
					result = st.toExpression(ff);
					trace(expression, result, "SIMP_RAN_PRJ2");
					return result;		
				}
			}
			
			/**
			 * SIMP_TYPE_DOM
			 *    dom(Ty) == Ta (where Ty is a type expression equal to Ta×Tb)
			 */
			Dom(Cprod(Ta, Tb)) -> {
				if (level2 && `Ta.isATypeExpression() && `Tb.isATypeExpression()) {
					result = `Ta;
					trace(expression, result, "SIMP_TYPE_DOM");
					return result;		
				}
			}
			 
			/**
			 * SIMP_TYPE_RAN
			 *    ran(Ty) == Tb (where Ty is a type expression equal to Ta×Tb)
			 */
			Ran(Cprod(Ta, Tb)) -> {
				if (level2 && `Ta.isATypeExpression() && `Tb.isATypeExpression()) {
					result = `Tb;
					trace(expression, result, "SIMP_TYPE_RAN");
					return result;
				}
			}
			
			/**
			 * SIMP_MIN_SING
			 *    min({E}) == E (where E is a single expression)
			 */
			Min(SetExtension(eList(E))) -> {
				if (level2) {
					result = `E;
					trace(expression, result, "SIMP_MIN_SING");
					return result;
				}
			}
			
			/**
			 * SIMP_MAX_SING
			 *    max({E}) == E (where E is a single expression)
			 */
			Max(SetExtension(eList(E))) -> {
				if (level2) {
					result = `E;
					trace(expression, result, "SIMP_MAX_SING");
					return result;
				}
			}
			
			/**
			 * SIMP_MIN_NATURAL
			 *    min(ℕ) == 0
			 */
			Min(Natural()) -> {
				if (level2) {
					result = number0;
					trace(expression, result, "SIMP_MIN_NATURAL");
					return result;
				}
			}

			/**
			 * SIMP_MIN_NATURAL1
			 *    min(ℕ1) == 1
			 */
			Min(Natural1()) -> {
				if (level2) {
					result = number1;
					trace(expression, result, "SIMP_MIN_NATURAL1");
					return result;
				}
			}
			
			/**
			 * SIMP_MIN_UPTO
			 *    min(E‥F) == E
			 */
			Min(UpTo(E, _)) -> {
				if (level2) {
					result = `E;
					trace(expression, result, "SIMP_MIN_UPTO");
					return result;
				}
			}
			 
			/**
			 * SIMP_MAX_UPTO
			 *    max(E‥F) == F
			 */
			Max(UpTo(_, F)) -> {
				if (level2) {
					result = `F;
					trace(expression, result, "SIMP_MAX_UPTO");
					return result;
				}
			}
			
			/**
			 * SIMP_CARD_CONVERSE
			 *    card(r∼) == card(r)
			 */
			Card(Converse(r)) -> {
				if (level2) {
					result = makeCard(`r);
					trace(expression, result, "SIMP_CARD_CONVERSE");
					return result;
				}
			}
			
			/**
			 * SIMP_CONVERSE_CPROD
			 *    (A × B)∼ == B × A
			 */
			Converse(Cprod(A, B)) -> {
				if (level2) {
					result = makeBinaryExpression(CPROD, `B, `A);
					trace(expression, result, "SIMP_CONVERSE_CPROD");
					return result;
				}
			}
			
			/**
			 * SIMP_CONVERSE_COMPSET
			 *    {X · P ∣ x ↦ y}∼ = {X · P ∣ y ↦ x}
			 */
			Converse(Cset(bil, P, Mapsto(x, y))) -> {
				if (level2) {
					result = makeQuantifiedExpression(CSET, `bil, `P,
								makeBinaryExpression(MAPSTO, `y, `x),
								Form.Explicit);
					trace(expression, result, "SIMP_CONVERSE_COMPSET");
					return result;
				}
			}

	    	/**
			 * SIMP_DOM_LAMBDA
			 *    dom({x · P ∣ E ↦ F}) = {x · P ∣ E}
			 */
			Dom(Cset(bil, P, Mapsto(E, _))) -> {
				if (level2) {
					result = makeQuantifiedExpression(CSET,
								`bil, `P, `E, Form.Explicit);
					trace(expression, result, "SIMP_DOM_LAMBDA");
					return result;
				}
			}
			
			/**
			 * SIMP_RAN_LAMBDA
			 *    ran({x · P ∣ E ↦ F}) = {x · P ∣ F}
			 */
			Ran(Cset(bil, P, Mapsto(_, F))) -> {
				if (level2) {
					result = makeQuantifiedExpression(CSET,
								`bil, `P, `F, Form.Explicit);
					trace(expression, result, "SIMP_RAN_LAMBDA");
					return result;
				}
			}
			
			/**
			 * SIMP_MIN_BUNION_SING
			 *    min(S ∪ .. ∪ {min(T)} ∪ .. ∪ U) == min(S ∪ .. ∪ T ∪ .. ∪ U)
			 */
			Min(BUnion(children)) -> {
				if (level2) {
					final Expression rewritten =
							simplifyExtremumOfUnion(`children, KMIN);
					if (rewritten != null) {
						result = rewritten;
						trace(expression, result, "SIMP_MIN_BUNION_SING");
						return result;
					}
				}
			}
			
			/**
			 * SIMP_MAX_BUNION_SING
			 *    max(S ∪ .. ∪ {max(T)} ∪ .. ∪ U) == max(S ∪ .. ∪ T ∪ .. ∪ U)
			 */
			Max(BUnion(children)) -> {
				if (level2) {
					final Expression rewritten =
							simplifyExtremumOfUnion(`children, KMAX);
					if (rewritten != null) {
						result = rewritten;
						trace(expression, result, "SIMP_MAX_BUNION_SING");
						return result;
					}
				}
			}
			
			/**
			 * SIMP_LIT_MIN
			 *    min({E, .. , i, .. , j, .. , H}) = min({E, .. , i, .. , H})
			 *		(where i and j are literals and i ≤ j)
			 */
			Min(setext@SetExtension(_)) -> {
				if (level2) {
					Expression newSet = simplifyMin((SetExtension) `setext, ff);
					if (newSet != `setext) {
						result = makeUnaryExpression(KMIN, newSet);
						trace(expression, result, "SIMP_LIT_MIN");
						return result;
					}
				}
			}
			 
			/**
			 * SIMP_LIT_MAX
			 *    max({E, .. , i, .. , j, .. , H}) = max({E, .. , i, .. , H})
			 *		(where i and j are literals and i ≥ j)
			 */
			Max(setext@SetExtension(_)) -> {
				if (level2) {
					Expression newSet = simplifyMax((SetExtension) `setext, ff);
					if (newSet != `setext) {
						result = makeUnaryExpression(KMAX, newSet);
						trace(expression, result, "SIMP_LIT_MAX");
						return result;
					}
				}
			}
			
			/**
			 * SIMP_CARD_ID
			 *    card(id) == card(S) where id has type ℙ(S×S)
			 * SIMP_CARD_PRJ1
			 *    card(prj1) == card(S×T) where prj1 has type ℙ(S×T×S)
			 * SIMP_CARD_PRJ2
			 *    card(prj2) == card(S×T) where prj2 has type ℙ(S×T×T)
			 */
			Card(op@(IdGen | Prj1Gen | Prj2Gen)()) -> {
				if (level2) {
					result = makeCard(`op.getType().getSource().toExpression(ff));
					trace(expression, result, "SIMP_CARD_ID", "SIMP_CARD_PRJ1", "SIMP_CARD_PRJ2");
					return result;
				}
			}
			
			/**
			 * SIMP_CARD_ID_DOMRES
			 *    card(E ◁ id) == card(E)
			 * SIMP_CARD_PRJ1_DOMRES
			 *    card(E ◁ prj1) == card(E)
			 * SIMP_CARD_PRJ2_DOMRES
			 *    card(E ◁ prj2) == card(E)
			 */
			Card(DomRes(E, (IdGen | Prj1Gen | Prj2Gen)())) -> {
				if (level2) {
					result = makeCard(`E);
					trace(expression, result, "SIMP_CARD_ID_DOMRES", "SIMP_CARD_PRJ1_DOMRES", "SIMP_CARD_PRJ2_DOMRES");
					return result;
				}
			}
			
			/**
			 * SIMP_CARD_LAMBDA
			 *    card({x · P ∣ E ↦ F}) == card({x · P ∣ E})
			 */
			Card(lambda@Cset(bil, P, Mapsto(E,_))) -> {
				if (level2 && functionalCheck((QuantifiedExpression) `lambda)) {
					result = makeCard(
								makeQuantifiedExpression(CSET,
								`bil, `P, `E, Form.Explicit));
					trace(expression, result, "SIMP_CARD_LAMBDA");
					return result;
				}
			}
			
	    }
	    return expression;
	}

	@ProverRule( { "SIMP_SPECIAL_KBOOL_BFALSE", "SIMP_SPECIAL_KBOOL_BTRUE" }) 
    @Override
	public Expression rewrite(BoolExpression expression) {
		final Expression result; 
	    %match (Expression expression) {
	   		/**
             * SIMP_SPECIAL_KBOOL_BFALSE
	    	 * Set Theory:	bool(⊥) = FALSE
	    	 */
	    	Bool(BFALSE()) -> {
				result = ff.makeAtomicExpression(FALSE, null);
	    		trace(expression, result, "SIMP_SPECIAL_KBOOL_BFALSE");
	    		return result;
			}

	   		/**
             * SIMP_SPECIAL_KBOOL_BTRUE
	    	 * Set Theory:	bool(⊤) = TRUE  
	    	 */
	    	Bool(BTRUE()) -> {
				result = ff.makeAtomicExpression(TRUE, null);
	    		trace(expression, result, "SIMP_SPECIAL_KBOOL_BTRUE");
	    		return result;
			}
    	}
	    return expression;
    }

	private List<List<Expression>> getExpressions(Expression [] array, int from, int size) {
		List<List<Expression>> result = new ArrayList<List<Expression>>();
		if (size == 0) {
			result.add(new ArrayList<Expression>());
		}
		else {
			for (int i = from; i <= array.length - size; ++i) {
				List<List<Expression>> lists = getExpressions(array, i + 1, size - 1);
				for (List<Expression> list : lists) {
					List<Expression> newList = new ArrayList<Expression>();
					newList.add(array[i]);
					newList.addAll(list);
					result.add(newList);
				}
			}
		}
		return result;
	}
    
    @ProverRule( {"SIMP_MULTI_SETENUM" } ) 
	@Override
	public Expression rewrite(SetExtension expression) {
    	final Expression result;
	    %match (Expression expression) {
			/**
             * SIMP_MULTI_SETENUM
	    	 * Set Theory: {A, ..., B, ..., B, ..., C} == {A, ..., B, ..., C}
	    	 */
	    	SetExtension(members) -> {
	    		Collection<Expression> newMembers = new LinkedHashSet<Expression>();

				for (Expression member : `members) {
					newMembers.add(member);
				}
				if (newMembers.size() != `members.length) {
					result = makeSetExtension(newMembers);
		    		trace(expression, result, "SIMP_MULTI_SETENUM");
		    		return result;
				}
	    	}
	    	
		}
	    return expression;
	}
    
	@ProverRule({ "SIMP_DESTR_CONSTR", "SIMP_SPECIAL_COND_BTRUE",
			"SIMP_SPECIAL_COND_BFALSE", "SIMP_MULTI_COND" })
	@Override
	public Expression rewrite(ExtendedExpression expression) {
    	final Expression result;
    	%match (Expression expression) {

    		/**
    		 * SIMP_DESTR_CONSTR:
    		 * destr(cons(a_1, ..., a_n))  ==  a_i   [i is the param index of the destructor]
    		 */
			ExtendedExpression(eList(cons@ExtendedExpression(as,_)), pList()) -> {
				final int idx = getParamIndex((ExtendedExpression) expression,
						(ExtendedExpression) `cons);
				if (idx >= 0) {
					result = `as[idx];
					trace(expression, result, "SIMP_DESTR_CONSTR");
					return result;
				}
    		}
    		
    		/**
    		 * SIMP_SPECIAL_COND_BTRUE:
    		 * COND(true, E_1, E_2) == E_1
    		 */
    		ExtendedExpression(eList(E1,_), pList(BTRUE())) -> {
    			final ExtendedExpression ee = (ExtendedExpression) expression;
    			if (ee.getExtension() == FormulaFactory.getCond()) {
					result = `E1;
		    		trace(expression, result, "SIMP_SPECIAL_COND_BTRUE");
		    		return result;
				}
    		}

    		/**
    		 * SIMP_SPECIAL_COND_BFALSE:
    		 * COND(false, E_1, E_2) == E_2
    		 */
    		ExtendedExpression(eList(_,E2), pList(BFALSE())) -> {
    			final ExtendedExpression ee = (ExtendedExpression) expression;
    			if (ee.getExtension() == FormulaFactory.getCond()) {
					result = `E2;
		    		trace(expression, result, "SIMP_SPECIAL_COND_BFALSE");
		    		return result;
				}
    		}

    		/**
    		 * SIMP_MULTI_COND:
    		 * COND(C, E, E) == E
    		 */
    		ExtendedExpression(eList(E,E), pList(_)) -> {
    			final ExtendedExpression ee = (ExtendedExpression) expression;
    			if (ee.getExtension() == FormulaFactory.getCond()) {
					result = `E;
		    		trace(expression, result, "SIMP_MULTI_COND");
		    		return result;
    			}
    		}
    	}
    	return expression;
    }
    
        
    @ProverRule( { "SIMP_SPECIAL_COMPSET_BFALSE",
    			"SIMP_SPECIAL_COMPSET_BTRUE", "SIMP_SPECIAL_QUNION",
    			"SIMP_COMPSET_IN", "SIMP_COMPSET_SUBSETEQ" } )
    @Override
    public Expression rewrite(QuantifiedExpression expression) {
    	final Expression result;
    	%match (Expression expression) {
    	
    		/**
	    	 * SIMP_SPECIAL_COMPSET_BFALSE
	    	 *    {x · ⊥ ∣ x} == ∅
	    	 */
	    	Cset(_, BFALSE(), _) -> {
	    		if (level2) {
	    			result = makeEmptySet(expression.getType());
	    			trace(expression, result, "SIMP_SPECIAL_COMPSET_BFALSE");
		    		return result;
	    		}
	    	}
	    	
	    	/**
	    	 * SIMP_SPECIAL_COMPSET_BTRUE
	    	 *    {x · ⊤ ∣ x} == Ty (where the type of x is Ty)
	    	 */
	    	Cset(decls, BTRUE(), E) -> {
	    		if (level2 && partialLambdaPatternCheck(`E, `decls.length)) {
	    			result = `E.getType().toExpression(ff);
		    		trace(expression, result, "SIMP_SPECIAL_COMPSET_BTRUE");
		    		return result;
	    		}
	    	}
	    	
	    	/**
	    	 * SIMP_SPECIAL_QUNION
	    	 *    ⋃x · ⊥ ∣ E == ∅
	    	 */
	    	Qunion(_, BFALSE(), _) -> {
	    		if (level2) {
	    			result = makeEmptySet(expression.getType());
	    			trace(expression, result, "SIMP_SPECIAL_QUNION");
		    		return result;
	    		}
	    	}
	    	
	    	/**
	    	 * SIMP_COMPSET_IN
	    	 *    {x · x∈S ∣ x} == S
	    	 */
	    	Cset(decls, In(E, S), E) -> {
	    		final int nbBound = `decls.length;
	    		if (level2 && notLocallyBound(`S, nbBound)
	    				&& partialLambdaPatternCheck(`E, nbBound)) {
   					result = `S.shiftBoundIdentifiers(-nbBound, ff);
   					trace(expression, result, "SIMP_COMPSET_IN");
    				return result;
	    		}
	    	}
	    	
	    	/**
	    	 * SIMP_COMPSET_SUBSETEQ
	    	 *    {x · x⊆S ∣ x} == ℙ(S)
	    	 */
	    	Cset(decls, SubsetEq(bi@BoundIdentifier(_), S), bi) -> {
	    		final int nbBound = `decls.length;
	    		if (level2 && notLocallyBound(`S, nbBound)) {
	    			result = makeUnaryExpression(POW, `S);
	    			trace(expression, result, "SIMP_COMPSET_SUBSETEQ");
    				return result;
	    		}
	    	}
	 
    	}
    	return expression;
    }

    private static int getParamIndex(ExtendedExpression destr, ExtendedExpression cons) {
    	final IExpressionExtension consExt = cons.getExtension();
    	final Object origin = consExt.getOrigin();
    	if (!(origin instanceof IDatatype)) {
    		return -1;
    	}
    	final IDatatype datatype = (IDatatype) origin;
    	return datatype.getDestructorIndex(consExt, destr.getExtension());
    }

}
