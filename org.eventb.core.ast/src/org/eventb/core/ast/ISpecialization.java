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
 * Common protocol for describing a specialization. A specialization groups
 * together a given type substitution and a free identifier substitution in one
 * object. It allows to specialize a parametric formula by applying both
 * substitutions at once.
 * <p>
 * Instances of this class can be created only through a formula factory. Type
 * and free identifier substitutions are then added one by one using
 * <code>put()</code> methods. Finally, the instance is passed as argument to a
 * <code>specialize()</code> method on a type or formula.
 * </p>
 * <p>
 * Both substitutions must be compatible, that is:
 * <ul>
 * <li>The expression to be substituted for an identifier must bear a type which
 * is the result of applying the type substitution to the type of the
 * identifier.</li>
 * <li>If an identifier denotes a given type, its substituting expression must
 * denote the type which is substituted to that given type.</li>
 * </ul>
 * These conditions must hold after every method call.
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
	 * will be applied in parallel when specializing a formula.
	 * 
	 * @param key
	 *            given type to specialize
	 * @param value
	 *            replacement for the given type
	 */
	void put(GivenType key, Type value);

	/**
	 * Adds a new free identifier substitution to this specialization. All
	 * substitutions will be applied in parallel when specializing a formula.
	 * Both parameters must be type-checked.
	 * 
	 * @param ident
	 *            a typed identifier to substitute
	 * @param value
	 *            a typed expression to substitute for the given identifier
	 */
	void put(FreeIdentifier ident, Expression value);

}
