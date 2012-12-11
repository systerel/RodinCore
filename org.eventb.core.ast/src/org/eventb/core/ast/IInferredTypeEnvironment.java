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
 * Common protocol for inferred type environments.
 * <p>
 * An inferred type environment is a map from names to their respective type and
 * a reference to the initial type environment. It is produced by the formula
 * type-checker as an output.
 * </p>
 * <p>
 * More precisely, the type-checker takes as input a type environment which
 * gives the type of some names and produces as output a new inferred type
 * environment that records the types inferred from the formula.
 * </p>
 * <p>
 * All value access methods return only values from inferred environment and all
 * value set methods add values in inferred environment only if they do not
 * exist in the initial environment.
 * </p>
 * 
 * @author Vincent Monfort
 * @since 3.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @see Formula#typeCheck(ITypeEnvironment)
 */
public interface IInferredTypeEnvironment extends ITypeEnvironmentBuilder {

	/**
	 * Returns the initial type environment that was passed to the formula
	 * type-checker when it created this inferred type environment.
	 * 
	 * @return the initial type environment
	 */
	public ISealedTypeEnvironment getInitialTypeEnvironment();

}
