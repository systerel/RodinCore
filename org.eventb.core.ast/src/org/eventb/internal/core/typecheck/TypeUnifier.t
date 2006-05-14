/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.typecheck;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.FormulaFactory;

/**
 * This class is used to solve unknown variables in formula's type. 
 *
 * @author François Terrier
 *
 */
@SuppressWarnings("all")		// Hide warnings caused by Tom.
public class TypeUnifier {
	private FormulaFactory factory;
	private TypeCheckResult result;

	public TypeUnifier (TypeCheckResult result) {
		this.factory = result.getFormulaFactory();
		this.result = result;
	}
	
	%include{ Type.tom }
	
	// This declaration is made local to this file, as type variables are not
	// exposed to the published API.
	%op Type TypeVar {
		is_fsym(t) { t instanceof TypeVariable }
	}

	protected final Type unify(Type left, Type right, SourceLocation location) {
		if (left == null || right == null) {
			return null;
		}
		%match (Type left, Type right) {
			tv@TypeVar(), other -> {
				return unifyVariable((TypeVariable) `tv, `other, location);
			}
		  	other, tv@TypeVar() -> {
				return unifyVariable((TypeVariable) `tv, `other, location);
			}
			PowSet(child1), PowSet(child2) -> {
				Type newChild = unify(`child1, `child2, location);
				if (newChild == null) {
					return null;
				}
				if (newChild == `child1) {
					return left;
				}
				if (newChild == `child2) {
					return right;
				}
				return result.makePowerSetType(newChild);
			}
			CProd(left1, right1), CProd(left2, right2) -> {
				Type newLeft = unify(`left1, `left2, location);
				Type newRight = unify(`right1, `right2, location);
				if (newLeft == null || newRight == null) {
					return null;
				}
				if (newLeft == `left1 && newRight == `right1) {
					return left;
				}
				if (newLeft == `left2 && newRight == `right2) {
					return right;
				}
				return result.makeProductType(newLeft, newRight);
			}
			Int(), Int() -> {
				return left;
			}
			Bool(), Bool() -> {
				return left;
			}
			Set(name1), Set(name2) -> {
				if (`name1.equals(`name2)) {
					return left;
				}
				else {
					result.addProblem(new ASTProblem(
							location,
							ProblemKind.TypesDoNotMatch,
							ProblemSeverities.Error,
							left,
							right));
					return null;
				}
			}
			_, _ -> {
				result.addProblem(new ASTProblem(
						location,
						ProblemKind.TypesDoNotMatch,
						ProblemSeverities.Error,
						left,
						right));
				return null;
			}
		}
	}

	private Type unifyVariable(TypeVariable variable, Type otherType,
			SourceLocation location) {
			
		Type type = variable.getValue();
		if (type != null) {
			type = unify(type, otherType, location);
			if (type != null) {
				variable.setValue(type);
			}
			return type;
		} else {
			type = solve(otherType);
			if (type == variable) {
				return variable;
			}
			else if (occurs(variable, type)) {
				result.addProblem(new ASTProblem(
						location,
						ProblemKind.Circularity,
						ProblemSeverities.Error));
				return null;		
			}
			variable.setValue(type);
			return type;
		}
	}

	/**
	 * Returns the type variable's corresponding type.  Never returns <code>null</code>.
	 *
	 * @param intype the type variable to solve
	 * @return the solved type
	 */
	public final Type solve(Type intype) {
		assert intype != null;
		%match (Type intype) {
			TypeVar() -> {
				TypeVariable typeVar = (TypeVariable) intype;
				Type type = typeVar.getValue();		
				if (type != null) {
					type = solve(type);
					typeVar.setValue(type);
					return type;
				}
				else {
					return intype;
				}
			}
			PowSet(child) -> {
				Type newChild = solve(`child);
				if (newChild == `child) {
					return intype;
				}
				return result.makePowerSetType(newChild);
			}
			CProd(left, right) -> {
				Type newLeft = solve(`left);
				Type newRight = solve(`right);
				if (newLeft == `left && newRight == `right) {
					return intype;
				}
				return result.makeProductType(newLeft, newRight);
			}
			_ -> {
				return intype;
			}
		}
	}

	protected final boolean occurs(TypeVariable typeVar, Type expr) {
		%match (Type expr) {
			tv@TypeVar() -> {
				return typeVar == `tv;
			}
			PowSet(child) -> {
				return occurs(typeVar, `child);
			}
			CProd(left, right) -> {
				return occurs(typeVar, `left) || occurs(typeVar, `right);
			}
			_ -> {
				return false;
			}
		}
	}
	
	public final FormulaFactory getFormulaFactory() {
		return factory;
	}
	
}
