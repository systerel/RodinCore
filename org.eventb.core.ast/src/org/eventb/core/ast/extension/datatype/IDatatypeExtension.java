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

import org.eventb.core.ast.FormulaFactory;

/**
 * Common protocol for the definition of datatypes.
 * <p>
 * This interface is intended to be implemented by clients who wish to define
 * new datatypes, parse them and/or build formulae with datatype nodes.
 * </p>
 * <p>
 * Instances of this interface are intended to be passed as argument to
 * {@link FormulaFactory#makeDatatype(IDatatypeExtension)} in order to obtain a
 * datatype instance.
 * </p>
 * 
 * @see IDatatype
 * @author Nicolas Beauger
 * @since 2.0
 * 
 */
public interface IDatatypeExtension {

	/**
	 * Returns the name of this datatype.
	 * <p>
	 * The name is used as syntax symbol for the expression and the type
	 * constructor of this datatype.
	 * </p>
	 * 
	 * @return a name
	 */
	String getTypeName();

	/**
	 * Returns the unique id of this datatype.
	 * <p>
	 * Returned id is subsequently used as type constructor extension id.
	 * </p>
	 * 
	 * @return a unique id
	 */
	String getId();

	/**
	 * Returns the unique group id of this datatype.
	 * <p>
	 * Returned id is subsequently used as type constructor extension group id.
	 * </p>
	 * 
	 * @return a unique id
	 */
	String getGroupId();

	/**
	 * Adds the type parameters of this datatype (if any). Does nothing if this
	 * datatype has no type parameter.
	 * 
	 * @param mediator
	 *            a mediator for adding type parameters
	 */
	void addTypeParameters(ITypeConstructorMediator mediator);

	/**
	 * Adds the type parameters of this datatype (if any).
	 * 
	 * @param mediator
	 *            a mediator for adding constructors
	 */
	void addConstructors(IConstructorMediator mediator);

}
