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
package org.eventb.core.ast.datatype;

import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.Type;

/**
 * Common protocol for obtaining datatype argument types.
 * <p>
 * Implementations of this interface are obtained from a datatype object
 * and can only be used to compute the actual type of an argument of a value
 * constructor of that datatype.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 3.0
 * @see IDatatype#getTypeInstantiation(Type)
 * @see IConstructorArgument#getType(ITypeInstantiation)
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITypeInstantiation {

	/**
	 * Returns the datatype that built this type instantiation.
	 * 
	 * @return the datatype of this type instantiation
	 */
	IDatatype getOrigin();

	/**
	 * Returns the type from which this instance was built, that is a type built
	 * with the type constructor of the datatype of this instance.
	 * 
	 * @return the type used to build this instance
	 */
	ParametricType getInstanceType();

}
