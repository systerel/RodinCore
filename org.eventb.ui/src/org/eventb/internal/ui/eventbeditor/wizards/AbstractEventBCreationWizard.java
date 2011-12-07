/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.wizards;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.eventbeditor.dialogs.EventBDialog;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;

/**
 * This class synthesizes the editor wizard to create EventB elements.
 * Subclasses have to override
 * {@link #createDialog(AbstractEventBCreationWizard, IEventBRoot, Shell)}
 * method in order to specify the dialog to be displayed. Subclasses have to
 * override {@link #addValue(IEventBRoot, EventBDialog)} to handle the
 * information at dialog close and create corresponding EventB elements. The
 * method {@link #openDialog(IEventBEditor)} is specific to
 * {@link IEventBEditor} as the method
 * {@link IEventBEditor#addNewElement(org.rodinp.core.IRodinElement)} is called.
 * 
 * @author Thomas Muller
 */
public abstract class AbstractEventBCreationWizard {

	/**
	 * Creates a dialog to assist element creation.
	 * 
	 * @param wizard
	 *            the owner of the dialog
	 * @param root
	 *            the root element for which elements will be created
	 * @param shell
	 *            the current shell for the dialog
	 * 
	 * @return the dialog to assist element creation
	 */
	public abstract EventBDialog createDialog(
			AbstractEventBCreationWizard wizard, IEventBRoot root, Shell shell);

	/**
	 * Method retrieving the informations from the given dialog and creates the
	 * intended elements in the given root element.
	 * 
	 * @param root
	 *            the root element for which elements will be created
	 * @param dialog
	 *            the dialog that originated the element creation
	 * @return the atomic operation corresponding to the element creation that
	 *         has been created
	 */
	public abstract AtomicOperation addValue(IEventBRoot root,
			EventBDialog dialog);

	/**
	 * Open a wizard to assist the creation of an EventB element for a given
	 * {@link IEventBEditor} and handles the element creation by calling back
	 * the editor.
	 * 
	 * @param editor
	 *            the {@link IEventBEditor} to be called back
	 * @return the atomic operation performed
	 */
	public AtomicOperation openDialog(IEventBEditor<?> editor) {
		final AtomicOperation op = openDialog(
				(IEventBRoot) editor.getRodinInput(), editor.getSite()
						.getShell(), History.getInstance());
		if (op != null)
			addNewElement(editor, op);
		return op;
	}

	/**
	 * Open a wizard to assist the creation of a element of a given root for a
	 * given undo-redo managing history
	 * 
	 * @param root
	 *            the root element for which elements will be created
	 * @param history
	 *            the undo-redo managing history to which the element creation
	 *            atomic operation is added
	 * @return the atomic operation corresponding to the element creation
	 */
	public AtomicOperation openDialog(IEventBRoot root, Shell activeShell,
			History history) {
		final EventBDialog dialog = createDialog(this, root, activeShell);
		dialog.open();
		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return null; // Cancel
		final AtomicOperation op = addValue(root, dialog);
		if (op != null) {
			history.addOperation(op);
		}
		return op;
	}

	/**
	 * Add the element concerned by the operation to the given editor via the
	 * call-back method
	 * {@link IEventBEditor#addNewElement(org.rodinp.core.IRodinElement)}
	 * 
	 * @param editor
	 *            the editor to add the element to
	 * @param op
	 *            the corresponding atomic operation performed
	 */
	private static void addNewElement(IEventBEditor<?> editor,
			AtomicOperation op) {
		final IInternalElement element = op.getCreatedElement();
		if (element != null) {
			editor.addNewElement(element);
		}
	}

}
