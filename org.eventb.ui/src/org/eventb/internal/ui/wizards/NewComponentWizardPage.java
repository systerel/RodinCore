/*******************************************************************************
 * Copyright (c) 2005, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *     Systerel - improved detection of project name
 *******************************************************************************/
package org.eventb.internal.ui.wizards;

import static org.rodinp.core.RodinCore.asRodinElement;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eventb.core.IEventBProject;
import org.eventb.internal.ui.RodinProjectSelectionDialog;
import org.eventb.internal.ui.UIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

/**
 * @author htson
 *         <p>
 *         The "New" wizard page allows setting the project for the new
 *         component as well as the component name. The page will only accept
 *         component name without the extension.
 */
public class NewComponentWizardPage extends WizardPage {

	// Some text areas.
	private Text projectText;
	EventBProjectValidator projectValidator;

	private Text componentText;

	// Some buttons.
	private Button machineButton;

	private Button contextButton;

	// The selection when the wizard is launched.
	private ISelection selection;

	/**
	 * Constructor for NewComponentWizardPage.
	 * <p>
	 * 
	 * @param selection
	 *            The selection when the wizard is launched
	 */
	public NewComponentWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("New Event-B Component");
		setDescription("This wizard creates a new Event-B component (machine, context, etc.) that can be opened by a multi-page editor.");
		this.selection = selection;
	}

	/**
	 * Creating the components of the dialog.
	 * <p>
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(composite, SWT.NULL);
		label.setText("&Project:");

		projectText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		projectValidator = new EventBProjectValidator();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		projectText.setLayoutData(gd);
		final TextModifyListener listener = new TextModifyListener();
		projectText.addModifyListener(listener);

		Button button = new Button(composite, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		label = new Label(composite, SWT.NULL);
		label.setText("&Component name:");

		componentText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		componentText.setLayoutData(gd);
		componentText.addModifyListener(listener);

		createComposite(composite, 1); // ignore the next cell

		// composite_tab << parent
		Composite composite_tab = createComposite(composite, 1);
		createLabel(composite_tab,
				"Please choose the type of the new component"); //$NON-NLS-1$
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = 3;
		composite_tab.setLayoutData(data);

		// radio button composite << tab composite
		Composite composite_radioButton = createComposite(composite_tab, 1);
		machineButton = createRadioButton(composite_radioButton, "Machine"); //$NON-NLS-1$
		contextButton = createRadioButton(composite_radioButton, "Context"); //$NON-NLS-1$

		initialize();
		setControl(composite);
	}

	/**
	 * If we want to focus on a control different from the first one, we need to
	 * post the focus event for future processing because the wizard dialog
	 * always focuses on the first control, whatever was done by the page
	 * itself.
	 */
    @Override
	public void setVisible(boolean visible) {
    	super.setVisible(visible);
		if (!visible) {
			return;
		}
		if (projectValidator.hasError()) {
			// Focus on project field
			setFocusAndSelectAll(projectText);
		} else {
			// Project is valid, focus on component control
			setFocusAndSelectAll(componentText);
		}
	}
    
    private void setFocusAndSelectAll(Text text) {
		text.selectAll();
		text.setFocus();
    }

	/**
	 * Creates composite control and sets the default layout data.
	 * <p>
	 * 
	 * @param parent
	 *            the parent of the new composite
	 * @param numColumns
	 *            the number of columns for the new composite
	 * @return the newly-created coposite
	 */
	private Composite createComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);

		// GridLayout
		GridLayout layout = new GridLayout();
		layout.numColumns = numColumns;
		composite.setLayout(layout);

		return composite;
	}

	/**
	 * Utility method that creates a label instance and sets the default layout
	 * data.
	 * <p>
	 * 
	 * @param parent
	 *            the parent for the new label
	 * @param text
	 *            the text for the new label
	 * @return the new label
	 */
	private Label createLabel(Composite parent, String text) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText(text);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		data.horizontalAlignment = GridData.FILL;
		label.setLayoutData(data);
		return label;
	}

	/**
	 * Utility method that creates a radio button instance and sets the default
	 * layout data.
	 * <p>
	 * 
	 * @param parent
	 *            the parent for the new button
	 * @param label
	 *            the label for the new button
	 * @return the newly-created button
	 */
	private Button createRadioButton(Composite parent, String label) {
		Button button = new Button(parent, SWT.RADIO | SWT.LEFT);
		button.setText(label);
		GridData data = new GridData();
		button.setLayoutData(data);
		return button;
	}

	/**
	 * Tests if the current workbench selection is a suitable project to use.
	 */
	private void initialize() {
		componentText.setText("changeMe");
		machineButton.setSelection(true);
		contextButton.setSelection(false);

		final IRodinProject project;
		project = getProjectFromSelection();

		if (UIUtils.DEBUG)
			System.out.println("Project " + project);
		
		if (project != null) {
			projectText.setText(project.getElementName());
			componentText.selectAll();
		}
	}

	private IRodinProject getProjectFromSelection() {
		if (!(selection instanceof IStructuredSelection))
			return null;
		final Iterator<?> iter = ((IStructuredSelection) selection).iterator();
		while (iter.hasNext()) {
			final Object obj = iter.next();
			final IRodinElement element = asRodinElement(obj);
			if (element != null) {
				return element.getRodinProject();
			}
		}
		return null;
	}

	/**
	 * Uses the RODIN project selection dialog to choose the new value for
	 * the project field.
	 */
	void handleBrowse() {
		final String projectName = getProjectName();
		IRodinProject rodinProject;
		if (projectName.equals(""))
			rodinProject = null;
		else
			rodinProject = EventBUIPlugin.getRodinDatabase().getRodinProject(
					projectName);
		
		RodinProjectSelectionDialog dialog = new RodinProjectSelectionDialog(
				getShell(), rodinProject, false, "Project Selection",
				"Select a RODIN project");
		if (dialog.open() == RodinProjectSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				projectText.setText(((IRodinProject) result[0]).getElementName());
			}
		}
	}

	class TextModifyListener implements ModifyListener {
		
		/**
		 * Ensures that both text fields are set correctly.
		 */
		@Override
		public void modifyText(ModifyEvent e) {
			projectValidator.validate(getProjectName());
			if (projectValidator.hasError()) {
				updateStatus(projectValidator.getErrorMessage());
				return;
			}
			
			final IEventBProject evbProject = projectValidator.getEventBProject();
			final String componentName = getComponentName();
			if (componentName.length() == 0) {
				updateStatus("Component name must be specified");
				return;
			}
			
			final IRodinFile machineFile = evbProject.getMachineFile(componentName);
			final IRodinFile contextFile = evbProject.getContextFile(componentName);
			if (machineFile == null || contextFile == null) {
				updateStatus("Component name must be valid");
				return;
			}
			if (machineFile.exists()) {
				updateStatus("There is already a machine with this name");
				return;
			}
			if (contextFile.exists()) {
				updateStatus("There is already a context with this name");
				return;
			}
			updateStatus(null);
		}
	}

	/**
	 * Update the status of this dialog.
	 * <p>
	 * 
	 * @param message
	 *            A string message
	 */
	void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	/**
	 * Get the name of the project.
	 * <p>
	 * 
	 * @return The name of the project
	 */
	public String getProjectName() {
		return projectText.getText();
	}

	/**
	 * Get the name of the new component.
	 * <p>
	 * 
	 * @return The name of the new component (without extension)
	 */
	public String getComponentName() {
		return componentText.getText();
	}

	/**
	 * Get the type of the new component ("bum" / "buc").
	 * <p>
	 * 
	 * @return The extension for the new component
	 */
	public String getType() {
		if (machineButton.getSelection())
			return "bum";
		if (contextButton.getSelection())
			return "buc";
		return null;
	}

}