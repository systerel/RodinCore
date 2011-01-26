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
 *     Systerel - mathematical language V2
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *     Systerel - modifying getParsedTypeChecked() calls to getParsed()
 *     Systerel - fixed Hyperlink.setImage() calls
 ******************************************************************************/
package org.eventb.internal.ui.goal;
import static org.eventb.internal.ui.prover.ProverUIUtils.debug;
import static org.eventb.internal.ui.prover.ProverUIUtils.getProofStateDelta;
import static org.eventb.internal.ui.prover.ProverUIUtils.getUserSupportDelta;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.Page;
import org.eventb.core.EventBPlugin;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerDelta;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.autocompletion.ContentProposalFactory;
import org.eventb.internal.ui.proofcontrol.ProofControlUtils;
import org.eventb.internal.ui.prover.CheckBoxMaker;
import org.eventb.internal.ui.prover.ControlMaker;
import org.eventb.internal.ui.prover.PredicateRow;
import org.eventb.internal.ui.prover.ProofStatusLineManager;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.prover.TacticHyperlinkManager;
import org.eventb.internal.ui.prover.YellowBoxMaker;
import org.rodinp.keyboard.preferences.PreferenceConstants;

/**
 * @author htson
 *         This class is an implementation of a Goal 'page'.
 */
public class GoalPage extends Page implements IGoalPage, IPropertyChangeListener {

	private static final IUserSupportManager USM = EventBPlugin.getUserSupportManager();
	private static final Color NO_GOAL_BG_COLOR = EventBSharedColor.getSystemColor(SWT.COLOR_GRAY);
	
	// Number of tabulation in the left margin
	private static final int NB_TABS_LEFT = 3; //tabs
	private static final int LINE_SPACING = 2; //px

	protected final IUserSupport userSupport;

	protected ScrolledComposite sc;

	private StyledText styledText;

	private PredicateRow row;
	
	private TacticHyperlinkManager manager;

	private ProofStatusLineManager statusManager;

	private ProverUI proverUI;

	private Font font;

	private Composite control;
	
	/**
	 * Constructor.
	 * 
	 * @param userSupport
	 *            the User Support associated with this Goal Page.
	 */
	public GoalPage(ProverUI proverUI, IUserSupport userSupport) {
		super();
		this.proverUI = proverUI;
		this.userSupport = userSupport;
		USM.addChangeListener(this);
	}

	@Override
	public void dispose() {
		USM.removeChangeListener(this);
		JFaceResources.getFontRegistry().removeListener(this);
		super.dispose();
	}

	private void totalClearance() {
		if (row != null) {
			row.dispose();
			row = null;
		}
		if (manager != null)
			manager.dispose();
	}
	
