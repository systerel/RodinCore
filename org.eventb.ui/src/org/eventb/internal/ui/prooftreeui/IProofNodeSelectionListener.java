/*******************************************************************************
 * Copyright (c) 2016 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eventb.core.seqprover.IProofTreeNode;

/**
 * Common protocol for classes that react when the selected proof node changes.
 * <p>
 * Such classes shall register with the {@link ProofNodeSelectionService}.
 * </p>
 * 
 * @author beauger
 * @see ProofNodeSelectionService
 */
public interface IProofNodeSelectionListener {

	/**
	 * Notification that the current proof node has changed.
	 * 
	 * @param newNode
	 *            the new proof node
	 */
	void nodeChanged(IProofTreeNode newNode);

}
