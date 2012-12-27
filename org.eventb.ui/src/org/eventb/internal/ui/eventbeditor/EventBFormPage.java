/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used of interface ISelectionProvider
 *******************************************************************************/
package org.eventb.internal.ui.eventbeditor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.IRodinElement;

/**
 * @author htson
 *         <p>
 *         An abstract class which extends </class>FormPage<class> for editing
 *         Rodin elements.
 */
public abstract class EventBFormPage extends EventBEditorPage implements
		ISelectionProvider {

	// Title of the page.
//	private String pageTitle;

	private EventBPartWithButtons part;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param pageID
	 *            The ID of the page
	 * @param pageTitle
	 *            The title of the page
	 * @param pageTabTitle
	 *            The tab title of the page
	 */
	public EventBFormPage(String pageID, String pageTitle,
			String pageTabTitle) {
		super(pageID, pageTabTitle, pageTitle); //$NON-NLS-1$
//		this.pageTitle = pageTitle;
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

		part = createMasterSection(managedForm, body, Section.TITLE_BAR
				| Section.DESCRIPTION, getEventBEditor());
		managedForm.addPart(part);

		// createMirrorSections(body, managedForm);

	}

	/**
	 * Creating mirror sections in the page.
	 * 
	 * @param body
	 *            The composite to create the mirror sections
	 * @param managedForm
	 *            The form to create the mirror sections
	 */
	// protected abstract void createMirrorSections(Composite body, IManagedForm
	// managedForm);
	/**
	 * This method create a master section which should be an Event-B Part with
	 * Buttons. Sub-class should implement this method and return their own
	 * specific part.
	 * <p>
	 * 
	 * @param managedForm
	 *            The managed form used to create the part.
	 * @param parent
	 *            The composite parent of the part
	 * @param style
	 *            The style to create the part
	 * @param editor
	 *            The Event-B Editor.
	 * @return A new Event-B Part with Buttons
	 */
	protected abstract EventBPartWithButtons createMasterSection(
			IManagedForm managedForm, Composite parent, int style,
			IEventBEditor<?> editor);

	/**
	 * Setting the selection within the page.
	 * <p>
	 * 
	 * @param element
	 *            A Rodin Element
	 */
	public void edit(IRodinElement element) {
		part.edit(element);
	}

	/**
	 * Return the Master part
	 * 
	 * @return the Event-B Part with buttons which is the master part of the
	 *         page.
	 */
	public EventBPartWithButtons getPart() {
		return part;
	}

	public void selectElement(IRodinElement element) {
		part.setSelection(element);
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		getPart().getViewer().addSelectionChangedListener(listener);
	}

	@Override
	public ISelection getSelection() {
		return getPart().getViewer().getSelection();
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		getPart().getViewer().removeSelectionChangedListener(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		getPart().getViewer().setSelection(selection);
	}	
}
