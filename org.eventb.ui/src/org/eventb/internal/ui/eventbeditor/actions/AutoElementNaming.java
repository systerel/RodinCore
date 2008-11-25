/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.editpage.AttributeRelUISpecRegistry;
import org.eventb.internal.ui.eventbeditor.editpage.IAttributeFactory;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinFile;

public abstract class AutoElementNaming implements IEditorActionDelegate {

	IEventBEditor<?> editor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor)
			editor = (IEventBEditor<?>) targetEditor;
	}

	public abstract String getAttributeRelationshipID();
	
	private void rename(final String prefix, final String attributeID) {
		IInternalElement root = editor.getRodinInput();
		IInternalElementType<?> type = AttributeRelUISpecRegistry.getDefault()
				.getType(attributeID);
		IAttributeFactory factory = null;
		if (isIdentifierAttribute(attributeID)) {
			factory = UIUtils.getIdentifierAttributeFactory(type);
		} else if (isLabelAttribute(attributeID)) {
			factory = UIUtils.getLabelAttributeFactory(type);
		}
		if (factory != null) {
			History.getInstance().addOperation(
					OperationFactory
							.renameElements(root, type, factory, prefix));
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
		String attributeID = getAttributeRelationshipID();
		IInternalElementType<?> type = AttributeRelUISpecRegistry.getDefault()
				.getType(attributeID);
		IRodinFile inputFile = editor.getRodinInput().getRodinFile();
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
