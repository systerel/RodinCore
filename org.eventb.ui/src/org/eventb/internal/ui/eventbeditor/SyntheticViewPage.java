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
 *         An implementation of the Event-B Form Page for showing the synthetic
 *         view of the editting construct.
 * @deprecated use {@link EditPage} instead.
 */
@Deprecated
public class SyntheticViewPage extends EventBFormPage {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".syntheticviewpage"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_synthethicViewPage_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_synthethicViewPage_tabTitle;

	/**
	 * Constructor.
	 */
	public SyntheticViewPage() {
		super(PAGE_ID, PAGE_TITLE, PAGE_TAB_TITLE); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.eventbeditor.EventBFormPage#createMasterSection(org.eclipse.ui.forms.IManagedForm,
	 *      org.eclipse.swt.widgets.Composite, int,
	 *      org.eventb.internal.ui.eventbeditor.EventBEditor)
	 */
	@Override
	protected EventBPartWithButtons createMasterSection(
			IManagedForm managedForm, Composite parent, int style,
			IEventBEditor<?> editor) {
		EventBPartWithButtons part;
		if (editor instanceof EventBMachineEditor)
			part = new SyntheticMachineViewSection(managedForm, parent,
					managedForm.getToolkit(), Section.NO_TITLE,
					(EventBMachineEditor) editor);
		else
			part = new SyntheticContextViewSection(managedForm, parent,
					managedForm.getToolkit(), Section.NO_TITLE,
					(EventBContextEditor) editor);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 150;
		gd.widthHint = 150;
		part.getSection().setLayoutData(gd);
		return part;
	}

}
