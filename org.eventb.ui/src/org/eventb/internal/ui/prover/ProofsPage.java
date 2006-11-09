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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.ProofState;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.RodinDBException;

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

	private static final int MIN_SECTION_HEIGHT = 30;

	private static final int DEFAULT_HEIGHT = 400;

	private static final int DEFAULT_WIDTH = 400;

	// Different sections
	GoalSection goalSection;

	private HypothesesSection selectedSection;

	private HypothesesSection cachedSection;

	HypothesesSection searchedSection;

	private SashForm sashForm;

	UserSupport userSupport;

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
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		form.setText(PAGE_TITLE); //$NON-NLS-1$
		body = form.getBody();

		sashForm = new SashForm(body, SWT.VERTICAL);
		sashForm.setSize(400, 600);

		FillLayout flayout = new FillLayout(SWT.VERTICAL);
		sashForm.setLayout(flayout);
		// sashForm.setLayout(new GridLayout());

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
		cachedSection.getSection().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		selectedSection = new SelectedHypothesesSection(this, sashForm,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE
						| Section.EXPANDED);
		managedForm.addPart(selectedSection);
		// selectedSection.getSection().setLayoutData(
		// new GridData(SWT.FILL, SWT.FILL, true, true));

		ProofState ps = userSupport.getCurrentPO();
		initHypothesisSections(ps);

		goalSection = new GoalSection(this, sashForm,
				ExpandableComposite.TITLE_BAR);
		managedForm.addPart(goalSection);
		// goalSection.getSection().setLayoutData(
		// new GridData(SWT.FILL, SWT.FILL, true, true));

		// body.setLayout(new ProofPageLayout());

		body.addControlListener(new ControlAdapter() {

			@Override
			public void controlResized(ControlEvent e) {
				internalLayout();
				super.controlResized(e);
			}

		});
		
		createToolBarActions(managedForm);
		// Try auto layout in the beginning 
		autoLayout();
	}

	/**
	 * Create the Toolbar actions.
	 * <p>
	 * 
	 * @param managedForm
	 *            The managed form contains the Toolbar.
	 */
	protected void createToolBarActions(IManagedForm managedForm) {
		final Action layoutAction = new Action("auto", Action.AS_PUSH_BUTTON) {
			@Override
			public void run() {
				autoLayout();
			}
		};
		layoutAction.setChecked(false);
		layoutAction.setToolTipText("Automatically layout");

		ScrolledForm form = managedForm.getForm();

		form.getToolBarManager().add(layoutAction);
		form.updateToolBar();
	}

//	private updateButtons() {
//		
//	}
	
