/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 * Manages the installation/deinstallation of global actions for multi-page editors.
 * Responsible for the redirection of global actions to the active editor.
 * Multi-page contributor replaces the contributors for the individual editors in the multi-page editor.
 */
public class EventBMachineEditorContributor
	extends MultiPageEditorActionBarContributor
{
	public static RefreshAction sampleAction;
	private IEditorPart activeEditorPart;
	
	public static Action newVariables;
	public static Action newInvariants;
	public static Action newTheorems;
	public static Action newEvent;
	
	/**
	 * Creates a multi-page contributor.
	 */
	public EventBMachineEditorContributor() {
		super();
		createActions();
	}
	/**
	 * Returns the action registed with the given text editor.
	 * @return IAction or null if editor is null.
	 */
	protected IAction getAction(ITextEditor editor, String actionID) {
		return (editor == null ? null : editor.getAction(actionID));
	}
	/* (non-JavaDoc)
	 * Method declared in AbstractMultiPageEditorActionBarContributor.
	 */

	public void setActivePage(IEditorPart part) {
		if (activeEditorPart == part)
			return;

		activeEditorPart = part;

		IActionBars actionBars = getActionBars();
//		if (actionBars != null) {
//
//			ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;
//
//			actionBars.setGlobalActionHandler(
//				ActionFactory.DELETE.getId(),
//				getAction(editor, ITextEditorActionConstants.DELETE));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.UNDO.getId(),
//				getAction(editor, ITextEditorActionConstants.UNDO));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.REDO.getId(),
//				getAction(editor, ITextEditorActionConstants.REDO));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.CUT.getId(),
//				getAction(editor, ITextEditorActionConstants.CUT));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.COPY.getId(),
//				getAction(editor, ITextEditorActionConstants.COPY));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.PASTE.getId(),
//				getAction(editor, ITextEditorActionConstants.PASTE));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.SELECT_ALL.getId(),
//				getAction(editor, ITextEditorActionConstants.SELECT_ALL));
//			actionBars.setGlobalActionHandler(
//				ActionFactory.FIND.getId(),
//				getAction(editor, ITextEditorActionConstants.FIND));
//			actionBars.setGlobalActionHandler(
//				IDEActionFactory.BOOKMARK.getId(),
//				getAction(editor, IDEActionFactory.BOOKMARK.getId()));
//			actionBars.updateActionBars();
//		}
//		else {
			IToolBarManager manager = actionBars.getToolBarManager();
			manager.add(sampleAction);
			actionBars.updateActionBars();
			
//		}
	}
	private void createActions() {
		sampleAction = new RefreshAction(EventBUIPlugin.getActiveWorkbenchShell());

		sampleAction.setToolTipText("Refresh the component");
		sampleAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ActionFactory.REFRESH.getId()));
		
		newVariables = new Action() {
			public void run() {
				IEditorPart part = EventBUIPlugin.getActivePage().getActiveEditor();
				if (part instanceof EventBEditor) {
					EventBEditor editor = (EventBEditor) part;
					IRodinFile rodinFile = editor.getRodinInput();
					UIUtils.intelligentNewVariables(rodinFile);
					editor.setActivePage(VariablesPage.PAGE_ID);
				}
			}
		};
		newVariables.setText("New Variables");
		newVariables.setToolTipText("Create new variables for the component");
		newVariables.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEW_VARIABLES_PATH));
		
		newInvariants = new Action() {
			public void run() {
				IEditorPart part = EventBUIPlugin.getActivePage().getActiveEditor();
				if (part instanceof EventBEditor) {
					EventBEditor editor = (EventBEditor) part;
					IRodinFile rodinFile = editor.getRodinInput();
					UIUtils.newInvariants(rodinFile);
					editor.setActivePage(InvariantsPage.PAGE_ID);
				}
			}
		};
		newInvariants.setText("New Invariants");
		newInvariants.setToolTipText("Create new invariants for the component");
		newInvariants.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEW_INVARIANTS_PATH));
			
		newTheorems = new Action() {
			public void run() {
				IEditorPart part = EventBUIPlugin.getActivePage().getActiveEditor();
				if (part instanceof EventBEditor) {
					EventBEditor editor = (EventBEditor) part;
					IRodinFile rodinFile = editor.getRodinInput();
					UIUtils.newTheorems(rodinFile);
					editor.setActivePage(TheoremsPage.PAGE_ID);
				}
			}
		};
		newTheorems.setText("New Theorems");
		newTheorems.setToolTipText("Create new theorems for the component");
		newTheorems.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEW_THEOREMS_PATH));
		
		newEvent = new Action() {
			public void run() {
				IEditorPart part = EventBUIPlugin.getActivePage().getActiveEditor();
				if (part instanceof EventBEditor) {
					EventBEditor editor = (EventBEditor) part;
					IRodinFile rodinFile = editor.getRodinInput();
					UIUtils.newEvent(rodinFile);
					editor.setActivePage(EventsPage.PAGE_ID);
				}
			}
		};
		newEvent.setText("New Events");
		newEvent.setToolTipText("Create a new event for the component");
		newEvent.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEW_EVENT_PATH));

	}
	
	
	public void contributeToMenu(IMenuManager manager) {
		IMenuManager menu = new MenuManager("Event-B");
		menu.add(newVariables);
		menu.add(newInvariants);
		menu.add(newTheorems);
		menu.add(newEvent);
		manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
	}

	public void contributeToToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		manager.add(newVariables);
		manager.add(newInvariants);
		manager.add(newTheorems);
		manager.add(newEvent);
	}
}
