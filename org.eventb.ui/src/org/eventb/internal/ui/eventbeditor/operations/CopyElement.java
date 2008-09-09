package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eventb.core.IAxiom;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IEvent;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.core.IVariant;
import org.eventb.internal.ui.EventBUtils;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IInternalParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;

public class CopyElement extends OperationLeaf {

	private final IRodinFile pasteInto;
	private final IRodinElement defaultParent;
	private IInternalElement source;
	private boolean first;
	// Operation to delete the copy. Use in undo
	private OperationTree operationDelete;

	public CopyElement(IRodinFile pasteInto, IRodinElement parent,
			IInternalElement source) {
		super("CopyElement");
		this.pasteInto = pasteInto;
		this.defaultParent = parent;
		this.source = source;
		first = true;
	}

	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		IInternalParent copyParent;
		String nameCopy;
		final OperationBuilder builder;
		final IInternalElement element;
		final String copyId;
		final IInternalElementType<?> copyType;

		try {
			copyParent = pasteInto;
			if (source instanceof IEvent) {
				copyId = "evt";
				copyType = IEvent.ELEMENT_TYPE;
			} else if (source instanceof IInvariant) {
				copyId = "inv";
				copyType = IInvariant.ELEMENT_TYPE;
			} else if (source instanceof ITheorem) {
				copyId = "thm";
				copyType = ITheorem.ELEMENT_TYPE;
			} else if (source instanceof IVariant) {
				copyId = "variant";
				copyType = IVariant.ELEMENT_TYPE;
			} else if (source instanceof IAxiom) {
				copyId = "axm";
				copyType = IAxiom.ELEMENT_TYPE;
			} else if (source instanceof IConstant) {
				copyId = "cst";
				copyType = IConstant.ELEMENT_TYPE;
			} else if (source instanceof ICarrierSet) {
				copyId = "set";
				copyType = ICarrierSet.ELEMENT_TYPE;

			} else if (source instanceof IVariable) {
				copyId = "set";
				copyType = IVariable.ELEMENT_TYPE;
			} else {
				copyId = "element";
				copyType = ((InternalElement) source).getElementType();
				copyParent = (IInternalParent) defaultParent;
			}

			nameCopy = copyId
					+ EventBUtils.getFreeChildNameIndex(copyParent, copyType,
							copyId);
			source.copy(copyParent, null, nameCopy, false, null);
			builder = new OperationBuilder();
			element = copyParent.getInternalElement(source.getElementType(),
					nameCopy);

			if (element != null) {
				operationDelete = builder.deleteElement(element);
				return Status.OK_STATUS;
			} else {
				operationDelete = null;
				return Status.OK_STATUS;
			}
		} catch (RodinDBException e) {
			operationDelete = null;
			return e.getStatus();
		}
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (operationDelete != null) {
			return operationDelete.undo(monitor, info);
		}
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		if (operationDelete != null) {
			if (first) {
				System.out.println("OperationDelete");
				first = false;
				return operationDelete.execute(monitor, info);
			} else {
				return operationDelete.redo(monitor, info);
			}
		}
		return Status.OK_STATUS;
	}

	public void setParent(IInternalElement element) {
		source = element;
	}

}
