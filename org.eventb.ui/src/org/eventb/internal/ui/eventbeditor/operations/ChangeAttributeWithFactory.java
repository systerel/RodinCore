package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

class ChangeAttributeWithFactory extends OperationLeaf {

	private final IAttributeFactory factory;
	private final IAttributedElement element;
	private String valueDo;
	private String valueUndo;

	public ChangeAttributeWithFactory(IAttributeFactory factory,
			IAttributedElement element, String value) {
		super("ChangeAttibuteWithFactory");
		this.factory = factory;
		this.element = element;
		this.valueDo = value;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			valueUndo = factory.getValue(element, monitor);
			factory.setValue(element, valueDo, monitor);
		} catch (RodinDBException e) {
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			factory.setValue(element, valueDo, monitor);
		} catch (RodinDBException e) {
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		try {
			factory.setValue(element, valueUndo, monitor);
		} catch (RodinDBException e) {
			return e.getStatus();
		}
		return Status.OK_STATUS;
	}

	/**
	 * parent is the element to be modified.<p>
	 * The method is not available
	 * */
	public void setParent(IInternalElement element) {
		// TODO Auto-generated method stub
	}
}
