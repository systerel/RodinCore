/*******************************************************************************
 * Copyright (c) 2012, 2013 Systerel and others.
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
 * Common protocol for building type environments.
 * <p>
 * See {@link ITypeEnvironment} for the general description.
 * </p>
 * <p>
 * When the type environment is built, an immutable copy of this environment
 * could be retrieved by calling {@link ITypeEnvironmentBuilder#makeSnapshot()}
 * or a mutable copy by calling {@link ITypeEnvironment#makeBuilder()}.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients. Use
 * {@link FormulaFactory#makeTypeEnvironment()} to create new type environments.
 * </p>
 * 
 * @author Vincent Monfort
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITypeEnvironmentBuilder extends ITypeEnvironment {

	/**
	 * Adds all mappings of the given type environment to this environment.
	 * <p>
	 * All names that are common to this type environment and the given one must
	 * be associated with the same type in both type environments.
	 * </p>
	 * 
	 * @param other
	 *            another type environment
	 * @throws IllegalArgumentException
	 *             if the mappings of the given type environment are
	 *             incompatible with this type environment
	 * @throws IllegalArgumentException
	 *             if the given type environment formula factory is not the
	 *             same as the current type environment formula factory
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
	 * @throws IllegalArgumentException
	 *             if the given free identifier is either not a valid identifier
	 *             or not typed, or incompatible with this type environment
	 * @throws IllegalArgumentException
	 *             if the free identifier formula factory is not the same as
	 *             the type environment formula factory
	 */
	void add(FreeIdentifier freeIdent);

	/**
	 * Adds all given free identifiers to this environment.
	 * <p>
	 * All given free identifiers must already be type-checked. All names that
	 * are common to this type environment and the given free identifiers must
	 * be associated with the same type.
	 * </p>
	 * 
	 * @param freeIdents
	 *            array of free identifiers
	 * @throws IllegalArgumentException
	 *             if any given free identifier is ill-formed
	 * @throws IllegalArgumentException
	 *             if a free identifier formula factory is not the same as
	 *             the type environment formula factory
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
	 * associated with the given type. The given type is also analyzed to check
	 * that GivenType types used are defined (if not add them) or are coherent
	 * with current environment.
	 * </p>
	 * 
	 * @param name
	 *            the name to add
	 * @param type
	 *            the type to associate to the given name
	 * @throws IllegalArgumentException
	 *             if the given name is not a valid identifier name or if the
	 *             given type is not compatible with this type environment:
	 *             different from a type already registered with the same name
	 *             or containing incompatible carrier sets
	 * @throws IllegalArgumentException
	 *             if the given type formula factory is not the same as the type
	 *             environment formula factory
	 */
	void addName(String name, Type type);
	
	/**
	 * Creates fresh identifiers corresponding to some bound identifier
	 * declarations and inserts them into the type environment.
	 * <p>
	 * For each bound identifier declaration, a free identifier is created. This
	 * new identifier has a name that does not occur in the given type
	 * environment and is based on the given declaration. It is guaranteed that
	 * not two free identifiers in the result bear the same name.
	 * </p>
	 * <p>
	 * The given bound identifier declarations must be typed. The types are then
	 * stored in the type environment for the corresponding fresh free
	 * identifiers.
	 * </p>
	 * 
	 * @param boundIdents
	 *            array of bound identifier declarations for which corresponding
	 *            fresh free identifiers should be created. Each declaration
	 *            must be typed
	 * @return an array of fresh free identifiers
	 * @since 3.0
	 */
	FreeIdentifier[] makeFreshIdentifiers(BoundIdentDecl[] boundIdents);

	@Override
	public ITypeEnvironmentBuilder translate(FormulaFactory factory);

}