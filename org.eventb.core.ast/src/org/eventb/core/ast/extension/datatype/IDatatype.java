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
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.extension.IExpressionExtension;
import org.eventb.core.ast.extension.IFormulaExtension;

/**
 * Common protocol for datatypes.
 * <p>
 * Instances of this interface are intended to be made through
 * {@link FormulaFactory#makeDatatype(IDatatypeExtension)}.
 * </p>
 * 
 * @author Nicolas Beauger
 * @since 2.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IDatatype {

	/**
	 * Returns the type parameters of this datatype.
	 * 
	 * @return a list of type parameters (possibly empty)
	 */
	List<ITypeParameter> getTypeParameters();

	/**
	 * Returns the type constructor extension for this datatype.
	 * <p>
	 * Returned extension can be used as parameter in
	 * {@link FormulaFactory#makeParametricType(List, IExpressionExtension)} as
	 * well as makeExtendedExpression(IExpressionExtension, ...) methods.
	 * </p>
	 * 
	 * @return an expression extension
	 */
	IExpressionExtension getTypeConstructor();

	/**
	 * Returns the constructor extension associated to the given id, or
	 * <code>null</code> if not found.
	 * 
	 * <p>
	 * Given id refers to the same id parameter given through
	 * {@link IConstructorMediator#addConstructor(String, String, List)}.
	 * </p>
	 * 
	 * @param constructorId
	 *            a String identifier
	 * @return an expression extension or <code>null</code>
	 */
	IExpressionExtension getConstructor(String constructorId);

	/**
	 * Returns the destructor extension that corresponds to the given
	 * constructor id and the given argument number, or <code>null</code> if not
	 * found.
	 * <p>
	 * The argument number is the zero-based index of the constructor argument
	 * that corresponds to the destructor target. For instance, in constructor
	 * definition <b>cons(head, tail)</b>, <b>head</b> has argument number 0 and
	 * <b>tail</b> has argument number 1.
	 * </p>
	 * <p>
	 * <code>null</code> is returned when:
	 * <li>the given id is not associated to a constructor</li>
	 * <li>the given argument number is out of the bounds of the constructor
	 * arguments</li>
	 * <li>the argument corresponding to the given argument number has no
	 * associated destructor</li>
	 * </p>
	 * 
	 * @param constructorId
	 *            a String id
	 * @param argNumber
	 *            a natural number
	 * @return an expression extension, or <code>null</code>
	 */
	IExpressionExtension getDestructor(String constructorId, int argNumber);

	/**
	 * Returns a set of all formula extensions defined by this datatype.
	 * <p>
	 * Defined extensions are:
	 * <li>the type constructor</li>
	 * <li>all constructors</li>
	 * <li>all destructors</li>
	 * </p>
	 * 
	 * @return a set of formula extensions
	 */
	Set<IFormulaExtension> getExtensions();

}
