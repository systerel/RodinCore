/*******************************************************************************
 * Copyright (c) 2007, 2013 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - refactored defining AbstractProofTreeAction
 *     Systerel - refactored using handlers
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui.handlers;

import static org.eventb.core.seqprover.eventbExtensions.Tactics.prune;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_pruneSuccessMessage;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * Handler for the <code>org.eventb.ui.proofTreeUi.prune</code> ProofTreeNode
 * command.
 */
public class PruneHandler extends AbstractProofTreeCommandHandler {

	private final static String COMMAND_ID = "org.eventb.ui.prune";

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
		return super.isEnabled();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		applyTactic(prune(), false, proofTreeHandler_pruneSuccessMessage);
		return null;
	}

}
