/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.autocompletion;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.location.IAttributeLocation;

public class WizardProposalProvider extends ProposalProvider {

	private final List<String> identifiers = new ArrayList<String>();

	public WizardProposalProvider(IAttributeLocation location,
			FormulaFactory factory) {
		super(location, factory);
	}

	public void setIdentifiers(List<String> identifiers) {
		this.identifiers.clear();
		this.identifiers.addAll(identifiers);
	}

	@Override
	protected IContentProposal[] makeAllProposals(String contents, int position,
			String prefix) {
		final IContentProposal[] globalProposal = super.makeAllProposals(contents,
				position, prefix);
		final IContentProposal[] localProposal = getLocalProposal(contents,
				position, prefix);

		final IContentProposal[] result = new IContentProposal[localProposal.length
				+ globalProposal.length];

		System.arraycopy(localProposal, 0, result, 0, localProposal.length);
		System.arraycopy(globalProposal, 0, result, localProposal.length,
				globalProposal.length);
		return result;
	}

	private IContentProposal[] getLocalProposal(String contents, int position,
			String prefix) {
		final Set<String> completions = new HashSet<String>(identifiers);
		return makeProposals(contents, position, prefix, completions);
	}
}
