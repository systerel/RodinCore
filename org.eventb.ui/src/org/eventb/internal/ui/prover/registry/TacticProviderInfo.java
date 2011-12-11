/*******************************************************************************
 * Copyright (c) 2005, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *******************************************************************************/
package org.eventb.internal.ui.prover.registry;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.prover.ITacticApplication;
import org.eventb.ui.prover.ITacticProvider;

public class TacticProviderInfo extends TacticUIInfo {

	private final ITacticProvider tacticProvider;

	public TacticProviderInfo(String id, Target target, ImageDescriptor iconDesc,
			boolean interrupt, String tooltip, int priority, String name,
			String dropdown, String toolbar, boolean skipPostTactic,
			ITacticProvider tacticProvider) {
		super(id, target, iconDesc, interrupt, tooltip, priority, name, dropdown,
				toolbar, skipPostTactic);
		this.tacticProvider = tacticProvider;
	}

	public List<ITacticApplication> getApplicationsToGoal(IUserSupport us) {
		return getApplications(us, null, null);
	}

	public List<ITacticApplication> getApplicationsToHypothesis(
			IUserSupport us, Predicate hyp) {
		return getApplications(us, hyp, null);
	}

	private List<ITacticApplication> getApplications(IUserSupport us,
			Predicate hyp, String globalInput) {

		final IProofState currentPO = us.getCurrentPO();
		if (currentPO == null) {
			return Collections.emptyList();
		}
		final IProofTreeNode node = currentPO.getCurrentNode();
		if (node == null) {
			return Collections.emptyList();
		}

		return tacticProvider.getPossibleApplications(node, hyp, globalInput);
	}

	@Override
	public Object getGlobalApplication(IUserSupport us, String globalInput) {
		final List<ITacticApplication> applications = getApplications(us, null,
				globalInput);
		// TODO document protocol in extension point
		switch (applications.size()) {
		case 0:
			// not applicable
			return null;
		case 1:
			// sole application
			return applications.get(0);
		default:
			// more than 1 application is ambiguous and forbidden by
			// protocol
			final String message = "could not provide global tactic application for tactic "
					+ getID()
					+ "\nReason: unexpected number of applications: "
					+ applications.size();
			UIUtils.log(null, message);
			ProverUIUtils.debug(message);
			return null;
		}
	}

}