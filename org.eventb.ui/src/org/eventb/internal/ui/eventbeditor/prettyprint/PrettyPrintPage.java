/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.eventbeditor.prettyprint;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.internal.ui.UIUtils;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.EventBFormText;
import org.eventb.ui.EventBUIPlugin;
import org.eventb.ui.IEventBFormText;
import org.eventb.ui.eventbeditor.EventBEditorPage;
import org.eventb.ui.eventbeditor.IEventBEditor;
import org.rodinp.core.ElementChangedEvent;
import org.rodinp.core.IElementChangedListener;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinCore;

/**
 * @author htson
 *         <p>
 *         This class extends the
 *         <code>org.eventbui.eventbeditor.EventBEditorPage</code> class and
 *         provides a pretty print page for Event-B Editor
 */
public class PrettyPrintPage extends EventBEditorPage implements
		IElementChangedListener {

	// Title, tab title and ID of the page.
	public static final String PAGE_ID = EventBUIPlugin.PLUGIN_ID
			+ ".prettyprintpage"; //$NON-NLS-1$

	public static final String PAGE_TITLE = Messages.editorPage_prettyPrint_title;

	public static final String PAGE_TAB_TITLE = Messages.editorPage_prettyPrint_tabTitle;

	// The scrolled form
	private ScrolledForm form;

	// The form text
	private IEventBFormText formText;
	
	private boolean needsUpdate;
	
	private AstConverter astConverter;


	/**
	 * Constructor: This default constructor will be used to create the page
	 */
	public PrettyPrintPage() {
		super(PAGE_ID, PAGE_TAB_TITLE, PAGE_TITLE);
		astConverter = new Ast2HtmlConverter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.IFormPage#initialize(org.eclipse.ui.forms.editor.FormEditor)
	 */
	@Override
	public void initialize(FormEditor editor) {
		super.initialize(editor);
		((IEventBEditor) editor).addElementChangedListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		form = managedForm.getForm();
		Composite body = form.getBody();
		body.setLayout(new FillLayout());

		FormText widget = managedForm.getToolkit().createFormText(body, true);

		widget.addHyperlinkListener(new HyperlinkAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see org.eclipse.ui.forms.events.HyperlinkAdapter#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
			 */
			@Override
			public void linkActivated(HyperlinkEvent e) {
				String id = (String) e.getHref();
				IRodinElement element = RodinCore.valueOf(id);
				if (element != null && element.exists())
					UIUtils.linkToEventBEditor(element);
			}
		});

		formText = new EventBFormText(widget);

		setFormText(new NullProgressMonitor());

	}

	/**
	 * This private helper method is use to set the content string of the form
	 * text. The content string is set according to the type of the rodin input
	 * file and the content of that file
	 * <p>
	 * 
	 * @param monitor
	 *            a progress monitor
	 */
	void setFormText(IProgressMonitor monitor) {
		IRodinFile rodinFile = ((IEventBEditor) this.getEditor())
		.getRodinInput();
		String text = astConverter.getText(monitor, rodinFile);
		formText.getFormText().setText(text, true, true);
		form.reflow(true);
	}

	

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (formText != null)
			formText.dispose();
		((IEventBEditor) this.getEditor()).removeElementChangedListener(this);
		super.dispose();
	}
	
	@Override
	public void setActive(boolean active) {
		super.setActive(active);
		if (form == null)
			return;
		if (form.getContent().isDisposed())
			return;
		if (active && needsUpdate) {
			// We are switching to this page - refresh it
			// if needed.
			Display display = this.getEditorSite().getShell().getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					// Reset the content string of the form text
					setFormText(new NullProgressMonitor());
				}
			});
			needsUpdate = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rodinp.core.IElementChangedListener#elementChanged(org.rodinp.core.ElementChangedEvent)
	 */
	public void elementChanged(ElementChangedEvent event) {
		needsUpdate = true;
	}

}
