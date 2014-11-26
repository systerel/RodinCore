/*******************************************************************************
 * Copyright (c) 2007, 2014 ETH Zurich and others.
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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
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
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);

		if (!(sel instanceof IStructuredSelection)) {
			throw new ExecutionException(
					"invalid selection for proof tree copy");
		}
		final Object selected = ((IStructuredSelection) sel).getFirstElement();
		if (!(selected instanceof IProofTreeNode)) {
			throw new ExecutionException(
					"invalid selection: proof tree node expected");
		}
		final IProofTreeNode selectedNode = (IProofTreeNode) selected;

		ProofTreeUI.buffer = selectedNode.copyProofSkeleton();
		log(copySuccessMessage(ProofTreeUI.buffer));
		return null;
	}

}
