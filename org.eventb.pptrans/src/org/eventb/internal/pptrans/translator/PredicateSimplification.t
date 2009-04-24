/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - mathematical language V2
 *******************************************************************************/

package org.eventb.internal.pptrans.translator;

import static org.eventb.core.ast.Formula.BFALSE;
import static org.eventb.core.ast.Formula.BTRUE;
import static org.eventb.core.ast.Formula.LAND;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
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
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;


/**
 * Simplifies predicates with rules PR1 - PR16
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings({"unused", "cast"})
public class PredicateSimplification extends IdentityTranslator  {
	
	/**
	 *	Simplifies a predicate
	 *	@param pred the predicate to simplify
	 *	@param ff the Formula Factory to be used during simplification
	 *	@return returns a new simplified predicate.
	 */
	public static Predicate simplifyPredicate(Predicate pred, FormulaFactory ff) {
		Predicate newPred = pred;
		pred = null;
		while (newPred != pred) {
			pred = newPred;
			newPred = new PredicateSimplification(ff).translate(pred);
		}
		return newPred;
	}

	private PredicateSimplification(FormulaFactory ff) {
		super(ff);
	}

	private Predicate mBTRUE(SourceLocation loc) {
		return ff.makeLiteralPredicate(BTRUE, loc);
	}

	private Predicate mBFALSE(SourceLocation loc) {
		return ff.makeLiteralPredicate(BFALSE, loc);
	}

	private Predicate simplify(AssociativePredicate pred) {
		final int tag = pred.getTag();
		final Predicate[] children = pred.getChildren();
		final SourceLocation loc = pred.getSourceLocation();
		final Predicate identity = tag == LAND ? mBTRUE(loc) : mBFALSE(loc); 
		final Predicate bound = tag == LAND ? mBFALSE(loc) : mBTRUE(loc);
		final int length = children.length;
		final List<Predicate> newChildren = new ArrayList<Predicate>(length);
		
		boolean hasChanged = false;
		for (final Predicate child : children) {
			final Predicate newChild = translate(child);
			if (newChild.equals(bound)) {
				return bound;
			}
			if (!newChild.equals(identity)) {
				newChildren.add(newChild);
				hasChanged |= newChild != child;
			} else {
				hasChanged = true;
			}
		}

		if (newChildren.size() == 0) {
			return identity;
		}
		if (newChildren.size() == 1) {
			return newChildren.get(0);
		}
		if (hasChanged) {
			return ff.makeAssociativePredicate(tag, newChildren, loc);
		}
		return pred;
	}

	%include {FormulaV2.tom}

	@Override
	protected Predicate translate(Predicate pred) {
		SourceLocation loc = pred.getSourceLocation();
	    %match (Predicate pred) {
	    	/**
	    	 *	RULE PR1:	... ∧ ⊥ ∧ ...
	    	 *				⊥
	    	 *	RULE PR2:   ... ∨ ⊤ ∨ ...
	    	 *				⊤
	    	 *	RULE PR3:	... ∧ c(k−1) ∧ ⊤ ∧ c(k+1) ∧ ...
	    	 *				... ∧ c(k−1) ∧ c(k+1) ∧ ...
	    	 *	RULE PR4:	... ∨ c(k−1) ∨ ⊥ ∨ c(k+1) ∨ ...
	    	 *				... ∨ c(k−1) ∨ c(k+1) ∨ ...
	    	 */
	    	Land(_) -> {
	    		return simplify((AssociativePredicate) pred);
	    	}
	    	Lor(_) -> {
	    		return simplify((AssociativePredicate) pred);
	    	}

	    	/*
	    	 *	RULE PR5:	P ⇒ ⊤
	    	 *				⊤
	    	 */
	    	Limp(_, BTRUE()) -> {
	    		return mBTRUE(loc);
	    	}

	    	/*
	    	 *	RULE PR6:	⊥ ⇒ P	
	    	 *				⊤
	    	 */
	    	Limp(BFALSE(), _) -> {
	    		return mBTRUE(loc);
	    	}

	    	/**
	    	 *	RULE PR7:	⊤ ⇒ P
	    	 *				P
	    	 */
	    	Limp(BTRUE(), P) -> {
	    		return translate(`P);
	    	}
	    	
	    	/**
	    	 *	RULE PR8:	P ⇒ ⊥	
	    	 *				¬P
	    	 */

	    	Limp(P, BFALSE()) -> {
	    		return translate(
	    			ff.makeUnaryPredicate(Formula.NOT, `P, loc));
	    	}

	    	/**
	    	 *	RULE PR9:	¬⊤ 	
	    	 *				⊥
	    	 */
	    	Not(BTRUE()) -> {
	    		return mBFALSE(loc);
	    	}

	    	/**
	    	 *	RULE PR10:	¬⊥ 
	    	 *				⊤
	    	 */
	    	Not(BFALSE()) -> {
	    		return mBTRUE(loc);
	    	}

	    	/**
	    	 *	RULE PR11:	¬¬P 
	    	 *				P
	    	 */
	    	Not(Not(P)) -> {
	    		return translate(`P);
	    	}

	    	/**
	    	 *	RULE PR12:	P ⇔ P 
	    	 *				⊤
	    	 */
	    	Leqv(P, P) -> {
	    		return mBTRUE(loc);
	    	}

	    	/**
	    	 *	RULE PR13: 	P ⇔ ⊤
	    	 *				P
	    	 */
	    	Leqv(P, BTRUE()) -> {
	    		return translate(`P);
	    	}

	    	Leqv(BTRUE(), P) -> {
	    		return translate(`P);
	    	}

	    	/**
	    	 *	RULE PR14: 	P ⇔ ⊥
	    	 *				¬P
	    	 */
	    	Leqv(P, BFALSE()) -> {
	    		return translate(
	    			ff.makeUnaryPredicate(Formula.NOT, `P, loc));
	    	}

	    	Leqv(BFALSE(), P) -> {
	    		return translate(
	    			ff.makeUnaryPredicate(Formula.NOT, `P, loc));
	    	}

	    	/**
	    	 *	RULE PR15: 	∀/∃x1,...,xn·⊤
	    	 *				⊤
	    	 */
	    	ForAll(_, BTRUE()) -> {
	    		return mBTRUE(loc);
	    	}

	    	Exists(_, BTRUE()) -> {
	    		return mBTRUE(loc);
	    	}

	    	/**
	    	 *	RULE PR16: 	∀/∃x1,...,xn·⊥
	    	 *				⊥
	    	 */
	    	ForAll(_, BFALSE()) -> {
	    		return mBFALSE(loc);
	    	}

	    	Exists(_, BFALSE()) -> {
	    		return mBFALSE(loc);
	    	}

	    	_ -> {
	    		return super.translate(pred);
	    	}
	    }
	}
	
	@Override
	protected Expression translate(Expression expr) {
		return expr;
	}

}