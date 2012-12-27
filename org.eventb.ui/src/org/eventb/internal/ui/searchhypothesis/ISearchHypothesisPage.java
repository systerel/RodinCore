/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added dropdown list, buttons and search field
 *******************************************************************************/
package org.eventb.internal.ui.searchhypothesis;

import org.eventb.internal.ui.prover.IHypothesisPage;

/**
 * Common protocol for a page displaying searched hypotheses.
 *
 * @author htson
 */
public interface ISearchHypothesisPage extends IHypothesisPage {

	/**
	 * Sets the string pattern to use for searching relevant hypotheses.
	 */
	public void setPattern(String input);

}
