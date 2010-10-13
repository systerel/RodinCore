/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - added history support
 *     Systerel - separation of file and root element
 *     Systerel - prevented from editing generated elements
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
import org.eventb.core.IContextRoot;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the Constant
 *         Editable Tree Viewer.
 */
public class ConstantMasterSectionActionGroup extends ActionGroup {

	// The Event-B Editor.
	IEventBEditor<IContextRoot> editor;

	// The tree viewer in the master section
	TreeViewer viewer;

	// Some actions
	protected Action addConstant;

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
	public ConstantMasterSectionActionGroup(IEventBEditor<IContextRoot> eventBEditor,
			TreeViewer treeViewer) {
		this.editor = eventBEditor;
		this.viewer = treeViewer;

		// Add a constant.
		addConstant = new SynthesisAction() {
			@Override
			public void run() {
				if (checkReadOnly(editor.getRodinInput())) {
					return;
				}
				EventBEditorUtils.addConstant(editor, viewer);
			}
		};
		addConstant.setText("New &Constant");
		addConstant.setToolTipText("Create a new constant");
		addConstant.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_CONSTANTS_PATH));

		// Delete the current selected element in the tree viewer.
		delete = new SynthesisAction() {
			@Override
			public void run() {
				if (checkReadOnly(viewer)) {
					return;
				}
				EventBEditorUtils.deleteElements(viewer);
			}
		};
		delete.setText("&Delete");
		delete.setToolTipText("Delete selected element");
		delete.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));

		// Handle the up action.
		handleUp = new SynthesisAction() {
			@Override
			public void run() {
				if (checkReadOnly(viewer)) {
					return;
				}
				EventBEditorUtils.handleUp(editor, viewer);
			}
		};
		handleUp.setText("&Up");
		handleUp.setToolTipText("Move the element up");
		handleUp.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_UP_PATH));

		// Handle the down action.
		handleDown = new SynthesisAction() {
			@Override
			public void run() {
				if (checkReadOnly(viewer)) {
					return;
				}
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
		if (!sel.isEmpty()) {
			menu.add(delete);
		}
		menu.add(new Separator());
		menu.add(addConstant);
		// Other plug-ins can contribute there actions here
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}
