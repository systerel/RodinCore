/*******************************************************************************
 * Copyright (c) 2005-2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.searchhypothesis;

import org.eclipse.ui.IWorkbenchPart;
import org.eventb.internal.ui.prover.ProverContentOutline;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 *         Implementation of the Search Hypothesis View.
 */
public class SearchHypothesis extends ProverContentOutline {

	/**
	 * The identifier of the Search Hypothesis View (value
	 * <code>"org.eventb.ui.views.SearchHypothesis"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID
			+ ".views.SearchHypothesis"; // $NON-NLS-1$

	/**
	 * Constructor.
	 * <p>
	 * Create a prover content outline with the default message.
	 */
	public SearchHypothesis() {
		super(Messages.searchedHypothesis_defaultMessage);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.part.PageBookView#doCreatePage(org.eclipse.ui.IWorkbenchPart)
	 */
	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// Try to get a Search Hypothesis Page.
		Object obj = part.getAdapter(ISearchHypothesisPage.class);
		if (obj instanceof ISearchHypothesisPage) {
			ISearchHypothesisPage page = (ISearchHypothesisPage) obj;
			initPage(page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		// There is no Search Hypotheses Page
		return null;
	}

}