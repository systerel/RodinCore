/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

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
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Identifier;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LiteralPredicate;
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
 * ...
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("all")	// Should be ("unused", "uselessTypeCheck")
public abstract class GoalChecker {

%include {Formula.tom}

	public static boolean isInGoal(Predicate pred, FormulaFactory ff) {
		%match(Predicate pred) {
			BinaryPredicate(P1, P2) -> {
				return isInGoal(`P1, ff) && isInGoal(`P2, ff);
			}
			AssociativePredicate(children) -> {
				for(Predicate child: `children) {
					if(!isInGoal(child, ff))
						return false;
				}
				return true;
			}
			UnaryPredicate(P) | QuantifiedPredicate(_, P) -> {
				return isInGoal(`P, ff);
			}
			LiteralPredicate() -> {
				return true;
			}
			NotEqual(AE1, AE2) | Lt(AE1, AE2) | 
			Le(AE1, AE2) | Gt(AE1, AE2) | Ge(AE1, AE2) -> {
				return isArithmeticExpression(`AE1, ff) && isArithmeticExpression(`AE2, ff);
			}
			In(ME1, SE1) -> {
				return isMapletExpression(`ME1, ff) && isSetExpression(`SE1, ff);
			}
			Equal(E1, E2) -> {
				return 
					(isArithmeticExpression(`E1, ff) && isArithmeticExpression(`E2, ff)) ||
					(isSetExpression(`E1, ff) && isSetExpression(`E2, ff)) ||
					(isBooleanExpression(`E1, ff) && isBooleanExpression(`E2, ff)) ||
/*TODO: control*/	(isMapletExpression(`E1, ff) && isMapletExpression(`E2, ff));
			}	
			_ -> {
				throw new AssertionError("Unknown Predicate: " + pred);
			}	
		}
	}
	
	private static boolean isArithmeticExpression(Expression expr, FormulaFactory ff) {
		%match(Expression expr) {
			Plus(children) | Mul(children) -> {
				for(Expression child: `children) {
					if(!isArithmeticExpression(child, ff))
						return false;
				}
				return true;
			}
			Minus(AE1, AE2) | Div(AE1, AE2) | Mod(AE1, AE2) | Expn(AE1, AE2) -> {
				return isArithmeticExpression(`AE1, ff) && isArithmeticExpression(`AE2, ff);
			}
			UnMinus(AE) -> {
				return isArithmeticExpression(`AE, ff);
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
	
	private static boolean isSetExpression(Expression expr, FormulaFactory ff) {
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
	protected static boolean isMapletExpression(Expression expr, FormulaFactory ff) {
		%match(Expression expr) {
			Mapsto(l, r) -> {
				return isMapletExpression(`l, ff) && isMapletExpression(`r, ff);
			}
			Identifier() | INTEGER() | BOOL() -> { 
/* TODO add check on identifier type (must not be a cartesian product. */
				return true; 
			}
			_ -> {
				return false;
			}
		}
	}

	private static boolean isBooleanExpression(Expression expr, FormulaFactory ff) {
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