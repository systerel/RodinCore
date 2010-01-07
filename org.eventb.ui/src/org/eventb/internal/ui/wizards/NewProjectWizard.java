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
 *     Systerel - redirected dialog opening
 ******************************************************************************/

package org.eventb.internal.ui.wizards;

import static org.eventb.internal.ui.utils.Messages.title_error;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
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
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is the new wizard for creating new Event-B projects,
 * 
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

	// The selection when the wizard is launched (this is not used for this
	// wizard).
	private ISelection selection;

	/**
	 * Constructor: This wizard needs a progress monitor.
	 */
	public NewProjectWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
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
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException {
				try {
					doFinish(projectName, monitor);
				} catch (CoreException e) {
					e.printStackTrace();
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
			Throwable realException = e.getTargetException();
			UIUtils.showError(title_error, realException.getMessage());
			return false;
		}
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
	void doFinish(String projectName, IProgressMonitor monitor)
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

		try {
			final IRodinProject rodinProject = EventBUIPlugin.getRodinDatabase()
					.getRodinProject(projectName);

			RodinCore.run(new IWorkspaceRunnable() {

				public void run(IProgressMonitor pMonitor) throws CoreException {
					IProject project = rodinProject.getProject();
					if (!project.exists())
						project.create(null);
					project.open(null);
					IProjectDescription description = project.getDescription();
					description.setNatureIds(new String[] { RodinCore.NATURE_ID });
					project.setDescription(description, null);					
				}
				
			}, monitor);
			
			monitor.worked(1);
		} catch (RodinDBException e) {
			e.printStackTrace();
			throw e;
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
		IStatus status = new Status(IStatus.ERROR, "org.eventb.internal.ui",
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
	public void init(IWorkbench workbench, IStructuredSelection sel) {
		this.selection = sel;
	}
}