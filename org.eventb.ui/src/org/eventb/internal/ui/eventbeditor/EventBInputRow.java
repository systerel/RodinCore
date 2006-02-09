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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author htson
 * <p>
 * An abstract class to create an input row (a label and a text field) 
 * for editing Rodin elements (e.g. name, content, attribute, etc.).
 */
public abstract class EventBInputRow
	implements ModifyListener
{
	
	// Dirty status: The database is inconsistent with the physical file
	protected boolean dirty;
	
	// The text input area.
	protected IEventBInputText textInput;
	
	// The detail page contain this input row.
	protected EventBDetailsSection page;
	
	
	/**
	 * Contructor.
	 * @param page The detail page
	 * @param toolkit The Form Toolkit to create this row
	 * @param parent The composite parent
	 * @param label The label of the input row
	 * @param tip The tip for the input row
	 * @param style The style
	 */
	public EventBInputRow(EventBDetailsSection page, FormToolkit toolkit, Composite parent, String label, String tip, int style) {
		dirty = false;
		this.page = page;
		
        Label aLabel = toolkit.createLabel(parent, label + ":", SWT.NULL);
		aLabel.setForeground(toolkit.getColors().getColor(FormColors.TITLE));
		aLabel.setToolTipText(tip);
		
		textInput = createEventBInputText(parent, toolkit, style);
		GridData gD; 
		if ((style & SWT.MULTI) != 0) {
			gD = new GridData(GridData.FILL_BOTH);
			gD.widthHint = 120;
			gD.heightHint = 80;
		}
		else {
			gD = new GridData(GridData.FILL_HORIZONTAL);
		}
		textInput.setLayoutData(gD);
		setText();
        
		textInput.addModifyListener(this);
	}
	
	
	/**
	 * Create the input text field
	 * @param parent The composite parent
	 * @param toolkit The Form Toolkit used to create the Text area
	 * @param style The style to create the Text area
	 * @return A new Event-B Input Text area
	 */
	protected abstract IEventBInputText createEventBInputText(Composite parent, FormToolkit toolkit, int style);
	
	
	/**
	 * Set the Text in the input text area (initially).
	 */
	protected abstract void setText();
	

	/**
	 * The method responses to the changes in the text area.
	 */
	public void modifyText(ModifyEvent e) {
		dirty = true;
		page.markDirty();
	}
	
	
	/**
	 * Pass the focus to the input text field
	 */
	public void setFocus() {textInput.setFocus();}
	
	
	/**
	 * Commiting the changes.
	 */
	public abstract void commit();
	
	protected abstract void update();

}
