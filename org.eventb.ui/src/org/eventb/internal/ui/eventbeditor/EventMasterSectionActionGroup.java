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
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.TreeItem;
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
import org.rodinp.core.IRodinElement;
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
	protected Action newLocalVariable;
	protected Action newGuard;
	protected Action newAction;
	protected Action delete;
	
	/**
	 * Constructor: Create the actions
	 * <p>
	 * @param eventSection The Event Master section
	 */
	public EventMasterSectionActionGroup(EventBEditor eventBEditor, TreeViewer treeViewer) {
		this.editor = eventBEditor;
		this.viewer = treeViewer;
		
		newLocalVariable = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								try {
									IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
									if (ssel.size() == 1) {
										Object obj = ssel.getFirstElement();
										IInternalElement event = TreeSupports.getEvent(obj);
										int counter = ((IInternalElement) event).getChildrenOfType(IVariable.ELEMENT_TYPE).length;
										IInternalElement var = event.createInternalElement(IVariable.ELEMENT_TYPE, "var"+(counter+1), null, null);
										editor.addNewElement(var);
										viewer.setExpandedState(TreeSupports.findItem(viewer.getTree(), event).getData(), true);
										select(var, 0);
									}
								}
								catch (RodinDBException e) {
									e.printStackTrace();
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
								try {
									IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
									if (ssel.size() == 1) {
										Object obj = ssel.getFirstElement();
										IInternalElement event = TreeSupports.getEvent(obj);
										int counter = ((IInternalElement) event).getChildrenOfType(IGuard.ELEMENT_TYPE).length;
										IInternalElement grd = event.createInternalElement(IGuard.ELEMENT_TYPE, "grd"+(counter+1), null, null);
										grd.setContents(EventBUIPlugin.GRD_DEFAULT);
										editor.addNewElement(grd);
										viewer.setExpandedState(TreeSupports.findItem(viewer.getTree(), event).getData(), true);
										select(grd, 1);
									}
								}
								catch (RodinDBException e) {
									e.printStackTrace();
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
								try {
									IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
									if (ssel.size() == 1) {
										Object obj = ssel.getFirstElement();
										IInternalElement event = TreeSupports.getEvent(obj);
										int counter = ((IInternalElement) event).getChildrenOfType(IAction.ELEMENT_TYPE).length;
										IInternalElement act = event.createInternalElement(IAction.ELEMENT_TYPE, null, null, null);
										act.setContents("act" + (counter+1));
										editor.addNewElement(act);
										viewer.setExpandedState(TreeSupports.findItem(viewer.getTree(), event).getData(), true);
										select(act, 1);
									}
								}
								catch (RodinDBException e) {
									e.printStackTrace();
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
								try {
									IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection();
									if (ssel.size() == 1) {
										Object obj = ssel.getFirstElement();
										IInternalElement event = TreeSupports.getEvent(obj);
										int counter = ((IInternalElement) event).getChildrenOfType(IAction.ELEMENT_TYPE).length;
										IInternalElement element = event.createInternalElement(IAction.ELEMENT_TYPE, null, null, null);
										element.setContents("act"+(counter+1));
										viewer.setExpandedState(TreeSupports.findItem(viewer.getTree(), event).getData(), true);
										/* TODO Should use the previous findItem to avoid searching again */
										select(element, 1);
									}
								}
								catch (RodinDBException e) {
									e.printStackTrace();
								}
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
			IStructuredSelection ssel = (IStructuredSelection) sel; 
			if (ssel.size() == 1) {
				Object obj = ssel.getFirstElement();
				
				if (((Leaf) obj).getElement() instanceof IEvent) {
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
			menu.add(delete);
//			menu.add(deleteAction);
//			menu.add(new Separator());
//			drillDownAdapter.addNavigationActions(menu);
			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

	private void select(Object obj, int column) throws RodinDBException {
//		UIUtils.debug("Element: " + obj);
//		if (obj instanceof IAction) {
//			UIUtils.debug("Action: " + ((IAction) obj).getContents());
//		}
		TreeItem item = TreeSupports.findItem(viewer.getTree(), (IRodinElement) obj);
		viewer.reveal(item.getData());

		((EventBEditableTreeViewer) viewer).selectItem(item, column); // try to select the second column to edit name
	}
}
