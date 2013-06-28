/*******************************************************************************
 * Copyright (c) 2010, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui.handlers;

import static org.eclipse.jface.dialogs.MessageDialog.openInformation;
import static org.eventb.internal.ui.prooftreeui.ProofTreeUIUtils.DEBUG;
import static org.eventb.internal.ui.prooftreeui.ProofTreeUIUtils.debug;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.commandNotEnabledInfo;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_commandNotExecutedTitle;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_elementIsNotProofTreeNode;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_moreThanOneElementSelected;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_noActiveProofTreeUIError;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_noActiveUserSupportError;
import static org.eventb.internal.ui.prover.ProverUIUtils.applyTacticWithProgress;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.internal.ui.proofSkeletonView.ProofSkeletonView;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;
import org.eventb.internal.ui.prooftreeui.ProofTreeUIPage;

/**
 * Abstract class for ProofTreeNode command handlers.
 */
public abstract class AbstractProofTreeCommandHandler extends AbstractHandler {

	@Override
	public boolean isEnabled() {
		final IWorkbenchWindow ww = getActiveIWorkbenchWindow();
		if (ww == null) {
			return false;
		}
		final ProofTreeUI ui = getActiveProofTreeUI(ww);
		final IUserSupport us = getUserSupport(ui);
		if (us == null) {
			return false;
		}
		final IProofTreeNode node = getProofTreeNode(ui.getSelection());
		return node != null;
	}

	protected static boolean isInProofSkeletonView(final IWorkbenchWindow ww) {
		final IWorkbenchPage activePage = ww.getActivePage();
		if (activePage == null) {
			return false;
		}
		final IWorkbenchPart activePart = activePage.getActivePart();
		return activePart instanceof ProofSkeletonView;
	}

	protected static IWorkbenchWindow getActiveIWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	protected static ProofTreeUI getActiveProofTreeUI(IWorkbenchWindow ww) {
		final IWorkbenchPage activePage = ww.getActivePage();
		if (activePage == null) {
			return null;
		}
		final IWorkbenchPart part = activePage.getActivePart();
		if (!(part instanceof ProofTreeUI)) {
			return null;
		}
		return (ProofTreeUI) part;
	}

	/**
	 * Returns the <code>id</code> of the command the handler is associated
	 * with.
	 * 
	 * @return the handler command <code>id</code>
	 */
	protected abstract String getCommandID();

	protected IProofTreeNode getProofTreeNode(ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		final IStructuredSelection ssel = (IStructuredSelection) selection;
		if (ssel.size() != 1) {
			logNotEnabled(proofTreeHandler_moreThanOneElementSelected);
			return null;
		}
		final Object firstElement = ssel.getFirstElement();
		if (!(firstElement instanceof IProofTreeNode)) {
			logNotEnabled(proofTreeHandler_elementIsNotProofTreeNode);
			return null;
		}
		return (IProofTreeNode) firstElement;
	}

	protected static IUserSupport getUserSupport(ProofTreeUI ui) {
		final ProofTreeUIPage page = (ProofTreeUIPage) ui.getCurrentPage();
		if (page == null) {
			return null;
		}
		return page.getUserSupport();
	}

	protected final void applyTactic(ITactic tactic, boolean applyPostTactic) {
		final IWorkbenchWindow ww = getActiveIWorkbenchWindow();
		final ProofTreeUI ui = getActiveProofTreeUI(ww);
		final Shell shell = ww.getShell();
		if (ui == null) {
			logAndInformCommandNotExecuted(shell,
					proofTreeHandler_noActiveProofTreeUIError);
		}
		final IUserSupport us = getUserSupport(ui);
		if (us == null) {
			logAndInformCommandNotExecuted(shell,
					proofTreeHandler_noActiveUserSupportError);
		}
		applyTacticWithProgress(shell, us, tactic, applyPostTactic);
	}

	protected void logNotEnabled(String message) {
		if (DEBUG) {
			final String id = getCommandID();
			debug(commandNotEnabledInfo(id, message));
		}
	}

	protected void logAndInformCommandNotExecuted(Shell shell, String cause) {
		final String commandID = getCommandID();
		final String errorMessage = Messages.commandError(commandID, cause);
		if (DEBUG) {
			debug(errorMessage);
		}
		openInformation(shell, proofTreeHandler_commandNotExecutedTitle,
				errorMessage);
	}

}
