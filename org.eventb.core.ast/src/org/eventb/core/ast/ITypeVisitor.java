/*******************************************************************************
 * Copyright (c) 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Simple visitor for traversing types.  The traversal of the type hierarchy
 * is up to the implementor of this interface.
 * <p>
 * Type variables (which are used internally by the AST type-checking algorithm
 * are not supported by this interface).
 * </p>
 * <p>
 * This interface is intended to be implemented by clients who contribute new
 * visitors.
 * </p>
 * 
 * @see Type#accept(ITypeVisitor)
 * 
 * @author Laurent Voisin
 * @since 2.7
 */
public interface ITypeVisitor {

	/**
	 * Visits the given boolean type node.
	 * 
	 * @param type
	 *            the type node to visit
	 */
	void visit(BooleanType type);

	/**
	 * Visits the given "given type" node.
	 * 
	 * @param type
	 *            the type node to visit
	 */
	void visit(GivenType type);

	/**
	 * Visits the given integer type node.
	 * 
	 * @param type
	 *            the type node to visit
	 */
	void visit(IntegerType type);

	/**
	 * Visits the given parametric type node.
	 * 
	 * @param type
	 *            the type node to visit
	 */
	void visit(ParametricType type);

	/**
	 * Visits the given power set type node.
	 * 
	 * @param type
	 *            the type node to visit
	 */
	void visit(PowerSetType type);

	/**
	 * Visits the given product type node.
	 * 
	 * @param type
	 *            the type node to visit
	 */
	void visit(ProductType type);

}
