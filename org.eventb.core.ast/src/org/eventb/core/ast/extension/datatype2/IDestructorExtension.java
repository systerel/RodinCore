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

import org.eventb.core.ast.extension.IExpressionExtension;

/**
 * Common protocol for datatype destructor extensions.
 * 
 * <p>
 * A destructor extension is the implementation of a datatype destructor, it is
 * characterized by its name and its return type.
 * </p>
 * 
 * @author Vincent Monfort
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IDestructorExtension extends IConstructorArgument,
		IExpressionExtension {

	/**
	 * The destructor name.
	 * 
	 * @return the destructor name
	 */
	public String getName();

}
