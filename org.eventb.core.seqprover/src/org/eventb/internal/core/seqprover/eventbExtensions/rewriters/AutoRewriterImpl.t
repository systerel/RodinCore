/*******************************************************************************
 * Copyright (c) 2006, 2024 ETH Zurich and others.
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
 *     Systerel - Level 4
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
import static org.eventb.core.ast.Formula.EXPN;
import static org.eventb.core.ast.Formula.FORALL;
import static org.eventb.core.ast.Formula.GE;
import static org.eventb.core.ast.Formula.GT;
import static org.eventb.core.ast.Formula.IN;
import static org.eventb.core.ast.Formula.INTEGER;
import static org.eventb.core.ast.Formula.KCARD;
import static org.eventb.core.ast.Formula.KDOM;
import static org.eventb.core.ast.Formula.KFINITE;
import static org.eventb.core.ast.Formula.KMAX;
import static org.eventb.core.ast.Formula.KMIN;
import static org.eventb.core.ast.Formula.KPARTITION;
import static org.eventb.core.ast.Formula.KRAN;
import static org.eventb.core.ast.Formula.KSUCC;
import static org.eventb.core.ast.Formula.LAND;
import static org.eventb.core.ast.Formula.LE;
import static org.eventb.core.ast.Formula.LEQV;
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
import static org.eventb.core.ast.Formula.UNMINUS;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyComp;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyInter;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyMult;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyOvr;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyPlus;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AssociativeSimplification.simplifyUnion;
import static org.eventb.internal.core.seqprover.eventbExtensions.rewriters.DivisionUtils.getFaction;
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
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IntegerLiteral;
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
import org.eventb.core.ast.datatype.IConstructorArgument;
import org.eventb.core.ast.datatype.IConstructorExtension;
import org.eventb.core.ast.datatype.IDestructorExtension;
import org.eventb.core.ast.datatype.ISetInstantiation;
import org.eventb.core.ast.datatype.ITypeConstructorExtension;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.seqprover.ProverRule;
import org.eventb.core.seqprover.eventbExtensions.DLib;
import org.eventb.core.seqprover.eventbExtensions.Lib;
import org.eventb.internal.core.seqprover.eventbExtensions.OnePointProcessorRewriting;
import org.eventb.internal.core.seqprover.eventbExtensions.rewriters.AutoRewrites.Level;

/**
 * Basic automated rewriter for the Event-B sequent prover.
 */
@SuppressWarnings({"unused", "cast"})
public class AutoRewriterImpl extends PredicateSimplifier {

	private static final BigInteger TWO = BigInteger.valueOf(2L);

	public static boolean DEBUG;

	private final Level level;

	// Cached enabled levels
	private final boolean level1;
	private final boolean level2;
	private final boolean level3;
	private final boolean level4;
	private final boolean level5;

	private static int optionsForLevel(Level level) {
		int result = 0;
		result |= MULTI_IMP | QUANT_DISTR | MULTI_AND_OR;
		if (level.from(Level.L2)) {
			result |= MULTI_EQV_NOT | MULTI_IMP_AND;
		}
		if (level.from(Level.L3)) {
			result |= MULTI_IMP_NOT | EXISTS_IMP;
		}
		return result;
	}

	public AutoRewriterImpl(Level level) {
		super(optionsForLevel(level), DEBUG, "AutoRewriter");
		this.level = level;
		this.level1 = level.from(Level.L1);
		this.level2 = level.from(Level.L2);
		this.level3 = level.from(Level.L3);
		this.level4 = level.from(Level.L4);
		this.level5 = level.from(Level.L5);
	}

