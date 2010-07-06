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

import static org.eventb.core.ast.IPosition.ROOT;

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
	
    private static final IPosition LEFT_CHILD = ROOT.getFirstChild();
    private static final IPosition LEFT_FIRST_CHILD = LEFT_CHILD.getFirstChild();
    private static final IPosition RIGHT_CHILD = LEFT_CHILD.getNextSibling();
    private static final IPosition RIGHT_FIRST_CHILD = RIGHT_CHILD.getFirstChild();

    private static IPosition getSecondChild(IPosition position) {
        return position.getFirstChild().getNextSibling();
    }

	private static class SameFormulaeFinder {
    
        private final Formula<?>[] left;
        private final IPosition firstLeftPos;
        private final Formula<?>[] right;
        private final IPosition firstRightPos;
    
        public SameFormulaeFinder(Formula<?> left, IPosition leftPos, Formula<?>[] right, IPosition firstRightPos) {
            this(new Formula<?>[] {left}, leftPos, right, firstRightPos);
        }

        public SameFormulaeFinder(Formula<?>[] left, IPosition firstLeftPos, Formula<?>[] right, IPosition firstRightPos) {
            this.left = left;
            this.firstLeftPos = firstLeftPos;
            this.right = right;
            this.firstRightPos = firstRightPos;
        }

        public IPosition[] findSame() {
            for (int l = 0; l < left.length; l++) {
                final int r = getEqualsIndex(left[l], right);
                if (r >= 0) {
                    final IPosition samePosLeft = nextSibling(firstLeftPos, l);
                    final IPosition samePosRight = nextSibling(firstRightPos, r);
                    return mArray(samePosLeft, samePosRight); 
                }
            }
            return null;
        }

        private static <T> T[] mArray(T... objs) {
            return objs;
        }

        private static <T> int getEqualsIndex(T f, T[] candidates) {
            for (int i = 0; i < candidates.length; i++) {
                if (f.equals(candidates[i])) {
                    return i;
                }
            }
            return -1;
        }
        
        private static IPosition nextSibling(IPosition position, int n) {
           assert n >= 0;
           IPosition result = position;
           for (int i=0; i<n; i++) {
              result = result.getNextSibling();
           }
           return result;
        }
	}

	protected AssociativeExpression makeAssociativeExpression(int tag, Collection<Expression> children) {
		return ff.makeAssociativeExpression(tag, children, null);
	}

	protected RelationalPredicate makeRelationalPredicate(int tag, Expression left,
			Expression right) {
		return ff.makeRelationalPredicate(tag, left, right, null);
	}

	public ArithRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
    
    private Expression simplify(Expression expression, IPosition... positions) {
        return AdditiveSimplifier.simplify(expression, positions, ff);
    }

    private RelationalPredicate simplify(RelationalPredicate predicate,
            IPosition... positions) {
        return AdditiveSimplifier.simplify(predicate, positions, ff);
    }
    
	%include {FormulaV2.tom}
	
    @ProverRule( { "SIMP_MULTI_MINUS_PLUS_L", "SIMP_MULTI_MINUS_PLUS_R",
            "SIMP_MULTI_MINUS_PLUS_PLUS", "SIMP_MINUS_UNMINUS" })
	@Override
	public Expression rewrite(BinaryExpression expression) {
	    %match (Expression expression) {
	        /**
             * SIMP_MULTI_MINUS_PLUS_L
	         * Arithmetics: (A + ... + C + ... + B) − C == A + .. + B
	         */
			Minus(Plus(children), C) -> {
			    final SameFormulaeFinder finder =
			        new SameFormulaeFinder(`C, RIGHT_CHILD,
			                               `children, LEFT_FIRST_CHILD);
                final IPosition[] found = finder.findSame();
			    if (found != null) {
                    return simplify(expression, found);
                } 
			}
			
			/**
             * SIMP_MULTI_MINUS_PLUS_R
			 * Arithmetics: C − (A + ... + C + ... + B)  ==  −(A + ... + B)
			 */
			Minus(C, Plus(children)) -> {
                final SameFormulaeFinder finder =
                    new SameFormulaeFinder(`C, LEFT_CHILD,
                                           `children, RIGHT_FIRST_CHILD);
                final IPosition[] found = finder.findSame();
                if (found != null) {
                    return simplify(expression, found);
                }
			}
			
			/**
             * SIMP_MULTI_MINUS_PLUS_PLUS
			 * Arithmetics: (A + ... + E + ... + B) − (C + ... + E + ... + D)  == (A + ... + B) − (C + ... + D)
			 */
			Minus(Plus(left), Plus(right)) -> {
                final SameFormulaeFinder finder =
                    new SameFormulaeFinder(`left, LEFT_FIRST_CHILD,
                                           `right, RIGHT_FIRST_CHILD);
                final IPosition[] found = finder.findSame();
                if (found != null) {
                    return simplify(expression, found);
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
			    IPosition cMinusDPos = LEFT_CHILD;
			    for (Expression child: `children) {
			        if(child.getTag() == Expression.MINUS) {
			            final Expression d = ((BinaryExpression) child).getRight();
                        final SameFormulaeFinder finder =
                            new SameFormulaeFinder(d, getSecondChild(cMinusDPos),
                                                   `children, LEFT_CHILD);
                        final IPosition[] found = finder.findSame();
                        if (found != null) {
                            return simplify(expression, found);
                        }
			        }
		            cMinusDPos = cMinusDPos.getNextSibling();
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
                final SameFormulaeFinder finder =
                    new SameFormulaeFinder(`childrenLeft, LEFT_FIRST_CHILD,
                                           `childrenRight, RIGHT_FIRST_CHILD);
                final IPosition[] found = finder.findSame();
                if (found != null) {
                    return simplify(predicate, found);
                }
			}

			/**
             * SIMP_MULTI_ARITHREL_PLUS_R
			 * Arithmetic: C < A + ... + C ... + B   ==   0 < A + ... + B
			 */
			(Equal|Lt|Le|Gt|Ge)(C, Plus(children)) -> {
                final SameFormulaeFinder finder =
                    new SameFormulaeFinder(`C, LEFT_CHILD,
                                           `children, RIGHT_FIRST_CHILD);
                final IPosition[] found = finder.findSame();
                if (found != null) {
                    return simplify(predicate, found);
                }
			}

			/**
			 * SIMP_MULTI_ARITHREL_PLUS_L
             * Arithmetic: A + ... + C ... + B < C   ==   A + ... + B < 0
			 */
			(Equal|Lt|Le|Gt|Ge)(Plus(children), C) -> {
                final SameFormulaeFinder finder =
                    new SameFormulaeFinder(`C, RIGHT_CHILD,
                                           `children, LEFT_FIRST_CHILD);
                final IPosition[] found = finder.findSame();
                if (found != null) {
                    return simplify(predicate, found);
                }
			}

			/**
             * SIMP_MULTI_ARITHREL_MINUS_MINUS_R
			 * Arithmetic: A − C < B − C  == A < B
			 */
			(Equal|Lt|Le|Gt|Ge)(Minus(_, C), Minus(_, C)) -> {
			    return simplify(predicate, getSecondChild(LEFT_CHILD),
			                               getSecondChild(RIGHT_CHILD));
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
