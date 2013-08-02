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

import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.Type;
import org.eventb.internal.core.ast.extension.datatype2.DatatypeBuilder;

/**
 * Common protocol for specifying a value constructor of a datatype. Instances
 * of this interface are obtained by calling
 * {@link IDatatypeBuilder#addConstructor(String)} with the name of the
 * constructor to build.
 * <p>
 * Call {@link #addArgument(Type)} or {@link #addArgument(String, Type)} to
 * specify the arguments of the constructor (and the corresponding destructors).
 * For each argument, clients must specify its type. In this context, the
 * datatype type parameters are represented as {@link GivenType}s with the type
 * parameter name and the datatype type is represented as a {@link GivenType}
 * using the datatype name (without any reference to its type parameters).
 * </p>
 * <p>
 * E.g.: for the list datatype, <code>List(S)</code>, reference to
 * <code>S</code> must be done using the given type <code>S</code> and reference
 * to <code>List(S)</code> using the given type <code>List</code>.
 * </p>
 * <p>
 * If a name is also specified for an argument, it denotes the corresponding
 * destructor. Otherwise, the parameter is nameless and no destructor is
 * generated.
 * </p>
 * <p>
 * If the argument type is available in string form, then clients can call
 * {@link DatatypeBuilder#parseType(String)} to obtain a type following the
 * above conventions.
 * </p>
 * <p>
 * Arguments can be added until this constructor is finalized (by finalizing the
 * datatype builder that created it with
 * {@link IDatatypeBuilder#finalizeDatatype()}).
 * </p>
 * 
 * @author Vincent Monfort
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IConstructorBuilder {

	/**
	 * Adds a constructor argument together with the corresponding destructor.
	 * <p>
	 * The type can be obtained by calling
	 * {@link DatatypeBuilder#parseType(String)} with a string representation of
	 * the argument type or must be constructed as follows:
	 * <ul>
	 * <li>A type definition using the datatype type must use a
	 * {@link GivenType} with the datatype name (instead of a parametric type)</li>
	 * <li>A type definition must refer to a type parameter using a
	 * {@link GivenType} with the type parameter name</li>
	 * <li>The datatype type must not occur within a powerset construct</li>
	 * </ul>
	 * </p>
	 * 
	 * @param name
	 *            the name of the corresponding destructor or <code>null</code>
	 *            if there is no corresponding destructor
	 * @param type
	 *            the type of the constructor argument
	 * 
	 * @throws IllegalArgumentException
	 *             if the given type was not created by the same factory has the
	 *             datatype builder of this object
	 * @throws IllegalArgumentException
	 *             if the datatype type occurs within a powerset in the argument
	 *             type
	 * @throws IllegalStateException
	 *             if this builder has been finalized
	 * @see #addArgument(Type)
	 */
	void addArgument(String name, Type type);

	/**
	 * Adds a constructor argument without any corresponding destructor. This is
	 * a short-hand method fully equivalent to calling
	 * <code>addArgument(null, argType)</code>.
	 * 
	 * @param argType
	 *            the type of the argument
	 */
	void addArgument(Type argType);

	/**
	 * Tells whether this constructor is a basic constructor. A basic
	 * constructor is such that none of its argument types reference the
	 * datatype type itself (no recursive use of the datatype).
	 * 
	 * @return <code>true</code> iff this is a basic constructor
	 */
	boolean isBasic();

}
