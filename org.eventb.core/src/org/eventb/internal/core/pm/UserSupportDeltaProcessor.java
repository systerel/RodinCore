/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.core.pm;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.pm.IProofState;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class UserSupportDeltaProcessor {
	Set<IProofState> toBeTrashed;

	Set<IProofState> toBeReloaded;

	Set<IProofState> toBeReused;

	Set<IProofState> toBeRebuilt;

	Set<IProofState> toBeDeleted;

	Set<IPSStatus> toBeAdded;

	boolean needRefreshed;

	private UserSupport userSupport;
	
	public UserSupportDeltaProcessor(UserSupport userSupport) {
		this.userSupport = userSupport;
		toBeTrashed = new HashSet<IProofState>();
		toBeReloaded = new HashSet<IProofState>();
		toBeReused = new HashSet<IProofState>();
		toBeRebuilt = new HashSet<IProofState>();
		toBeAdded = new HashSet<IPSStatus>();
		toBeDeleted = new HashSet<IProofState>();
		needRefreshed = false;
	}

	public Set<IProofState> getToBeDeleted() {
		return toBeDeleted;
	}

	public boolean needRefreshed() {
		return needRefreshed;
	}

	public Set<IProofState> getToBeReloaded() {
		return toBeReloaded;
	}

	public Set<IProofState> getToBeReused() {
		return toBeReused;
	}

	public Set<IProofState> getToBeRebuilt() {
		return toBeRebuilt;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("** Delta Processor Status **");
		buffer.append("Need refresh? ");
		buffer.append(needRefreshed);
		buffer.append("\n");
		buffer.append("To be reloaded:");
		buffer.append(toBeReloaded);
		buffer.append("\n");
		buffer.append("To be trashed: ");
		buffer.append(toBeTrashed);
		buffer.append("\n");
		buffer.append("To be rebuilt: ");
		buffer.append(toBeRebuilt);
		buffer.append("\n");
		buffer.append("To be reused: ");
		buffer.append(toBeReused);
		buffer.append("\n");
		buffer.append("To be added: ");
		buffer.append(toBeAdded);
		buffer.append("\n");
		buffer.append("To be deleted: ");
		buffer.append(toBeDeleted);
		buffer.append("\n");
		return buffer.toString();
	}
	
	public void processDelta(IRodinElementDelta elementChangedDelta,
			IProgressMonitor monitor) {
		final IRodinElement element = elementChangedDelta.getElement();
		
		// Root DB
		if (element instanceof IRodinDB) {
			for (IRodinElementDelta d : elementChangedDelta
					.getAffectedChildren()) {
				processDelta(d, monitor);
			}			
		}

		final IPSRoot input = userSupport.getInput();
		if (input != null) {

			// IRodinProject
			if (element instanceof IRodinProject) {
				if (input.getRodinProject().equals(element)) {
					for (IRodinElementDelta d : elementChangedDelta
							.getAffectedChildren()) {
						processDelta(d, monitor);
					}
				}
				return;
			}

			// IRodinFile
			if (element instanceof IRodinFile) {
				if (input.getRodinFile().equals(element)) {
					for (IRodinElementDelta d : elementChangedDelta
							.getAffectedChildren()) {
						processDelta(d, monitor);
					}
				}
				return;
			}

			// IPSRoot
			if (element instanceof IPSRoot) {
				if (input.equals(element)) {
					for (IRodinElementDelta d : elementChangedDelta
							.getAffectedChildren()) {
						processDelta(d, monitor);
					}
					return;
				}
			}
		}
		
		
		
		// IPSStatus has been changed
		if (element instanceof IPSStatus) {
			final IPSStatus psStatus = (IPSStatus) element;
			int kind = elementChangedDelta.getKind();

			if (kind == IRodinElementDelta.ADDED) {
				if (UserSupportUtils.DEBUG)
					UserSupportUtils.debug("IPSStatus changed: "
							+ element.getElementName() + " is added");
				toBeAdded.add(psStatus);
				needRefreshed = true;
				return;
			}

			if (kind == IRodinElementDelta.REMOVED) {
				if (UserSupportUtils.DEBUG)
					UserSupportUtils.debug("IPSStatus changed: "
							+ element.getElementName() + " is removed");
				// Try to reuse
				IProofState proofState = userSupport.getProofState(psStatus);
				toBeDeleted.add(proofState);
				toBeTrashed.add(proofState);
				needRefreshed = true;
				return;
			}

			if (kind == IRodinElementDelta.CHANGED) {

				int flags = elementChangedDelta.getFlags();
				if ((flags & IRodinElementDelta.F_REORDERED) != 0) {
					needRefreshed = true;
				}

				if ((flags & IRodinElementDelta.F_ATTRIBUTE) != 0) {
					// Try to reuse
					IProofState proofState = userSupport.getProofState(psStatus);
					
					if (proofState == null)
						return;

					// Do nothing if the state is uninitialized
					if (proofState.isUninitialised())
						return;

					// If the state is not modified, reload the proof from the
					// DB
					if (!proofState.isDirty()) {
						toBeReloaded.add(proofState);
						return;
					}

					// if the state is discharged automatically, trash the
					// current proof and reload the proof from the DB
					try {
						if (proofState.isSequentDischarged()) {
							if (UserSupportUtils.DEBUG)
								UserSupportUtils
										.debug("Proof Discharged in file, to be trashed then reloaded");
							toBeTrashed.add(proofState);
							toBeReloaded.add(proofState);
							return;
						}
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					try {
						if (proofState.isProofReusable()) {
							if (UserSupportUtils.DEBUG)
								UserSupportUtils
										.debug("Proof is reusable, to be reused");
							toBeReused.add(proofState);
							return;
						}

						// Trash the current proof tree and then re-build
						else {
							if (UserSupportUtils.DEBUG)
								UserSupportUtils
										.debug("Proof is NOT reusable, to be trashed and rebuilt");
							toBeTrashed.add(proofState);
							toBeRebuilt.add(proofState);
						}
					} catch (RodinDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				return;
			}
		}
	}

}
