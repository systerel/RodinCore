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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISources;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinDBException;

/**
 * Adds a new element after the currently selected element in the active
 * EventBEditor.
 * 
 * @author vgheorgh
 */
public class CreateElementHandler extends AbstractHandler {

	/**
	 * @param selection 
	 * @return a valid insertion point corresponding to the current selection,
	 *         or null
	 * 
	 *         The given selection must contain a single object.
	 * 
	 *         A valid insertion point is: - an IInternalElement - that has a
	 *         parent which is also an IInternalElement
	 * 
	 *         Note. This does not include the condition that the parent is not
	 *         read-only.
	 * 
	 */
	public static IInternalElement insertionPointForSelection(ISelection selection) {

		// If there is no selection or selection is empty then return null.
		if (selection == null || selection.isEmpty()) {
			return null;
		}

		final IInternalElement insertionPoint;
		
		if (selection instanceof IInternalElement) {
			insertionPoint = (IInternalElement) selection;
		} else if (selection instanceof IStructuredSelection) {

			final IStructuredSelection ssel = (IStructuredSelection) selection;

			if (ssel.size() > 0) {
				Object last = ssel.toArray()[ssel.size()-1];

				insertionPoint = last instanceof IInternalElement ? 
						(IInternalElement) last : null;
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

	/**
	 *  Create new element.
	 * 
	 * @param parent Parent of the new element
	 * @param type Type of the new element
	 * @param insertionPoint An existing child of parent, after which the new element will be added. May be null.
	 * 
	 * @throws RodinDBException
	 * 
	 * @pre If insertionPoint is not null, then it must have the same type as the new element.  
	 * 
	 */
	static public void doExecute(IInternalElement parent, IInternalElementType<? extends IInternalElement> type,
			IInternalElement insertionPoint) throws RodinDBException {

		// Check preconditions
		if( ! ( insertionPoint==null || insertionPoint.getElementType().equals(type) )) {
			
			IStatus status = new Status(IStatus.ERROR, EventBUIPlugin.PLUGIN_ID,
					"o.e.i.u.eventbeditor.handlers.CreateElementHandler : invalid call");
			UIUtils.log(status);
			return;
		}
		
		// handle read-only model
		if (EventBEditorUtils.checkAndShowReadOnly(parent)) {
			return;
		}
		
		final IInternalElement sibling = insertionPoint==null ?
				null
				: insertionPoint.getNextSibling();
		
		// perform creation
		final AtomicOperation operation = 
				OperationFactory.createElementGeneric(parent,type,sibling);

		History.getInstance().addOperation(operation);
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		// Get the selection from the current active workbench page.
		final ISelection selection = (ISelection) HandlerUtil.getVariable(
				event.getApplicationContext(), ISources.ACTIVE_CURRENT_SELECTION_NAME);

		final IInternalElement insertionPoint = insertionPointForSelection(selection);

		if (insertionPoint == null) {
			throw new ExecutionException("invalid selection");
		}

		final IInternalElement parent = (IInternalElement) insertionPoint.getParent();

		try {
			doExecute(parent, insertionPoint.getElementType(), insertionPoint);
		} catch (RodinDBException e) {
			throw new ExecutionException("internal error", e);
		}

		return null;
	}

}
