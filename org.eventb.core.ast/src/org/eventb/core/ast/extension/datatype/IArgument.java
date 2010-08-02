/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension.datatype;

/**
 * Common protocol for the definition of the arguments of a constructor in a
 * datatype extension.
 * 
 * <p>
 * Instances of this interface are intended to be obtained by calling
 * {@link IConstructorMediator#newArgument(IArgumentType)} or
 * {@link IConstructorMediator#newArgument(String, IArgumentType)} from a
 * {@link IDatatypeExtension} implementation.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IArgument {

	/**
	 * Returns the argument type of this argument.
	 * 
	 * @return an argument type
	 */
	IArgumentType getType();

	/**
	 * Returns whether this argument has an associated destructor.
	 * 
	 * @return <code>true</code> iff this argument has an associated destructor
	 */
	boolean hasDestructor();

	/**
	 * Returns the name of the destructor associated to this argument, or
	 * <code>null</code> if none has been defined.
	 * 
	 * @return a String or <code>null</code>
	 */
	String getDestructor();

}
