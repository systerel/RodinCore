/*******************************************************************************
 * Copyright (c) 2008 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.contributer.seqprover.fr1935674;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

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
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Generated formula rewriter for the Event-B sequent prover.
 */
@SuppressWarnings("unused")
public class AutoRewriterImpl extends DefaultRewriter {

	protected AssociativePredicate makeAssociativePredicate(int tag, Collection<Predicate> children) {
		return ff.makeAssociativePredicate(tag, children, null);
	}

	protected UnaryPredicate makeUnaryPredicate(int tag, Predicate child) {
		return ff.makeUnaryPredicate(tag, child, null);
	}

	protected QuantifiedPredicate makeQuantifiedPredicate(int tag,BoundIdentDecl[] boundIdentifiers, Predicate child) {
		return ff.makeQuantifiedPredicate(tag,boundIdentifiers, child, null);
	}

	public AutoRewriterImpl() {
		super(true, FormulaFactory.getDefault());
	}
		
	%include {Formula.tom}
	@Override
	public Predicate rewrite(UnaryPredicate predicate)
	{
			%match (Predicate predicate) {
			
			/**
	         * DeMorgan: ¬(A ∧ B ∧ ... ∧ N)=¬A ∨ ¬B ∨ ... ∨ ¬N
	         */
			Not(Land(children))->{
				ArrayList<Predicate> new_children = new ArrayList<Predicate>(`children.length);
				for (Predicate child : `children)
				{
					new_children.add(makeUnaryPredicate(Predicate.NOT,child));
				}
				return makeAssociativePredicate(Predicate.LOR,new_children);
			}
			
			/**
	         * DeMorgan: ¬(A ∨ B ∨ ... ∨ N)=¬A ∧ ¬B ∧ ... ∧ ¬N
	         */
			Not(Lor(children))->{
				ArrayList<Predicate> new_children = new ArrayList<Predicate>(`children.length);
				for (Predicate child : `children)
				{
					new_children.add(makeUnaryPredicate(Predicate.NOT,child));
				}
				return makeAssociativePredicate(Predicate.LAND,new_children);
			}
			
			/**
	         * DeMorgan: ¬∀x,...·P(x,...)=∃x,...·¬P(x,...)
	         */
			Not(ForAll(ident,child))->{
				return makeQuantifiedPredicate(Formula.EXISTS,`ident,makeUnaryPredicate(Predicate.NOT,`child));
			}
			
			/**
	         * DeMorgan: ¬∃x,...·P(x,...)=∀x,...·¬P(x,...)
	         */
			Not(Exists(ident,child))->{
				return makeQuantifiedPredicate(Formula.FORALL,`ident,makeUnaryPredicate(Predicate.NOT,`child));
			}
			}
			return predicate;
	}
}