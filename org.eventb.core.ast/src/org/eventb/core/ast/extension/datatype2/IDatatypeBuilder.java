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

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Type;

/**
 * Common protocol for constructing new datatypes. To create a new datatype, you
 * must first obtain an instance of this interface from a formula factory, then
 * declare all constructors of the datatype and finalize it.
 * <p>
 * New instances can be obtained by calling
 * {@link FormulaFactory#makeDatatypeBuilder(String, java.util.List)}or
 * {@link FormulaFactory#makeDatatypeBuilder(String, GivenType...)} providing it
 * with the datatype name and type parameters.
 * </p>
 * <p>
 * Then datatype constructors can be added with the
 * {@link #addConstructor(String)} method and their arguments could be added
 * directly on the {@link IConstructorBuilder} objects returned.
 * </p>
 * <p>
 * Once all constructors have been added, with at least one basic constructor
 * (which does not reference the datatype itself), the method
 * {@link #finalizeDatatype()} must be called to obtain the final datatype. The
 * restriction on the existence of a basic constructor ensures that the built
 * datatype will not be empty (all types are inhabited in Event-B).
 * </p>
 * <p>
 * A parser that can parse string representations of argument types referencing
 * the type parameters or the datatype itself is provided. It can be used to
 * define easily constructors argument types (see
 * {@link IConstructorBuilder#addArgument(Type)}).
 * </p>
 * 
 * @author Vincent Monfort
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDatatypeBuilder {

	/**
	 * Adds a new constructor to the datatype. The arguments of the constructor
	 * are then defined through the returned object. The constructor name must
	 * be an identifier name different from all names already declared in the
	 * datatype (i.e., the datatype name, the name of already added constructors
	 * and destructors). However, the given name can be the same as one of the
	 * formal type parameters of the datatype.
	 * 
	 * @param name
	 *            the constructor name
	 * @return an {@link IConstructorBuilder} for which arguments could be
	 *         defined
	 * @throws IllegalArgumentException
	 *             if the given name is not an identifier name or is not fresh
	 * @throws IllegalStateException
	 *             if this {@link IDatatypeBuilder} has already been finalized
	 */
	IConstructorBuilder addConstructor(String name);

	/**
	 * Returns the base formula factory of this builder, that is the formula
	 * factory that created this instance.
	 * 
	 * @return the base formula factory
	 */
	FormulaFactory getFactory();

	/**
	 * Provides a parser that can parse a string representation of a type
	 * referencing type parameters or the datatype type itself. It returns the
	 * correct intermediate representation necessary to add constructor
	 * arguments with {@link IConstructorBuilder#addArgument(Type)} or
	 * {@link IConstructorBuilder#addArgument(Type, String)}.
	 * <p>
	 * A datatype type must only be written as DT(X,Y,Z) with DT the datatype
	 * name and (X,Y,Z) the type parameters in the exact same order they were
	 * declared in the datatype (X first type parameter, etc.).
	 * </p>
	 * <p>
	 * If the parse result was correctly parsed (i.e. not
	 * {@link IParseResult#hasProblem()}) then a call to
	 * {@link IParseResult#getParsedType()} will provide a type in the correct
	 * intermediate representation of type that must be used for constructor
	 * arguments.
	 * </p>
	 * 
	 * @param strType
	 *            the string representation of a constructor argument type
	 * @return the parse result in the correct intermediate representation for
	 *         constructor argument type
	 * @see IConstructorBuilder#addArgument(Type)
	 * @see IConstructorBuilder#addArgument(Type, String)
	 */
	IParseResult parseType(String strType);

	/**
	 * Tells if the datatype has a basic constructor. A basic constructor is a
	 * constructor that does not reference the datatype itself.
	 * 
	 * @return <code>true</code> iff a basic constructor has already been
	 *         defined
	 */
	boolean hasBasicConstructor();

	/**
	 * Finalizes this builder and returns the assembled datatype.
	 * <p>
	 * At least one basic constructor must have been added to this builder.
	 * </p>
	 * <p>
	 * Upon return from this method, this datatype builder and all constructor
	 * builders that it created are finalized and cannot be used anymore.
	 * </p>
	 * <p>
	 * Any subsequent call will return the same datatype object as the first
	 * successful call.
	 * </p>
	 * 
	 * @since 3.0
	 * @see #hasBasicConstructor()
	 * @throws IllegalStateException
	 *             if the datatype does not have a basic constructor
	 */
	IDatatype2 finalizeDatatype();

}
