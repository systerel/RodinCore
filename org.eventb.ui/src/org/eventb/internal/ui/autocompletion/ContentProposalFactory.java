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

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;
import org.rodinp.core.location.IAttributeLocation;

public class ContentProposalFactory {

	private static final String CTRL_SPACE = "Ctrl+Space";
	private static final KeyStroke keyStroke = getKeyStroke();

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for the field.
	 */
	public static EventBContentProposalAdapter getContentProposal(
			IAttributeLocation location, StyledText text) {
		final ProposalProvider proposalProvider = new ProposalProvider(location);
		return new EventBContentProposalAdapter(text,
				new StyledTextContentAdapter(), proposalProvider, keyStroke,
				null);
	}

	/**
	 * Construct a content proposal adapter that can assist the user with
	 * choosing content for the field.
	 */
	public static EventBContentProposalAdapter getContentProposal(
			IAttributeLocation location, Text text) {

		final IContentProposalProvider propProvider = new ProposalProvider(
				location);
		return new EventBContentProposalAdapter(text, new TextContentAdapter(),
				propProvider, keyStroke, null);
	}

	private static KeyStroke getKeyStroke() {
		try {
			return KeyStroke.getInstance(CTRL_SPACE);
		} catch (ParseException e) {
			return null;
		}
	}

}
