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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.Page;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateChangedListener;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.HypothesisRow;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.EventBUIPlugin;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Proof Control 'page'.
 */
public class SearchHypothesisPage extends Page implements
		ISearchHypothesisPage, IProofStateChangedListener {

	private ProverUI editor;

	private ScrolledForm scrolledForm;

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
		editor.getUserSupport().addStateChangedListeners(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	@Override
	public void dispose() {
		// Deregister with the UserSupport
		editor.getUserSupport().removeStateChangedListeners(this);
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

	}

	private void init() {
		IProofState ps = editor.getUserSupport().getCurrentPO();

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
			cached = ps.getCached();
			
			searched = ps.getSearched();
		}

		init(searched, selected, cached, enable);
		// selectedSection.init(selected, enable);
		// cachedSection.init(cached, enable);
		// searchedSection.init(searched, enable);
	}

	private void init(Collection<Hypothesis> hyps,
			Collection<Hypothesis> selected, Collection<Hypothesis> cached,
			boolean enable) {
		// Remove everything
		for (HypothesisRow row : rows) {
			row.dispose();
		}
		rows.clear();

		// Add new hyps
		int i = 0;
		for (Hypothesis hyp : hyps) {
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
	 * Setup the context menu.
	 */
//	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu");
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				SearchHypothesisPage.this.fillContextMenu(manager);
//			}
//		});
//		Menu menu = menuMgr.createContextMenu(this.getControl());
//		this.getControl().setMenu(menu);
//	}

	/**
	 * Setup the action bars
	 */
//	private void contributeToActionBars() {
//		IActionBars bars = getSite().getActionBars();
//		fillLocalPullDown(bars.getMenuManager());
//		fillLocalToolBar(bars.getToolBarManager());
//	}

	/**
	 * Fill the local pull down.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
//	private void fillLocalPullDown(IMenuManager manager) {
//		// manager.add(expertMode);
//		manager.add(new Separator());
//	}

	/**
	 * Fill the context menu.
	 * <p>
	 * 
	 * @param manager
	 *            the menu manager
	 */
//	private void fillContextMenu(IMenuManager manager) {
//		// manager.add(expertMode);
//		// Other plug-ins can contribute there actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//	}

	/**
	 * Fill the local toolbar.
	 * <p>
	 * 
	 * @param manager
	 *            the toolbar manager
	 */
//	private void fillLocalToolBar(IToolBarManager manager) {
		// if (UserSupport.isExpertMode()) {
		// expertMode.setChecked(true);
		// } else {
		// expertMode.setChecked(false);
		// }
		// manager.add(expertMode);
//	}

	/**
	 * Creat the actions used in this page.
	 */
//	private void makeActions() {
		// expertMode = new Action("Expert mode switch", SWT.CHECK) {
		// public void run() {
		// if (expertMode.isChecked())
		// UserSupport.setExpertMode(true);
		// else
		// UserSupport.setExpertMode(false);
		// }
		// };
		// expertMode.setToolTipText("Expert mode switch");
		//
		// expertMode.setImageDescriptor(EventBImage
		// .getImageDescriptor(EventBImage.IMG_EXPERT_MODE_PATH));

//	}

	/**
	 * Passing the focus request to the button bar.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	public void setFocus() {
		scrolledForm.setFocus();
		// textInput.setFocus();
		// ProofState currentPO = editor.getUserSupport().getCurrentPO();
		// if (currentPO == null)
		// updateToolItems(null);
		// else
		// updateToolItems(currentPO.getCurrentNode());
		// buttonBar.setFocus();
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

	/**
	 * Update the status of the toolbar items.
	 */
//	private void updateToolItems(IProofTreeNode node) {
		// for (GlobalTacticDropdownToolItem item : dropdownItems) {
		// item.updateStatus(node, textInput.getTextWidget().getText());
		// }
		//
		// for (GlobalTacticToolItem item : toolItems) {
		// item.updateStatus(node, textInput.getTextWidget().getText());
		// }

//		return;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofStateChangedListener#proofStateChanged(org.eventb.core.pm.IProofStateDelta)
	 */
	public void proofStateChanged(final IProofStateDelta delta) {

		if (scrolledForm.isDisposed())
			return;

		Display display = Display.getDefault();

		final IUserSupport userSupport = editor.getUserSupport();
		display.syncExec(new Runnable() {
			public void run() {
				IProofState ps = delta.getProofState();
				if (delta.isDeleted()) {
					if (ps != null) {
						IWorkbenchPage activePage = EventBUIPlugin
								.getActivePage();
						if (activePage.isPartVisible(editor)) {
							try {
								MessageDialog.openInformation(SearchHypothesisPage.this
										.getSite().getShell(), "Out of Date",
										"The Proof Obligation is deleted.");
								userSupport.nextUndischargedPO(true, new NullProgressMonitor());
							} catch (RodinDBException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							try {
								userSupport.setCurrentPO(null, new NullProgressMonitor());
							} catch (RodinDBException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} else if (delta.isNewProofState()) {
					if (ps != null) { // Reload everything
						init();
					} else {
						init();
					}
					scrolledForm.reflow(true);
				} else {
					IProofTreeNode node = delta.getNewProofTreeNode();
					if (node != null) {
						init();
					} else if (delta.getNewCache()) {
						init();
					} else if (delta.getNewSearch()) {
						init();
					}
					scrolledForm.reflow(true);
				}

			}
		});
		
	}

}
