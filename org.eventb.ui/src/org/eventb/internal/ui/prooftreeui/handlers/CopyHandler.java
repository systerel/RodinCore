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

import static org.eventb.internal.ui.prooftreeui.handlers.Messages.commandError;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.copySuccessMessage;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
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
		final Object appContext = event.getApplicationContext();
		if (!(appContext instanceof EvaluationContext)) {
			log(commandError(COMMAND_ID, "invalid context"));
			return null;
		}
		final EvaluationContext context = (EvaluationContext) appContext;

		// default variable is the selection as a collection
		final Object var = context.getDefaultVariable();
		if (!(var instanceof Collection)) {
			log(commandError(COMMAND_ID,
					"invalid context variable: collection expected"));
			return null;
		}
		final Collection<?> selection = (Collection<?>) var;
		if (selection.size() != 1) {
			log(commandError(COMMAND_ID, "invalid selection size: 1 expected"));
			return null;
		}

		final Object selected = selection.iterator().next();
		if (!(selected instanceof IProofTreeNode)) {
			log(commandError(COMMAND_ID,
					"invalid selection: proof tree node expected"));
			return null;
		}
		final IProofTreeNode selectedNode = (IProofTreeNode) selected;

		ProofTreeUI.buffer = selectedNode.copyProofSkeleton();
		log(copySuccessMessage(ProofTreeUI.buffer));
		return null;
	}

}
