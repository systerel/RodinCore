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

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.pm.IUserSupport;
import org.rodinp.core.location.IAttributeLocation;

public class ContentProposalFactory {

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for StyledText control.
	 * 
	 * @param factory
	 *            the formula factory used by the underlying root
	 */
	public static EventBContentProposalAdapter makeContentProposal(
			IAttributeLocation location, StyledText text, FormulaFactory factory) {
		final ProposalProvider provider = getProposalProvider(location, factory);
		return makeContentProposal(text, provider);
	}

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for StyledText control.
	 * 
	 * @param text
	 *            a styled text
	 * @param provider
	 *            a content proposal provider
	 * @return a proposal adapter
	 */
	public static EventBContentProposalAdapter makeContentProposal(
			StyledText text, IContentProposalProvider provider) {
		return new EventBContentProposalAdapter(text,
				new StyledTextContentAdapter(), provider);
	}

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for StyledText control.
	 * 
	 * @param us
	 *            the user support
	 */
	public static EventBContentProposalAdapter makeContentProposal(
			StyledText text, IUserSupport us) {
		return new EventBContentProposalAdapter(text,
				new StyledTextContentAdapter(), getProposalProvider(us));
	}

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for a Text control.
	 * 
	 * @param factory
	 *            the formula factory used by the underlying root
	 */
	public static EventBContentProposalAdapter makeContentProposal(
			IAttributeLocation location, Text text, FormulaFactory factory) {
		return new EventBContentProposalAdapter(text, new TextContentAdapter(),
				getProposalProvider(location, factory));
	}

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for a Text control.
	 * 
	 * @param us
	 *            the user support
	 */
	public static EventBContentProposalAdapter makeContentProposal(Text text,
			IUserSupport us) {
		return new EventBContentProposalAdapter(text, new TextContentAdapter(),
				getProposalProvider(us));
	}

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for a Text control.
	 */
	public static EventBContentProposalAdapter makeContentProposal(
			IContentProposalProvider provider, Text text) {
		return new EventBContentProposalAdapter(text,
				new TextContentAdapter(), provider);
	}

	public static ProposalProvider getProposalProvider(
			IAttributeLocation location, FormulaFactory factory) {
		return new ProposalProvider(location, factory);
	}

	public static IContentProposalProvider getProposalProvider(
			IUserSupport us) {
		return new ProofProposalProvider(us);
	}

}
