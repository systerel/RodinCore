/*******************************************************************************
 * Copyright (c) 2008, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofSkeletonView;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.actions.SelectionListenerAction;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;

public class CopyAction extends SelectionListenerAction {

	/**
	 * The id of this action.
	 */
	public static final String ID = EventBUIPlugin.PLUGIN_ID + ".PrfSklCopyAction"; //$NON-NLS-1$

	private final Clipboard clipboard;

	private final ListViewer viewer;

	/**
	 * Creates a new action.
	 * 
	 * @param viewer
	 * 			the list viewer to get the selection from
	 * @param clipboard
	 *            a platform clipboard
	 */
	public CopyAction(ListViewer viewer, Clipboard clipboard) {
		super(Messages.proofskeleton_copy_title);
		Assert.isNotNull(clipboard);
		this.clipboard = clipboard;
		this.viewer = viewer;
		setToolTipText(Messages.proofskeleton_copy_toolTip);
		setId(CopyAction.ID);
	}

	@Override
	public void run() {
		final String[] selection = viewer.getList().getSelection();
		if (selection.length == 0) {
			return;
		}
		final StringBuilder sb = new StringBuilder();
		for (String line : selection) {
			sb.append(line);
			sb.append('\n');
		}
		clipboard.setContents(new Object[] { sb.toString() },
				new Transfer[] { TextTransfer.getInstance() });
	}
}
