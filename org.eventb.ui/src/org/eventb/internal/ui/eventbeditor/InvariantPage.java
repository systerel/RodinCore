/*******************************************************************************
 * Copyright (c) 2005 ETH-Zurich
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH RODIN Group
 *******************************************************************************/

package org.eventb.internal.ui.eventbeditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Form Page
 * for editing Invariants (Rodin elements).
 */
public class InvariantPage 
	extends NewEventBFormPage 
{
	
	// Title, tab title and ID of the page.
	public static final String PAGE_ID = "Invariants"; //$NON-NLS-1$
	public static final String PAGE_TITLE = "Invariants";
	public static final String PAGE_TAB_TITLE = "Invariants";
	

	/**
	 * Contructor.
	 * <p>
	 * @param editor The form editor that holds the page 
	 */
	public InvariantPage(FormEditor editor) {
		super(editor, PAGE_ID, PAGE_TITLE, PAGE_TAB_TITLE);  //$NON-NLS-1$
	}

	/* (non-Javadoc)
	 * @see org.eventb.internal.ui.eventbeditor.NewEventBFormPage#createMasterSection(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite, int, org.eventb.internal.ui.eventbeditor.EventBEditor)
	 */
	@Override
	protected NewEventBTablePartWithButtons createMasterSection(IManagedForm managedForm, Composite parent, int style, EventBEditor editor) {
		InvariantMasterSection part = new InvariantMasterSection(managedForm, parent, managedForm.getToolkit(), Section.NO_TITLE, (EventBEditor) this.getEditor());
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 150;
		gd.widthHint = 150;
		part.getSection().setLayoutData(gd);
		return part;
	}
		
}	
			
