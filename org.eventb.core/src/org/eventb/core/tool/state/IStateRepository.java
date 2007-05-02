/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.tool.state;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;

/**
 * @author Stefan Hallerstede
 *
 * The state repository allows different modules to share state.
 */
// TODO javadoc
public interface IStateRepository <I extends IState> {

	/**
	 * Returns the state stored for the specified state type.
	 * @param stateType the type identifier of the state
	 * @return the state stored for the specified state type
	 * @throws CoreException if no state for the specified type has been created,
	 * 		i.e. if the state was not initialised before being read.
	 */
	I getState(IStateType<? extends I> stateType) throws CoreException;
	
	void setState(I state) throws CoreException;
	
	void removeState(IStateType<? extends I> stateType) throws CoreException;
	
	FormulaFactory getFormulaFactory() throws CoreException;
	
	ITypeEnvironment getTypeEnvironment() throws CoreException;
	
	void setTypeEnvironment(ITypeEnvironment environment) throws CoreException;
	
	boolean targetHasChanged() throws CoreException;
	
	void setTargetChanged() throws CoreException;
}
