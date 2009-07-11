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
 *     Systerel - used ElementDescRegistry
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.elementdesc.IAttributeDesc;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

// TODO in the longer term, all subclasses shall disappear in favor of instances
public abstract class AutoElementNaming implements IEditorActionDelegate {

	private IEventBEditor<?> editor;
	private final PrefixElementName prefixElementName;

	public AutoElementNaming(PrefixElementName prefixElementName) {
		this.prefixElementName = prefixElementName;
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		if (targetEditor instanceof IEventBEditor<?>)
			editor = (IEventBEditor<?>) targetEditor;
	}

	public void selectionChanged(IAction action, ISelection selection) {
		return; // Do nothing
	}

	public void run(IAction action) {
		final IInternalElementType<?> type = prefixElementName.getElementType();
		final IInternalElement root = editor.getRodinInput();

		final String prefix = UIUtils.getAutoNamePrefix(root, type);
		final IAttributeDesc desc = ElementDescRegistry.getInstance()
				.getElementDesc(type).getAutoNameAttribute();

		History.getInstance().addOperation(
				OperationFactory.renameElements(root, type, desc.getManipulation(),
						prefix));
	}
}
