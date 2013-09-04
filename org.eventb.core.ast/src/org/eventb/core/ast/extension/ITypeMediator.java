/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

import java.util.List;

import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.Type;

/**
 * Common protocol for synthesizing the type of or type-checking formula
 * extensions.
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITypeMediator {

	/**
	 * Returns the factory to use for type-checking.
	 * 
	 * @return the factory to use for type-checking
	 */
	FormulaFactory getFactory();

	/**
	 * This is a short-hand method fully equivalent to
	 * 
	 * <pre>
	 * getFactory().makeBooleanType()
	 * </pre>
	 */
	BooleanType makeBooleanType();

	/**
	 * This is a short-hand method fully equivalent to
	 * 
	 * <pre>
	 * getFactory().makeParametricType(typeConstr, typePrms)
	 * </pre>
	 * 
	 * @since 3.0
	 */
	ParametricType makeParametricType(IExpressionExtension typeConstr,
			List<Type> typePrms);

	/**
	 * This is a short-hand method fully equivalent to
	 * 
	 * <pre>
	 * getFactory().makeGivenType(name)
	 * </pre>
	 */
	GivenType makeGivenType(String name);

	/**
	 * This is a short-hand method fully equivalent to
	 * 
	 * <pre>
	 * getFactory().makeIntegerType()
	 * </pre>
	 */
	IntegerType makeIntegerType();

	/**
	 * This is a short-hand method fully equivalent to
	 * 
	 * <pre>
	 * getFactory().makePowerSetType(base)
	 * </pre>
	 */
	PowerSetType makePowerSetType(Type base);

	/**
	 * This is a short-hand method fully equivalent to
	 * 
	 * <pre>
	 * getFactory().makeProductType(left, right)
	 * </pre>
	 */
	ProductType makeProductType(Type left, Type right);

	/**
	 * This is a short-hand method fully equivalent to
	 * 
	 * <pre>
	 * getFactory().makeRelationalType(left, right)
	 * </pre>
	 */
	PowerSetType makeRelationalType(Type left, Type right);

}