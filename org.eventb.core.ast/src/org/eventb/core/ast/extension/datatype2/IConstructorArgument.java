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
import org.eventb.core.ast.Type;

/**
 * Common protocol for arguments of datatype constructors. Constructors can take
 * two kind of arguments:
 * <ul>
 * <li>anonymous arguments which are passed to the constructor but whose value
 * can no longer be obtained from the constructed value;</li>
 * <li>named arguments that are also a destructor that allows to retrieve their
 * value from the constructed value.</li>
 * </ul>
 * 
 * @author Laurent Voisin
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IConstructorArgument {

	/**
	 * Returns the datatype to which this constructor argument belongs.
	 * 
	 * @return the datatype of this constructor argument
	 */
	IDatatype2 getOrigin();

	/**
	 * The constructor extension of which this object is an argument.
	 * 
	 * @return the datatype constructor
	 */
	IConstructorExtension getConstructor();

	/**
	 * Tells whether this argument can be retrieved from a datatype value.
	 * 
	 * @return <code>true</code> iff this argument is also a destructor
	 */
	boolean isDestructor();

	/**
	 * Returns this argument as a destructor, or <code>null</code> if this
	 * argument is not a destructor.
	 * 
	 * @return this argument as a destructor, or <code>null</code>
	 */
	IDestructorExtension asDestructor();

	/**
	 * Returns the type of this argument in the given instantiation.
	 * 
	 * @param instantiation
	 *            a type instantiation obtained from the datatype of this
	 *            argument
	 * @return the type of this argument in the given instantiation
	 * @throws IllegalArgumentException
	 *             if the instantiation comes from a different origin
	 */
	Type getType(ITypeInstantiation instantiation);

	/**
	 * Returns the set of this argument in the given instantiation, that is the
	 * set to which this argument must belong such that a value constructed with
	 * this argument belongs to the set given at instantiation creation.
	 * 
	 * @param instantiation
	 *            a set instantiation obtained from the datatype of this
	 *            argument
	 * @return the set of this argument in the given instantiation
	 * @throws IllegalArgumentException
	 *             if the instantiation comes from a different origin
	 */
	Expression getSet(ISetInstantiation instantiation);

}
