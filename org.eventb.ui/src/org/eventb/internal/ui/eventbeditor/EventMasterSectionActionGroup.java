/*******************************************************************************
 * Copyright (c) 2005-2008 ETH Zurich.
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
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eventb.core.IEvent;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.eventbeditor.actions.ShowAbstractEventContribution;
import org.eventb.internal.ui.eventbeditor.actions.ShowAbstractInvariantContribution;
import org.eventb.internal.ui.eventbeditor.actions.ShowSeesContextContribution;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the Event
 *         Editable Tree Viewer.
 */
public class EventMasterSectionActionGroup extends
		MasterSectionActionGroup<EventBMachineEditor> {

	// Some actions
	protected Action addEvent;

	protected Action addLocalVariable;

	protected Action addGuard;

	protected Action addAction;

	protected Action delete;
	
	protected Action addRefinesEvent;
	
	protected Action addWitness;

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
	public EventMasterSectionActionGroup(EventBMachineEditor eventBEditor,
			TreeViewer treeViewer) {
		super(eventBEditor, treeViewer);

		// Add an event.
		addEvent = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addEvent(editor, viewer);
			}
		};
		addEvent.setText("New &Event");
		addEvent.setToolTipText("Create a new event");
		addEvent.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_EVENT_PATH));

		// Add a local variable.
		addLocalVariable = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addLocalVariable(editor, viewer);
			}
		};
		addLocalVariable.setText("New &Local Variable");
		addLocalVariable.setToolTipText("Create a new (local) variable");
		addLocalVariable.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_VARIABLES_PATH));

		// Add a guard.
		addGuard = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addGuard(editor, viewer);
			}
		};
		addGuard.setText("New &Guard");
		addGuard.setToolTipText("Create a new guard");
		addGuard.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_GUARD_PATH));

		// Add an action.
		addAction = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addAction(editor, viewer);
			}
		};
		addAction.setText("New &Action");
		addAction.setToolTipText("Create a new action");
		addAction.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_ACTION_PATH));

		// Add a refines event.
		addRefinesEvent = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addRefinesEvent(editor, viewer);
			}
		};
		addRefinesEvent.setText("New &Refine Event");
		addRefinesEvent.setToolTipText("Create a new refines event");
		addRefinesEvent.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_EVENT_PATH));
		
		// Add a refines event.
		addWitness = new Action() {
			@Override
			public void run() {
				EventBEditorUtils.addWitness(editor, viewer);
			}
		};
		addWitness.setText("New &Witness");
		addWitness.setToolTipText("Create a new witness");
		addWitness.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_EVENT_PATH));

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
				EventBEditorUtils.handleUp(viewer);
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
				EventBEditorUtils.handleDown(viewer);
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
		menu.add(addEvent);
		IRodinFile file = editor.getRodinInput();

		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) sel;

			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();

				if (obj instanceof IEvent) {
					menu.add(addLocalVariable);
					menu.add(addGuard);
					menu.add(addAction);
					menu.add(addRefinesEvent);
					menu.add(addWitness);
					// MenuManager newMenu = new MenuManager("&New");
					// newMenu.add(addLocalVariable);
					// newMenu.add(addGuard);
					// newMenu.add(addAction);
					// menu.add(newMenu);
				}
			}

			// Group for showing the corresponding abstract information
			menu.add(new Separator("Information"));

			// Contexts
			MenuManager submenu = new MenuManager("Sees Contexts");
			menu.add(submenu);
			submenu.add(new ShowSeesContextContribution(file));

			// Abstract event
			submenu = new MenuManager("Abstract Event");
			menu.add(submenu);
			if (ssel.size() == 1) {
				IRodinElement element = (IRodinElement) ssel.getFirstElement();
				submenu.add(new ShowAbstractEventContribution(element
						.getAncestor(IEvent.ELEMENT_TYPE)));
			}

			// Abstract invariants
			submenu = new MenuManager("Abstract Invariant");
			menu.add(submenu);
			submenu.add(new ShowAbstractInvariantContribution(file));

			if (!sel.isEmpty()) {
				menu.add(new Separator());
				menu.add(delete);
			}
		}
		// menu.add(deleteAction);
		// menu.add(new Separator());
		// drillDownAdapter.addNavigationActions(menu);

		// Other plug-ins can contribute there actions here
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
}
