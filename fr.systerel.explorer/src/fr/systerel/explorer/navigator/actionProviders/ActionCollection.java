/*******************************************************************************
 * Copyright (c) 2008 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/


package fr.systerel.explorer.navigator.actionProviders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.YesToAllMessageDialog;
import org.eventb.internal.ui.projectexplorer.ProjectExplorerUtils;
import org.eventb.internal.ui.proofcontrol.ProofControl;
import org.eventb.internal.ui.prooftreeui.ProofTreeUI;
import org.eventb.internal.ui.wizards.NewComponentWizard;
import org.eventb.internal.ui.wizards.NewProjectWizard;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author Maria Husmann
 *
 */
public class ActionCollection {
	
	public static Action getOpenAction(final ICommonActionExtensionSite site) {
		Action doubleClickAction = new Action("Open") {
			@Override
			public void run() {
				ISelection selection = site.getStructuredViewer().getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();

				if (!(obj instanceof IRodinProject)) {
					if (obj instanceof IPSStatus) {
						selectPO((IPSStatus) obj);
					} else {
						UIUtils.linkToEventBEditor(obj);				
					}
				}
			}
		};
		return doubleClickAction;
		
	}
	
	/**
	 * 
	 * @param site
	 * @return	An action to rename a project
	 */
	public static Action getRenameAction(final ICommonActionExtensionSite site){
		Action renameAction = new Action() {
			@Override
			public void run() {
				//TODO
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				IRodinProject project = null;
				for (Iterator<?> it = sel.iterator(); it.hasNext();) {
					Object obj = it.next();
					if ((obj instanceof IRodinProject)) {
						project = (IRodinProject) obj;
					}
				}
				if (project != null) {
					IProject resource = project.getProject();
					try {
						IInputValidator validator= new IInputValidator() {
							public String isValid(String string) {
									IResource container = ResourcesPlugin.getWorkspace().getRoot()
									.findMember(new Path(string));
	
								if (string.length() == 0) {
									return ("Project name must be specified");
								}
								if (container != null) {
									return("A project with this name already exists.");
								}
									return null;
								}
							
						};
						InputDialog dialog = new InputDialog(site.getViewSite().getShell(),
								"Rename Project",
								"Please enter the new name of the project", "project",
								validator);
						dialog.open();
						final String bareName = dialog.getValue();
						if (dialog.getReturnCode() == InputDialog.CANCEL)
							return; // Cancel
						
						//TODO is there away to do this without closing the editors?
						closeOpenEditors(resource);
						IProjectDescription desc = resource.getDescription();
						desc.setName(bareName);
						resource.move(desc, true, null);
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		renameAction.setText("Rename");
		return renameAction;
	}
	
	public static Action getDeleteAction(final ICommonActionExtensionSite site) {
		Action deleteAction = new Action() {
			@Override
			public void run() {
				if (!(site.getStructuredViewer().getSelection().isEmpty())) {
		
					// Putting the selection into a set which does not contains any pair
					// of parent and child
					Collection<IRodinElement> set = new ArrayList<IRodinElement>();
		
					IStructuredSelection ssel = (IStructuredSelection) site.getStructuredViewer().getSelection();
		
					for (Iterator<?> it = ssel.iterator(); it.hasNext();) {
						Object obj = it.next();
						// Ignore element which is not Rodin Element
						if (!(obj instanceof IRodinElement))
							continue;
						else
							set = UIUtils.addToTreeSet(set, (IRodinElement) obj);
					}
		
					int answer = YesToAllMessageDialog.YES;
					for (IRodinElement element : set) {
						if (element instanceof IRodinProject) {
							IRodinProject rodinProject = (IRodinProject) element;
							// Confirmation dialog
							if (answer != YesToAllMessageDialog.YES_TO_ALL) {
								answer = YesToAllMessageDialog
										.openYesNoToAllQuestion(site.getViewSite()
												.getShell(), "Confirm Project Delete",
												"Are you sure you want to delete project '"
														+ rodinProject.getElementName()
														+ "' ?");
								if (ProjectExplorerUtils.DEBUG)
									ProjectExplorerUtils.debug("Answer: " + answer);
							}
							if (answer == YesToAllMessageDialog.NO_TO_ALL)
								break;
		
							if (answer != YesToAllMessageDialog.NO) {
								IProject project = rodinProject.getProject();
		
								try {
									// Close all the open file which is the children of
									// this project
									IRodinElement[] files = rodinProject.getChildren();
									for (IRodinElement file : files) {
										if (file instanceof IRodinFile)
											closeOpenedEditor((IRodinFile) file);
									}
		
									project.delete(true, true, null);
								} catch (PartInitException e) {
									e.printStackTrace();
								} catch (RodinDBException e) {
									e.printStackTrace();
								} catch (CoreException e) {
									e.printStackTrace();
								}
							}
						}
		
						else if (element instanceof IRodinFile) {
							if (answer != YesToAllMessageDialog.YES_TO_ALL) {
								answer = YesToAllMessageDialog.openYesNoToAllQuestion(
										site.getViewSite().getShell(),
										"Confirm File Delete",
										"Are you sure you want to delete file '"
												+ ((IRodinFile) element)
														.getElementName()
												+ "' in project '"
												+ element.getParent().getElementName()
												+ "' ?");
							}
							if (answer == YesToAllMessageDialog.NO_TO_ALL)
								break;
		
							if (answer != YesToAllMessageDialog.NO) {
								try {
									closeOpenedEditor((IRodinFile) element);
									((IRodinFile) element).delete(true,
											new NullProgressMonitor());
								} catch (PartInitException e) {
									e.printStackTrace();
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						}
					}
		
				}
			}
		};
		deleteAction.setText("&Delete");
		deleteAction.setToolTipText("Delete these elements");
		deleteAction.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_DELETE_PATH));
		
		return deleteAction;
	}
	
	public static Action getNewProjectAction(final ICommonActionExtensionSite site){
		Action newProjectAction = new Action() {
			@Override
			public void run() {
				BusyIndicator.showWhile(site.getViewSite().getShell().getDisplay(), new Runnable() {
					public void run() {
						NewProjectWizard wizard = new NewProjectWizard();
						WizardDialog dialog = new WizardDialog(EventBUIPlugin
								.getActiveWorkbenchShell(), wizard);
						dialog.create();
						dialog.open();
					}
				});
			}
		};
		newProjectAction.setText("&Project");
		newProjectAction.setToolTipText("Create new project");
		newProjectAction.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_PROJECT_PATH));
		return newProjectAction;
	}
	
	public static Action getNewComponentAction(final ICommonActionExtensionSite site){
		Action newComponentAction = new Action() {
			@Override
			public void run() {
				BusyIndicator.showWhile(site.getViewSite().getShell().getDisplay(), new Runnable() {
					public void run() {
						IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
						NewComponentWizard wizard = new NewComponentWizard();
						wizard.init(EventBUIPlugin.getDefault().getWorkbench(),
								sel);
						WizardDialog dialog = new WizardDialog(EventBUIPlugin
								.getActiveWorkbenchShell(), wizard);
						dialog.create();
						// SWTUtil.setDialogSize(dialog, 500, 500);
						dialog.open();
					}
				});
			}
		};
		newComponentAction.setText("&Component");
		newComponentAction.setToolTipText("Create new component");
		newComponentAction.setImageDescriptor(EventBImage
				.getImageDescriptor(IEventBSharedImages.IMG_NEW_COMPONENT_PATH));
		return newComponentAction;
	}
	
	public static RetryAutoProversAction getRetryAutoProversAction(final ICommonActionExtensionSite site) {
		return new RetryAutoProversAction(site.getStructuredViewer());
	}
	
	public static RecalculateAutoStatusAction getRecalculateAutoStatusAction(final ICommonActionExtensionSite site) {
		return new RecalculateAutoStatusAction(site.getStructuredViewer());
	}
	
	
	static void selectPO(IPSStatus ps) {
		UIUtils.linkToProverUI(ps);
		UIUtils.activateView(ProofControl.VIEW_ID);
		UIUtils.activateView(ProofTreeUI.VIEW_ID);
	}

	
	/**
	 * Close the open editor for a particular Rodin File
	 * 
	 * @param file
	 *            A Rodin File
	 * @throws PartInitException
	 *             Exception when closing the editor
	 */
	static void closeOpenedEditor(IRodinFile file) throws PartInitException {
		IEditorReference[] editorReferences = EventBUIPlugin.getActivePage()
				.getEditorReferences();
		for (int j = 0; j < editorReferences.length; j++) {
			IFile inputFile = (IFile) editorReferences[j].getEditorInput()
					.getAdapter(IFile.class);

			if (file.getResource().equals(inputFile)) {
				IEditorPart editor = editorReferences[j].getEditor(true);
				IWorkbenchPage page = EventBUIPlugin.getActivePage();
				page.closeEditor(editor, false);
			}
		}
	}
	
	/**
	 * Close the open editor for a particular Rodin Project
	 * 
	 * @param project A Rodin Project
	 * @throws PartInitException
	 *             Exception when closing the editor
	 */
	static void closeOpenEditors(IProject project) throws PartInitException {
		IEditorReference[] editorReferences = EventBUIPlugin.getActivePage()
		.getEditorReferences();
		for (int j = 0; j < editorReferences.length; j++) {
			IFile inputFile = (IFile) editorReferences[j].getEditorInput()
					.getAdapter(IFile.class);
		
			if (inputFile.getProject().equals(project)) {
				IEditorPart editor = editorReferences[j].getEditor(true);
				IWorkbenchPage page = EventBUIPlugin.getActivePage();
				page.closeEditor(editor, false);
			}
		}	
	}
	
}
