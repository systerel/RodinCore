/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core.sc;

import java.util.Hashtable;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.sc.IState;
import org.eventb.core.sc.IStateRepository;
import org.eventb.internal.core.Util;

/**
 * @author Stefan Hallerstede
 *
 */
public final class StateRepository implements IStateRepository {
	
	private CoreException exception;

	public static final int REPOSITORY_SIZE = 117;
	
	private boolean fileChanged;
	
	private final FormulaFactory factory;
	
	public StateRepository(FormulaFactory factory) {
		this.factory = factory;
		fileChanged = false;
		repository = new Hashtable<String, IState>(REPOSITORY_SIZE);
		exception = null;
	}
	
	private final Hashtable<String, IState> repository;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IStateRepository#getState(java.lang.String)
	 */
	public IState getState(String stateType) throws CoreException {
		if (exception != null)
			throw exception;
		IState state = repository.get(stateType);
		if (state == null)
			throw Util.newCoreException(Messages.sctool_UninitializedStateError);
		return state;
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

	public void setChanged() throws CoreException {
		if (exception != null)
			throw exception;
		fileChanged = true;
	}

	public void setState(IState state) throws CoreException {
		if (exception != null)
			throw exception;
		repository.put(state.getStateType(), state);
	}

	public void removeState(String stateType) throws CoreException {
		if (exception != null)
			throw exception;
		
		repository.remove(stateType);
	}

}
