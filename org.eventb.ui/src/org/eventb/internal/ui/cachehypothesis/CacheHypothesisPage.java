/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.cachehypothesis;

import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.prover.HypothesisComposite;
import org.eventb.internal.ui.prover.HypothesisPage;
import org.eventb.internal.ui.prover.ProverUI;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Cache Hypothesis 'page'.
 */
public class CacheHypothesisPage extends HypothesisPage implements
		ICacheHypothesisPage {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this Hypothesis Page.
	 */
	public CacheHypothesisPage(IUserSupport userSupport,
			ProverUI proverUI) {
		super(userSupport, proverUI);
	}

	@Override
	public HypothesisComposite getHypypothesisCompsite() {
		return new CacheHypothesisComposite(userSupport, proverUI);
	}
	
}
