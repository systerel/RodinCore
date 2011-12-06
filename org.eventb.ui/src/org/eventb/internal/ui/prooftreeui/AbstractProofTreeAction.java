/*******************************************************************************
 * Copyright (c) 2010 Systerel.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - Initial API and implementation
 ******************************************************************************/
package org.eventb.internal.ui.prooftreeui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITactic;
import org.eventb.internal.ui.proofSkeletonView.ProofSkeletonView;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.EventBUIPlugin;

public abstract class AbstractProofTreeAction implements IObjectActionDelegate {

	protected IStructuredSelection selection;

	private final boolean enabledOnOpenNode;

	protected Shell shell;

	protected IUserSupport userSupport = null;

	public AbstractProofTreeAction(boolean canBeOpen) {
		super();
		this.enabledOnOpenNode = canBeOpen;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.shell = targetPart.getSite().getShell();
	}

	@Override
	public void selectionChanged(IAction action, ISelection sel) {
		this.selection = extractProofTreeNode(sel);
		action.setEnabled(isEnabled(action, selection));
	}

	protected boolean isEnabled(IAction action, ISelection sel) {
		if (selection.size() != 1) {
			traceDisabledness("There should be exactly one selected element",
					action);
			return false;
		}
		if (!(selection.getFirstElement() instanceof IProofTreeNode)) {
			traceDisabledness(
					"The selected element should be a IProofTreeNode", action);
			return false;
		}
		final IProofTreeNode node = (IProofTreeNode) selection.getFirstElement();
		if (enabledOnOpenNode != node.isOpen()) {
			traceDisabledness("The proof tree node should be "
					+ (enabledOnOpenNode ? "open" : "not open"), action);
			return false;
		}
		if (ProofTreeUIUtils.DEBUG) {
			ProofTreeUIUtils.debug("Enable " + action.getId());
		}
		return true;
	}

	protected void traceDisabledness(final String message, IAction action) {
		if (ProofTreeUIUtils.DEBUG) {
			ProofTreeUIUtils.debug(message + ", disable " + action.getId());
		}
	}

	private static IStructuredSelection extractProofTreeNode(ISelection sel) {
		assert sel instanceof IStructuredSelection;
		final IStructuredSelection ssel = (IStructuredSelection) sel;
		assert (ssel.size() == 1);
		assert (ssel.getFirstElement() instanceof IProofTreeNode);
		return ssel;
	}

	public void setUserSupport(IWorkbenchPart targetPart) {
		if (targetPart instanceof ProofTreeUI) {
			final ProofTreeUI ui = (ProofTreeUI) targetPart;
			final ProofTreeUIPage page = (ProofTreeUIPage) ui.getCurrentPage();
			if (page == null) {
				return;
			}
			this.userSupport = page.getUserSupport();
		}
	}

	public boolean isInProofSkeletonView(IAction action) {
		final IWorkbenchPage page = EventBUIPlugin.getActivePage();
		if (page == null) {
			return false;
		}
		final IWorkbenchPart part = page.getActivePart();
		return part instanceof ProofSkeletonView;
	}

	public boolean isUserSupportPresent(IAction action) {
		return userSupport != null;
	}

	protected final void applyTactic(ITactic tactic, boolean applyPostTactic) {
		assert userSupport != null;
		ProverUIUtils.applyTacticWithProgress(shell, userSupport, tactic,
				applyPostTactic);
	}

}