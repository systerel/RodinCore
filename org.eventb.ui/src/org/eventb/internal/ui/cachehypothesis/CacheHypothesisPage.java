/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.cachehypothesis;

import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.prover.HypothesisComposite;
import org.eventb.internal.ui.prover.HypothesisPage;
import org.eventb.internal.ui.prover.ProverUI;

/**
 * @author htson
 *         <p>
 *         This class extends the default implementation {@link HypothesisPage}
 *         to implement a Cache Hypothesis 'page'. This implementation just use
 *         a {@link CacheHypothesisComposite} for displaying the cached
 *         hypotheses.
 */
public class CacheHypothesisPage extends HypothesisPage implements
		ICacheHypothesisPage {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this cached hypothesis page.
	 *            This must not be <code>null</code>.
	 * @param proverUI
	 *            the main prover editor ({@link ProverUI}). This must not be
	 *            <code>null</null>.
	 */
	public CacheHypothesisPage(IUserSupport userSupport,
			ProverUI proverUI) {
		super(userSupport, proverUI);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisPage#getHypypothesisCompsite()
	 */
	@Override
	public HypothesisComposite getHypypothesisCompsite() {
		return new CacheHypothesisComposite(userSupport, proverUI);
	}
	
}
