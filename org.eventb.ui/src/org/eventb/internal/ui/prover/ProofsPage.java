/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prover;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.IProofTreeNode;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.internal.ui.EventBUIPlugin;

/**
 * @author htson
 * <p>
 * This is the implementation of the Proof Page in the Prover UI Editor.
 */
public class ProofsPage extends FormPage implements
		IProofStateChangedListener {

	// ID, title and the tab-title
	public static final String PAGE_ID = "Proof State"; //$NON-NLS-1$

	public static final String PAGE_TITLE = "Proof State";

	public static final String PAGE_TAB_TITLE = "Proof State";

	// Different sections
	private GoalSection goalSection;

	private HypothesesSection selectedSection;

	private HypothesesSection cachedSection;

	private HypothesesSection searchedSection;

	private Collection<Hypothesis> selected;

	private Collection<Hypothesis> cached;

	private Collection<Hypothesis> searched;
	
	private UserSupport userSupport;

	/**
	 * Constructor.
	 * <p>
	 * @param editor the Prover UI Editor contains this page. 
	 */
	public ProofsPage(ProverUI editor) {
		super(editor, PAGE_ID, PAGE_TAB_TITLE); //$NON-NLS-1$
		userSupport = editor.getUserSupport();
		userSupport.addStateChangedListeners(this);
		selected = new HashSet<Hypothesis>();
		cached = new HashSet<Hypothesis>();
		searched = new HashSet<Hypothesis>();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
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
		
		ProofState ps = userSupport.getCurrentPO();
		
		searchedSection = new SearchHypothesesSection(this, body,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| Section.COMPACT);
		managedForm.addPart(searchedSection);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		// gd.heightHint = 0;
		gd.widthHint = 200;
		searchedSection.getSection().setLayoutData(gd);

		if (ps != null) {
			Collection<Hypothesis> currentSearched = ps.getSearched();
			for (Iterator<Hypothesis> i = currentSearched.iterator();i.hasNext();) {
				searched.add(i.next());
			}
		}
		searchedSection.update(searched, new HashSet<Hypothesis>());
		
		cachedSection = new CacheHypothesesSection(this, body,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| Section.EXPANDED);
		managedForm.addPart(cachedSection);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 100;
		gd.minimumHeight = 100;
		gd.widthHint = 200;
		cachedSection.getSection().setLayoutData(gd);

		if (ps != null) {
			Collection<Hypothesis> currentCached = ps.getCached();
			for (Iterator<Hypothesis> i = currentCached.iterator();i.hasNext();) {
				cached.add(i.next());
			}
		}
		cachedSection.update(cached, new HashSet<Hypothesis>());
		
		selectedSection = new SelectedHypothesesSection(this, body,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| Section.EXPANDED);
		managedForm.addPart(selectedSection);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 150;
		gd.minimumHeight = 100;
		gd.widthHint = 200;
		selectedSection.getSection().setLayoutData(gd);
		
		
		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				Set<Hypothesis> currentSelected = node.getSequent().selectedHypotheses();
				for (Iterator<Hypothesis> i = currentSelected.iterator();i.hasNext();) {
					selected.add(i.next());
				}
			}
		}
		selectedSection.update(selected, new HashSet<Hypothesis>());
		
		
		goalSection = new GoalSection(this, body, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE | Section.EXPANDED);
		managedForm.addPart(goalSection);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 75;
		gd.minimumHeight = 50;
		gd.widthHint = 200;
		goalSection.getSection().setLayoutData(gd);

	}

	public void proofStateChanged(IProofStateDelta delta) {
		Display display = EventBUIPlugin.getDefault().getWorkbench()
		.getDisplay();
		final ProofState ps = delta.getNewProofState();
		if (ps != null) { // Reload everything
			display.syncExec(new Runnable() {
				public void run() {
					selected = ps.getCurrentNode().getSequent().selectedHypotheses();
					selectedSection.refresh(selected);
					cached = ps.getCached();
					cachedSection.refresh(cached);
					searched = ps.getSearched();
					searchedSection.refresh(searched);
				}
			});
		}
		else { // Update the hypotheses sections
			
		}
		// final PenguinDanceDialog dialog = new
		// PenguinDanceDialog(EventBUIPlugin.getActiveWorkbenchShell());
		//		
//		display.syncExec(new Runnable() {
//			public void run() {
//				ProofsPage.this.getEditor().editorDirtyStateChanged();
//			}
//		});
		
//		Collection<IHypothesisDelta> hypDelta = delta.getHypothesesDelta();
//
//		final Collection<Hypothesis> addedToSelected = new HashSet<Hypothesis>();
//		final Collection<Hypothesis> removedFromSelected = new HashSet<Hypothesis>();
//		final Collection<Hypothesis> addedToCached = new HashSet<Hypothesis>();
//		final Collection<Hypothesis> removedFromCached = new HashSet<Hypothesis>();
//		final Collection<Hypothesis> addedToSearched = new HashSet<Hypothesis>();
//		final Collection<Hypothesis> removedFromSearched = new HashSet<Hypothesis>();
//
//		for (Iterator<IHypothesisDelta> it = hypDelta.iterator(); it.hasNext();) {
//			IHypothesisDelta d = it.next();
//			Hypothesis hyp = d.getHypothesis();
//			UIUtils.debugProverUI("Hypothesis: " + hyp.getPredicate());
//			if ((d.getFlags() & IHypothesisDelta.F_ADDED_TO_SELECTED) != 0) {
//				addedToSelected.add(hyp);
//				if (cached.contains(hyp))
//					removedFromCached.add(hyp);
//				else if (searched.contains(hyp))
//					removedFromSearched.add(hyp);
//				UIUtils.debugProverUI("Add to UI Selected");
//				selected.add(hyp);
//			}
//			if ((d.getFlags() & IHypothesisDelta.F_REMOVED_FROM_SELECTED) != 0) {
//				removedFromSelected.add(hyp);
//				if (cached.contains(hyp))
//					addedToCached.add(hyp);
//				else if (searched.contains(hyp))
//					addedToSearched.add(hyp);
//				UIUtils.debugProverUI("Remove from UI Selected");
//				selected.remove(hyp);
//			}
//			if ((d.getFlags() & IHypothesisDelta.F_ADDED_TO_CACHED) != 0) {
//				if (!selected.contains(hyp)) {
//					addedToCached.add(hyp);
//					if (searched.contains(hyp))
//						removedFromSearched.add(hyp);
//				}
//				UIUtils.debugProverUI("Add to UI Cached");
//				cached.add(hyp);
//			}
//			if ((d.getFlags() & IHypothesisDelta.F_REMOVED_FROM_CACHED) != 0) {
//				if (!selected.contains(hyp)) {
//					removedFromCached.add(hyp);
//					if (searched.contains(hyp))
//						addedToSearched.add(hyp);
//				}
//				UIUtils.debugProverUI("Remove from UI Cached");
//				cached.remove(hyp);
//			}
//			if ((d.getFlags() & IHypothesisDelta.F_ADDED_TO_SEARCHED) != 0) {
//				if (!selected.contains(hyp) && !cached.contains(hyp))
//					addedToSearched.add(hyp);
//				UIUtils.debugProverUI("Add to UI Searched");
//				searched.add(hyp);
//			}
//			if ((d.getFlags() & IHypothesisDelta.F_REMOVED_FROM_SEARCHED) != 0) {
//				if (!selected.contains(hyp) && !cached.contains(hyp))
//					removedFromSearched.add(d.getHypothesis());
//				UIUtils.debugProverUI("Remove from UI Searched");
//				searched.remove(hyp);
//			}
//		}
//
//		// if (UIUtils.DEBUG) System.out.println("Update selectedSection");
////		Display display = EventBUIPlugin.getDefault().getWorkbench()
////				.getDisplay();
//		display.syncExec(new Runnable() {
//			public void run() {
//				selectedSection.update(addedToSelected, removedFromSelected);
//				cachedSection.update(addedToCached, removedFromCached);
//				searchedSection.update(addedToSearched, removedFromSearched);
//			}
//		});
	}

}