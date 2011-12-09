/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.ui.autocompletion;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IEventBRoot;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.EventBUtils;
import org.eventb.internal.ui.autocompletion.ContentProposalFactory;
import org.rodinp.core.location.IAttributeLocation;

/**
 * A factory to create both content providers and content adapters.
 * 
 * @author Thomas Muller
 * @since 2.4
 */
public class EventBContentProposalFactory {

	/**
	 * Returns a content proposal provider for a given attribute location and a
	 * given root element.
	 * 
	 * @param location
	 *            the location of the attribute to get content proposal for
	 * @param root
	 *            the root to traverse for content proposal
	 * @return an Event-B content proposal provider
	 */
	public static IEventBContentProposalProvider getProposalProvider(
			IAttributeLocation location, IEventBRoot root) {
		return ContentProposalFactory.getProposalProvider(location,
				EventBUtils.getFormulaFactory(root));
	}

	/**
	 * Returns a content proposal adapter for a given styledText and an
	 * associated proposal provider.
	 * 
	 * @param text
	 *            the styled text that receive the content proposals
	 * @param provider
	 *            the provider of the content proposals
	 * @return an Event-B content proposal adapter
	 */
	public static IEventBContentProposalAdapter getContentProposalAdapter(
			StyledText text, IContentProposalProvider provider) {
		return ContentProposalFactory.makeContentProposal(text, provider);
	}

	/**
	 * Returns a content proposal adapter for a given text and an associated
	 * user support.
	 * 
	 * @param text
	 *            the text widget that receive the content proposals
	 * @param us
	 *            the current user support
	 * @return an Event-B content proposal adapter
	 */
	public static IEventBContentProposalAdapter getContentProposalAdapter(
			Text text, IUserSupport us) {
		return ContentProposalFactory.makeContentProposal(text, us);
	}

}
