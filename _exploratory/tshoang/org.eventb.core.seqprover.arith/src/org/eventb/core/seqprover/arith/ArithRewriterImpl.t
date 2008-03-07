/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.seqprover.arith;

import java.util.ArrayList;
import java.util.Collection;
import java.math.BigInteger;

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
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.seqprover.eventbExtensions.Lib;

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

	public ArithRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {Formula.tom}
	
	@Override
	public Expression rewrite(BinaryExpression expression) {
	    %match (Expression expression) {
	        /**
	         * Arithmetics: (A + ... + C + ... + B) − C = A + .. + B
	         */
			Minus(Plus(children), C) -> {
	        	ArrayList<Expression> remain = new ArrayList<Expression>(`children.length);
	        	boolean found = false;
				for (Expression child : `children) {
				    if (!found && `C.equals(child))
						found = true;
					else
						remain.add(child);
				}
				if (found) {
					if (remain.size() == 1)
						return remain.get(0);
					else
						return makeAssociativeExpression(Expression.PLUS, remain);
				}
			}
			
			/**
			 * Arithmetics: C − (A + ... + C + ... + B)  ==  −(A + ... + B)
			 */
			Minus(C, Plus(children)) -> {
				ArrayList<Expression> remain = new ArrayList<Expression>(`children.length);
	        	boolean found = false;
				for (Expression child : `children) {
				    if (!found && `C.equals(child))
						found = true;
					else
						remain.add(child);
				}
				if (found) {
					Expression sum;
					if (remain.size() == 1)
						sum = remain.get(0);
					else
						sum = makeAssociativeExpression(Expression.PLUS, remain);
					return makeUnaryExpression(Expression.UNMINUS, sum);
				}
			}
			
			/**
			 * Arithmetics: (A + ... + E + ... + B) − (C + ... + E + ... + D)  == (A + ... + B) − (C + ... + D)
			 */
			Minus(Plus(right), Plus(left)) -> {
				int rightIndex = -1;
				int leftIndex = -1;
				for (int i = 0; i < `right.length; ++i) {
					for (int j = 0; j < `left.length; ++j) {
						if (`right[i].equals(`left[j])) {
							rightIndex = i;
							leftIndex = j;
							break;
						}
					}
					if (rightIndex != -1)
						break;
				}
				
				if (rightIndex != -1) {
					ArrayList<Expression> rightChildren = new ArrayList<Expression>(`right.length - 1);
					ArrayList<Expression> leftChildren = new ArrayList<Expression>(`left.length - 1);
					for (int i = 0; i < `right.length; ++i) {
						if (i != rightIndex)
							rightChildren.add(`right[i]);
					}
					for (int i = 0; i < `left.length; ++i) {
						if (i != leftIndex)
							leftChildren.add(`left[i]);
					}
					
					Expression rightResult;
					Expression leftResult;
					if (rightChildren.size() == 1)
						rightResult = rightChildren.get(0);
					else
						rightResult = makeAssociativeExpression(Expression.PLUS, rightChildren);
					if (leftChildren.size() == 1)
						leftResult = leftChildren.get(0);
					else
						leftResult = makeAssociativeExpression(Expression.PLUS, leftChildren);
					return makeBinaryExpression(Expression.MINUS, rightResult, leftResult);
				}
			}
	    }
	    return expression;
	}

	@Override
	public Expression rewrite(AssociativeExpression expression) {
	    %match (Expression expression) {
			/**
	         * Arithmetics: (A + ... + D + ... + (C − D) + ... + B) = A + ... + C + ... + B
	         */
			Plus(children) -> {
				// Search for the indices that can be simplified
			    int minusIndex = -1;
			    int plusIndex = -1;
			    Expression simplify = null;
				for (int i = 0; i < `children.length - 1; ++i) {
					for (int j = i + 1; j < `children.length;++j) {
						Expression lExp = `children[i];
						%match (Expression lExp) {
							Minus(C, D) -> {
								if (`D.equals(`children[j])) {
									simplify = `C;
									minusIndex = i;
									plusIndex = j;
									break;
								}
							}
						}
						
						lExp = `children[j];
						%match (Expression lExp) {
							Minus(C,D) -> {
								if (`D.equals(`children[i])) {
									simplify = `C;
									minusIndex = j;
									plusIndex = i;
									break;
								}
							}
						}
					}
					
					if (simplify != null)
						break;
				}

				if (simplify != null) {
					ArrayList<Expression> result = new ArrayList<Expression>(`children.length);
					for (int i = 0; i < `children.length; ++i) {
						if (i == plusIndex) 
							continue;
						
						if (i == minusIndex)
							result.add(simplify);
						else
							result.add(`children[i]);
					}
					
					if (result.size() == 1)
						return result.get(0);
					else
						return makeAssociativeExpression(Expression.PLUS, result);
				}
			}
	    }
	    return expression;
	}
}
