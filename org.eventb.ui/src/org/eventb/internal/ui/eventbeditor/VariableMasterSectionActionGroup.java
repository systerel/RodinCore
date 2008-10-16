/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
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
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the Variable
 *         Editable Tree Viewer.
 */
public class VariableMasterSectionActionGroup extends ActionGroup {

	// The Event-B Editor.
	IEventBEditor<IMachineRoot> editor;

	// The tree viewer in the master section
	TreeViewer viewer;

	// Some actions
	protected Action addVariable;

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
	public VariableMasterSectionActionGroup(IEventBEditor<IMachineRoot> eventBEditor,
			TreeViewer treeViewer) {
		this.editor = eventBEditor;
		this.viewer = treeViewer;

		// Add a variable.
		addVariable = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addVariable(editor, viewer);
			}
		};
		addVariable.setText("New &Variable");
		addVariable.setToolTipText("Create a new variable");
		addVariable.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_VARIABLES_PATH));

		// Delete the current selected element in the tree viewer.
		delete = new Action() {
			@Override
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
			@Override
			public void run() {
				EventBEditorUtils.handleUp(editor, viewer);
			}
		};
		handleUp.setText("&Up");
		handleUp.setToolTipText("Move the element up");
		handleUp.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_UP_PATH));

		// Handle the down action.
		handleDown = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.handleDown(editor, viewer);
			}
		};
		handleDown.setText("D&own");
		handleDown.setToolTipText("Move the element down");
		handleDown.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_DOWN_PATH));
	}

	/**
	 * Fill the context menu with the actions create initially.
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		ISelection sel = getContext().getSelection();
		menu.add(addVariable);
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
