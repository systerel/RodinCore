/*******************************************************************************
 * Copyright (c) 2009, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.handlers;

import static org.eventb.internal.ui.UIUtils.showInfo;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IPRProof;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.proofsimplifier.ProofSimplification.FecthProofs;
import org.eventb.internal.ui.proofsimplifier.ProofSimplification.Simplify;
import org.eventb.internal.ui.utils.Messages;

/**
 * @author "Nicolas Beauger"
 * 
 */
public class ProofSimplifyHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			showInfo(Messages.proofSimplification_invalidSelection);
			return null;
		}
		final FecthProofs fetchProofs = new FecthProofs(
				((IStructuredSelection) selection).toList());
		final Shell shell = PlatformUI.getWorkbench()
				.getModalDialogShellProvider().getShell();
		if (shell == null) {
			showInfo(Messages.proofSimplification_couldNotRun);
			return null;
		}
		runOp(fetchProofs, shell);
		if (fetchProofs.wasCancelled())
			return null;
		final IPRProof[] input = fetchProofs.getProofs();
		if (input.length == 0) {
			showInfo(Messages.proofSimplification_noProofsToSimplify);
			return null;
		}
		final Simplify simplify = new Simplify(input);
		runOp(simplify, shell);
		return null;
	}

	private static void runOp(final IRunnableWithProgress op, Shell shell) {
		try {
			new ProgressMonitorDialog(shell).run(true, true, op);
		} catch (InvocationTargetException e) {
			final Throwable cause = e.getCause();
			UIUtils.showUnexpectedError(cause, "while simplifying proofs");
		} catch (InterruptedException e) {
			// Propagate the interruption
			Thread.currentThread().interrupt();
		}
	}

}
