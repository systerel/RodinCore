/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Added a constant for the user support manager
 *     Systerel - used EventBSharedColor
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.searchhypothesis.SearchHypothesisComposite;
import org.eventb.internal.ui.searchhypothesis.SearchHypothesisUtils;
import org.rodinp.keyboard.preferences.PreferenceConstants;

/**
 * @author htson
 *         <p>
 *         This is the abstract implementation of a hypothesis composite which
 *         can be used in different Hypothesis Page. The composite consist of a
 *         cool bar in the top and a set of hypothesis rows wrapped inside a
 *         scrolled form.
 *         </p>
 *         <p>
 *         When creating a hypothesis composite, client needs to specify the
 *         proof state change flags that the composite need to respond to.
 *         </p>
 *         <p>
 *         Clients need to implements method for creating different tool bar
 *         items {@link #createItems(ToolBar)}, update the status of these items
 *         when there are some changes occur {@link #updateToolbarItems()}.
 *         </p>
 *         <p>
 *         Moreover, clients need to implement method
 *         {@link #getHypotheses(IProofState)} to return the list of hypotheses
 *         that will be displayed within this hypothesis composite.
 *         </p>
 * @see IProofStateDelta#F_NODE
 * @see IProofStateDelta#F_PROOFTREE
 * @see IProofStateDelta#F_CACHE
 * @see IProofStateDelta#F_SEARCH
 */
