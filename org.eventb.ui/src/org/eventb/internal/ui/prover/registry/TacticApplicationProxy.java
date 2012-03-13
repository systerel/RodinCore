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
package org.eventb.internal.ui.prover.registry;

import static org.eventb.internal.ui.UIUtils.log;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eventb.core.seqprover.ITactic;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Internal implementation of {@link ITacticApplication} that encapsulates the
 * implementations provided by clients. The purpose of this class is to act as a
 * firewall with respect to clients.
 * 
 * @author Laurent Voisin
 */
public abstract class TacticApplicationProxy<T extends ITacticApplication> {

	/**
	 * Protocol of an abstract factory for creating proxys.
	 */
	public abstract static class TacticApplicationFactory<T extends TacticApplicationProxy<?>> {

		/**
		 * Returns a new proxy object for the given application or
		 * <code>null</code> if the given application is incompatible (e.g., an
		 * <code>IPositionApplication</code> when an
		 * <code>IPredicateApplication</code> is expected).
		 * 
		 * @param provider
		 *            descriptor of the tactic provider that created the
		 *            application
		 * @param application
		 *            some tactic application to wrap around
		 * @return a new proxy object or <code>null</code>
		 */
		public abstract T create(TacticProviderInfo provider,
				ITacticApplication application);

	}

	/**
	 * Encapsulates safely a call to client code, optionally performing validity
	 * checks on the value returned by client code.
	 * 
	 * @param <V>
	 *            type of the value returned by client code
	 */
	protected abstract class SafeCall<V> implements ISafeRunnable {

		V result;

		public V call() {
			SafeRunner.run(this);
			if (!isValid()) {
				return defaultValue();
			}
			return result;
		}

		@Override
		public final void handleException(Throwable exception) {
			// Exception has already been logged by SafeRunner.
		}

		/**
		 * @return a replacement value if the client code returned null
		 */
		protected V defaultValue() {
			return null;
		}

		protected boolean isValid() {
			return result != null;
		}

	}

	protected final TacticProviderInfo provider;

	protected final T client;

	protected TacticApplicationProxy(TacticProviderInfo provider, T client) {
		this.provider = provider;
		this.client = client;
	}

	public ITactic getTactic(final String[] inputs, final String globalInput) {
		return new SafeCall<ITactic>() {
			@Override
			public void run() throws Exception {
				result = client.getTactic(inputs, globalInput);
			}

			@Override
			protected boolean isValid() {
				if (result == null) {
					log(null, "Null returned by getTactic() for tactic "
							+ getTacticID());
					return false;
				}
				return true;
			}
		}.call();
	}

	/*
	 * Here, we bypass the implementation provided by the client and return
	 * directly the id of the tactic provider, thus enforcing the contract for
	 * getTacticID().
	 */
	public String getTacticID() {
		return provider.getID();
	}

	public boolean isSkipPostTactic() {
		return provider.isSkipPostTactic();
	}

}
