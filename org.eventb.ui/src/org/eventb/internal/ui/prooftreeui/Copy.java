/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others. 
 *  
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - Initial API and implementation
 *     Systerel - Refactored defining AbstractProofTreeAction
 ******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.IAction;

public class Copy extends AbstractProofTreeAction {

	public Copy() {
		super(false);
	}

	@Override
	public void run(IAction action) {
		ProofTreeUI.buffer = getSelection().copyProofSkeleton();
		if (ProofTreeUIUtils.DEBUG)
			ProofTreeUIUtils.debug("Copied : " + ProofTreeUI.buffer);
	}

}
