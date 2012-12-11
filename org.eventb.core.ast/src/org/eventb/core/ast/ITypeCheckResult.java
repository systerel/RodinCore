/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.core.ast;

/**
 * Protocol for the result of type-checking a formula.
 * <p>
 * In case of success, a new type environment has been inferred from the
 * formula. This new environment specifies the type of all free identifiers of
 * the formula that didn't occur in the initial type environment.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * 
 * @author Laurent Voisin
 * @since 1.0
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface ITypeCheckResult extends IResult {

	/**
	 * Returns the inferred type environment.
	 * <p>
	 * The inferred type environment contains exactly the types of all
	 * identifiers that occur free in the type-checked formula and that are not
	 * typed in the initial type environment. Hence, the inferred environment
	 * always has an empty intersection with the initial environment.
	 * </p>
	 * <p>
	 * In case of success, this inferred type environment can be safely merged within the initial
	 * type environment using {@link ITypeEnvironmentBuilder#addAll(ITypeEnvironment)}.
	 * </p>
	 * 
	 * @return the inferred type environment or <code>null</code> if
	 *         type-check failed
	 * @since 3.0: the return type changed from ITypeEnvironment to IInferredTypeEnvironment
	 */
	IInferredTypeEnvironment getInferredEnvironment();

	/**
	 * Returns the initial type environment that was provided to the
	 * {@link Formula#typeCheck(ITypeEnvironment)} method. This initial
	 * environment is never modified by the type-check operation.
	 * 
	 * @return the initial type environment
	 */
	ITypeEnvironment getInitialTypeEnvironment();

}