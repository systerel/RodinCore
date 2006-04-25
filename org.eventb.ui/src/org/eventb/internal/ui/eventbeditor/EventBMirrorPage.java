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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.internal.ui.EventBFormText;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public abstract class EventBMirrorPage
	extends Page 
	implements	IEventBMirrorPage
{
	// The current associated editor.
//	private ProverUI editor;
	
	protected EventBEditor editor;
	ScrolledForm form;
	EventBFormText formText;

	// The hyperlink listener.
	private HyperlinkAdapter listener;

	/**
	 * Creates a content outline page using the given editor.
	 * Register as a change listener for the Rodin Database.
	 * <p> 
	 * @param editor the editor
	 */
	public EventBMirrorPage(EventBEditor editor) {
		super();
		this.editor = editor;
		editor.addElementChangedListener(this);
	}   
    
    public void createControl(Composite parent) {
    	FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    	form = toolkit.createScrolledForm(parent);
		form.getBody().setLayout(new GridLayout());
		formText = new EventBFormText(toolkit.createFormText(form.getBody(), true));
		toolkit.paintBordersFor(form.getBody());
		refresh();
    }

    /**
	 * Refresh the form
	 */
	public void refresh() {
		formText.getFormText().setText(getFormString(), true, false);
		if (listener != null) formText.getFormText().removeHyperlinkListener(listener);
		listener = createHyperlinkListener();
		formText.getFormText().addHyperlinkListener(listener);
		form.reflow(true);  // refresh the form and recompute the boundary
	}
	/**
	 * Return the form (XML formatted) string that represents the information 
	 * of the constants.
	 */
	abstract protected String getFormString();	

	/**
	 * Return the hyperlink listener which enable the navigation on the form. 
	 */
	abstract protected HyperlinkAdapter createHyperlinkListener();


	@Override
	public Control getControl() {
		return form;
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}

	/* (non-Javadoc)
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		IRodinElementDelta delta = event.getDelta();
		if (delta.getElement() instanceof IRodinFile && delta.getKind() != IRodinElementDelta.REMOVED) {
			Display.getCurrent().syncExec(new Runnable() {
				public void run() {
					refresh();
				}
			});
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	@Override
	public void dispose() {
		editor.removeElementChangedListener(this);
		super.dispose();
	}

}