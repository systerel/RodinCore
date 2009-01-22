/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry.Column.CONTENT;import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IMachineRoot;
import org.eventb.ui.eventbeditor.IEventBEditor;
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
			FormToolkit toolkit, int style, IEventBEditor<?> editor) {
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
	@Override
	@SuppressWarnings("unchecked")
	protected EventBEditableTreeViewer createTreeViewer(
			IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		return new InvariantEditableTreeViewer((IEventBEditor<IMachineRoot>) editor, parent, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#updateButtons()
	 */
	@Override
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
	@Override
	protected void buttonSelected(int index) {
		final InvariantMasterSectionActionGroup actionSet = (InvariantMasterSectionActionGroup) this
				.getActionGroup();
		switch (index) {
		case ADD_INDEX:
			actionSet.addInvariant.run();
			break;
		case DELETE_INDEX:
			actionSet.delete.run();
			break;
		case UP_INDEX:
			actionSet.handleUp.run();
			break;
		case DOWN_INDEX:
			actionSet.handleDown.run();
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(final ElementChangedEvent event) {
		syncExec(new Runnable() {
			public void run() {
				if (InvariantMasterSection.this.getViewer().getControl().isDisposed())
					return;
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
	@Override
	protected void edit(IRodinElement element) {
		TreeViewer viewer = (TreeViewer) this.getViewer();
		viewer.reveal(element);
		TreeItem item = TreeSupports.findItem(viewer.getTree(), element);
		selectItem(item, CONTENT.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBTreePartWithButtons#createActionGroup()
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected ActionGroup createActionGroup() {
		return new InvariantMasterSectionActionGroup((IEventBEditor<IMachineRoot>) editor, (TreeViewer) this
				.getViewer());
	}

}
