/*******************************************************************************
 * Copyright (c) 2007 ETH Zurich.
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
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
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
import org.eventb.internal.ui.HypothesisRow;
import org.eventb.ui.EventBUIPlugin;

/**
 * @author htson
 *         <p>
 *         The composite consist of a cool bar in the top and a set of
 *         hypothesis rows wrapped in a scrolled composite
 */
public abstract class HypothesisComposite implements
		IUserSupportManagerChangedListener, SelectionListener {

	IUserSupport userSupport;

	ScrolledForm scrolledForm;

	protected Collection<HypothesisRow> rows;

	private FormToolkit toolkit;

	private Composite control;

	int flags;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this Hypothesis Page.
	 * @param flags
	 *            the IProofStateDelta flags that this page should respond to
	 *            when changes occur in the proof state. For example,
	 *            <code>IProofStateDelta.F_NODE | IProofStateDelta.F_SEARCH</code>
	 *            specify that the page is refresh when the current node or the
	 *            search hypothesis has been changed
	 */
	public HypothesisComposite(IUserSupport userSupport, int flags) {
		this.userSupport = userSupport;
		this.flags = flags;
		rows = new ArrayList<HypothesisRow>();
		EventBPlugin.getDefault().getUserSupportManager().addChangeListener(
				this);
	}

	public void dispose() {
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(
				this);
		for (HypothesisRow row : rows) {
			row.dispose();
		}
	}
	
	public void createControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());

		control = toolkit.createComposite(parent, SWT.NULL);

		CoolBar buttonBar = new CoolBar(control, SWT.FLAT);
		ToolBar toolBar = new ToolBar(buttonBar, SWT.FLAT);
		createItems(toolBar);
		toolBar.pack();
		Point size = toolBar.getSize();
		CoolItem item = new CoolItem(buttonBar, SWT.NONE);
		item.setControl(toolBar);
		Point preferred = item.computeSize(size.x, size.y);
		item.setPreferredSize(preferred);

		// Creat a dummy coolbar, if not then the cool bar is not displayed
		// Very strange requirement for creating a cool bar.
		ToolBar dummyBar = new ToolBar(buttonBar, SWT.FLAT);
		dummyBar.pack();
		size = dummyBar.getSize();
		CoolItem dummyItem = new CoolItem(buttonBar, SWT.NONE);
		dummyItem.setControl(dummyBar);
		preferred = dummyItem.computeSize(size.x, size.y);
		dummyItem.setPreferredSize(preferred);

		control.setLayout(new FormLayout());
		FormData coolData = new FormData();
		coolData.left = new FormAttachment(0);
		coolData.right = new FormAttachment(100);
		coolData.top = new FormAttachment(0);
		buttonBar.setLayoutData(coolData);

		scrolledForm = toolkit.createScrolledForm(control);
		FormData scrolledData = new FormData();
		scrolledData.left = new FormAttachment(0);
		scrolledData.right = new FormAttachment(100);
		scrolledData.top = new FormAttachment(buttonBar);
		scrolledData.bottom = new FormAttachment(100);
		scrolledForm.setLayoutData(scrolledData);

		Composite comp = scrolledForm.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 0;
		comp.setLayout(layout);

	}

	public abstract void createItems(ToolBar toolBar);

	public abstract void updateToolbarItems();

	private IProverSequent getProverSequent(IProofState ps) {
		IProverSequent sequent = null;
		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				sequent = node.getSequent();
			}
		}
		return sequent;
	}

	private boolean isEnable(IProofState ps) {
		boolean enable = false;
		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				if (node.isOpen())
					enable = true;
			}
		}
		return enable;
	}

	void init() {
		IProofState ps = userSupport.getCurrentPO();

		IProverSequent sequent = getProverSequent(ps);
		Collection<Predicate> hyps = getHypotheses(ps);
		boolean enable = isEnable(ps);
		init(hyps, sequent, enable);
	}

	public abstract Collection<Predicate> getHypotheses(IProofState ps);

	private void init(Collection<Predicate> hyps, IProverSequent sequent,
			boolean enable) {
		// Remove everything
		for (HypothesisRow row : rows) {
			row.dispose();

		}
		rows.clear();

		int i = 0;
		for (Predicate hyp : hyps) {
			HypothesisRow row = new HypothesisRow(toolkit, scrolledForm
					.getBody(), hyp, userSupport, sequent.isSelected(hyp),
					enable, this);
			rows.add(row);
			i++;
		}

		scrolledForm.reflow(true);
		updateToolbarItems();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofStateChangedListener#proofStateChanged(org.eventb.core.pm.IProofStateDelta)
	 */
	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {

		// Do nothing if the form is disposed.
		if (scrolledForm.isDisposed())
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
			if (ProverUIUtils.DEBUG)
				ProverUIUtils
						.debug("Error: Delta said that the user Support is added");
			return; // Do nothing
		}

		Display display = EventBUIPlugin.getDefault().getWorkbench()
				.getDisplay();

		display.syncExec(new Runnable() {
			public void run() {
				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int usFlags = affectedUserSupport.getFlags();
					if ((usFlags & IUserSupportDelta.F_CURRENT) != 0) {
						// The current proof state is changed, reinitialise the
						// view.
						init();
						scrolledForm.reflow(true);
						return;
					}
					if ((usFlags & IUserSupportDelta.F_STATE) != 0) {
						// If the changes occurs in some proof states.	
						IProofState proofState = userSupport.getCurrentPO();
						// Trying to get the change for the current proof state. 
						final IProofStateDelta affectedProofState = ProverUIUtils
								.getProofStateDelta(affectedUserSupport,
										proofState);
						if (affectedProofState != null) {
							// If there are some changes
							int psKind = affectedProofState.getKind();
							if (psKind == IProofStateDelta.ADDED) {
								// This case should not happened
								if (ProverUIUtils.DEBUG)
									ProverUIUtils
											.debug("Error: Delta said that the proof state is added");
								return;
							}

							if (psKind == IProofStateDelta.REMOVED) {
								// Do nothing in this case, this will be handled
								// by the main proof editor.
								return;
							}
							
							if (psKind == IProofStateDelta.CHANGED) {
								// If there are some changes to the proof state.
								int psFlags = affectedProofState.getFlags();
								if ((psFlags & flags) != 0) {
									// Update the view if the corresponding flag
									// has been changed
									init();
									scrolledForm.reflow(true);
									return;
								}

							}
						}
					}
				}
			}
		});

	}

	public void setFocus() {
		scrolledForm.setFocus();
	}

	public Control getControl() {
		return control;
	}

	public IUserSupport getUserSupport() {
		return userSupport;
	}

	public Set<Predicate> getSelectedHyps() {
		Set<Predicate> selected = new HashSet<Predicate>();
		for (HypothesisRow hr : rows) {
			if (hr.isSelected()) {
				selected.add(hr.getHypothesis());
			}
		}
		return selected;
	}
}
