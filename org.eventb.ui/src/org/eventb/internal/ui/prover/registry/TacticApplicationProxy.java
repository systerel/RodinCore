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

	protected final TacticProviderInfo provider;

	protected final T client;

	protected TacticApplicationProxy(TacticProviderInfo provider, T client) {
		this.provider = provider;
		this.client = client;
	}

	// FIXME what if the client returns null ?
	public ITactic getTactic(String[] inputs, String globalInput) {
		try {
			return client.getTactic(inputs, globalInput);
		} catch (Throwable exc) {
			log(exc, "when calling getTactic() for " + provider.getID());
			return null;
		}
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
