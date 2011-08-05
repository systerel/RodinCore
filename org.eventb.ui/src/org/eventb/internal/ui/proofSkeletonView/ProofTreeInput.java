/*******************************************************************************
 * Copyright (c) 2008-2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/

package org.eventb.internal.ui.proofSkeletonView;

import org.eventb.core.seqprover.IProofTree;

/**
 * @author Nicolas Beauger
 *
 */
public class ProofTreeInput implements IViewerInput {

	private final IProofTree proofTree;
	
	private final String tooltip;
	
	public ProofTreeInput(IProofTree proofTree, String tooltip) {
		this.proofTree = proofTree;
		this.tooltip = tooltip;
	}


	@Override
	public Object[] getElements() {
		return new Object[] {proofTree.getRoot()};
	}


	@Override
	public String getTitleTooltip() {
		return tooltip;
	}

}
