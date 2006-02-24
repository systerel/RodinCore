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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.pm.IHypothesisChangeEvent;
import org.eventb.core.pm.IHypothesisChangedListener;
import org.eventb.core.pm.IHypothesisDelta;
import org.eventb.core.pm.IProofStatusChangedListener;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.internal.ui.EventBUIPlugin;

public class ProofsPage
	extends FormPage 
	implements	IHypothesisChangedListener,
				IProofStatusChangedListener
{
	
	public static final String PAGE_ID = "Proof State"; //$NON-NLS-1$
	public static final String PAGE_TITLE = "Proof State";
	public static final String PAGE_TAB_TITLE = "Proof State";
	
	private GoalSection goal;
	private HypothesesSection selected;
	private HypothesesSection cache;
	private HypothesesSection search;
	
	public ProofsPage(ProverUI editor) {
		super(editor, PAGE_ID, PAGE_TAB_TITLE);  //$NON-NLS-1$
		editor.getUserSupport().addHypothesisChangedListener(this);
		editor.getUserSupport().addProofStatusChangedListener(this);
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
		
		search = new SearchHypothesesSection(this, body, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | Section.COMPACT);
		managedForm.addPart(search);
		
		cache = new CacheHypothesesSection(this, body, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | Section.EXPANDED);
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

	public void hypothesisChanged(IHypothesisChangeEvent e) {
		IHypothesisDelta delta = e.getDelta();
		
		final Collection<Hypothesis> addedToSelected = delta.getHypotheses(IHypothesisDelta.SELECTED, IHypothesisDelta.ADDED);
		final Collection<Hypothesis> removedFromSelected = delta.getHypotheses(IHypothesisDelta.SELECTED, IHypothesisDelta.REMOVED);
//		System.out.println("Update selected");
		Display display = EventBUIPlugin.getDefault().getWorkbench().getDisplay();
		display.syncExec (new Runnable () {
			public void run () {
				selected.update(addedToSelected, removedFromSelected);
			}
		});
//		System.out.println("***************");
		
		final Collection<Hypothesis> addedToCached = delta.getHypotheses(IHypothesisDelta.CACHED, IHypothesisDelta.ADDED);
		final Collection<Hypothesis> removedFromCached = delta.getHypotheses(IHypothesisDelta.CACHED, IHypothesisDelta.REMOVED);
//		System.out.println("Update cached");
		display.syncExec (new Runnable () {
			public void run () {
				cache.update(addedToCached, removedFromCached);
			}
		});
//		System.out.println("*************");
		
		final Collection<Hypothesis> addedToSearched = delta.getHypotheses(IHypothesisDelta.SEARCHED, IHypothesisDelta.ADDED);
		final Collection<Hypothesis> removedFromSearched = delta.getHypotheses(IHypothesisDelta.SEARCHED, IHypothesisDelta.REMOVED);
//		System.out.println("Update searched");
		display.syncExec (new Runnable () {
			public void run () {
				search.update(addedToSearched, removedFromSearched);
			}
		});
//		System.out.println("*************");	
	}

	/* (non-Javadoc)
	 * @see org.eventb.core.pm.IProofStatusChangedListener#proofStatusChanged()
	 */
	public void proofStatusChanged(final boolean complete) {
		goal.markDirty();
		final PenguinDanceDialog dialog = new PenguinDanceDialog(EventBUIPlugin.getActiveWorkbenchShell());
		
		Display display = EventBUIPlugin.getDefault().getWorkbench().getDisplay();
		display.syncExec (new Runnable () {
			public void run () {
				if (complete) dialog.open();
			}
		});
	}
	
}