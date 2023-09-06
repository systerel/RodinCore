/*******************************************************************************
 * Copyright (c) 2006, 2023 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added formula factory field along with set and get methods
 *******************************************************************************/
package org.eventb.internal.core.tool.state;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IEventBRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironmentBuilder;
import org.eventb.core.tool.IState;
import org.eventb.core.tool.IStateRepository;
import org.eventb.core.tool.IStateType;
import org.eventb.internal.core.Util;

/**
 * @author Stefan Hallerstede
 *
 */
public abstract class StateRepository<I extends IState> implements IStateRepository<I> {
	
	private CoreException exception;
	
	private boolean DEBUG = false;
	
	public void debug() {
		DEBUG = true;
	}

	public static final int REPOSITORY_SIZE = 117;
	
	private ITypeEnvironmentBuilder environment;
	
	private FormulaFactory factory;
	
	public StateRepository(IEventBRoot root) throws CoreException {
		if (DEBUG)
			System.out.println("NEW STATE REPOSITORY ##################");
		// init with root factory
		factory = root.getSafeFormulaFactory();
		environment = factory.makeTypeEnvironment();
		repository = new HashMap<IStateType<?>, I>(REPOSITORY_SIZE);
		exception = null;
	}
	
	private final HashMap<IStateType<?>, I> repository;
	
	/* (non-Javadoc)
	 * @see org.eventb.core.sc.IStateRepository#getState(java.lang.String)
	 */
	@Override
	public final <T extends I> T getState(IStateType<T> stateType) throws CoreException {
		if (exception != null)
			throw exception;
		// The cast below is correct by construction: the object has been
		// entered in the repository by using the exact same state type.
		@SuppressWarnings("unchecked")
		T state = (T) repository.get(stateType);
		if (DEBUG)
			System.out.print("GET STATE: " + stateType + 
					((state == null) ? " NONE" : 
						(state.isImmutable() ? " IMMUTABLE" : " MUTABLE")));
		if (state == null) {
			if (DEBUG)
				System.out.println(" FAILED");
			throwNewCoreException("Attempt to access uninitialized state in state repository");
		}
		if (DEBUG)
			System.out.println(" OK");
		return state;
	}

	@Override
	public final ITypeEnvironmentBuilder getTypeEnvironment() throws CoreException {
		if (exception != null)
			throw exception;
		return environment;
	}

	@Override
	public final void setTypeEnvironment(ITypeEnvironmentBuilder environment) throws CoreException {
		if (exception != null)
			throw exception;
		if (environment == null)
			throwNewCoreException("Attempt to create null typenv");
		this.environment = environment;
	}

	@Override
	public FormulaFactory getFormulaFactory() throws CoreException {
		if (exception != null)
			throw exception;
		return factory;
	}
	
	@Override
	public void setFormulaFactory(FormulaFactory factory) throws CoreException {
		if (exception != null)
			throw exception;
		if (factory == null)
			throwNewCoreException("Attempt to set null formula factory");
		this.factory = factory;
	}
	
	@Override
	public final void setState(I state) throws CoreException {
		if (DEBUG)
			System.out.println("SET STATE: " + state.getStateType());
		if (exception != null)
			throw exception;
		if (state == null)
			throwNewCoreException("Attempt to create null state");
		repository.put(state.getStateType(), state);
	}

	@Override
	public final void removeState(IStateType<? extends I> stateType) throws CoreException {
		if (exception != null)
			throw exception;
		
		repository.remove(stateType);
	}
	
	private void throwNewCoreException(String m) throws CoreException {
		exception = Util.newCoreException(m);
		throw exception;
	}

}
