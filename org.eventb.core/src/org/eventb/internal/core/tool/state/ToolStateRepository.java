/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.tool.state;

import java.util.Hashtable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.tool.state.IToolState;
import org.eventb.core.tool.state.IToolStateRepository;
import org.eventb.internal.core.Util;
import org.eventb.internal.core.sc.Messages;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class ToolStateRepository<I extends IToolState> implements IToolStateRepository<I> {
	
	private CoreException exception;
	
	private boolean DEBUG = false;
	
	public void debug() {
		DEBUG = true;
	}

	public static final int REPOSITORY_SIZE = 117;
	
	private boolean fileChanged;
	
	private final FormulaFactory factory;
	
	private ITypeEnvironment environment;
	
	public ToolStateRepository(FormulaFactory factory) {
		if (DEBUG)
			System.out.println("NEW STATE REPOSITORY ##################");
		this.factory = factory;
		environment = factory.makeTypeEnvironment();
		fileChanged = false;
		repository = new Hashtable<String, I>(REPOSITORY_SIZE);
		exception = null;
	}
	
	private final Hashtable<String, I> repository;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IStateRepository#getState(java.lang.String)
	 */
	public I getState(String stateType) throws CoreException {
		if (exception != null)
			throw exception;
		I state = repository.get(stateType);
		if (state == null)
			throw Util.newCoreException(Messages.sctool_UninitializedStateError);
		return state;
	}

	public ITypeEnvironment getTypeEnvironment() throws CoreException {
		if (exception != null)
			throw exception;
		return environment;
	}

	public void setTypeEnvironment(ITypeEnvironment environment) throws CoreException {
		if (exception != null)
			throw exception;
		this.environment = environment;
	}

	public FormulaFactory getFormulaFactory() throws CoreException {
		if (exception != null)
			throw exception;
		return factory;
	}

	public boolean targetHasChanged() throws CoreException {
		if (exception != null)
			throw exception;
		return fileChanged;
	}

	public void setTargetChanged() throws CoreException {
		if (exception != null)
			throw exception;
		fileChanged = true;
	}

	public void setState(I state) throws CoreException {
		if (DEBUG)
			System.out.println("SET STATE: " + state.getStateType() + " [" + state.getClass().getName() + "]");
		if (exception != null)
			throw exception;
		if (state == null)
			throw Util.newCoreException(Messages.sctool_NullStateError);
		repository.put(state.getStateType(), state);
	}

	public void removeState(String stateType) throws CoreException {
		if (exception != null)
			throw exception;
		
		repository.remove(stateType);
	}

}
