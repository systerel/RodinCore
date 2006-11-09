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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IRodinElementDelta;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Mirror 'page' for showing
 *         related information to the current editting construct in the Event-B
 *         Editor.
 */
public abstract class EventBMirrorPage extends Page implements
		IEventBMirrorPage {

	// The associated Event-B Editor
	protected IEventBEditor editor;

	ScrolledForm form;

	EventBFormText formText;

	// The hyperlink listener.
	private HyperlinkAdapter listener;

	/**
	 * @return the form (XML formatted) string that represents the related
	 *         information of current editting construct.
	 */
	abstract protected String getFormString();

	/**
	 * @return the hyperlink listener which enable the navigation on the form.
	 */
	abstract protected HyperlinkAdapter createHyperlinkListener();

	/**
	 * Creates a content outline page using the given editor. Register as a
	 * change listener for the Rodin Database.
	 * <p>
	 * 
	 * @param editor
	 *            the editor
	 */
	public EventBMirrorPage(IEventBEditor editor) {
		super();
		this.editor = editor;
		editor.addElementChangedListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.getBody().setLayout(new GridLayout());
		formText = new EventBFormText(toolkit.createFormText(form.getBody(),
				true));
		toolkit.paintBordersFor(form.getBody());
		refresh();
	}

	/**
	 * Refresh the form
	 */
	public void refresh() {
		formText.getFormText().setText(getFormString(), true, false);
		if (listener != null)
			formText.getFormText().removeHyperlinkListener(listener);
		listener = createHyperlinkListener();
		formText.getFormText().addHyperlinkListener(listener);
		form.reflow(true); // refresh the form and recompute the boundary
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#getControl()
	 */
	@Override
	public Control getControl() {
		return form;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	@Override
	public void setFocus() {
		form.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(final ElementChangedEvent event) {
		Display.getCurrent().syncExec(new Runnable() {
			public void run() {
				IRodinElementDelta delta = event.getDelta();
				if (delta.getElement() instanceof IRodinFile
						&& delta.getKind() != IRodinElementDelta.REMOVED) {
					refresh();
				}
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#dispose()
	 */
	@Override
	public void dispose() {
		formText.dispose();
		editor.removeElementChangedListener(this);
		super.dispose();
	}

}