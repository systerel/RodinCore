package org.eventb.internal.ui.projectexplorer.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class Delete implements IViewActionDelegate {

	private ISelection selection;
	private IWorkbenchPart part;
	
	public Delete() {
		super();
	}

	public void run(IAction action) {
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
							part.getSite().getShell(),
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
							part.getSite().getShell(),
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
	

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
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

	public void init(IViewPart view) {
		part = view;
	}
	
}
