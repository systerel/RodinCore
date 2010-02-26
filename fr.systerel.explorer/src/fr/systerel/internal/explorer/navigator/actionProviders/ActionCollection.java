/*******************************************************************************
 * Copyright (c) 2009, 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License  v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
  *******************************************************************************/
package fr.systerel.internal.explorer.navigator.actionProviders;

import static fr.systerel.internal.explorer.navigator.ExplorerUtils.factory;
import static org.eventb.ui.EventBUIPlugin.PROOF_CONTROL_VIEW_ID;
import static org.eventb.ui.EventBUIPlugin.PROOF_TREE_VIEW_ID;

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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eventb.core.IPSStatus;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.YesToAllMessageDialog;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.wizards.NewComponentWizard;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

import fr.systerel.internal.explorer.navigator.ExplorerUtils;
import fr.systerel.internal.explorer.navigator.wizards.NewProjectWizard;

/**
 * A collection of actions for the navigator
 *
 */
public class ActionCollection {
	
	/**
	 * Provides an open action for various elements in Rodin 
	 * (Machines, Contexts, Invariants, ProofObligations...)
	 * @param site
	 * @return An open action
	 */
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
						UIUtils.linkToPreferredEditor(obj);				
					}
				}
			}
		};

		return doubleClickAction;
		
	}
	
	/**
	 * 
	 * @param site
	 * @return an action that copies projects
	 */
	public static Action getCopyProjectAction(final ICommonActionExtensionSite site) {
		Action copyAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if ((sel.getFirstElement() instanceof IRodinProject)) {
					IRodinProject project = (IRodinProject) sel.getFirstElement();
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
								"Copy Project",
								"Please enter the new name of the project", "Copy of "+project.getElementName(),
								validator);
						dialog.open();
						final String bareName = dialog.getValue();
						if (dialog.getReturnCode() == InputDialog.CANCEL)
							return; // Cancel

						IProjectDescription desc = resource.getDescription();
						desc.setName(bareName);
						resource.copy(desc, false, null);
					} catch (CoreException e) {
						MessageDialog.openError(null, "Error", 
						"Could not copy project. Make sure there is no resource with the same name.");
					}
				}
			}
		};	
		copyAction.setText("Copy");
		return copyAction;
	}
	
	
	/**
	 * Produces an action for renaming projects
	 * @param site
	 * @return	The action to rename a project
	 */
	public static Action getRenameProjectAction(final ICommonActionExtensionSite site){
		Action renameAction = new Action() {
			@Override
			public void run() {
				IStructuredSelection sel = (IStructuredSelection) site.getStructuredViewer().getSelection();
				if ((sel.getFirstElement() instanceof IRodinProject)) {
					IRodinProject project = (IRodinProject) sel.getFirstElement();
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
						
						//is there away to do this without closing the editors?
						closeOpenEditors(resource);
						IProjectDescription desc = resource.getDescription();
						desc.setName(bareName);
						resource.move(desc, false, null);
					} catch (CoreException e) {
						MessageDialog.openError(null, "Error", 
								"Could not rename project. Make sure there is no resource with the same name.");

					}
				}
			}
		};
		renameAction.setText("Rename");
		return renameAction;
	}
	
	/**
	 * 
	 * @param site
	 * @return An action for deleting rodin project or rodin files
	 */
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
						final Object obj = it.next();
						if (!(obj instanceof IRodinElement)) {
							continue;
						}
						IRodinElement elem = (IRodinElement) obj;
						if (elem.isRoot()) {
							elem = elem.getParent();
						}
						set = UIUtils.addToTreeSet(set, elem);
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
								if (ExplorerUtils.DEBUG)
									ExplorerUtils.debug("Answer: " + answer);
							}
							if (answer == YesToAllMessageDialog.NO_TO_ALL)
								break;
		
							if (answer != YesToAllMessageDialog.NO) {
								IProject project = rodinProject.getProject();
		
								try {
									// Close all the open files which are children of
									// this project
									final IRodinFile[] rfs = rodinProject
											.getRodinFiles();
									for (IRodinFile rf : rfs) {
										closeOpenedEditor(rf);
									}
		
									project.delete(true, false, null);
								} catch (PartInitException e) {
									MessageDialog.openError(null, "Error", "Could not delete project");
								} catch (RodinDBException e) {
									MessageDialog.openError(null, "Error", "Could not delete project");
								} catch (CoreException e) {
									MessageDialog.openError(null, "Error", "Could not delete project");
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
									MessageDialog.openError(null, "Error", "Could not delete file");
								} catch (RodinDBException e) {
									MessageDialog.openError(null, "Error", "Could not delete file");
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
	
	/**
	 * 
	 * @param site
	 * @return an Action for creating new Projects
	 */
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
	
	/**
	 * 
	 * @param site
	 * @return An action for creating new components.
	 */
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
	
	public static ReplayUndischargedAction getReplayUndischargedAction(ICommonActionExtensionSite site) {
		return new ReplayUndischargedAction(site.getStructuredViewer(), factory);
	}
	
	static void selectPO(IPSStatus ps) {
		final IRodinFile component = (IRodinFile) UIUtils.getOpenable(ps);
		final IEditorDescriptor desc = IDE.getDefaultEditor(component
				.getResource());
		if (desc.getId().equals(ProverUI.EDITOR_ID))
			UIUtils.linkToProverUI(ps);
		else
			UIUtils.linkToPreferredEditor(ps);
		UIUtils.activateView(PROOF_CONTROL_VIEW_ID);
		UIUtils.activateView(PROOF_TREE_VIEW_ID);
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
	 * Close the open editors for a particular Rodin Project
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
