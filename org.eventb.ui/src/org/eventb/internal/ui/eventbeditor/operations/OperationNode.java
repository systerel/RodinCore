/*******************************************************************************
 * Copyright (c) 2008, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rodinp.core.IInternalElement;

class OperationNode extends AbstractEventBOperation implements OperationTree,
		Iterable<OperationTree> {

	protected ArrayList<OperationTree> children;

	public OperationNode() {
		super("CommandNode");
		children = new ArrayList<OperationTree>();
	}

	@Override
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws CoreException {
		for (OperationTree child: children) {
				child.doExecute(monitor, info);
		}
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws CoreException {
		for (OperationTree child: children) {
			child.doRedo(monitor, info);
		}
	}

	@Override
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws CoreException {
		for (int i = children.size() - 1; 0 <= i; i--) {
			children.get(i).doUndo(monitor, info);
		}
	}

	public void addCommand(OperationTree cmd) {
		if (cmd != this) {
			children.add(cmd);
		}
	}

	@Override
	public void setParent(IInternalElement element) {
		for (OperationTree child : children) {
			child.setParent(element);
		}
	}

	@Override
	public Collection<IInternalElement> getCreatedElements() {
		ArrayList<IInternalElement> result = new ArrayList<IInternalElement>();
		for (OperationTree child : children) {
			result.addAll(child.getCreatedElements());
		}
		return result;
	}

	@Override
	public IInternalElement getCreatedElement() {
		if (children.size() > 0) {
			return children.get(0).getCreatedElement();
		} else {
			return null;
		}
	}

	@Override
	public Iterator<OperationTree> iterator() {
		return children.iterator();
	}

}
