/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.ast.datatype;

import static org.eventb.core.ast.extension.ExtensionFactory.makeAllExpr;
import static org.eventb.core.ast.extension.ExtensionFactory.makeFixedArity;
import static org.eventb.core.ast.extension.ExtensionFactory.makePrefixKind;
import static org.eventb.core.ast.extension.IFormulaExtension.ATOMIC_EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.StandardGroup.ATOMIC_EXPR;
import static org.eventb.core.ast.extension.StandardGroup.CLOSED;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExtensionKind;
import org.eventb.internal.core.typecheck.TypeCheckResult;
import org.eventb.internal.core.typecheck.TypeVariable;

/**
 * This class provides services for building and defining a datatype.
 * <p>
 * First it provides the way to have unique ID for datatype extensions, as well
 * as correct group ID and extension kind regarding extensions arguments. Then
 * to instantiate type parameters and datatype type for a datatype using a given
 * type which represents the datatype type.
 * </p>
 * 
 * @author Vincent Monfort
 */
public class DatatypeHelper {

	private static long uniqueId = 0;

	public static String computeId(String name) {
		if (name == null) {
			name = "Id";
		}
		return name + uniqueId++;
	}

	public static String computeGroup(int nbArgs) {
		if (nbArgs > 0) {
			return CLOSED.getId();
		}
		if (nbArgs == 0) {
			return ATOMIC_EXPR.getId();
		}
		throw new IllegalArgumentException("negative number of arguments !");
	}

	public static IExtensionKind computeKind(int nbArgs) {
		final IExtensionKind kind;
		if (nbArgs == 0) {
			kind = ATOMIC_EXPRESSION;
		} else {
			kind = makePrefixKind(EXPRESSION,
					makeAllExpr(makeFixedArity(nbArgs)));
		}
		return kind;
	}

	/**
	 * Returns the map representing the instantiated values for the given types
	 * which were representing the datatype type parameters and its type during
	 * the datatype construction. The datatype type constructor extension and a
	 * given type representing the instantiated datatype type must be provided
	 * in order to calculate this map values.
	 * 
	 * @param typeCons
	 *            the datatype type constructor extension
	 * @param datatypeType
	 *            the given type that was representing the datatype type during
	 *            datatype construction
	 * @param typeParams
	 *            the type parameters of the datatype
	 * @param givenDTType
	 *            the type representing the datatype type instantiated
	 * @return the map representing the instantiated values for the given types
	 *         which were representing the datatype type parameters and its type
	 *         itself
	 */
	public static Map<GivenType, Type> instantiate(
			TypeConstructorExtension typeCons, GivenType datatypeType,
			List<GivenType> typeParams, Type givenDTType) {
		// FIXME: Do not use type check result and use "manual" instantiation
		final TypeCheckResult result = new TypeCheckResult(givenDTType
				.getFactory().makeTypeEnvironment().makeSnapshot());

		TypeVariable[] instantiations = new TypeVariable[typeParams.size()];

		for (int i = 0; i < typeParams.size(); i++) {
			instantiations[i] = result.newFreshVariable(null);
		}

		Type resultType = result.makeParametricType(instantiations, typeCons);
		result.unify(givenDTType, resultType, givenDTType.toExpression());
		result.solveTypeVariables();

		if (result.hasProblem()) {
			return null;
		}

		HashMap<GivenType, Type> instantiated = new HashMap<GivenType, Type>(
				typeParams.size());
		// Add the type parameters instantiations
		for (int i = 0; i < typeParams.size(); i++) {
			instantiated.put(typeParams.get(i), instantiations[i].getValue());
		}
		// Add the datatype type instantiation
		instantiated.put(datatypeType, givenDTType);

		return instantiated;
	}

}
