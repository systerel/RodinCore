/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Refactored defining AbstractProofTreeAction
 *     Systerel - refactored using handlers
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui.handlers;

import static org.eventb.core.seqprover.tactics.BasicTactics.rebuildTac;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.pasteSuccessMessage;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_copyBufferIsEmpty;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_copyBufferNotAProofSkeleton;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eventb.core.seqprover.IProofSkeleton;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;

/**
 * Handler for the <code>org.eventb.ui.proofTreeUi.paste</code> ProofTreeNode
 * command.
 */
public class PasteHandler extends AbstractProofTreeCommandHandler {

	private static final String COMMAND_ID = "org.eventb.ui.proofTreeUi.paste";

	@Override
	protected String getCommandID() {
		return COMMAND_ID;
	}
	
	@Override
	public boolean isEnabled() {
		final IWorkbenchWindow ww = getActiveIWorkbenchWindow();
		if (ww == null) {
			return false;
		}
		if (isInProofSkeletonView(ww)) {
			return false;
		}
		final boolean goForward = super.isEnabled();
		if (!goForward)
			return false;
		if (!isNodeOpen())
			return false;
		if (ProofTreeUI.buffer == null) {
			logNotEnabled(proofTreeHandler_copyBufferIsEmpty);
			return false;
		}
		if (!(ProofTreeUI.buffer instanceof IProofSkeleton)) {
			logNotEnabled(proofTreeHandler_copyBufferNotAProofSkeleton);
			return false;
		}
		return true;
	}

	private boolean isNodeOpen() {
		final IWorkbenchWindow ww = getActiveIWorkbenchWindow();
		final ProofTreeUI ui = getActiveProofTreeUI(ww);
		final IProofTreeNode node = getProofTreeNode(ui.getSelection());
		return node.isOpen();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		assert ProofTreeUI.buffer instanceof IProofSkeleton;
		final IProofSkeleton copyNode = (IProofSkeleton) ProofTreeUI.buffer;
		applyTactic(rebuildTac(copyNode), true, pasteSuccessMessage(copyNode));
		return null;
	}

}
