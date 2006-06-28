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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author htson
 *         <p>
 *         An implementation of the Event-B Form Page for editing the
 *         dependencies (e.g. Sees, refines clause).
 */
public class DependenciesPage extends FormPage {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = "Dependencies"; //$NON-NLS-1$

	public static final String PAGE_TITLE = "Dependencies";

	public static final String PAGE_TAB_TITLE = "Dependencies";

	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            The form editor that holds the page
	 */
	public DependenciesPage(FormEditor editor) {
		super(editor, PAGE_ID, PAGE_TAB_TITLE); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		form.setText(PAGE_TITLE); //$NON-NLS-1$
		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 10;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);

		SectionPart part = new SeesSection(this.getEditor(), this
				.getManagedForm().getToolkit(), body);
		part.getSection().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		managedForm.addPart(part);
		
		part = new RefinesSection(this.getEditor(), this
				.getManagedForm().getToolkit(), body);
		part.getSection().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));
		managedForm.addPart(part);
	}

}
