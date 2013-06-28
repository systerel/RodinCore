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

import static org.eventb.internal.ui.prooftreeui.handlers.Messages.copySuccessMessage;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_invalidSelectionError;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_selectionNotProofTreeNodeError;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;


/**
 * Handler for the <code>org.eventb.ui.proofTreeUi.copy</code> ProofTreeNode
 * command.
 */
public class CopyHandler extends AbstractProofTreeCommandHandler {


	private static final String COMMAND_ID = "org.eventb.ui.proofTreeUi.copy";

	@Override
	protected String getCommandID() {
		return COMMAND_ID;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow ww = getActiveIWorkbenchWindow();
		final ProofTreeUI ui = getActiveProofTreeUI(ww);
		final ISelection selection = ui.getSelection();
		final Shell shell = ww.getShell();
		if (!(selection instanceof ITreeSelection)) {
			logAndInformCommandNotExecuted(shell,
					proofTreeHandler_invalidSelectionError);
			return null;
		}
		final ITreeSelection ts = ((ITreeSelection) selection);
		final Object selectedNode = ts.getFirstElement();
		if (!(selectedNode instanceof IProofTreeNode)) {
			logAndInformCommandNotExecuted(shell,
					proofTreeHandler_selectionNotProofTreeNodeError);
			return null;
		}
		ProofTreeUI.buffer = ((IProofTreeNode) selectedNode)
				.copyProofSkeleton();
		log(copySuccessMessage(ProofTreeUI.buffer));
		return null;
	}


}
