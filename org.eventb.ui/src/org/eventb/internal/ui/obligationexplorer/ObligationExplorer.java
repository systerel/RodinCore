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

package org.eventb.internal.ui.obligationexplorer;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.ViewPart;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IAction;
import org.eventb.core.IGuard;
import org.eventb.core.IPRFile;
import org.eventb.core.IPROOF;
import org.eventb.core.IPRSequent;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.prover.ProofControl;
import org.eventb.internal.ui.prover.ProofTreeUI;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * The view shows data obtained from the model.
 * The view gets a list of models from the RodinDB.
 * The view is connected to the model using a content provider.
 */
public class ObligationExplorer
	extends ViewPart
	implements ISelectionChangedListener
{

	/**
	 * The plug-in identifier of the Obligation Explorer (value
	 * <code>"org.eventb.ui.views.ObligationExplorer"</code>).
	 */
	public static final String VIEW_ID = EventBUIPlugin.PLUGIN_ID +".views.ObligationExplorer";
	
	// The tree viewer to display the structure of projects, components, etc.
	private TreeViewer viewer;

	// Action when double clicking. 
	private Action doubleClickAction;

	// Group of action that is used.
	private ObligationExplorerActionGroup groupActionSet;

	
	/**
	 * The constructor.
	 */
	public ObligationExplorer() {
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
			if (obj instanceof IRodinProject) return ((IRodinProject) obj).getElementName();
			if (obj instanceof IRodinFile) {
				String name = ((IRodinFile) obj).getElementName();
				return EventBPlugin.getComponentName(name);
			}
			if (obj instanceof IPRSequent) return ((IPRSequent) obj).getName();
			
			return obj.toString();
		}
		
		public Image getImage(Object obj) {
			ImageRegistry registry = EventBUIPlugin.getDefault().getImageRegistry();
			if (obj instanceof IPRSequent) {
				IPRSequent ps = (IPRSequent) obj;
				try {
					IPROOF status = ps.getProof();
					if (status.getContents().equals("PENDING")) return registry.get(EventBImage.IMG_PENDING);
					else if (status.getContents().equals("DISCHARGED")) return registry.get(EventBImage.IMG_DISCHARGED);
				}
				catch (RodinDBException e) {
					e.printStackTrace();
				}
			}
			if (obj instanceof IPRFile) {
				IPRFile prFile = (IPRFile) obj;
				if (prFile.getMachine().exists()) return registry.get(EventBImage.IMG_MACHINE);
				else if (prFile.getContext().exists()) return registry.get(EventBImage.IMG_CONTEXT);
			}
			return UIUtils.getImage(obj);
		}
	}
	
	
	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ObligationExplorerContentProvider(this));
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new ProjectsSorter());
		viewer.setInput(EventBUIPlugin.getRodinDatabase());
		viewer.addSelectionChangedListener(this);
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
		manager.add(newMenu);
		manager.add(new Separator());
		manager.add(ObligationExplorerActionGroup.refreshAction);
	}
	
	
	/**
	 * Fill the toolbar with actions.
	 * <p> 
	 * @param manager A Menu manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new Separator());
		ObligationExplorerActionGroup.drillDownAdapter.addNavigationActions(manager);
	}

	
	/*
	 * Creat the actions.
	 */
	private void makeActions() {
		groupActionSet = new ObligationExplorerActionGroup(this);

		// Double click to link with editor
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
			
				if (obj instanceof IPRSequent) {
					IPRSequent ps = (IPRSequent) obj;
					try {
						if (!ps.isDischarged()) {				
							UIUtils.linkToProverUI(ps);
							UIUtils.activateView(ProofControl.VIEW_ID);
							UIUtils.activateView(ProofTreeUI.VIEW_ID);
						}
						else {
							YesNoDialog dialog = new YesNoDialog(viewer.getControl().getShell(), "Re-prove the obligation", "The obligation has been proved. Do you want to re-prove?");
							dialog.open();
							boolean answer = dialog.getAnswer();
							if (answer) {
								UIUtils.linkToProverUI(ps);
								UIUtils.activateView(ProofControl.VIEW_ID);
								UIUtils.activateView(ProofTreeUI.VIEW_ID);
							}
						}
					}
					catch (RodinDBException e) {
						e.printStackTrace();
					}
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
	
	public void refresh() {
		UIUtils.debug("********** REFRESH *********");
		viewer.refresh();
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		doubleClickAction.run();
	}	
	
}