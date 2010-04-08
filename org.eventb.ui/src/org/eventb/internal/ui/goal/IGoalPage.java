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

package org.eventb.internal.ui.goal;

import org.eclipse.ui.part.IPageBookViewPage;
import org.eventb.core.pm.IUserSupportManagerChangedListener;

/**
 * @author htson
 *         <p>
 *         This is the interface for a Goal Page.
 */
public interface IGoalPage extends IPageBookViewPage,
		IUserSupportManagerChangedListener {
	// Direct extension of IHypothesisPage.
}
