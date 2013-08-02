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

import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;

/**
 * Common protocol for obtaining datatype argument sets.
 * <p>
 * Implementations of this interface are obtained from a set built with a type
 * constructor. They allow to compute the set to which an argument of a value
 * constructor must belong for the constructed value belongs to the initial set.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 3.0
 * @see IDatatype#getSetInstantiation(Expression)
 * @see IConstructorArgument#getSet(ISetInstantiation)
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISetInstantiation {

	/**
	 * Returns the datatype that built this set instantiation.
	 * 
	 * @return the datatype of this set instantiation
	 */
	IDatatype getOrigin();

	/**
	 * Returns the set from which this instance was built, that is a set built
	 * with the type constructor of the datatype of this instance.
	 * 
	 * @return the set used to build this instance
	 */
	ExtendedExpression getInstanceSet();

}
