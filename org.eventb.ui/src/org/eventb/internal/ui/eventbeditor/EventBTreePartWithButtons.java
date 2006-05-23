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

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         This class extends the EventBPartWithButtons by having a Tree Viewer.
 */
public abstract class EventBTreePartWithButtons extends EventBPartWithButtons
		implements IStatusChangedListener {
	// The group of actions for the tree part.
	protected EventBMasterSectionActionGroup groupActionSet;

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
			EventBEditor editor, String[] buttonLabels, String title,
			String description) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels, title,
				description);
		makeActions();
		editor.addStatusListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#createViewer(org.eclipse.ui.forms.IManagedForm,
	 *      org.eclipse.ui.forms.widgets.FormToolkit,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	protected Viewer createViewer(IManagedForm managedForm,
			FormToolkit toolkit, Composite parent) {
		return createTreeViewer(managedForm, toolkit, parent);
	}

	/*
	 * Create the actions that can be used in the tree.
	 */
	private void makeActions() {
		groupActionSet = new EventBMasterSectionActionGroup(editor,
				(TreeViewer) this.getViewer());
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#setSelection(org.rodinp.core.IRodinElement)
	 */
	public void setSelection(IRodinElement element) {
		StructuredViewer viewer = (StructuredViewer) this.getViewer();
		viewer.setSelection(new StructuredSelection(element));
		edit(element);
	}

	/**
	 * Select an item (TreeItem) at specific column.
	 * <p>
	 * @param item A tree item
	 * @param column a valid column number
	 */
	protected void selectItem(TreeItem item, int column) {
		((EventBEditableTreeViewer) getViewer()).selectItem(item, column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.IStatusChangedListener#statusChanged(java.util.Collection)
	 */
	public void statusChanged(IRodinElement element) {
		((EventBEditableTreeViewer) this.getViewer()).statusChanged(element);
		updateButtons();
	}

}
