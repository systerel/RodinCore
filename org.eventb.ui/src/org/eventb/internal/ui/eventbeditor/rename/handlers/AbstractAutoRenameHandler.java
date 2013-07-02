/*******************************************************************************
 * Copyright (c) 2006, 2013 ETH Zurich and others.
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
 *     Systerel - now using new prefix preference mechanism
 *     Systerel - refactored to a handler
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.rename.handlers;

import static org.eventb.internal.ui.eventbeditor.operations.OperationFactory.renameElements;
import static org.eventb.internal.ui.preferences.PreferenceUtils.getAutoNamePrefix;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.eventb.ui.itemdescription.IAttributeDesc;
import org.eventb.ui.manipulation.IAttributeManipulation;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;

/**
 * Abstract class for automatic rename command handlers.
 */
public abstract class AbstractAutoRenameHandler extends AbstractHandler {

	private IEventBEditor<?> editor;

	protected abstract IInternalElementType<?> getType();
	
	/**
	 * TODO ECLIPSE4 /// Reuse evaluationContext to obtain the current Event-B
	 * editor. This implementation was chosen to ensure backward compatibility
	 * with Eclipse 3.7 and 3.8.
	 */
	@Override
	public void setEnabled(Object evaluationContext) {
		super.setEnabled(evaluationContext);
		final IWorkbenchWindow ww = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (ww == null) {
			return;
		}
		final IWorkbenchPage page = ww.getActivePage();
		if (page == null) {
			return;
		}
		final IEditorPart activeEditor = page.getActiveEditor();
		if (activeEditor instanceof IEventBEditor<?>) {
			editor = (IEventBEditor<?>) activeEditor;
		}
	}
	
	/**
	 * For testing purpose only.
	 */
	public void setEditor(IEventBEditor<?> editor) {
		this.editor = editor;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final IInternalElement root = editor.getRodinInput();
		final IInternalElementType<?> type = getType();
		final String prefix = getAutoNamePrefix(root, type);
		final IAttributeDesc desc = ElementDescRegistry.getInstance()
				.getElementDesc(type).getAutoNameAttribute();
		final IAttributeManipulation manip = desc.getManipulation();
		final AtomicOperation op = 	renameElements(root, type, manip, prefix);
		History.getInstance().addOperation(op);
		return null;
	}

}
