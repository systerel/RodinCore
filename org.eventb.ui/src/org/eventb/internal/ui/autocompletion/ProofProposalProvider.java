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

import java.util.Set;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;

public class ProofProposalProvider extends AbstractProposalProvider {

	private final IUserSupport us;

	private static final IContentProposal[] EMPTY = new IContentProposal[0];

	public ProofProposalProvider(IUserSupport us) {
		super(us.getFormulaFactory());
		this.us = us;
	}

	@Override
	protected IContentProposal[] makeAllProposals(String contents,
			int position, String prefix) {
		final IProofState po = us.getCurrentPO();
		if (po == null)
			return EMPTY;
		final IProofTreeNode node = po.getCurrentNode();
		if (node == null)
			return EMPTY;
		final Set<String> completions = node.getSequent().typeEnvironment()
				.getNames();
		return makeProposals(contents, position, prefix, completions);
	}

}
