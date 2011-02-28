/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - migration to tom-2.8
 *******************************************************************************/

package org.eventb.internal.core.typecheck;

import java.util.ArrayList;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProblemKind;
import org.eventb.core.ast.ProblemSeverities;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;

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
	%op Type TypeVar() {
		is_fsym(t) { t instanceof TypeVariable }
	}

	protected final <T extends Formula<?>> Type unify(Type left, Type right, T origin) {
		if (left == null || right == null) {
			return null;
		}
		%match (Type left, Type right) {
			tv@TypeVar(), other -> {
				return unifyVariable((TypeVariable) `tv, `other, origin);
			}
		  	other, tv@TypeVar() -> {
				return unifyVariable((TypeVariable) `tv, `other, origin);
			}
			PowSet(child1), PowSet(child2) -> {
				Type newChild = unify(`child1, `child2, origin);
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
				Type newLeft = unify(`left1, `left2, origin);
				Type newRight = unify(`right1, `right2, origin);
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
			ParamType(params1), ParamType(params2) -> {
				final ParametricType paramType1 = (ParametricType) `left;
				final ParametricType paramType2 = (ParametricType) `right;
				final IExpressionExtension ext = paramType1.getExprExtension();
				if (ext != paramType2.getExprExtension()) {
					return null;
				}
				final int length = `params1.length;
				assert length == `params2.length;
				final Type[] newParams = new Type[length];
				boolean all1 = true;
				boolean all2 = true;
				for (int i = 0; i < length; i++) {
					final Type param1 = `params1[i];
					final Type param2 = `params2[i];
					final Type newParam = unify(param1, param2, origin);
					if (newParam == null) {
						return null;
					}
					all1 &= newParam == param1;
					all2 &= newParam == param2;
					newParams[i] = newParam;
				}
				if (all1) {
					return left;
				}
				if (all2) {
					return right;
				}
				return result.makeParametricType(newParams, ext);
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
							origin.getSourceLocation(),
							ProblemKind.TypesDoNotMatch,
							ProblemSeverities.Error,
							left,
							right));
					return null;
				}
			}
		}
		result.addUnificationProblem(left, right, origin);
		return null;
	}

	private <T extends Formula<?>> Type unifyVariable(TypeVariable variable, Type otherType, T origin) {
			
		Type type = variable.getValue();
		if (type != null) {
			type = unify(type, otherType, origin);
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
						origin.getSourceLocation(),
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
			ParamType(params) -> {
				final int length = `params.length;
				final Type[] newParams = new Type[length];
				boolean same = true;
				for (int i = 0; i < length; i++) {
					final Type param = `params[i];
					final Type newParam = solve(param);
					same &= newParam == param;
					newParams[i] = newParam;
				}
				if (same) {
					return intype;
				}
				final IExpressionExtension exprExt = ((ParametricType) intype).getExprExtension();
				return result.makeParametricType(newParams, exprExt);
			}
		}
		return intype;
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
			ParamType(params) -> {
				for(Type param: `params) {
					if (occurs(typeVar, param)) {
						return true;
					}
				}
				return false;
			}
		}
		return false;
	}
	
	public final FormulaFactory getFormulaFactory() {
		return factory;
	}
	
}
