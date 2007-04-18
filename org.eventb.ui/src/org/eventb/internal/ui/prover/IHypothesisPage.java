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

package org.eventb.internal.ui.prover;

import java.util.Set;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;

/**
 * @author htson
 *         <p>
 *         This is the interface for the Proof Control pages.
 */
public interface IHypothesisPage extends IPageBookViewPage, SelectionListener {

	IUserSupport getUserSupport();

	Set<Predicate> getSelectedHyps();
}
