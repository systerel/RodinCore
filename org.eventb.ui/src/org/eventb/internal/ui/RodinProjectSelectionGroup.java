/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Igor Fedorenko - Fix for Bug 136921 [IDE]
 *     ETH Zurich - adaptation to Event-B
 *     Systerel - remove unused code
 *******************************************************************************/
package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.DrillDownComposite;
import org.eventb.internal.ui.preferences.EventBPreferenceStore;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinProject;

/**
 * @author htson
 *         <p>
 */
public class RodinProjectSelectionGroup extends Composite {
	// The listener to notify of events
	private Listener listener;

	// Enable user to type in new project name
	private boolean allowNewProjectName = true;

	// Last selection made by user
	private IRodinProject selectedProject;

	// handle on parts
	private Text projectNameField;

	TreeViewer treeViewer;

	// the message to display at the top of this dialog
	private static final String DEFAULT_MSG_NEW_ALLOWED = "Select a project or enter project name";

	private static final String DEFAULT_MSG_SELECT_ONLY = "Select a project";

	// sizing constants
	private static final int SIZING_SELECTION_PANE_WIDTH = 320;

	private static final int SIZING_SELECTION_PANE_HEIGHT = 300;

	/**
	 * Creates a new instance of the widget.
	 * 
	 * @param parent
	 *            The parent widget of the group.
	 * @param listener
	 *            A listener to forward events to. Can be null if no listener is
	 *            required.
	 * @param allowNewProjectName
	 *            Enable the user to type in a new project name instead of
	 *            just selecting from the existing ones.
	 */
	public RodinProjectSelectionGroup(Composite parent, Listener listener,
			boolean allowNewProjectName) {
		this(parent, listener, allowNewProjectName, null);
	}

	/**
	 * Creates a new instance of the widget.
	 * 
	 * @param parent
	 *            The parent widget of the group.
	 * @param listener
	 *            A listener to forward events to. Can be null if no listener is
	 *            required.
	 * @param allowNewProjectName
	 *            Enable the user to type in a new project name instead of
	 *            just selecting from the existing ones.
	 * @param message
	 *            The text to present to the user.
	 */
	public RodinProjectSelectionGroup(Composite parent, Listener listener,
			boolean allowNewProjectName, String message) {
		this(parent, listener, allowNewProjectName, message,
				SIZING_SELECTION_PANE_HEIGHT,
				SIZING_SELECTION_PANE_WIDTH);
	}

	/**
	 * Creates a new instance of the widget.
	 * 
	 * @param parent
	 *            The parent widget of the group.
	 * @param listener
	 *            A listener to forward events to. Can be null if no listener is
	 *            required.
	 * @param allowNewProjectName
	 *            Enable the user to type in a new project name instead of
	 *            just selecting from the existing ones.
	 * @param message
	 *            The text to present to the user.
	 * @param heightHint
	 *            height hint for the drill down composite
	 * @param widthHint
	 *            width hint for the drill down composite
	 */
	public RodinProjectSelectionGroup(Composite parent, Listener listener,
			boolean allowNewProjectName, String message,
			int heightHint, int widthHint) {
		super(parent, SWT.NONE);
		this.listener = listener;
		this.allowNewProjectName = allowNewProjectName;
		if (message != null) {
			createContents(message, heightHint, widthHint);
		} else if (allowNewProjectName) {
			createContents(DEFAULT_MSG_NEW_ALLOWED, heightHint, widthHint);
		} else {
			createContents(DEFAULT_MSG_SELECT_ONLY, heightHint, widthHint);
		}
	}

	/**
	 * The project selection has changed in the tree view. Update the
	 * project name field value and notify all listeners.
	 * 
	 * @param project
	 *            The project that changed
	 */
	public void projectSelectionChanged(IRodinProject project) {
		selectedProject = project;

		if (allowNewProjectName) {
			if (project == null) {
				projectNameField.setText("");//$NON-NLS-1$
			} else {
				String text = project.getElementName();
				projectNameField.setText(text);
				projectNameField.setToolTipText(text);
			}
		}

		// fire an event so the parent can update its controls
		if (listener != null) {
			Event changeEvent = new Event();
			changeEvent.type = SWT.Selection;
			changeEvent.widget = this;
			listener.handleEvent(changeEvent);
		}
	}

	/**
	 * Creates the contents of the composite.
	 * 
	 * @param message
	 */
	public void createContents(String message) {
		createContents(message, SIZING_SELECTION_PANE_HEIGHT,
				SIZING_SELECTION_PANE_WIDTH);
	}

