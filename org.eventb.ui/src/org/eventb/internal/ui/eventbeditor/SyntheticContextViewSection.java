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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ICarrierSet;
import org.eventb.core.IConstant;
import org.eventb.core.IGuard;
import org.eventb.core.IVariable;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IUnnamedInternalElement;

/**
 * @author htson
 * <p>
 * An implementation of Section Part for displaying and editting Sees clause.
 */
public class SyntheticContextViewSection
	extends EventBTreePartWithButtons
	implements IStatusChangedListener
{

	// The indexes for different buttons.
	private static final int ADD_SET_INDEX = 0;
	private static final int ADD_CST_INDEX = 1;
	private static final int ADD_AXM_INDEX = 2;
	private static final int ADD_THM_INDEX = 3;
	private static final int UP_INDEX = 4;
	private static final int DOWN_INDEX = 5;

	private static String [] buttonLabels =
	{"Add Set.", "Add Cst.", "Add Axm.", "Add Thm.", "Up", "Down"};

	// Title and description of the section.
	private final static String SECTION_TITLE = "Synthetics";
	private final static String SECTION_DESCRIPTION = "Synthetics View";
	
	private ViewerFilter varFilter;
	private ViewerFilter grdFilter;

	/**
     * Constructor.
     * <p>
     * @param editor The Form editor contains this section
     * @param page The Dependencies page contains this section
     * @param parent The composite parent
     */
	public SyntheticContextViewSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit,
			int style, EventBEditor editor) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels, SECTION_TITLE, SECTION_DESCRIPTION);

		hookContextMenu();
		createToolBarActions(managedForm);
	}

	/**
	 * Create the Toolbar actions
	 */
	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		varFilter = new ViewerFilter() {

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				IRodinElement rodinElement = ((Leaf) element).getElement();
				
				if (rodinElement instanceof IVariable) return false;
				else return true;
			}
			
		};
		
		grdFilter = new ViewerFilter() {

			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				IRodinElement rodinElement = ((Leaf) element).getElement();
				if (rodinElement instanceof IGuard) return false;
				else return true;
			}
			
		};

		Action filterVarAction = new Action("var", Action.AS_CHECK_BOX) {
			public void run() {
				TreeViewer viewer = ((TreeViewer) SyntheticContextViewSection.this.getViewer());
				if (isChecked()) viewer.addFilter(varFilter);
				else viewer.removeFilter(varFilter);
			}
		};
		filterVarAction.setChecked(false);
		filterVarAction.setToolTipText("Filter variable elements");
		Action filterGrdAtion = new Action("grd", Action.AS_CHECK_BOX) {
			public void run() {
				TreeViewer viewer = ((TreeViewer) SyntheticContextViewSection.this.getViewer());
				if (isChecked()) viewer.addFilter(grdFilter);
				else viewer.removeFilter(grdFilter);
			}
		};
		filterGrdAtion.setChecked(false);
		filterGrdAtion.setToolTipText("Filter guard elements");
		form.getToolBarManager().add(filterVarAction);
		form.getToolBarManager().add(filterGrdAtion);
		form.updateToolBar();
	}
	
	
	/**
	 * Hook the actions to the menu
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(((StructuredViewer) getViewer()).getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Viewer viewer = getViewer();
		Menu menu = menuMgr.createContextMenu(((Viewer) viewer).getControl());
		((Viewer) viewer).getControl().setMenu(menu);
		this.editor.getSite().registerContextMenu(menuMgr, (ISelectionProvider) viewer);
	}
	
	
	/**
	 * Update the expanded of buttons.
	 */
	protected void updateButtons() {
		Tree tree = ((TreeViewer) getViewer()).getTree();
		TreeItem [] items = tree.getSelection();

		boolean hasOneSelection = items.length == 1;
		boolean canMoveUp = false;
		boolean canMoveDown = false;
		
		if (hasOneSelection) {
			TreeItem item = items[0];
			IRodinElement element = ((Leaf) item.getData()).getElement();
			TreeItem prev = TreeSupports.findPrevItem(tree, item);
			if (prev != null) {
				Leaf leaf = (Leaf) prev.getData();
				if (element.getElementType() == leaf.getElement().getElementType())
					canMoveUp = true;
			}
			TreeItem next = TreeSupports.findNextItem(tree, item);
			if (next != null) {
				Leaf leaf = (Leaf) next.getData();
				if (element.getElementType() == leaf.getElement().getElementType())
					canMoveDown = true;
			}
		}
        setButtonEnabled(
			UP_INDEX,
			hasOneSelection && canMoveUp);
		setButtonEnabled(
			DOWN_INDEX,
			hasOneSelection && canMoveDown);
		
		setButtonEnabled(ADD_SET_INDEX, true);
		setButtonEnabled(ADD_CST_INDEX, true);
		setButtonEnabled(ADD_AXM_INDEX, true);
		setButtonEnabled(ADD_THM_INDEX, true);
		
	}
	

	/**
	 * Method to response to button selection.
	 * <p>
	 * @param index The index of selected button
	 */
	protected void buttonSelected(int index) {
		switch (index) {
		case ADD_SET_INDEX:
			groupActionSet.addSet.run();
			break;
		case ADD_CST_INDEX:
			groupActionSet.addConstant.run();
			break;
		case ADD_AXM_INDEX:
			groupActionSet.addAxiom.run();
			break;
		case ADD_THM_INDEX:
			groupActionSet.addTheorem.run();
			break;
		case UP_INDEX:
			groupActionSet.handleUp.run();
			break;
		case DOWN_INDEX:
			groupActionSet.handleDown.run();
			break;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		((EventBEditableTreeViewer) this.getViewer()).elementChanged(event);
		updateButtons();
	}
	
	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.EventBPartWithButtons#edit(org.rodinp.core.IRodinElement)
	 */
	@Override
	protected void edit(IRodinElement element) {
		TreeViewer viewer = (TreeViewer) this.getViewer();
		viewer.reveal(element);
		TreeItem item  = TreeSupports.findItem(viewer.getTree(), element);
		if (element instanceof IUnnamedInternalElement) selectItem(item, 1);
		else if (element instanceof ICarrierSet) selectItem(item, 0);
		else if (element instanceof IConstant) selectItem(item, 0);
		else selectItem(item, 1);
	}

	@Override
	protected EventBEditableTreeViewer createTreeViewer(IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		return new SyntheticEditableTreeViewer(editor, parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
	 */
	@Override
	public void dispose() {
		editor.removeStatusListener(this);
		super.dispose();
	}

	
}