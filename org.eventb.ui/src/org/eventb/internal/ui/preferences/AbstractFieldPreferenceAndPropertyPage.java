/*******************************************************************************
 * Copyright (c) 2010 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.preferences;

import static org.eventb.internal.ui.utils.Messages.preferencepage_enableProjectSpecifixSettings;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.rodinp.core.RodinCore;

/**
 * Class used to create a page for both workbench preferences and project
 * relative properties page. It uses a scoped store on the concerned project
 * that save the properties.
 * 
 * This class is intended to be sub-classed.
 */
public abstract class AbstractFieldPreferenceAndPropertyPage extends
		FieldEditorPreferencePage implements IWorkbenchPropertyPage {

	// Stores all created field editors
	protected List<FieldEditor> editors = new ArrayList<FieldEditor>();

	// The current preference store used
	protected IPreferenceStore preferenceStore;

	// Stores the project properties owner
	private IProject project;

	// The image descriptor of this pages title image
	private ImageDescriptor image;

	// Cache for page id
	private String prefPageId;

	// The button to enable/disable project specific settings
	private Button specificButton;

	// The project scope context
	private IScopeContext sc;

	// The preferences node attached to this page
	private IEclipsePreferences node;

	// Cache to save if the properties at opening
	private boolean wasEnabled;

	/**
	 * Constructor
	 * 
	 * @param style
	 *            the layout style
	 * @param prefPageID
	 *            the ID of this page used to store the preferences
	 */
	public AbstractFieldPreferenceAndPropertyPage(int style, String prefPageID) {
		super(style);
		this.prefPageId = prefPageID;
	}

	/**
	 * Constructor
	 * 
	 * @param title
	 *            the title string
	 * @param style
	 *            the layout style
	 * @param prefPageID
	 *            the ID of this page used to store the preferences
	 */
	public AbstractFieldPreferenceAndPropertyPage(String title, int style, String prefPageID) {
		super(title, style);
		this.prefPageId = prefPageID;
	}

	/**
	 * Constructor
	 * 
	 * @param title
	 *            the title string
	 * @param image
	 *            the title image
	 * @param style
	 *            the layout style
	 * @param prefPageID
	 *            the ID of this page used to store the preferences
	 */
	public AbstractFieldPreferenceAndPropertyPage(String title, ImageDescriptor image,
			int style, String prefPageID) {
		super(title, image, style);
		this.image = image;
		this.prefPageId = prefPageID;
	}
	
	/**
	 * Method that users should implement, in order to load the values of the
	 * project scope preference store with the workspace global values. The
	 * user is responsible of the preferences types he wants to use. The user
	 * should implement the way that workspace preference values can overwrite
	 * the project specific values in case of a defaults restoration when
	 * <code>reset</code> is set to <code>true</code>.
	 */
	protected abstract void initializeEditors(boolean reset);

	/**
	 * Method that users must implement, in order to define the editors they
	 * intend to use.
	 */
	@Override
	protected abstract void createFieldEditors();
	
	/**
	 * Return the title for a properties page associated with a project.
	 * 
	 * @param prj
	 *            the project to be considered
	 * @return the title of the property page
	 */
	protected String getPropertyPageTitle(IProject prj) {
		return Messages.preferencepage_prefix_propertyPageTitle(prj.getName());
	}

	/**
	 * We override the addField method. This allows us to store each field
	 * editor added by subclasses in a list for later processing.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#addField(org.eclipse.jface.preference.FieldEditor)
	 */
	@Override
	protected void addField(FieldEditor editor) {
		editors.add(editor);
		super.addField(editor);
	}

	/**
	 * We override the createContents method. In case of property pages we
	 * insert two radio buttons at the top of the page.
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		createButtonOrLink(parent);
		return super.createContents(parent);
	}

	private void createButtonOrLink(Composite parent) {
		final Composite comp = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(SWT.END, SWT.CENTER, false, false));
		if (!isPropertyPage()) {
			createLink(comp,
					Messages.preferencepage_prefixSettings_configureLink);
		} else {
			addEnableSpecificButton(comp);
		}
	}

	/**
	 * We override this method to provide specific behavior on property pages.
	 * 
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#initialize()
	 */
	@Override
	protected void initialize() {
		if (isPropertyPage()) {
			initializeButtonStatus();
			initializeEditors(false);
			initializeEditorsState();
		}
		super.initialize();
	}

	private void initializeEditorsState() {
		if (isPropertyPage()) {
			updateEditors(wasEnabled);
		}
	}

	private void initializeButtonStatus() {
		wasEnabled = hasProjectSettings();
		specificButton.setSelection(wasEnabled);
	}

	private IProject getSelectedProject() {
		final Set<IProject> projects = new HashSet<IProject>();
		final IProject[] projArray = RodinCore.getRodinDB().getWorkspaceRoot()
				.getProjects();
		for (IProject prj : projArray) {
			projects.add(prj);
		}
		final ProjectSelectionDialog sel = new ProjectSelectionDialog(getShell(),
				RodinCore.getRodinDB().getWorkspaceRoot());
		sel.open();
		final Object[] projectSelection = sel.getResult();
		if (projectSelection != null && projectSelection.length > 0
				&& projectSelection[0] instanceof IProject) {
			return (IProject) projectSelection[0];
		}
		return null;
	}

	/**
	 * Creates a new preferences page and opens it
	 */
	private void configureProjectSettings() {
		try {
			final IProject handled = getSelectedProject();
			if (handled == null) {
				return;
			}
			// create a new instance of the current class
			final AbstractFieldPreferenceAndPropertyPage page = this.getClass()
					.newInstance();
			page.project = handled;
			page.setTitle(getTitle());
			page.setImageDescriptor(image);
			// and show it
			page.setDescription(this.getDescription());
			showPropertiesPage(prefPageId, page, page.project);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Receives the object that owns the properties shown in this property page.
	 */
	@Override
	public void setElement(IAdaptable element) {
		if (element instanceof IProject)
			this.project = ((IProject) element);
	}

	/**
	 * Delivers the object that owns the properties shown in this property page.
	 * 
	 * @see org.eclipse.ui.IWorkbenchPropertyPage#getElement()
	 */
	@Override
	public IAdaptable getElement() {
		return project;
	}

	/**
	 * Returns true if this instance represents a property page (i.e. a project
	 * is set as owner)
	 * 
	 * @return true for property pages, false for preference pages
	 */
	public boolean isPropertyPage() {
		return project != null;
	}

	/**
	 * We override the createControl method. In case of property pages we create
	 * a new <code>ScopedPreferenceStore</code>.
	 */
	@Override
	public void createControl(Composite parent) {
		if (isPropertyPage()) {
			sc = new ProjectScope(project);
			node = sc.getNode(prefPageId);
			preferenceStore = new ScopedPreferenceStore(sc, prefPageId);
		} else {
			preferenceStore = EventBUIPlugin.getDefault().getPreferenceStore();
		}
		super.setPreferenceStore(preferenceStore);
		super.createControl(parent);
	}

	protected boolean hasProjectSettings() {
		if (preferenceStore == null)
			return false;
		if (node == null)
			return false;
		try {
			return node.keys().length != 0;
		} catch (BackingStoreException e) {
			return false;
		}
	}

	private Link createLink(Composite composite, String text) {
		final Link link = new Link(composite, SWT.NONE);
		link.setFont(composite.getFont());
		link.setText("<A>" + text + "</A>");
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doLinkActivated((Link) e.widget);
			}
		});
		return link;
	}

	protected void doLinkActivated(Link link) {
		configureProjectSettings();
	}

	protected Button getSpecificButton() {
		return specificButton;
	}

	private void addEnableSpecificButton(Composite parent) {
		specificButton = new Button(parent, SWT.CHECK | SWT.NONE);
		specificButton.setText(preferencepage_enableProjectSpecifixSettings);
		specificButton.setVisible(true);
		specificButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final boolean isSpecific = getSpecificButton().getSelection();
				updateEditors(isSpecific);
			}
		});
	}

	protected void updateEditors(boolean active) {
		for (FieldEditor ed : editors) {
			ed.load();
			ed.setEnabled(active, getFieldEditorParent());
		}
	}

	protected void restoreDefaults(){
		initializeEditors(true);
		for (FieldEditor ed : editors) {
			ed.load();
			ed.setEnabled(false, getFieldEditorParent());
		}
	}
	
	
	@Override
	public IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	@Override
	public void performDefaults() {
		if (isPropertyPage()) {
			specificButton.setSelection(false);
			restoreDefaults();
			return;
		}
		super.performDefaults();
	}

	@Override
	public void performApply() {
		flush();
	}

	@Override
	public boolean performOk() {
		if (isPropertyPage() && specificButton.getSelection())
			flush();
		if (isPropertyPage() && !specificButton.getSelection()) {
			clearProjectStore();
			return true;
		}
		return super.performOk();
	}

	@Override
	public boolean performCancel() {
		if (isPropertyPage() && !wasEnabled) {
			clearProjectStore();
		}
		return true;
	}

	private void flush() {
		try {
			if (node != null)
				node.flush();
		} catch (BackingStoreException e) {
			// no need to bother the user.
		}
	}

	private void clearProjectStore() {
		if (node != null)
			PreferenceUtils.clearAllProperties(node.name(), project);
		flush();
	}

	/**
	 * Show a single preference page for a project (i.e. property page)
	 * 
	 * @param id
	 *            the preference page identification
	 * @param page
	 *            the preference page
	 */
	public void showPropertiesPage(String id, IPreferencePage page,
			final IProject prj) {
		final IPreferenceNode targetNode = new PreferenceNode(id, page);
		final PreferenceManager manager = new PreferenceManager();
		manager.addToRoot(targetNode);
		final PreferenceDialog dialog = new PreferenceDialog(getControl()
				.getShell(), manager);
		BusyIndicator.showWhile(getControl().getDisplay(), new Runnable() {
			@Override
			public void run() {
				dialog.create();
				dialog.getShell().setText(getPropertyPageTitle(prj));
				dialog.setMessage(targetNode.getLabelText());
				dialog.open();
			}
		});
	}

}
