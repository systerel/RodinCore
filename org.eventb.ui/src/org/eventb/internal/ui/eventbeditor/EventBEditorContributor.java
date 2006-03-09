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

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * Manages the installation/deinstallation of global actions for multi-page editors.
 * Responsible for the redirection of global actions to the active editor.
 * Multi-page contributor replaces the contributors for the individual editors in the multi-page editor.
 */
public class EventBEditorContributor
	extends MultiPageEditorActionBarContributor
{
	public static RefreshAction sampleAction;
	private IEditorPart activeEditorPart;
	public static Action newVariables;
	
	/**
	 * Creates a multi-page contributor.
	 */
	public EventBEditorContributor() {
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
		if (actionBars != null) {

			ITextEditor editor = (part instanceof ITextEditor) ? (ITextEditor) part : null;

			actionBars.setGlobalActionHandler(
				ActionFactory.DELETE.getId(),
				getAction(editor, ITextEditorActionConstants.DELETE));
			actionBars.setGlobalActionHandler(
				ActionFactory.UNDO.getId(),
				getAction(editor, ITextEditorActionConstants.UNDO));
			actionBars.setGlobalActionHandler(
				ActionFactory.REDO.getId(),
				getAction(editor, ITextEditorActionConstants.REDO));
			actionBars.setGlobalActionHandler(
				ActionFactory.CUT.getId(),
				getAction(editor, ITextEditorActionConstants.CUT));
			actionBars.setGlobalActionHandler(
				ActionFactory.COPY.getId(),
				getAction(editor, ITextEditorActionConstants.COPY));
			actionBars.setGlobalActionHandler(
				ActionFactory.PASTE.getId(),
				getAction(editor, ITextEditorActionConstants.PASTE));
			actionBars.setGlobalActionHandler(
				ActionFactory.SELECT_ALL.getId(),
				getAction(editor, ITextEditorActionConstants.SELECT_ALL));
			actionBars.setGlobalActionHandler(
				ActionFactory.FIND.getId(),
				getAction(editor, ITextEditorActionConstants.FIND));
			actionBars.setGlobalActionHandler(
				IDEActionFactory.BOOKMARK.getId(),
				getAction(editor, IDEActionFactory.BOOKMARK.getId()));
			actionBars.updateActionBars();
		}
		else {
			IToolBarManager manager = actionBars.getToolBarManager();
//			manager.add(sampleAction);
			Action newVariables = new Action() {
				public void run() {
					IEditorPart part = EventBUIPlugin.getActivePage().getActiveEditor();
					if (part instanceof EventBEditor) {
						EventBEditor editor = (EventBEditor) activeEditorPart;
						IRodinFile rodinFile = editor.getRodinInput();
						try {
							int counter = rodinFile.getChildrenOfType(IVariable.ELEMENT_TYPE).length;
							ElementAtributeInputDialog dialog = new ElementAtributeInputDialog(activeEditorPart.getSite().getShell(), new FormToolkit(Display.getCurrent()), "New Variables", "Name of the new variable", "var" + (counter + 1));
			
							dialog.open();
							Collection<String> names = dialog.getAttributes();
							for (Iterator<String> it = names.iterator(); it.hasNext();) {
								String name = it.next();
								rodinFile.createInternalElement(IVariable.ELEMENT_TYPE, name, null, null);
							}
						}
						catch (RodinDBException e) {
							e.printStackTrace();
						}
					}
				}
			};
			newVariables.setText("New Variables");
//			newVariables.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//					getImageDescriptor(EventBImage.IMG_VARIABLE));
			manager.add(newVariables);

			actionBars.updateActionBars();
			
		}
	}
	private void createActions() {
		sampleAction = new RefreshAction(EventBUIPlugin.getActiveWorkbenchShell());
//		sampleAction = new Action() {
//			public void run() {
//				MessageDialog.openInformation(null, "EventB Plug-in", "Sample Action Executed");
//			}
//		};
//		sampleAction.setText("Refresh");
		sampleAction.setToolTipText("Refresh the component");
		sampleAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ActionFactory.REFRESH.getId()));
		
		newVariables = new Action() {
			public void run() {
				IEditorPart part = EventBUIPlugin.getActivePage().getActiveEditor();
				if (part instanceof EventBEditor) {
					EventBEditor editor = (EventBEditor) part;
					IRodinFile rodinFile = editor.getRodinInput();
					UIUtils.newVariables(rodinFile);
				}
			}
		};
		newVariables.setText("New Variables");
		newVariables.setToolTipText("Create new variables for the component");
		newVariables.setImageDescriptor(EventBImage.getImageDescriptor(EventBImage.IMG_NEW_VARIABLES_PATH));
			
	}
	
	
	public void contributeToMenu(IMenuManager manager) {
		IMenuManager menu = new MenuManager("Event-B");
		manager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
		manager.add(newVariables);		
//		menu.add(sampleAction);
	}
	public void contributeToToolBar(IToolBarManager manager) {
		manager.add(new Separator());
//		manager.add(sampleAction);
		manager.add(newVariables);		
	}
}
