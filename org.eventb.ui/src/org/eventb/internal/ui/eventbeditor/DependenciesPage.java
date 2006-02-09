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

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author htson
 * <p>
 * An implementation of the Event-B Form Page
 * for editing the dependencies (e.g. Sees, refines clause).
 */
public class DependenciesPage extends FormPage {
	
	// Title, tab title and ID of the page.
	public static final String PAGE_ID = "dependencies"; //$NON-NLS-1$
	public static final String PAGE_TITLE = "Dependencies";
	public static final String PAGE_TAB_TITLE = "Dependencies";
	
	// List of change listeners
	private List<ChangeListener> changeListeners;
	

	/**
	 * Contructor.
	 * @param editor The form editor that holds the page 
	 */
	public DependenciesPage(FormEditor editor) {
		super(editor, PAGE_ID, PAGE_TAB_TITLE);  //$NON-NLS-1$
		changeListeners = new ArrayList<ChangeListener>();
	}
	
	
	/**
	 * Register a change listener.
	 * <p>
	 * @param listener A change listener
	 */
	public void addChangedListener(ChangeListener listener) {
		changeListeners.add(listener);
	}

	
	/**
	 * Notify the listeners about the change
	 */
	public void notifyChangeListeners() {
		// TODO To be removed when the Editor implement the change listener.
		for (int i = 0; i < changeListeners.size(); i++) {
			((ChangeListener) changeListeners.get(i)).stateChanged(new ChangeEvent(this));
		}
	}
	
	
	/**
	 * Creating the content of the page
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		form.setText(PAGE_TITLE); //$NON-NLS-1$
		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);
		
		managedForm.addPart(new SeeSection(this.getEditor(), this, body));
	}

}
