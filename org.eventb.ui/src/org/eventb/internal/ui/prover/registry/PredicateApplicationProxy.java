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

import org.eclipse.swt.graphics.Image;
import org.eventb.ui.prover.IPredicateApplication;

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
		TacticApplicationProxy<IPredicateApplication> implements
		IPredicateApplication {

	public PredicateApplicationProxy(TacticProviderInfo provider,
			IPredicateApplication client) {
		super(provider, client);
	}

	// FIXME cleanup duplicated code.

	@Override
	public Image getIcon() {
		final Image clientResult = getIconFromClient();
		if (clientResult != null) {
			return clientResult;
		}
		return provider.getIcon();
	}

	private Image getIconFromClient() {
		try {
			return client.getIcon();
		} catch (Throwable exc) {
			log(exc, "when calling getIcon() for " + provider.getID());
			return null;
		}
	}

	@Override
	public String getTooltip() {
		final String clientResult = getTooltipFromClient();
		if (clientResult != null) {
			return clientResult;
		}
		return provider.getTooltip();
	}

	private String getTooltipFromClient() {
		try {
			return client.getTooltip();
		} catch (Throwable exc) {
			log(exc, "when calling getTooltip() for " + provider.getID());
			return null;
		}
	}

}
