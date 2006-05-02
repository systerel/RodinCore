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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IInvariant;
import org.eventb.core.ITheorem;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IUnnamedInternalElement;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of Section Part for displaying and editting Sees clause.
 */
public class SyntheticMachineViewSection
	extends EventBTreePartWithButtons
	implements IStatusChangedListener
{

	// The indexes for different buttons.
	private static final int ADD_VAR_INDEX = 0;
	private static final int ADD_INV_INDEX = 1;
	private static final int ADD_THM_INDEX = 2;
	private static final int ADD_EVT_INDEX = 3;
	private static final int UP_INDEX = 4;
	private static final int DOWN_INDEX = 5;

	private static String [] buttonLabels =
	{"Add Var.", "Add Inv.", "Add Thm.", "Add Evt.", "Up", "Down"};

	// Title and description of the section.
	private final static String SECTION_TITLE = "Synthetics";
	private final static String SECTION_DESCRIPTION = "Synthetics View";
	
	private ViewerFilter varFilter;
	private ViewerFilter grdFilter;

	// The group of actions for the tree part.
	private ActionGroup groupActionSet;

	/**
     * Constructor.
     * <p>
     * @param editor The Form editor contains this section
     * @param page The Dependencies page contains this section
     * @param parent The composite parent
     */
	public SyntheticMachineViewSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit,
			int style, EventBEditor editor) {
		super(managedForm, parent, toolkit, style, editor, buttonLabels, SECTION_TITLE, SECTION_DESCRIPTION);

		makeActions();
		hookContextMenu();
		createToolBarActions(managedForm);
		editor.addStatusListener(this);
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
				TreeViewer viewer = ((TreeViewer) SyntheticMachineViewSection.this.getViewer());
				if (isChecked()) viewer.addFilter(varFilter);
				else viewer.removeFilter(varFilter);
			}
		};
		filterVarAction.setChecked(false);
		filterVarAction.setToolTipText("Filter variable elements");
		Action filterGrdAtion = new Action("grd", Action.AS_CHECK_BOX) {
			public void run() {
				TreeViewer viewer = ((TreeViewer) SyntheticMachineViewSection.this.getViewer());
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
	
	/*
	 * Create the actions that can be used in the tree.
	 */
	private void makeActions() {
		groupActionSet = new EventMasterSectionActionGroup(editor, (TreeViewer) this.getViewer());
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
		ISelection sel = ((ISelectionProvider) getViewer()).getSelection();
		Object [] selections = ((IStructuredSelection) sel).toArray();
		
		boolean hasOneSelection = selections.length == 1;

		setButtonEnabled(ADD_EVT_INDEX, true);
		setButtonEnabled(ADD_VAR_INDEX, true);
		setButtonEnabled(ADD_INV_INDEX, true);
		setButtonEnabled(ADD_THM_INDEX, true);
		setButtonEnabled(UP_INDEX, hasOneSelection);
		setButtonEnabled(DOWN_INDEX, hasOneSelection);
	}
	

	/**
	 * Method to response to button selection.
	 * <p>
	 * @param index The index of selected button
	 */
	protected void buttonSelected(int index) {
		switch (index) {
		case ADD_VAR_INDEX:
			handleAddVar();
			break;
		case ADD_INV_INDEX:
			handleAddInv();
			break;
		case ADD_THM_INDEX:
			handleAddThm();
			break;
		case ADD_EVT_INDEX:
			handleAddEvt();
			break;
		case UP_INDEX:
			handleUp();
			break;
		case DOWN_INDEX:
			handleDown();
			break;
		}
	}

	private void handleAddVar() {
		IRodinFile rodinFile = editor.getRodinInput();
		try {
			int counter = rodinFile.getChildrenOfType(IVariable.ELEMENT_TYPE).length;
			IRodinElement var = rodinFile.createInternalElement(IVariable.ELEMENT_TYPE, "var" + (counter+1), null, null);
			editor.addNewElement(var);
			edit(var);
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	private void handleAddInv() {
		IRodinFile rodinFile = editor.getRodinInput();
		try {
			int counter = rodinFile.getChildrenOfType(IInvariant.ELEMENT_TYPE).length;
			IInternalElement inv = rodinFile.createInternalElement(IInvariant.ELEMENT_TYPE, "inv" + (counter+1), null, null);
			inv.setContents(EventBUIPlugin.INV_DEFAULT);
			editor.addNewElement(inv);
			edit(inv);
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	private void handleAddThm() {
		IRodinFile rodinFile = editor.getRodinInput();
		try {
			int counter = rodinFile.getChildrenOfType(ITheorem.ELEMENT_TYPE).length;
			IInternalElement thm = rodinFile.createInternalElement(ITheorem.ELEMENT_TYPE, "thm" + (counter+1), null, null);
			thm.setContents(EventBUIPlugin.THM_DEFAULT);
			editor.addNewElement(thm);
			edit(thm);
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Handle add (new element) action.
	 */
	private void handleAddEvt() {
		IRodinFile rodinFile = editor.getRodinInput();
		try {
			int counter = rodinFile.getChildrenOfType(IEvent.ELEMENT_TYPE).length;
			String [] varNames = {"var1", "var2", "var3"};
			String [] grdNames = {"grd1", "grd2", "grd3"};
			String [] actions = {"act1", "act2", "act3"};
				
			IInternalElement event = rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, "evt" + (counter+1), null, null);
			editor.addNewElement(event);
			for (String varName : varNames) {
				IInternalElement var = event.createInternalElement(IVariable.ELEMENT_TYPE, varName, null, null);
				editor.addNewElement(var);
			}
				
			for (int i = 0; i < grdNames.length; i++) {
				IInternalElement grd = event.createInternalElement(IGuard.ELEMENT_TYPE, grdNames[i], null, null);
				grd.setContents(EventBUIPlugin.GRD_DEFAULT);
				editor.addNewElement(grd);
			}
				
			IInternalElement act = null;
			for (String action : actions) {
				act = event.createInternalElement(IAction.ELEMENT_TYPE, null, null, null);
				act.setContents(action);
				editor.addNewElement(act);
			}
			TreeViewer viewer = (TreeViewer) this.getViewer();
			Leaf leaf = (Leaf) TreeSupports.findItem(viewer.getTree(), event).getData();
			viewer.setExpandedState(leaf, true);
			
			viewer.reveal(TreeSupports.findItem(viewer.getTree(), act).getData());
			edit(event);
		}
		catch (RodinDBException e) {
			e.printStackTrace();
		}
	}
	

	/*
	 * Handle up action.
	 */
	private void handleUp() {
		UIUtils.debug("Up: To be implemented");
		return;
	}
	
	
	/*
	 * Handle down action. 
	 */
	private void handleDown() {
		UIUtils.debug("Down: To be implemented");
		return;
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
		else if (element instanceof IVariable) selectItem(item, 0);
		else if (element instanceof IEvent) selectItem(item, 0);
		else selectItem(item, 1);
	}

	@Override
	protected EventBEditableTreeViewer createTreeViewer(IManagedForm managedForm, FormToolkit toolkit, Composite parent) {
		return new SyntheticEditableTreeViewer(editor, parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.IStatusChangedListener#statusChanged(java.util.Collection)
	 */
	public void statusChanged(IRodinElement element) {
		((EventBEditableTreeViewer) this.getViewer()).statusChanged(element);
		updateButtons();
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