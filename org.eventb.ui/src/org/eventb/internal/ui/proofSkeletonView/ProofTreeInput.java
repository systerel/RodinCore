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
	final IProofTree proofTree;
	
	public ProofTreeInput(IProofTree proofTree) {
		this.proofTree = proofTree;
	}


	public Object[] getElements() {
		return new Object[] {proofTree.getRoot()};
	}

}
