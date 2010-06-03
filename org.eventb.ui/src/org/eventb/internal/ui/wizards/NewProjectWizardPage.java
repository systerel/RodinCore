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
 ******************************************************************************/
package org.eventb.internal.ui.wizards;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;

/**
 * This is the unique page of the NewProjec wizard. It allows the user to enter
 * a name for the project and add it to working sets.
 * 
 * @author htson
 */
public class NewProjectWizardPage extends WizardPage {

	// A Text area for entering the project name
	private Text projectText;
	
	// A control for specifying the working sets to which this project shall be
	// added
	private WorkingSetControl workingSetControl;

	/**
	 * Constructor for NewProjectWizardPage.
	 * 
	 * @param selection
	 *            The selection when the wizard is launched
	 */
	public NewProjectWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("New Event-B Project");
		setDescription("This wizard creates a new (empty) Event-B Project in the current Workspace");
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Project name:");

		projectText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectText.setLayoutData(gd);
		projectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});
		workingSetControl = new WorkingSetControl(container, parent.getShell());
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Set the initial value for the text area.
	 */
	private void initialize() {
		projectText.setText("NewProject");
		projectText.selectAll();
		projectText.setFocus();
	}

	/**
	 * Ensures that input is valid.
	 */
	void dialogChanged() {
		String projectName = getProjectName();
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(projectName));

		if (projectName.length() == 0) {
			updateStatus("Project name must be specified");
			return;
		}
		if (container != null) {
			updateStatus("A project with this name already exists.");
			return;
		}
		updateStatus(null);
	}

	/**
	 * Update the status of this dialog.
	 * <p>
	 * 
	 * @param message
	 *            A string message
	 */
	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	/**
	 * Get the name of the new project.
	 * <p>
	 * 
	 * @return The name of the new project
	 */
	public String getProjectName() {
		return projectText.getText();
	}

	/**
	 * Get the working sets that the new project should belong to
	 * <p>
	 * 
	 * @return The working sets of the new project
	 */
	public IWorkingSet[] getWorkingSets() {
		return workingSetControl.getSelection();
	}

}
