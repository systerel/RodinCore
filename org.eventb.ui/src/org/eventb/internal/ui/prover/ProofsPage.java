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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ScrollBar;
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

/**
 * @author htson
 *         <p>
 *         This is the implementation of the Proof Page in the Prover UI Editor.
 */
public class ProofsPage extends FormPage implements IProofStateChangedListener {

	// ID, title and the tab-title
	public static final String PAGE_ID = "Proof State"; //$NON-NLS-1$

	public static final String PAGE_TITLE = "Proof State";

	public static final String PAGE_TAB_TITLE = "Proof State";

	// Different sections
	private GoalSection goalSection;

	private HypothesesSection selectedSection;

	private HypothesesSection cachedSection;

	private HypothesesSection searchedSection;

	private SashForm sashForm;

	private UserSupport userSupport;

	private Composite body;

	private Composite comp;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param editor
	 *            the Prover UI Editor contains this page.
	 */
	public ProofsPage(ProverUI editor) {
		super(editor, PAGE_ID, PAGE_TAB_TITLE); //$NON-NLS-1$
		userSupport = editor.getUserSupport();
		userSupport.addStateChangedListeners(this);
	}

	@Override
	public void dispose() {
		userSupport.removeStateChangedListeners(this);
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.forms.editor.FormPage#createFormContent(org.eclipse.ui.forms.IManagedForm)
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		form.setText(PAGE_TITLE); //$NON-NLS-1$
		body = form.getBody();

		sashForm = new SashForm(body, SWT.VERTICAL);
		sashForm.setSize(400, 600);

		FillLayout flayout = new FillLayout(SWT.VERTICAL);
		sashForm.setLayout(flayout);

		comp = new Composite(sashForm, SWT.NULL);
		comp.setBackground(form.getBackground());

		searchedSection = new SearchHypothesesSection(this, sashForm,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| Section.COMPACT);
		managedForm.addPart(searchedSection);

		cachedSection = new CacheHypothesesSection(this, sashForm,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| Section.EXPANDED);
		managedForm.addPart(cachedSection);
		
		selectedSection = new SelectedHypothesesSection(this, sashForm,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| Section.EXPANDED);
		managedForm.addPart(selectedSection);

		initHypothesisSections();

		goalSection = new GoalSection(this, sashForm,
				ExpandableComposite.TITLE_BAR);
		managedForm.addPart(goalSection);

		body.setLayout(new ProofPageLayout());

//		layout();
	}

