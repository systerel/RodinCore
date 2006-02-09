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

package org.eventb.internal.ui.prover;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

public class CacheHypothesesSection
	extends SectionPart
{
	private static final String SECTION_TITLE = "Cache Hypotheses";
	private static final String SECTION_DESCRIPTION = "List of hypotheses in the cache";	
	
	//Leaf [] contexts;
	
    private FormPage page;

    private class IContextButtonListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			Button obj = (Button) (e.getSource());
			if (obj.getSelection()) {
			}
		}
	}

    // Contructor
	public CacheHypothesesSection(FormPage page, Composite parent, int style) {
		super(parent, page.getManagedForm().getToolkit(), style);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		createClient(getSection(), toolkit);
	}

	public void createClient(Section section, FormToolkit toolkit) {
        section.setText(SECTION_TITLE);
        section.setDescription(SECTION_DESCRIPTION);
        ScrolledForm scrolledForm = toolkit.createScrolledForm(section);
        
		Composite comp = scrolledForm.getBody();
        GridLayout layout = new GridLayout();
        layout.numColumns  = 6;
        layout.verticalSpacing = 5;
		comp.setLayout(layout);
		section.setClient(scrolledForm);
        toolkit.paintBordersFor(scrolledForm);
		
		for (int i = 0; i < 5; i++) {
			GridData gd;
			toolkit.createButton(comp, "", SWT.CHECK);
			Button b1 = toolkit.createButton(comp, "sl", SWT.PUSH);
			gd = new GridData();
			gd.widthHint = 25;
			b1.setLayoutData(gd);
			b1 = toolkit.createButton(comp, "eh", SWT.PUSH);
			b1.setLayoutData(gd);
			b1 = toolkit.createButton(comp, "he", SWT.PUSH);
			b1.setLayoutData(gd);
			
			Text textInput = toolkit.createText(comp, "", SWT.SINGLE);
			gd = new GridData();
			gd.widthHint = 20;
			textInput.setLayoutData(gd);
			
			Text hyp = toolkit.createText(comp, "x ∈ S", SWT.READ_ONLY);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			hyp.setLayoutData(gd);
		}

	}
	
	//private FormPage getPage() {return page;}
	@Override
	protected void expansionStateChanging(boolean expanding) {
		// TODO Estimate the heightHint by the number of hypothesis, etc.
		if (expanding) {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 150;
			gd.minimumHeight = 100;
			gd.widthHint = 200;
			this.getSection().setLayoutData(gd);
		}
		else {
			GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			gd.widthHint = 200;
			this.getSection().setLayoutData(gd);
		}
		super.expansionStateChanging(expanding);
	}

}