/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - Initial API and implementation
 *     Systerel - Added working sets
 *     Systerel - redirected dialog opening
 ******************************************************************************/
package org.eventb.internal.ui.wizards;

import static org.eclipse.ui.wizards.newresource.BasicNewResourceWizard.selectAndReveal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * Wizard for creating new Event-B Projects and adding them to Working Sets.
 * 
 * @author htson
 */
public class NewProjectWizard extends Wizard implements INewWizard {

	/**
	 * The identifier of the new component wizard (value
	 * <code>"org.eventb.ui.wizards.NewProject"</code>).
	 */
	public static final String WIZARD_ID = EventBUIPlugin.PLUGIN_ID
			+ ".wizards.NewProject";

	// The wizard page.
	private NewProjectWizardPage page;

	// The workbench when the wizard is launched
	private IWorkbench workbench;
	
	// The current selection when the wizard is launched.
	private ISelection selection;

	/**
	 * Constructor: This wizard needs a progress monitor.
	 */
	public NewProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		page = new NewProjectWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard. We
	 * will create an operation and run it using wizard as execution context.
	 * <p>
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		final String projectName = page.getProjectName();
		final IWorkingSet[] workingSets = page.getWorkingSets();
		final IRunnableWithProgress op = new IRunnableWithProgress() {
			@Override
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(projectName, workingSets, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			UIUtils.showUnexpectedError(e.getTargetException(),
					"when creating project " + projectName);
			return false;
		}

		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject newProject = root.getProject(projectName);
		selectAndReveal(newProject, workbench.getActiveWorkbenchWindow());
		return true;
	}

	/**
	 * The worker method. This will create a new project (provided that it does
	 * not exist before).
	 * <p>
	 * 
	 * @param projectName
	 *            the name of the project
	 * @param monitor
	 *            a progress monitor
	 * @throws RodinDBException
	 *             a core exception throws when creating a new project
	 */
	void doFinish(String projectName, final IWorkingSet[] workingSets, IProgressMonitor monitor)
			throws CoreException {
		// create an empty Rodin project
		monitor.beginTask("Creating " + projectName, 1);
		IResource resource = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(projectName));

		if (resource != null) {
			throwCoreException("Project \"" + projectName
					+ "\" already exists.");
			return;
		}

		final IRodinProject rodinProject = EventBUIPlugin.getRodinDatabase()
		.getRodinProject(projectName);
		try {

			RodinCore.run(new IWorkspaceRunnable() {

				@Override
				public void run(IProgressMonitor pMonitor) throws CoreException {
					IProject project = rodinProject.getProject();
					if (!project.exists())
						project.create(null);
					project.open(null);
					IProjectDescription description = project.getDescription();
					description.setNatureIds(new String[] { RodinCore.NATURE_ID });
					project.setDescription(description, null);		
					addToWorkingSets(project, workingSets);
				}
				
			}, monitor);
			
		} finally {
			monitor.worked(1);
		}
	}

	/**
	 * Throw a Core exception.
	 * <p>
	 * 
	 * @param message
	 *            The message for displaying
	 * @throws CoreException
	 *             a Core exception with the status contains the input message
	 */
	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, EventBUIPlugin.PLUGIN_ID,
				IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if we can initialize
	 * from it.
	 * <p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench wb, IStructuredSelection sel) {
		this.workbench = wb;
		this.selection = sel;
	}
	
	/**
	 * Adds a given project (i.e. all its resources) to some given working set.
	 * 
	 * @param project
	 *            The project to add
	 * @param workingSets
	 *            The working sets the project should be added to
	 */
	void addToWorkingSets(IProject project, IWorkingSet[] workingSets) {
		for (IWorkingSet workingSet : workingSets) {
			IAdaptable[] oldElements = workingSet.getElements();
			final int oldLength = oldElements.length;
			IAdaptable[] newElements = new IAdaptable[oldLength + 1];
			System.arraycopy(oldElements, 0, newElements, 0, oldLength);
			newElements[oldLength] = project;
			workingSet.setElements(workingSet.adaptElements(newElements));
		}
	}

}
