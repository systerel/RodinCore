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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.IVariable;

/**
 * @author htson
 * <p>
 * An implementation of Event-B detail page
 * for editing Variable object (Rodin Element).
 */
public class VariableDetailsSection 
	extends EventBDetailsSection 
{

	// Title and description of the section.
	private static final String SECTION_TITLE = "Modifying Variables";
	private static final String SECTION_DESCRIPTION = "Modifying the details of the selected variable";	
	
	// The row for editing name.
	EventBInputRow name; 
	
	/**
	 * Contructor.
	 * <p>
	 * @param block the MasterDetailBlock of this section
	 * @param input The Variable (Rodin Element) associated with this
	 */
	public VariableDetailsSection(EventBMasterDetailsBlock block, IVariable input) {
		super(block, input);
	}


	/**
	 * Creat the content of the section.
	 */
	public void createClient(Section section, FormToolkit toolkit) {
        section.setText(SECTION_TITLE);
        section.setDescription(SECTION_DESCRIPTION);
		Composite comp = toolkit.createComposite(section);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 5;
        layout.marginHeight = 5;
        layout.numColumns = 2;
        layout.verticalSpacing = 5;
		comp.setLayout(layout);
		toolkit.paintBordersFor(comp);
        section.setClient(comp);
        
        name = new NameInputRow(this, toolkit, comp, "name*", "Name of the variable");
        addRow(name);
		
		
        GridData gd = new GridData(GridData.FILL_BOTH);
		gd.minimumWidth = 250;
		gd.minimumHeight = 100;
		section.setLayoutData(gd);
		
		this.setFocus();
	}


	/**
	 * Setting the focus when there is a selection change.
	 * <p>
	 * @see org.eclipse.ui.forms.IDetailsPage#inputChanged(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void selectionChanged(IFormPart part, ISelection selection) {
		this.setFocus();
	}


	/**
	 * Setting the focus for editing the name.
	 */
	public void setFocus() {
		name.setFocus();
	}
	
}
