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

import java.util.List;

/**
 * @author Nicolas Beauger
 * @since 2.0
 * 
 */
public interface IConstructorMediator extends IDatatypeMediator {

	/**
	 * Instance with no arguments.
	 * <p>
	 * Same as calling {@link #addConstructor(String, String, String, List)}
	 * with an empty list.
	 * </p>
	 * 
	 * @param name
	 *            the name of the constructor
	 */
	void addConstructor(String name, String id, String groupId);

	/**
	 * Instance with arguments, whose types are given.
	 * <p>
	 * The number of arguments is determined from the size of the given list.
	 * </p>
	 * 
	 * @param name
	 *            the name of the constructor
	 * @param argumentTypes
	 *            a list of types
	 */
	void addConstructor(String name, String id, String groupId,
			List<ITypeParameter> argumentTypes);

}
