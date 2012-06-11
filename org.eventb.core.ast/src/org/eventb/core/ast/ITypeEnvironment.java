/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - published method getFormulaFactory()
 *     Systerel - added support for specialization 
 *******************************************************************************/
package org.eventb.core.ast;

import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Common protocol for type environments.
 * <p>
 * A type environment is a map from names to their respective type. It is used
 * by the formula type-checker as both an input and output.
 * </p>
 * <p>
 * More precisely, the type-checker takes as input a type environment which
 * gives the type of some names and produce as output a new type environment
 * that records the types inferred from the formula.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients. Use
 * {@link FormulaFactory#makeTypeEnvironment()} to create new type environments.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
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
	 * Adds all mappings of the given type environment to this environment.
	 * <p>
	 * All names that are common to this type environment and the given one must
	 * be associated with the same type in both type environments.
	 * </p>
	 * 
	 * @param other
	 *            another type environment
	 */
	void addAll(ITypeEnvironment other);

	/**
	 * Adds the given free identifier to this environment.
	 * <p>
	 * This is a convenience method, fully equivalent to
	 * <code>addName(freeIdent.getName(), freeIdent.getType())</code>.
	 * </p>
	 * 
	 * @param freeIdent
	 *            a free identifier
	 */
	void add(FreeIdentifier freeIdent);
	
	/**
	 * Adds all given free identifiers to this environment.
	 * <p>
	 * All given free identifiers must already be type-checked.
	 * All names that are common to this type environment and the given free identifiers
	 * must be associated with the same type.
	 * </p>
	 * 
	 * @param freeIdents
	 *            array of free identifiers
	 */
	void addAll(FreeIdentifier[] freeIdents);
	
	/**
	 * Adds a given set to this environment.
	 * <p>
	 * The given name will be assigned its power set as type.
	 * </p>
	 * <p>
	 * If the given name already occurs in this environment, it must be
	 * associated with its power set.
	 * </p>
	 * 
	 * @param name
	 *            the name to add
	 */
	void addGivenSet(String name);

	/**
	 * Adds a name and its specified type in the type environment.
	 * <p>
	 * If the given name already occurs in this environment, it must be
	 * associated with the given type.
	 * </p>
	 * 
	 * @param name
	 *            the name to add
	 * @param type
	 *            the type to associate to the given name
	 */
	void addName(String name, Type type);

	/**
	 * Returns a deep copy of this type environment.
	 * 
	 * @return a deep copy of this type environment.
	 */
	ITypeEnvironment clone();

	/**
	 * Returns <code>true</code> if the type environment contains the given
	 * name.
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
	 * in <code>typenv</code> also occur in this typing enviroment.
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
	 * Returs whether this type environment is empty.
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
	 * Uses the given specialization to get a new specialization of this type
	 * environment.
	 * 
	 * @since 2.6
	 */
	ITypeEnvironment specialize(ISpecialization specialization);
	
}