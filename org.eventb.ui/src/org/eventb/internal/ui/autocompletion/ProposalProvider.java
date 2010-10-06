/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.location.IAttributeLocation;

public class ProposalProvider implements IContentProposalProvider {

	private static class Proposal implements IContentProposal {

		private final String content;
		private final int cursorPosition;
		private final String label;

		public Proposal(String content, int cursorPosition,
				String label) {
			this.content = content;
			this.cursorPosition = cursorPosition;
			this.label = label;
		}

		@Override
		public String getContent() {
			return content;
		}

		@Override
		public int getCursorPosition() {
			return cursorPosition;
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public String getLabel() {
			return label;
		}

	}

	private final IAttributeLocation location;
	private final FormulaFactory factory;

	public ProposalProvider(IAttributeLocation location, FormulaFactory factory) {
		this.location = location;
		this.factory = factory;
	}

	@Override
	public final IContentProposal[] getProposals(String contents, int position) {
		final PrefixComputer pc = new PrefixComputer(contents, position, factory);
		final String prefix = pc.getPrefix();
		// TODO launch a job that waits up-to-date completions
		// and then updates proposals 
		
		return makeAllProposals(contents, position, prefix);
	}

	protected IContentProposal[] makeAllProposals(String contents, int position,
			String prefix) {
		final Set<String> completions = EventBPlugin.getProposals(
				location, false);
		return makeProposals(contents, position, prefix, completions);
	}

	protected static IContentProposal[] makeProposals(String contents, int position,
			String prefix, Set<String> completions) {
		final List<String> filteredSorted = filterAndSort(completions, prefix);
		final IContentProposal[] proposals = new IContentProposal[filteredSorted
				.size()];
		for (int i = 0; i < proposals.length; i++) {
			proposals[i] = makeProposal(contents, position, prefix, filteredSorted
					.get(i));
		}
		return proposals;
	}

	private static IContentProposal makeProposal(String contents, int position,
			String prefix, String completion) {
		final String propContents = completion.substring(prefix.length());
		final int cursorPos = position + propContents.length();
		return new Proposal(propContents, cursorPos, completion);
	}

	private static List<String> filterAndSort(Set<String> completions, String prefix) {
		filterPrefix(completions, prefix);
		final List<String> sortedNames = new ArrayList<String>(completions);
		Collections.sort(sortedNames);
		return sortedNames;
	}
	
	private static void filterPrefix(Set<String> names, String prefix) {
		if (prefix.length() == 0) {
			return;
		}
		final Iterator<String> iter = names.iterator();
		while(iter.hasNext()) {
			final String name = iter.next();
			if (!name.startsWith(prefix)) {
				iter.remove();
			}
		}
	}
}
