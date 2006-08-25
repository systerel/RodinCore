/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.core.sc;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;

/**
 * @author Stefan Hallerstede
 *
 * The state repsotory allows different static checker modules to share state.
 */
public interface IStateRepository {

	/**
	 * Returns the state stored for the specified state type.
	 * @param stateType the type identifier of the state
	 * @return the state stored for the specified state type
	 * @throws CoreException if no state for the specified type has been created,
	 * 		i.e. if the state was not initialised before being read.
	 */
	IState getState(String stateType) throws CoreException;
	
	void setState(IState state) throws CoreException;
	
	void removeState(String stateType) throws CoreException;
	
	FormulaFactory getFormulaFactory() throws CoreException;
	
	boolean targetHasChanged() throws CoreException;
	
	void setChanged() throws CoreException;
}
