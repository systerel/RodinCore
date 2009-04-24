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

import java.math.BigInteger;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;


/**
 * The GoalChecker checks whether a given predicate is in a reduced form.
 * Meaning, that set membership is the only set theoretic construct and
 * arithmetic expressions are separated.
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("all")	// Should be ("unused", "uselessTypeCheck")
public abstract class GoalChecker {

%include {FormulaV2.tom}

	/**
	 * Checks whether a given predicate is in a reduced form
	 * @param pred the predicate to be checked
	 * @return returns true if the predicate is in a reduced form
	 */
	public static boolean isInGoal(Predicate pred) {
		%match(Predicate pred) {
			BinaryPredicate(P1, P2) -> {
				return isInGoal(`P1) && isInGoal(`P2);
			}
			AssociativePredicate(children) -> {
				for (Predicate child: `children) {
					if (!isInGoal(child))
						return false;
				}
				return true;
			}
			QuantifiedPredicate(bids, P) -> {
				return areBoundDeclsUsed(`bids, `P) && isInGoal(`P);
			}
			UnaryPredicate(P) -> {
				return isInGoal(`P);
			}
			LiteralPredicate() -> {
				return true;
			}
			(NotEqual|Lt|Le|Gt|Ge)(AE1, AE2) -> {
				return isArithmeticExpression(`AE1) && isArithmeticExpression(`AE2);
			}
			In(ME1, SE1) -> {
				return isMapletExpression(`ME1) && isSetExpression(`SE1);
			}
			Equal(id@Identifier(), Identifier()) -> {
				return ! (`id.getType() instanceof ProductType);
			}
			Equal(E1, E2) -> {
      			final Type type = `E1.getType();
      			if (type instanceof IntegerType) {
      				return isArithmeticExpression(`E1) && isArithmeticExpression(`E2);
      			}
      			if (type instanceof BooleanType) {
      				return isBooleanExpression(`E1) && isBooleanExpression(`E2);
      			}
      			if (type instanceof PowerSetType) {
      				return isSetExpression(`E1) && isSetExpression(`E2);
      			}
      			return false;
			}	
			_ -> {
				return false;
			}	
		}
	}
	
    /**
	 * Tells whether all bound identifier declarations are actually used in the
	 * given predicate.
	 * 
	 * @param bids
	 *            array of declarations to check for use
	 * @param P
	 *            predicate where bound identifiers should appear
	 * @return <code>true</code> iff all bound identifiers do occur in the
	 *         given predicate
	 */
    private static boolean areBoundDeclsUsed(BoundIdentDecl[] bids, Predicate P) {
    	final int length = bids.length;
    	final BoundIdentifier[] bis = P.getBoundIdentifiers();
    	if (bis.length < length) {
    		// Not enough bound identifiers in given predicate
    		return false;
    	}
    	for (int i = 0; i < length; ++i) {
    		if (bis[i].getBoundIndex() != i) {
    			// Missing index: this bound identifier is not used.
    			return false;
    		}
    	}
    	return true;
    }

	private static boolean isArithmeticExpression(Expression expr) {
		%match(Expression expr) {
			(Plus|Mul)(children) -> {
				for (Expression child: `children) {
					if (! isArithmeticExpression(child))
						return false;
				}
				return true;
			}
			(Minus|Div|Mod|Expn)(AE1, AE2) -> {
				return isArithmeticExpression(`AE1) && isArithmeticExpression(`AE2);
			}
			UnMinus(AE) -> {
				return isArithmeticExpression(`AE);
			}
			Identifier() -> {
				return expr.getType() instanceof IntegerType;
			}
			IntegerLiteral(_) -> {
				return true;
			}
			_ -> {
				return false;
			}
		}
	}
	
	private static boolean isSetExpression(Expression expr) {
		%match(Expression expr) {
			Identifier() -> {
				return expr.getType() instanceof PowerSetType;
			}
			_ -> {
				return false;
			}
		}
	}

/* TODO turn back to private ? */
	protected static boolean isMapletExpression(Expression expr) {
		%match(Expression expr) {
			Mapsto(l, r) -> {
				return isMapletExpression(`l) && isMapletExpression(`r);
			}
			Identifier() -> { 
				return ! (expr.getType() instanceof ProductType); 
			}
			(INTEGER|BOOL)() -> { 
				return true; 
			}
			_ -> {
				return false;
			}
		}
	}

	private static boolean isBooleanExpression(Expression expr) {
		%match(Expression expr) {
			TRUE() -> {
				return true;
			}
			Identifier() -> {
				return expr.getType() instanceof BooleanType;
			}
			_ -> {
				return false;
			}
		}
	}
}