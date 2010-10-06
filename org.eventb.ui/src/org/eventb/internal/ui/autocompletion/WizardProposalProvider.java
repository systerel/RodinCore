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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.location.IAttributeLocation;

public class WizardProposalProvider extends ProposalProvider {

	private static final ProposalComparator COMPARATOR = new ProposalComparator();
	
	private final FormulaFactory factory;
	
	private final List<String> identifiers;

	public WizardProposalProvider(IAttributeLocation location,
			FormulaFactory factory) {
		super(location, factory);
		this.factory = factory;
		identifiers = new ArrayList<String>();
	}

	public void setIdentifiers(List<String> identifiers) {
		this.identifiers.clear();
		this.identifiers.addAll(identifiers);
	}

	@Override
	public IContentProposal[] getProposals(String contents, int position) {
		final IContentProposal[] globalProposal = super.getProposals(contents,
				position);
		final List<IContentProposal> localProposal = getLocalProposal(contents,
				position);

		final IContentProposal[] result = new IContentProposal[localProposal
				.size() + globalProposal.length];
		for (int i = 0; i < localProposal.size(); i++) {
			result[i] = localProposal.get(i);
		}
		for (int i = 0; i < globalProposal.length; i++) {
			result[i + localProposal.size()] = globalProposal[i];
		}
		return result;
	}

	private List<IContentProposal> getLocalProposal(String contents,
			int position) {
		final List<IContentProposal> localProposal = new ArrayList<IContentProposal>();
		final PrefixComputer pc = new PrefixComputer(contents, position,
				factory);
		final String prefix = pc.getPrefix();
		for (String ident : identifiers) {
			if (ident.startsWith(prefix)) {
				localProposal.add(makeProposal(contents, position, prefix,
						ident));
			}
		}
		Collections.sort(localProposal, COMPARATOR);
		return localProposal;
	}

	static class ProposalComparator implements Comparator<IContentProposal> {

		@Override
		public int compare(IContentProposal o1, IContentProposal o2) {
			return o1.getLabel().compareTo(o2.getLabel());
		}
	}
}
