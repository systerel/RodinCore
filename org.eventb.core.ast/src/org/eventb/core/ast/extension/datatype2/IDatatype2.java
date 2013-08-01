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
package org.eventb.core.ast.extension.datatype2;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * Common protocol for datatypes.
 * <p>
 * Instances of this interface are built using the {@link IDatatypeBuilder}
 * protocol.
 * </p>
 * <p>
 * The datatype name can be retrieved in its {@link ITypeConstructorExtension}
 * using {@link ITypeConstructorExtension#getSyntaxSymbol()} as well as the
 * names of formal type parameters using
 * {@link ITypeConstructorExtension#getFormalNames()}.
 * </p>
 * <p>
 * Extensions returned by {@link #getExtensions()} are intended to be passed to
 * {@link FormulaFactory#getInstance(Set)} (possibly within a larger set of
 * extensions) in order to get a factory able to parse and build formulas
 * containing references to this datatype.
 * </p>
 * <p>
 * Extensions related to a datatype (type constructor, constructors,
 * destructors) all have an instance of their {@link IDatatype2} as origin (
 * {@link IFormulaExtension#getOrigin()}).
 * </p>
 * 
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDatatype2 {

	/**
	 * Returns the type constructor of this datatype.
	 * <p>
	 * Returned extension can be used as parameter in
	 * {@link FormulaFactory#makeParametricType(List, IExpressionExtension)} as
	 * well as makeExtendedExpression(IExpressionExtension, ...) methods.
	 * </p>
	 * 
	 * @return the type constructor
	 */
	ITypeConstructorExtension getTypeConstructor();

	/**
	 * Returns the value constructor with the given name, or <code>null</code>
	 * if not found.
	 * 
	 * @param name
	 *            the name of a value constructor
	 * @return the value constructor with the given name or <code>null</code>
	 */
	IConstructorExtension getConstructor(String name);

	/**
	 * Returns all value constructors of this datatype.
	 * 
	 * @return an array of all value constructors
	 */
	IConstructorExtension[] getConstructors();

	/**
	 * Returns the destructor with the given name, or <code>null</code> if not
	 * found.
	 * 
	 * @param name
	 *            the name of a destructor
	 * @return the destructor with the given name or <code>null</code>
	 */
	IDestructorExtension getDestructor(String name);

	/**
	 * Returns a minimal formula factory that contains all extensions that are
	 * needed to define this datatype. For instance, if the type of an argument
	 * of a value constructor uses another datatype, the base factory will
	 * contain this datatype.
	 * 
	 * @return a formula factory supporting the definition of this datatype
	 */
	FormulaFactory getBaseFactory();

	/**
	 * Returns a minimal formula factory that contains this datatype. This is a
	 * shorthand fully equivalent to
	 * 
	 * <pre>
	 * getBaseFactory().withExtensions(getExtensions());
	 * </pre>
	 * 
	 * @return a formula factory supporting this datatype
	 */
	FormulaFactory getFactory();

	/**
	 * Returns a set of all the formula extensions defined by this datatype.
	 * This set is not modifiable by clients.
	 * <p>
	 * Defined extensions are:
	 * <li>the type constructor</li>
	 * <li>all value constructors</li>
	 * <li>all destructors</li>
	 * </p>
	 * 
	 * @return an unmodifiable set of all formula extensions
	 */
	Set<IFormulaExtension> getExtensions();

	/**
	 * Returns an object that can be used to compute the actual types of
	 * arguments of constructors of this datatype.
	 * 
	 * @param type
	 *            a type built with the type constructor of this datatype
	 * @return a type instantiation usable for computing argument types
	 * @throws IllegalArgumentException
	 *             if the given type is not a parametric type built with the
	 *             type constructor of this datatype
	 */
	ITypeInstantiation getTypeInstantiation(Type type);

}
