/*******************************************************************************
 * Copyright (c) 2010, 2014 Systerel and others.
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
import java.util.List;
import java.util.Set;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eventb.core.ast.FormulaFactory;

public abstract class AbstractProposalProvider implements
		IContentProposalProvider {

	private static class Proposal implements IContentProposal {

		private final String content;
		private final int cursorPosition;
		private final String label;

		public Proposal(String content, int cursorPosition, String label) {
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

	private final FormulaFactory factory;

	public AbstractProposalProvider(FormulaFactory factory) {
		this.factory = factory;
	}

	protected abstract IContentProposal[] makeAllProposals(String contents,
			int position, String prefix);

	@Override
	public final IContentProposal[] getProposals(String contents, int position) {
		final String prefix = getPrefix(contents, position);
		return makeAllProposals(contents, position, prefix);
	}

	protected String getPrefix(String contents, int position) {
		final PrefixComputer pc = new PrefixComputer(contents, position,
				factory);
		return pc.getPrefix();
	}

	protected static IContentProposal[] makeProposals(String contents,
			int position, String prefix, Set<String> completions) {
		final List<String> filteredSorted = filterAndSort(completions, prefix);
		final IContentProposal[] proposals = new IContentProposal[filteredSorted
				.size()];
		for (int i = 0; i < proposals.length; i++) {
			proposals[i] = makeProposal(contents, position, prefix,
					filteredSorted.get(i));
		}
		return proposals;
	}

	private static IContentProposal makeProposal(String contents, int position,
			String prefix, String completion) {
		final String propContents = completion.substring(prefix.length());
		final int cursorPos = position + propContents.length();
		return new Proposal(propContents, cursorPos, completion);
	}

	private static List<String> filterAndSort(Set<String> completions,
			String prefix) {
		final List<String> filteredNames = filterPrefix(completions, prefix);
		Collections.sort(filteredNames);
		return filteredNames;
	}

	private static List<String> filterPrefix(Set<String> names, String prefix) {
		if (prefix.length() == 0) {
			return new ArrayList<String>(names);
		}
		final List<String> filteredNames = new ArrayList<String>();
		for(String name: names) {
			if (name.startsWith(prefix)) {
				filteredNames.add(name);
			}
		}
		return filteredNames;
	}

}
