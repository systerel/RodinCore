/*******************************************************************************
 * Copyright (c) 2009 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package fr.systerel.editor.operations;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

class CreateElementGeneric<T extends IInternalElement> extends OperationLeaf {

	private IInternalElement parent;
	private final IInternalElementType<T> type;
	private final IInternalElement sibling;
	private IInternalElement element;
	private OperationTree operationDelete;
	private boolean first;

	public CreateElementGeneric(IInternalElement parent,
			final IInternalElementType<T> type, final IInternalElement sibling) {
		super("CreateElementGeneric");
		this.parent = parent;
		this.type = type;
		this.sibling = sibling;
		first = true;
	}

	@Override
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {

		final IInternalElement root = parent.getRoot();
		element = ElementDescRegistry.getInstance().createElement(root,
				parent, type, sibling);
		final OperationBuilder builder = new OperationBuilder();
		operationDelete = builder.deleteElement(element, true);
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		operationDelete.doUndo(monitor, info);
	}

	@Override
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		if (first) {
			operationDelete.doExecute(monitor, info);
		} else {
			operationDelete.doRedo(monitor, info);
		}
	}

	@Override
	public void setParent(IInternalElement element) {
		parent = element;
	}

	@Override
	public Collection<IInternalElement> getCreatedElements() {
		return Collections.singletonList(element);
	}

	@Override
	public IInternalElement getCreatedElement() {
		return element;
	}
}
