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

package org.eventb.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.IAction;
import org.eventb.core.IGuard;
import org.eventb.core.IVariable;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.Utils;
import org.eventb.ui.editors.EventBEditor;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author htson
 * <p>
 * The view shows data obtained from the model.
 * The view gets a list of models from the RodinDB.
 * The view is connected to the model using a content provider.
 */
public class ProjectExplorer
	extends ViewPart
{

	/**
	 * The plug-in identifier of the Project Explorer (value
	 * <code>"org.eventb.ui.views.ProjectExplorer"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID +".views.ProjectExplorer";
	
	// The tree viewer to display the structure of projects, constructs, etc.
	private TreeViewer viewer;

	// Action when double clicking. 
	private Action doubleClickAction;

	// Group of action that is used.
	private ProjectExplorerActionGroup groupActionSet;

	
	/**
	 * The constructor.
	 */
	public ProjectExplorer() {
	}

	
	/*
	 * @return the tree viewer.
	 */
	public TreeViewer getTreeViewer() {return viewer;}
	

	/**
	 * @author htson
	 * <p>
	 * This class sorts the projects by name (ignore other elements)
	 */
	private class ProjectsSorter extends ViewerSorter {
		
		public int compare(Viewer viewer, Object e1, Object e2) {
			if (e1 instanceof IRodinProject) {
				return super.compare(viewer, e1, e2);
			}
			else {
			    int cat1 = category(e1);
		        int cat2 = category(e2);
		        return cat1 - cat2;
			}
		}
		
		public int category(Object element) {
			if (element instanceof IVariable) return 1;
			if (element instanceof IGuard) return 2;
			if (element instanceof IAction) return 3;
			
			return 0;
		}
	}
	
	
	/*
	 * @author htson
	 * <p>
	 * Providing the label for object in the tree. 
	 */
	private class ViewLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			if (obj instanceof IRodinFile) {
				String name = ((IRodinFile) obj).getElementName();
				return Utils.getFileNameWithoutExtension(name);
			}
			if (obj instanceof IRodinElement) return ((IRodinElement) obj).getElementName();
			return obj.toString();
		}
		
		public Image getImage(Object obj) {
			return Utils.getImage(obj);
		}
	}
	
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ProjectExplorerContentProvider(this));
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new ProjectsSorter());
		viewer.setInput(EventBUIPlugin.getRodinDatabase());
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	
	/*
	 * Hook the actions to the context menu. 
	 */
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				groupActionSet.setContext(new ActionContext(viewer.getSelection()));
				groupActionSet.fillContextMenu(manager);
				groupActionSet.setContext(null);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	
	/*
	 * Put the actions to to the pull down menu and toolbar.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	
	/*
	 * Fill the local pull down menu with actions.
	 * <p> 
	 * @param manager A Menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		MenuManager newMenu = new MenuManager("&New");
		newMenu.add(ProjectExplorerActionGroup.newProjectAction);
		newMenu.add(ProjectExplorerActionGroup.newConstructAction);
		manager.add(newMenu);
		manager.add(new Separator());
		manager.add(ProjectExplorerActionGroup.deleteAction);
	}
	
	
	/**
	 * Fill the toolbar with actions.
	 * <p> 
	 * @param manager A Menu manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(ProjectExplorerActionGroup.newProjectAction);
		manager.add(ProjectExplorerActionGroup.newConstructAction);
		manager.add(ProjectExplorerActionGroup.deleteAction);
		manager.add(new Separator());
		ProjectExplorerActionGroup.drillDownAdapter.addNavigationActions(manager);
	}

	
	/*
	 * Creat the actions.
	 */
	private void makeActions() {
		groupActionSet = new ProjectExplorerActionGroup(this);

		// Double click to link with editor
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
			
				if (!(obj instanceof IRodinProject)) {
					linkToEditor(obj);
				}
			}
		};
	}

	
	/*
	 * Associate the double click action. 
	 */
	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}	
	
	
	/*
	 * Link the current object to an Event-B editor.
	 */
	public void linkToEditor(Object obj) {
		String editorId = EventBEditor.EDITOR_ID;
		IRodinFile construct;
		
		if (!(obj instanceof IRodinProject)) {
			construct = (IRodinFile) Utils.getOpenable(obj); 
			try {
				IEditorInput fileInput = new FileEditorInput(construct.getResource());
				EventBEditor editor = (EventBEditor) EventBUIPlugin.getActivePage().openEditor(fileInput, editorId);
				editor.setSelection(obj);
			} catch (PartInitException e) {
				MessageDialog.openError(null, null, "Error open the Event-B Editor");
				e.printStackTrace();
				// TODO EventBUIPlugin.logException(e);
			}
		}
		return;
	}

}