/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     ETH Zurich - adapted from org.eclipse.jdt.internal.core.BatchOperation
 *******************************************************************************/
package org.rodinp.internal.core;

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.rodinp.core.IRodinDBStatus;
import org.rodinp.core.RodinDBException;

/**
 * An operation created as a result of a call to RodinCore.run(IWorkspaceRunnable, IProgressMonitor)
 * that encapsulates a user defined IWorkspaceRunnable.
 */
public class BatchOperation extends RodinDBOperation {

	private final IWorkspaceRunnable runnable;
	private final ISchedulingRule rule;

	public BatchOperation(IWorkspaceRunnable runnable, ISchedulingRule rule) {
		this.runnable = runnable;
		this.rule = rule;
	}

	@Override
	protected void executeOperation() throws RodinDBException {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			if (workspace.isTreeLocked()) {
				runnable.run(progressMonitor);
			} else {
				workspace.run(runnable, rule,
						IWorkspace.AVOID_UPDATE, progressMonitor);
			}
		} catch (RodinDBException re) {
			throw re;
		} catch (CoreException ce) {
			if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
				Throwable e = ce.getStatus().getException();
				if (e instanceof RodinDBException) {
					throw (RodinDBException) e;
				}
			}
			throw new RodinDBException(ce);
		}
	}
	
	@Override
	protected IRodinDBStatus verify() {
		// cannot verify user defined operation
		return RodinDBStatus.VERIFIED_OK;
	}
	
}
