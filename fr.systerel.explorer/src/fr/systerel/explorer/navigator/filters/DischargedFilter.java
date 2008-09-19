/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.navigator.filters;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofTree;
import org.rodinp.core.RodinDBException;

/**
 * Implements filtering of discharged proof obligations.
 */
public class DischargedFilter extends ViewerFilter {

	private static final IUserSupportManager USM = EventBPlugin
	.getUserSupportManager();

	// Proof states
	static int UNKNOWN = -1;
	static int DISCHARGED = 0;
	static int PENDING_BROKEN = 2;
	static int PENDING = 3;
	static int REVIEWED_BROKEN = 4;
	static int REVIEWED = 5;
	static int DISCHARGED_BROKEN = 6;
	
	public static boolean active = false;

	/**
	 * 
	 */
	public DischargedFilter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement,
			Object element) {
		
		if (element instanceof IPSStatus) {
			if (! active) {
				// No filtering on discharged POs
				return true;
			}

			try {
				return getStatus((IPSStatus) element) != DISCHARGED;
			} catch (RodinDBException e) {
				// Ignore case where database is not up to date
			}
		}
		return true;
	}

	int getStatus(IPSStatus status) throws RodinDBException {
		// Try to synchronize with the proof tree in memory
		Collection<IUserSupport> userSupports = USM.getUserSupports();
		final boolean proofBroken = status.isBroken();
		for (IUserSupport userSupport : userSupports) {
			IProofState [] proofStates = userSupport.getPOs();
			for (IProofState proofState : proofStates) {
				if (proofState.getPSStatus().equals(status)) {
					IProofTree tree = proofState.getProofTree();
					if (!proofState.isDirty() || tree == null)
						break;

					int confidence = tree.getConfidence();

					if (confidence == IConfidence.PENDING) {
						if (false && proofBroken)
							return PENDING_BROKEN;
						else
							return PENDING;
					}
					if (confidence <= IConfidence.REVIEWED_MAX) {
						if (false && proofBroken)
							return REVIEWED_BROKEN;
						else
							return REVIEWED;
					}
					if (confidence <= IConfidence.DISCHARGED_MAX) {
						if (false && proofBroken)
							return DISCHARGED_BROKEN;
						else
							return DISCHARGED;
					}
					return UNKNOWN; // Should not happen
				}
			}
		}

		// Otherwise, setting the label accordingly.

		final IPRProof prProof = status.getProof();

		// TODO : confidence now expresses unattempted as well
		if ((!prProof.exists()) || (prProof.getConfidence() <= IConfidence.UNATTEMPTED))
			return PENDING;

		int confidence = prProof.getConfidence();
		if (proofBroken) {

			if (confidence == IConfidence.PENDING)
				return PENDING_BROKEN;
			if (confidence <= IConfidence.REVIEWED_MAX)
				return REVIEWED_BROKEN;
			if (confidence <= IConfidence.DISCHARGED_MAX)
				return DISCHARGED_BROKEN;

		} else {

			if (confidence == IConfidence.PENDING)
				return PENDING;
			if (confidence <= IConfidence.REVIEWED_MAX)
				return REVIEWED;
			if (confidence <= IConfidence.DISCHARGED_MAX)
				return DISCHARGED;

		}

		return UNKNOWN;
	}


}
