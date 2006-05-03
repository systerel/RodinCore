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

package org.eventb.internal.ui.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eventb.core.EventBPlugin;
import org.eventb.core.IContext;
import org.eventb.core.IMachine;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.projectexplorer.ProjectExplorer;
import org.eventb.internal.ui.projectexplorer.TreeNode;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         The "New" wizard page allows setting the container for the new
 *         component as well as the component name. The page will only accept
 *         component name without the extension.
 */
public class NewComponentWizardPage extends WizardPage {

	// Some text areas.
	private Text containerText;

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
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText("&Container:");

		containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		containerText.setLayoutData(gd);
		containerText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		label = new Label(container, SWT.NULL);
		label.setText("&Component name:");

		componentText = new Text(container, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		componentText.setLayoutData(gd);
		componentText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		createComposite(container, 1); // ignore the next cell

		// composite_tab << parent
		Composite composite_tab = createComposite(container, 1);
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
		dialogChanged();
		setControl(container);
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
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() {
		componentText.setText("NewComponent");
		machineButton.setSelection(true);
		contextButton.setSelection(false);

		IRodinProject project;
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object element = ssel.getFirstElement();
			IRodinElement curr;
			if (element instanceof IRodinElement) {
				curr = (IRodinElement) element;
			} else if (element instanceof TreeNode) {
				curr = (IRodinElement) ((TreeNode) element).getParent();
			} else
				curr = null;
			while (!(curr instanceof IRodinProject || curr == null)) {
				curr = curr.getParent();
			}
			project = (IRodinProject) curr;
		} else {
			// Trying to find the current project from the Project Explorer
			UIUtils.debug("From Project Explorer: ");
			ProjectExplorer explorer = (ProjectExplorer) EventBUIPlugin
					.getActivePage().findView(ProjectExplorer.VIEW_ID);
			project = explorer.getCurrentProject();
		}

		UIUtils.debug("Project " + project);
		if (project != null) {
			try {
				IResource obj = project.getCorrespondingResource();

				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				containerText.setText(container.getFullPath().toString());
			} catch (RodinDBException e) {
				e.printStackTrace();
			}
		}
		componentText.setFocus();
		componentText.selectAll();
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for
	 * the container field.
	 */
	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				getShell(), ResourcesPlugin.getWorkspace().getRoot(), false,
				"Select new component container");
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				containerText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set correctly.
	 */
	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot()
				.findMember(new Path(getContainerName()));
		String componentName = getComponentName();

		if (getContainerName().length() == 0) {
			updateStatus("Project must be specified");
			return;
		}

		if (container == null
				|| (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus("Project must exist");
			return;
		}
		if (!container.isAccessible()) {
			updateStatus("Project must be writable");
			return;
		}
		if (componentName.length() == 0) {
			updateStatus("Component name must be specified");
			return;
		}
		if (componentName.replace('\\', '/').indexOf('/', 1) > 0) {
			updateStatus("Component name must be valid");
			return;
		}
		IRodinProject rodinProject = EventBUIPlugin.getRodinDatabase()
				.getRodinProject(getContainerName());
		try {
			IRodinElement[] elements = rodinProject.getChildren();
			for (IRodinElement elem : elements) {
				if (elem instanceof IMachine || elem instanceof IContext) {
					if (EventBPlugin.getComponentName(
							((IRodinFile) elem).getElementName()).equals(
							componentName)) {
						updateStatus("Component name already exists");
						return;
					}
				}
			}
		} catch (RodinDBException e) {
			e.printStackTrace();
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
	 * Get the name of the container (project).
	 * <p>
	 * 
	 * @return The name of the container project
	 */
	public String getContainerName() {
		return containerText.getText();
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