/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
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

	private ISearchHypothesisPage mainPage;

	public SearchHypothesis() {
		super(Messages.searchedHypothesis_defaultMessage);
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		// Try to get a Search Hypothesis Page.
		final Object obj = part.getAdapter(ISearchHypothesisPage.class);
		if (obj instanceof ISearchHypothesisPage) {
			final ISearchHypothesisPage page = (ISearchHypothesisPage) obj;
			initPage(page);
			page.createControl(getPageBook());
			mainPage = page;
			return new PageRec(part, page);
		}
		// There is no Search Hypotheses Page
		return null;
	}

	public void setSearchedHyp(String pattern) {
		if (mainPage != null) {
			mainPage.setPattern(pattern);
		}
	}

}