//	private class ProofPageLayout extends Layout {
//
//		@Override
//		protected Point computeSize(Composite composite, int wHint, int hHint,
//				boolean flushCache) {
//			Point pt = sashForm.getSize();
//			if (sashForm != null)
//				return new Point(pt.x, pt.y);
//
//			Rectangle rect = ProofsPage.this.getManagedForm().getForm()
//					.getClientArea();
//
//			Point size = new Point(rect.width, rect.height);
//			return size;
//		}
//
//		@Override
//		protected void layout(Composite composite, boolean flushCache) {
//			ProofsPage.this.autoLayout();
//		}
//
//	}

	void initHypothesisSections(ProofState ps) {

		Collection<Hypothesis> selected = new ArrayList<Hypothesis>();
		Collection<Hypothesis> cached = new ArrayList<Hypothesis>();
		Collection<Hypothesis> searched = new ArrayList<Hypothesis>();

		boolean enable = false;
		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				selected = node.getSequent().selectedHypotheses();
				if (node.isOpen())
					enable = true;
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

		selectedSection.init(selected, enable);
		cachedSection.init(cached, enable);
		searchedSection.init(searched, enable);

	}

	public void proofStateChanged(final IProofStateDelta delta) {
		Display display = Display.getDefault();
		if (this.getManagedForm().getForm().isDisposed())
			return;
		if (ProverUIUtils.DEBUG)
			ProverUIUtils
					.debug("Proof State Change "
							+ ((ProverUI) getEditor()).getRodinInput()
									.getElementName());
		display.syncExec(new Runnable() {
			public void run() {
				ProofState ps = delta.getProofState();
				if (delta.isDeleted()) {
					if (ps != null) {

						IWorkbenchPage activePage = EventBUIPlugin
								.getActivePage();
						if (activePage.isPartVisible(ProofsPage.this
								.getEditor())) {
							try {
								MessageDialog.openInformation(ProofsPage.this
										.getSite().getShell(), "Out of Date",
										"The Proof Obligation is deleted.");
								userSupport.nextUndischargedPO(true,
										new NullProgressMonitor());
							} catch (RodinDBException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							try {
								userSupport.setCurrentPO(null,
										new NullProgressMonitor());
							} catch (RodinDBException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} else if (delta.isNewProofState()) {
					if (ps != null) { // Reload everything
						initHypothesisSections(ps);
						goalSection.setGoal(ps.getCurrentNode());
					} else {
						initHypothesisSections(null);
						goalSection.setGoal(null);
					}
					ProofsPage.this.getManagedForm().getForm().reflow(true);
				} else {
					IProofTreeNode node = delta.getNewProofTreeNode();
					if (node != null) {
						initHypothesisSections(userSupport.getCurrentPO());
						goalSection.setGoal(node);
					} else if (delta.getNewCache()) {
						initCacheAndSearch();
					} else if (delta.getNewSearch()) {
						initCacheAndSearch();
						Section section = searchedSection.getSection();
						if (!section.isExpanded()) {
							section.setExpanded(true);
						}
					}
					ProofsPage.this.getManagedForm().getForm().reflow(true);
				}

			}
		});
	}

	void initCacheAndSearch() {
		ProofState ps = userSupport.getCurrentPO();
		Set<Hypothesis> selected = new HashSet<Hypothesis>();
		ArrayList<Hypothesis> cached = new ArrayList<Hypothesis>();
		ArrayList<Hypothesis> searched = new ArrayList<Hypothesis>();
		boolean enable = false;
		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				selected = node.getSequent().selectedHypotheses();
				if (node.isOpen())
					enable = true;
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
		cachedSection.init(cached, enable);
		searchedSection.init(searched, enable);

	}

	private boolean flag;

	int[] weights = new int[5];

	public void autoLayout() {
		if (flag)
			return;
		flag = true;
		weights[0] = 0;

		ScrolledForm form = this.getManagedForm().getForm();
		Rectangle original = form.getBody().getBounds();
		// ProverUIUtils.debugProverUI("Original bound " + original);

		if (ProverUIUtils.DEBUG) {
			ProverUIUtils.debug("*********************");
			ProverUIUtils.debug("Client area height "
					+ form.getClientArea().height);
			ProverUIUtils.debug("Client area width "
					+ form.getClientArea().width);
		}
		// -1 in totalHeight to avoid the vertical scrollbar in the beginning???
		int totalHeight = form.getClientArea().height - original.y - 1;
		int totalWidth = form.getClientArea().width;

		ScrollBar horizontal = form.getHorizontalBar();
		ScrollBar vertical = form.getVerticalBar();

		if (horizontal != null && horizontal.isVisible()) {
			totalHeight += horizontal.getSize().y;
			// ProverUIUtils.debugProverUI("Horizontal " +
			// horizontal.getSize());
		}

		if (vertical != null && vertical.isVisible()) {
			totalWidth += vertical.getSize().x;
			// ProverUIUtils.debugProverUI("Vertical " + vertical.getSize());
		}

		// ProverUIUtils.debugProverUI("Total Height: " + totalHeight);
		// ProverUIUtils.debugProverUI("Total Width: " + totalWidth);

		weights[1] = searchedSection.getSection().computeSize(totalWidth,
				SWT.DEFAULT).y;
		weights[2] = cachedSection.getSection().computeSize(totalWidth,
				SWT.DEFAULT).y;

		weights[3] = selectedSection.getSection().computeSize(totalWidth,
				SWT.DEFAULT).y;

		weights[4] = goalSection.getSection().computeSize(totalWidth,
				SWT.DEFAULT).y;

		if (ProverUIUtils.DEBUG) {
			ProverUIUtils.debug("Desired Weight ");
			for (int i = 0; i < 5; i++) {
				ProverUIUtils.debug("weights[" + i + "] is " + weights[i]);
			}
		}
		// for (int i = 0; i < 5; i++) {
		// ProverUIUtils.debugProverUI("Before Height (" + i + "): "
		// + weights[i]);
		// }

		if (totalHeight < 1) {
			totalHeight = DEFAULT_HEIGHT;
			totalWidth = DEFAULT_WIDTH;
		}
//		if (totalHeight < 1) { // Not initialised yet
//			weights[0] = 0;
//			// UIUtils.debugProverUI("Client area: " +
//			// sashForm.getClientArea());
//			sashForm.setWeights(weights);
//			form.reflow(true);
//		} else {
			int sum = 0;
			// Do not resize the goalSection
			for (int i = 1; i < 4; i++) {
				sum += weights[i];
			}

			if (sum < totalHeight - weights[4]) {
				weights[0] = totalHeight - sum - weights[4];
				Rectangle rect = sashForm.computeTrim(0, 0, totalWidth,
						totalHeight);
				if (ProverUIUtils.DEBUG) {
					ProverUIUtils.debug("Total Width " + totalWidth);
					ProverUIUtils.debug("Total Height " + totalHeight);
					for (int i = 0; i < 5; i++) {
						ProverUIUtils.debug("weights[" + i + "] is "
								+ weights[i]);
					}
					ProverUIUtils.debug("Rect: " + rect);
				}
				// ProverUIUtils.debugProverUI("Client area: "
				// + sashForm.getClientArea());
				sashForm.setBounds(rect);
				sashForm.setWeights(weights);
				form.reflow(true);
			} else {
				weights[0] = 0;
				for (int i = 1; i < 4; i++) {
					weights[i] = weights[i] * (totalHeight - weights[4]) / sum;
				}

				// re-adjust according to MINIMUM_SECTION_HEIGHT
				Collection<Integer> fix = new ArrayList<Integer>();
				if (totalHeight - weights[4] - MIN_SECTION_HEIGHT * 3 <= 0) {
					for (int i = 1; i < 4; i++) {
						weights[i] = MIN_SECTION_HEIGHT;
					}
				} else {
					int i = checkWeight();
					while (i != 0 && fix.size() != 3) {
						weights[i] = MIN_SECTION_HEIGHT;
						fix.add(new Integer(i));
						// readjust
						sum = 0;
						for (int j = 1; j < 4; j++) {
							if (!fix.contains(new Integer(j)))
								sum += weights[j];
						}

						for (int j = 1; j < 4; j++) {
							if (!fix.contains(new Integer(j)))
								weights[j] = weights[j]
										* (totalHeight - weights[4] - MIN_SECTION_HEIGHT
												* fix.size()) / sum;
						}

						i = checkWeight();
					}
				}
				Rectangle rect = sashForm.computeTrim(0, 0, totalWidth,
						totalHeight);
				if (ProverUIUtils.DEBUG) {
					ProverUIUtils.debug("Total Width " + totalWidth);
					ProverUIUtils.debug("Total Height " + totalHeight);
					for (int i = 0; i < 5; i++) {
						ProverUIUtils.debug("weights[" + i + "] is "
								+ weights[i]);
					}
					ProverUIUtils.debug("Rect: " + rect);
				}

				sashForm.setBounds(rect);

				// ProverUIUtils.debugProverUI("Client area: "
				// + sashForm.getClientArea());
				selectedSection.getSection().layout();
				searchedSection.getSection().layout();
				cachedSection.getSection().layout();
				goalSection.getSection().layout();
				sashForm.setWeights(weights);
				
				// ProverUIUtils.debugProverUI("form Client area "
				// + form.getClientArea());
				form.reflow(true);
			}
//		}

		flag = false;
		// for (HypothesisRow row : selectedSection.getRows()) {
		// row.createHypothesisText();
		// }
	}

	void internalLayout() {
		ScrolledForm form = this.getManagedForm().getForm();
		Rectangle original = form.getBody().getBounds();
		// ProverUIUtils.debugProverUI("Original bound " + original);

		// -1 in totalHeight to avoid the vertical scrollbar in the beginning???
		int totalHeight = form.getClientArea().height - original.y - 1;
		int totalWidth = form.getClientArea().width;

		ScrollBar horizontal = form.getHorizontalBar();
		ScrollBar vertical = form.getVerticalBar();

		if (horizontal != null && horizontal.isVisible()) {
			totalHeight += horizontal.getSize().y;
			// ProverUIUtils.debugProverUI("Horizontal " +
			// horizontal.getSize());
		}

		if (vertical != null && vertical.isVisible()) {
			totalWidth += vertical.getSize().x;
			// ProverUIUtils.debugProverUI("Vertical " + vertical.getSize());
		}

		Rectangle rect = sashForm.computeTrim(0, 0, totalWidth,
		 totalHeight);
		 sashForm.setBounds(rect);
		
		 // ProverUIUtils.debugProverUI("Client area: "
		 // + sashForm.getClientArea());
		 sashForm.setWeights(weights);
		
		 // ProverUIUtils.debugProverUI("form Client area "
		 // + form.getClientArea());
		 form.reflow(true);
				
	}

	private int checkWeight() {
		for (int i = 1; i < 4; i++) {
			if (weights[i] < MIN_SECTION_HEIGHT)
				return i;
		}
		return 0;
	}
}