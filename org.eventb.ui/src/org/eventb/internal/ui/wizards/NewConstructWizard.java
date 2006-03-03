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

package org.eventb.internal.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eventb.core.IEvent;
import org.eventb.core.IMachine;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.eventbeditor.EventBEditor;
import org.rodinp.core.IRodinDB;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author htson
 * <p>
 * This is the new wizard for creating new Event-B construct (machine, context, etc.)
 * resource in the provided project. If the project resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * project. The wizard creates one file with the extension
 * "bum" / "buc" (for machine/context respectively).
 * An instance of the Event-B Editor will be opened for editting
 * the new construct.
 */
public class NewConstructWizard 
	extends Wizard 
	implements INewWizard 
{
	public static final String WIZARD_ID = EventBUIPlugin.PLUGIN_ID + ".wizards.NewConstructWizard";
	
	// The wizard page.
	private NewConstructWizardPage page;
	
	// The selection when the wizard is launched.
	private ISelection selection;


	/**
	 * Constructor for NewConstructWizard.
	 */
	public NewConstructWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		page = new NewConstructWizardPage(selection);
		addPage(page);
	}


	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		final String projectName = page.getContainerName();
		final String fileName = page.getConstructName() + "." + page.getType();
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(projectName, fileName, monitor);
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
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}
	

	/**
	 * The worker method. It will find the project, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */
	private void doFinish(
		String projectName,
		String fileName,
		IProgressMonitor monitor)
		throws CoreException {
		
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(projectName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Project \"" + projectName + "\" does not exist.");
		}

		IRodinDB db = EventBUIPlugin.getRodinDatabase();
		// Creating a project handle
		IRodinProject rodinProject = db.getRodinProject(projectName); 

		final IRodinFile rodinFile = rodinProject.createRodinFile(fileName, false, null);
		if (rodinFile instanceof IMachine) {
			rodinFile.createInternalElement(IEvent.ELEMENT_TYPE, "INITIALISATION", null, null);
		}
		
		monitor.worked(1);
		
		
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					String editorId = EventBEditor.EDITOR_ID;
					IEditorInput fileInput = new FileEditorInput(rodinFile.getResource());
					EventBUIPlugin.getActivePage().openEditor(fileInput, editorId);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	

	/**
	 * Throw a Core exception.
	 * <p>
	 * @param message The message for displaying
	 * @throws CoreException a Core exception with the status contains the input message
	 */
	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "org.eventb.internal.ui", IStatus.OK, message, null);
		throw new CoreException(status);
	}


	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * <p>
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

}