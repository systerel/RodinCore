/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.contributer.seqprover.fr1942714;

import java.math.BigInteger;
import java.util.ArrayList;

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
 * Generated formula rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class AutoRewriterImpl extends DefaultRewriter {

	public AutoRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {Formula.tom}
	@Override
	public Predicate rewrite(RelationalPredicate predicate)
	{
		BinaryExpression expression1=null;
		BinaryExpression expression2=null;
		BinaryExpression expression;
		%match (Predicate predicate) {
			/**
	         * SetComprehension: A ↦ B ↦ ... ↦N∈{a ↦ b ↦ ... ↦n|P(a,b,...,n)}=P(A,B,...,N)
	         */
			In(left,Cset(ident,pred,expr))->{
				
				// is the part before ∈ of the form A ↦ B ↦ ... ↦N then rewrite it to x ↦ x ↦ ... ↦x
				if (`expr.getTag()==Formula.MAPSTO)
					expression1=(BinaryExpression) redo((BinaryExpression)`expr);
				// is the part before | of the form a ↦ b ↦ ... ↦n then rewrite it to x ↦ x ↦ ... ↦x
				if (`left.getTag()==Formula.MAPSTO)
					expression2=(BinaryExpression) redo((BinaryExpression)`left);
				
				if(expression1.equals(expression2))
				{
					//if they have the same structure we define a quantified predicate and instantiate it 
					// with the extracted Expressions from A ↦ B ↦ ... ↦N
					QuantifiedPredicate pred = ff.makeQuantifiedPredicate(Formula.FORALL, `ident, `pred, null);
					Expression[] expressions=new Expression[1];
					expressions=extract_expressions((BinaryExpression)tom_left).toArray(expressions);
					return pred.instantiate(expressions, ff);
				}
				
				return predicate;
			}
		}
		return predicate;
	}
	
	/**
	 * Rewrites a formula of the form A ↦ B ↦ ... ↦N to x ↦ x ↦ ... ↦x where the x are free identifiers so 
	 * equality can be checked.
	 *
	 * @param expr
	 *            a binary expression of the form A ↦ B ↦ ... ↦N
	 * @return This formula after application of the substitution.
	 */
	private Expression redo (BinaryExpression expr)
	{
		Expression left;
		Expression right;
		
			if(expr.getLeft().getTag()==Formula.MAPSTO) 
				left=redo((BinaryExpression)expr.getLeft());
			else
				left=ff.makeFreeIdentifier("x", null,expr.getLeft().getType() );

			if(expr.getRight().getTag()==Formula.MAPSTO) 
				right=redo((BinaryExpression)expr.getRight());
			else
				right=ff.makeFreeIdentifier("x", null,expr.getRight().getType() );
		
		return ff.makeBinaryExpression(Formula.MAPSTO, left, right, null);
	}

	/**
	 * Extracts the expressions a formula of the form A ↦ B ↦ ... ↦N. In the case of
	 * A ↦ B ↦ C we would get A,B,C.
	 *
	 * @param expr
	 *            a binary expression of the form A ↦ B ↦ ... ↦N
	 * @return A list of the extracted expressions.
	 */
	private ArrayList<Expression> extract_expressions(BinaryExpression expr)
	{
		ArrayList<Expression> expressions= new ArrayList<Expression>();
		
		if(expr.getLeft().getTag()==Formula.MAPSTO) 
			expressions.addAll(extract_expressions((BinaryExpression)expr.getLeft()));
		else
			expressions.add(expr.getLeft());

		if(expr.getRight().getTag()==Formula.MAPSTO) 
			expressions.addAll(extract_expressions((BinaryExpression)expr.getRight()));
		else
			expressions.add (expr.getRight());
		
		return expressions;
		
	}

}