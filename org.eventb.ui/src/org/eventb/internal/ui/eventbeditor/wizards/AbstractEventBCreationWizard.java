/*******************************************************************************
 * Copyright (c) 2011, 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor.wizards;

import static org.eventb.core.EventBAttributes.GENERATED_ATTRIBUTE;
import static org.eventb.internal.ui.UIUtils.log;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IEventBRoot;
import org.eventb.internal.ui.eventbeditor.dialogs.EventBDialog;
import org.eventb.internal.ui.eventbeditor.operations.AtomicOperation;
import org.eventb.internal.ui.eventbeditor.operations.History;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.RodinElement;

/**
 * This class synthesizes the editor wizard to create EventB elements.
 * Subclasses have to override {@link #createDialog(IEventBRoot, Shell)} method
 * in order to specify the dialog to be displayed. Subclasses have to override
 * {@link #getCreationOperation(IEventBRoot, EventBDialog)} to handle the
 * information at dialog close and create corresponding EventB elements. The
 * method {@link #getAndRegisterCreationOperation(EventBDialog)} tries to
 * register the new element if there is any {@link IEventBEditor} linked. See
 * also {@link IEventBEditor#addNewElement(org.rodinp.core.IRodinElement)} is
 * called.
 * 
 * @author Thomas Muller
 */
public abstract class AbstractEventBCreationWizard {

	private IEventBEditor<?> fEditor;

	/**
	 * Creates a dialog to assist element creation.
	 * 
	 * @param root
	 *            the root element for which elements will be created
	 * @param shell
	 *            the current shell for the dialog
	 * 
	 * @return the dialog to assist element creation
	 */
	protected abstract EventBDialog createDialog(IEventBRoot root, Shell shell);

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
	public abstract AtomicOperation getCreationOperation(IEventBRoot root,
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
		fEditor = editor;
		final AtomicOperation op = openDialog(
				(IEventBRoot) editor.getRodinInput(), editor.getSite()
						.getShell());
		return op;
	}

	/**
	 * Open a wizard to assist the creation of a element of a given root for a
	 * given undo-redo managing history
	 * 
	 * @param root
	 *            the root element for which elements will be created
	 * @param shell
	 *            the parent shell
	 * @return the atomic operation corresponding to the element creation
	 */
	public AtomicOperation openDialog(IEventBRoot root, Shell shell) {
		final EventBDialog dialog = createDialog(root, shell);
		dialog.open();
		if (dialog.getReturnCode() == InputDialog.CANCEL)
			return null; // Cancel
		final AtomicOperation op = getAndRegisterCreationOperation(dialog);
		return op;
	}

	/**
	 * Gets the atomic operation corresponding to the element creation and
	 * registers it in the history associated with this wizard. It attempts to
	 * register the corresponding new element in the current IEventBEditor if it
	 * exists.
	 * 
	 * @param dialog
	 *            the dialog requesting the creation operation and having the
	 *            necesseray information to do it
	 * 
	 * @return the created operation
	 */
	public AtomicOperation getAndRegisterCreationOperation(EventBDialog dialog) {
		final AtomicOperation op = getCreationOperation(dialog.getRoot(),
				dialog);
		History.getInstance().addOperation(op);
		if (fEditor != null) {
			addNewElement(fEditor, op);
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

	/**
	 * Tells if creation is available for the given <code>root</code>. Event-B
	 * roots which are generated or read-only do not allow creation of further
	 * element from the UI, and thus creation possibilities shall be disabled.
	 *
	 * @param root
	 *            the root element for which wizards should allow element
	 *            creation
	 * @return <code>true</code> if the creation is possible, <code>false</code>
	 *         otherwise.
	 */
	public static boolean isCreationAllowed(IEventBRoot root) {
		try {
			return !root.isReadOnly()
					&& !root.hasAttribute(GENERATED_ATTRIBUTE);
		} catch (RodinDBException e) {
			log(e,
					"Problem occurred while retrieving "
							+ "the generated attribute for "
							+ ((RodinElement) root).toStringWithAncestors());
		}
		return false;
	}

}
