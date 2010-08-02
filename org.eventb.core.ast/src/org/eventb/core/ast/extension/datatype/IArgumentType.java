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

import org.eventb.core.ast.Type;
import org.eventb.core.ast.extension.ITypeMediator;
import org.eventb.internal.core.ast.extension.datatype.TypeInstantiation;

/**
 * Common protocol for the definition of argument types in a datatype extension.
 * 
 * <p>
 * Instances of this interface are intended to be obtained by calling methods
 * from {@link IConstructorMediator} in a {@link IDatatypeExtension}
 * implementation.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IArgumentType {

	/**
	 * Instantiates this argument type from the given type instantiation.
	 * 
	 * @param mediator
	 *            a mediator for building types
	 * @param instantiation
	 *            a type instantiation
	 * @return a type
	 */
	Type toType(ITypeMediator mediator, TypeInstantiation instantiation);

	/**
	 * Verifies that the given proposed type is a valid instantiation of this
	 * argument type for the given type instantiation.
	 * 
	 * @param proposedType
	 *            a type
	 * @param instantiation
	 *            a type instantiation
	 * @return <code>true</code> iff the proposed type is valid
	 */
	boolean verifyType(Type proposedType, TypeInstantiation instantiation);

}
