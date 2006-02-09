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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.rodinp.core.IInternalElement;

/**
 * @author htson
 * <p>
 * An abstract class for detail pages for editing Rodin elements.
 */
public abstract class EventBDetailsSection
	extends AbstractFormPart
	implements IDetailsPage
{

	// List of input rows.
	private List<EventBInputRow> rows;
	
	// The master detail block that holds this detail page.
    private EventBMasterDetailsBlock block;
    
    // The Internal Element input of this detail page.
    protected IInternalElement input;


    /**
     * Constructor.
     * @param block The master detail block that holds this detail page
     * @param input
     */
    public EventBDetailsSection(EventBMasterDetailsBlock block, IInternalElement input) {
		rows = new ArrayList<EventBInputRow>();
		this.block = block;
		this.input = input;
	}

	
    /**
     * Creating the content of the section.
	 * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
	 */
	public void createContents(Composite parent) {
		FormToolkit toolkit = getManagedForm().getToolkit();
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 5;
		layout.rightMargin = 0;
		layout.bottomMargin = 0;
		parent.setLayout(new GridLayout());
		Section section = toolkit.createSection(parent, Section.TITLE_BAR|Section.DESCRIPTION);
		section.marginHeight = 5;		
		section.marginWidth = 5;

		createClient(section, toolkit);
	}

	
	/**
	 *  Abstract class for creating the client of the section.
	 */ 
	protected abstract void createClient(Section section, FormToolkit toolkit);

	
	/**
	 * Register an input row belongs to this detail section.
	 * @param row an Event-B input row
	 */
	protected void addRow(EventBInputRow row) {
		rows.add(row);
	}
	
	
	/**
	 * Commit the changes by going through the input rows and commit the changes.
	 * @see org.eclipse.ui.forms.AbstractFormPart#commit(boolean)
	 */
	public void commit(boolean onSave) {
		System.out.println("COMMIT");
		super.commit(onSave);
		for (int i = 0; i < rows.size(); i++)
			((EventBInputRow) rows.get(i)).commit();
	}

	public void doSave(boolean onSave) {
		for (int i = 0; i < rows.size(); i++)
			((EventBInputRow) rows.get(i)).commit();
	}
	
	/**
	 * Return the Master Detail block that holds this detail section.
	 * @return An Event-B Master Detail block
	 */
	protected EventBMasterDetailsBlock getBlock() {return block;}
	
	
	/**
	 * Return the Internal Element belongs to this detail section.  
	 * @return An Internal Element
	 */
	protected IInternalElement getInput() {return input;}

	
	/**
	 * Setting the input of this detail section.
	 * @param input An Internal Element
	 */
	protected void setInput(IInternalElement input) {
		this.input = input;
	}

	protected void update() {
		for (int i = 0; i < rows.size(); i++) {
			((EventBInputRow) rows.get(i)).update();
		}
	}
}
