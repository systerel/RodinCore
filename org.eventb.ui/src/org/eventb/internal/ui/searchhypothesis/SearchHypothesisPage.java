/*******************************************************************************
 * Copyright (c) 2005-2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.searchhypothesis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.HypothesisRow;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Proof Control 'page'.
 */
public class SearchHypothesisPage extends Page implements
		ISearchHypothesisPage, IUserSupportManagerChangedListener {

	ProverUI editor;

	ScrolledForm scrolledForm;

	protected Collection<HypothesisRow> rows;

	private FormToolkit toolkit;

	/**
	 * Constructor
	 * <p>
	 * 
	 * @param editor
	 *            the Prover UI editor associated with this Proof Control page.
	 */
	public SearchHypothesisPage(ProverUI editor) {
		this.editor = editor;
		rows = new ArrayList<HypothesisRow>();
		EventBPlugin.getDefault().getUserSupportManager().addChangeListener(
				this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	@Override
	public void dispose() {
		// Deregister with the UserSupport
		EventBPlugin.getDefault().getUserSupportManager().removeChangeListener(
				this);
		super.dispose();
	}

	/**
	 * Helper function to create tool item
	 * 
	 * @param parent
	 *            the parent toolbar
	 * @param type
	 *            the type of tool item to create
	 * @param text
	 *            the text to display on the tool item
	 * @param image
	 *            the image to display on the tool item
	 * @param hotImage
	 *            the hot image to display on the tool item
	 * @param toolTipText
	 *            the tool tip text for the tool item
	 * @return ToolItem
	 */

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * 
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());

		scrolledForm = toolkit.createScrolledForm(parent);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrolledForm.setLayoutData(gd);

		Composite comp = scrolledForm.getBody();
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.verticalSpacing = 0;
		comp.setLayout(layout);

		// createTextClient(section, toolkit);
		// updateTextClientStatus();
//		hookContextMenu();
		contributeToActionBars();

	}

	void init() {
		IProofState ps = editor.getUserSupport().getCurrentPO();

		Iterable<Predicate> selected = new ArrayList<Predicate>();
		Collection<Predicate> cached = new ArrayList<Predicate>();
		Collection<Predicate> searched = new ArrayList<Predicate>();

		boolean enable = false;
		if (ps != null) {
			IProofTreeNode node = ps.getCurrentNode();
			if (node != null) {
				selected = node.getSequent().selectedHypIterable();
				if (node.isOpen())
					enable = true;
			}
			cached = ps.getCached();

			searched = ps.getSearched();
		}

		init(searched, selected, cached, enable);
		// selectedSection.init(selected, enable);
		// cachedSection.init(cached, enable);
		// searchedSection.init(searched, enable);
	}

	private void init(Collection<Predicate> hyps, Iterable<Predicate> selected,
			Collection<Predicate> cached, boolean enable) {
		// Remove everything
		for (HypothesisRow row : rows) {
			row.dispose();
		}
		rows.clear();

		// Add new hyps
		int i = 0;
		for (Predicate hyp : hyps) {
			// UIUtils.debugEventBEditor("Add to " + this.title + " hyp: "
			// + hyp.getPredicate());
			HypothesisRow row = new HypothesisRow(toolkit, scrolledForm
					.getBody(), hyp, editor.getUserSupport(), (i % 2) == 0,
					enable);
			rows.add(row);
			i++;
		}

		// updateTextClientStatus();
		scrolledForm.reflow(true);
	}

	/**
	 * Setup the action bars
	 */
	private void contributeToActionBars() {
		IActionBars bars = getSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	/**
	 * Fill the local pull down.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(new Separator());
	}

	/**
	 * Fill the context menu.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
	void fillContextMenu(IMenuManager manager) {
		// manager.add(expertMode);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Fill the local toolbar.
	 * <p>
	 * 
	 * @param manager
	 *            the toolbar manager
	 */
	private void fillLocalToolBar(IToolBarManager manager) {
		// Do nothing
	}

	/**
	 * Passing the focus request to the button bar.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	@Override
	public void setFocus() {
		scrolledForm.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		return scrolledForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofStateChangedListener#proofStateChanged(org.eventb.core.pm.IProofStateDelta)
	 */
	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {

		if (scrolledForm.isDisposed())
			return;

		final IUserSupport userSupport = editor.getUserSupport();

		final IUserSupportDelta affectedUserSupport = ProverUIUtils
				.getUserSupportDelta(delta, userSupport);

		if (affectedUserSupport == null)
			return;

		final int kind = affectedUserSupport.getKind();
		if (kind == IUserSupportDelta.REMOVED)
			return;

		Display display = Display.getDefault();

		display.syncExec(new Runnable() {
			public void run() {
				if (kind == IUserSupportDelta.ADDED) {
					init();
					scrolledForm.reflow(true);
				} else if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();
					IProofState ps = userSupport.getCurrentPO();
					if ((flags & IUserSupportDelta.F_CURRENT) != 0) {
						init();
						scrolledForm.reflow(true);
					} else if ((flags & IUserSupportDelta.F_STATE) != 0) {
						IProofStateDelta affectedProofState = ProverUIUtils
								.getProofStateDelta(affectedUserSupport, ps);
						int psKind = affectedProofState.getKind();
						if (psKind == IProofStateDelta.ADDED) {
							init();
							scrolledForm.reflow(true);
						} else if (psKind == IProofStateDelta.REMOVED) {
							if (ps != null) {
								IWorkbenchPage activePage = EventBUIPlugin
										.getActivePage();
								if (activePage.isPartVisible(editor)) {
									try {
										MessageDialog
												.openInformation(
														SearchHypothesisPage.this
																.getSite()
																.getShell(),
														"Out of Date",
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
						} else if (psKind == IProofStateDelta.CHANGED) {
							init();
							scrolledForm.reflow(true);
						}
					}
				}
			}
		});

	}

	public IUserSupport getUserSupport() {
		return editor.getUserSupport();
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
