/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.util.*;
import java.math.BigInteger;

import org.eventb.core.ast.*;


/**
 * ...
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
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
	
	public static boolean isArithmeticExpression(Expression expr, FormulaFactory ff) {
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
				return expr.getType() == ff.makeIntegerType();
			}
			IntegerLiteral(_) -> {
				return true;
			}
			_ -> {
				return false;
			}
		}
	}
	
	public static boolean isSetExpression(Expression expr, FormulaFactory ff) {
		%match(Expression expr) {
			Identifier() -> {
				/*TODO: correct!*/
				return expr.getType().getBaseType() != null;
			}
			_ -> {
				return false;
			}
		}
	}

	public static boolean isMapletExpression(Expression expr, FormulaFactory ff) {
		%match(Expression expr) {
			Mapsto(l, r) -> {
				return isMapletExpression(`l, ff) && isMapletExpression(`r, ff);
			}
			BoundIdentifier(_) | FreeIdentifier(_) | INTEGER() | BOOL() -> { 
				return true; 
			}
			_ -> {
				return false;
			}
		}
	}

	public static boolean isBooleanExpression(Expression expr, FormulaFactory ff) {
		%match(Expression expr) {
			TRUE() -> {
				return true;
			}
			Identifier() -> {
				return ff.makeBooleanType().equals(expr.getType());
			}
			_ -> {
				return false;
			}
		}
	}
}