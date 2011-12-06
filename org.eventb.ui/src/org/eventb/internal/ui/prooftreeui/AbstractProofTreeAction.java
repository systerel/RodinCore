/*******************************************************************************
 * Copyright (c) 2010, 2011 Systerel.
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

	private IProofTreeNode selection;

	private final boolean enabledOnOpenNode;

	protected Shell shell;

	protected IUserSupport userSupport = null;

	public AbstractProofTreeAction(boolean canBeOpen) {
		super();
		this.enabledOnOpenNode = canBeOpen;
	}

	public IProofTreeNode getSelection() {
		return selection;
	}
	
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.shell = targetPart.getSite().getShell();
	}

	@Override
	public void selectionChanged(IAction action, ISelection sel) {
		final IProofTreeNode node = extractProofTreeNode(action, sel);
		if (node == null) {
			action.setEnabled(false);
			return;
		}
		this.selection = node;
		action.setEnabled(isEnabled(action));
	}

	protected boolean isEnabled(IAction action) {
		if (enabledOnOpenNode != selection.isOpen()) {
			traceDisabledness("The proof tree node should be "
					+ (enabledOnOpenNode ? "open" : "not open"), action);
			return false;
		}
		if (ProofTreeUIUtils.DEBUG) {
			ProofTreeUIUtils.debug("Enable " + action.getId());
		}
		return true;
	}

	protected static void traceDisabledness(final String message, IAction action) {
		if (ProofTreeUIUtils.DEBUG) {
			ProofTreeUIUtils.debug(message + ", disable " + action.getId());
		}
	}

	private static IProofTreeNode extractProofTreeNode(IAction action,
			ISelection sel) {
		if (!(sel instanceof IStructuredSelection)) {
			return null;
		}
		final IStructuredSelection ssel = (IStructuredSelection) sel;
		if (ssel.size() != 1) {
			traceDisabledness("There should be exactly one selected element",
					action);
			return null;
		}
		final Object firstElement = ssel.getFirstElement();
		if (!(firstElement instanceof IProofTreeNode)) {
			traceDisabledness(
					"The selected element should be a IProofTreeNode", action);
			return null;
		}
		return (IProofTreeNode) firstElement;
	}

	protected void setUserSupport(IWorkbenchPart targetPart) {
		if (targetPart instanceof ProofTreeUI) {
			final ProofTreeUI ui = (ProofTreeUI) targetPart;
			final ProofTreeUIPage page = (ProofTreeUIPage) ui.getCurrentPage();
			if (page == null) {
				return;
			}
			this.userSupport = page.getUserSupport();
		}
	}

	protected static boolean isInProofSkeletonView(IAction action) {
		final IWorkbenchPage page = EventBUIPlugin.getActivePage();
		if (page == null) {
			return false;
		}
		final IWorkbenchPart part = page.getActivePart();
		return part instanceof ProofSkeletonView;
	}

	protected boolean isUserSupportPresent(IAction action) {
		return userSupport != null;
	}

	protected final void applyTactic(ITactic tactic, boolean applyPostTactic) {
		assert userSupport != null;
		ProverUIUtils.applyTacticWithProgress(shell, userSupport, tactic,
				applyPostTactic);
	}

}