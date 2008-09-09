package org.eventb.internal.ui.eventbeditor.operations;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.internal.ui.Pair;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IAttributedElement;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

class ChangeAttribute extends OperationLeaf {

	private IAttributedElement element;
	final private EventBAttributesManager managerDo;
	private EventBAttributesManager managerUndo;
	// TODO effacer les attributs ( cree par execute ou redo ) lors du Undo
	private ArrayList<IAttributeType> attributeToDelete;

	public ChangeAttribute(EventBAttributesManager manager) {
		super("ChangeAttribute");
		this.managerDo = manager;
		attributeToDelete = new ArrayList<IAttributeType>();
	}

	public ChangeAttribute(IAttributedElement element,
			EventBAttributesManager manager) {
		super("ChangeAttribute");
		this.element = element;
		this.managerDo = manager;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		assert element != null;
		try {
			managerUndo = new EventBAttributesManager(element);
			attributeToDelete = new ArrayList<IAttributeType>();
			attributeToDelete.addAll(managerDo.getAttributeTypes());
			attributeToDelete.removeAll(managerUndo.getAttributeTypes());
		} catch (RodinDBException e) {
			e.printStackTrace();
			managerUndo = new EventBAttributesManager();
		}
		return computeSetAllAttributes(element, managerDo, monitor);
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return computeSetAllAttributes(element, managerDo, monitor);
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return computeSetAllAttributes(element, managerUndo, monitor);
	}

	private IStatus computeSetAllAttributes(IAttributedElement elem,
			EventBAttributesManager manager, IProgressMonitor monitor) {
		try {
			setAllAttributes(elem, manager, monitor);
			return Status.OK_STATUS;
		} catch (RodinDBException e) {
			// TODO Auto-generated catch block
			return e.getStatus();
		}
	}

	private void setAllAttributes(IAttributedElement element,
			EventBAttributesManager manager, IProgressMonitor monitor)
			throws RodinDBException {
		if (manager == managerUndo) {
			deleteAttributeToDelete(monitor);
		}
		setBooleanAttributes(element, manager.getBooleanAttribute(), monitor);
		setHandleAttributes(element, manager.getHandleAttribute(), monitor);
		setIntegerAttributes(element, manager.getIntegerAttribute(), monitor);
		setLongAttributes(element, manager.getLongAttribute(), monitor);
		setStringAttributes(element, manager.getStringAttribute(), monitor);
	}

	private void deleteAttributeToDelete(IProgressMonitor monitor) throws RodinDBException {
		for(IAttributeType type : attributeToDelete){
			element.removeAttribute(type, monitor);
		}
		
	}

	private void setBooleanAttributes(IAttributedElement element,
			ArrayList<Pair<IAttributeType.Boolean, Boolean>> list,
			IProgressMonitor monitor) throws RodinDBException {
		for (Pair<IAttributeType.Boolean, Boolean> pair : list) {
			if (!element.hasAttribute(pair.getFirst())) {
				attributeToDelete.add(pair.getFirst());
			} else {
				element.setAttributeValue(pair.getFirst(), pair.getSecond(),
						monitor);
			}
		}
	}

	private void setHandleAttributes(IAttributedElement element,
			ArrayList<Pair<IAttributeType.Handle, IRodinElement>> list,
			IProgressMonitor monitor) throws RodinDBException {
		for (Pair<IAttributeType.Handle, IRodinElement> pair : list) {

			element.setAttributeValue(pair.getFirst(), pair.getSecond(),
					monitor);
		}
	}

	private void setIntegerAttributes(IAttributedElement element,
			ArrayList<Pair<IAttributeType.Integer, Integer>> list,
			IProgressMonitor monitor) throws RodinDBException {
		for (Pair<IAttributeType.Integer, Integer> pair : list) {

			element.setAttributeValue(pair.getFirst(), pair.getSecond(),
					monitor);
		}
	}

	private void setLongAttributes(IAttributedElement element,
			ArrayList<Pair<IAttributeType.Long, Long>> list,
			IProgressMonitor monitor) throws RodinDBException {
		for (Pair<IAttributeType.Long, Long> pair : list) {

			element.setAttributeValue(pair.getFirst(), pair.getSecond(),
					monitor);
		}
	}

	private void setStringAttributes(IAttributedElement element,
			ArrayList<Pair<IAttributeType.String, String>> list,
			IProgressMonitor monitor) throws RodinDBException {
		for (Pair<IAttributeType.String, String> pair : list) {
			element.setAttributeValue(pair.getFirst(), pair.getSecond(),
					monitor);
		}
	}

	public void setParent(IInternalElement element) {
		this.element = element;
	}

}
