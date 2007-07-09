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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the tree
 *         viewer for the Project Explorer.
 */
public class MainActionGroup extends EventBEditableTreeViewerActionGroup {

	protected RefactorActionGroup refactorGroup;
	
	// private Clipboard clipboard;

	// Some actions and the drill down adapter
	public static DrillDownAdapter drillDownAdapter;

	/**
	 * Constructor: Create the actions.
	 * 
	 * @param editor
	 *            an Event-B Editor
	 * @param viewer
	 * 			  an editable tree viewer
	 */
	public MainActionGroup(IEventBEditor<?> editor, EventBEditableTreeViewer viewer) {
		// super(navigator);
		// resourceChangeListener = new IResourceChangeListener() {
		// public void resourceChanged(IResourceChangeEvent event) {
		// handleResourceChanged(event);
		// }
		// };
		// ResourcesPlugin.getWorkspace().addResourceChangeListener(
		// resourceChangeListener, IResourceChangeEvent.POST_CHANGE);
		super(editor, viewer);
		makeSubGroups();

		// clipboard = new Clipboard(projectExplorer.getSite().getShell()
		// .getDisplay());

		// copy = new Action() {
		//
		// @Override
		// public void run() {
		// BusyIndicator.showWhile(explorer.getTreeViewer().getTree()
		// .getDisplay(), new Runnable() {
		// public void run() {
		// IStructuredSelection selection = (IStructuredSelection) explorer
		// .getTreeViewer().getSelection();
		//
		// if (selection.size() == 0)
		// return;
		//
		// // Collect the element to be copied
		// Collection<IRodinElement> elements = new ArrayList<IRodinElement>();
		// for (Iterator it = selection.iterator(); it.hasNext();) {
		// Object obj = it.next();
		// if (obj instanceof TreeNode) {
		// IRodinElement[] children = ((TreeNode) obj)
		// .getChildren();
		// UIUtils.addToTreeSet(elements, children);
		// } else if (obj instanceof IRodinElement) {
		// UIUtils.addToTreeSet(elements,
		// (IRodinElement) obj);
		// }
		// }
		//
		// clipboard.setContents(
		// new Object[] { (IRodinElement[]) elements
		// .toArray(new IRodinElement[elements
		// .size()]) },
		// new Transfer[] { RodinElementTransfer
		// .getInstance() });
		// }
		// });
		// }
		//
		// };

		// paste = new Action() {
		// @Override
		// public void run() {
		// BusyIndicator.showWhile(explorer.getTreeViewer().getTree()
		// .getDisplay(), new Runnable() {
		// public void run() {
		// IStructuredSelection selection = (IStructuredSelection) explorer
		// .getTreeViewer().getSelection();
		//
		// // Only paste if there is one selection.
		// if (selection.size() == 1) {
		// final Object parent = selection.getFirstElement();
		//
		// final Object[] elements = (Object[]) clipboard
		// .getContents(RodinElementTransfer
		// .getInstance());
		//
		// // If there is nothing in the clipboard then return
		// if (elements == null)
		// return;
		//							
		// // Only paste Rodin File into rodin project
		// if (parent instanceof IRodinProject) {
		// // Checking if it contain some elements that
		// // cannot be paste
		// for (Object element : elements) {
		// if (!(element instanceof IRodinFile)) {
		// MessageDialog
		// .openError(explorer
		// .getViewSite()
		// .getShell(),
		// "Cannot Paste",
		// "Cannot paste non Rodin File element into a Rodin Project");
		// return;
		// }
		// }
		//
		// try {
		// RodinCore.run(new IWorkspaceRunnable() {
		//
		// public void run(IProgressMonitor monitor)
		// throws CoreException {
		// for (Object element : elements) {
		// UIUtils
		// .debugProjectExplorer("Pasted Element: "
		// + ((IRodinElement) element)
		// .getElementName()
		// + " to "
		// + ((IRodinElement) parent)
		// .getElementName());
		// }
		// }
		//
		// }, new NullProgressMonitor());
		// } catch (CoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return;
		// }
		//
		// if (parent instanceof IParent) {
		//
		//
		// for (Object element : elements) {
		// UIUtils
		// .debugProjectExplorer("Pasted Element: "
		// + ((IRodinElement) element)
		// .getElementName()
		// + " to "
		// + ((IRodinElement) parent)
		// .getElementName());
		// }
		// return;
		// }
		//
		// if (parent instanceof TreeNode) {
		// final TreeNode node = (TreeNode) parent;
		// String type = node.getType();
		//
		// // Checking if it contain some elements that
		// // cannot be paste
		// for (Object element : elements) {
		// if (!(element instanceof IRodinElement)
		// || !((IRodinElement) element)
		// .getElementType().equals(
		// type)) {
		// MessageDialog
		// .openError(explorer
		// .getViewSite()
		// .getShell(),
		// "Cannot Paste",
		// "Cannot paste element into this parent");
		// return;
		// }
		// }
		//
		// try {
		// RodinCore.run(new IWorkspaceRunnable() {
		//
		// public void run(IProgressMonitor monitor)
		// throws CoreException {
		// for (Object element : elements) {
		// ((IInternalElement) element)
		// .copy(
		// (IRodinElement) node.getParent(),
		// null,
		// null,
		// false,
		// new NullProgressMonitor());
		// }
		// }
		//
		// }, new NullProgressMonitor());
		// } catch (CoreException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// return;
		// }
		//
		// MessageDialog.openError(explorer.getViewSite()
		// .getShell(), "Cannot Paste",
		// "Cannot paste into the element");
		//
		// }
		// }
		// });
		// }
		// };
		// IActionBars bars = projectExplorer.getViewSite().getActionBars();
		// bars.setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
		// bars.setGlobalActionHandler(ActionFactory.PASTE.getId(), paste);
	}

