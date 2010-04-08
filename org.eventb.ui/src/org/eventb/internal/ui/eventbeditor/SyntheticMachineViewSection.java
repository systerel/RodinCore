/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - replaced local variable by parameter
 *     Systerel - introduced read only elements
 ******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import static org.eventb.internal.ui.eventbeditor.EventBEditorUtils.checkAndShowReadOnly;
import static org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry.Column.CONTENT;
import static org.eventb.internal.ui.eventbeditor.elementdesc.IElementDescRegistry.Column.LABEL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eventb.core.IInvariant;
import org.eventb.core.IParameter;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBImage;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         An implementation of the Event-B Tree part with buttons for
 *         displaying and editting elements of Machine construct.
 */
public class SyntheticMachineViewSection extends EventBTreePartWithButtons {

	// Labels correspond to the above buttons.
	private static String[] buttonLabels = {};

	// Title and description of the section.
	private final static String SECTION_TITLE = "Synthetics";

	private final static String SECTION_DESCRIPTION = "Synthetics View";

	private Action upAction;

	private Action downAction;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param managedForm
	 *            the managed form contains the section
	 * @param parent
	 *            the composite parent of the section
	 * @param toolkit
	 *            the FormToolkit used to create the section
	 * @param style
	 *            the style used to created the section
	 * @param editor
	 *            an Event-B Editor
	 */
	public SyntheticMachineViewSection(IManagedForm managedForm,
			Composite parent, FormToolkit toolkit, int style,
			EventBMachineEditor editor) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels,
				SECTION_TITLE, SECTION_DESCRIPTION);

		createToolBarActions(managedForm);
		this.getViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						updateToolbars();
					}
				});
	}

	/**
	 * Create the Toolbar actions.
	 * <p>
	 * 
	 * @param managedForm
	 *            the managed form contains the Toolbar
	 */
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		final Action filterVarAction = new Action("var", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				TreeViewer viewer = ((TreeViewer) SyntheticMachineViewSection.this
						.getViewer());
				viewer.refresh();
			}
		};
		filterVarAction.setChecked(false);
		filterVarAction.setToolTipText("Filter variable elements");

		final Action filterGrdAction = new Action("grd", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				TreeViewer viewer = ((TreeViewer) SyntheticMachineViewSection.this
						.getViewer());
				viewer.refresh();
			}
		};
		filterGrdAction.setChecked(false);
		filterGrdAction.setToolTipText("Filter guard elements");

		final Action filterInvAction = new Action("inv", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				TreeViewer viewer = ((TreeViewer) SyntheticMachineViewSection.this
						.getViewer());
				viewer.refresh();
			}
		};
		filterInvAction.setChecked(false);
		filterInvAction.setToolTipText("Filter invariant elements");

		final Action filterParamAction = new Action("prm", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				TreeViewer viewer = ((TreeViewer) SyntheticMachineViewSection.this
						.getViewer());
				viewer.refresh();
			}
		};
		filterParamAction.setChecked(false);
		filterParamAction.setToolTipText("Filter parameter elements");

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
				if (element instanceof IVariable) {
					return !filterVarAction.isChecked();
				}
				if (element instanceof IParameter) {
					return !filterParamAction.isChecked();
				}
				if (element instanceof IGuard) {
					return !filterGrdAction.isChecked();
				}
				if (element instanceof IInvariant) {
					return !filterInvAction.isChecked();
				}
				return true;
			}

		};
		((TreeViewer) this.getViewer()).addFilter(elementFilter);
		form.getToolBarManager().add(filterVarAction);
		form.getToolBarManager().add(filterGrdAction);
		form.getToolBarManager().add(filterInvAction);
		form.getToolBarManager().add(filterParamAction);

		final SyntheticMachineMasterSectionActionGroup actionSet = (SyntheticMachineMasterSectionActionGroup) this
				.getActionGroup();
		upAction = new Action("Up", Action.AS_PUSH_BUTTON) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				if(checkAndShowReadOnly(editor.getRodinInput())) {
					return;
				}
				actionSet.handleUp.run();
			}
		};
		upAction.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_UP_PATH));
		form.getToolBarManager().add(upAction);
		form.updateToolBar();

		downAction = new Action("Down", Action.AS_PUSH_BUTTON) {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.jface.action.Action#run()
			 */
			@Override
			public void run() {
				if(checkAndShowReadOnly(editor.getRodinInput())) {
					return;
				}
				actionSet.handleDown.run();
			}
		};
		downAction.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_DOWN_PATH));
		form.getToolBarManager().add(downAction);
		form.updateToolBar();

		updateToolbars();
	}

	protected void updateToolbars() {
		Tree tree = ((TreeViewer) getViewer()).getTree();
		TreeItem[] items = tree.getSelection();

		boolean hasOneSelection = items.length == 1;
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
		upAction.setEnabled(hasOneSelection && canMoveUp);
		downAction.setEnabled(hasOneSelection && canMoveDown);
	}

	/**
	 * Update the expanded of buttons.
	 */
	@Override
	protected void updateButtons() {
		// Tree tree = ((TreeViewer) getViewer()).getTree();
		// TreeItem[] items = tree.getSelection();
		//
		// boolean hasOneSelection = items.length == 1;
		// boolean canMoveUp = false;
		// boolean canMoveDown = false;
		//
		// if (hasOneSelection) {
		// TreeItem item = items[0];
		// IRodinElement element = (IRodinElement) item.getData();
		// TreeItem prev = TreeSupports.findPrevItem(tree, item);
		// if (prev != null) {
		// if (element.getElementType() == ((IRodinElement) prev.getData())
		// .getElementType())
		// canMoveUp = true;
		// }
		// TreeItem next = TreeSupports.findNextItem(tree, item);
		// if (next != null) {
		// if (element.getElementType() == ((IRodinElement) next.getData())
		// .getElementType())
		// canMoveDown = true;
		// }
		// }
		// setButtonEnabled(UP_INDEX, hasOneSelection && canMoveUp);
		// setButtonEnabled(DOWN_INDEX, hasOneSelection && canMoveDown);

		// setButtonEnabled(ADD_EVT_INDEX, true);
		// setButtonEnabled(ADD_VAR_INDEX, true);
		// setButtonEnabled(ADD_INV_INDEX, true);
		// setButtonEnabled(ADD_THM_INDEX, true);
	}

	/**
	 * Method to response to button selection.
	 * <p>
	 * 
	 * @param index
	 *            The index of selected button
	 */
	@Override
	protected void buttonSelected(int index) {
		// switch (index) {
		// case ADD_VAR_INDEX:
		// groupActionSet.addVariable.run();
		// break;
		// case ADD_INV_INDEX:
		// groupActionSet.addInvariant.run();
		// break;
		// case ADD_THM_INDEX:
		// groupActionSet.addTheorem.run();
		// break;
		// case ADD_EVT_INDEX:
		// groupActionSet.addEvent.run();
		// break;
		// case UP_INDEX:
		// groupActionSet.handleUp.run();
		// break;
		// case DOWN_INDEX:
		// groupActionSet.handleDown.run();
		// break;
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(final ElementChangedEvent event) {
		syncExec(new Runnable() {
			public void run() {
				if (SyntheticMachineViewSection.this.getViewer().getControl()
						.isDisposed())
					return;
				((EventBEditableTreeViewer) SyntheticMachineViewSection.this
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
		if (element instanceof IVariable)
			selectItem(item, LABEL.getId());
		else if (element instanceof IParameter)
			selectItem(item, LABEL.getId());
		else if (element instanceof IEvent)
			selectItem(item, LABEL.getId());
		else
			selectItem(item, CONTENT.getId());
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
		return new SyntheticEditableTreeViewer(editor, parent, SWT.MULTI
				| SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
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
		return new SyntheticMachineMasterSectionActionGroup(
				(EventBMachineEditor) editor, (TreeViewer) this.getViewer());
	}

}