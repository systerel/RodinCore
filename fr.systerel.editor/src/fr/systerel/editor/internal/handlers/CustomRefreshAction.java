/*******************************************************************************
 * Copyright (c) 2000, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Systerel - modification to introduce mutexRule
 *******************************************************************************/
package fr.systerel.editor.internal.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.misc.StatusUtil;

/**
 * Customization of org.eclipse.ui.actions.RefreshAction in order to be able to
 * specify the custom scheduling rule used to run the refresh action.
 */
@SuppressWarnings("restriction")
public class CustomRefreshAction extends RefreshAction {

	private final ISchedulingRule customRule;

	public CustomRefreshAction(IShellProvider provider, ISchedulingRule rule) {
		super(provider);
		this.customRule = rule;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.actions.WorkspaceAction#run()
	 */
	public void run() {
		final IStatus[] errorStatus = new IStatus[1];
		errorStatus[0] = Status.OK_STATUS;
		final WorkspaceModifyOperation op = (WorkspaceModifyOperation) createOperation(errorStatus);
		WorkspaceJob job = new WorkspaceJob("refresh") { //$NON-NLS-1$

			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				try {
					op.run(monitor);
				} catch (InvocationTargetException e) {
					String msg = NLS.bind(
							IDEWorkbenchMessages.WorkspaceAction_logTitle,
							getClass().getName(), e.getTargetException());
					throw new CoreException(StatusUtil.newStatus(IStatus.ERROR,
							msg, e.getTargetException()));
				} catch (InterruptedException e) {
					return Status.CANCEL_STATUS;
				}
				return errorStatus[0];
			}

		};
		// The following line is the only modification of
		// org.eclipse.ui.actions.RefreshAction
		ISchedulingRule rule = getMutexRule(op.getRule());
		if (rule != null) {
			job.setRule(rule);
		}
		job.setUser(true);
		job.schedule();
	}

	/**
	 * Returns a complex rule combining the custom rule carried by the action
	 * and the rule corresponding to the refresh action.
	 * 
	 * @param operationRule
	 *            the rule on manipulated resources
	 * @return a complex rule combining the provided custom rule and the rule
	 *         used by the action to operate on resources.
	 */
	private ISchedulingRule getMutexRule(ISchedulingRule operationRule) {
		return MultiRule.combine(operationRule, customRule);
	}

}