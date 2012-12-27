/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced local variable by parameter
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IParameter;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An implementation of the Event-B Tree part with buttons for
 *         displaying and editting events.
 */
public class EventMasterSection extends EventBTreePartWithButtons {

	// The indexes for different buttons.
	private static final int ADD_EVT_INDEX = 0;

	private static final int ADD_PARAM_INDEX = 1;

	private static final int ADD_GRD_INDEX = 2;

	private static final int ADD_ACT_INDEX = 3;

	private static final int UP_INDEX = 4;

	private static final int DOWN_INDEX = 5;

	// The labels correspond to the above buttons.
	private static final String[] buttonLabels = { "Add Event", "Add Param",
			"Add Guard", "Add Action", "Up", "Down" };

	// Title and description of the section.
	private final static String SECTION_TITLE = "Events";

	private final static String SECTION_DESCRIPTION = "The list contains events from the model whose details are editable on the right";

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
	public EventMasterSection(IManagedForm managedForm, Composite parent,
			FormToolkit toolkit, int style, IEventBEditor<?> editor) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels,
				SECTION_TITLE, SECTION_DESCRIPTION);

		createToolBarActions(managedForm);
	}

	/**
	 * Create the Toolbar actions.
	 * <p>
	 * 
	 * @param managedForm
	 *            The managed form contains the Toolbar.
	 */
	protected void createToolBarActions(IManagedForm managedForm) {
		final Action filterParamAction = new Action("prm", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				TreeViewer viewer = ((TreeViewer) EventMasterSection.this
						.getViewer());
				viewer.refresh();
			}
		};
		filterParamAction.setChecked(false);
		filterParamAction.setToolTipText("Filter parameter elements");

		final Action filterGrdAction = new Action("grd", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				TreeViewer viewer = ((TreeViewer) EventMasterSection.this
						.getViewer());
				viewer.refresh();
			}
		};
		filterGrdAction.setChecked(false);
		filterGrdAction.setToolTipText("Filter guard elements");

		ViewerFilter elementFilter = new ViewerFilter() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
			 *      java.lang.Object, java.lang.Object)
			 */
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) {
				if (element instanceof IParameter) {
					if (filterParamAction.isChecked())
						return false;
					else
						return true;
				} else if (element instanceof IGuard) {
					if (filterGrdAction.isChecked())
						return false;
					else
						return true;
				}
				return true;
			}

		};
		((TreeViewer) this.getViewer()).addFilter(elementFilter);
		ScrolledForm form = managedForm.getForm();

		form.getToolBarManager().add(filterParamAction);
		form.getToolBarManager().add(filterGrdAction);

		form.updateToolBar();
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
		IStructuredSelection ssel = (IStructuredSelection) getViewer()
				.getSelection();
		boolean hasOneSelection = ssel.size() == 1;
		boolean initSelected = false;
		boolean canMoveUp = false;
		boolean canMoveDown = false;

		if (hasOneSelection) {
			IEvent event;
			if (ssel.getFirstElement() instanceof IRodinElement) {
				IRodinElement element = (IRodinElement) ssel.getFirstElement();
				if (element instanceof IEvent) {
					event = (IEvent) element;
				} else if (element instanceof IInternalElement) {
					event = (IEvent) ((IInternalElement) element).getParent();
				} else { // Should not happen
					event = null;
				}
				try {
					initSelected = (event.getLabel().equals("INITIALISATION")) ? true
							: false;
				} catch (RodinDBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		if (hasOneSelection) {
			TreeItem item = items[0];
			IRodinElement element = ((IRodinElement) item.getData());
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

		setButtonEnabled(ADD_EVT_INDEX, true);
		setButtonEnabled(ADD_PARAM_INDEX, hasOneSelection && !initSelected);
		setButtonEnabled(ADD_GRD_INDEX, hasOneSelection && !initSelected);
		setButtonEnabled(ADD_ACT_INDEX, hasOneSelection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#buttonSelected(int)
	 */
	@Override
	protected void buttonSelected(int index) {
		EventMasterSectionActionGroup actionSet = (EventMasterSectionActionGroup) this
				.getActionGroup();
		switch (index) {
		case ADD_EVT_INDEX:
			actionSet.addEvent.run();
			break;
		case ADD_PARAM_INDEX:
			actionSet.addParameter.run();
			break;
		case ADD_GRD_INDEX:
			actionSet.addGuard.run();
			break;
		case ADD_ACT_INDEX:
			actionSet.addAction.run();
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
	 * @see org.eventb.internal.ui.eventbeditor.EventBTreePartWithButtons#createTreeViewer(org.eclipse.ui.forms.IManagedForm,
	 *      org.eclipse.ui.forms.widgets.FormToolkit,
	 *      org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected EventBEditableTreeViewer createTreeViewer(
			IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		return new EventEditableTreeViewer(editor, parent, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	@Override
	public void elementChanged(final ElementChangedEvent event) {
		syncExec(new Runnable() {
			@Override
			public void run() {
				if (EventMasterSection.this.getViewer().getControl()
						.isDisposed())
					return;
				((EventBEditableTreeViewer) EventMasterSection.this.getViewer())
						.elementChanged(event);
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
		EventBEditableTreeViewer viewer = (EventBEditableTreeViewer) this.getViewer();
		viewer.edit(element);
//		TreeItem item = TreeSupports.findItem(viewer.getTree(), element);
//		if (element instanceof IVariable)
//			selectItem(item, 0);
//		else if (element instanceof IEvent)
//			selectItem(item, 0);
//		else if (element instanceof IRefinesEvent)
//			selectItem(item, 0);
//		else if (element instanceof IWitness)
//			selectItem(item, 0);
//		else
//			selectItem(item, 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
	 */
	@Override
	public void dispose() {
		editor.removeStatusListener(this);
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBTreePartWithButtons#createActionGroup()
	 */
	@Override
	protected ActionGroup createActionGroup() {
		return new EventMasterSectionActionGroup((EventBMachineEditor) editor,
				(TreeViewer) this.getViewer());
	}

}
