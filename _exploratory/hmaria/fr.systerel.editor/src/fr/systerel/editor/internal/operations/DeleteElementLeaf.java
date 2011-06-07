/*******************************************************************************
 * Copyright (c) 2008, 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.internal.operations;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

class DeleteElementLeaf extends OperationLeaf {

	private final IInternalElement element;
	private final boolean force;

	private OperationTree createTree;
	private IInternalElement nextSibling;

	public DeleteElementLeaf(IInternalElement element,
			OperationTree createTree, boolean force) {
		super("DeleteElement");
		this.element = element;
		this.force = force;
		this.createTree = createTree;
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

	@Override
	public void setParent(IInternalElement element) {
		// do nothing
	}

}
