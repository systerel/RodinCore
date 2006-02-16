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

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.pm.ProofState;
import org.eventb.core.prover.rules.ProofTree;
import org.eventb.core.prover.sequent.HypothesesManagement;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.Tactic;
import org.eventb.core.prover.tactics.Tactics;

public class SelectedHypothesesSection
	extends SectionPart
{
	private static final String SECTION_TITLE = "Selected Hypotheses";
	private static final String SECTION_DESCRIPTION = "The set of selected hypotheses";	
	
    private ProofsPage page;
    private Composite comp;
    private ScrolledForm scrolledForm;
    
    private Collection<HypothesisRow> rows;
    
    private class DeselectHyperlinkAdapter extends HyperlinkAdapter {

		@Override
		public void linkActivated(HyperlinkEvent e) {
			Set<Hypothesis> deselected = new HashSet<Hypothesis>();
//			Collection<HypothesisRow> deletedRows = new HashSet<HypothesisRow>();
//				
			for (Iterator<HypothesisRow> it = rows.iterator(); it.hasNext();) {
				HypothesisRow hr = it.next();
				if (hr.isSelected()) {
					deselected.add(hr.getHypothesis());
//					deletedRows.add(hr);
//					hr.dispose();
				}
			}
//			rows.removeAll(deletedRows);
//			scrolledForm.reflow(true); // Important for refresh
//			SelectedHypothesesSection.this.refresh();	
			
			Tactic t = Tactics.mngHyp(HypothesesManagement.ActionType.DESELECT, deselected);
			apply(t);
			//page.getProverSequent().deselectHypotheses(deselected);
		}
    	
    }
    
    private void apply(Tactic t) {
    	ProverUI editor = (ProverUI) page.getEditor();
		if (editor != null) {
			TreeViewer viewer = editor.getContentOutline().getViewer();
		
			ISelection selection = viewer.getSelection();
			Object obj = ((IStructuredSelection) selection).getFirstElement();

			if (obj instanceof ProofTree) {
				ProofTree proofTree = (ProofTree) obj;
				if (!proofTree.isClosed()) {
					t.apply(proofTree);
					editor.getContentOutline().refresh(proofTree);
					// Expand the node
					viewer.expandToLevel(proofTree, AbstractTreeViewer.ALL_LEVELS);
					//viewer.setExpandedState(proofTree, true);

					// Select the "next" pending "subgoal"
					ProofState ps = editor.getUserSupport().getCurrentPO();
					ProofTree pt = ps.getNextPendingSubgoal(proofTree);
					if (pt != null) 
						editor.getContentOutline().getViewer().setSelection(new StructuredSelection(pt));
				}
			}
		}
    }
    
    
    // Contructor
	public SelectedHypothesesSection(ProofsPage page, Composite parent, int style) {
		super(parent, page.getManagedForm().getToolkit(), style);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		createClient(getSection(), toolkit);
		rows = new HashSet<HypothesisRow>();
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
		FormText formText = toolkit.createFormText(comp, true);
		gd = new GridData();
		gd.widthHint = 50;
		gd.horizontalAlignment = SWT.CENTER;
		formText.setLayoutData(gd);
		formText.addHyperlinkListener(new DeselectHyperlinkAdapter());
		formText.setText("<form><li style=\"text\" value=\"\" bindent=\"-20\"><a href=\"ds\">ds</a></li></form>", true, false);
		
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

//	public void setHypotheses(Set<Hypothesis> hypotheses) {
//		
//		for (Iterator<HypothesisRow> it = rows.iterator(); it.hasNext();) {
//			HypothesisRow hr = it.next();
//			hr.dispose();
//		}
//		
//		rows.clear();
//		
//		for (Iterator<Hypothesis> it = hypotheses.iterator();it.hasNext();) {
//			Hypothesis hyp = it.next();
//			HypothesisRow row = new HypothesisRow(this.getManagedForm().getToolkit(), comp, hyp);
//			rows.add(row);
//		}
//		
//		scrolledForm.reflow(true); // Important for refresh
//		this.refresh();
//		return;
//	}
	
	
	protected void update(Collection<Hypothesis> added, Collection<Hypothesis> removed) {
		Collection<HypothesisRow> deletedRows = new HashSet<HypothesisRow>();

		for (Iterator<HypothesisRow> it = rows.iterator(); it.hasNext();) {
			HypothesisRow hr = it.next();
			if (removed.contains(hr.getHypothesis())) {
				deletedRows.add(hr);
				hr.dispose();
			}
		}
		rows.removeAll(deletedRows);
		
		for (Iterator<Hypothesis> it = added.iterator(); it.hasNext();) {
			Hypothesis hp = it.next();
			HypothesisRow row = new HypothesisRow(this.getManagedForm().getToolkit(), comp, hp);
			rows.add(row);
		}

		scrolledForm.reflow(true); // Important for refresh
		this.refresh();

	}
}