	public Level getLevel() {
		return level;
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

	protected RelationalPredicate makeIsEmpty(Expression set) {
		return makeRelationalPredicate(EQUAL, set,
			makeEmptySet(set.getFactory(), set.getType()));
	}

	protected RelationalPredicate makeIsType(Expression set) {
		return makeRelationalPredicate(EQUAL, set,
				getBaseTypeExpression(set));
	}

	// produces E_1 = {} & E_2 = {} & ... & E_n = {}
	protected AssociativePredicate makeAreAllEmpty(Expression[] expressions) {
		final Predicate[] newChildren = new Predicate[expressions.length];
		for (int i = 0; i < expressions.length; ++i) {
			newChildren[i] = makeIsEmpty(expressions[i]);
		}
		return makeAssociativePredicate(LAND, newChildren);
	}

	protected AssociativePredicate makeAreAllType(Expression... expressions) {
		final Predicate[] newChildren = new Predicate[expressions.length];
		for (int i = 0; i < expressions.length; ++i) {
			newChildren[i] = makeIsType(expressions[i]);
		}
		return makeAssociativePredicate(LAND, newChildren);
	}

	protected Predicate makeIsNotEmpty(Expression set) {
		return makeUnaryPredicate(NOT, makeIsEmpty(set));
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

	protected RelationalPredicate makeEmptyInter(Expression left,
			Expression right) {
		return makeIsEmpty(makeAssociativeExpression(BINTER, left, right));
	}

	protected Expression getBaseTypeExpression(Expression expression) {
		return expression.getType().getBaseType().toExpression();
	}

	protected boolean containsSingleton(Expression[] exprs) {
		for (final Expression expr : exprs) {
			%match (Expression expr) {
				SetExtension(eList(_)) -> {
					return true;
				}
			}
		}
		return false;
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
		final FormulaFactory ff = predicate.getFactory();
		final Predicate result;
	    %match (Predicate predicate) {

			/**
             * SIMP_SPECIAL_FINITE
	    	 * Finite: finite(∅) = ⊤
	    	 */
			Finite(EmptySet()) -> {
				result = DLib.True(ff);
				trace(predicate, result, "SIMP_SPECIAL_FINITE");
				return result;
			}

			/**
			 * SIMP_FINITE_NATURAL
			 *    finite(ℕ) == ⊥
			 */
			Finite(Natural()) -> {
				if (level2) {
					result = DLib.False(ff);
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
					result = DLib.False(ff);
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
					result = DLib.False(ff);
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
					result = DLib.True(ff);
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
					result = makeFinite(s.toExpression());
					trace(predicate, result, "SIMP_FINITE_ID");
					return result;
				}
			}

			/**
             * SIMP_FINITE_SETENUM
	    	 * Finite: finite({a, ..., b}) = ⊤
	    	 */
			Finite(SetExtension(_)) -> {
				result = DLib.True(ff);
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
				result = DLib.True(ff);
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
					result = makeFinite(`prj1.getType().getSource().toExpression());
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
					result = makeFinite(`prj2.getType().getSource().toExpression());
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

    @ProverRule( { "SIMP_EMPTY_PARTITION", "SIMP_SINGLE_PARTITION" } )
	@Override
	public Predicate rewrite(MultiplePredicate predicate) {
		if (!level4) {
			return predicate;
		}
		assert predicate.getTag() == KPARTITION;
		final Expression[] children = predicate.getChildren();
		final Predicate result;
		switch (children.length) {
			case 1:
    			result = makeIsEmpty(children[0]);
    			trace(predicate, result, "SIMP_EMPTY_PARTITION");
    			return result;
			case 2:
    			result = makeRelationalPredicate(EQUAL,
    					children[0], children[1]);
    			trace(predicate, result, "SIMP_SINGLE_PARTITION");
    			return result;
			default:
				return predicate;
		}
	}

    @ProverRule( { "SIMP_NOT_LE", "SIMP_NOT_GE", "SIMP_NOT_GT",
			"SIMP_NOT_LT", "SIMP_SPECIAL_NOT_EQUAL_FALSE_R",
			"SIMP_SPECIAL_NOT_EQUAL_TRUE_R", "SIMP_SPECIAL_NOT_EQUAL_FALSE_L",
			"SIMP_SPECIAL_NOT_EQUAL_TRUE_L" })
	@Override
	public Predicate rewrite(UnaryPredicate predicate) {
		final Predicate attempt = super.rewrite(predicate);
		if (attempt != predicate) {
			return attempt;
		}

		final FormulaFactory ff = predicate.getFactory();
		final Predicate result;
	    %match (Predicate predicate) {
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
				result =  makeRelationalPredicate(EQUAL, `E, DLib.TRUE(ff));
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_FALSE_R");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_TRUE_R
	    	 * Negation 13: ¬ (E = TRUE) == E = FALSE
	    	 */
			Not(Equal(E, TRUE())) -> {
				result =  makeRelationalPredicate(EQUAL, `E, DLib.FALSE(ff));
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_TRUE_R");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_FALSE_L
	    	 * Negation 14: ¬ (FALSE = E) == TRUE = E
	    	 */
			Not(Equal(FALSE(), E)) -> {
				result =  makeRelationalPredicate(EQUAL, DLib.TRUE(ff), `E);
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_FALSE_L");
				return result;
			}

	    	/**
             * SIMP_SPECIAL_NOT_EQUAL_TRUE_L
	    	 * Negation 15: ¬ (TRUE = E) == FALSE = E
	    	 */
			Not(Equal(TRUE(), E)) -> {
				result =  makeRelationalPredicate(EQUAL, DLib.FALSE(ff), `E);
	    		trace(predicate, result, "SIMP_SPECIAL_NOT_EQUAL_TRUE_L");
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
            "SIMP_UPTO_EQUAL_NATURAL", "SIMP_UPTO_EQUAL_NATURAL1",
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
			"SIMP_SPECIAL_EQUAL_COMPSET", "DEF_IN_MAPSTO", "DERIV_MULTI_IN_SETMINUS", 
			"DERIV_MULTI_IN_BUNION", "DERIV_PRJ1_SURJ", "DERIV_PRJ2_SURJ",
			"DERIV_ID_BIJ", "SIMP_MIN_IN", "SIMP_MAX_IN", "SIMP_SPECIAL_IN_ID",
			"SIMP_SPECIAL_IN_SETMINUS_ID", "SIMP_SPECIAL_IN_DOMRES_ID",
			"SIMP_SPECIAL_IN_SETMINUS_DOMRES_ID" })
    @Override
	public Predicate rewrite(RelationalPredicate predicate) {
		final FormulaFactory ff = predicate.getFactory();
		final IntegerLiteral number0 = ff.makeIntegerLiteral(ZERO, null);
		final IntegerLiteral number1 = ff.makeIntegerLiteral(ONE, null);
		final Predicate result;
	    %match (Predicate predicate) {

	    	/**
             * SIMP_MULTI_EQUAL
	    	 * Equality: E = E == ⊤
	    	 */
	    	Equal(E, E) -> {
	    		result = DLib.True(ff);
	    		trace(predicate, result, "SIMP_MULTI_EQUAL");
				return result;
	    	}

	    	/**
             * SIMP_MULTI_NOTEQUAL
	    	 * Equality: E ≠ E == ⊥
	    	 */
	    	NotEqual(E, E) -> {
	    		result = DLib.False(ff);
	    		trace(predicate, result, "SIMP_MULTI_NOTEQUAL");
				return result;
	    	}

			/**
             * SIMP_MULTI_LE
	    	 * Arithmetic: E ≤ E == ⊤
	    	 */
	    	Le(E, E) -> {
				result = DLib.True(ff);
	    		trace(predicate, result, "SIMP_MULTI_LE");
				return result;
	    	}

	    	/**
             * SIMP_MULTI_GE
	    	 * Arithmetic: E ≥ E == ⊤
	    	 */
	    	Ge(E, E) -> {
				result = DLib.True(ff);
	    		trace(predicate, result, "SIMP_MULTI_GE");
				return result;
	    	}

			/**
             * SIMP_MULTI_LT
	    	 * Arithmetic: E < E == ⊥
	    	 */
	    	Lt(E, E) -> {
				result = DLib.False(ff);
	    		trace(predicate, result, "SIMP_MULTI_LT");
				return result;
	    	}

			/**
             * SIMP_MULTI_GE
	    	 * Arithmetic: E > E == ⊥
	    	 */
	    	Gt(E, E) -> {
				result = DLib.False(ff);
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
	    		result = DLib.False(ff);
	    		trace(predicate, result, "SIMP_SPECIAL_EQUAL_TRUE");
				return result;
	    	}

	    	/**
             * SIMP_SPECIAL_EQUAL_TRUE
	    	 * Equality 5: FALSE = TRUE == ⊥
	    	 */
	    	Equal(FALSE(), TRUE()) -> {
	    		result = DLib.False(ff);
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
	    		result = DLib.True(ff);
	    		trace(predicate, result, "SIMP_SPECIAL_SUBSETEQ");
				return result;
	    	}

	    	/**
             * SIMP_MULTI_SUBSETEQ
	    	 * Set Theory: S ⊆ S == ⊤
	    	 */
	    	SubsetEq(S, S) -> {
	    		result = DLib.True(ff);
	    		trace(predicate, result, "SIMP_MULTI_SUBSETEQ");
				return result;
	    	}

			/**
			 * Rules for equality with an empty set
			 * E = ∅ == ...
			 * E ⊆ ∅ == ...
			 * ∅ = E == ...
			 */
			(Equal|SubsetEq)(E, EmptySet()) ||
			Equal(EmptySet(), E) << predicate -> {
				if (level4) {
					final Predicate pred = rewriteEqualsEmptySet(predicate, `E);
					if (pred != null) {
						return pred;
					}
				}
			}

	    	/**
             * SIMP_SUBSETEQ_BUNION
	    	 * Set Theory: S ⊆ A ∪ ... ∪ S ∪ ... ∪ B == ⊤
	    	 */
	    	SubsetEq(S, BUnion(eList(_*, S, _*))) -> {
	    		result = DLib.True(ff);
	    		trace(predicate, result, "SIMP_SUBSETEQ_BUNION");
	    		return result;
	    	}

	    	/**
             * SIMP_SUBSETEQ_BINTER
	    	 * Set Theory: A ∩ ... ∩ S ∩ ... ∩ B ⊆ S == ⊤
	    	 */
	    	SubsetEq(BInter(eList(_*, S, _*)), S) -> {
				result = DLib.True(ff);
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
	    		result = DLib.False(ff);
	    		trace(predicate, result, "SIMP_SPECIAL_IN");
				return result;
	    	}

			/**
	    	 * SIMP_MULTI_IN
             * Set Theory 9: B ∈ {A, ..., B, ..., C} == ⊤
	    	 */
	    	In(B, SetExtension(eList(_*, B, _*))) -> {
				result = DLib.True(ff);
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
	    		result = `i.equals(`j) ? DLib.True(ff) : DLib.False(ff);
	    		trace(predicate, result, "SIMP_LIT_EQUAL");
				return result;
	    	}

	    	/**
             * SIMP_LIT_LE
	    	 * Arithmetic 17: i ≤ j == ⊤  or  i ≤ j == ⊥ (by computation)
	    	 */
	    	Le(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) <= 0 ? DLib.True(ff) : DLib.False(ff);
	    		trace(predicate, result, "SIMP_LIT_LE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_LT
	    	 * Arithmetic 18: i < j == ⊤  or  i < j == ⊥ (by computation)
	    	 */
	    	Lt(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) < 0 ? DLib.True(ff) : DLib.False(ff);
	    		trace(predicate, result, "SIMP_LIT_LT");
				return result;
	    	}

	    	/**
             * SIMP_LIT_GE
	    	 * Arithmetic 19: i ≥ j == ⊤  or  i ≥ j == ⊥ (by computation)
	    	 */
	    	Ge(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) >= 0 ? DLib.True(ff) : DLib.False(ff);
	    		trace(predicate, result, "SIMP_LIT_GE");
				return result;
	    	}

	    	/**
             * SIMP_LIT_GT
	    	 * Arithmetic 20: i > j == ⊤  or  i > j == ⊥ (by computation)
	    	 */
	    	Gt(IntegerLiteral(i), IntegerLiteral(j)) -> {
	    		result = `i.compareTo(`j) > 0 ? DLib.True(ff) : DLib.False(ff);
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
					result = makeIsNotEmpty(`S);
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
					result = makeIsNotEmpty(`S);
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
             * SIMP_EQUAL_DT
             * datatype(T1, U1, ...) = datatype(T2, U2, ...)  ==  T1 = T2 & U1 = U2 & ...
             */
            Equal(ext1@ExtendedExpression(args1, _), ext2@ExtendedExpression(args2, _)) -> {
                if (isDTConstructor((ExtendedExpression)`ext1)
                		&& isDTConstructor((ExtendedExpression)`ext2)) {
                	if (`ext1.getTag() != `ext2.getTag()) {
                		result = ff.makeLiteralPredicate(BFALSE, null);
                		trace(predicate, result, "SIMP_EQUAL_CONSTR_DIFF");
                		return result;
                	}
               		result = makeConjunctionComparisons(EQUAL, `args1, `args2, ff);
               		trace(predicate, result, "SIMP_EQUAL_CONSTR");
               		return result;
                } else if (level5 && isTypeConstructor((ExtendedExpression)`ext1)
                        && isTypeConstructor((ExtendedExpression)`ext2)) {
                    assert `ext1.getTag() == `ext2.getTag(); // Type-checking should prevent comparing different types
                    result = makeConjunctionComparisons(EQUAL, `args1, `args2, ff);
                    trace(predicate, result, "SIMP_EQUAL_DT");
                    return result;
                }
            }

            /**
             * SIMP_CONSTR_IN
             * cons(a, ...) : datatype(T, ...)  ==  a : destr1-set(T, ...) & ...
             */
            In(ext1@ExtendedExpression(args1, _), ext2@ExtendedExpression(_, _)) -> {
                if (level5 && isDTConstructor((ExtendedExpression)`ext1)
                        && isTypeConstructor((ExtendedExpression)`ext2)) {
                    var typeConsExt = (ITypeConstructorExtension) ((ExtendedExpression) `ext2).getExtension();
                    ISetInstantiation instantiation = typeConsExt.getOrigin().getSetInstantiation(`ext2);
                    var consExt = (IConstructorExtension) ((ExtendedExpression) `ext1).getExtension();
                    IConstructorArgument[] formalArgs = consExt.getArguments();
                    assert formalArgs.length == `args1.length;
                    Expression[] sets = new Expression[formalArgs.length];
                    for (int i = 0; i < formalArgs.length; i++) {
                        sets[i] = formalArgs[i].getSet(instantiation);
                    }
                    result = makeConjunctionComparisons(IN, `args1, sets, ff);
                    trace(predicate, result, "SIMP_CONSTR_IN");
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
					result = DLib.False(ff);
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
					result = DLib.False(ff);
					trace(predicate, result, "SIMP_MULTI_SUBSET");
					return result;
				}
			}

			/**
			 * SIMP_SPECIAL_EQUAL_REL
			 *    A ↔ B = ∅ == ⊥
			 *    A ⇸ B = ∅ == ⊥
			 *    A ⤔ B = ∅ == ⊥
			 */
			Equal((Rel | Pfun | Pinj)(_, _), EmptySet()) -> {
				if (level2) {
					result = DLib.False(ff);
					trace(predicate, result, "SIMP_SPECIAL_EQUAL_REL");
					return result;
				}
			}

			/**
			 * SIMP_SPECIAL_EQUAL_RELDOM
			 *    A  B = ∅ == ¬(A = ∅) ∧ B = ∅
			 *    A → B = ∅ == ¬(A = ∅) ∧ B = ∅
			 */
			Equal((Tfun | Trel)(A, B), EmptySet()) -> {
				if (level2) {
					result = makeAssociativePredicate(LAND,
								makeIsNotEmpty(`A),
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
					result = DLib.True(ff);
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
					result = makeIsNotEmpty(`S);
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
						result = DLib.True(ff);
						trace(predicate, result, "SIMP_LIT_IN_NATURAL");
						return result;
					} else {
						result = DLib.False(ff);
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
						result = DLib.True(ff);
						trace(predicate, result, "SIMP_LIT_IN_NATURAL1");
						return result;
					case 0:
						result = DLib.False(ff);
						trace(predicate, result, "SIMP_SPECIAL_IN_NATURAL1");
						return result;
					case -1:
						result = DLib.False(ff);
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
 	    				result = DLib.True(ff);
						trace(predicate, result, "SIMP_LIT_LE_CARD_0");
						return result;
 	    			} else if (`i.equals(ONE)) {
						result = makeIsNotEmpty(`S);
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
	 	    			result = DLib.True(ff);
						trace(predicate, result, "SIMP_LIT_GE_CARD_0");
						return result;
 	    			} else if (`i.equals(ONE)) {
						result = makeIsNotEmpty(`S);
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
					result = DLib.True(ff);
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
					result = DLib.True(ff);
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
					result = DLib.True(ff);
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
													`S.shiftBoundIdentifiers(`bidl.length)));
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
			
			/**
			 * DEF_IN_MAPSTO
			 * a↦b ∈ A×B == a∈A ∧ b∈B
			 */
			In(Mapsto(a,b), Cprod(A,B)) -> {
				if (level3) {
					final Predicate aInA = makeRelationalPredicate(IN, `a, `A);
					final Predicate bInB = makeRelationalPredicate(IN, `b, `B);
					result = makeAssociativePredicate(LAND, aInA, bInB);
					trace(predicate, result, "DEF_IN_MAPSTO");
					return result;
				}
			}

			/**
			 * DERIV_MULTI_IN_SETMINUS
			 * E∈S∖{..., E, ...} == ⊥
			 */
			In(E, SetMinus(_, SetExtension(eList(_*, E, _*)))) -> {
				if (level3) {
					result = DLib.False(ff);
					trace(predicate, result, "DERIV_MULTI_IN_SETMINUS");
					return result;
				}
			}

			/**
			 * DERIV_MULTI_IN_BUNION
			 * E∈A∪...∪{..., E, ...}∪ ... ∪Z == ⊤
			 */
			In(E, BUnion(eList(_*, SetExtension(eList(_*, E, _*)), _*))) -> {
				if (level3) {
					result = DLib.True(ff);
					trace(predicate, result, "DERIV_MULTI_IN_BUNION");
					return result;
				}
			}

			/**
			 * SIMP_UPTO_EQUAL_NATURAL
			 * i ‥ j = ℕ == ⊥
			 * ℕ ⊂ i ‥ j == ⊥
			 * ℕ ⊆ i ‥ j == ⊥
			 * ℕ = i ‥ j == ⊥
			 */
			Equal(UpTo(_, _), Natural()) ||
			(Subset|SubsetEq|Equal)(Natural(), UpTo(_, _)) << predicate -> {
				if (level4) {
					result = DLib.False(ff);
					trace(predicate, result, "SIMP_UPTO_EQUAL_NATURAL");
					return result;
				}
			}

			/**
			 * SIMP_UPTO_EQUAL_NATURAL1
			 * i ‥ j = ℕ1 == ⊥
			 * ℕ1 ⊂ i ‥ j == ⊥
			 * ℕ1 ⊆ i ‥ j == ⊥
			 * ℕ1 = i ‥ j == ⊥
			 */
			Equal(UpTo(_, _), Natural1()) ||
			(Subset|SubsetEq|Equal)(Natural1(), UpTo(_, _)) << predicate -> {
				if (level4) {
					result = DLib.False(ff);
					trace(predicate, result, "SIMP_UPTO_EQUAL_NATURAL1");
					return result;
				}
			}

			/**
			 * Rules for equality with a type (where Ty is a type expression)
			 * E = Ty == ...
			 * Ty ⊆ E == ...
			 * Ty = E == ...
			 */
			Equal(E, Ty) ||
			(SubsetEq|Equal)(Ty, E) << predicate -> {
				if (level4 && `Ty.isATypeExpression()) {
					final Predicate pred = rewriteEqualsType(predicate, `E);
					if (pred != null) {
						return pred;
					}
				}
			}

			/**
			 * DERIV_PRJ1_SURJ
			 *    prj1 ∈ Ty1 op Ty2 where op is not injective
			 */
			In(Prj1Gen(), (Rel|Trel|Srel|Strel|Pfun|Tfun|Psur|Tsur)(Ty1, Ty2)) -> {
				if (level4 && `Ty1.isATypeExpression()
				           && `Ty2.isATypeExpression()) {
					result = DLib.True(ff);
					trace(predicate, result, "DERIV_PRJ1_SURJ");
					return result;
				}
			}

			/**
			 * DERIV_PRJ2_SURJ
			 *    prj2 ∈ Ty1 op Ty2 where op is not injective
			 */
			In(Prj2Gen(), (Rel|Trel|Srel|Strel|Pfun|Tfun|Psur|Tsur)(Ty1, Ty2)) -> {
				if (level4 && `Ty1.isATypeExpression()
				           && `Ty2.isATypeExpression()) {
					result = DLib.True(ff);
					trace(predicate, result, "DERIV_PRJ2_SURJ");
					return result;
				}
			}

			/**
			 * DERIV_ID_BIJ
			 *    id ∈ Ty1 op Ty2 for all arrows
			 */
			In(IdGen(), (Rel|Trel|Srel|Strel|Pfun|Tfun|Psur|Tsur|Pinj|Tinj|Tbij)(Ty1, Ty2)) -> {
				if (level4 && `Ty1.isATypeExpression()
				           && `Ty2.isATypeExpression()) {
					result = DLib.True(ff);
					trace(predicate, result, "DERIV_ID_BIJ");
					return result;
				}
			}

			/**
			 * SIMP_MIN_IN
			 *    min(S)∈S == ⊤
			 */
			In(Min(S), S) -> {
				if (level5) {
					result = DLib.True(ff);
					trace(predicate, result, "SIMP_MIN_IN");
					return result;
				}
			}

			/**
			 * SIMP_MAX_IN
			 *    max(S)∈S == ⊤
			 */
			In(Max(S), S) -> {
				if (level5) {
					result = DLib.True(ff);
					trace(predicate, result, "SIMP_MAX_IN");
					return result;
				}
			}

			/**
			 * SIMP_SPECIAL_IN_ID
			 *    E ↦ E ∈ id == ⊤
			 */
			In(Mapsto(E, E), IdGen()) -> {
				if (level5) {
					result = DLib.True(ff);
					trace(predicate, result, "SIMP_SPECIAL_IN_ID");
					return result;
				}
			}

			/**
			 * SIMP_SPECIAL_IN_SETMINUS_ID
			 *    E ↦ E ∈ r ∖ id == ⊥
			 */
			In(Mapsto(E, E), SetMinus(_, IdGen())) -> {
				if (level5) {
					result = DLib.False(ff);
					trace(predicate, result, "SIMP_SPECIAL_IN_SETMINUS_ID");
					return result;
				}
			}

			/**
			 * SIMP_SPECIAL_IN_DOMRES_ID
			 *    E ↦ E ∈ S ◁ id == E ∈ S
			 */
			In(Mapsto(E, E), DomRes(S, IdGen())) -> {
				if (level5) {
					result = makeRelationalPredicate(IN, `E, `S);
					trace(predicate, result, "SIMP_SPECIAL_IN_DOMRES_ID");
					return result;
				}
			}

			/**
			 * SIMP_SPECIAL_IN_SETMINUS_DOMRES_ID
			 *    E ↦ E ∈ r ∖ (S ◁ id) == E ↦ E ∈ S ⩤ r
			 */
			In(M@Mapsto(E, E), SetMinus(r, DomRes(S, IdGen()))) -> {
				if (level5) {
					result = makeRelationalPredicate(IN, `M, makeBinaryExpression(DOMSUB, `S, `r));
					trace(predicate, result, "SIMP_SPECIAL_IN_SETMINUS_DOMRES_ID");
					return result;
				}
			}
	    }
	    return predicate;
	}

    @ProverRule( { "SIMP_SETENUM_EQUAL_EMPTY", "SIMP_BINTER_SING_EQUAL_EMPTY",
			"SIMP_BINTER_SETMINUS_EQUAL_EMPTY", "SIMP_BUNION_EQUAL_EMPTY",
			"SIMP_SETMINUS_EQUAL_EMPTY", "SIMP_POW_EQUAL_EMPTY",
			"SIMP_POW1_EQUAL_EMPTY", "SIMP_KUNION_EQUAL_EMPTY",
			"SIMP_QUNION_EQUAL_EMPTY", "SIMP_NATURAL_EQUAL_EMPTY",
			"SIMP_NATURAL1_EQUAL_EMPTY", "SIMP_CPROD_EQUAL_EMPTY",
		    "SIMP_UPTO_EQUAL_EMPTY", "SIMP_SREL_EQUAL_EMPTY",
		    "SIMP_STREL_EQUAL_EMPTY", "SIMP_DOM_EQUAL_EMPTY",
			"SIMP_RAN_EQUAL_EMPTY", "SIMP_FCOMP_EQUAL_EMPTY",
			"SIMP_BCOMP_EQUAL_EMPTY", "SIMP_DOMRES_EQUAL_EMPTY",
			"SIMP_DOMSUB_EQUAL_EMPTY", "SIMP_RANRES_EQUAL_EMPTY",
			"SIMP_RANSUB_EQUAL_EMPTY", "SIMP_CONVERSE_EQUAL_EMPTY",
			"SIMP_RELIMAGE_EQUAL_EMPTY", "SIMP_OVERL_EQUAL_EMPTY",
			"SIMP_DPROD_EQUAL_EMPTY", "SIMP_PPROD_EQUAL_EMPTY",
			"SIMP_ID_EQUAL_EMPTY", "SIMP_PRJ1_EQUAL_EMPTY",
			"SIMP_PRJ2_EQUAL_EMPTY"})
	private Predicate rewriteEqualsEmptySet(Predicate predicate,
			Expression expression) {
		final FormulaFactory ff = predicate.getFactory();
		final Predicate result;
	    %match (Expression expression) {

			/**
			 * SIMP_SETENUM_EQUAL_EMPTY
			 * {A...B} = ∅ == ⊥
			 */
			SetExtension(eList(_, _*)) -> {
				result = DLib.False(ff);
				trace(predicate, result, "SIMP_SETENUM_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_BINTER_SING_EQUAL_EMPTY
			 * A ∩...∩ {a} ∩...∩ B = ∅ == ¬ a ∈ A ∩...∩ B
			 */
			BInter(children@eList(_*, singleton@SetExtension(eList(a)), _*)) -> {
				final Expression[] newChildren = remove(`singleton, `children);
				if (newChildren.length == 1 || !containsSingleton(newChildren)) {
					result = makeUnaryPredicate(NOT,
							makeRelationalPredicate(IN, `a,
								makeAssociativeExpression(BINTER, newChildren)));
					trace(predicate, result, "SIMP_BINTER_SING_EQUAL_EMPTY");
					return result;
				}
	        }

            /**
			 * SIMP_BINTER_SETMINUS_EQUAL_EMPTY
			 * (A ∖ B) ∩ C ∩ (D ∖ E) == (A ∩ C ∩ D) ⊆ B ∪ E
			 */
			BInter(children@eList(_*, SetMinus(_, _), _*)) -> {
				final List<Expression> lhs = new ArrayList<Expression>();
				final List<Expression> rhs = new ArrayList<Expression>();
				for (final Expression child : `children) {
					if (child.getTag() == SETMINUS) {
						final BinaryExpression binExpr = (BinaryExpression) child;
						lhs.add(binExpr.getLeft());
						rhs.add(binExpr.getRight());
					} else {
						lhs.add(child);
					}
				}
				result = makeRelationalPredicate(SUBSETEQ,
						makeAssociativeExpression(BINTER, lhs),
						makeAssociativeExpression(BUNION, rhs));
				trace(predicate, result, "SIMP_BINTER_SETMINUS_EQUAL_EMPTY");
				return result;
	         }

			/**
			 * SIMP_BUNION_EQUAL_EMPTY
			 * A ∪...∪ B = ∅ == A=∅ ∧...∧ B=∅
			 */
			BUnion(children) -> {
				result = makeAreAllEmpty(`children);
				trace(predicate, result, "SIMP_BUNION_EQUAL_EMPTY");
				return result;
			}

            /**
             * SIMP_SETMINUS_EQUAL_EMPTY
             *    A ∖ B = ∅ == A ⊆ B
             */
			SetMinus(A, B) -> {
				result = makeRelationalPredicate(SUBSETEQ, `A, `B);
				trace(predicate, result, "SIMP_SETMINUS_EQUAL_EMPTY");
				return result;
			}

            /**
             * SIMP_POW_EQUAL_EMPTY
             *    ℙ(S) = ∅ == ⊥
             */
			Pow(_) -> {
				result = DLib.False(ff);
				trace(predicate, result, "SIMP_POW_EQUAL_EMPTY");
				return result;
			}

			/**
             * SIMP_POW1_EQUAL_EMPTY
             *    ℙ1(S) = ∅ == S=∅
             */
			Pow1(S) -> {
				result = makeIsEmpty(`S);
				trace(predicate, result, "SIMP_POW1_EQUAL_EMPTY");
				return result;
			}

            /**
             * SIMP_KUNION_EQUAL_EMPTY
             *    union(S) = ∅ == S⊆{∅}
             */
			Union(S) -> {
				final SetExtension singleton = makeSetExtension(
						makeEmptySet(ff, expression.getType()));
				result = makeRelationalPredicate(SUBSETEQ, `S, singleton);
				trace(predicate, result, "SIMP_KUNION_EQUAL_EMPTY");
				return result;
			}

			/**
             * SIMP_QUNION_EQUAL_EMPTY
             *    (⋃x· P(x) ∣ E(x)) = ∅ == ∀x· P(x) ⇒ E(x)=∅
             */
			Qunion(bidl, P, E) -> {
				final Predicate pred = makeBinaryPredicate(LIMP,
						`P,
						makeIsEmpty(`E));
				result = makeQuantifiedPredicate(FORALL, `bidl, pred);
				trace(predicate, result, "SIMP_QUNION_EQUAL_EMPTY");
				return result;
			}

            /**
			 * SIMP_NATURAL_EQUAL_EMPTY
			 *    ℕ = ∅ == ⊥
			 */
			Natural() -> {
				result = DLib.False(ff);
				trace(predicate, result, "SIMP_NATURAL_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_NATURAL1_EQUAL_EMPTY
			 *    ℕ1 = ∅ == ⊥
			 */
			Natural1() -> {
				result = DLib.False(ff);
				trace(predicate, result, "SIMP_NATURAL1_EQUAL_EMPTY");
				return result;
			}

            /**
             * SIMP_CPROD_EQUAL_EMPTY
             *    S × T = ∅ == S=∅ ∨ T=∅
             */
            Cprod(S, T) -> {
				result = makeAssociativePredicate(LOR,
						makeIsEmpty(`S),
						makeIsEmpty(`T));
				trace(predicate, result, "SIMP_CPROD_EQUAL_EMPTY");
				return result;
            }

            /**
			 * SIMP_UPTO_EQUAL_EMPTY
			 *    i‥j = ∅ == i > j
			 */
			UpTo(i, j) -> {
				result = makeRelationalPredicate(GT, `i, `j);
				trace(predicate, result, "SIMP_UPTO_EQUAL_EMPTY");
				return result;
			}

            /**
			 * SIMP_SREL_EQUAL_EMPTY
			 *    A  B = ∅ == A=∅  ∧ ¬ B=∅
			 */
			Srel(A, B) -> {
				result = makeAssociativePredicate(LAND,
						makeIsEmpty(`A),
						makeIsNotEmpty(`B));
				trace(predicate, result, "SIMP_SREL_EQUAL_EMPTY");
				return result;
			}

            /**
			 * SIMP_STREL_EQUAL_EMPTY
			 *    A  B = ∅ == A=∅  ⇔ ¬ B=∅
			 */
			Strel(A, B) -> {
				result = makeBinaryPredicate(LEQV,
						makeIsEmpty(`A),
						makeIsNotEmpty(`B));
				trace(predicate, result, "SIMP_STREL_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_DOM_EQUAL_EMPTY
			 *    dom(r) = ∅ == r = ∅
			 */
			Dom(r) -> {
				result = makeIsEmpty(`r);
				trace(predicate, result, "SIMP_DOM_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_RAN_EQUAL_EMPTY
			 *    ran(r) = ∅ == r = ∅
			 */
			Ran(r) -> {
				result = makeIsEmpty(`r);
				trace(predicate, result, "SIMP_RAN_EQUAL_EMPTY");
				return result;
			 }

			/**
			 * SIMP_FCOMP_EQUAL_EMPTY
			 *    p ; q = ∅ == (ran(p) ∩ dom(q) = ∅)
			 */
			Fcomp(eList(p, q)) -> {
				result = makeEmptyInter(
							makeUnaryExpression(KRAN, `p),
							makeUnaryExpression(KDOM, `q));
				trace(predicate, result, "SIMP_FCOMP_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_BCOMP_EQUAL_EMPTY
			 *    p ∘ q = ∅ == (ran(q) ∩ dom(p) = ∅)
			 */
			Bcomp(eList(p, q)) -> {
				result = makeEmptyInter(
							makeUnaryExpression(KRAN, `q),
							makeUnaryExpression(KDOM, `p));
				trace(predicate, result, "SIMP_BCOMP_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_DOMRES_EQUAL_EMPTY
			 *    S ◁ r = ∅ == (dom(r) ∩ S = ∅)
			 */
			DomRes(S, r) -> {
				result = makeEmptyInter(makeUnaryExpression(KDOM, `r), `S);
				trace(predicate, result, "SIMP_DOMRES_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_DOMSUB_EQUAL_EMPTY
			 *    S ⩤ r = ∅ ⇔ dom(r) ⊆ S
			 */
			DomSub(S, r) -> {
				result = makeRelationalPredicate(
						SUBSETEQ,
						makeUnaryExpression(KDOM, `r),
						`S);
				trace(predicate, result, "SIMP_DOMSUB_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_RANRES_EQUAL_EMPTY
			 *    r ▷ S = ∅ == (ran(r) ∩ S = ∅)
			 */
			RanRes(r, S) -> {
				result = makeEmptyInter(makeUnaryExpression(KRAN, `r), `S);
				trace(predicate, result, "SIMP_RANRES_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_RANSUB_EQUAL_EMPTY
			 *    r ⩥ S = ∅ ⇔ ran(r) ⊆ S
			 */
			RanSub(r, S) -> {
				result = makeRelationalPredicate(SUBSETEQ,
						makeUnaryExpression(KRAN, `r),
						`S);
				trace(predicate, result, "SIMP_RANSUB_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_CONVERSE_EQUAL_EMPTY
			 *    r∼ = ∅ == r = ∅
			 */
			Converse(r) -> {
				result = makeIsEmpty(`r);
				trace(predicate, result, "SIMP_CONVERSE_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_RELIMAGE_EQUAL_EMPTY
             * 	  r[S] = ∅ == S ◁ r = ∅
			 */
			RelImage(r, S) -> {
				result = makeIsEmpty(makeBinaryExpression(DOMRES, `S, `r));
				trace(predicate, result, "SIMP_RELIMAGE_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_OVERL_EQUAL_EMPTY
             *   r  ...  s = ∅ == r=∅ ∧ ... ∧ s=∅
			 */
			Ovr(children) -> {
				result = makeAreAllEmpty(`children);
				trace(predicate, result, "SIMP_OVERL_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_DPROD_EQUAL_EMPTY
             * 	  p ⊗ q = ∅ ⇔ (dom(p) ∩ dom(q) = ∅)
			 */
			Dprod(p, q) -> {
				result = makeEmptyInter(
							makeUnaryExpression(KDOM, `p),
							makeUnaryExpression(KDOM, `q));
				trace(predicate, result, "SIMP_DPROD_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_PPROD_EQUAL_EMPTY
             * 	  p ∥ q = ∅ == p=∅ ∨ q=∅
			 */
			Pprod(p, q) -> {
				result = makeAssociativePredicate(LOR,
						makeIsEmpty(`p),
						makeIsEmpty(`q));
				trace(predicate, result, "SIMP_PPROD_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_ID_EQUAL_EMPTY
             * 	  id = ∅ == ⊥
			 */
			IdGen() -> {
				result = DLib.False(ff);
				trace(predicate, result, "SIMP_ID_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_PRJ1_EQUAL_EMPTY
             * 	  prj1 = ∅ == ⊥
			 */
			Prj1Gen() -> {
				result = DLib.False(ff);
				trace(predicate, result, "SIMP_PRJ1_EQUAL_EMPTY");
				return result;
			}

			/**
			 * SIMP_PRJ2_EQUAL_EMPTY
             * 	  prj2 = ∅ == ⊥
			 */
			Prj2Gen() -> {
				result = DLib.False(ff);
				trace(predicate, result, "SIMP_PRJ2_EQUAL_EMPTY");
				return result;
			}
	    }
	    return null;
	}

    @ProverRule( {"SIMP_BINTER_EQUAL_TYPE", "SIMP_SETMINUS_EQUAL_TYPE",
		"SIMP_KINTER_EQUAL_TYPE", "SIMP_QINTER_EQUAL_TYPE",
		"SIMP_CPROD_EQUAL_TYPE", "SIMP_UPTO_EQUAL_INTEGER",
		"SIMP_DOMRES_EQUAL_TYPE",
		"SIMP_DOMSUB_EQUAL_TYPE", "SIMP_RANRES_EQUAL_TYPE",
		"SIMP_RANSUB_EQUAL_TYPE", "SIMP_CONVERSE_EQUAL_TYPE",
		"SIMP_DPROD_EQUAL_TYPE", "SIMP_PPROD_EQUAL_TYPE"})
    private Predicate rewriteEqualsType(Predicate predicate,
			Expression expression) {
		final FormulaFactory ff = predicate.getFactory();
		final Predicate result;
	    %match (Expression expression) {

			/**
			 * SIMP_BINTER_EQUAL_TYPE
			 * A ∩...∩ B = Ty == A=Ty ∧...∧ B=Ty
			 */
			BInter(children) -> {
				result = makeAreAllType(`children);
				trace(predicate, result, "SIMP_BINTER_EQUAL_TYPE");
				return result;
			}

			/**
			 * SIMP_SETMINUS_EQUAL_TYPE
			 * A ∖ B = Ty == A=Ty ∧ B=∅
			 */
			SetMinus(A, B) -> {
				Predicate [] newChildren = new Predicate[2];
				newChildren[0] = makeIsType(`A);
				newChildren[1] = makeIsEmpty(`B);
				result =  makeAssociativePredicate(LAND, newChildren);
				trace(predicate, result, "SIMP_SETMINUS_EQUAL_TYPE");
				return result;
			}

			/**
             * SIMP_KINTER_EQUAL_TYPE
             *    inter(S) = Ty == S={Ty}
             */
			Inter(S) -> {
				final SetExtension singleton = makeSetExtension(
						getBaseTypeExpression(expression));
				result = makeRelationalPredicate(EQUAL, `S, singleton);
				trace(predicate, result, "SIMP_KINTER_EQUAL_TYPE");
				return result;
			}

			/**
             * SIMP_QINTER_EQUAL_TYPE
             *    (⋂x· P(x) ∣ E(x)) = Ty == ∀x· P(x) ⇒ E(x)=Ty
             */
			Qinter(bidl, P, E) -> {
				final Predicate pred = makeBinaryPredicate(LIMP,
						`P,
						makeIsType(`E));
				result = makeQuantifiedPredicate(FORALL, `bidl, pred);
				trace(predicate, result, "SIMP_QINTER_EQUAL_TYPE");
				return result;
			}

			/**
			 * SIMP_CPROD_EQUAL_TYPE
			 * S × T = Ty == S=Ta ∧ T=Tb
			 */
			Cprod(S, T) -> {
				result = makeAreAllType(`S, `T);
				trace(predicate, result, "SIMP_CPROD_EQUAL_TYPE");
				return result;
			}

            /**
			 * SIMP_UPTO_EQUAL_INTEGER
			 *    i‥j = ℤ == ⊥
			 */
			UpTo(_, _) -> {
				result = DLib.False(ff);
				trace(predicate, result, "SIMP_UPTO_EQUAL_INTEGER");
				return result;
			}

			/**
			 * SIMP_DOMRES_EQUAL_TYPE
			 *    S ◁ r = Ty == A=Ta ∧ r=Ty (where Ty is a type expression and
			 *    Ty = Ta × Tb)
			 */
			DomRes(S, r) -> {
				result = makeAreAllType(`S, `r);
				trace(predicate, result, "SIMP_DOMRES_EQUAL_TYPE");
				return result;
			}

			/**
			 * SIMP_DOMSUB_EQUAL_TYPE
			 * A ⩤ r = Ty == A=∅ ∧ r=Ty
			 */
			DomSub(A, r) -> {
				Predicate [] newChildren = new Predicate[2];
				newChildren[0] = makeIsEmpty(`A);
				newChildren[1] = makeIsType(`r);
				result =  makeAssociativePredicate(LAND, newChildren);
				trace(predicate, result, "SIMP_DOMSUB_EQUAL_TYPE");
				return result;
			}

			/**
			 * SIMP_RANRES_EQUAL_TYPE
			 * r ▷ A = Ty == A=Tb ∧ r=Ty (where Ty is a type expression and
			 * Ty = Ta × Tb)
			 */
			RanRes(r, A) -> {
				result =  makeAreAllType(`A, `r);
				trace(predicate, result, "SIMP_RANRES_EQUAL_TYPE");
				return result;
			}

			/**
			 * SIMP_RANSUB_EQUAL_TYPE
			 * r ⩥ A = Ty == A=∅ ∧ r=Ty
			 */
			RanSub(r, A) -> {
				Predicate [] newChildren = new Predicate[2];
				newChildren[0] = makeIsEmpty(`A);
				newChildren[1] = makeIsType(`r);
				result =  makeAssociativePredicate(LAND, newChildren);
				trace(predicate, result, "SIMP_RANSUB_EQUAL_TYPE");
				return result;
			}

			/**
			 * SIMP_CONVERSE_EQUAL_TYPE
			 *    r∼ = T×S == r = S×T
			 */
			Converse(r) -> {
				result = makeIsType(`r);
				trace(predicate, result, "SIMP_CONVERSE_EQUAL_TYPE");
				return result;
			}

			/**
			 * SIMP_DPROD_EQUAL_TYPE
             * 	  p ⊗ q = S×(T×U) ⇔ p=S×T ∧ q=S×U
			 */
			Dprod(p, q) -> {
				result =  makeAreAllType(`p, `q);
				trace(predicate, result, "SIMP_DPROD_EQUAL_TYPE");
				return result;
			}

			/**
			 * SIMP_PPROD_EQUAL_TYPE
             * 	  p ∥ q = (S×U)×(T×V) == p=S×T ∧ q=U×V
			 */
			Pprod(p, q) -> {
				result =  makeAreAllType(`p, `q);
				trace(predicate, result, "SIMP_PPROD_EQUAL_TYPE");
				return result;
			}

		}
	    return null;
	}

    private static boolean isDTConstructor(ExtendedExpression expr) {
		return expr.getExtension() instanceof IConstructorExtension;
	}

	private static boolean isTypeConstructor(ExtendedExpression expr) {
		return expr.getExtension() instanceof ITypeConstructorExtension;
	}

	private static Predicate makeConjunctionComparisons(int comparTag, Expression[] args1, Expression[] args2, FormulaFactory ff) {
		assert args1.length == args2.length;
		switch (args1.length) {
		case 0:
			return ff.makeLiteralPredicate(BTRUE, null);
		case 1:
			return ff.makeRelationalPredicate(comparTag, args1[0], args2[0], null);
		default:
			Predicate[] equalPreds = new Predicate[args1.length];
			for (int i = 0; i < args1.length; i++) {
				equalPreds[i] = ff.makeRelationalPredicate(comparTag, args1[i], args2[i], null);
			}
			return ff.makeAssociativePredicate(LAND, equalPreds, null);
		}
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
		final FormulaFactory ff = expression.getFactory();
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
	    		result = simplifyInter(expression);
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
	    		result = simplifyUnion(expression);
	    		trace(expression, result, "SIMP_SPECIAL_BUNION", "SIMP_TYPE_BUNION", "SIMP_MULTI_BUNION");
				return result;
	    	}

	    	/**
	    	 * SIMP_SPECIAL_PLUS
             * Arithmetic 1: E + ... + 0 + ... + F == E + ... + ... + F
	    	 */
	    	Plus(_) -> {
	    		final Expression rewritten = simplifyPlus(expression);
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
	    		final Expression rewritten = simplifyMult(expression);
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
	    		final Expression rewritten = simplifyComp(expression);
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
	    		final Expression rewritten = simplifyComp(expression);
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
	    		final Expression rewritten = simplifyOvr(expression);
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
			"SIMP_RELIMAGE_DOMRES_ID", "SIMP_RELIMAGE_DOMSUB_ID",
			"SIMP_MAPSTO_PRJ1_PRJ2", } )
	@Override
	public Expression rewrite(BinaryExpression expression) {
		final FormulaFactory ff = expression.getFactory();
		final IntegerLiteral number0 = ff.makeIntegerLiteral(ZERO, null);
		final IntegerLiteral number1 = ff.makeIntegerLiteral(ONE, null);
		final Expression result;
	    %match (Expression expression) {

			/**
             * SIMP_MULTI_SETMINUS
	    	 * Set Theory 11: S ∖ S == ∅
	    	 */
	    	SetMinus(S, S) -> {
	    		result = makeEmptySet(ff, `S.getType());
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
					result = makeEmptySet(ff, `T.getType());
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
	    		result = number1;
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
	    		result = getFaction(`E, `F);
	    		trace(expression, result, "SIMP_DIV_MINUS");
	    		return result;
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(UnMinus(E), IntegerLiteral(F)) -> {
	    		result = getFaction(`expression, `E, `F);
	    		trace(expression, result, "SIMP_DIV_MINUS");
	    		return result;
	    	}

			/**
	    	 * SIMP_DIV_MINUS
             * Arithmetic 10: (−E) ÷ (−F) == E ÷ F
	    	 */
	    	Div(IntegerLiteral(E), UnMinus(F)) -> {
	    		result = getFaction(`expression, `E, `F);
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
	    		result = getFaction(`expression, `E, `F);
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
				result = makeEmptySet(ff, ff.makePowerSetType(Lib.getRangeType(`r)));
	    		trace(expression, result, "SIMP_SPECIAL_RELIMAGE_R");
	    		return result;
			}

			/**
			 * SIMP_SPECIAL_RELIMAGE_L
             * Set Theory: ∅[A] == ∅
			 */
			RelImage(r@EmptySet(), _) -> {
				result = makeEmptySet(ff, ff.makePowerSetType(Lib.getRangeType(`r)));
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
			 		result = makeEmptySet(ff, expression.getType());
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
			 		result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeSetExtension(makeEmptySet(ff, expression.getType().getBaseType()));
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
					result = makeSetExtension(makeEmptySet(ff, expression.getType().getBaseType()));
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
					result = makeSetExtension(makeEmptySet(ff, expression.getType().getBaseType()));
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, `cr.getType());
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
					result = makeEmptySet(ff, `cr.getType());
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

			/**
			 * SIMP_MAPSTO_PRJ1_PRJ2
			 *    prj1(E) ↦ prj2(E) == E
			 */
			Mapsto(FunImage(Prj1Gen(), E), FunImage(Prj2Gen(), E)) -> {
				if (level4) {
					result = `E;
					trace(expression, result, "SIMP_MAPSTO_PRJ1_PRJ2");
					return result;
				}
			}

		}
	    return expression;
	}

	@ProverRule( { "DEF_PRED" })
	@Override
	public Expression rewrite(AtomicExpression expression) {
		final FormulaFactory ff = expression.getFactory();
		final Expression result;
		%match (Expression expression) {
			
			/**
			 * DEF_PRED
			 * pred == succ∼
			 */
			PRED() -> {
				if (level3) {
					result = makeUnaryExpression(CONVERSE, makeAtomicExpression(ff, KSUCC));
					trace(expression, result, "DEF_PRED");
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
			 "SIMP_CARD_LAMBDA", "SIMP_MULTI_DOM_DOMSUB", 
			 "SIMP_MULTI_DOM_DOMRES", "SIMP_MULTI_RAN_RANSUB",
			 "SIMP_MULTI_RAN_RANRES", "SIMP_DOM_SUCC", "SIMP_RAN_SUCC" })
	@Override
	public Expression rewrite(UnaryExpression expression) {
		final FormulaFactory ff = expression.getFactory();
		final IntegerLiteral number0 = ff.makeIntegerLiteral(ZERO, null);
		final IntegerLiteral number1 = ff.makeIntegerLiteral(ONE, null);
		final IntegerLiteral number2 = ff.makeIntegerLiteral(TWO, null);
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
				result = makeEmptySet(ff, ff.makePowerSetType(Lib.getDomainType(`r)));
	    		trace(expression, result, "SIMP_SPECIAL_DOM");
	    		return result;
			}

			/**
             * SIMP_SPECIAL_RAN
			 * Set Theory: ran(∅) == ∅
			 */
			Ran(r@EmptySet()) -> {
				result = makeEmptySet(ff, ff.makePowerSetType(Lib.getRangeType(`r)));
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, expression.getType());
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
					result = makeEmptySet(ff, `S.getType());
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
					result = s.toExpression();
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
					result = s.toExpression();
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
					result = st.toExpression();
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
					result = st.toExpression();
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
					result = st.toExpression();
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
					result = st.toExpression();
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
					result = makeCard(`op.getType().getSource().toExpression());
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
			
			/**
			 * SIMP_MULTI_DOM_DOMSUB
			 *    dom(A⩤f) == dom(f)∖A
			 */
			Dom(DomSub(A, f)) -> {
				if (level3) {
					result = makeBinaryExpression(SETMINUS, makeUnaryExpression(KDOM, `f), `A);
					trace(expression, result, "SIMP_MULTI_DOM_DOMSUB");
					return result;
				}
			}

			/**
			 * SIMP_MULTI_DOM_DOMRES
			 *    dom(A◁f) == dom(f)∩A
			 */
			Dom(DomRes(A, f)) -> {
				if (level3) {
					result = makeAssociativeExpression(BINTER, makeUnaryExpression(KDOM, `f), `A);
					trace(expression, result, "SIMP_MULTI_DOM_DOMRES");
					return result;
				}
			}

			/**
			 * SIMP_MULTI_RAN_RANSUB
			 *    ran(f⩥A) == ran(f)∖A
			 */
			Ran(RanSub(f, A)) -> {
				if (level3) {
					result = makeBinaryExpression(SETMINUS, makeUnaryExpression(KRAN, `f), `A);
					trace(expression, result, "SIMP_MULTI_RAN_RANSUB");
					return result;
				}
			}

			/**
			 * SIMP_MULTI_RAN_RANRES
			 *    ran(f▷A) == ran(f)∩A
			 */
			Ran(RanRes(f, A)) -> {
				if (level3) {
					result = makeAssociativeExpression(BINTER, makeUnaryExpression(KRAN, `f), `A);
					trace(expression, result, "SIMP_MULTI_RAN_RANRES");
					return result;
				}
			}

			/**
			 * SIMP_DOM_SUCC
			 *    dom(succ) == ℤ
			 */
			Dom(SUCC()) -> {
				if (level3) {
					result = makeAtomicExpression(ff, INTEGER);
					trace(expression, result, "SIMP_DOM_SUCC");
					return result;
				}
			}

			/**
			 * SIMP_RAN_SUCC
			 *    ran(succ) == ℤ
			 */
			Ran(SUCC()) -> {
				if (level3) {
					result = makeAtomicExpression(ff, INTEGER);
					trace(expression, result, "SIMP_RAN_SUCC");
					return result;
				}
			}

	    }
	    return expression;
	}

	@ProverRule( { "SIMP_SPECIAL_KBOOL_BFALSE", "SIMP_SPECIAL_KBOOL_BTRUE",
			"SIMP_KBOOL_LIT_EQUAL_TRUE" })
    @Override
	public Expression rewrite(BoolExpression expression) {
		final FormulaFactory ff = expression.getFactory();
		final Expression result;
	    %match (Expression expression) {
	   		/**
             * SIMP_SPECIAL_KBOOL_BFALSE
	    	 * Set Theory:	bool(⊥) = FALSE
	    	 */
	    	Bool(BFALSE()) -> {
				result = DLib.FALSE(ff);
	    		trace(expression, result, "SIMP_SPECIAL_KBOOL_BFALSE");
	    		return result;
			}

	   		/**
             * SIMP_SPECIAL_KBOOL_BTRUE
	    	 * Set Theory:	bool(⊤) = TRUE
	    	 */
	    	Bool(BTRUE()) -> {
				result = DLib.TRUE(ff);
	    		trace(expression, result, "SIMP_SPECIAL_KBOOL_BTRUE");
	    		return result;
			}

			/**
			 * SIMP_KBOOL_LIT_EQUAL_TRUE
			 *    bool(B = TRUE) == B
			 *    bool(TRUE = B) == B
			 */
			Bool(Equal(B, TRUE())) || Bool(Equal(TRUE(), B)) << expression -> {
				if (level5) {
					result = `B;
					trace(expression, result, "SIMP_KBOOL_LIT_EQUAL_TRUE");
					return result;
				}
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
		final FormulaFactory ff = expression.getFactory();
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
		final FormulaFactory ff = expression.getFactory();
    	final Expression result;
    	%match (Expression expression) {

    		/**
    		 * SIMP_DESTR_CONSTR:
    		 * destr(cons(a_1, ..., a_n))  ==  a_i   [i is the param index of the destructor]
    		 */
			ExtendedExpression(eList(cons@ExtendedExpression(as,_)), pList()) -> {
				final int idx = getArgIndex(expression,	(ExtendedExpression) `cons);
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
		final FormulaFactory ff = expression.getFactory();
    	final Expression result;
    	%match (Expression expression) {

    		/**
	    	 * SIMP_SPECIAL_COMPSET_BFALSE
	    	 *    {x · ⊥ ∣ x} == ∅
	    	 */
	    	Cset(_, BFALSE(), _) -> {
	    		if (level2) {
	    			result = makeEmptySet(ff, expression.getType());
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
	    			result = `E.getType().toExpression();
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
	    			result = makeEmptySet(ff, expression.getType());
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
   					result = `S.shiftBoundIdentifiers(-nbBound);
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
	    		if (level2 && notLocallyBound(`S, nbBound) && ((BoundIdentifier)`bi).getBoundIndex() < nbBound) {
	    			result = makeUnaryExpression(POW, `S.shiftBoundIdentifiers(-nbBound));
	    			trace(expression, result, "SIMP_COMPSET_SUBSETEQ");
    				return result;
	    		}
	    	}

    	}
    	return expression;
    }

    private static int getArgIndex(ExtendedExpression destrExpr,
    		ExtendedExpression consExpr) {
    	final IExpressionExtension destrExt = destrExpr.getExtension();
    	if (!(destrExt instanceof IDestructorExtension)) {
			return -1;
		}
    	final IDestructorExtension destr = (IDestructorExtension) destrExt;

    	final IExpressionExtension consExt = consExpr.getExtension();
    	if (!(consExt instanceof IConstructorExtension)) {
			return -1;
		}
    	final IConstructorExtension cons = (IConstructorExtension) consExt;

    	return cons.getArgumentIndex(destr);
    }

}