	private void makeSubGroups() {
		refactorGroup = new RefactorActionGroup(editor, viewer);
	}

	/**
	 * Extends the superclass implementation to set the context in the
	 * subgroups.
	 */
	@Override
	public void setContext(ActionContext context) {
		super.setContext(context);
		// gotoGroup.setContext(context);
		// openGroup.setContext(context);
		refactorGroup.setContext(context);
		// sortAndFilterGroup.setContext(context);
		// workspaceGroup.setContext(context);
		// undoRedoGroup.setContext(context);
	}

    /**
     * Adds the actions in this group and its subgroups to the action bars.
     */
    @Override
	public void fillActionBars(IActionBars actionBars) {
//        actionBars.setGlobalActionHandler(ActionFactory.PROPERTIES.getId(),
//                propertyDialogAction);
//        actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(),
//                addBookmarkAction);
//        actionBars.setGlobalActionHandler(IDEActionFactory.ADD_TASK.getId(),
//                addTaskAction);

//        gotoGroup.fillActionBars(actionBars);
//        openGroup.fillActionBars(actionBars);
        refactorGroup.fillActionBars(actionBars);
//        workingSetGroup.fillActionBars(actionBars);
//        sortAndFilterGroup.fillActionBars(actionBars);
//        workspaceGroup.fillActionBars(actionBars);
//        undoRedoGroup.fillActionBars(actionBars);

//        IMenuManager menu = actionBars.getMenuManager();
//        menu.add(toggleLinkingAction);

//        IToolBarManager toolBar = actionBars.getToolBarManager();
//        toolBar.add(new Separator());
//        toolBar.add(collapseAllAction);
//        toolBar.add(toggleLinkingAction);
    }
    
