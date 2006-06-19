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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eventb.core.IEvent;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBImageDescriptor;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the Synthetic
 *         Machine Editable Tree Viewer.
 */
public class SyntheticMachineMasterSectionActionGroup extends ActionGroup {

	// The Event-B Editor.
	private EventBEditor editor;

	// The tree viewer in the master section
	private TreeViewer viewer;

	// Some actions
	protected Action addVariable;

	protected Action addInvariant;

	protected Action addTheorem;

	protected Action addEvent;

	protected Action addLocalVariable;

	protected Action addGuard;

	protected Action addAction;

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
	public SyntheticMachineMasterSectionActionGroup(EventBEditor eventBEditor,
			TreeViewer treeViewer) {
		this.editor = eventBEditor;
		this.viewer = treeViewer;

		// Add a variable.
		addVariable = new Action() {
			public void run() {
				EventBEditorUtils.addVariable(editor, viewer);
			}
		};
		addVariable.setText("New &Variable");
		addVariable.setToolTipText("Create a new variable");
		addVariable.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_VARIABLES_PATH));

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

		// Add an event.
		addEvent = new Action() {
			public void run() {
				EventBEditorUtils.addEvent(editor, viewer);
			}
		};
		addEvent.setText("New &Event");
		addEvent.setToolTipText("Create a new event");
		addEvent.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_EVENT_PATH));

		// Add a local variable.
		addLocalVariable = new Action() {
			public void run() {
				EventBEditorUtils.addLocalVariable(editor, viewer);
			}
		};
		addLocalVariable.setText("New &Local Variable");
		addLocalVariable.setToolTipText("Create a new (local) variable");
		addLocalVariable.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_VARIABLES_PATH));

		// Add a guard.
		addGuard = new Action() {
			public void run() {
				EventBEditorUtils.addGuard(editor, viewer);
			}
		};
		addGuard.setText("New &Guard");
		addGuard.setToolTipText("Create a new guard");
		addGuard.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_GUARD_PATH));

		// Add an action.
		addAction = new Action() {
			public void run() {
				EventBEditorUtils.addAction(editor, viewer);
			}
		};
		addAction.setText("New &Action");
		addAction.setToolTipText("Create a new action");
		addAction.setImageDescriptor(EventBImage
				.getImageDescriptor(EventBImage.IMG_NEW_ACTION_PATH));

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
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();

				if (obj instanceof IEvent) {
					menu.add(addLocalVariable);
					menu.add(addGuard);
					menu.add(addAction);
				}
			}
		}
		menu.add(new Separator());
		menu.add(addVariable);
		menu.add(addInvariant);
		menu.add(addTheorem);
		menu.add(addEvent);
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
