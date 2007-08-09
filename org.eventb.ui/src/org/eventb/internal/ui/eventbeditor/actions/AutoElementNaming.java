package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.core.IIdentifierElement;
import org.eventb.core.ILabeledElement;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.editpage.AttributeRelUISpecRegistry;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public abstract class AutoElementNaming implements IEditorActionDelegate {

	IEventBEditor<?> editor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor)
			editor = (IEventBEditor<?>) targetEditor;
	}

	public abstract String getAttributeID();
	
	private void rename(final String prefix, final String attributeID) {
		try {
			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor monitor)
						throws RodinDBException {
					IRodinFile file = editor.getRodinInput();
					IInternalElementType<?> type = AttributeRelUISpecRegistry
							.getDefault().getType(attributeID);
					IInternalElement[] elements = file.getChildrenOfType(type);

					// Rename to the real desired naming convention
					for (int counter = 1; counter <= elements.length; counter++) {
						IInternalElement element = elements[counter - 1];
						if (isIdentifierAttribute(attributeID)) {
							if (EventBEditorUtils.DEBUG)
								EventBEditorUtils.debug("Rename: "
										+ ((IIdentifierElement) element)
												.getIdentifierString() + " to "
										+ prefix + +counter);
							((IIdentifierElement) element)
									.setIdentifierString(prefix + counter,
											new NullProgressMonitor());
						} else if (isLabelAttribute(attributeID)) {
							if (EventBEditorUtils.DEBUG)
								EventBEditorUtils.debug("Rename: "
										+ ((ILabeledElement) element)
												.getLabel() + " to " + prefix
										+ +counter);
							((ILabeledElement) element).setLabel(prefix
									+ counter, monitor);
						}
					}
				}

			}, null);
		} catch (RodinDBException e) {
			EventBUIExceptionHandler.handleSetAttributeException(e);
		}
	}

	boolean isLabelAttribute(String attributeID) {
		return isBelongToList(labelAttributes, attributeID);
	}

	boolean isIdentifierAttribute(String attributeID) {
		return isBelongToList(identifierAttributes, attributeID);
	}

	private boolean isBelongToList(String[] attributes,
			String attributeID) {
		for (String attribute : attributes)
			if (attribute.equals(attributeID))
				return true;
		return false;
	}

	protected String[] identifierAttributes = new String []{
			"org.eventb.core.variableIdentifier",
			"org.eventb.core.carrierSetIdentifier",
			"org.eventb.core.constantIdentifier",
	};
	
	protected String[] labelAttributes = new String []{
			"org.eventb.core.invariantLabel",
			"org.eventb.core.axiomLabel",
			"org.eventb.core.theoremLabel",
			"org.eventb.core.guardLabel",
			"org.eventb.core.eventLabel",
			"org.eventb.core.actionLabel",
	};
	
	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

	public void run(IAction action) {
		String attributeID = getAttributeID();
		IInternalElementType<?> type = AttributeRelUISpecRegistry.getDefault()
				.getType(attributeID);
		IRodinFile inputFile = editor.getRodinInput();
		String prefix = null;
		try {
			prefix = inputFile.getResource().getPersistentProperty(
					UIUtils.getQualifiedName(type));
		} catch (CoreException e) {
			EventBUIExceptionHandler.handleGetPersistentPropertyException(e);
		}

		if (prefix == null)
			prefix = AttributeRelUISpecRegistry.getDefault().getDefaultPrefix(
					attributeID);

		rename(prefix, attributeID);
	}
}
