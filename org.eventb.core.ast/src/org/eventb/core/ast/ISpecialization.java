/*******************************************************************************
 * Copyright (c) 2012, 2017 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *     University of Southampton - added support for predicate variables
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Common protocol for describing a specialization, which groups together type
 * substitutions, free-identifier substitutions, and predicate-variable
 * substitutions in one object. It allows to specialize a formula by applying
 * all these substitutions at once.
 * <p>
 * Instances of this class can be created only through a formula factory. Type,
 * free-identifier and predicate-variable substitutions are then added one by
 * one using <code>put()</code> methods. Finally, the instance is passed as
 * argument to a <code>specialize()</code> method on a type, type environment or
 * formula.
 * </p>
 * <p>
 * The substitutions can also change the formula factory. Consequently, all
 * substitutes must have been created with the same formula factory as this
 * specialization.
 * </p>
 * <p>
 * These substitutions must be compatible, that is the expression to be
 * substituted for an identifier must bear a type which is the result of
 * applying the type substitutions to the type of the identifier.
 * </p>
 * <p>
 * This condition must hold after every method call. This implies that a type
 * substitution required for a free identifier substitution should be put in the
 * specialization strictly before the considered free identifier substitution.
 * </p>
 * <p>
 * When specializing a type, type-environment or a formula, an instance of this
 * class can change by side-effect in the following manner: For each free
 * identifier, not already part of the free identifier substitution and which
 * occurs in the specialized type, type environment or formula, a new identifier
 * substitution is added to this specialization. It maps that identifier to an
 * identifier with the same name, but bearing the type obtained by specializing
 * the type of the original identifier.
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
 * Similarly, when specializing a formula, an instance of this class can change
 * by side-effect in the following manner: For each predicate variable, not
 * already part of the predicate variable substitution and which occurs in the
 * formula, a new predicate variable substitution is added to this
 * specialization. It maps that predicate variable to itself.
 * </p>
 * <p>
 * In the rare cases where the side-effects are not wanted, one can always clone
 * a specialization object and use the clone to apply a specialization.
 * </p>
 * 
 * @author Laurent Voisin
 * @author htson - added support for predicate variables.
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
	 * Returns the formula factory with which this specialization was created.
	 * 
	 * @return the formula factory of this specialization
	 * @since 3.1
	 */
	FormulaFactory getFactory();

	/**
	 * Adds a new type substitution to this specialization. All substitutions
	 * will be applied in parallel when specializing a formula. The added
	 * substitution must be compatible with already registered substitutions
	 * (for both given types and free identifiers). The value must have been
	 * created by the same formula factory as this instance.
	 * </p>
	 * <p>
	 * This method can have side-effects, as described in
	 * {@link ISpecialization}.
	 * </p>
	 * 
	 * @param type
	 *            given type to specialize
	 * @param value
	 *            replacement for the given type
	 * @throws IllegalArgumentException
	 *             if this substitution is not compatible with already
	 *             registered substitutions or the value has been created by
	 *             another formula factory
	 * @see Type#getFactory()
	 * @see #canPut(GivenType, Type)
	 */
	void put(GivenType type, Type value);

	/**
	 * Returns the type to be substituted for the given given type.
	 * 
	 * @param type
	 *            some given type
	 * @return the type to be substituted for the given given type or
	 *         <code>null</code> if no substitution has been defined for the
	 *         given given type
	 * @since 3.1
	 */
	Type get(GivenType type);

	/**
	 * Adds a new free identifier substitution to this specialization. All
	 * substitutions will be applied in parallel when specializing a formula.
	 * Both parameters must be type-checked. The given identifier must not
	 * denote a type. The added substitution must be compatible with already
	 * registered substitutions (for both given types and free identifiers). The
	 * given expression needs not be well-formed. In case the expression contains
	 * bound identifiers, it is up to the caller to manage that they do not
	 * escape their scope. The given expression must have been created by the
	 * same formula factory as this instance.
	 * <p>
	 * This method can have side-effects, as described in
	 * {@link ISpecialization}.
	 * </p>
	 * 
	 * @param ident
	 *            a typed identifier to substitute
	 * @param value
	 *            a typed expression to substitute for the given identifier
	 * @throws IllegalArgumentException
	 *             if either parameter is not typed, or if the identifier
	 *             denotes a type, or if this substitution is not compatible
	 *             with already registered substitutions or the value has been
	 *             created by another formula factory
	 * @see Formula#isTypeChecked()
	 * @see Formula#getFactory()
	 * @see #canPut(FreeIdentifier, Expression)
	 */
	void put(FreeIdentifier ident, Expression value);

	/**
	 * Returns the expression to be substituted for the given identifier.
	 * 
	 * @param ident
	 *            a typed identifier
	 * @return the expression to be substituted for the given identifier or
	 *         <code>null</code> if no substitution has been defined for the
	 *         given identifier
	 * @since 3.1
	 */
	Expression get(FreeIdentifier ident);

	/**
	 * Checks whether the proposed type substitution is compatible with already
	 * registered substitutions (for given types, free identifiers, and
	 * predicate variables), i.e., if the precondition for
	 * {@link #put(GivenType, Type)} is satisfied. The value must have been
	 * created by the same formula factory as this instance. This method does
	 * <em>not</em> modify the specialization instance.
	 * 
	 * @param type
	 *            given type to specialize
	 * @param value
	 *            replacement for the given type
	 * @return <code>true</code> if the proposed type substitution is compatible
	 *         with already registered substitutions, <code>false</code>
	 *         otherwise.
	 * @throws IllegalArgumentException
	 *             if the value has been created by another formula factory
	 * @author htson
	 * @since 3.3
	 */
	boolean canPut(GivenType type, Type value);
	
	/**
	 * Checks whether the proposed free-identifier substitution is compatible
	 * with already registered substitutions (for given types, free identifiers,
	 * and predicate variables), i.e., if the precondition of
	 * {@link #put(FreeIdentifier, Expression)} is satisfied. Both the type and
	 * the value must be type-checked. The value expression must have been
	 * created by the same formula factory as this instance. This method does
	 * <em>not</em> modify the specialization instance.
	 * 
	 * @param ident
	 *            a typed identifier to substitute
	 * @param value
	 *            a typed expression to substitute for the given identifier
	 * @return <code>true</code> if the proposed free identifier substitution is
	 *         compatible with already registered substitutions. Return
	 *         <code>false</code> otherwise.
	 * @throws IllegalArgumentException
	 *             if either parameter is not typed, or the value has been
	 *             created by another formula factory
	 * @author htson
	 * @since 3.3
	 */
	boolean canPut(FreeIdentifier ident, Expression value);

	/**
	 * Adds a new predicate variable substitution to this specialization. All
	 * substitutions will be applied in parallel when specializing a formula.
	 * The given predicate value must be typed, but needs not be well-formed. In
	 * case the predicate value contains bound identifiers, it is up to the
	 * caller to manage that they do not escape their scope. The given predicate
	 * value must have been created by the same formula factory as this
	 * instance.
	 * <p>
	 * This method can have side-effects, as described in
	 * {@link ISpecialization}.
	 * </p>
	 * 
	 * @param predVar
	 *            a predicate variable to substitute
	 * @param value
	 *            a typed predicate to substitute for the given predicate
	 *            variable.
	 * @return <code>true</code> if the proposed predicate variable substitution
	 *         is compatible with already registered substitutions, and this
	 *         specialization instance has been changed accordingly. Return
	 *         <code>false</code> otherwise and this specialization instance is
	 *         unchanged.
	 * @throws IllegalArgumentException
	 *             if the value is not typed or has been created by another
	 *             formula factory
	 * @see Formula#isTypeChecked()
	 * @see Formula#getFactory()
	 * @author htson
	 * @since 3.3
	 */
	boolean put(PredicateVariable predVar, Predicate value);

	/**
	 * Returns the predicate to be substituted for the given predicate variable.
	 * 
	 * @param predVar
	 *            a predicate variable.
	 * @return the predicate to be substituted for the given predicate variable
	 *         or <code>null</code> if no substitution has been defined for the
	 *         given predicate variable
	 * @author htson
	 * @since 3.3
	 */
	Predicate get(PredicateVariable predVar);

	/**
	 * Returns the set of given types to be substituted.
	 * 
	 * @return the set of given types to be substituted
	 * @author htson
	 * @since 3.3
	 */
	GivenType[] getTypes();

	/**
	 * Returns the set of free identifiers to be substituted.
	 * 
	 * @return the set of free identifiers to be substituted
	 * @author htson
	 * @since 3.3
	 */
	FreeIdentifier[] getFreeIdentifiers();

	/**
	 * Returns the set of predicate variables to be substituted.
	 * 
	 * @return the set of predicate variables to be substituted
	 * @author htson
	 * @since 3.3
	 */
	PredicateVariable[] getPredicateVariables();

}
