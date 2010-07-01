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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;

public abstract class AbstractJobHandler extends AbstractHandler {

	/**
	 * @return the specific WorkspaceJob associated with this handler.
	 */
	abstract WorkspaceJob getWorkspaceJob();

	public Object execute(ExecutionEvent event) throws ExecutionException {
		final WorkspaceJob job = getWorkspaceJob();
		job.setUser(true);
		try {
			job.setRule(getSchedulingRule());
		} catch (InterruptedException e) {
			// set and propagate the interrupt status above
			Thread.currentThread().interrupt();
			return null;
		}
		job.schedule();
		return null;
	}

	// TODO should be implemented by the job itself
	private ISchedulingRule getSchedulingRule() throws InterruptedException {
		final Set<ISchedulingRule> rules = new HashSet<ISchedulingRule>();
		final Set<IPSStatus> statuses = getSelectedStatuses(true, null);
		for (final IPSStatus status : statuses) {
			final IPSRoot psRoot = (IPSRoot) status.getRoot();
			rules.add(psRoot.getPORoot().getSchedulingRule());
			rules.add(psRoot.getPRRoot().getSchedulingRule());
			rules.add(psRoot.getSchedulingRule());
		}
		final ISchedulingRule[] array = rules.toArray(new ISchedulingRule[rules
				.size()]);
		return MultiRule.combine(array);
	}
	
	public static void setJobMonitorDone(IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.done();
		}
	}

}
