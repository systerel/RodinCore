/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.internal.ui.eventbeditor.editpage.EditPage;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.eventbeditor.IEventBEditor;

/**
 * @author htson
 *         <p>
 *         An implementation of the Event-B Form Page for editing Axioms (Rodin
 *         elements)
 * @deprecated use {@link EditPage} instead.
 */
@Deprecated
public class AxiomPage extends EventBFormPage {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".axiompage"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_axiomPage_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_axiomPage_tabTitle;

	/**
	 * Constructor.
	 */
	public AxiomPage() {
		super(PAGE_ID, PAGE_TITLE, PAGE_TAB_TITLE); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBFormPage#createMasterSection(org.eclipse.ui.forms.IManagedForm,
	 *      org.eclipse.swt.widgets.Composite, int,
	 *      org.eventb.ui.eventbeditor.IEventBEditor)
	 */
	@Override
	protected EventBPartWithButtons createMasterSection(
			IManagedForm managedForm, Composite parent, int style,
			IEventBEditor<?> editor) {
		AxiomMasterSection part = new AxiomMasterSection(managedForm, parent,
				managedForm.getToolkit(), Section.NO_TITLE, getEventBEditor());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 150;
		gd.widthHint = 150;
		part.getSection().setLayoutData(gd);
		return part;
	}

}
