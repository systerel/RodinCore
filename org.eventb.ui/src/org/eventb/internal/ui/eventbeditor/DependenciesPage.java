/*******************************************************************************
 * Copyright (c) 2005, 2008 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - separation of file and root element
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.IContextRoot;
import org.eventb.core.IMachineRoot;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IInternalElement;

/**
 * @author htson
 *         <p>
 *         An implementation of the Event-B Form Page for editing the
 *         dependencies (e.g. Sees, refines clause).
 */
public class DependenciesPage extends EventBEditorPage {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".dependencypage"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_dependencyPage_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_dependencyPage_tabTitle;

	/**
	 * Constructor.
	 */
	public DependenciesPage() {
		super(PAGE_ID, PAGE_TAB_TITLE, PAGE_TITLE); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();

		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);

		final IEventBEditor<?> eventBEditor = getEventBEditor();
		final IInternalElement rodinRoot = eventBEditor.getRodinInput();
		final FormToolkit toolkit = getManagedForm().getToolkit();
		if (rodinRoot instanceof IMachineRoot) {
			SectionPart part = new RefinesSection(
					(IEventBEditor<IMachineRoot>) eventBEditor, toolkit, body);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.minimumWidth = 150;
			part.getSection().setLayoutData(gd);
			managedForm.addPart(part);

			part = new SeesSection((IEventBEditor<IMachineRoot>) eventBEditor, toolkit, body);
			gd = new GridData(GridData.FILL_BOTH);
			gd.minimumWidth = 250;

			part.getSection().setLayoutData(gd);
			managedForm.addPart(part);

		} else if (rodinRoot instanceof IContextRoot) {
			SectionPart part = new ExtendsSection((IEventBEditor<IContextRoot>) eventBEditor, toolkit, body);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.minimumWidth = 250;

			part.getSection().setLayoutData(gd);
			managedForm.addPart(part);
		}
	}

}
