/*******************************************************************************
 * Copyright (c) 2011 Systerel and others. 
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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPart;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.MultiLineInputDialog;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

/**
 * @author Nicolas Beauger
 *
 */
public class EditProofTreeNodeComment extends AbstractProofTreeAction {

	public EditProofTreeNodeComment() {
		super(true);
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		super.setUserSupport(targetPart);
		super.setActivePart(action, targetPart);
	}
	
	@Override
	public void run(IAction action) {
		final IStructuredSelection ssel = extractStructuredSelection();
		assertIsProofTreeNode(ssel);
		final IProofTreeNode node = (IProofTreeNode) ssel.getFirstElement();
		final String currentComment = node
				.getComment();
		final InputDialog dialog = new MultiLineInputDialog(shell,
				Messages.EditProofTreeNodeComment_title,
				null,
				currentComment,
				null,
				userSupport);
		final int result = dialog.open();
		if (result == Window.CANCEL)
			return;
		final String newComment = dialog.getValue();
		if (newComment == null) {
			return;
		}

		try {
			userSupport.setComment(newComment, node);
		} catch (RodinDBException e) {
			UIUtils.log(e, "while setting proof node comment: " + newComment); //$NON-NLS-1$
		}
	}

	@Override
	protected boolean isEnabled(IAction action, ISelection sel) {
		if (isInProofSkeletonView(action)) {
			traceDisabledness("In proof skeleton view", action); //$NON-NLS-1$
			return false;
		}
		if (!isUserSupportPresent(action)) {
			traceDisabledness("No user support present", action); //$NON-NLS-1$
			return false;
		}
		// do not take open/close node condition into account
		return true;
	}

}
