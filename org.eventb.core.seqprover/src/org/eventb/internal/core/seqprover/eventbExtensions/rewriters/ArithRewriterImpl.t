/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used AdditiveSimplifier to optimize simplifications
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
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
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.IPosition;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.ProverRule;

/**
 * Basic automated arithmetic rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class ArithRewriterImpl extends DefaultRewriter {
	
	protected AssociativeExpression makeAssociativeExpression(int tag, Collection<Expression> children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	protected BinaryExpression makeBinaryExpression(int tag, Expression left, Expression right) {
		return ff.makeBinaryExpression(tag, left, right, null);
	}

	protected UnaryExpression makeUnaryExpression(int tag, Expression child) {
		return ff.makeUnaryExpression(tag, child, null);
	}

	protected RelationalPredicate makeRelationalPredicate(int tag, Expression left,
			Expression right) {
		return ff.makeRelationalPredicate(tag, left, right, null);
	}

	public ArithRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
    private static <T extends Formula<T>> T findSame(T f, T[] candidates) {
        for (T candidate : candidates) {
            if (f.equals(candidate)) {
                return candidate;
            }
        }
        return null;
    }
    
    private static IPosition[] getPositions(Formula<?> formula,
            Formula<?>... subFormulae) {
        final IPosition[] positions = new IPosition[subFormulae.length];
        for (int i = 0; i < positions.length; i++) {
            positions[i] = formula.getPosition(subFormulae[i]
                    .getSourceLocation());
        }
        return positions;
    }

    private Expression simplify(Expression expression, Expression e1,
            Expression e2) {
        final IPosition[] positions = getPositions(expression, e1, e2);
        return AdditiveSimplifier.simplify(expression, positions, ff);
    }

    private RelationalPredicate simplify(RelationalPredicate predicate,
            Expression e1, Expression e2) {
        final IPosition[] positions = getPositions(predicate, e1, e2);
        return AdditiveSimplifier.simplify(predicate, positions, ff);
    }
    
	%include {FormulaV2.tom}
	
    @ProverRule( { "SIMP_MINUS_PLUS_L", "SIMP_MINUS_PLUS_R",
            "SIMP_MINUS_PLUS_PLUS", "SIMP_MINUS_UNMINUS" })
	@Override
	public Expression rewrite(BinaryExpression expression) {
	    %match (Expression expression) {
	        /**
             * SIMP_MINUS_PLUS_L
	         * Arithmetics: (A + ... + C + ... + B) − C == A + .. + B
	         */
			Minus(Plus(children), C) -> {
			    final Expression sameAsC = findSame(`C, `children);
                if (sameAsC != null) {
                    return simplify(expression, `C, sameAsC);
                } 
			}
			
			/**
             * SIMP_MINUS_PLUS_R
			 * Arithmetics: C − (A + ... + C + ... + B)  ==  −(A + ... + B)
			 */
			Minus(C, Plus(children)) -> {
                final Expression sameAsC = findSame(`C, `children);
                if (sameAsC != null) {
                    return simplify(expression, `C, sameAsC);
                } 
			}
			
			/**
             * SIMP_MINUS_PLUS_PLUS
			 * Arithmetics: (A + ... + E + ... + B) − (C + ... + E + ... + D)  == (A + ... + B) − (C + ... + D)
			 */
			Minus(Plus(left), Plus(right)) -> {
			    for(Expression e: `left) {
			        final Expression sameAsE = findSame(e, `right);
                    if (sameAsE != null) {
                        return simplify(expression, e, sameAsE);
                    } 
			    }
			}
			
            /**
             * SIMP_MINUS_UNMINUS
             * Arithmetics: A − (− B)  == A + B
             */
			Minus(A, UnMinus(B)) -> {
			    final Expression result = makeAssociativeExpression(
			                                     Expression.PLUS,
			                                     Arrays.asList(`A, `B));
                if(autoFlatteningMode()) {
                    return result.flatten(ff);
                }
                return result;
			}
			
			Minus(A, IntegerLiteral(b)) -> {
			    if (`b.signum() < 0) {
			        final IntegerLiteral bNeg = ff.makeIntegerLiteral(`b.negate(), null);
                    final Expression result = makeAssociativeExpression(
                                                     Expression.PLUS,
                                                     Arrays.asList(`A, bNeg));
                    if(autoFlatteningMode()) {
                        return result.flatten(ff);
                    }
                    return result;
			    }
			}
	    }
	    return expression;
	}
    
    @ProverRule("SIMP_MULTI_PLUS_MINUS")
	@Override
	public Expression rewrite(AssociativeExpression expression) {
	    %match (Expression expression) {
			/**
	         * SIMP_MULTI_PLUS_MINUS
             * Arithmetics: (A + ... + D + ... + (C − D) + ... + B) == A + ... + C + ... + B
	         */
			Plus(children) -> {
			    // search for children of the form (C-D)
			    final List<Expression> minusChildren;
			    for (Expression child: `children) {
			        if(child.getTag() == Expression.MINUS) {
			            final Expression d = ((BinaryExpression) child).getRight();
                        final Expression sameAsD = findSame(d, `children);
                        if (sameAsD != null) {
                            return simplify(expression, d, sameAsD);
                        } 
			        }
			    }
			}   
		}
	    return expression;
	}

    @ProverRule( { "SIMP_MULTI_ARITHREL_PLUS_PLUS", "SIMP_MULTI_ARITHREL_PLUS_R",
            "SIMP_MULTI_ARITHREL_PLUS_L", "SIMP_MULTI_ARITHREL_MINUS_MINUS_R",
            "SIMP_MULTI_ARITHREL_MINUS_MINUS_L" })
	@Override
	public Predicate rewrite(RelationalPredicate predicate) {
		%match (Predicate predicate) {
			/**
             * SIMP_MULTI_ARITHREL_PLUS_PLUS
             * Arithmetic: A + ... + E + ... + B  < C + ... + E + ... + D   == A + ... + B = C + ... + D
			 */
			(Equal|Lt|Le|Gt|Ge)(Plus(childrenLeft), Plus(childrenRight)) -> {
			    for(Expression left: `childrenLeft) {
			        final Expression sameAsLeft = findSame(left, `childrenRight);
			        if (sameAsLeft != null) {
                        return simplify(predicate, left, sameAsLeft);
                    }
			    }
			}

			/**
             * SIMP_MULTI_ARITHREL_PLUS_R
			 * Arithmetic: C < A + ... + C ... + B   ==   0 < A + ... + B
			 */
			(Equal|Lt|Le|Gt|Ge)(C, Plus(children)) -> {
                final Expression sameAsC = findSame(`C, `children);
                if (sameAsC != null) {
                    return simplify(predicate, `C, sameAsC);
                }
			}

			/**
			 * SIMP_MULTI_ARITHREL_PLUS_L
             * Arithmetic: A + ... + C ... + B < C   ==   A + ... + B < 0
			 */
			(Equal|Lt|Le|Gt|Ge)(Plus(children), C) -> {
                final Expression sameAsC = findSame(`C, `children);
                if (sameAsC != null) {
                    return simplify(predicate, `C, sameAsC);
                }
			}

			/**
             * SIMP_MULTI_ARITHREL_MINUS_MINUS_R
			 * Arithmetic: A − C < B − C  == A < B
			 */
			(Equal|Lt|Le|Gt|Ge)(Minus(_, c1@C), Minus(_, c2@C)) -> {
			    return simplify(predicate, `c1, `c2);
			}
			
			/**
             * SIMP_MULTI_ARITHREL_MINUS_MINUS_L
			 * Arithmetic: C − A < C − B  == B < A
			 */
			(Equal|Lt|Le|Gt|Ge)(Minus(C, A), Minus(C, B)) -> {
				return makeRelationalPredicate(predicate.getTag(), `B, `A);
			}
		}
		return predicate;
	}
}
