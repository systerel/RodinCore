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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * Common protocol for datatype constructor extensions.
 * 
 * <p>
 * A constructor extension is the implementation of a datatype constructor, it
 * is characterized by its name and its typed arguments. The constructor
 * arguments which are named are also destructors.
 * </p>
 * 
 * @author Vincent Monfort
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IConstructorExtension extends IExpressionExtension {

	/**
	 * The constructor name.
	 * 
	 * @return the constructor name
	 */
	public String getName();

	/**
	 * Tells whether this value constructor takes any argument.
	 * 
	 * @return <code>true</code> iff this constructor takes some argument
	 */
	boolean hasArguments();

	/**
	 * Returns the arguments of this constructor. Each argument is either anonymous or a destructor.
	 * 
	 * @return an array of the arguments of this constructor
	 */
	IConstructorArgument[] getArguments();

	/**
	 * Returns the destructor extension corresponding to the given name or
	 * <code>null</code> if this name does not correspond to any destructor.
	 * 
	 * @param destName
	 *            the destructor name
	 * @return the destuctor extension corresponding or <code>null</code>
	 * @throws IllegalArgumentException
	 *             if the given name is not a destructor name in this extension
	 */
	public IDestructorExtension getDestructor(String destName);

	/**
	 * Returns the array of argument sets according to the given datatype set.
	 * In other words, returns the sets to which the arguments of this
	 * constructor must belong for the constructed value to belong to the given
	 * set. This is done by instantiating the type parameters of the datatype
	 * with the actual parameters of the type constructor in the given set.
	 * <p>
	 * For instance, suppose that the List datatype is defined by
	 * <code>List(T) ::= nil | cons(hd: T, tl: List(T))</code>, then the call
	 * <code>cons.getArgumentSets(List(1..2), factory)</code> returns the array
	 * <code>{ 1..2, List(1..2) }</code>.
	 * </p>
	 * 
	 * @param set
	 *            a set built with the type constructor of the datatype of this
	 *            constructor
	 * @return the array of argument sets
	 * @throws IllegalArgumentException
	 *             if the given constructor does not belong to this datatype or
	 *             if the given set is not an instance of the type constructor
	 *             of this datatype
	 */
	public Expression[] getArgumentSets(Expression set);

	/**
	 * Returns the types that the arguments of this constructor must have to
	 * build the given instance of the corresponding datatype.
	 * <p>
	 * If the given type is not an instance of the datatype to which this
	 * constructor belongs, then an exception is raised.
	 * </p>
	 * <p>
	 * The types that are returned are built with the same
	 * {@link FormulaFactory} than the given type.
	 * </p>
	 * 
	 * @param instance
	 *            a type instance of the datatype of this constructor
	 * @return the array of argument types
	 * @throws IllegalArgumentException
	 *             if the given type is not compatible with this constructor
	 */
	public Type[] getArgumentTypes(Type instance);

	/**
	 * Returns the datatype to which this constructor extension belongs.
	 * 
	 * @return the datatype of this constructor extension
	 */
	@Override
	public IDatatype2 getOrigin();

}
