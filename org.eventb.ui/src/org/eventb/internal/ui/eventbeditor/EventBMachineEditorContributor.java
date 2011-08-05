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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;

/**
 * @author htson
 *         <p>
 *         Manages the installation/deinstallation of global actions for
 *         multi-page editors. Responsible for the redirection of global actions
 *         to the active editor. Multi-page contributor replaces the
 *         contributors for the individual editors in the multi-page editor.
 */
public class EventBMachineEditorContributor extends EventBEditorContributor {

	// A set of global actions
//	public static Action newVariables;
//
//	public static Action newInvariants;
//
//	public static Action newTheorems;
//
//	public static Action newEvent;

	/**
	 * Creates a multi-page contributor.
	 */
	public EventBMachineEditorContributor() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBEditorContributor#createActions()
	 */
	@Override
	protected void createActions() {
		super.createActions();

		// New Variables.
//		newVariables = new Action() {
//			public void run() {
//				IEditorPart part = EventBUIPlugin.getActivePage()
//						.getActiveEditor();
//				if (part instanceof EventBEditor) {
//					EventBEditor editor = (EventBEditor) part;
//					IRodinFile rodinFile = editor.getRodinInput();
//					UIUtils.intelligentNewVariables(editor, rodinFile);
//					editor.editorDirtyStateChanged();
//				}
//			}
//		};
//		newVariables.setText("New Variables");
//		newVariables.setToolTipText("Create new variables for the component");
//		newVariables.setImageDescriptor(EventBImage
//				.getImageDescriptor(EventBImage.IMG_NEW_VARIABLES_PATH));
//
//		// New Invariants.
//		newInvariants = new Action() {
//			public void run() {
//				IEditorPart part = EventBUIPlugin.getActivePage()
//						.getActiveEditor();
//				if (part instanceof EventBEditor) {
//					EventBEditor editor = (EventBEditor) part;
//					IRodinFile rodinFile = editor.getRodinInput();
//					UIUtils.newInvariants(editor, rodinFile);
//				}
//			}
//		};
//		newInvariants.setText("New Invariants");
//		newInvariants.setToolTipText("Create new invariants for the component");
//		newInvariants.setImageDescriptor(EventBImage
//				.getImageDescriptor(EventBImage.IMG_NEW_INVARIANTS_PATH));
//
//		// New Theorems.
//		newTheorems = new Action() {
//			public void run() {
//				IEditorPart part = EventBUIPlugin.getActivePage()
//						.getActiveEditor();
//				if (part instanceof EventBEditor) {
//					EventBEditor editor = (EventBEditor) part;
//					IRodinFile rodinFile = editor.getRodinInput();
//					UIUtils.newTheorems(editor, rodinFile);
//				}
//			}
//		};
//		newTheorems.setText("New Theorems");
//		newTheorems.setToolTipText("Create new theorems for the component");
//		newTheorems.setImageDescriptor(EventBImage
//				.getImageDescriptor(EventBImage.IMG_NEW_THEOREMS_PATH));
//
//		// New Events.
//		newEvent = new Action() {
//			public void run() {
//				IEditorPart part = EventBUIPlugin.getActivePage()
//						.getActiveEditor();
//				if (part instanceof EventBEditor) {
//					EventBEditor editor = (EventBEditor) part;
//					IRodinFile rodinFile = editor.getRodinInput();
//					UIUtils.newEvent(editor, rodinFile);
//				}
//			}
//		};
//		newEvent.setText("New Events");
//		newEvent.setToolTipText("Create a new event for the component");
//		newEvent.setImageDescriptor(EventBImage
//				.getImageDescriptor(EventBImage.IMG_NEW_EVENT_PATH));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void contributeToMenu(IMenuManager manager) {
//		IMenuManager menu = new MenuManager("Event-B");
//		menu.add(newVariables);
//		menu.add(newInvariants);
//		menu.add(newTheorems);
//		menu.add(newEvent);
//		manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
	 */
	@Override
	public void contributeToToolBar(IToolBarManager manager) {
//		manager.add(new Separator());
//		manager.add(newVariables);
//		manager.add(newInvariants);
//		manager.add(newTheorems);
//		manager.add(newEvent);
	}

}
