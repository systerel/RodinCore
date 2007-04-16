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

package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * @author htson
 *         <p>
 *         This is the interface for Proof Tree UI pages.
 */
public interface IProofTreeUIPage extends ISelectionProvider, IPageBookViewPage {
	// Only extends IPage and ISelectionProvider
}
