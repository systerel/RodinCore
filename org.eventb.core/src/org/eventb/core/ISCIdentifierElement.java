/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.core;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.Type;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Common protocol for Event-B statically checked identifiers.
 * <p>
 * A checked identifier is guaranteed to parse and has a type associated to it.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Stefan Hallerstede
 */
public interface ISCIdentifierElement extends IInternalElement {

	/**
	 * Returns the type of this identifier.
	 * 
	 * @param factory
	 *            the formula factory to use for building the result
	 * 
	 * @return the type of this identifier
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	Type getType(FormulaFactory factory) throws RodinDBException;

	/**
	 * Sets the type of this element to a new value.
	 * 
	 * @param type
	 *            the type to give to this identifier
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	void setType(Type type) throws RodinDBException;

	/**
	 * Returns the AST corresponding to this element (a free identifier).
	 * 
	 * @param factory
	 *            the formula factory to use for building the result
	 * @return the AST representation of this element
	 * @throws RodinDBException
	 *             if there was a problem accessing the database
	 */
	FreeIdentifier getIdentifier(FormulaFactory factory) throws RodinDBException;
	
	/**
	 * Returns the name of the identifier as a character string.
	 * 
	 * @return name of the identifier as a character string
	 * @throws RodinDBException if there was a problem accessing the database
	 */
	String getIdentifierName() throws RodinDBException;

// TODO restore this method when identifiers are stored through an indirection.
//	/**
//	 * Sets the identifier contained in this element.
//	 * 
//	 * @param identifier
//	 *            the identifier to set (must be type-checked)
//	 * @throws RodinDBException
//	 *             if there was a problem accessing the database
//	 */
//	void setIdentifier(FreeIdentifier identifier) throws RodinDBException;

}
