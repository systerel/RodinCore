/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eventb.internal.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.seqprover.IProofMonitor;

/**
 * Bridge between an <code>IProgressMonitor</code> and an
 * <code>IProofMonitor</code>.
 * 
 * @author Laurent Voisin
 */
public class ProofMonitor implements IProofMonitor {

	IProgressMonitor pm;
	
	public ProofMonitor(IProgressMonitor pm) {
		this.pm = pm;
	}
	
	public boolean isCanceled() {
		return pm != null && pm.isCanceled();
	}

	public void setCanceled(boolean value) {
		assert false : "Should never be called";
	}

	public void setTask(String name) {
		if (pm != null) {
			pm.subTask(name);
		}
	}

}
