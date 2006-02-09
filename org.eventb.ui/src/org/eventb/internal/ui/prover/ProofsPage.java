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

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.prover.rules.ProofTree;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.sequent.IProverSequent;

public class ProofsPage extends FormPage {
	
	public static final String PAGE_ID = "Proof State"; //$NON-NLS-1$
	public static final String PAGE_TITLE = "Proof State";
	public static final String PAGE_TAB_TITLE = "Proof State";
	
	private GoalSection goal;
	private SelectedHypothesesSection selected;
	private IProverSequent ps;
	
	public ProofsPage(FormEditor editor) {
		super(editor, PAGE_ID, PAGE_TAB_TITLE);  //$NON-NLS-1$
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		form.setText(PAGE_TITLE); //$NON-NLS-1$
		Composite body = form.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginWidth = 10;
		layout.verticalSpacing = 20;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		body.setLayoutData(gd);
		
		SearchHypothesesSection search = new SearchHypothesesSection(this, body, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | Section.COMPACT);
		managedForm.addPart(search);
		
		CacheHypothesesSection cache = new CacheHypothesesSection(this, body, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | Section.EXPANDED);
		managedForm.addPart(cache);

		selected = new SelectedHypothesesSection(this, body, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | Section.EXPANDED);
		managedForm.addPart(selected);

		goal = new GoalSection(this, body, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | Section.EXPANDED);
		managedForm.addPart(goal);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 60;
		gd.minimumHeight = 40;
		gd.widthHint = 200;
		goal.getSection().setLayoutData(gd);
		
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 200;
		gd.minimumHeight = 100;
		gd.widthHint = 200;
		selected.getSection().setLayoutData(gd);
		
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 150;
		gd.minimumHeight = 100;
		gd.widthHint = 200;
		cache.getSection().setLayoutData(gd);
		
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 0;
		gd.widthHint = 200;
		search.getSection().setLayoutData(gd);
	}
	
	public void setSelection(Object obj) {
		if (obj instanceof ProofTree) {
			ProofTree pt = (ProofTree) obj;
			goal.setGoal(pt);
			
			ps = pt.getRootSeq();
			Set<Hypothesis> sh = ps.selectedHypotheses();
			selected.setHypotheses(sh);
		}
		return;
	}
	
	protected IProverSequent getProverSequent() {return ps;}
}
