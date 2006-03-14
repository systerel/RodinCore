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

package org.eventb.internal.ui.projectexplorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eventb.core.EventBPlugin;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBImageDescriptor;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.ProvingPerspective;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.wizards.NewComponentWizard;
import org.eventb.internal.ui.wizards.NewProjectWizard;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 * <p>
 * This class provides the actions that will be used with the tree viewer 
 * for the Project Explorer.
 */
public class ProjectExplorerActionGroup 
	extends ActionGroup 
{

	// The project explorer.
	private ProjectExplorer explorer;
	
	// Some actions and the drill down adapter
	public static DrillDownAdapter drillDownAdapter;
	public static Action newProjectAction;
	public static Action newComponentAction;
	public static Action deleteAction;
	public static Action proveAction;
	public static RefreshAction refreshAction;
	
	/**
	 * Constructor: Create the actions.
	 * @param projectExplorer The project explorer
	 */
	public ProjectExplorerActionGroup(ProjectExplorer projectExplorer) {
		this.explorer = projectExplorer;
		drillDownAdapter = new DrillDownAdapter(explorer.getTreeViewer());
		
		
		refreshAction = new RefreshAction(projectExplorer.getSite().getShell());
		
		// Creating the public action
		newProjectAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(explorer.getTreeViewer().getTree().getDisplay(),
						new Runnable() {
							public void run() {
								NewProjectWizard wizard = new NewProjectWizard();
								WizardDialog dialog = new WizardDialog(EventBUIPlugin.getActiveWorkbenchShell(), wizard);
								dialog.create();
								dialog.open();
							}
						});
			}
		};
		newProjectAction.setText("&Project");
		newProjectAction.setToolTipText("Create new project");
		newProjectAction.setImageDescriptor(new EventBImageDescriptor(EventBImage.IMG_NEW_PROJECT));

		newComponentAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(explorer.getTreeViewer().getTree().getDisplay(),
					new Runnable() {
						public void run() {
							IStructuredSelection sel = (IStructuredSelection) explorer.getTreeViewer().getSelection();
							NewComponentWizard wizard = new NewComponentWizard();
							wizard.init(EventBUIPlugin.getDefault().getWorkbench(), sel);
							WizardDialog dialog = new WizardDialog(EventBUIPlugin.getActiveWorkbenchShell(), wizard);
							dialog.create();
							//SWTUtil.setDialogSize(dialog, 500, 500);
							dialog.open();
						}
					});
			}
		};
		newComponentAction.setText("&Component");
		newComponentAction.setToolTipText("Create new component");
		newComponentAction.setImageDescriptor(new EventBImageDescriptor(EventBImage.IMG_NEW_COMPONENT));
	
		deleteAction = new Action() {
			public void run() {
				ISelection selection = explorer.getTreeViewer().getSelection();
				if (!(selection.isEmpty())) {
					IStructuredSelection ssel = (IStructuredSelection) selection;
					Object [] slist = ssel.toArray();
				
					for (int i = 0; i < slist.length; i++) {
						UIUtils.debug(slist[i].toString() + " : " + slist[i].getClass().toString());
						if (slist[i] instanceof IRodinProject) {
							IRodinProject rodinProject = (IRodinProject) slist[i];
							IProject project = rodinProject.getProject();
							
							try {
								IRodinElement [] files =  rodinProject.getChildren();
								for (int j = 0; j < files.length; j++) {
									if (files[j] instanceof IRodinFile) 
										closeOpenedEditor((IRodinFile) files[j]);
								}
							
								project.delete(true, true, null);
							}
							catch (PartInitException e) {
								e.printStackTrace();
							}
							catch (RodinDBException e) {
								e.printStackTrace();
							}
							catch (CoreException e) {
								e.printStackTrace();
							}
						}
					
						else if (slist[i] instanceof IRodinFile) {
							try {
								closeOpenedEditor((IRodinFile) slist[i]);
								((IRodinFile) slist[i]).delete(true, new NullProgressMonitor());
							}
							catch (PartInitException e) {
								e.printStackTrace();
							}
							catch (RodinDBException e) {
								e.printStackTrace();
							}
						}
					}
				//viewer.refresh();
				}
			}
		};
		deleteAction.setText("Delete");
		deleteAction.setToolTipText("Delete selected elements");
		deleteAction.setImageDescriptor(new EventBImageDescriptor(EventBImage.IMG_DELETE));
		
		proveAction = new Action() {
			public void run() {
				ISelection selection = explorer.getTreeViewer().getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection ssel = (IStructuredSelection) selection;
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						if (!(obj instanceof IRodinFile)) return;
						IRodinFile component = (IRodinFile) obj;
						IRodinProject prj = component.getRodinProject();
						String bareName = EventBPlugin.getComponentName(component.getElementName());
						IRodinFile prFile = prj.getRodinFile(EventBPlugin.getPRFileName(bareName));
						UIUtils.linkToProverUI(prFile);
						try {
							EventBUIPlugin.getActiveWorkbenchWindow().getWorkbench()
								.showPerspective(ProvingPerspective.PERSPECTIVE_ID, EventBUIPlugin.getActiveWorkbenchWindow());
						}
						catch (WorkbenchException e) {
							e.printStackTrace();
						}
//						UIUtils.activateView(ProofTreeUI.VIEW_ID);
//						UIUtils.activateView(ProofControl.VIEW_ID);
//						UIUtils.activateView(ProofInformation.VIEW_ID);
//						UIUtils.activateView(ObligationExplorer.VIEW_ID);
					}
				}
			}
		};
		proveAction.setText("Prove");
		proveAction.setToolTipText("Start the prover");
		proveAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
	}
	
	
	/*
	 * Close the open editor for a particular Rodin File 
	 * @param file A Rodin File
	 * @throws PartInitException Exception when closing the editor
	 */
	private void closeOpenedEditor(IRodinFile file) throws PartInitException {
		IEditorReference [] editorReferences = EventBUIPlugin.getActivePage().getEditorReferences();
		for (int j = 0; j < editorReferences.length; j++) {
			IFile inputFile = (IFile) editorReferences[j].getEditorInput().getAdapter(IFile.class);
	
			if (file.getResource().equals(inputFile)) {
				IEditorPart editor = editorReferences[j].getEditor(true);
				IWorkbenchPage page = EventBUIPlugin.getActivePage(); 
				page.closeEditor(editor, false);
			}
		}
	}

	
	/* 
	 * Dynamically fill the context menu (depends on the selection).
	 * <p> 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		Object input = getContext().getInput();
		if (sel instanceof IStructuredSelection) {
			MenuManager newMenu = new MenuManager("&New");
			
			IStructuredSelection ssel = (IStructuredSelection) sel;
			if (input == null) {   // Can only create new Project if at the Workspace level
				newMenu.add(newProjectAction);
				if (ssel.size() == 1) {
					newMenu.add(newComponentAction);
				}
			}
			else {
				newMenu.add(newComponentAction);
			}
			newMenu.add(new Separator());
			
			menu.add(newMenu);
			menu.add(deleteAction);
			menu.add(refreshAction);
			if ((ssel.size() == 1) && (ssel.getFirstElement() instanceof IRodinFile)) menu.add(proveAction);
			menu.add(new Separator());
			drillDownAdapter.addNavigationActions(menu);
			
			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

}
