/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced local variable by parameter
 *     Systerel - separation of file and root element
 *     Systerel - prevented from editing generated elements
 *******************************************************************************/
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
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.eventbeditor.actions.ShowAbstractEventContribution;
import org.eventb.internal.ui.eventbeditor.actions.ShowAbstractInvariantContribution;
import org.eventb.internal.ui.eventbeditor.actions.ShowSeesContextContribution;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;

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

	protected Action addParameter;

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
		addEvent = new SynthesisAction() {
			@Override
			public void run() {
				if (checkReadOnly(editor.getRodinInput())) {
					return;
				}
				EventBEditorUtils.addEvent(editor, viewer);
			}
		};
		addEvent.setText("New &Event");
		addEvent.setToolTipText("Create a new event");
		addEvent.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_EVENT_PATH));

		// Add a refines event.
		addRefinesEvent = new SynthesisAction() {
			@Override
			public void run() {
				if (checkReadOnly(viewer)) {
					return;
				}
				EventBEditorUtils.addRefinesEvent(editor, viewer);
			}
		};
		addRefinesEvent.setText("New &Refines Event");
		addRefinesEvent.setToolTipText("Create a new refines event");
		addRefinesEvent.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_EVENT_PATH));
		
		// Add a witness.
		addWitness = new SynthesisAction() {
			@Override
			public void run() {
				if (checkReadOnly(viewer)) {
					return;
				}
				EventBEditorUtils.addWitness(editor, viewer);
			}
		};
		addWitness.setText("New &Witness");
		addWitness.setToolTipText("Create a new witness");
		addWitness.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_EVENT_PATH));

		// Add a parameter.
		addParameter = new SynthesisAction() {
			@Override
			public void run() {
				if (checkReadOnly(viewer)) {
					return;
				}
				EventBEditorUtils.addParameter(editor, viewer);
			}
		};
		addParameter.setText("New &Parameter");
		addParameter.setToolTipText("Create a new parameter");
		addParameter.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_VARIABLES_PATH));

		// Add a guard.
		addGuard = new SynthesisAction() {
			@Override
			public void run() {
				if (checkReadOnly(viewer)) {
					return;
				}
				EventBEditorUtils.addGuard(editor, viewer);
			}
		};
		addGuard.setText("New &Guard");
		addGuard.setToolTipText("Create a new guard");
		addGuard.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_GUARD_PATH));

		// Add an action.
		addAction = new SynthesisAction() {
			@Override
			public void run() {
				if (checkReadOnly(viewer)) {
					return;
				}
				EventBEditorUtils.addAction(editor, viewer);
			}
		};
		addAction.setText("New &Action");
		addAction.setToolTipText("Create a new action");
		addAction.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_ACTION_PATH));

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
		IMachineRoot root = editor.getRodinInput();

		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			if (!sel.isEmpty()) {
				menu.add(delete);
			}
			
			menu.add(new Separator());
			menu.add(addEvent);
			
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();

				if (obj instanceof IEvent) {
					menu.add(new Separator());
					menu.add(addRefinesEvent);
					menu.add(addParameter);
					menu.add(addGuard);
					menu.add(addWitness);
					menu.add(addAction);
				}
			}

			// Group for showing the corresponding abstract information
			menu.add(new Separator("Information"));

			// Contexts
			MenuManager submenu = new MenuManager("Sees Contexts");
			menu.add(submenu);
			submenu.add(new ShowSeesContextContribution(root));

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
			submenu.add(new ShowAbstractInvariantContribution(root));
		}
		// menu.add(deleteAction);
		// menu.add(new Separator());
		// drillDownAdapter.addNavigationActions(menu);

		// Other plug-ins can contribute there actions here
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
}
