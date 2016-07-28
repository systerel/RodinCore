/*******************************************************************************
 * Copyright (c) 2010, 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

import static org.eventb.core.ast.extension.IOperatorProperties.BINARY;
import static org.eventb.core.ast.extension.IOperatorProperties.MULTARY_2;
import static org.eventb.core.ast.extension.IOperatorProperties.UNARY;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.EXPRESSION;
import static org.eventb.core.ast.extension.IOperatorProperties.FormulaType.PREDICATE;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.INFIX;
import static org.eventb.core.ast.extension.IOperatorProperties.Notation.PREFIX;

import org.eventb.core.ast.extension.IOperatorProperties.FormulaType;
import org.eventb.internal.core.ast.extension.Arity;
import org.eventb.internal.core.ast.extension.ExtensionKind;
import org.eventb.internal.core.ast.extension.TypeDistribs.AllSameType;
import org.eventb.internal.core.ast.extension.TypeDistribs.MixedTypes;

/**
 * @author Nicolas Beauger
 * @since 2.0
 * 
 */
public class ExtensionFactory {

	public static final ITypeDistribution NO_CHILD = makeChildTypes();
	public static final ITypeDistribution ONE_EXPR = makeAllExpr(UNARY);
	public static final ITypeDistribution TWO_EXPRS = makeAllExpr(BINARY);
	public static final ITypeDistribution TWO_OR_MORE_EXPRS = makeAllExpr(MULTARY_2);
	
	private ExtensionFactory() {
		// factory
	}

	/**
	 * Creates a new type distribution in which all types are expressions.
	 * 
	 * @param arity
	 *            arity of expressions
	 * 
	 * @return a new instance of type distribution
	 */
	public static ITypeDistribution makeAllExpr(IArity arity) {
		return new AllSameType(EXPRESSION, arity);
	}

	/**
	 * Creates a new type distribution in which all types are expressions.
	 * 
	 * @param arity
	 *            arity of predicates
	 * 
	 * @return a new instance of type distribution
	 */
	public static ITypeDistribution makeAllPred(IArity arity) {
		return new AllSameType(PREDICATE, arity);
	}

	/**
	 * Creates a new type distribution in which types are mixed, following the
	 * order of the given array.
	 * <p>
	 * The arity for expressions and predicates is fixed and is computed from
	 * the given array.
	 * </p>
	 * 
	 * @param types
	 *            an array of formula types (possibly empty)
	 * @return a new instance of type distribution
	 */
	public static ITypeDistribution makeChildTypes(FormulaType... types) {
		return new MixedTypes(types);
	}

	public static IExtensionKind makePrefixKind(FormulaType formulaType,
			ITypeDistribution childTypes) {
		return new ExtensionKind(PREFIX, formulaType, childTypes, false);
	}

	/**
	 * Make an infix extension kind.
	 * 
	 * @param formulaType
	 *            type of the resulting formula
	 * @param childTypes
	 *            type distribution of child formulae
	 * @param isAssociative
	 *            <code>true</code> for associative kind, <code>false</code>
	 *            otherwise
	 * @return a new extension kind
	 * @since 3.3
	 */
	public static IExtensionKind makeInfixKind(FormulaType formulaType,
			ITypeDistribution childTypes, boolean isAssociative) {
		return new ExtensionKind(INFIX, formulaType, childTypes, isAssociative);
	}

	public static IArity makeArity(int min, int max) {
		return new Arity(min, max);
	}

	public static IArity makeFixedArity(int arity) {
		return new Arity(arity, arity);
	}

}
