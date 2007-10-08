/*******************************************************************************
 * Copyright (c) 2006-2007 ETH Zurich.
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

import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.prover.HypothesisComposite;
import org.eventb.internal.ui.prover.HypothesisPage;
import org.eventb.internal.ui.prover.ProverUI;

/**
 * @author htson
 *         <p>
 *         This class extends the default implementation {@link HypothesisPage}
 *         to implement a Search Hypothesis 'page'. This implementation just use
 *         a {@link SearchHypothesisComposite} for displaying the cached
 *         hypotheses.
 */
public class SearchHypothesisPage extends HypothesisPage implements
		ISearchHypothesisPage {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this searched hypothesis page.
	 *            This must not be <code>null</code>.
	 * @param proverUI
	 *            the main prover editor ({@link ProverUI}). This must not be
	 *            <code>null</null>.
	 */
	public SearchHypothesisPage(IUserSupport userSupport, ProverUI proverUI) {
		super(userSupport, proverUI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisPage#getHypypothesisCompsite()
	 */
	@Override
	public HypothesisComposite getHypypothesisCompsite() {
		return new SearchHypothesisComposite(userSupport, proverUI);
	}

}
