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

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.IAction;
import org.eventb.core.IEvent;
import org.eventb.core.IGuard;
import org.eventb.core.IMachine;
import org.eventb.core.IVariable;
import org.eventb.eventBKeyboard.preferences.PreferenceConstants;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IParent;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Table part with buttons
 * for displaying constants (used as master section in Master-Detail block).
 */
public class EventMasterSection 
	extends EventBTreePartWithButtons
{
	// The indexes for different buttons.
	private static final int ADD_EVT_INDEX = 0;
	private static final int ADD_VAR_INDEX = 1;
	private static final int ADD_GRD_INDEX = 2;
	private static final int ADD_ACT_INDEX = 3;
	private static final int UP_INDEX = 4;
	private static final int DOWN_INDEX = 5;

	private static final String [] buttonLabels =
		{"Add Event", "Add Var.", "Add Guard", "Add Action", "Up", "Down"};

	// The group of actions for the tree part.
	private ActionGroup groupActionSet;
	

	/**
	 * The content provider class. 
	 */
	class MasterContentProvider
	implements IStructuredContentProvider, ITreeContentProvider
	{
		private IMachine invisibleRoot = null;
		
		public Object getParent(Object child) {
			if (child instanceof IRodinElement) return ((IRodinElement) child).getParent();
			return null;
		}
		
		public Object[] getChildren(Object parent) {
			if (parent instanceof IMachine) {
				try {
					return ((IMachine) parent).getEvents();
				}
				catch (RodinDBException e) {
					// TODO Exception handle
					e.printStackTrace();
				}
			}
			if (parent instanceof IParent) {
				try {
					return ((IParent) parent).getChildren();
				}
				catch (RodinDBException e) {
					// TODO Exception handle
					e.printStackTrace();
				}
			}
			return new Object[0];
		}
		public boolean hasChildren(Object parent) {
			return getChildren(parent).length > 0;
		}
		
		public Object[] getElements(Object parent) {
			if (parent instanceof IRodinFile) {
				if (invisibleRoot == null) {
					invisibleRoot = (IMachine) parent;
					return getChildren(invisibleRoot);
				}
			}
			return getChildren(parent);
		}
		
		public void dispose() {
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			invisibleRoot = null;
		}
	}
	
	
	/**
	 * @author htson
	 * This class provides the label for different elements in the tree.
	 */
	class MasterLabelProvider 
		implements  ITableLabelProvider, ITableFontProvider, ITableColorProvider {
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return UIUtils.getImage(element);
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IAction) {
				try {
					return ((IAction) element).getContents();
				}
				catch (RodinDBException e) {
					// TODO Handle Exception
					e.printStackTrace();
					return "";
				}
			}
			if (element instanceof IInternalElement) return ((IInternalElement) element).getElementName();
			return element.toString();
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
		 */
		public void dispose() {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
		 */
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
		 */
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
		 */
		public Color getBackground(Object element, int columnIndex) {
			 Display display = Display.getCurrent();
             return display.getSystemColor(SWT.COLOR_WHITE);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
		 */
		public Color getForeground(Object element, int columnIndex) {
			Display display = Display.getCurrent();
            return display.getSystemColor(SWT.COLOR_BLACK);
       }

//		public String getText(Object obj) {
//			if (obj instanceof IAction) {
//				try {
//					return ((IAction) obj).getContents();
//				}
//				catch (RodinDBException e) {
//					// TODO Handle Exception
//					e.printStackTrace();
//					return "";
//				}
//			}
//			if (obj instanceof IInternalElement) return ((IInternalElement) obj).getElementName();
//			return obj.toString();
//		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object, int)
		 */
		public Font getFont(Object element, int columnIndex) {
			UIUtils.debug("Get fonts");
			return JFaceResources.getFont(PreferenceConstants.EVENTB_MATH_FONT);
		}
		
		
//		public Image getImage(Object obj) {
//			return UIUtils.getImage(obj);
//		}
	
	
	
	}
	
	
	/**
	 * @author htson
	 * This class sorts the elements by types.
	 */
	private class ElementsSorter extends ViewerSorter {
		
		public int compare(Viewer viewer, Object e1, Object e2) {
	        int cat1 = category(e1);
	        int cat2 = category(e2);
	        return cat1 - cat2;
		}
		
		public int category(Object element) {
			if (element instanceof IVariable) return 1;
			if (element instanceof IGuard) return 2;
			if (element instanceof IAction) return 3;
			
			return 0;
		}
	}
	

	/**
	 * Contructor.
	 * <p>
	 * @param managedForm The form to create this master section
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create this master section
	 * @param style The style
	 * @param block The master detail block which this master section belong to
	 */
	public EventMasterSection(IManagedForm managedForm, Composite parent, FormToolkit toolkit, 
			int style, EventBMasterDetailsBlock block) {
		super(managedForm, parent, toolkit, style, block, buttonLabels);
		
		makeActions();
		hookContextMenu();
		getViewer().setSorter(new ElementsSorter());
	}
	
	
	/*
	 * Create the actions that can be used in the tree.
	 */
	private void makeActions() {
		groupActionSet = new EventMasterSectionActionGroup(this);
	}
	
	
	/**
	 * Hook the actions to the menu
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(getViewer().getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Viewer viewer = getViewer();
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		this.getBlock().getPage().getSite().registerContextMenu(menuMgr, viewer);
	}

	
	/*
	 * Handle add (new element) action.
	 */
	private void handleAddEvent() {
		UIUtils.newEvent(rodinFile);
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
	
	
	/**
	 * Update the status of buttons.
	 */
	protected void updateButtons() {
		ISelection sel = getViewer().getSelection();
		Object [] selections = ((IStructuredSelection) sel).toArray();
		
		boolean hasOneSelection = selections.length == 1;
		
		boolean anEventSelected = hasOneSelection && (selections[0] instanceof IEvent);
		
		boolean notInitSelected = anEventSelected && !((IEvent) selections[0]).getElementName().equals("INITIALISATION");
		
		setButtonEnabled(ADD_EVT_INDEX, true);
		setButtonEnabled(ADD_VAR_INDEX, anEventSelected && notInitSelected);
		setButtonEnabled(ADD_GRD_INDEX, anEventSelected && notInitSelected);
		setButtonEnabled(ADD_ACT_INDEX, anEventSelected);
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
			case ADD_EVT_INDEX:
				handleAddEvent();
				break;
			case ADD_VAR_INDEX:
				EventMasterSectionActionGroup.newLocalVariable.run();
				break;
			case ADD_GRD_INDEX:
				EventMasterSectionActionGroup.newGuard.run();
				break;
			case ADD_ACT_INDEX:
				EventMasterSectionActionGroup.newAction.run();
				break;
			case UP_INDEX:
				handleUp();
				break;
			case DOWN_INDEX:
				handleDown();
				break;
		}
	}
	

	/**
	 * Setting the input for the (table) viewer.
	 */
	protected void setViewerInput() {
		TreeViewer viewer = this.getViewer();
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(false);
		TreeColumn column = new TreeColumn(tree, SWT.LEFT);
		column.setText("Testing");
		column.setResizable(true);
		column.setWidth(150);
		viewer.setLabelProvider(new MasterLabelProvider());
		viewer.setContentProvider(new MasterContentProvider());
		rodinFile = ((EventBEditor) this.getBlock().getPage().getEditor()).getRodinInput();
		viewer.setInput(rodinFile);
		viewer.refresh();
	}


	/**
	 * Set the selection in the tree viewer.
	 * <p>
	 * @param element A Rodin element
	 */
	public void setSelection(IRodinElement element) {
		TreeViewer viewer = this.getViewer();
		viewer.setSelection(new StructuredSelection(element));
		//EventBMachineEditor editor = (EventBMachineEditor) getBlock().getPage().getEditor();
		//editor.getContentOutlinePage().setRodinElementSelection(element);
	}

	
	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		processDelta(delta);
	}
	
	private void processDelta(IRodinElementDelta delta) {
		IRodinElement element= delta.getElement();
		if (element instanceof IRodinFile) {
			IRodinElementDelta [] deltas = delta.getAffectedChildren();
			for (int i = 0; i < deltas.length; i++) {
				processDelta(deltas[i]);
			}

			return;
		}
		if (element instanceof IEvent) {
			UIUtils.postRunnable(new Runnable() {
				public void run() {
					getViewer().setInput(rodinFile);
					markDirty();
					updateButtons();
				}
			}, this.getSection().getClient());
		}
		else {
			return;
		}
	}
}
