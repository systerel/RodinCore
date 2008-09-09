package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.rodinp.core.IInternalElement;

/**
 * Operation Node avec une operation parent pour creer un element Les operations
 * fils dependent de l'element cree.
 * <p>
 * Lors de execute, l'element creer de vient le pere de chaque fils.
 * <p>
 * la methode setParent de OperationCreateElement ne s'applique que sur
 * operationCreate
 */
public class OperationCreateElement extends OperationNode {

	final private CreateElementGeneric<?> operationCreate;

	public OperationCreateElement(CreateElementGeneric<?> operationCreate) {
		this.operationCreate = operationCreate;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		operationCreate.execute(monitor, info);
		final IInternalElement element = operationCreate.getElement();
		for (OperationTree op : childrens) {
			op.setParent(element);
		}
		return super.execute(monitor, info);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		operationCreate.redo(monitor, info);
		return super.redo(monitor, info);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return operationCreate.undo(monitor, info);
	}

	@Override
	public void setParent(IInternalElement element) {
		operationCreate.setParent(element);
	}
}
