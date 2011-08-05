/*******************************************************************************
 * Copyright (c) 2009, 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.autocompletion;

import java.util.Set;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.location.IAttributeLocation;

public class ProposalProvider extends AbstractProposalProvider {

	private static final IContentProposal[] NO_PROPOSALS = new IContentProposal[0];

	private IAttributeLocation location;

	public ProposalProvider(IAttributeLocation location, FormulaFactory factory) {
		super(factory);
		this.location = location;
	}

	@Override
	protected IContentProposal[] makeAllProposals(String contents,
			int position, String prefix) {
		if (location == null) {
			UIUtils.log(null, "auto completion location is not initialized !");
			return NO_PROPOSALS;
		}
		// TODO launch a job that waits up-to-date completions
		// and then updates proposals

		final Set<String> completions = EventBPlugin.getProposals(location,
				false);
		return makeProposals(contents, position, prefix, completions);
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(IAttributeLocation location) {
		this.location = location;
	}
}
