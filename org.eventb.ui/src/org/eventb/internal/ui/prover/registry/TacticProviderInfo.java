/*******************************************************************************
 * Copyright (c) 2005, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *     Systerel - wrap client contributions within a proxy firewall
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import static java.util.Collections.emptyList;
import static org.eventb.internal.ui.UIUtils.log;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.prover.registry.TacticApplicationProxy.TacticApplicationFactory;
import org.eventb.ui.prover.IPositionApplication;
import org.eventb.ui.prover.IPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

public class TacticProviderInfo extends TacticUIInfo {

	private final ITacticProvider tacticProvider;

	public TacticProviderInfo(String id, Target target,
			ImageDescriptor iconDesc, boolean interrupt, String tooltip,
			int priority, String name, String dropdown, String toolbar,
			boolean skipPostTactic, ITacticProvider tacticProvider) {
		super(id, target, iconDesc, interrupt, tooltip, priority, name,
				dropdown, toolbar, skipPostTactic);
		this.tacticProvider = tacticProvider;
	}

	public <T extends TacticApplicationProxy<?>> List<T> getLocalApplications(
			IUserSupport us, Predicate hyp, TacticApplicationFactory<T> factory) {
		return wrap(factory, getApplicationsFromClient(us, hyp, null));
	}

	@Override
	public TacticApplicationProxy<?> getGlobalApplication(IUserSupport us,
			String globalInput) {
		final List<ITacticApplication> applications = getApplicationsFromClient(
				us, null, globalInput);
		switch (applications.size()) {
		case 0:
			// not applicable
			return null;
		case 1:
			// sole application
			return wrap(applications.get(0));
		default:
			// more than 1 application is ambiguous and forbidden by
			// protocol
			final String message = "could not provide global tactic application for tactic "
					+ getID()
					+ "\nReason: unexpected number of applications: "
					+ applications.size();
			log(null, message);
			ProverUIUtils.debug(message);
			return null;
		}
	}

	private List<ITacticApplication> getApplicationsFromClient(IUserSupport us,
			Predicate hyp, String globalInput) {
		final IProofState currentPO = us.getCurrentPO();
		if (currentPO == null) {
			return emptyList();
		}
		final IProofTreeNode node = currentPO.getCurrentNode();
		if (node == null) {
			return emptyList();
		}

		final List<ITacticApplication> applis;
		try {
			applis = tacticProvider.getPossibleApplications(node, hyp,
					globalInput);
		} catch (Throwable exc) {
			log(exc, "when calling getPossibleApplications() for " + getID());
			return emptyList();
		}
		if (applis == null) {
			log(null, "getPossibleApplications() returned null for " + getID());
			return emptyList();
		}
		return applis;
	}

	private <T extends TacticApplicationProxy<?>> List<T> wrap(
			TacticApplicationFactory<T> factory, List<ITacticApplication> applis) {
		final List<T> result = new ArrayList<T>(applis.size());
		for (final ITacticApplication appli : applis) {
			final T proxy = factory.create(this, appli);
			if (proxy != null) {
				result.add(proxy);
			}
		}
		return result;
	}

	private TacticApplicationProxy<?> wrap(ITacticApplication appli) {
		if (appli instanceof IPredicateApplication) {
			return new PredicateApplicationProxy(this,
					(IPredicateApplication) appli);
		}
		if (appli instanceof IPositionApplication) {
			return new PositionApplicationProxy(this,
					(IPositionApplication) appli);
		}
		log(null, "Null or invalid application returned by " + getID());
		return null;
	}

}