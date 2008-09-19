package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.RodinDBException;

public class Move extends OperationLeaf {

	// private final boolean up;
	// private final IInternalParent parent;
	// private final IInternalElementType<?> type;
	// // first element to move
	// private final IInternalElement firstElement;
	// // last element to move
	// private final IInternalElement lastElement;
	// private IInternalElement prevElement;
	// private IInternalElement nextElement;

	final private IInternalParent parent;
	final private IInternalElement oldSibling;
	final private IInternalElement newSibling;
	final private IInternalElement movedElement;

	// public Move(boolean up, IInternalParent parent,
	// IInternalElementType<?> type, IInternalElement firstElement,
	// IInternalElement lastElement) {
	// super("Move");
	// this.up = up;
	// this.parent = parent;
	// this.type = type;
	// this.firstElement = firstElement;
	// this.lastElement = lastElement;
	// }

	public Move(IInternalParent parent, IInternalElement movedElement,
			IInternalElement oldSibling, IInternalElement newSibling) {
		super("Move");
		assert movedElement != null ;
		this.parent = parent;
		this.movedElement = movedElement;
		this.oldSibling = oldSibling;
		this.newSibling = newSibling;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return move(newSibling);
	}

	/**
	 * If lUp is true move Prev after the last, else move Next before the first
	 */
	private IStatus move(IInternalElement sibling) {
		try {
			movedElement.move(parent, sibling, null, false,
					new NullProgressMonitor());
		} catch (RodinDBException e) {
			e.getStatus();
			return e.getStatus();
		}
		return Status.OK_STATUS;

	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return move(newSibling);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return move(oldSibling);

	}

	public void setParent(IInternalElement element) {
		// TODO Auto-generated method stub

	}

}
