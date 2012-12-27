/*******************************************************************************
 * Copyright (c) 2010, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.handlers;

import static org.eventb.core.EventBPlugin.rebuildProof;

import java.util.Set;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;
import fr.systerel.internal.explorer.navigator.actionProviders.Messages;

/**
 * Handler for the 'Replay Proofs of Undischarged POs' command.
 */
public class ReplayUndischargedHandler extends AbstractJobHandler {

	@Override
	protected WorkspaceJob getWorkspaceJob(IStructuredSelection sel) {
		return new ProofStatusJob(Messages.dialogs_replayingProofs, true, sel) {

			@Override
			protected void perform(Set<IPSStatus> statuses,
					SubMonitor subMonitor) throws RodinDBException,
					InterruptedException {
				rebuildProofs(statuses, subMonitor);
			}
		};
	}

	static void rebuildProofs(Set<IPSStatus> statuses, IProgressMonitor monitor)
			throws InterruptedException, RodinDBException {
		final SubMonitor subMonitor = SubMonitor.convert(monitor,
				statuses.size());
		for (IPSStatus status : statuses) {
			ExplorerUtils.checkCancel(subMonitor);
			final IPRProof proof = status.getProof();
			rebuildProof(proof, true, subMonitor.newChild(1));
		}
	}

}
