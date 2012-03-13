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

import org.eclipse.swt.graphics.Image;
import org.eventb.ui.prover.IPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Internal implementation of {@link IPredicateApplication} that encapsulates
 * the implementations provided by clients. The purpose of this class is to act
 * as a firewall with respect to clients. Moreover, it also implements the logic
 * of reverting to the tactic provider when the application returns
 * <code>null</code>.
 * 
 * @author Laurent Voisin
 */
public class PredicateApplicationProxy extends
		TacticApplicationProxy<IPredicateApplication> {

	/**
	 * Factory for creating predicate application proxys.
	 */
	public static class PredicateApplicationFactory extends
			TacticApplicationFactory<PredicateApplicationProxy> {

		@Override
		public PredicateApplicationProxy create(TacticProviderInfo provider,
				ITacticApplication application) {
			if (application instanceof IPredicateApplication) {
				return new PredicateApplicationProxy(provider,
						(IPredicateApplication) application);
			}
			return null;
		}

	}

	protected PredicateApplicationProxy(TacticProviderInfo provider,
			IPredicateApplication client) {
		super(provider, client);
	}

	public Image getIcon() {
		return new SafeCall<Image>() {
			@Override
			public void run() throws Exception {
				result = client.getIcon();
			}

			@Override
			protected Image defaultValue() {
				return provider.getIcon();
			}
		}.call();
	}

	public String getTooltip() {
		return new SafeCall<String>() {
			@Override
			public void run() throws Exception {
				result = client.getTooltip();
			}

			@Override
			protected String defaultValue() {
				return provider.getTooltip();
			}
		}.call();
	}

}
