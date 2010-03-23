/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Systerel - adaptation from ListSelectionDialog for Rodin
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import static org.eventb.internal.ui.utils.Messages.dialogs_projectSelection_description;
import static org.eventb.internal.ui.utils.Messages.dialogs_projectSelection_title;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.rodinp.core.RodinCore;

/**
 * A standard dialog which solicits a project selection from the user. This
 * class is configured with a data model represented by
 * <code>ProjectContentProvider</code> and <code>ProjectLabelProvider</code>
 * objects. The <code>getResult</code> method returns the selected project.
 * <p>
 * This class may be instantiated; it is not intended to be sub-classed.
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 * ProjectSelectionDialog dlg = new ProjectSelectionDialog(getShell(), input,
 * 		&quot;Select the project:&quot;);
 * dlg.setInitialSelections(projectsFromRoot);
 * dlg.setTitle(&quot;Rename Project&quot;);
 * dlg.open();
 * </pre>
 * 
 * </p>
 * 
 * @noextend This class is not intended to be sub-classed by clients.
 */
public class ProjectSelectionDialog extends SelectionDialog {
	// the root element to populate the viewer with
	private final Object inputElement;

	// providers for populating this dialog
	private final ILabelProvider labelProvider;

	private final IStructuredContentProvider contentProvider;

	private ISelection currentSelection;

	// the visual selection widget group
	private TableViewer projectViewer;

	// sizing constants
	private static final int SIZING_SELECTION_WIDGET_HEIGHT = 250;

	private static final int SIZING_SELECTION_WIDGET_WIDTH = 300;

	/**
	 * Creates a project dialog.
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param input
	 *            the root element to populate this dialog with
	 */
	public ProjectSelectionDialog(Shell parentShell, Object input) {
		super(parentShell);
		setTitle(dialogs_projectSelection_title);
		inputElement = input;
		this.contentProvider = new ProjectContentProvider();
		this.labelProvider = new ProjectLabelProvider();
		setMessage(dialogs_projectSelection_description);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		// page group
		final Composite composite = (Composite) super.createDialogArea(parent);

		initializeDialogUnits(composite);

		createMessageArea(composite);

		projectViewer = new TableViewer(composite);
		final GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
		data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
		projectViewer.getTable().setLayoutData(data);
		projectViewer.setLabelProvider(labelProvider);
		projectViewer.setContentProvider(contentProvider);
		projectViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {

					public void selectionChanged(SelectionChangedEvent event) {
						final ISelection sel = event.getSelection();
						if (sel != null) {
							setSelection(sel);
							if (sel instanceof StructuredSelection) {
								final StructuredSelection ssel = (StructuredSelection) sel;
								final Button okButton = getOkButton();
								if (ssel.size() > 1) {
									okButton.setEnabled(false);
								} else {
									okButton.setEnabled(true);
								}
							}
						}
					}
				});

		initializeViewer();
		Dialog.applyDialogFont(composite);
		return composite;
	}

	protected ISelection getSelection() {
		return currentSelection;
	}

	protected void setSelection(ISelection selection) {
		currentSelection = selection;
	}

	/**
	 * Returns the viewer used to show the list.
	 * 
	 * @return the viewer, or <code>null</code> if not yet created
	 */
	protected TableViewer getViewer() {
		return projectViewer;
	}

	/**
	 * Initializes this dialog's viewer after it has been laid out.
	 */
	private void initializeViewer() {
		projectViewer.setInput(inputElement);
	}

	/**
	 * The <code>ProjectSelectionDialog</code> implementation of this
	 * <code>Dialog</code> method builds a list of the selected elements for
	 * later retrieval by the client and closes this dialog.
	 */
	@Override
	protected void okPressed() {
		if (currentSelection != null
				&& currentSelection instanceof StructuredSelection) {
			setResult(((StructuredSelection) currentSelection).toList());
		}
		super.okPressed();
	}

	@Override
	public void setMessage(String msg) {
		super.setMessage(msg);
	}

	/**
	 * Content provider for IProject elements from the workspace root.
	 */
	public static class ProjectContentProvider implements
			IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return RodinCore.getRodinDB().getWorkspaceRoot().getProjects();
		}

		public void dispose() {
			// nothing to do
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// nothing to do
		}
	}

	/**
	 * Label provider for IProject objects.
	 */
	public static class ProjectLabelProvider extends LabelProvider {
		private static final Image IMG_FOLDER = PlatformUI.getWorkbench()
				.getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);

		@Override
		public Image getImage(Object element) {
			if (element instanceof IProject) {
				return IMG_FOLDER;
			}
			return null;
		}

		@Override
		public String getText(Object element) {
			if (element instanceof IProject) {
				return ((IProject) element).getName();
			}
			return super.getText(element);
		}
	}

}
