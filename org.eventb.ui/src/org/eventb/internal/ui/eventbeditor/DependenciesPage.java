/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.IContextFile;
import org.eventb.core.IMachineFile;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IRodinFile;

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

		IEventBEditor eventBEditor = (IEventBEditor) this.getEditor();
		IRodinFile rodinFile = eventBEditor
				.getRodinInput();

		if (rodinFile instanceof IMachineFile) {
			SectionPart part = new RefinesSection(eventBEditor, this
					.getManagedForm().getToolkit(), body);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.minimumWidth = 150;
			part.getSection().setLayoutData(gd);
			managedForm.addPart(part);

			part = new SeesSection(eventBEditor, this.getManagedForm()
					.getToolkit(), body);
			gd = new GridData(GridData.FILL_BOTH);
			gd.minimumWidth = 250;

			part.getSection().setLayoutData(gd);
			managedForm.addPart(part);

		} else if (rodinFile instanceof IContextFile) {
			SectionPart part = new ExtendsSection(eventBEditor, this
					.getManagedForm().getToolkit(), body);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.minimumWidth = 250;

			part.getSection().setLayoutData(gd);
			managedForm.addPart(part);
		}
	}

}
