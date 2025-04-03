/*******************************************************************************
 * Copyright (c) 2018, 2025 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover.handlers;

import static org.eclipse.jface.dialogs.ErrorDialog.openError;
import static org.eclipse.jface.dialogs.MessageDialog.openInformation;
import static org.eclipse.ui.handlers.HandlerUtil.getActiveWorkbenchWindowChecked;
import static org.eclipse.ui.statushandlers.StatusManager.LOG;
import static org.eclipse.ui.statushandlers.StatusManager.SHOW;
import static org.eventb.core.seqprover.SequentProver.checkAutoTactics;
import static org.eventb.internal.ui.UIUtils.log;
import static org.eventb.internal.ui.handlers.Messages.proof_checkExternalProvers_error_message;
import static org.eventb.internal.ui.handlers.Messages.proof_checkExternalProvers_ok_message;
import static org.eventb.internal.ui.handlers.Messages.proof_checkExternalProvers_title;
import static org.eventb.ui.EventBUIPlugin.getActiveWorkbenchWindow;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eventb.ui.EventBUIPlugin;

/**
 * Default handler for the check external prover command.
 * 
 * Also includes a lazy check to be run at startup.
 *
 * @author Laurent Voisin
 */
public class CheckExternalProvers extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow window = getActiveWorkbenchWindowChecked(event);
		final IProgressService ps = window.getWorkbench().getProgressService();
		try {
			ps.busyCursorWhile(pm -> run(window.getShell(), pm));
		} catch (InterruptedException e) {
			// Do nothing
		} catch (InvocationTargetException e) {
			final IStatus status = new Status(IStatus.ERROR, //
					EventBUIPlugin.PLUGIN_ID, //
					e.getLocalizedMessage(), e);
			StatusManager.getManager().handle(status, SHOW | LOG);
		}
		return null;
	}

	private void run(Shell shell, IProgressMonitor pm) {
		final IStatus status = checkAutoTactics(true, pm);
		shell.getDisplay().asyncExec(() -> {
			if (status.isOK()) {
				openInformation(shell, proof_checkExternalProvers_title, //
						proof_checkExternalProvers_ok_message);
			} else {
				reportError(shell, status);
			}
		});
	}

	/**
	 * Job to check external provers asynchronously.
	 */
	private static class LazyCheckerJob extends Job {

		public LazyCheckerJob() {
			super("External prover check");
		}

		/**
		 * How many times we should try again when the check fails.
		 */
		private static int RETRIES = 2;

		private int try_count = 0;

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			final IStatus status = checkAutoTactics(false, monitor);
			if (!status.isOK()) {
				if (try_count++ < RETRIES) {
					schedule(5_000);
				} else {
					reportError(null, status);
				}
			}
			return Status.OK_STATUS;
		}

	}

	/**
	 * Launch an external prover check asynchronously.
	 */
	public static void checkExternalProversLazily() {
		final Job checker = new LazyCheckerJob();
		checker.setPriority(Job.BUILD);

		// Prevent the check from running concurrently with a build
		// to avoid concurrency issues and potential timeouts.
		checker.setRule(ResourcesPlugin.getWorkspace().getRoot());

		// Delay the scheduling of the job in order to not interfere with the
		// Rodin startup.
		checker.schedule(10_000);
	}

	static void reportError(Shell shell, IStatus status) {
		log(status);

		Display.getDefault().asyncExec(() -> {
			Shell realShell = shell != null ? shell : getActiveWorkbenchWindow().getShell();
			openError(realShell, proof_checkExternalProvers_title, //
					proof_checkExternalProvers_error_message, status);
		});
	}

}
