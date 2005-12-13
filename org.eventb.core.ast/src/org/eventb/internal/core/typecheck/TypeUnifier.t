/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.core.typecheck;

import java.util.List;

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
	private List<TypeVariable> typeVariables;
	private FormulaFactory factory;
	private TypeCheckResult result;
	
	public TypeUnifier (List<TypeVariable> typeVariables, FormulaFactory factory, TypeCheckResult result) {
		this.typeVariables = typeVariables;
		this.factory = factory;
		this.result = result;
	}
	
	%include{ Type.tom }
	
	public Type unify(Type left, Type right, SourceLocation location) {
		if (left == null || right == null) {
			return null;
		}
		%match (Type left, Type right) {
			tv@TypeVar(), other
		  | other,             tv@TypeVar() -> {
		  		TypeVariable typeVar = (TypeVariable) `tv;
				Type type = typeVar.getValue();
				if (type != null) {
					type = unify(type, other, location);
					if (type != null) {
						typeVar.setValue(type);
					}
					return type;
				}
				else {
					type = solve(other);
					if (type == tv) {
						return tv;
					}
					else if (occurs(typeVar, type)) {
						result.addProblem(new ASTProblem(
								location,
								ProblemKind.Circularity,
								ProblemSeverities.Error));
						return null;		
					}
					typeVar.setValue(type);
					return type;
				}				
			}
			PowSet (child1), PowSet (child2) -> {
				Type newChild = unify(`child1, `child2, location);
				if (newChild == null) {
					return null;
				}
				if (newChild == child1) {
					return left;
				}
				if (newChild == child2) {
					return right;
				}
				return factory.makePowerSetType(newChild);
			}
			CProd (left1, right1), CProd (left2, right2) -> {
				Type newLeft = unify(`left1, `left2, location);
				Type newRight = unify(`right1, `right2, location);
				if (newLeft == null || newRight == null) {
					return null;
				}
				if (newLeft == left1 && newRight == right1) {
					return left;
				}
				if (newLeft == left2 && newRight == right2) {
					return right;
				}
				return factory.makeProductType(newLeft, newRight);
			}
			Int, Int -> {
				return left;
			}
			Bool, Bool -> {
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

	/**
	 * Returns the type variable's corresponding type.  Never returns <code>null</code>.
	 *
	 * @param inexpr the type variable to solve
	 * @return the solved type
	 */
	public Type solve(Type intype) {
		assert intype != null;
		%match (Type intype) {
			TypeVar -> {
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
			PowSet (child) -> {
				Type newChild = solve(child);
				if (newChild == child) {
					return intype;
				}
				return factory.makePowerSetType(newChild);
			}
			CProd (left, right) -> {
				Type newLeft = solve(left);
				Type newRight = solve(right);
				if (newLeft == left && newRight == right) {
					return intype;
				}
				return factory.makeProductType(newLeft, newRight);
			}
			_ -> {
				return intype;
			}
		}
	}

	public boolean occurs(TypeVariable typeVar, Type expr) {
		%match (Type expr) {
			tv@TypeVar() -> {
				return typeVar == `tv;
			}
			PowSet (child) -> {
				return occurs(typeVar, child);
			}
			CProd (left, right) -> {
				return occurs(typeVar, left) || occurs(typeVar, right);
			}
			_ -> {
				return false;
			}
		}
	}
}
