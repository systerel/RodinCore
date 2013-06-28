/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prooftreeui.handlers;

import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_editCommentTitle;
import static org.eventb.internal.ui.prooftreeui.handlers.Messages.proofTreeHandler_editSuccessMessage;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.MultiLineInputDialog;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;

/**
 * Handler for the <code>org.eventb.ui.proofTreeUi.editComment</code>
 * ProofTreeNode command.
 * 
 * @author Nicolas Beauger
 */
public class EditCommentHandler extends AbstractProofTreeCommandHandler {

	private static final String COMMAND_ID = "org.eventb.ui.proofTreeUi.editComment";

	@Override
	protected String getCommandID() {
		return COMMAND_ID;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IWorkbenchWindow ww = getActiveIWorkbenchWindow();
		final ProofTreeUI ui = getActiveProofTreeUI(ww);
		final ISelection selection = ui.getSelection();
		if (!(selection instanceof ITreeSelection)) {
			return null;
		}
		final Object nodeObject = ((ITreeSelection) selection)
				.getFirstElement();
		if (!(nodeObject instanceof IProofTreeNode)) {
			return null;
		}
		final IProofTreeNode ptNode = (IProofTreeNode) nodeObject;
		final String currentComment = ptNode.getComment();
		final Shell shell = ww.getShell();
		final IUserSupport userSupport = getUserSupport(ui);
		final InputDialog dialog = new MultiLineInputDialog(shell,
				proofTreeHandler_editCommentTitle, null, currentComment, null,
				userSupport);
		final int result = dialog.open();
		if (result == Window.CANCEL)
			return null;
		final String newComment = dialog.getValue();
		if (newComment == null) {
			return null;
		}
		userSupport.setComment(newComment, ptNode);
		log(proofTreeHandler_editSuccessMessage);
		return null;
	}

}
