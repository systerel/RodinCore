/*******************************************************************************
 * Copyright (c) 2006, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.projectexplorer.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.YesToAllMessageDialog;
import org.eventb.internal.ui.projectexplorer.ProjectExplorerUtils;
import org.eventb.ui.EventBUIPlugin;
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

			// Putting the selection into a set which does not contains any pair
			// of parent and child
			Collection<IRodinElement> set = new ArrayList<IRodinElement>();

			IStructuredSelection ssel = (IStructuredSelection) selection;

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
								.openYesNoToAllQuestion(part.getSite()
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
				} else if (element instanceof IRodinFile) {
					answer = deleteRodinFile(answer, (IRodinFile) element);
					if(answer == YesToAllMessageDialog.NO_TO_ALL)
						break;
				} else if (element instanceof IContextRoot || element instanceof IMachineRoot) {
					answer = deleteRodinFile(answer, ((IEventBRoot) element).getRodinFile());
					if(answer == YesToAllMessageDialog.NO_TO_ALL)
						break;
				}
			}

		}
	}

	
	/**
	 * if answer is not YesToAllMessageDialog.YES_TO_ALL, open a dialogue to ask
	 * user to delete rodinFile. Delete the file as the answer and return answer
	 */
	private int deleteRodinFile(int delete, IRodinFile rf) {
		int answer = delete ;
		if (answer != YesToAllMessageDialog.YES_TO_ALL) {
			answer = YesToAllMessageDialog.openYesNoToAllQuestion(part
					.getSite().getShell(), "Confirm File Delete",
					"Are you sure you want to delete file '"
							+ rf.getElementName() + "' in project '"
							+ rf.getParent().getElementName() + "' ?");
		}
		if ((answer != YesToAllMessageDialog.NO_TO_ALL)
				&& (answer != YesToAllMessageDialog.NO)) {
			try {
				closeOpenedEditor(rf);
				(rf).delete(true, new NullProgressMonitor());
			} catch (PartInitException e) {
				e.printStackTrace();
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		}
		return answer;
	}
	
	
	public void selectionChanged(IAction action, ISelection sel) {
		this.selection = sel;
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
