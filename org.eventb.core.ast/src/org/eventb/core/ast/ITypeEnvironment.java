package org.eventb.core.ast;

import java.util.Set;

import org.eventb.internal.core.typecheck.TypeEnvironment;

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
 */
public interface ITypeEnvironment {

	/**
	 * Adds all mappings of the given type environment to this environment.
	 * <p>
	 * The given type environment must have a disjoint domain with this type
	 * environment, i.e., their set of names must be disjoint.
	 * </p>
	 * 
	 * @param other
	 *            another type environment
	 */
	void addAll(ITypeEnvironment other);

	/**
	 * Adds a given set to this environment.
	 * <p>
	 * The given name must not yet be inside this environment. It will be
	 * assigned its power set as type.
	 * </p>
	 * 
	 * @param name
	 *            the name to add
	 */
	void addGivenSet(String name);

	/**
	 * Adds a name and its specified type in the type environment.
	 * <p>
	 * The given name must not yet be inside this environment.
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

}