	private class ProofPageLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint,
				boolean flushCache) {
			Point pt = sashForm.getSize();
			if (sashForm != null)
				return new Point(pt.x, pt.y);

			Rectangle rect = ProofsPage.this.getManagedForm().getForm()
					.getClientArea();

			Point size = new Point(rect.width, rect.height);
			return size;
		}

		@Override
		protected void layout(Composite composite, boolean flushCache) {
			ProofsPage.this.layout();
		}

	}

	private void initHypothesisSections() {
		ProofState ps = userSupport.getCurrentPO();
		Set<Hypothesis> selected = new HashSet<Hypothesis>();
		ArrayList<Hypothesis> cached = new ArrayList<Hypothesis>();
		ArrayList<Hypothesis> searched = new ArrayList<Hypothesis>();

		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				selected = node.getSequent().selectedHypotheses();
			}
			Collection<Hypothesis> currentCached = ps.getCached();
			for (Iterator<Hypothesis> i = currentCached.iterator(); i.hasNext();) {
				Hypothesis hyp = i.next();
				if (node != null)
					if (!node.getSequent().hypotheses().contains(hyp))
						continue;
				if (!selected.contains(hyp))
					cached.add(hyp);
			}
			Collection<Hypothesis> currentSearched = ps.getSearched();
			for (Iterator<Hypothesis> i = currentSearched.iterator(); i
					.hasNext();) {
				Hypothesis hyp = i.next();
				if (node != null)
					if (!node.getSequent().hypotheses().contains(hyp))
						continue;
				if (!selected.contains(hyp) && !cached.contains(hyp))
					searched.add(hyp);
			}
		}
		selectedSection.init(selected);
		cachedSection.init(cached);
		searchedSection.init(searched);

	}

	public void proofStateChanged(final IProofStateDelta delta) {
		Display display = this.getEditorSite().getWorkbenchWindow()
				.getWorkbench().getDisplay();

		display.syncExec(new Runnable() {
			public void run() {
				ProofState ps = delta.getNewProofState();
				boolean refresh = false;
				if (ps != null) { // Reload everything
					refresh = true;
					goalSection.setGoal(ps.getCurrentNode());
				} else {
					IProofTreeNode node = delta.getNewProofTreeNode();
					if (node != null) {
						refresh = true;
						goalSection.setGoal(node);
					} else {
						if (delta.getNewCache() || delta.getNewSearch()) {
							initCacheAndSearch();
							ProofsPage.this.getManagedForm().getForm().reflow(true);
						}
					}
				}

				if (refresh) {
					initHypothesisSections();
					ProofsPage.this.getManagedForm().getForm().reflow(true);
				}
			}
		});
	}

	private void initCacheAndSearch() {
		ProofState ps = userSupport.getCurrentPO();
		Set<Hypothesis> selected = new HashSet<Hypothesis>();
		ArrayList<Hypothesis> cached = new ArrayList<Hypothesis>();
		ArrayList<Hypothesis> searched = new ArrayList<Hypothesis>();

		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				selected = node.getSequent().selectedHypotheses();
			}
			Collection<Hypothesis> currentCached = ps.getCached();
			for (Iterator<Hypothesis> i = currentCached.iterator(); i.hasNext();) {
				Hypothesis hyp = i.next();
				if (node != null)
					if (!node.getSequent().hypotheses().contains(hyp))
						continue;
				if (!selected.contains(hyp))
					cached.add(hyp);
			}

			Collection<Hypothesis> currentSearched = ps.getSearched();
			for (Iterator<Hypothesis> i = currentSearched.iterator(); i
					.hasNext();) {
				Hypothesis hyp = i.next();
				if (node != null)
					if (!node.getSequent().hypotheses().contains(hyp))
						continue;
				if (!selected.contains(hyp) && !cached.contains(hyp))
					searched.add(hyp);
			}
		}
		cachedSection.init(cached);
		searchedSection.init(searched);

	}

	private boolean flag;

	public void layout() {
		if (flag)
			return;
		flag = true;
		int[] weights = new int[5];
		weights[0] = 0;

		ScrolledForm form = this.getManagedForm().getForm();
		Rectangle original = form.getBody().getBounds();
		ProverUIUtils.debugProverUI("Original bound " + original);

		// -1 in totalHeight to avoid the vertical scrollbar in the beginning???
		int totalHeight = form.getClientArea().height - original.y - 1;
		int totalWidth = form.getClientArea().width;

		ScrollBar horizontal = form.getHorizontalBar();
		ScrollBar vertical = form.getVerticalBar();

		if (horizontal != null && horizontal.isVisible()) {
			totalHeight += horizontal.getSize().y;
			ProverUIUtils.debugProverUI("Horizontal " + horizontal.getSize());
		}

		if (vertical != null && vertical.isVisible()) {
			totalWidth += vertical.getSize().x;
			ProverUIUtils.debugProverUI("Vertical " + vertical.getSize());
		}

		ProverUIUtils.debugProverUI("Total Height: " + totalHeight);
		ProverUIUtils.debugProverUI("Total Width: " + totalWidth);

		weights[1] = searchedSection.getSection().computeSize(SWT.DEFAULT,
				SWT.DEFAULT).y;
		weights[2] = cachedSection.getSection().computeSize(SWT.DEFAULT,
				SWT.DEFAULT).y;

		weights[3] = selectedSection.getSection().computeSize(SWT.DEFAULT,
				SWT.DEFAULT).y;

		weights[4] = goalSection.getSection().computeSize(SWT.DEFAULT,
				SWT.DEFAULT).y;

		for (int i = 0; i < 5; i++) {
			ProverUIUtils.debugProverUI("Before Height (" + i + "): " + weights[i]);
		}

		if (totalHeight < 1) { // Not initialised yet
			weights[0] = 0;
//			UIUtils.debugProverUI("Client area: " + sashForm.getClientArea());
			sashForm.setWeights(weights);
			form.reflow(true);
		} else {
			int sum = 0;
			for (int i = 1; i < 5; i++) {
				sum += weights[i];
			}

			if (sum < totalHeight) {
				weights[0] = totalHeight - sum;
				ProverUIUtils.debugProverUI("Client area: "
						+ sashForm.getClientArea());
				Rectangle rect = sashForm.computeTrim(0, 0, totalWidth,
						totalHeight);
				sashForm.setBounds(rect);
				sashForm.setWeights(weights);
				form.reflow(true);
			} else {
				weights[0] = 0;
				for (int i = 1; i < 5; i++) {
					weights[i] = weights[i] * totalHeight / sum;
				}
				Rectangle rect = sashForm.computeTrim(0, 0, totalWidth,
						totalHeight);
				sashForm.setBounds(rect);

				ProverUIUtils.debugProverUI("Client area: "
						+ sashForm.getClientArea());
				sashForm.setWeights(weights);

				ProverUIUtils.debugProverUI("form Client area "
						+ form.getClientArea());
				form.reflow(true);
			}
		}

		flag = false;
	}

}