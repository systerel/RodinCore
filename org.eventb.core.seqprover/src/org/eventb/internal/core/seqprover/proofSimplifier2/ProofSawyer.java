/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.core.seqprover.proofSimplifier2;

import static org.eventb.internal.core.seqprover.Util.getNullProofMonitor;
import static org.eventb.internal.core.seqprover.proofSimplifier2.ProofSawyer.CancelException.checkCancel;

import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofTree;

/**
 * @author Nicolas Beauger
 * 
 */
public class ProofSawyer {

	public static class CancelException extends Exception {

		private static final long serialVersionUID = -7008468725701730153L;

		private static final CancelException SINGLETON = new CancelException();
		
		private CancelException() {
			// singleton
		}
		
		public static void checkCancel(IProofMonitor monitor)
				throws CancelException {
			if (monitor.isCanceled()) {
				throw SINGLETON;
			}
		}
		
	}
	
	/**
	 * Simplify the proof tree by removing:
	 * <ul>
	 * <li>useless hyp actions
	 * <li>useless proof steps
	 * </ul>
	 * 
	 * @param monitor
	 *            a monitor to report progress and support cancellation
	 * @return the simplified proof tree, or <code>null</code> if no
	 *         simplification occurred
	 * @throws CancelException
	 *             when the monitor is cancelled
	 */
	public IProofTree simplify(IProofTree proofTree, IProofMonitor monitor)
			throws CancelException {
		if (!proofTree.isClosed()) {
			throw new IllegalArgumentException(
					"Cannot simplify a non closed proof tree");
		}
		if (monitor == null) {
			monitor = getNullProofMonitor();
		}

		final SawyerTree sawyerTree = new SawyerTree(proofTree.getRoot());
		checkCancel(monitor);
		sawyerTree.init(monitor);
		sawyerTree.saw(monitor);
		return sawyerTree.toProofTree(monitor);
	}
}
