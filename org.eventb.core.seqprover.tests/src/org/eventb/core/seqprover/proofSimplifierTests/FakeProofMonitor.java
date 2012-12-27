/*******************************************************************************
 * Copyright (c) 2009, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.core.seqprover.proofSimplifierTests;

import org.eventb.core.seqprover.IProofMonitor;

/**
 * @author "Nicolas Beauger"
 *
 */
public class FakeProofMonitor implements IProofMonitor {

	private final int nbChecksBeforeCancel;
	private int nbCancelCheck;
	
	
	public FakeProofMonitor(int nbChecksBeforeCancel) {
		this.nbChecksBeforeCancel = nbChecksBeforeCancel;
		this.nbCancelCheck = 0;
	}

	public boolean isCanceled() {
		nbCancelCheck++;
		return nbCancelCheck >= nbChecksBeforeCancel;
	}

	public void setCanceled(boolean value) {
		// nothing to do
	}

	public void setTask(String name) {
		// nothing to do
	}

}
