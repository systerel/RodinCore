/*******************************************************************************
 * Copyright (c) 2008, 2015 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.operations;

import static org.eclipse.core.runtime.SubMonitor.convert;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * This class handles the move operation of internal elements.
 * <p>
 * Two cases are considered in this implementation:
 * <ul>
 * <li>when element source and destination parents are the same, there is no
 * risk of naming collision,</li>
 * <li>when element source and destination parents differ, there could be a risk
 * of naming collision, thus a fresh element name is computed. The old name of
 * the element is then kept to allow conservative <tt>undo</tt> and <tt>redo</tt>
 * operations.</li>
 * </ul>
 * </p>
 */
public class Move extends OperationLeaf {

	private final IInternalElement oldParent;
	private final IInternalElement oldSibling;
	private final String oldName;
	private final IInternalElementType<?> type;
	private final IInternalElement newParent;
	private final IInternalElement newSibling;
	private String newName;

	private IInternalElement movedElement;

	public Move(IInternalElement movedElement, IInternalElement newParent,
			IInternalElement newSibling) {
		super("Move");
		this.movedElement = movedElement;
		this.newParent = newParent;
		this.newSibling = newSibling;
		oldName = movedElement.getElementName();
		type = movedElement.getElementType();
		oldParent = (IInternalElement) movedElement.getParent();
		oldSibling = getNextSibling(movedElement);
	}

	private IInternalElement getNextSibling(IInternalElement element) {
		try {
			return element.getNextSibling();
		} catch (RodinDBException e) {
			return null;
		}
	}

	private void move(IInternalElement parent, IInternalElement sibling,
			String name, IProgressMonitor monitor) throws RodinDBException {
		movedElement.move(parent, sibling, name, false, monitor);
		movedElement = parent.getInternalElement(type, name);
	}

	/**
	 * Fresh name is computed here if necessary, as it depends on the move
	 * operation context (i.e. it can't be computed at construction).
	 */
	@Override
	public void doExecute(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		final SubMonitor sm = convert(monitor, 3);
		try {
			if (!oldParent.equals(newParent)) {
				final IInternalElement temp;
				temp = newParent.createChild(type, null, sm.newChild(1));
				newName = temp.getElementName();
				temp.delete(false, sm.newChild(1));
			} else {
				newName = oldName;
				sm.setWorkRemaining(1);
			}
			move(newParent, newSibling, newName, sm.newChild(1));
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	@Override
	public void doUndo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		move(oldParent, oldSibling, oldName, monitor);
	}

	@Override
	public void doRedo(IProgressMonitor monitor, IAdaptable info)
			throws RodinDBException {
		move(newParent, newSibling, newName, monitor);
	}

	@Override
	public void setParent(IInternalElement element) {
		// do nothing
	}

}