/*******************************************************************************
 * Copyright (c) 2012, 2014 Systerel and others.
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
 * <p>
 * When specializing a type-environment or a formula, an instance of this class
 * can change by side-effect in the following manner: For each free identifier,
 * not already part of the free identifier substitution and which occurs in the
 * specialized type environment or formula, a new identifier substitution is
 * added to this specialization. It maps that identifier to an identifier with
 * the same name, but bearing the type obtained by specializing the type of the
 * original identifier.
 * </p>
 * <p>
 * For instance, if a specialization substitutes type <code>T</code> for type
 * <code>S</code> and is applied to a type environment containing free
 * identifier <code>x</code> of type <code>S</code>, then the specialization
 * will be complemented with the substitution of <code>x</code> of type
 * <code>T</code> for <code>x</code> of type <code>S</code>.
 * </p>
 * <p>
 * This side-effect is made on purpose to ensure that successive applications of
 * a specialization to several type environments or formulas are all compatible.
 * In particular, if a formula type-checks with respect to some type
 * environment, then the specialized formula also type-checks with the
 * specialized type environment, under the condition that both specialization
 * are performed with the same instance of this class.
 * </p>
 * <p>
 * In the rare cases where this side-effect is not wanted, one can always clone
 * a specialization object and use the clone to apply a specialization.
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
	 * Returns a deep copy of this specialization. This method is useful to
	 * protect a specialization from side-effects when applying it to a type
	 * environment or a mathematical formula.
	 * 
	 * @return a deep copy of this specialization
	 */
	ISpecialization clone();

	/**
	 * Adds a new type substitution to this specialization. All substitutions
	 * will be applied in parallel when specializing a formula. The added
	 * substitution must be compatible with already registered substitutions
	 * (for both given types and free identifiers).
	 * 
	 * @param type
	 *            given type to specialize
	 * @param value
	 *            replacement for the given type
	 * @throws IllegalArgumentException
	 *             if this substitution is not compatible with already
	 *             registered substitutions
	 */
	void put(GivenType type, Type value);

	/**
	 * Adds a new free identifier substitution to this specialization. All
	 * substitutions will be applied in parallel when specializing a formula.
	 * Both parameters must be type-checked. The given identifier must not
	 * denote a type. The added substitution must be compatible with already
	 * registered substitutions (for both given types and free identifiers). The
	 * given expression needs not be well-formed. In case the expression contains
	 * bound identifiers, it is up to the caller to manage that they do not
	 * escape their scope.
	 * 
	 * @param ident
	 *            a typed identifier to substitute
	 * @param value
	 *            a typed expression to substitute for the given identifier
	 * @throws IllegalArgumentException
	 *             if either parameter is not typed, or if the identifier
	 *             denotes a type, or if this substitution is not compatible
	 *             with already registered substitutions
	 * @see Formula#isTypeChecked()
	 */
	void put(FreeIdentifier ident, Expression value);

}