public abstract class HypothesisComposite implements
		IUserSupportManagerChangedListener, SelectionListener,
		IPropertyChangeListener {

	private static final IUserSupportManager USM = EventBPlugin
			.getUserSupportManager();

	
	private static final int NB_TABS_LEFT = 3; // Tabs
	private static final int NB_CONTROLS = 2; // number of buttons on the left

	// The User Support associated with this Hypothesis Composite.
	private final IUserSupport userSupport;

	// The main scrolled form.
	private StyledText styledText;
	
	private TacticHyperlinkManager manager;

	// The collection of hypothesis rows.
	private final List<PredicateRow> rows = new ArrayList<PredicateRow>();

	// The top-level composite control of this hypothesis composite.
	private Composite control;

	// The main prover editor associated with this Hypothesis composite.
	private final ProverUI proverUI;

	private final int flags;

	private ScrolledComposite sc;


	private Font font;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this Hypothesis Page. This
	 *            value must not be <code>null</code>.
	 * @param flags
	 *            the IProofStateDelta flags that this page should respond to
	 *            when changes occur in the proof state. For example,
	 *            <code>IProofStateDelta.F_NODE | IProofStateDelta.F_SEARCH</code>
	 *            specify that the page is refresh when the current node or the
	 *            search hypothesis has been changed,
	 * @param proverUI
	 *            the main prover editor associated with this Hypothesis
	 *            Composite. This value must not be <code>null</code>.
	 */
	public HypothesisComposite(IUserSupport userSupport, int flags,
			ProverUI proverUI) {
		Assert.isNotNull(userSupport, "The User Support must not be null"); // $NON-NLS-1$
		Assert.isNotNull(proverUI, "The main prover editor must not be null"); // $NON-NLS-1$
		this.userSupport = userSupport;
		this.proverUI = proverUI;
		this.flags = flags;
	}

	/**
	 * Dispose the hypothesis composite by disposing the hypothesis rows.
	 */
	public void dispose() {
		// Disconnect from the user support manager.
		USM.removeChangeListener(this);

		JFaceResources.getFontRegistry().removeListener(this);
		
		totalClearance();
	}
	
	/**
	 * Create the control of the hypothesis composite. This should be called
	 * after the constructor.
	 * 
	 * @param parent
	 *            the composite parent of the hypothesis composite.
	 */
	public void createControl(Composite parent) {

		// Create the top-level composite.
		control = new Composite(parent, SWT.NULL);
		if (ProverUIUtils.DEBUG) {
			control.setBackground(EventBSharedColor	//private static final int LINE_SPACING = 3; // px
					.getSystemColor(SWT.COLOR_DARK_GRAY));
		}
		// Set the layout of the top-level control to a form layout.
		control.setLayout(new FormLayout());

		// Create the top cool bar.
		final CoolBar buttonBar = createTopCoolBar(control);

		// Create a dummy toolbar, if not then the cool bar is not displayed.
		createDummyToolbar(buttonBar);

		// Set the layout data for the top cool bar
		setLayoutData(buttonBar);
		
		// Creates a scrolledComposite to hold the styledText.
		sc = new ScrolledComposite(control, SWT.H_SCROLL | SWT.V_SCROLL);
		setScrolledLayout(sc, buttonBar);
		
		font = JFaceResources
				.getFont(PreferenceConstants.RODIN_MATH_FONT);
		JFaceResources.getFontRegistry().addListener(this);

		// Refresh to create to fill out the content of the scrolled form.
		refresh();
		USM.addChangeListener(this);
	}

	private CoolBar createTopCoolBar(Composite parent) {
		final CoolBar buttonBar = new CoolBar(parent, SWT.FLAT);
		final ToolBar toolBar = new ToolBar(buttonBar, SWT.FLAT);
		createItems(toolBar);
		final CoolItem item = new CoolItem(buttonBar, SWT.NONE);
		item.setControl(toolBar);
		toolBar.pack();
		final Point size = toolBar.getSize();
		final Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);
		return buttonBar;
	}

	private static void createDummyToolbar(final CoolBar buttonBar) {
		final ToolBar dummyBar = new ToolBar(buttonBar, SWT.FLAT);
		dummyBar.pack();
		final Point size2 = dummyBar.getSize();
		final CoolItem dummyItem = new CoolItem(buttonBar, SWT.NONE);
		dummyItem.setControl(dummyBar);
		final Point preferred2 = dummyItem.computeSize(size2.x, size2.y);
		dummyItem.setPreferredSize(preferred2);
	}
	
	private static void setLayoutData(CoolBar buttonBar) {
		final FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		buttonBar.setLayoutData(coolData);
	}

	private static void setScrolledLayout(ScrolledComposite scComp, CoolBar buttonBar) {
		scComp.setLayout(new GridLayout(1, false));
		final FormData scrolledData = new FormData();
		scrolledData.left = new FormAttachment(0);
		scrolledData.right = new FormAttachment(100);
		scrolledData.top = new FormAttachment(buttonBar);
		scrolledData.bottom = new FormAttachment(100);
		scComp.setLayoutData(scrolledData);
	}

	/**
	 * Abstract method for create different items within the top tool bar.
	 * 
	 * @param toolBar
	 *            the parent tool bar where different items to be created.
	 */
	public abstract void createItems(ToolBar toolBar);

	/**
	 * Clients implement this method to Update the status of the different tool
	 * bar items.
	 */
	public abstract void updateToolbarItems();

	/**
	 * Utility method for getting the prover sequent associated with a proof
	 * state.
	 * 
	 * @param ps
	 *            a proof state.
	 * @return the prover sequent at the current node of the proof state. Return
	 *         <code>null</code> if the proof state itself is <code>null</code>
	 *         or the current node within the proof state is <code>null</code>.
	 */
	private IProverSequent getProverSequent(IProofState ps) {
		IProverSequent sequent = null;
		if (ps != null) {
			final IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				sequent = node.getSequent();
			}
		}
		return sequent;
	}

	/**
	 * Utility method to check if the hypothesis should be enable for a proof
	 * state.
	 * 
	 * @param ps
	 *            a proof state
	 * @return <code>true</code> if the current node of the proof state is
	 *         opened. Return <code>false</code> if the proof state is
	 *         <code>null</code> or the current node of the proof state is
	 *         <code>null</code> or the current node of the proof state is
	 *         closed.
	 */
	private boolean isEnabled(IProofState ps) {
		boolean enabled = false;
		if (ps != null) {
			final IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				if (node.isOpen())
					enabled = true;
			}
		}
		return enabled;
	}

	/**
	 * Refresh the content of the hypothesis composite by recreating the
	 * hypothesis rows. This must be called within the UI Threads.
	 */
	protected void refresh() {
		final boolean traced = SearchHypothesisUtils.DEBUG
				&& (this instanceof SearchHypothesisComposite);
		long start = 0;
		if (traced) {
			SearchHypothesisUtils.debug("Start refreshing view");
			start = System.currentTimeMillis();
		}

		final IProofState ps = userSupport.getCurrentPO();
		final IProverSequent sequent = getProverSequent(ps);
		final Iterable<Predicate> hyps = getHypotheses(ps);
		final boolean enabled = isEnabled(ps);
		reinitialise(hyps, sequent, enabled);
		if (traced) {
			final long elapsed = System.currentTimeMillis() - start;
			SearchHypothesisUtils.debug("Refreshing view took " + elapsed
					+ " ms.");
		}
	}

	/**
	 * Abstract method to get the list of hypotheses. Clients implement this
	 * method to return the appropriate hypotheses which will be displayed, e.g.
	 * Searched Hypotheses or Cached Hypotheses.
	 * 
	 * @param ps
	 *            a proof state
	 * @return a collection of hypotheses as an {@link Iterable}.
	 */
	public abstract Iterable<Predicate> getHypotheses(IProofState ps);

	/**
	 * Utility method for initialising the hypothesis rows with the given
	 * collection of hypothesis, the prover sequent. Also specify if the
	 * hypothesis rows should be enable or not.
	 * 
	 * @param hyps
	 *            a collection of hypotheses.
	 * @param sequent
	 *            a prover sequent
	 * @param enabled
	 *            <code>true</code> if the hypothesis rows should be enabled.
	 */
	private void reinitialise(Iterable<Predicate> hyps, IProverSequent sequent,
			boolean enabled) {

		final boolean traced = ProverUIUtils.DEBUG;
		long start = 0;
		if (traced) {
			start = System.currentTimeMillis();
		}
		if (styledText != null) {
			styledText.dispose();
		}
		totalClearance();
		initStyledTextAndManager();
		assert styledText != null;
		styledText.setRedraw(false);
		
		if (traced) {
			final long elapsed = System.currentTimeMillis() - start;
			ProverUIUtils.debug("Clearing rows and text took " + elapsed
					+ " ms.");
			start = System.currentTimeMillis();
		}

		// Recreating the hypothesis rows according to the input.
		for (Predicate hyp : hyps) {
			final PredicateRow row = new PredicateRow(NB_TABS_LEFT, hyp, false,
					userSupport, enabled, this, proverUI, manager);
			rows.add(row);
		}
		
		if (traced) {
			final long elapsed = System.currentTimeMillis() - start;
			SearchHypothesisUtils.debug("Creating rows took " + elapsed + " ms.");
			start = System.currentTimeMillis();
		}
		
		final String indentation = ProverUIUtils.getControlSpacing(NB_CONTROLS,
				NB_TABS_LEFT - NB_CONTROLS);
		int i = 0;
		for (PredicateRow row : rows) {
			manager.appendText(indentation);
			row.append(i%2!=0);
			i++;
		}
		manager.setContents();
		manager.activateBackgroundColoration();
		for (PredicateRow row : rows) {
			row.attachButtons();
		}
		
		if (traced) {
			final long elapsed = System.currentTimeMillis() - start;
			SearchHypothesisUtils.debug("painting styledText took " + elapsed + " ms.");
			start = System.currentTimeMillis();
		}

		// update the status of the tool bar items.
		updateToolbarItems();
		sc.setMinSize(styledText.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		manager.enableListeners(enabled);

		styledText.setRedraw(true);
		if (traced) {
			final long elapsed = System.currentTimeMillis() - start;
			SearchHypothesisUtils.debug("reflow + toolbars took " + elapsed
					+ " ms.");
		}
	}

	private void totalClearance() {
		if (styledText == null) {
			return;
		}
		for (PredicateRow row : rows) {
			row.dispose();
		}
		rows.clear();
		if (manager != null)
			manager.dispose();
	}

	private void initStyledTextAndManager(){
		// Create the styled text below the cool bar
		styledText = new StyledText(sc, SWT.NONE);
		sc.setContent(styledText);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		styledText.setFont(font);
		styledText.setEditable(false);
		manager = new TacticHyperlinkManager(styledText);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eventb.core.pm.IProofStateChangedListener#proofStateChanged(org.eventb
	 * .core.pm.IProofStateDelta)
	 */
	@Override
	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {

		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Begin User Support Manager Changed"); // $NON-NLS-1$

		// Do nothing if the top-level control is already disposed.
		if (control.isDisposed())
			return;

		// Trying to get the changes for the current user support.
		final IUserSupportDelta affectedUserSupport = ProverUIUtils
				.getUserSupportDelta(delta, userSupport);

		// Do nothing if there is no change for this current user support.
		if (affectedUserSupport == null)
			return;

		// If the user support has been removed, do nothing. This will be handle
		// by the main proof editor.
		final int kind = affectedUserSupport.getKind();
		if (kind == IUserSupportDelta.REMOVED) {
			return; // Do nothing
		}

		// This case should NOT happened.
		if (kind == IUserSupportDelta.ADDED) {
			// Should not happen because the user support should exist before
			// creating this.
			if (ProverUIUtils.DEBUG)
				ProverUIUtils
						.debug("Error: Delta said that the user Support is added"); // $NON-NLS-1$
			return; // Do nothing
		}

		final boolean needRefresh = needRefresh(affectedUserSupport);

		if (needRefresh && styledText != null) {
			
			final Display display = styledText.getDisplay();
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					if (!getControl().isDisposed())
						refresh();
				}
			});
		}

		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("End User Support Manager Changed"); // $NON-NLS-1$
	}

	private boolean needRefresh(IUserSupportDelta affectedUserSupport) {
		final int kind = affectedUserSupport.getKind();
		
		// Handle the case where the user support has changed.
		if (kind != IUserSupportDelta.CHANGED) {
			return false;
		}
		final int usFlags = affectedUserSupport.getFlags();
		if ((usFlags & IUserSupportDelta.F_CURRENT) != 0) {
			// The current proof state is changed, reinitialise the
			// view.
			return true;
		}
		if ((usFlags & IUserSupportDelta.F_STATE) == 0) {
			return false;
		}
		// If the changes occurs in some proof states.
		final IProofState proofState = userSupport.getCurrentPO();
		// Trying to get the change for the current proof state.
		final IProofStateDelta affectedProofState = ProverUIUtils
				.getProofStateDelta(affectedUserSupport, proofState);
		if (affectedProofState == null) {
			return false;
		}
		// If there are some changes
		final int psKind = affectedProofState.getKind();
		switch (psKind) {
		case IProofStateDelta.ADDED:
			// This case should not happened since the proof state
			// must exist before creating this.
			if (ProverUIUtils.DEBUG)
				ProverUIUtils
						.debug("Error: Delta said that the proof state is added"); // $NON-NLS-1$
			return false; // Do nothing
		case IProofStateDelta.REMOVED:
			// Do nothing in this case, this will be handled
			// by the main proof editor.
			return false;
		case IProofStateDelta.CHANGED:
			// If there are some changes to the proof state.
			final int psFlags = affectedProofState.getFlags();
			if ((psFlags & flags) != 0) {
				// Update the view if the corresponding flag
				// has been changed
				return true;
			}
		default:
			return false;
		}
	}

	/**
	 * Pass the focus to the scrolled form.
	 */
	public void setFocus() {
		styledText.setFocus();
	}

	/**
	 * Get the top-level control.
	 * 
	 * @return the top-level control
	 */
	public Control getControl() {
		return control;
	}

	/**
	 * Get the user support associated with the hypothesis composite.
	 * 
	 * @return the user support associated with the hypothesis composite.
	 */
	public IUserSupport getUserSupport() {
		return userSupport;
	}

	/**
	 * Get the set of current selected hypotheses.
	 * 
	 * @return the set of selected hypotheses.
	 */
	public Set<Predicate> getSelectedHyps() {
		Set<Predicate> selected = new HashSet<Predicate>();
		for (PredicateRow hr : rows) {
			if (hr.isSelected()) {
				selected.add(hr.getPredicate());
			}
		}
		return selected;
	}

	/**
	 * Inverse the current selection.
	 */
	public void inverseSelectedHyps() {
		for (PredicateRow hr : rows) {
			hr.setSelected(!hr.isSelected());
		}
		updateToolbarItems();
	}

	/**
	 * De-select all hypotheses. 
	 */
	public void deselectAllHyps() {
		for (PredicateRow hr : rows) {
			if (hr.isSelected())
				hr.setSelected(false);
		}
		updateToolbarItems();
	}

	/**
	 * Select all hypotheses.
	 */
	public void selectAllHyps() {
		for (PredicateRow hr : rows) {
			if (!hr.isSelected())
				hr.setSelected(true);
		}
		updateToolbarItems();
	}

	/**
	 * Scroll to the bottom of the list of hypothesis rows. This is implemented
	 * directly by calculating the uppermost line to be shown.
	 */
	public void scrollToBottom() {
		if (!rows.isEmpty()) {
			final int lastLineIndex = styledText.getLineCount() - 1;
			final Rectangle area = control.getClientArea();
			final int avgLineHeight = styledText.getLineHeight();
			final int avgLinesUp = area.height / avgLineHeight;

			int cumulatedSize = 0;
			int i = lastLineIndex;
			final int offsetAtLine = styledText.getOffsetAtLine(i);
			for (i = lastLineIndex; i > lastLineIndex - avgLinesUp; i--) {
				cumulatedSize += styledText.getLineHeight(offsetAtLine);
				if (cumulatedSize >= area.height) {
					break;
				}
			}
			final Point lineLoc = styledText.getLocationAtOffset(offsetAtLine);
			sc.setOrigin(lineLoc);
		}
	}
	
	

	/**
	 * Set the size of the top-level control.
	 * 
	 * @param width
	 *            the desired width for the top-level control.
	 * @param height
	 *            the desired height for the top-level control.
	 */
	public void setSize(int width, int height) {
		control.setSize(width, height);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
	 * .jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(PreferenceConstants.RODIN_MATH_FONT)) {
			font = JFaceResources
					.getFont(PreferenceConstants.RODIN_MATH_FONT);
			styledText.setFont(font);
			styledText.pack();
		}
	}

}