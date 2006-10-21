/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eventb.internal.ui.EventBImage;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the Invariant
 *         Editable Tree Viewer.
 */
public class InvariantMasterSectionActionGroup extends ActionGroup {

	// The Event-B Editor.
	private EventBEditor editor;

	// The tree viewer in the master section
	private TreeViewer viewer;

	// Some actions
	protected Action addInvariant;

	protected Action delete;

	protected Action handleUp;

	protected Action handleDown;

	/**
	 * Constructor: Create the actions.
	 * <p>
	 * 
	 * @param eventBEditor
	 *            The Event-B Editor
	 * @param treeViewer
	 *            The tree viewer associated with this action group
	 */
	public InvariantMasterSectionActionGroup(EventBEditor eventBEditor,
			TreeViewer treeViewer) {
		this.editor = eventBEditor;
		this.viewer = treeViewer;

		// Add an invariant.
		addInvariant = new Action() {
			public void run() {
				EventBEditorUtils.addInvariant(editor, viewer);
			}
		};
		addInvariant.setText("New &Invariant");
		addInvariant.setToolTipText("Create a new invariant");
		addInvariant.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_INVARIANTS_PATH));

		// Delete the current selected element in the tree viewer.
		delete = new Action() {
			public void run() {
				EventBEditorUtils.deleteElements(viewer);
			}
		};
		delete.setText("&Delete");
		delete.setToolTipText("Delete selected element");
		delete.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		// Handle the up action.
		handleUp = new Action() {
			public void run() {
				EventBEditorUtils.handleUp(viewer);
			}
		};
		handleUp.setText("&Up");
		handleUp.setToolTipText("Move the element up");
		handleUp.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_UP_PATH));

		// Handle the down action.
		handleDown = new Action() {
			public void run() {
				EventBEditorUtils.handleDown(viewer);
			}
		};
		handleDown.setText("D&own");
		handleDown.setToolTipText("Move the element down");
		handleDown.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_DOWN_PATH));
	}

	/**
	 * Fill the context menu with the actions create initially.
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		ISelection sel = getContext().getSelection();
		menu.add(new Separator());
		menu.add(addInvariant);
		if (!sel.isEmpty()) {
			menu.add(new Separator());
			menu.add(delete);
		}
		// Other plug-ins can contribute there actions here
		// These actions are added by extending the extension point
		// org.eventb.ui.popup
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}
