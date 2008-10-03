/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eventb.internal.ui.eventbeditor.TreeSupports;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

public class Handle extends OperationLeaf {

	private final TreeViewer viewer;
	private final boolean up;

	public Handle(TreeViewer viewer, boolean up) {
		super("Move");
		this.viewer = viewer;
		this.up = up;
	}

	private IStatus move(TreeViewer lViewer, boolean lUp) {
		Tree tree = lViewer.getTree();
		TreeItem[] items = tree.getSelection();
		TreeItem item = items[0];
		TreeItem next;
		if (lUp) {
			next = TreeSupports.findPrevItem(tree, item);
		} else {
			next = TreeSupports.findNextItem(tree, item);
		}

		IRodinElement currObj = (IRodinElement) item.getData();
		IRodinElement nextObj = (IRodinElement) next.getData();

		try {
			((IInternalElement) nextObj).move(nextObj.getParent(), currObj,
					null, lUp, null);
			TreeItem newItem = TreeSupports.findItem(tree, currObj);
			lViewer.setSelection(new StructuredSelection(newItem.getData()));
		} catch (RodinDBException e) {
			e.printStackTrace();
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return move(viewer,up);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return move(viewer,up);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return move(viewer,!up);
	}

	public void setParent(IInternalElement element) {
		// TODO Auto-generated method stub

	}

}
