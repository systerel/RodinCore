/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.internal.explorer.navigator.handlers;

import static fr.systerel.explorer.ExplorerPlugin.getSelectedStatuses;
import static org.eventb.core.EventBPlugin.rebuildProof;

import java.util.Set;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IPRProof;
import org.eventb.core.IPSStatus;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;
import fr.systerel.internal.explorer.navigator.actionProviders.Messages;

/**
 * Handler for the 'Replay Proofs of Undischarged POs' command.
 */
public class ReplayUndischargedHandler extends AbstractJobHandler {

	private static final FormulaFactory factory = FormulaFactory.getDefault();

	private static class ReplayJob extends WorkspaceJob {
		
		public ReplayJob(String name) {
			super(name);
		}

		@Override
		public IStatus runInWorkspace(IProgressMonitor monitor)
				throws CoreException {
			try {
				final SubMonitor subMonitor = SubMonitor.convert(monitor,
						Messages.dialogs_replayingProofs, 10);
				final Set<IPSStatus> statuses = getSelectedStatuses(true,
						subMonitor.newChild(1));
				// rebuild proofs of gathered POs
				rebuildProofs(statuses, subMonitor.newChild(9));
			} catch (InterruptedException e) {
				// set and propagate the interrupt status above
				Thread.currentThread().interrupt();
				setJobMonitorDone(monitor);
				// canceled: return as soon as possible
				return Status.CANCEL_STATUS;
			} finally {
				setJobMonitorDone(monitor);
			}
			return Status.OK_STATUS;
		}
	}

	static void rebuildProofs(Set<IPSStatus> statuses, IProgressMonitor monitor)
			throws InterruptedException {
		final SubMonitor subMonitor = SubMonitor.convert(monitor, statuses
				.size());
		for (IPSStatus status : statuses) {
			ExplorerUtils.checkCancel(subMonitor);
			final IPRProof proof = status.getProof();
			try {
				rebuildProof(proof, factory, subMonitor.newChild(1));
			} catch (RodinDBException e) {
				EventBUIExceptionHandler.handleRodinException(e,
						UserAwareness.INFORM);
			}
		}
	}

	@Override
	WorkspaceJob getWorkspaceJob() {
		return new ReplayJob(Messages.dialogs_replayingProofs);
	}

}
