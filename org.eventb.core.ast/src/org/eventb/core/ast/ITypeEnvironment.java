/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - published method getFormulaFactory()
 *     Systerel - added support for specialization
 *     Systerel - immutable type environments
 *     Systerel - added support for factory translation
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Common protocol for accessing type environments.
 * <p>
 * A type environment is a map from names to their respective type. It is used
 * by the formula type-checker as both an input and output.
 * </p>
 * <p>
 * More precisely, the type-checker takes as input a type environment which
 * gives the type of some names and produces as output a new inferred type
 * environment that records the types inferred from the formula.
 * </p>
 * <p>
 * Type environments enforce name consistency. This means that all names must be
 * valid identifier names in the associated language (as defined by the formula
 * factory used to create this type environment) and that any name that occurs
 * in a registered type (i.e., any carrier set name) is automatically added to
 * the type environment as a given set.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients. Use
 * {@link FormulaFactory#makeTypeEnvironment()} to create new type environments.
 * </p>
 * <p>
 * There are two implementations of this interface. One is mutable and allows to
 * gradually build a type environment (see {@link ITypeEnvironmentBuilder}). The
 * other is immutable and stores a stable type environment (see
 * {@link ISealedTypeEnvironment}). Methods
 * {@link ITypeEnvironment#makeSnapshot()} and
 * {@link ITypeEnvironment#makeBuilder()} allow to navigate between these two
 * variants.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @see FormulaFactory#isValidIdentifierName(String)
 */
public interface ITypeEnvironment {

	/**
	 * Protocol for iterating over a type environment.
	 * <p>
	 * Typical use for this iterator looks like:
	 * <pre>
	 *    Iterator iterator = typeEnvironment.getIterator();
	 *    while (iterator.hasNext()) {
	 *        iterator.advance();
	 *        String name = iterator.getName();
	 *        Type type = iterator.getType();
	 *        ...
	 *    }
	 * </pre>
	 * </p>
	 */
	static interface IIterator {

		/**
		 * Tells whether the iterator has reached the last mapping of the iterated
		 * type environment.
		 * 
		 * @return <code>true</code> if there is another mapping to iterate over
		 */
		boolean hasNext();

		/**
		 * Advance to the next mapping in the iterated type environment.
		 * 
		 * @throws NoSuchElementException
		 *             if the iterator has been exhausted
		 */
		void advance() throws NoSuchElementException;

		/**
		 * Returns the name of the current mapping (that is its left-hand side).
		 * The method {@link #advance()} must have been called at least once
		 * since the creation of the iterator, before calling this method.
		 * 
		 * @return the name of the current mapping
		 * @throws NoSuchElementException
		 *             if <code>advance</code> hasn't been called before
		 */
		String getName() throws NoSuchElementException;

		/**
		 * Returns the type of the current mapping (that is its right-hand side).
		 * The method {@link #advance()} must have been called at least once
		 * since the creation of the iterator, before calling this method.
		 * 
		 * @return the type of the current mapping
		 * @throws NoSuchElementException
		 *             if <code>advance</code> hasn't been called before
		 */
		Type getType() throws NoSuchElementException;

		/**
		 * Returns whether the current mapping denotes a given set. Given set
		 * are recognized by the fact that they associate a name <code>S</code>
		 * to the type <code>ℙ(S)</code>.
		 * <p>
		 * The method {@link #advance()} must have been called at least once
		 * since the creation of the iterator, before calling this method.
		 * </p>
		 * 
		 * @return <code>true</code> iff the current mapping denotes a given
		 *         set
		 * @throws NoSuchElementException
		 *             if <code>advance</code> hasn't been called before
		 */
		boolean isGivenSet() throws NoSuchElementException;
	}

	/**
	 * Returns whether this type environment contains the given name.
	 * 
	 * @param name
	 *            the name to lookup
	 * @return <code>true</code> iff the type environment contains the given
	 *         name
	 */
	boolean contains(String name);

	/**
	 * Returns whether the given type environment is a subset of this type
	 * environment.
	 * <p>
	 * In other words, this method returns <code>true</code> iff all mappings
	 * in <code>typenv</code> also occur in this typing environment.
	 * </p>
	 * 
	 * @param typenv
	 *            the type environment to check for inclusion
	 * 
	 * @return <code>true</code> iff the given type environment is a subset of
	 *         this type environment
	 */
	boolean containsAll(ITypeEnvironment typenv);

	/**
	 * Returns a fresh new datatype translation for this type environment. The
	 * resulting translation will be applicable to any formula which is
	 * type-checked within this type environment.
	 * 
	 * @return a fresh datatype translation context
	 * @see Formula#translateDatatype(IDatatypeTranslation)
	 * @since 2.7
	 */
	IDatatypeTranslation makeDatatypeTranslation();

	/**
	 * Returns an iterator for traversing this type environment.
	 * 
	 * @return an iterator on this type environment.
	 */
	IIterator getIterator();

	/**
	 * Returns the set of all names mapped in this type environment.
	 * 
	 * @return the set of all mapped names
	 */
	Set<String> getNames();

	/**
	 * Gets the type of a name in this type environment.
	 * 
	 * @param name
	 *            the name to lookup
	 * @return the type associated to the given name or <code>null</code> if
	 *         it is not in this type environment.
	 */
	Type getType(String name);

	/**
	 * Returns whether this type environment is empty.
	 * 
	 * @return <code>true</code> iff this environment doesn't contain any
	 *         mapping.
	 */
	boolean isEmpty();

	/**
	 * Returns the formula factory associated with this type environment.
	 * 
	 * @return the associated formula factory
	 * @since 2.0
	 */
	FormulaFactory getFormulaFactory();

	/**
	 * Returns the type environment obtained by applying the given
	 * specialization to this type environment. The effect of this application
	 * is to specialize all types of the type environment, to remove all free
	 * identifiers that are substituted by the given specialization and to
	 * introduce all free identifiers that occur in substituting expressions for
	 * the free identifiers that have been removed.
	 * <p>
	 * The given specialization may change as a side-effect of calling this
	 * method. Please see the documentation of {@link ISpecialization} for
	 * details.
	 * </p>
	 * <p>
	 * The returned type environment is always a new object, even if the
	 * specialization has no effect on this type environment (e.g., no
	 * substitution of type or free identifier has been performed),
	 * </p>
	 * 
	 * @param specialization
	 *            the specialization to apply
	 * @return the type environment obtained by applying the given
	 *         specialization to this type environment
	 * @since 3.0
	 */
	ITypeEnvironmentBuilder specialize(ISpecialization specialization);

	/**
	 * Checks if the current type environment can be translated with the given
	 * factory.
	 * <p>
	 * A type environment is compatible with the given factory either if the
	 * factory is the type environment factory or if type environment elements
	 * names are not reserved words in the given factory and elements types can
	 * be translated.
	 * </p>
	 * 
	 * @return <code>false</code> only if a call to
	 *         {@link ITypeEnvironment#translate(FormulaFactory)} will fail by
	 *         raising an exception and returns <code>true</code> otherwise.
	 * @since 3.0
	 */
	boolean isTranslatable(FormulaFactory fac);

	/**
	 * Returns the type environment built by using the given formula factory.
	 * <p>
	 * If the type environment factory and the given factory are the same then
	 * the same type environment object is returned. Otherwise a new type
	 * environment object is built. In both cases the type environment returned
	 * has the same type as the current type environment (e.g. if the type
	 * environment is a {@link ITypeEnvironmentBuilder}, then the returned one
	 * is also a {@link ITypeEnvironmentBuilder}).
	 * </p>
	 * <p>
	 * The translation of the type environment can fail if an element has a name
	 * that is a reserved keyword in the target formula factory or if an element
	 * has a type which is not translatable.
	 * </p>
	 * <p>
	 * This operation is not supported for {@link IInferredTypeEnvironment}.
	 * </p>
	 * 
	 * @param fac
	 *            the factory to use to rebuild the type environment
	 * @return the type environment obtained by the rebuilt based on the given
	 *         factory
	 * @throws IllegalArgumentException
	 *             if the type environment contains elements for which names are
	 *             reserved keywords in the given formula factory
	 * @throws IllegalArgumentException
	 *             if the type environment contains elements for which type use
	 *             a reserved keywords in the given formula factory
	 * @throws IllegalArgumentException
	 *             if the type environment contains elements for which type use
	 *             an extension not supported by the given factory
	 * @throws UnsupportedOperationException
	 *             if the type environment is an inferred type environment
	 * @since 3.0
	 */
	ITypeEnvironment translate(FormulaFactory fac);

	/**
	 * Get an immutable snapshot of this type environment.
	 * 
	 * @return a snapshot of this type environment
	 * @since 3.0
	 */
	ISealedTypeEnvironment makeSnapshot();

	/**
	 * Get a mutable copy of this type environment in order to build a new one.
	 * The copy is guaranteed to be a different instance and will evolve
	 * independently of this type environment.
	 * 
	 * @return a copy of this type environment as a builder
	 * @since 3.0
	 */
	ITypeEnvironmentBuilder makeBuilder();

}