    /**
     * Updates the actions which were added to the action bars,
     * delegating to the subgroups as necessary.
     */
    @Override
	public void updateActionBars() {
//        IStructuredSelection selection = (IStructuredSelection) getContext()
//                .getSelection();
//        propertyDialogAction.setEnabled(selection.size() == 1);
//        addBookmarkAction.selectionChanged(selection);
//        addTaskAction.selectionChanged(selection);
//
//        gotoGroup.updateActionBars();
//        openGroup.updateActionBars();
        refactorGroup.updateActionBars();
//        workingSetGroup.updateActionBars();
//        sortAndFilterGroup.updateActionBars();
//        workspaceGroup.updateActionBars();
//        undoRedoGroup.updateActionBars();
    }
    
    /**
     * Handles a key pressed event by invoking the appropriate action,
     * delegating to the subgroups as necessary.
     */
    @Override
	public void handleKeyPressed(KeyEvent event) {
        refactorGroup.handleKeyPressed(event);
//        workspaceGroup.handleKeyPressed(event);
    }
    
    /**
	 * Dynamically fill the context menu (depends on the selection).
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	@Override
	public void fillContextMenu(IMenuManager menu) {
//		IStructuredSelection selection = (IStructuredSelection) getContext()
//				.getSelection();

//		MenuManager newMenu = new MenuManager(
//				ProjectExplorerMessages.ProjectExplorer_new);
//		menu.add(newMenu);
//		newMenu.add(newProjectAction);
//		newMenu.add(newComponentAction);

//		gotoGroup.fillContextMenu(menu);
//		openGroup.fillContextMenu(menu);
		menu.add(new Separator("refactor"));

		refactorGroup.fillContextMenu(menu);
		menu.add(new Separator());

		menu.add(new Separator("modelling"));
		
		menu.add(new Separator("proving"));
//		menu.add(importAction);
//		menu.add(exportAction);
//		importAction.selectionChanged(selection);
//		exportAction.selectionChanged(selection);
		menu.add(new Separator());

//		workspaceGroup.fillContextMenu(menu);

		menu.add(new Separator());
		drillDownAdapter.addNavigationActions(menu);

		// Other plug-ins can contribute there actions here
		menu.add(new Separator());
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		super.fillContextMenu(menu);

//		menu
//				.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS
//						+ "-end")); //$NON-NLS-1$

//		if (selection.size() == 1) {
//			propertyDialogAction.selectionChanged(selection);
//			menu.add(propertyDialogAction);
//		}

//		ISelection sel = getContext().getSelection();
//		Object input = getContext().getInput();
//		if (sel instanceof IStructuredSelection) {
//			MenuManager newMenu = new MenuManager("&New");
//
//			IStructuredSelection ssel = (IStructuredSelection) sel;
//
//			// Can only create new Project if at the Workspace level
//			if (input == null) {
//				newMenu.add(newProjectAction);
//				if (ssel.size() == 1) {
//					newMenu.add(newComponentAction);
//				}
//			} else {
//				newMenu.add(newComponentAction);
//			}
//			newMenu.add(new Separator("new"));
//			menu.add(newMenu);
//			menu.add(new Separator());
//			// menu.add(refreshAction);
//
//			menu.add(new Separator());
//
//
//			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//		}
	}

	@Override
	protected void makeActions() {
		// Do nothing
	}

	   /**
     * Extends the superclass implementation to dispose the 
     * actions in this group and its subgroups.
     */
    @Override
	public void dispose() {
//        ResourcesPlugin.getWorkspace().removeResourceChangeListener(
//                resourceChangeListener);

//        newWizardMenu.dispose();
//        collapseAllAction.dispose();
//        exportAction.dispose();
//        importAction.dispose();
//        propertyDialogAction.dispose();
//        toggleLinkingAction.dispose();
//
//        gotoGroup.dispose();
//        openGroup.dispose();
        refactorGroup.dispose();
//        sortAndFilterGroup.dispose();
//        workingSetGroup.dispose();
//        workspaceGroup.dispose();
//        undoRedoGroup.dispose();
        super.dispose();
    }
}
