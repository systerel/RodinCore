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
import org.eventb.internal.ui.EventBImageDescriptor;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the Synthetic
 *         Context Editable Tree Viewer.
 */
public class SyntheticContextMasterSectionActionGroup extends ActionGroup {

	// The Event-B Editor.
	private EventBEditor editor;

	// The tree viewer in the master section
	private TreeViewer viewer;

	// Some actions
	protected Action addTheorem;

	protected Action addSet;

	protected Action addConstant;

	protected Action addAxiom;

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
	public SyntheticContextMasterSectionActionGroup(EventBEditor eventBEditor,
			TreeViewer treeViewer) {
		this.editor = eventBEditor;
		this.viewer = treeViewer;

		// Add a theorem.
		addTheorem = new Action() {
			public void run() {
				EventBEditorUtils.addTheorem(editor, viewer);
			}
		};
		addTheorem.setText("New &Theorem");
		addTheorem.setToolTipText("Create a new theorem");
		addTheorem.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_THEOREMS_PATH));

		// Add a carrier set.
		addSet = new Action() {
			public void run() {
				EventBEditorUtils.addSet(editor, viewer);
			}
		};
		addSet.setText("New Carrier &Set");
		addSet.setToolTipText("Create a new carrier set");
		addSet.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_CARRIER_SETS_PATH));

		// Add a constant.
		addConstant = new Action() {
			public void run() {
				EventBEditorUtils.addConstant(editor, viewer);
			}
		};
		addConstant.setText("New &Constant");
		addConstant.setToolTipText("Create a new constant");
		addConstant.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_CONSTANTS_PATH));

		// Add an axiom.
		addAxiom = new Action() {
			public void run() {
				EventBEditorUtils.addAxiom(editor, viewer);
			}
		};
		addAxiom.setText("New &Axiom");
		addAxiom.setToolTipText("Create a new axiom");
		addAxiom.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_AXIOMS_PATH));

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
		handleUp.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		// Handle the down action.
		handleDown = new Action() {
			public void run() {
				EventBEditorUtils.handleDown(viewer);
			}
		};
		handleDown.setText("D&own");
		handleDown.setToolTipText("Move the element down");
		handleDown.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));
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
		menu.add(addSet);
		menu.add(addConstant);
		menu.add(addAxiom);
		menu.add(addTheorem);
		if (!sel.isEmpty()) {
			menu.add(new Separator());
			menu.add(delete);
		}
		// Other plug-ins can contribute there actions here
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

}
