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

package org.eventb.ui.internal.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IVariable;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.views.EventBImageDescriptor;
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
	
	// The Event Master section.
	private EventMasterSection section;
	
	// The tree viewer in the master section
	private TreeViewer viewer;
	
	// Some actions
	private static Action newLocalVariable;
	private static Action newGuard;
	private static Action newAction;
	
	// The counter used to create automatic name for new elements.
	private int counter;

	
	/**
	 * Constructor: Create the actions
	 * <p>
	 * @param eventSection The Event Master section
	 */
	public EventMasterSectionActionGroup(EventMasterSection eventSection) {
		this.section = eventSection;
		viewer = section.getViewer();
		counter = 0;
		
		newLocalVariable = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection(); 
								if (ssel.size() == 1) {
									IEvent event = (IEvent) ssel.getFirstElement();
									try {
										InputDialog dialog = new InputDialog(null, "Variable Name", "Name of the new variable", "var" + counter, null);
										dialog.open();
										String name = dialog.getValue();
										if (name != null) {
											IInternalElement variable = event.createInternalElement(IVariable.ELEMENT_TYPE, name, null, null);
											counter++;
											viewer.refresh(event, true);
											viewer.setSelection(new StructuredSelection(variable));
											section.markDirty();
											((EventBFormPage) section.block.getPage()).notifyChangeListeners();

										}
										dialog.close();
									}
									catch (RodinDBException e) {
										e.printStackTrace();
									}
								}
								
							}
						});
			}
		};
		newLocalVariable.setText("&Variable");
		newLocalVariable.setToolTipText("Create a new (local) variable");
		newLocalVariable.setImageDescriptor(new EventBImageDescriptor(EventBUIPlugin.IMG_NEW_PROJECT));

		newGuard = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection(); 
								if (ssel.size() == 1) {
									IEvent event = (IEvent) ssel.getFirstElement();
									try {
										IInternalElement guard = event.createInternalElement(IGuard.ELEMENT_TYPE, "guard" + counter++, null, null);
										viewer.refresh(event, true);
										viewer.setSelection(new StructuredSelection(guard));
										section.markDirty();
										((EventBFormPage) section.block.getPage()).notifyChangeListeners();

									}
									catch (RodinDBException e) {
										e.printStackTrace();
									}
								}
								
							}
						});
			}
		};
		newGuard.setText("&Guard");
		newGuard.setToolTipText("Create a new guard");
		newGuard.setImageDescriptor(new EventBImageDescriptor(EventBUIPlugin.IMG_NEW_PROJECT));

		newAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(viewer.getTree().getDisplay(),
						new Runnable() {
							public void run() {
								IStructuredSelection ssel = (IStructuredSelection) viewer.getSelection(); 
								if (ssel.size() == 1) {
									IEvent event = (IEvent) ssel.getFirstElement();
									try {
										IInternalElement action = event.createInternalElement(IAction.ELEMENT_TYPE, "action" + counter++, null, null);
										viewer.refresh(event, true);
										viewer.setSelection(new StructuredSelection(action));
										section.markDirty();
										((EventBFormPage) section.block.getPage()).notifyChangeListeners();
									}
									catch (RodinDBException e) {
										e.printStackTrace();
									}
								}
								
							}
						});
			}
		};
		newAction.setText("&Action");
		newAction.setToolTipText("Create a new action");
		newAction.setImageDescriptor(new EventBImageDescriptor(EventBUIPlugin.IMG_NEW_PROJECT));	
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
				if (obj instanceof IEvent) {
					MenuManager newMenu = new MenuManager("&New");
					newMenu.add(newLocalVariable);
					newMenu.add(newGuard);
					newMenu.add(newAction);
					menu.add(newMenu);
				}
			}
//			menu.add(deleteAction);
			menu.add(new Separator());
//			drillDownAdapter.addNavigationActions(menu);
			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

}
