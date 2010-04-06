/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added set and get for formula factory
 *******************************************************************************/
package org.eventb.core.tool;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.internal.core.tool.types.IState;

/**
 *
 * The state repository allows different modules to share state.
 * Modules can read and write shared state ({@link IState}) in the repository.
 * <p>
 * The repository has a strict protocol to deal with errors.
 * As soon as an exception is thrown by some of the methods of
 * a repository, the repository keeps throwing that exception.
 * It does not recover.
 * <p>
 * A {@link ITypeEnvironment} is contained in any state repository.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p> 
 * @author Stefan Hallerstede
 * @since 1.1
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IStateRepository <I extends IState> {

	/**
	 * Returns the state stored for the specified state type.
	 * 
	 * @param stateType
	 *            the type of the state
	 * @return the state stored for the specified state type
	 * @throws CoreException
	 *             if no state for the specified type has been set, (i.e. if the
	 *             state was not initialised before being read), or if an
	 *             exception has occurred earlier with this repository
	 */
	I getState(IStateType<? extends I> stateType) throws CoreException;
	
	/**
	 * Set the specified state. The state type is accessible by means of
	 * {@link IState}.
	 * 
	 * @param state
	 *            the state to set
	 * @throws CoreException
	 *             if the state equals <code>null</code> or if an exception
	 *             has occurred earlier with this repository
	 */
	void setState(I state) throws CoreException;
	
	/**
	 * Remove the state of the specified type from the repository. It is not
	 * important if the repository contains a state of the specified type or
	 * not. If such a state is not contained, nothing happens.
	 * 
	 * @param stateType
	 *            the state type of the state to be removed
	 * @throws CoreException
	 *             if an exception has occurred earlier with this repository
	 */
	void removeState(IStateType<? extends I> stateType) throws CoreException;
	
	/**
	 * Returns the type environment stored in the repository.
	 * 
	 * @return the type environment stored in the repository
	 * @throws CoreException
	 *             if an exception has occurred earlier with this repository
	 */
	ITypeEnvironment getTypeEnvironment() throws CoreException;
	
	/**
	 * Sets the type environment of this state repository.
	 * 
	 * @param environment
	 *            the type environment to set
	 * @throws CoreException
	 *             if the passed type environment is <code>null</code> or if
	 *             an exception has occurred earlier with this repository
	 */
	void setTypeEnvironment(ITypeEnvironment environment) throws CoreException;
	
	/**
	 * Returns the formula factory stored in the repository.
	 * 
	 * @return the formula factory stored in the repository
	 * @throws CoreException
	 *             if an exception has occurred earlier with this repository
	 * @since 1.3
	 */
	FormulaFactory getFormulaFactory() throws CoreException;
	
	/**
	 * Sets the formula factory of this state repository.
	 * 
	 * @param factory
	 *            the formula factory to set
	 * @throws CoreException
	 *             if the passed formula factory is <code>null</code> or if
	 *             an exception has occurred earlier with this repository
	 * @since 1.3
	 */
	void setFormulaFactory(FormulaFactory factory) throws CoreException;
}
