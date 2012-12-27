/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
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
public class EventBContextEditorContributor extends EventBEditorContributor {

	// Actions associated with Context Editor.
//	private static Action newCarrierSets;
//
//	private static Action newConstants;
//
//	private static Action newTheorems;
//
//	private static Action newAxioms;

	/**
	 * Default constructor: Creates a multi-page contributor.
	 */
	public EventBContextEditorContributor() {
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

//		// New Carrier Sets
//		newCarrierSets = new Action() {
//			public void run() {
//				IEditorPart part = EventBUIPlugin.getActivePage()
//						.getActiveEditor();
//				if (part instanceof EventBEditor) {
//					EventBEditor editor = (EventBEditor) part;
//					IRodinFile rodinFile = editor.getRodinInput();
//					UIUtils.newCarrierSets(editor, rodinFile);
//					editor.editorDirtyStateChanged();
//				}
//			}
//		};
//		newCarrierSets.setText("New Carrier Sets");
//		newCarrierSets
//				.setToolTipText("Create new carrier sets for the component");
//		newCarrierSets.setImageDescriptor(EventBImage
//				.getImageDescriptor(EventBImage.IMG_NEW_CARRIER_SETS_PATH));
//
//		// New Constants
//		newConstants = new Action() {
//			public void run() {
//				IEditorPart part = EventBUIPlugin.getActivePage()
//						.getActiveEditor();
//				if (part instanceof EventBEditor) {
//					EventBEditor editor = (EventBEditor) part;
//					IRodinFile rodinFile = editor.getRodinInput();
//					UIUtils.newConstants(editor, rodinFile);
//					editor.editorDirtyStateChanged();
//				}
//			}
//		};
//		newConstants.setText("New Constants");
//		newConstants.setToolTipText("Create new cosntants for the component");
//		newConstants.setImageDescriptor(EventBImage
//				.getImageDescriptor(EventBImage.IMG_NEW_CONSTANTS_PATH));
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
//					editor.editorDirtyStateChanged();
//				}
//			}
//		};
//		newTheorems.setText("New Theorems");
//		newTheorems.setToolTipText("Create new theorems for the component");
//		newTheorems.setImageDescriptor(EventBImage
//				.getImageDescriptor(EventBImage.IMG_NEW_THEOREMS_PATH));
//
//		// New Axioms.
//		newAxioms = new Action() {
//			public void run() {
//				IEditorPart part = EventBUIPlugin.getActivePage()
//						.getActiveEditor();
//				if (part instanceof EventBEditor) {
//					EventBEditor editor = (EventBEditor) part;
//					IRodinFile rodinFile = editor.getRodinInput();
//					UIUtils.newAxioms(editor, rodinFile);
//					editor.editorDirtyStateChanged();
//				}
//			}
//		};
//		newAxioms.setText("New Axioms");
//		newAxioms.setToolTipText("Create new axioms for the component");
//		newAxioms.setImageDescriptor(EventBImage
//				.getImageDescriptor(EventBImage.IMG_NEW_AXIOMS_PATH));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void contributeToMenu(IMenuManager manager) {
//		IMenuManager menu = new MenuManager("Event-B");
//		menu.add(newCarrierSets);
//		menu.add(newConstants);
//		menu.add(newTheorems);
//		menu.add(newAxioms);
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
//		manager.add(newCarrierSets);
//		manager.add(newConstants);
//		manager.add(newTheorems);
//		manager.add(newAxioms);
	}

}
