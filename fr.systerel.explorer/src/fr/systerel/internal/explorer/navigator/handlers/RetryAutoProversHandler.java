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
import static fr.systerel.internal.explorer.navigator.ExplorerUtils.runWithProgress;

import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eventb.core.IPSStatus;
import org.eventb.internal.core.pom.AutoProver;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.EventBUIExceptionHandler.UserAwareness;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.actionProviders.Messages;

/**
 * Handler for the Retry Auto Provers command.
 */
public class RetryAutoProversHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkspaceRunnable op = new IWorkspaceRunnable() {

			public void run(IProgressMonitor monitor) throws CoreException {
				try {
					final SubMonitor subMonitor = SubMonitor.convert(monitor,
							Messages.dialogs_replayingProofs, 10);
					final Set<IPSStatus> statuses = getSelectedStatuses(true,
							subMonitor.newChild(1));
					AutoProver.run(statuses.toArray(new IPSStatus[statuses
							.size()]), monitor);
				} catch (RodinDBException e) {
					EventBUIExceptionHandler.handleRodinException(e,
							UserAwareness.IGNORE);
				} catch (InterruptedException e) {
					// canceled: return as soon as possible
				} finally {
					if (monitor != null) {
						monitor.done();
					}
				}
			}
		};
		runWithProgress(op);
	return null;
	}

}
