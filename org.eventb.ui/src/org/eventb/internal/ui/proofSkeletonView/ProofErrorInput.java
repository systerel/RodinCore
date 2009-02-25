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

import org.eventb.core.IPRProof;
import org.eventb.internal.ui.utils.Messages;

public class ProofErrorInput implements IPrfSklInput {
	private final IPRProof proof;

	public ProofErrorInput(IPRProof proof) {
		this.proof = proof;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(Messages.proofskeleton_cantdisplayproof);
		sb.append(proof.getElementName());
		return sb.toString();
	}
}