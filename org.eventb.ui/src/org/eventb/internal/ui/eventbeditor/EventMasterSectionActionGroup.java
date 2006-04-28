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
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionGroup;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBImageDescriptor;
import org.eventb.internal.ui.EventBUIPlugin;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * This class provides the actions that will be used with the tree viewer 
 * for Events (Rodin elements).
 */
public class EventMasterSectionActionGroup
	extends ActionGroup 
{
	
	// The Event-B Editor.
	private EventBEditor editor;
	
	// The tree viewer in the master section
	private TreeViewer viewer;
	
	// Some actions
	protected static Action newLocalVariable;
	protected static Action newGuard;
	protected static Action newAction;
	protected static Action delete;
	
	// The counter used to create automatic name for new elements.
	private int counter;
	
	/**
	 * Constructor: Create the actions
	 * <p>
	 * @param eventSection The Event Master section
	 */
	public EventMasterSectionActionGroup(EventBEditor eventBEditor, TreeViewer treeViewer) {
		this.editor = eventBEditor;
		this.viewer = treeViewer;
		counter = 0;
		
		newLocalVariable = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection(); 
								if (ssel.size() == 1) {
									IEvent event = (IEvent) ssel.getFirstElement();

								ElementAtributeInputDialog dialog = new ElementAtributeInputDialog(editor.getSite().getShell(), "New Local Variable", "Name of the new (local) variable", "var" + (counter + 1));
								dialog.open();
								Collection<String> names = dialog.getAttributes();
								try {
									for (Iterator<String> it = names.iterator(); it.hasNext();) {
										String name = it.next();
										event.createInternalElement(IVariable.ELEMENT_TYPE, name, null, null);
										counter++;
									}
								}
								catch (RodinDBException e) {
									e.printStackTrace();
								}
								viewer.refresh(event, true);
								viewer.setExpandedState(event, true);
								editor.editorDirtyStateChanged();
								}								
							}
						});
			}
		};
		newLocalVariable.setText("New &Variable");
		newLocalVariable.setToolTipText("Create a new (local) variable");
		newLocalVariable.setImageDescriptor(new EventBImageDescriptor(EventBImage.IMG_NEW_PROJECT));

		newGuard = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection(); 
								if (ssel.size() == 1) {
									IEvent event = (IEvent) ssel.getFirstElement();
									ElementNameContentInputDialog dialog = new ElementNameContentInputDialog(editor.getSite().getShell(), "New Invariants", "Name and predicate of the new invariant", "grd", counter + 1);
									dialog.open();
									String [] names = dialog.getNewNames();
									String [] contents = dialog.getNewContents();
									try {
										for (int i = 0; i < names.length; i++) {
											String name = names[i];
											String content = contents[i];
											IInternalElement guard = event.createInternalElement(IGuard.ELEMENT_TYPE, name, null, null);
											guard.setContents(content);
											counter++;
										}
									}
									catch (RodinDBException e) {
										e.printStackTrace();
									}
									viewer.refresh(event, true);
									viewer.setExpandedState(event, true);
									editor.editorDirtyStateChanged();
								}
								
							}
						});
			}
		};
		newGuard.setText("New &Guard");
		newGuard.setToolTipText("Create a new guard");
		newGuard.setImageDescriptor(new EventBImageDescriptor(EventBImage.IMG_NEW_PROJECT));

		newAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection(); 
								if (ssel.size() == 1) {
									IEvent event = (IEvent) ssel.getFirstElement();
									ElementAtributeInputDialog dialog = 
										new ElementAtributeInputDialog(editor.getSite().getShell(), 
												"New Action",
												"Substitute of the new action",
												EventBUIPlugin.SUB_DEFAULT);
									dialog.open();
									Collection<String> subs = dialog.getAttributes();
									try {
										for (Iterator<String> it = subs.iterator(); it.hasNext();) {
											String sub = it.next();
											IAction action = (IAction) event.createInternalElement(IAction.ELEMENT_TYPE, null, null, null);
											action.setContents(sub);
										}
									}
									catch (RodinDBException e) {
										e.printStackTrace();
									}
									viewer.refresh(event, true);
									viewer.setExpandedState(event, true);
									editor.editorDirtyStateChanged();
								}
							}
						});
			}
		};
		newAction.setText("New &Action");
		newAction.setToolTipText("Create a new action");
		newAction.setImageDescriptor(new EventBImageDescriptor(EventBImage.IMG_NEW_PROJECT));
		
		delete = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
								
								Object [] objects = ssel.toArray();
								Collection<IInternalElement> toDelete = new HashSet<IInternalElement>();
								for (int i = 0; i < objects.length; i++) {
									if (objects[i] instanceof IInternalElement) {
											toDelete.add((IInternalElement)objects[i]);
									}
								}
								try {
									EventBUIPlugin.getRodinDatabase().delete(toDelete.toArray(new IInternalElement[toDelete.size()]), true, null);
									viewer.refresh();
									editor.editorDirtyStateChanged();
								}
								catch (RodinDBException e) {
									e.printStackTrace();
								}
								return;
							}
						});
			}
		};
		delete.setText("&Delete");
		delete.setToolTipText("Delete selected element");
		delete.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
	}


	/**
	 * Fill the context menu with the actions create initially.
	 * <p>
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		if (sel instanceof IStructuredSelection) {
			menu.add(delete);
			IStructuredSelection ssel = (IStructuredSelection) sel; 
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				if (obj instanceof IEvent) {
					menu.add(newLocalVariable);
					menu.add(newGuard);
					menu.add(newAction);
//					MenuManager newMenu = new MenuManager("&New");
//					newMenu.add(newLocalVariable);
//					newMenu.add(newGuard);
//					newMenu.add(newAction);
//					menu.add(newMenu);
				}
			}
//			menu.add(deleteAction);
//			menu.add(new Separator());
//			drillDownAdapter.addNavigationActions(menu);
			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

}
