package org.eventb.internal.ui.eventbeditor.operations;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.rodinp.core.IInternalElement;

interface OperationTree extends IUndoableOperation {
	// effectuer une ou plusieurs commande
	void setParent(IInternalElement element) ;
}