	/**
	 * Creates the contents of the composite.
	 * 
	 * @param message
	 * @param heightHint
	 * @param widthHint
	 */
	public void createContents(String message, int heightHint, int widthHint) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		setLayout(layout);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label label = new Label(this, SWT.WRAP);
		label.setText(message);
		label.setFont(this.getFont());

		if (allowNewProjectName) {
			projectNameField = new Text(this, SWT.SINGLE | SWT.BORDER);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.widthHint = widthHint;
			projectNameField.setLayoutData(gd);
			projectNameField.addListener(SWT.Modify, listener);
			projectNameField.setFont(this.getFont());
			// Mark as required field.
			projectNameField
					.setBackground(EventBPreferenceStore
							.getColorPreference(PreferenceConstants
									.P_REQUIRED_FIELD_BACKGROUND));
		} else {
			// filler...
			new Label(this, SWT.NONE);
		}

		createTreeViewer(heightHint);
		Dialog.applyDialogFont(this);
	}

	/**
	 * Returns a new drill down viewer for this dialog.
	 * 
	 * @param heightHint
	 *            height hint for the drill down composite
	 */
	protected void createTreeViewer(int heightHint) {
		// Create drill down.
		DrillDownComposite drillDown = new DrillDownComposite(this, SWT.BORDER);
		GridData spec = new GridData(SWT.FILL, SWT.FILL, true, true);
		spec.widthHint = SIZING_SELECTION_PANE_WIDTH;
		spec.heightHint = heightHint;
		drillDown.setLayoutData(spec);

		// Create tree viewer inside drill down.
		treeViewer = new TreeViewer(drillDown, SWT.NONE);
		drillDown.setChildTree(treeViewer);
		RodinProjectContentProvider contentProvider = new RodinProjectContentProvider();
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new LabelProvider() {

			@Override
			public Image getImage(Object element) {
				if (element instanceof IRodinProject) {
					return EventBImage.getRodinImage((IRodinProject) element);
				}
				return super.getImage(element);
			}

			@Override
			public String getText(Object element) {
				if (element instanceof IRodinProject) {
					return ((IRodinProject) element).getElementName();
				}
				if (element instanceof IResource) {
					return ((IResource) element).getName();
				}
				return super.getText(element);
			}

		});
		treeViewer.setSorter(new ViewerSorter());
		treeViewer.setUseHashlookup(true);
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				projectSelectionChanged((IRodinProject) selection
						.getFirstElement()); // allow null
			}
		});
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					Object item = ((IStructuredSelection) selection)
							.getFirstElement();
					if (item == null) {
						return;
					}
					if (treeViewer.getExpandedState(item)) {
						treeViewer.collapseToLevel(item, 1);
					} else {
						treeViewer.expandToLevel(item, 1);
					}
				}
			}
		});

		// This has to be done after the viewer has been laid out
		treeViewer.setInput(EventBUIPlugin.getRodinDatabase());
	}

	/**
	 * Returns the currently entered project name. Null if the field is empty.
	 * Note that the project may not exist yet if the user entered a new project
	 * name in the field.
	 * <p>
	 * 
	 * @return String
	 */
	public IRodinProject getProject() {
		if (allowNewProjectName) {
			String projectName = projectNameField.getText();
			if (projectName == null || projectName.length() < 1) {
				return null;
			}
			return EventBUIPlugin.getRodinDatabase().getRodinProject(
					projectName);
		}
		if (selectedProject == null)
			return null;
		return selectedProject;
	}

	/**
	 * Gives focus to one of the widgets in the group, as determined by the
	 * group.
	 */
	public void setInitialFocus() {
		if (allowNewProjectName) {
			projectNameField.setFocus();
		} else {
			treeViewer.getTree().setFocus();
		}
	}

	/**
	 * Sets the selected existing project.
	 * 
	 * @param project
	 */
	public void setSelectedProject(IRodinProject project) {
		selectedProject = project;

		// expand to and select the specified project
		List<IRodinElement> itemsToExpand = new ArrayList<IRodinElement>();
		IRodinElement parent = project.getParent();
		while (parent != null) {
			itemsToExpand.add(0, parent);
			parent = parent.getParent();
		}
		treeViewer.setExpandedElements(itemsToExpand.toArray());
		treeViewer.setSelection(new StructuredSelection(project), true);
	}

}
