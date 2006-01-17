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

package org.eventb.ui.internal.editors;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.ui.editors.IEventBFormText;
import org.rodinp.core.IRodinFile;

/**
 * @author htson
 * <p>
 * An implementation of Event-B Mirror section to display the information of
 * Rodin elements.
 */
public abstract class EventBMirrorSection
	extends SectionPart
	implements ChangeListener
{

	// The XML Form Text.
	private IEventBFormText formText;
	
	// The hyperlink listener.
	private HyperlinkAdapter listener;
	
	// The form page that contains this mirror section.
    private FormPage page;
    
    // The Rodin file where the information in the section belongs to.
    protected IRodinFile rodinFile;
    
    // The scrolled form.
    private ScrolledForm form;
    
	
    /**
     * Constructor.
     * <p>
     * @param page The form page that contains this mirror section.
     * @param parent The composite parent
     * @param style The style
     * @param title The title of the section
     * @param description The description of the section
     * @param rodinFile The Rodin File
     */
    public EventBMirrorSection(FormPage page, Composite parent, int style, String title, String description, IRodinFile rodinFile) {
		super(parent, page.getManagedForm().getToolkit(), style);
		this.page = page;
		this.rodinFile = rodinFile;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		Section section = getSection();
        section.setText(title);
        section.setDescription(description);
        listener = null;
		createClient(section, toolkit);
	}

    
    /**
     * Create the client of the section.
     * <p>
     * @param section The section (which will be used as composite parent)
     * @param toolkit The Form Toolkit used to create the section
     */
	public void createClient(Section section, FormToolkit toolkit) {
		form = toolkit.createScrolledForm(section);
		form.getBody().setLayout(new GridLayout());
		toolkit.paintBordersFor(form);
		section.setClient(form);
		formText = new EventBFormText(form.getBody(), toolkit);
	}
		
	
	/**
	 * Getting the XML Form String.
	 */
	// TODO Need to treat '<' differently
	protected abstract String getFormString();
	
	
	/**
	 * Create the hyperlink listener
	 * <p>
	 * @return A Hyperlink listener
	 */
	protected abstract HyperlinkAdapter createHyperlinkListener();
	
	
	/**
	 * Utitlity method to create a text and link with the same label
	 * <p>
	 * @param link a String
	 * @return XML formatted string represents the link
	 */
	protected String makeHyperlink(String link) {
		return "<a href=\"" + link + "\">" + link + "</a>";
	}
	
	
	/**
	 * Refresh the form
	 */
	public void refresh() {
		formText.setText(getFormString());
		if (listener != null) formText.removeHyperlinkListener(listener);
		listener = createHyperlinkListener();
		formText.addHyperlinkListener(listener);
		form.reflow(true);  // refresh the form and recompute the boundary
		super.refresh();
	}
	
	
	/**
	 * Return the page container of this section
	 * @return A Form page
	 */
	protected FormPage getPage() {return page;}


	/**
	 * A method responses for changes.
	 */
	// TODO Remove when the editor listens to Rodin DB changes. 
	public void stateChanged(ChangeEvent event) {this.refresh();}

}
