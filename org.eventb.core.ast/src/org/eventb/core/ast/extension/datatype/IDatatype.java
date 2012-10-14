/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension.datatype;

import java.util.List;
import java.util.Set;

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * Common protocol for datatypes.
 * <p>
 * Instances of this interface are intended to be obtained by calling
 * {@link FormulaFactory#makeDatatype(IDatatypeExtension)}.
 * </p>
 * <p>
 * Extensions returned by {@link #getExtensions()} are intended to be passed to
 * {@link FormulaFactory#getInstance(Set)} (possibly within a larger set of
 * extensions) in order to get a factory able to parse and build formulae
 * containing references to this datatype.
 * </p>
 * <p>
 * Extensions related to a datatype (type constructor, constructors,
 * destructors) all have an instance of their IDatatype as origin (
 * {@link IFormulaExtension#getOrigin()}).
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IDatatype {

	/**
	 * Returns the type parameters of this datatype.
	 * 
	 * @return a list of type parameters (possibly empty)
	 */
	List<ITypeParameter> getTypeParameters();

	/**
	 * Returns the type constructor extension for this datatype.
	 * <p>
	 * Returned extension can be used as parameter in
	 * {@link FormulaFactory#makeParametricType(List, IExpressionExtension)} as
	 * well as makeExtendedExpression(IExpressionExtension, ...) methods.
	 * </p>
	 * 
	 * @return an expression extension
	 */
	IExpressionExtension getTypeConstructor();

	/**
	 * Returns the constructor extension associated to the given id, or
	 * <code>null</code> if not found.
	 * 
	 * <p>
	 * Given id refers to the same id parameter given through
	 * {@link IConstructorMediator#addConstructor(String, String, List)}.
	 * </p>
	 * 
	 * @param constructorId
	 *            a String identifier
	 * @return an expression extension or <code>null</code>
	 */
	IExpressionExtension getConstructor(String constructorId);

	/**
	 * Returns all constructors of this datatype.
	 * 
	 * @return a set of expression extensions (possibly empty)
	 */
	Set<IExpressionExtension> getConstructors();
	
	/**
	 * Returns whether the given extension is a constructor of this datatype.
	 * 
	 * @param extension
	 *            an extension
	 * @return <code>true</code> iff the given extension is a constructor of
	 *         this datatype
	 */
	boolean isConstructor(IExpressionExtension extension);

	/**
	 * Returns the destructor extension that corresponds to the given
	 * constructor id and the given argument number, or <code>null</code> if not
	 * found.
	 * <p>
	 * The argument number is the zero-based index of the constructor argument
	 * that corresponds to the destructor target. For instance, in constructor
	 * definition <b>cons(head, tail)</b>, <b>head</b> has argument number 0 and
	 * <b>tail</b> has argument number 1.
	 * </p>
	 * <p>
	 * <code>null</code> is returned when:
	 * <li>the given id is not associated to a constructor</li>
	 * <li>the given argument number is out of the bounds of the constructor
	 * arguments</li>
	 * <li>the argument corresponding to the given argument number has no
	 * associated destructor</li>
	 * </p>
	 * 
	 * @param constructorId
	 *            a String id
	 * @param argNumber
	 *            a natural number
	 * @return an expression extension, or <code>null</code>
	 */
	IExpressionExtension getDestructor(String constructorId, int argNumber);

	/**
	 * Returns a list of arguments for the given constructor, or
	 * <code>null</code> if the given extension is not a constructor of this
	 * datatype.
	 * <p>
	 * When not <code>null</code>, the returned list has the size of the number
	 * of arguments of the constructor, and is ordered after the order of these
	 * arguments.
	 * </p>
	 * 
	 * @param constructor
	 *            a constructor extension
	 * @return a list of arguments, or <code>null</code>
	 */
	List<IArgument> getArguments(IExpressionExtension constructor);

	/**
	 * Returns the list of argument types for the given constructor, according
	 * to the given return type.
	 * <p>
	 * <code>null</code> is returned if the given extension is not a constructor
	 * of this datatype, or if the given return type does not have the type
	 * constructor of this datatype for expression extension.
	 * </p>
	 * <p>
	 * When not <code>null</code>, the returned list has the size of the number
	 * of arguments of the constructor, and is ordered after the order of these
	 * arguments.
	 * </p>
	 * <p>
	 * Given return type is used to instantiate type parameters, which may occur
	 * in argument types.
	 * </p>
	 * 
	 * @param constructor
	 *            a constructor extension
	 * @param returnType
	 *            return type of the constructor
	 * @param factory
	 *            a formula factory to use for building types
	 * @return a list of types, or <code>null</code>
	 */
	List<Type> getArgumentTypes(IExpressionExtension constructor,
			ParametricType returnType, FormulaFactory factory);

	/**
	 * Returns the list of argument sets for the given constructor, according to
	 * the given datatype set. In other words, returns the sets to which the
	 * arguments of the given constructor must belong for the constructed value
	 * to belong to the given set. This is done by instantiating the type
	 * parameters of this datatype with the actual parameters of the type
	 * constructor in the given set.
	 * <p>
	 * For instance, suppose that the List datatype is defined by
	 * <code>List(T) ::= nil | cons(hd: T, tl: List(T))</code>, then the call
	 * <code>getArgumentSets(cons, List(1..2), factory)</code> returns the list
	 * <code>{ 1..2, List(1..2) }</code>.
	 * </p>
	 * 
	 * @param constructor
	 *            a constructor of this datatype
	 * @param set
	 *            a set built with the type constructor of this datatype
	 * @param factory
	 *            formula factory to use for building the result (must include
	 *            all extensions of this datatype)
	 * @return a list of sets
	 * @throws IllegalArgumentException
	 *             if the given constructor does not belong to this datatype or
	 *             if the given set is not an instance of the type constructor
	 *             of this datatype
	 * @since 2.7
	 */
	List<Expression> getArgumentSets(IExpressionExtension constructor,
			ExtendedExpression set, FormulaFactory factory);

	/**
	 * Returns the argument number of the given destructor for the given
	 * constructor.
	 * <p>
	 * A negative integer is returned if:
	 * <li>the given constructor is not defined in this datatype</li>
	 * <li>the given destructor is not defined in this datatype</li>
	 * <li>the given destructor does not correspond to the given constructor</li>
	 * </p>
	 * 
	 * @param constructor
	 *            a constructor
	 * @param destructor
	 *            a destructor
	 * @return a non negative integer in case of success, a negative integer in
	 *         case of failure
	 */
	int getDestructorIndex(IExpressionExtension constructor,
			IExpressionExtension destructor);

	/**
	 * Returns a set of all formula extensions defined by this datatype.
	 * <p>
	 * Defined extensions are:
	 * <li>the type constructor</li>
	 * <li>all constructors</li>
	 * <li>all destructors</li>
	 * </p>
	 * 
	 * @return a set of formula extensions
	 */
	Set<IFormulaExtension> getExtensions();

}
