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
 * Common protocol for type constructor mediators.
 * 
 * <p>
 * Instances of this interface are passed as argument to
 * {@link IDatatypeExtension#addTypeParameters(ITypeConstructorMediator)} method
 * for declaring type parameters of a datatype.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITypeConstructorMediator {

	/**
	 * Adds a new type parameter with the given name.
	 * 
	 * @param name
	 *            a new type parameter name
	 * @throws IllegalArgumentException
	 *             if a type parameter with the same name already exists
	 */
	void addTypeParam(String name);

}
