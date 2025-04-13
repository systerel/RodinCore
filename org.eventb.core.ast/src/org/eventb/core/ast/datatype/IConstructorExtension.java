/*******************************************************************************
 * Copyright (c) 2013, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.datatype;

import org.eventb.core.ast.extension.IExpressionExtension2;

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
public interface IConstructorExtension extends IExpressionExtension2 {

	/**
	 * The constructor name.
	 * 
	 * @return the constructor name
	 */
	String getName();

	/**
	 * Tells whether this value constructor takes any argument.
	 * 
	 * @return <code>true</code> iff this constructor takes some argument
	 */
	boolean hasArguments();

	/**
	 * Returns the arguments of this constructor. Each argument is either
	 * anonymous or a destructor.
	 * 
	 * @return an array of the arguments of this constructor
	 */
	IConstructorArgument[] getArguments();

	/**
	 * Returns the destructor extension corresponding to the given name or
	 * <code>null</code> if this name does not correspond to any destructor.
	 * 
	 * @param destName
	 *            a destructor name
	 * @return the corresponding destructor extension or <code>null</code>
	 */
	IDestructorExtension getDestructor(String destName);

	/**
	 * Returns the index of the given argument, counted from 0, if it exists.
	 * Returns <code>-1</code> if this constructor does not take the given
	 * argument.
	 * 
	 * @param argument
	 *            a constructor argument
	 * @return the index of the given argument, or <code>-1</code> if unknown
	 */
	int getArgumentIndex(IConstructorArgument argument);

	/**
	 * Tells whether this constructor is a basic constructor. A basic
	 * constructor is such that none of its argument types reference the
	 * datatype type itself (no recursive use of the datatype).
	 *
	 * @return <code>true</code> iff this is a basic constructor
	 * @since 3.9
	 */
	boolean isBasic();

	/**
	 * Returns the datatype to which this constructor extension belongs.
	 * 
	 * @return the datatype of this constructor extension
	 */
	@Override
	IDatatype getOrigin();

}
