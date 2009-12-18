/*******************************************************************************
 * Copyright (c) 2006, 2009 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - added history support
 *     Systerel - added check before pasting element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.handlers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eventb.internal.ui.RodinHandleTransfer;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.internal.ui.eventbeditor.elementdesc.ElementDescRegistry;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.internal.ui.eventbeditor.operations.OperationFactory;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IElementType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         An extension of {@link AbstractHandler} for handling Paste action.
 */
public class PasteHandler extends AbstractHandler implements IHandler {

	public Object execute(ExecutionEvent arg0) throws ExecutionException {

		// Get the current selection from the active page.
		IWorkbenchPage activePage = EventBUIPlugin.getActivePage();
		ISelection selection = activePage.getSelection();
		
		// Do nothing if there is no selection.
		if (selection == null) {
			if (EventBEditorUtils.DEBUG) {
				EventBEditorUtils.debug("Paste action: Current active page is "
						+ activePage);
			}
			return "Must have a selection to paste";
		}

		// Create the clipboard associated with the workbench.
		IWorkbench workbench = EventBUIPlugin.getDefault().getWorkbench();
		Clipboard clipboard = new Clipboard(workbench.getDisplay());

		// Try to handle by using a rodin handle transfer.
		RodinHandleTransfer rodinHandleTransfer = RodinHandleTransfer
				.getInstance();
		final IRodinElement[] elements = (IRodinElement[]) clipboard
				.getContents(rodinHandleTransfer);

		// There is no data in the clipboard for rodin handle transfer then do nothing.
		if (elements == null)
			return "Nothing to paste";
		
		// Check for the existing of the elements to be pasted.
		for (IRodinElement element : elements) {
			if (!element.exists()) {
				openError(Messages.bind(Messages.dialogs_nothingToPaste,
						element));
			}
		}
		
		// Get the target from the current selection.
		IStructuredSelection ssel = (IStructuredSelection) selection;
		final IRodinElement target = getTarget(ssel);
		if (!(target instanceof IInternalElement) || !target.exists())
			return "Target does not exist";

		if (EventBEditorUtils.checkAndShowReadOnly(target)) {
			return null;
		}

		final IElementType<?> typeNotAllowed = elementTypeNotAllowed(
				elements, target);
		if (typeNotAllowed == null) {
			copyElements(elements, target);
		} else if (haveSameType(elements, target)) {
			copyElements(elements, target.getParent());
		} else {
			openError(Messages
					.bind(Messages.dialogs_pasteNotAllowed, typeNotAllowed
							.getName(), target.getElementType().getName()));
			return null;
		}
		if (EventBEditorUtils.DEBUG)
			EventBEditorUtils.debug("PASTE SUCCESSFULLY");
		return null;
	}
	
	/**
	 * Returns the actual target of the paste action. Returns null if no valid
	 * target is selected.
	 * 
	 * @return the actual target of the paste action
	 */
	private IRodinElement getTarget(IStructuredSelection selection) {
		if (selection.size() == 0) {
			return getRodinInput();
		}
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof IRodinElement)
			return (IRodinElement) firstElement;

		return null;
	}

	/**
	 * Returns the Rodin input of the active editor or null if the editor is not
	 * an IEventBEditor.
	 * 
	 * @return the Rodin input of the active editor or null if there is not.
	 * */
	private IInternalElement getRodinInput() {
		IEditorPart editor = EventBUIPlugin.getActivePage().getActiveEditor();
		if (editor instanceof IEventBEditor<?>) {
			return ((IEventBEditor<?>) editor).getRodinInput();
		} else {
			return null;
		}
	}

	/**
	 * Returns the type of an element that is not allowed to be pasted as child
	 * of target.
	 * 
	 * @return the type that is not allowed to be pasted or <code>null</code> if
	 *         all elements to paste can become valid children
	 * */
	private static IElementType<?> elementTypeNotAllowed(
			IRodinElement[] toPaste, IRodinElement target) {
		final Set<IElementType<?>> allowedTypes = getAllowedChildTypes(target);
		for (IRodinElement e : toPaste) {
			final IElementType<?> type = e.getElementType();
			if (!allowedTypes.contains(type)) {
				return type;
			}
		}
		return null;
	}

	private static Set<IElementType<?>> getAllowedChildTypes(
			IRodinElement target) {
		final IElementType<?> targetType = target.getElementType();
		final IElementType<?>[] childTypes = ElementDescRegistry.getInstance()
				.getChildTypes(targetType);
		final Set<IElementType<?>> allowedTypes = new HashSet<IElementType<?>>(
				Arrays.asList(childTypes));
		return allowedTypes;
	}
	
	private static boolean haveSameType(IRodinElement[]toPaste, IRodinElement target){
		final IElementType<?> targetType = target.getElementType();
		for (IRodinElement e : toPaste) {
			if (targetType != e.getElementType()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Opens an Error Dialog to display the message.
	 */
	private static void openError(String message) {
		final Shell shell = EventBUIPlugin.getActiveWorkbenchShell();
		MessageDialog.openError(shell, "Cannot Paste", message);
	}
	
	/**
	 * Perform a copy operation through the undo history.
	 */
	private static void copyElements(IRodinElement[] handleData,
			IRodinElement target) {
		History.getInstance().addOperation(
				OperationFactory.copyElements((IInternalElement) target,
						handleData));
	}
	
}
