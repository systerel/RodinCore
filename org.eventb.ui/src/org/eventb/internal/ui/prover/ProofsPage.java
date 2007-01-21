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
import java.util.Iterator;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.internal.ui.preferences.PreferenceConstants;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This is the implementation of the Proof Page in the Prover UI Editor.
 */
public class ProofsPage extends FormPage implements
		IUserSupportManagerChangedListener, IPropertyChangeListener {

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

	Action layoutAction;

	HypothesesSection searchedSection;

	private SashForm sashForm;

	IUserSupport userSupport;

	Composite body;

	private Composite comp;

	boolean layouting;

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
		EventBPlugin.getDefault().getUserSupportManager().addChangeListener(this);
		layouting = false;
		IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		store.addPropertyChangeListener(this);

	}

	@Override
	public void dispose() {
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(this);
		IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();
		store.removePropertyChangeListener(this);
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

		IProofState ps = userSupport.getCurrentPO();
		initHypothesisSections(ps);

		goalSection = new GoalSection(this, sashForm,
				ExpandableComposite.TITLE_BAR);
		managedForm.addPart(goalSection);

		body.setLayout(new ProofsPageLayout());
		createToolBarActions(managedForm);
	}

	/**
	 * @author htson
	 *         <p>
	 *         A special layout class for the Proofs Page to fit the sections
	 *         into the client area of the page. Depending on the state of the
	 *         auto layout button, the sections are resize to the best display
	 *         or not
	 * 
	 */
	protected class ProofsPageLayout extends Layout {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.widgets.Layout#computeSize(org.eclipse.swt.widgets.Composite,
		 *      int, int, boolean)
		 */
		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint,
				boolean flushCache) {
			ScrolledForm form = ProofsPage.this.getManagedForm().getForm();
			Rectangle bounds = form.getBody().getBounds();

			return new Point(bounds.x, bounds.y);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.widgets.Layout#layout(org.eclipse.swt.widgets.Composite,
		 *      boolean)
		 */
		@Override
		protected void layout(Composite composite, boolean flushCache) {
			if (layouting == true) // Do nothing if already layouting (avoid
				// loop)
				return;
			layouting = true;
			if (layoutAction.isChecked())
				autoLayout();
			else
				scaleLayout();
			layouting = false;
		}

	}

	/**
	 * Create the Toolbar actions.
	 * <p>
	 * 
	 * @param managedForm
	 *            The managed form contains the Toolbar.
	 */
	protected void createToolBarActions(IManagedForm managedForm) {
		final IPreferenceStore store = EventBUIPlugin.getDefault()
				.getPreferenceStore();

		layoutAction = new Action("auto", Action.AS_CHECK_BOX) {
			@Override
			public void run() {
				boolean checked = this.isChecked();
				store.setValue(PreferenceConstants.P_PROOFPAGE_AUTOLAYOUT,
						checked);
				if (checked) {
					ProofsPage.this.body.layout();
				}
			}
		};

		layoutAction.setChecked(store
				.getBoolean(PreferenceConstants.P_PROOFPAGE_AUTOLAYOUT)); // TODO
		// Set
		// from
		// preference
		// page
		layoutAction.setToolTipText("Automatically layout");

		ScrolledForm form = managedForm.getForm();

		form.getToolBarManager().add(layoutAction);
		form.updateToolBar();
	}

	// private updateButtons() {
	//		
	// }

	// private class ProofPageLayout extends Layout {
	//
	// @Override
	// protected Point computeSize(Composite composite, int wHint, int hHint,
	// boolean flushCache) {
	// Point pt = sashForm.getSize();
	// if (sashForm != null)
	// return new Point(pt.x, pt.y);
	//
	// Rectangle rect = ProofsPage.this.getManagedForm().getForm()
	// .getClientArea();
	//
	// Point size = new Point(rect.width, rect.height);
	// return size;
	// }
	//
	// @Override
	// protected void layout(Composite composite, boolean flushCache) {
	// ProofsPage.this.autoLayout();
	// }
	//
	// }

	void initHypothesisSections(IProofState ps) {

		Iterable<Predicate> selected = new ArrayList<Predicate>();
		Collection<Predicate> cached = new ArrayList<Predicate>();
		Collection<Predicate> searched = new ArrayList<Predicate>();

		boolean enable = false;
		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			IProverSequent sequent = node.getSequent();
			if (node != null) {
				selected = sequent.selectedHypIterable();
				if (node.isOpen())
					enable = true;
			}
			Collection<Predicate> currentCached = ps.getCached();
			for (Iterator<Predicate> i = currentCached.iterator(); i.hasNext();) {
				Predicate hyp = i.next();
				if (node != null)
					if (!sequent.containsHypothesis(hyp))
						continue;
				if (!(sequent.isSelected(hyp)))
					cached.add(hyp);
			}
			Collection<Predicate> currentSearched = ps.getSearched();
			for (Iterator<Predicate> i = currentSearched.iterator(); i
					.hasNext();) {
				Predicate hyp = i.next();
				if (node != null)
					if (!sequent.containsHypothesis(hyp))
						continue;
				if (!sequent.isSelected(hyp) && !cached.contains(hyp))
					searched.add(hyp);
			}
		}

		selectedSection.init(selected, enable);
		cachedSection.init(cached, enable);
		searchedSection.init(searched, enable);

	}

	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {
		Display display = Display.getDefault();
		if (this.getManagedForm().getForm().isDisposed())
			return;

		final IUserSupportDelta affectedUserSupport = ProverUIUtils
				.getUserSupportDelta(delta, userSupport);
		
		if (affectedUserSupport == null)
			return;
		
		final int kind = affectedUserSupport.getKind();
		
		if (kind == IUserSupportDelta.REMOVED)
			return;
		
		if (ProverUIUtils.DEBUG)
			ProverUIUtils
					.debug("Proof State Change "
							+ ((ProverUI) getEditor()).getRodinInput()
									.getElementName());
		display.syncExec(new Runnable() {
			public void run() {
				if (kind == IUserSupportDelta.ADDED) {
					IProofState ps = userSupport.getCurrentPO();
					if (ps != null) { // Reload everything
						initHypothesisSections(ps);
						goalSection.setGoal(ps.getCurrentNode());
					} else {
						initHypothesisSections(null);
						goalSection.setGoal(null);
					}
					ProofsPage.this.getManagedForm().getForm().reflow(true);
				}
				else if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();
					if ((flags | IUserSupportDelta.F_CURRENT) != 0) {
						IProofState ps = userSupport.getCurrentPO();
						if (ps != null) { // Reload everything
							initHypothesisSections(ps);
							goalSection.setGoal(ps.getCurrentNode());
						} else {
							initHypothesisSections(null);
							goalSection.setGoal(null);
						}
						ProofsPage.this.getManagedForm().getForm().reflow(true);						
					}
					else if ((flags | IUserSupportDelta.F_STATE) != 0) {
						IProofState proofState = userSupport.getCurrentPO();
						IProofStateDelta affectedProofState = ProverUIUtils.getProofStateDelta(affectedUserSupport, proofState);
						
						if (affectedProofState == null) return;
						
						if (affectedProofState.getKind() == IProofStateDelta.REMOVED) {
							if (proofState != null) {

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
						}
						else if (affectedProofState.getKind() == IProofStateDelta.REMOVED) {
							return;
						}
						else if (affectedProofState.getKind() == IProofStateDelta.CHANGED) {
							int psFlags = affectedProofState.getFlags();
							if ((psFlags | IProofStateDelta.F_NODE) != 0) {
								initHypothesisSections(proofState);
								goalSection.setGoal(proofState.getCurrentNode());
							}
							if ((psFlags | IProofStateDelta.F_CACHE) != 0) {
								initCacheAndSearch();								
							}
							if ((psFlags | IProofStateDelta.F_SEARCH) != 0) {
								initCacheAndSearch();								
								Section section = searchedSection.getSection();
								if (!section.isExpanded()) {
									section.setExpanded(true);
								}
							}
							ProofsPage.this.getManagedForm().getForm().reflow(true);							
						}
					}
				}
			}
		});
	}

	void initCacheAndSearch() {
		IProofState ps = userSupport.getCurrentPO();
		ArrayList<Predicate> cached = new ArrayList<Predicate>();
		ArrayList<Predicate> searched = new ArrayList<Predicate>();
		boolean enable = false;
		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			IProverSequent sequent = node.getSequent();
			if (node != null) {
				if (node.isOpen())
					enable = true;
			}
			Collection<Predicate> currentCached = ps.getCached();
			for (Iterator<Predicate> i = currentCached.iterator(); i.hasNext();) {
				Predicate hyp = i.next();
				if (node != null)
					if (!sequent.containsHypothesis(hyp))
						continue;
				if (!sequent.isSelected(hyp))
					cached.add(hyp);
			}

			Collection<Predicate> currentSearched = ps.getSearched();
			for (Iterator<Predicate> i = currentSearched.iterator(); i
					.hasNext();) {
				Predicate hyp = i.next();
				if (node != null)
					if (!sequent.containsHypothesis(hyp))
						continue;
				if (!sequent.isSelected(hyp) && !cached.contains(hyp))
					searched.add(hyp);
			}
		}
		cachedSection.init(cached, enable);
		searchedSection.init(searched, enable);

	}

	// private boolean flag;

	int[] weights = new int[5];

	void autoLayout() {
		// if (flag)
		// return;
		// flag = true;
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
		// if (totalHeight < 1) { // Not initialised yet
		// weights[0] = 0;
		// // UIUtils.debugProverUI("Client area: " +
		// // sashForm.getClientArea());
		// sashForm.setWeights(weights);
		// form.reflow(true);
		// } else {
		int sum = 0;
		// Do not resize the goalSection
		for (int i = 1; i < 4; i++) {
			sum += weights[i];
		}

		if (sum < totalHeight - weights[4]) {
			weights[0] = totalHeight - sum - weights[4];
			Rectangle rect = sashForm
					.computeTrim(0, 0, totalWidth, totalHeight);
			if (ProverUIUtils.DEBUG) {
				ProverUIUtils.debug("Total Width " + totalWidth);
				ProverUIUtils.debug("Total Height " + totalHeight);
				for (int i = 0; i < 5; i++) {
					ProverUIUtils.debug("weights[" + i + "] is " + weights[i]);
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
			Rectangle rect = sashForm
					.computeTrim(0, 0, totalWidth, totalHeight);
			if (ProverUIUtils.DEBUG) {
				ProverUIUtils.debug("Total Width " + totalWidth);
				ProverUIUtils.debug("Total Height " + totalHeight);
				for (int i = 0; i < 5; i++) {
					ProverUIUtils.debug("weights[" + i + "] is " + weights[i]);
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
			// form.reflow(true);
		}
		// }

		// flag = false;
		// for (HypothesisRow row : selectedSection.getRows()) {
		// row.createHypothesisText();
		// }
	}

	void scaleLayout() {
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

		Rectangle rect = sashForm.computeTrim(0, 0, totalWidth, totalHeight);
		sashForm.setBounds(rect);

		// ProverUIUtils.debugProverUI("Client area: "
		// + sashForm.getClientArea());
		// sashForm.setWeights(weights);

		// ProverUIUtils.debugProverUI("form Client area "
		// + form.getClientArea());
		// form.reflow(true);

	}

	private int checkWeight() {
		for (int i = 1; i < 4; i++) {
			if (weights[i] < MIN_SECTION_HEIGHT)
				return i;
		}
		return 0;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(
				PreferenceConstants.P_PROOFPAGE_AUTOLAYOUT)) {
			Object newValue = event.getNewValue();
			assert newValue instanceof Boolean || newValue instanceof String;
			if (newValue instanceof String)
				layoutAction.setChecked(((String) newValue)
						.compareToIgnoreCase("true") == 0);
			else {
				layoutAction.setChecked((Boolean) newValue);
			}
			// Run layout action
			layoutAction.run();
		}

	}
}