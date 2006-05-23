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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         An implementation of the Event-B Tree part with buttons for
 *         displaying and editting invariants.
 */
public class InvariantMasterSection extends EventBTreePartWithButtons {

	// The indexes for different buttons.
	private static final int ADD_INDEX = 0;

	private static final int DELETE_INDEX = 1;

	private static final int UP_INDEX = 2;

	private static final int DOWN_INDEX = 3;

	// The labels correspond to the above buttons.
	private static final String[] buttonLabels = { "Add", "Delete", "Up",
			"Down" };

	// Title and description of the section.
	private static final String SECTION_TITLE = "Invariants";

	private static final String SECTION_DESCRIPTION = "List of invariants of the component";

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param managedForm
	 *            The form to create this master section
	 * @param parent
	 *            The composite parent
	 * @param toolkit
	 *            The Form Toolkit used to create this master section
	 * @param style
	 *            The style
	 * @param editor
	 *            an Event-B Editor
	 */
	public InvariantMasterSection(IManagedForm managedForm, Composite parent,
			FormToolkit toolkit, int style, EventBEditor editor) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels,
				SECTION_TITLE, SECTION_DESCRIPTION);
	}

	/**
	 * Create the tree view part specifically for invariants.
	 * <p>
	 * 
	 * @param managedForm
	 *            The Form used to create the viewer.
	 * @param toolkit
	 *            The Form Toolkit used to create the viewer
	 * @param parent
	 *            The composite parent
	 */
	protected EventBEditableTreeViewer createTreeViewer(
			IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		return new InvariantEditableTreeViewer(editor, parent, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#updateButtons()
	 */
	protected void updateButtons() {
		Tree tree = ((TreeViewer) getViewer()).getTree();
		TreeItem[] items = tree.getSelection();

		boolean hasOneSelection = items.length == 1;
		boolean hasSelection = items.length > 0;
		boolean canMoveUp = false;
		boolean canMoveDown = false;
		if (hasOneSelection) {
			TreeItem item = items[0];
			IRodinElement element = (IRodinElement) item.getData();
			TreeItem prev = TreeSupports.findPrevItem(tree, item);
			if (prev != null) {
				if (element.getElementType() == ((IRodinElement) prev.getData())
						.getElementType())
					canMoveUp = true;
			}
			TreeItem next = TreeSupports.findNextItem(tree, item);
			if (next != null) {
				if (element.getElementType() == ((IRodinElement) next.getData())
						.getElementType())
					canMoveDown = true;
			}
		}
		setButtonEnabled(UP_INDEX, hasOneSelection && canMoveUp);
		setButtonEnabled(DOWN_INDEX, hasOneSelection && canMoveDown);

		setButtonEnabled(ADD_INDEX, true);
		setButtonEnabled(DELETE_INDEX, hasSelection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#buttonSelected(int)
	 */
	protected void buttonSelected(int index) {
		switch (index) {
		case ADD_INDEX:
			groupActionSet.addInvariant.run();
			break;
		case DELETE_INDEX:
			groupActionSet.delete.run();
			break;
		case UP_INDEX:
			groupActionSet.handleUp.run();
			break;
		case DOWN_INDEX:
			groupActionSet.handleDown.run();
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(final ElementChangedEvent event) {
		if (this.getViewer().getControl().isDisposed())
			return;
		Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				((EventBEditableTreeViewer) InvariantMasterSection.this
						.getViewer()).elementChanged(event);
				updateButtons();
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#edit(org.rodinp.core.IRodinElement)
	 */
	protected void edit(IRodinElement element) {
		TreeViewer viewer = (TreeViewer) this.getViewer();
		viewer.reveal(element);
		TreeItem item = TreeSupports.findItem(viewer.getTree(), element);
		selectItem(item, 1);
	}

}
