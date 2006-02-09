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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Form Page
 * for editing Events (Rodin elements).
 */
public class EventsPage
	extends EventBFormPage
{
	
	// Title, tab title and ID of the page.
	public static final String PAGE_ID = "events"; //$NON-NLS-1$
	public static final String PAGE_TITLE = "Events";
	public static final String PAGE_TAB_TITLE = "Events";
		

	/**
	 * Contructor.
	 * <p>
	 * @param editor The form editor that holds the page 
	 */
	public EventsPage(FormEditor editor) {
		super(editor, PAGE_ID, PAGE_TITLE, PAGE_TAB_TITLE);  //$NON-NLS-1$
	}


	/**
	 * Create the Master detail block.
	 */
	protected EventBMasterDetailsBlock createMasterDetailsBlock() {
		return new EventMasterDetailsBlock(this);
	}
	

	/**
	 * Creating the Mirror sections within the page.
	 */
	protected void createMirrorSections(Composite body, IManagedForm managedForm) {
		IRodinFile rodinFile = ((EventBEditor) this.getEditor()).getRodinInput();
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		Composite composite = managedForm.getToolkit().createComposite(body);
		composite.setLayoutData(gd);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		// Invariant mirror section.
		InvariantMirrorSection invariantMirrorSection = new InvariantMirrorSection(this, composite, ExpandableComposite.TITLE_BAR |Section.EXPANDED, rodinFile);
		managedForm.addPart(invariantMirrorSection);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 150;
		gd.widthHint = 200;
		invariantMirrorSection.getSection().setLayoutData(gd);
		EventBFormPage page = (EventBFormPage) this.getEditor().findPage(InvariantsPage.PAGE_ID);
		page.addChangedListener(invariantMirrorSection);

		// Event mirror section.
		EventMirrorSection eventMirrorSection = new EventMirrorSection(this, composite, ExpandableComposite.TITLE_BAR |Section.EXPANDED, rodinFile);
		managedForm.addPart(eventMirrorSection);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 150;
		gd.widthHint = 200;
		eventMirrorSection.getSection().setLayoutData(gd);
		page = (EventBFormPage) this.getEditor().findPage(EventsPage.PAGE_ID);
		page.addChangedListener(eventMirrorSection);

	}

}