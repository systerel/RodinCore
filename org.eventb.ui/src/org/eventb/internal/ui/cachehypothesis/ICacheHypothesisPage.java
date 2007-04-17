/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
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

import java.util.Set;

import org.eclipse.ui.part.IPageBookViewPage;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;

/**
 * @author htson
 *         <p>
 *         This is the interface for the Cache Hypothesis Page.
 */
public interface ICacheHypothesisPage extends IPageBookViewPage {

	IUserSupport getUserSupport();
	// Only extends IPage

	Set<Predicate> getSelectedHyps();
}
