/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.math.BigInteger;
import java.util.LinkedList;

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
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

/**
 * Simplifies predicates with rules PR1 - PR16
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
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

	%include {Formula.tom}

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
	    	Land(children) | Lor(children) -> {
	    		LinkedList<Predicate> newChilds = new LinkedList<Predicate>();
	    		boolean hasChanged = false;
	    		for (Predicate child: `children) {
	    			Predicate newChild = translate(child);
	    			newChilds.add(newChild);
	    			hasChanged |= newChild != child;
	    		}
				boolean isAnd = pred.getTag() == Formula.LAND;
				Predicate btrue = ff.makeLiteralPredicate(Formula.BTRUE, loc);
				Predicate bfalse = ff.makeLiteralPredicate(Formula.BFALSE, loc);	
							
    			return FormulaConstructor.makeSimplifiedAssociativePredicate(
    				ff, 
    				pred.getTag(), 
    				newChilds, 
    				isAnd ? btrue : bfalse,
    				isAnd ? bfalse : btrue,
    				loc, 
    				hasChanged ? null : pred);
	    	}

	    	/**
	    	 *	RULE PR5:	P ⇒ ⊤
	    	 *				⊤
	    	 *	RULE PR6:	⊥ ⇒ P	
	    	 *				⊤
	    	 */
	    	Limp(_, BTRUE()) | Limp(BFALSE(), _) -> {
	    		return ff.makeLiteralPredicate(Formula.BTRUE, loc);
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
	    		return ff.makeLiteralPredicate(Formula.BFALSE, loc);
	    	}

	    	/**
	    	 *	RULE PR10:	¬⊥ 
	    	 *				⊤
	    	 */
	    	Not(BFALSE()) -> {
	    		return ff.makeLiteralPredicate(Formula.BTRUE, loc);
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
	    		return ff.makeLiteralPredicate(Formula.BTRUE, loc);
	    	}

	    	/**
	    	 *	RULE PR13: 	P ⇔ ⊤
	    	 *				P
	    	 */
	    	Leqv(P, BTRUE()) | Leqv(BTRUE(), P) -> {
	    		return translate(`P);
	    	}

	    	/**
	    	 *	RULE PR14: 	P ⇔ ⊥
	    	 *				¬P
	    	 */
	    	Leqv(P, BFALSE()) | Leqv(BFALSE(), P) -> {
	    		return translate(
	    			ff.makeUnaryPredicate(Formula.NOT, `P, loc));
	    	}

	    	/**
	    	 *	RULE PR15: 	∀/∃x1,...,xn·⊤
	    	 *				⊤
	    	 */
	    	ForAll(_, BTRUE()) | Exists(_, BTRUE()) -> {
	    		return ff.makeLiteralPredicate(Formula.BTRUE, loc);
	    	}

	    	/**
	    	 *	RULE PR16: 	∀/∃x1,...,xn·⊥
	    	 *				⊥
	    	 */
	    	ForAll(_, BFALSE()) | Exists(_, BFALSE()) -> {
	    		return ff.makeLiteralPredicate(Formula.BFALSE, loc);
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