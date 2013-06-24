/*******************************************************************************
 * Copyright (c) 2013 vgheorgh and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     vgheorgh - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * Adds a new element after the currently selected element in the active
 * EventBEditor.
 * 
 * @author vgheorgh
 */
public class CreateElementHandler extends AbstractHandler {

	/**
	 * 
	 * @return a valid insertion point corresponding to the current selection,
	 *         or null
	 * 
	 *         The current selection must contain a single object.
	 * 
	 *         A valid insertion point is: - an IInternalElement - that has a
	 *         parent which is also an IInternalElement
	 * 
	 *         Note. This does not include the condition that the parent is not
	 *         read-only.
	 * 
	 */
	private static IInternalElement insertionPointForSelection(Object context) {

		// Get the selection from the current active workbench page.
		final ISelection selection = (ISelection) HandlerUtil.getVariable(
				context, ISources.ACTIVE_CURRENT_SELECTION_NAME);

		// If there is no selection or selection is empty then do nothing.
		if (selection == null || selection.isEmpty()) {
			return null;
		}

		final IInternalElement insertionPoint;
		if (selection instanceof IInternalElement) {
			insertionPoint = (IInternalElement) selection;
		} else if (selection instanceof IStructuredSelection) {

			final IStructuredSelection ssel = (IStructuredSelection) selection;

			if (ssel.size() == 1
					&& ssel.getFirstElement() instanceof IInternalElement) {
				insertionPoint = (IInternalElement) ssel.getFirstElement();
			} else {
				return null;
			}
		} else {
			return null;
		}

		// parent must be an internal element
		if (!(insertionPoint.getParent() != null && insertionPoint.getParent() instanceof IInternalElement)) {
			return null;
		}

		return insertionPoint;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final IInternalElement insertionPoint = insertionPointForSelection(event
				.getApplicationContext());

		if (insertionPoint == null) {
			throw new ExecutionException("invalid selection");
		}

		final IInternalElement parent = (IInternalElement) insertionPoint
				.getParent();

		// handle read-only model
		if (EventBEditorUtils.checkAndShowReadOnly(parent)) {
			return null;
		}

		try {

			// perform creation
			final AtomicOperation operation = OperationFactory
					.createElementGeneric(parent,
							insertionPoint.getElementType(),
							insertionPoint.getNextSibling());

			History.getInstance().addOperation(operation);

		} catch (RodinDBException e) {
			throw new ExecutionException("internal error", e);
		}

		return null;
	}

}
