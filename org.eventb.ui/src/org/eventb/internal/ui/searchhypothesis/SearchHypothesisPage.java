/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
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

import java.util.ArrayList;
import java.util.Collection;

import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.prover.HypothesisPage;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Search Hypothesis Page.
 */
public class SearchHypothesisPage extends HypothesisPage implements
		ISearchHypothesisPage {

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this Hypothesis Page.
	 */
	public SearchHypothesisPage(IUserSupport userSupport) {
		super(userSupport, IProofStateDelta.F_NODE | IProofStateDelta.F_SEARCH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisPage#getHypotheses(org.eventb.core.pm.IProofState)
	 */
	@Override
	public Collection<Predicate> getHypotheses(IProofState ps) {
		Collection<Predicate> searched = new ArrayList<Predicate>();
		if (ps != null) {
			searched = ps.getSearched();
		}
		return searched;
	}

}
