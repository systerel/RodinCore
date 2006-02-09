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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.prover.sequent.Hypothesis;

public class SelectedHypothesesSection
	extends SectionPart
{
	private static final String SECTION_TITLE = "Selected Hypotheses";
	private static final String SECTION_DESCRIPTION = "The set of selected hypotheses";	
	
    private ProofsPage page;
    private Composite comp;
    private ScrolledForm scrolledForm;
    
    private List<HypothesisRow> rows;
    
    private class IContextButtonListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {

			Set<Hypothesis> deselected = new HashSet<Hypothesis>();
			Collection<HypothesisRow> deletedRows = new HashSet<HypothesisRow>();
				
			for (Iterator<HypothesisRow> it = rows.iterator(); it.hasNext();) {
				HypothesisRow hr = it.next();
//				System.out.println("Row " + hr.toString());
				if (hr.isSelected()) {
//					System.out.println("Remove hyp: " + hr.getHypothesis());
					deselected.add(hr.getHypothesis());
					deletedRows.add(hr);
					hr.dispose();
				}
			}
			rows.removeAll(deletedRows);
//				System.out.println("Deselected " + deselected);
			page.getProverSequent().deselectHypotheses(deselected);
				
//			Set<Hypothesis> remain = page.getProverSequent().selectedHypotheses();
//			for (Iterator<Hypothesis> it = remain.iterator();it.hasNext();) {
//				Hypothesis hyp = it.next();
//				System.out.println("Hypothesis remained: " + hyp.toString());
//			}
				
			scrolledForm.reflow(true); // Important for refresh
			SelectedHypothesesSection.this.refresh();	
		}
	}

    // Contructor
	public SelectedHypothesesSection(ProofsPage page, Composite parent, int style) {
		super(parent, page.getManagedForm().getToolkit(), style);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		createClient(getSection(), toolkit);
		rows = new ArrayList<HypothesisRow>();
	}

	public void createClient(Section section, FormToolkit toolkit) {
        section.setText(SECTION_TITLE);
        section.setDescription(SECTION_DESCRIPTION);
        scrolledForm = toolkit.createScrolledForm(section);
        
		comp = scrolledForm.getBody();
        GridLayout layout = new GridLayout();
        layout.numColumns  = 3;
        layout.verticalSpacing = 5;
		comp.setLayout(layout);
		section.setClient(scrolledForm);
        toolkit.paintBordersFor(scrolledForm);
		
		GridData gd;
		Button b = toolkit.createButton(comp, "ds", SWT.PUSH);
		gd = new GridData();
		gd.widthHint = 25;
		gd.horizontalAlignment = SWT.CENTER;
		b.setLayoutData(gd);
		b.addSelectionListener(new IContextButtonListener());
		
		Composite container = toolkit.createComposite(comp);
        container.setLayout(new GridLayout());
		gd = new GridData();
		gd.horizontalSpan = 2;
		container.setLayoutData(gd);

	}
	

	@Override
	protected void expansionStateChanging(boolean expanding) {
		if (expanding) {
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 200;
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

	//private FormPage getPage() {return page;}

	public void setHypotheses(Set<Hypothesis> hypotheses) {
		
		for (Iterator<HypothesisRow> it = rows.iterator(); it.hasNext();) {
			HypothesisRow hr = it.next();
//			System.out.println("Dispose " + hr);
			hr.dispose();
		}
		
		rows.clear();
		
		for (Iterator<Hypothesis> it = hypotheses.iterator();it.hasNext();) {
			Hypothesis hyp = it.next();
//			System.out.println("Hypothesis " + hyp.toString());
			HypothesisRow row = new HypothesisRow(this.getManagedForm().getToolkit(), comp, hyp);
			rows.add(row);
		}
		
		scrolledForm.reflow(true); // Important for refresh
		this.refresh();
		return;
	}
	
}