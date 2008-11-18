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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         This class extends the EventBPartWithButtons by having a Tree Viewer.
 */
public abstract class EventBTreePartWithButtons extends EventBPartWithButtons
		implements IStatusChangedListener {

	ActionGroup groupActionSet;

	/**
	 * Create a new Tree Viewer
	 * <p>
	 * 
	 * @param managedForm
	 *            The mangaged form used to create the tree viewer.
	 * @param toolkit
	 *            The Form Toolkit used to create the tree viewer
	 * @param parent
	 *            The composite parent
	 * @return a new Event-B Editable Tree Viewer
	 */
	abstract protected EventBEditableTreeViewer createTreeViewer(
			IManagedForm managedForm, FormToolkit toolkit, Composite parent);

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param managedForm
	 *            The managed form used to create the Section
	 * @param parent
	 *            The composite parent of the section
	 * @param toolkit
	 *            The FormToolkit used to create the Section
	 * @param style
	 *            The style used to create the Section
	 * @param editor
	 *            The associated Event-B Editor
	 * @param buttonLabels
	 *            The labels of the buttons
	 * @param title
	 *            The title of the Section
	 * @param description
	 *            The description of the Section
	 */
	public EventBTreePartWithButtons(final IManagedForm managedForm,
			Composite parent, FormToolkit toolkit, int style,
			IEventBEditor<?> editor, String[] buttonLabels, String title,
			String description) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels, title,
				description);
		groupActionSet = createActionGroup();
		hookContextMenu();
		editor.addStatusListener(this);
	}

	/**
	 * Create the action group associated with this part.
	 * <p>
	 * 
	 * @return a new action group
	 */
	protected abstract ActionGroup createActionGroup();

	/**
	 * Return the action group associated with this part.
	 * <p>
	 * 
	 * @return an action group
	 */
	protected ActionGroup getActionGroup() {
		return groupActionSet;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#createViewer(org.eclipse.ui.forms.IManagedForm,
	 *      org.eclipse.ui.forms.widgets.FormToolkit,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Viewer createViewer(IManagedForm managedForm,
			FormToolkit toolkit, Composite parent) {
		return createTreeViewer(managedForm, toolkit, parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#setSelection(org.rodinp.core.IRodinElement)
	 */
	@Override
	public void setSelection(IRodinElement element) {
		StructuredViewer viewer = (StructuredViewer) this.getViewer();
		viewer.reveal(element);
		viewer.setSelection(new StructuredSelection(element));
//		edit(element);
	}

	/**
	 * Select an item (TreeItem) at specific column.
	 * <p>
	 * 
	 * @param item
	 *            A tree item
	 * @param column
	 *            a valid column number
	 */
	protected void selectItem(TreeItem item, int column) {
		((EventBEditableTreeViewer) getViewer()).selectItem(item, column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IStatusChangedListener#statusChanged(java.util.Collection)
	 */
	public void statusChanged(final IRodinElement element) {
		Display display = this.getViewer().getControl().getDisplay();
		display.syncExec(new Runnable() {

			public void run() {
				((EventBEditableTreeViewer) EventBTreePartWithButtons.this.getViewer()).statusChanged(element);
				updateButtons();
			}
			
		});
	}

	/**
	 * Hook the actions to the menu
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(
						((StructuredViewer) getViewer()).getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Viewer viewer = getViewer();
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		this.editor.getSite().registerContextMenu(menuMgr,
				viewer);
	}
	
	protected void syncExec(Runnable runnable) {
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(runnable);
	}

	@Override
	public void dispose() {
		editor.removeStatusListener(this);
		super.dispose();
	}
	
}
