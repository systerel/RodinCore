/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.projectexplorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eventb.core.IEvent;
import org.eventb.core.IMachineFile;
import org.eventb.core.IRefinesEvent;
import org.eventb.core.IRefinesMachine;
import org.eventb.core.ISeesContext;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBImageDescriptor;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.ProvingPerspective;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.wizards.NewComponentWizard;
import org.eventb.internal.ui.wizards.NewProjectWizard;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class provides the actions that will be used with the tree
 *         viewer for the Project Explorer.
 */
public class ProjectExplorerActionGroup extends ActionGroup {

	// The project explorer.
	private ProjectExplorer explorer;

	// Some actions and the drill down adapter
	public static DrillDownAdapter drillDownAdapter;

	public static Action newProjectAction;

	public static Action newComponentAction;

	public static Action deleteAction;

	public static Action proveAction;

	public static Action refineAction;

	public static RefreshAction refreshAction;

	/**
	 * Constructor: Create the actions.
	 * 
	 * @param projectExplorer
	 *            The project explorer
	 */
	public ProjectExplorerActionGroup(ProjectExplorer projectExplorer) {
		this.explorer = projectExplorer;
		drillDownAdapter = new DrillDownAdapter(explorer.getTreeViewer());

		refreshAction = new RefreshAction(projectExplorer.getSite().getShell());

		// Creating the public action
		newProjectAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(explorer.getTreeViewer().getTree()
						.getDisplay(), new Runnable() {
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
		newProjectAction.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_PROJECT));

		newComponentAction = new Action() {
			public void run() {
				BusyIndicator.showWhile(explorer.getTreeViewer().getTree()
						.getDisplay(), new Runnable() {
					public void run() {
						IStructuredSelection sel = (IStructuredSelection) explorer
								.getTreeViewer().getSelection();
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
		newComponentAction.setImageDescriptor(new EventBImageDescriptor(
				EventBImage.IMG_NEW_COMPONENT));

		deleteAction = new Action() {
			public void run() {
				ISelection selection = explorer.getTreeViewer().getSelection();
				if (!(selection.isEmpty())) {
					IStructuredSelection ssel = (IStructuredSelection) selection;
					Object[] slist = ssel.toArray();

					for (int i = 0; i < slist.length; i++) {
						UIUtils.debugProjectExplorer(slist[i].toString()
								+ " : " + slist[i].getClass().toString());
						if (slist[i] instanceof IRodinProject) {
							IRodinProject rodinProject = (IRodinProject) slist[i];
							// Confirmation dialog
							boolean answer = MessageDialog.openQuestion(
									explorer.getSite().getShell(),
									"Confirm Project Delete",
									"Are you sure you want to delete project '"
											+ rodinProject.getElementName()
											+ "' ?");
							if (answer) {
								IProject project = rodinProject.getProject();

								try {
									IRodinElement[] files = rodinProject
											.getChildren();
									for (int j = 0; j < files.length; j++) {
										if (files[j] instanceof IRodinFile)
											closeOpenedEditor((IRodinFile) files[j]);
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

						else if (slist[i] instanceof IRodinFile) {
							boolean answer = MessageDialog.openQuestion(
									explorer.getSite().getShell(),
									"Confirm File Delete",
									"Are you sure you want to delete file '"
											+ ((IRodinFile) slist[i])
													.getElementName() + "' ?");
							if (answer) {
								try {
									closeOpenedEditor((IRodinFile) slist[i]);
									((IRodinFile) slist[i]).delete(true,
											new NullProgressMonitor());
								} catch (PartInitException e) {
									e.printStackTrace();
								} catch (RodinDBException e) {
									e.printStackTrace();
								}
							}
						}
					}
					// viewer.refresh();
				}
			}
		};
		deleteAction.setText("Delete");
		deleteAction.setToolTipText("Delete selected elements");
		deleteAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_TOOL_DELETE));

		proveAction = new Action() {
			public void run() {
				ISelection selection = explorer.getTreeViewer().getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection ssel = (IStructuredSelection) selection;
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						if (!(obj instanceof IRodinFile))
							return;
						IRodinFile component = (IRodinFile) obj;
						IRodinProject prj = component.getRodinProject();
						String bareName = EventBPlugin
								.getComponentName(component.getElementName());
						IRodinFile prFile = prj.getRodinFile(EventBPlugin
								.getPRFileName(bareName));
						UIUtils.linkToProverUI(prFile);
						try {
							EventBUIPlugin
									.getActiveWorkbenchWindow()
									.getWorkbench()
									.showPerspective(
											ProvingPerspective.PERSPECTIVE_ID,
											EventBUIPlugin
													.getActiveWorkbenchWindow());
						} catch (WorkbenchException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		proveAction.setText("Prove");
		proveAction.setToolTipText("Start the prover");
		proveAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

		refineAction = new Action() {
			public void run() {
				ISelection selection = explorer.getTreeViewer().getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection ssel = (IStructuredSelection) selection;
					if (ssel.size() == 1) {
						Object obj = ssel.getFirstElement();
						if (!(obj instanceof IMachineFile))
							return;
						final IMachineFile machine = (IMachineFile) obj;
						final IRodinProject prj = machine.getRodinProject();

						InputDialog dialog = new InputDialog(explorer.getSite()
								.getShell(), "New REFINES Clause",
								"Please enter the name of the new machine",
								"m0", new MachineInputValidator(prj));

						dialog.open();

						final String abstractMachineName = EventBPlugin
								.getComponentName(machine.getElementName());
						final String bareName = dialog.getValue();

						try {
							RodinCore.run(new IWorkspaceRunnable() {

								public void run(IProgressMonitor monitor)
										throws CoreException {
									IRodinFile newFile = prj
											.createRodinFile(
													EventBPlugin
															.getMachineFileName(bareName),
													false, null);
									
									IInternalElement refined = newFile
											.createInternalElement(
													IRefinesMachine.ELEMENT_TYPE,
													abstractMachineName, null,
													null);
									refined.setContents(abstractMachineName);

									copyChildrenOfType(newFile, machine,
											ISeesContext.ELEMENT_TYPE);
									copyChildrenOfType(newFile, machine,
											IVariable.ELEMENT_TYPE);
									copyChildrenOfType(newFile, machine,
											IEvent.ELEMENT_TYPE);
									
									IRodinElement[] elements = machine.getChildrenOfType(IEvent.ELEMENT_TYPE);

									for (IRodinElement element : elements) {
										String name = element.getElementName();
										IInternalElement newElement = newFile.getInternalElement(IEvent.ELEMENT_TYPE, name);
										IInternalElement refineEvent = newElement.createInternalElement(IRefinesEvent.ELEMENT_TYPE, name, null, null);
										refineEvent.setContents(name);
									}
									newFile.save(null, true);
									UIUtils.linkToEventBEditor(newFile);

								}

							}, null);
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
			}
		};
		refineAction.setText("Refines");
		refineAction.setToolTipText("Refines the machine");
		refineAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

	}

	private void copyChildrenOfType(IRodinFile destination,
			IRodinFile original, String type) throws RodinDBException {
		IRodinElement[] elements = original.getChildrenOfType(type);

		for (IRodinElement element : elements) {
			((IInternalElement) element).copy(destination, null, null, false, null);
		}
	}

	private class MachineInputValidator implements IInputValidator {

		IRodinProject prj;

		MachineInputValidator(IRodinProject prj) {
			this.prj = prj;
		}

		public String isValid(String newText) {
			if (newText.equals(""))
				return "Name must not be empty.";
			IRodinFile file = prj.getRodinFile(EventBPlugin
					.getMachineFileName(newText));
			if (file.exists())
				return "Machine " + newText + " already exists.";
			return null;
		}

	}

	/**
	 * Close the open editor for a particular Rodin File
	 * 
	 * @param file
	 *            A Rodin File
	 * @throws PartInitException
	 *             Exception when closing the editor
	 */
	private void closeOpenedEditor(IRodinFile file) throws PartInitException {
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
	 * Dynamically fill the context menu (depends on the selection).
	 * <p>
	 * 
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {
		ISelection sel = getContext().getSelection();
		Object input = getContext().getInput();
		if (sel instanceof IStructuredSelection) {
			MenuManager newMenu = new MenuManager("&New");

			IStructuredSelection ssel = (IStructuredSelection) sel;

			// Can only create new Project if at the Workspace level
			if (input == null) {
				newMenu.add(newProjectAction);
				if (ssel.size() == 1) {
					newMenu.add(newComponentAction);
				}
			} else {
				newMenu.add(newComponentAction);
			}
			newMenu.add(new Separator());

			if ((ssel.size() == 1)
					&& (ssel.getFirstElement() instanceof IMachineFile))
				menu.add(refineAction);

			menu.add(newMenu);
			menu.add(deleteAction);
			menu.add(refreshAction);
			if ((ssel.size() == 1)
					&& (ssel.getFirstElement() instanceof IRodinFile))
				menu.add(proveAction);
			menu.add(new Separator());
			drillDownAdapter.addNavigationActions(menu);

			// Other plug-ins can contribute there actions here
			menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
			super.fillContextMenu(menu);
		}
	}

}
