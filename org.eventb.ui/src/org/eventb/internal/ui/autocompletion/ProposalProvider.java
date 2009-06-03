/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.autocompletion;

import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.FormulaFactory;
import org.rodinp.core.location.IAttributeLocation;

public class ProposalProvider implements IContentProposalProvider {

	// TODO see if we can factorize a unique FormulaFactory for the plugin
	private static final FormulaFactory ff = FormulaFactory.getDefault();

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

		public String getContent() {
			return content;
		}

		public int getCursorPosition() {
			return cursorPosition;
		}

		public String getDescription() {
			return null;
		}

		public String getLabel() {
			return label;
		}

	}

	private final IAttributeLocation location;

	public ProposalProvider(IAttributeLocation location) {
		this.location = location;
	}

	private String getPrefix(String text, int position) {
		if (position == 0)
			return "";
		int i;
		for (i = position - 1; i >= 0; i--) {
			final String candidate = text.substring(i, position);
			if (!ff.isValidIdentifierName(candidate)) {
				return text.substring(i + 1, position);
			}
		}
		return text.substring(0, position);
	}

	public IContentProposal[] getProposals(String contents, int position) {
		final String prefix = getPrefix(contents, position);
		final List<String> completions = EventBPlugin.getCompletions(location,
				prefix);
		return makeProposals(contents, position, prefix, completions);
	}

	private IContentProposal[] makeProposals(String contents, int position,
			String prefix, List<String> completions) {
		final IContentProposal[] proposals = new IContentProposal[completions
				.size()];
		for (int i = 0; i < proposals.length; i++) {
			proposals[i] = makeProposal(contents, position, prefix, completions
					.get(i));
		}
		return proposals;
	}

	private IContentProposal makeProposal(String contents, int position,
			String prefix, String completion) {
		final String propContents = completion.substring(prefix.length());
		final int cursorPos = position + propContents.length();
		return new Proposal(propContents, cursorPos, completion);
	}
}
