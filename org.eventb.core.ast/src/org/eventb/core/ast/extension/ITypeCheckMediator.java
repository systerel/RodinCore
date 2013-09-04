/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast.extension;

import org.eventb.core.ast.Type;

/**
 * Common protocol for type-checking formula extensions.
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITypeCheckMediator extends ITypeMediator {

	/**
	 * Returns a fresh type variable that can be used as a place-holder when
	 * checking types.
	 * 
	 * @return a fresh type variable
	 */
	Type newTypeVariable();

	/**
	 * Declares a type-checking constraint stating that two types must be
	 * unifiable each with the other.
	 * 
	 * @param left
	 *            some type
	 * @param right
	 *            some type
	 */
	void sameType(Type left, Type right);

}
