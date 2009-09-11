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

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

class DeleteElementLeaf extends OperationLeaf {

	private IInternalElement element;
	private OperationTree createTree;
	private IInternalElement nextSibling;
	private final boolean force;

	public DeleteElementLeaf(IInternalElement element,
			OperationTree createTree, boolean force) {
		super("DeleteElement");
		this.element = element;
		this.createTree = createTree;
		nextSibling = null;
		this.force = force;
	}

	@Override
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		nextSibling = element.getNextSibling();
		element.delete(force, monitor);
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		doExecute(monitor, info);
	}

	@Override
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		element.create(nextSibling, monitor);
		createTree.doExecute(monitor, info);
	}

	public void setParent(IInternalElement element) {
		// do nothing
	}

}