	@Override
	public void createControl(Composite parent) {
		font = JFaceResources.getFont(PreferenceConstants.RODIN_MATH_FONT);
		// Create the top-level composite.
		control = new Composite(parent, SWT.NULL);
		if (ProverUIUtils.DEBUG) {
			control.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_DARK_GRAY));
		}
		// Set the layout of the top-level control to a form layout.
		control.setLayout(new FormLayout());
		
		sc = new ScrolledComposite(control, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setLayout(new GridLayout(1, false));
		final FormData fd = new FormData();
		fd.left = new FormAttachment(0);
		fd.right = new FormAttachment(100);
		fd.top = new FormAttachment(0);
		fd.bottom = new FormAttachment(100);
		sc.setLayoutData(fd);

		final IProofState ps = userSupport.getCurrentPO();
		if (ps != null) {
			setGoal(ps.getCurrentNode());
		} else {
			setGoal(null);
		}
		contributeToActionBars();
	}

	/**
	 * Set the current goal
	 * 
	 * @param node
	 *            the current proof tree node.
	 */
	public void setGoal(IProofTreeNode node) {
		if (styledText != null) {
			styledText.dispose();
			styledText = null;
		}
		totalClearance();
		styledText = new StyledText(sc, SWT.NONE);
		styledText.setFont(font);
		styledText.setEditable(false);
		styledText.setLineSpacing(LINE_SPACING);
		manager = new TacticHyperlinkManager(styledText);
		createGoalText(node);
		sc.setContent(styledText);
		sc.setMinSize(styledText.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createGoalText(final IProofTreeNode node) {
		if (node == null) {
			styledText.append("No current goal");
			styledText.setBackground(NO_GOAL_BG_COLOR);
			return;
		}
		final Predicate goal = node.getSequent().goal();
		final boolean enabled = node.isOpen();
		styledText.setRedraw(false);
		manager.appendText("\t\uFFFC\t");
		
		final ControlMaker checkboxMaker = new CheckBoxMaker(styledText);
		final IContentProposalProvider provider = ContentProposalFactory
				.getProposalProvider(userSupport);
		final YellowBoxMaker yellowBoxMaker = new YellowBoxMaker(styledText,
				provider);
		row = new PredicateRow(NB_TABS_LEFT, goal, true, userSupport, enabled,
				null, proverUI, manager, checkboxMaker, yellowBoxMaker);
		row.append(false);
		manager.setContents();
		row.attachButtons();
		manager.enableListeners(enabled);
		styledText.setRedraw(true);
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
	 * 
	 * @param menuManager
	 *            the menu manager
	 */
	private void fillLocalPullDown(IMenuManager menuManager) {
		menuManager.add(new Separator());
	}

	/**
	 * Fill the context menu.
	 * <p>
	 * 
	 * @param menuManager
	 *            the menu manager
	 */
	void fillContextMenu(IMenuManager menuManager) {
		// Other plug-ins can contribute there actions here
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/**
	 * Fill the local toolbar.
	 * 
	 * @param barManager
	 *            the toolbar manager
	 */
	private void fillLocalToolBar(IToolBarManager barManager) {
		// Do nothing
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setFocus() {
		control.setFocus();
	}

	@Override
	public void userSupportManagerChanged(IUserSupportManagerDelta delta) {
		if (GoalUtils.DEBUG)
			GoalUtils.debug("Begin User Support Manager Changed");

		// Do nothing if the page is disposed.
		if (control.isDisposed())
			return;

		// Trying to get the changes for the current user support.
		final IUserSupportDelta affectedUserSupport = getUserSupportDelta(delta, userSupport);

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
				debug("Error: Delta said that the user Support is added");
			return; // Do nothing
		}

		Display display = control.getDisplay();
		final Control c = control;
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (c.isDisposed())
					return;
				
				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();

					// Set the information if it has been changed.
					if ((flags & IUserSupportDelta.F_INFORMATION) != 0) {
						setInformation(affectedUserSupport.getInformation());
					}

					if ((flags & IUserSupportDelta.F_CURRENT) != 0) {
						// The current proof state is changed, reupdate the page
						IProofState ps = userSupport.getCurrentPO();
						if (ps != null) {
							setGoal(ps.getCurrentNode());
							return;
						} 
						setGoal(null);
						return;
					} 
					
					if ((flags & IUserSupportDelta.F_STATE) != 0) {
						// If the changes occurs in some proof states.	
						IProofState proofState = userSupport.getCurrentPO();
						// Trying to get the change for the current proof state. 
						final IProofStateDelta affectedProofState = getProofStateDelta(
								affectedUserSupport, proofState);
						if (affectedProofState != null) {
							// If there are some changes
							int psKind = affectedProofState.getKind();

							if (psKind == IProofStateDelta.ADDED) {
								// This case should not happened
								if (ProofControlUtils.DEBUG)
									ProofControlUtils
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

								if ((psFlags & IProofStateDelta.F_NODE) != 0
										|| (psFlags & IProofStateDelta.F_PROOFTREE) != 0) {
									setGoal(proofState.getCurrentNode());
								}
							}
						}
					}
				}
			}
		});

		if (GoalUtils.DEBUG)
			GoalUtils.debug("End User Support Manager Changed");
	}
	
	void setInformation(final IUserSupportInformation[] information) {
		if (statusManager == null) {
			statusManager = new ProofStatusLineManager(this.getSite()
					.getActionBars());
		}
		statusManager.setProofInformation(information);
	}

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
