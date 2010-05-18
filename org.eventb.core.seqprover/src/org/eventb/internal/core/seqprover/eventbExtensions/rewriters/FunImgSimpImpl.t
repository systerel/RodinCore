/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.eventbExtensions.rewriters;

import java.math.BigInteger;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.DefaultFilter;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
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
import org.eventb.core.seqprover.IProverSequent;

@SuppressWarnings("unused")
public class FunImgSimpImpl {
	
	private static class FunImgSimpFilter extends DefaultFilter {
		private final FunImgSimpImpl parent;

		public FunImgSimpFilter(FunImgSimpImpl parent) {
			this.parent = parent;
		}

		@Override
		public boolean select(BinaryExpression expression) {

		final Expression f = parent.getFunImgFunction(expression);
			if (f == null){
				return false;
			}
			return parent.isApplicable(f);
		}
	}
		
	private final IProverSequent sequent;
	private final Predicate hyp;
	
	public FunImgSimpImpl(Predicate hyp, IProverSequent sequent) {
		this.hyp = hyp;
		this.sequent = sequent;
	}
	
	public Predicate getNeededHyp(Expression expression){
		return searchFunction(expression);
	}
		
	public Predicate searchFunction(Expression f) {
		for (Predicate hypo : sequent.visibleHypIterable()) {
			final Predicate predicate = searchPFuncKind(f,hypo);
			if (predicate != null){
				return predicate;
			}
		}
		return null;
	}
	
	public boolean isApplicable(Expression f){
		return searchFunction(f) != null;
	}
	
	public List<IPosition> getApplicablePositions() {
		final FunImgSimpFilter filter = new FunImgSimpFilter(this);
		if (hyp == null){
			return sequent.goal().getPositions(filter);
		}
		return hyp.getPositions(filter);
	}

	%include {FormulaV2.tom}
	
	Expression getFunImgFunction(Expression expr){
	
		%match (Expression expr) {
			
			FunImage(DomSub((SetExtension|Dom)(_),fun),_)->
			{
				return `fun;
			}
		}
		return null;
	}
				 
	// search a function if it is at least a partial function 
	private Predicate searchPFuncKind(Expression f, Predicate predicate)
	{
			%match (Predicate predicate) {
			/**
	         * Partial function
	         */
			In(exp,Pfun(_,_))->
			{
						if (`exp.equals(f))
						return predicate;
			}
			
			/**
	         * Total function
	         */
			In(exp,Tfun(_,_))->
			{
						if (`exp.equals(f))
						return predicate;
			}
			
			/**
	         * Partial injection
	         */
			In(exp,Pinj(_,_))->
			{
						if (`exp.equals(f))
						return predicate;
			}
			
			/**
	         * Total injection
	         */
			In(exp,Tinj(_,_))->
			{
						if (`exp.equals(f))
						return predicate;
			}
			
			/**
	         * Partial surjection
	         */
			In(exp,Psur(_,_))->
			{
						if (`exp.equals(f))
						return predicate;
			}
			
			/**
	         * Total surjection
	         */
			In(exp,Tsur(_,_))->
			{
						if (`exp.equals(f))
						return predicate;
			}

			/**
	         * Bijection
	         */
			In(exp,Tbij(_,_))->
			{
						if (`exp.equals(f))
						return predicate;
			}
		    
			}
		return null;		
	} 
	
}