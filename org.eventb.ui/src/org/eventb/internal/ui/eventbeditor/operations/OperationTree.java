package org.eventb.internal.ui.eventbeditor.operations;

import java.util.Collection;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.rodinp.core.IInternalElement;

interface OperationTree extends IUndoableOperation {
	// TODO changer par setCreatedElementParent
	void setParent(IInternalElement element) ;
	Collection<IInternalElement> getCreatedElements();
	IInternalElement getCreatedElement();
}
