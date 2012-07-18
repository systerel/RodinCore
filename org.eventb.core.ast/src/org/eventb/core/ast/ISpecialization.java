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
 * Common protocol for describing a specialization, which groups together a type
 * substitution and a free identifier substitution in one object. It allows to
 * specialize a formula by applying both substitutions at once.
 * <p>
 * Instances of this class can be created only through a formula factory. Type
 * and free identifier substitutions are then added one by one using
 * <code>put()</code> methods. Finally, the instance is passed as argument to a
 * <code>specialize()</code> method on a type, type environment or formula.
 * </p>
 * <p>
 * Both substitutions must be compatible, that is the expression to be
 * substituted for an identifier must bear a type which is the result of
 * applying the type substitution to the type of the identifier.
 * </p>
 * <p>
 * This condition must hold after every method call. This implies that a type
 * substitution required for a free identifier substitution should be put in the
 * specialization strictly before the considered free identifier substitution.
 * </p>
 * 
 * @author Laurent Voisin
 * 
 * @see FormulaFactory#makeSpecialization()
 * @see Type#specialize(ISpecialization)
 * @see ITypeEnvironment#specialize(ISpecialization)
 * @see Formula#specialize(ISpecialization)
 * 
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.6
 */
public interface ISpecialization {

	/**
	 * Adds a new type substitution to this specialization. All substitutions
	 * will be applied in parallel when specializing a formula. The added
	 * substitution must be compatible with already registered substitutions
	 * (for both given types and free identifiers).
	 * 
	 * @param key
	 *            given type to specialize
	 * @param value
	 *            replacement for the given type
	 * @throws IllegalArgumentException
	 *             if this substitution is not compatible with already
	 *             registered substitutions
	 */
	void put(GivenType key, Type value);

	/**
	 * Adds a new free identifier substitution to this specialization. All
	 * substitutions will be applied in parallel when specializing a formula.
	 * Both parameters must be type-checked. The given identifier must not
	 * denote a type. The added substitution must be compatible with already
	 * registered substitutions (for both given types and free identifiers).
	 * 
	 * @param ident
	 *            a typed identifier to substitute
	 * @param value
	 *            a typed expression to substitute for the given identifier
	 * @throws IllegalArgumentException
	 *             if either parameter is not typed, or if the identifier
	 *             denotes a type, or if this substitution is not compatible
	 *             with already registered substitutions
	 */
	void put(FreeIdentifier ident, Expression value);

}
