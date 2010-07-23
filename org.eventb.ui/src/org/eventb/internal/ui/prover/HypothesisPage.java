/*******************************************************************************
 * Copyright (c) 2007, 2010 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - Added a constant for the user support manager
 ******************************************************************************/
package org.eventb.internal.ui.prover;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.Page;
import org.eventb.core.EventBPlugin;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.pm.IUserSupportDelta;
import org.eventb.core.pm.IUserSupportInformation;
import org.eventb.core.pm.IUserSupportManager;
import org.eventb.core.pm.IUserSupportManagerChangedListener;
import org.eventb.core.pm.IUserSupportManagerDelta;

/**
 * @author htson
 *         <p>
 *         This class is an abstract implementation of a Hypothesis 'Page' which
 *         can be used in Hypothesis View such as Cached and Search Hypothesis
 *         View. The implementation uses a main hypothesis composite
 *         {@link HypothesisComposite} for displaying the list of hypotheses.
 *         </p>
 *         <p>
 *         Clients need to implement the abstract method
 *         {@link #getHypypothesisCompsite()} to return appropriate Hypothesis
 *         Composite.
 *         </p>
 */
public abstract class HypothesisPage extends Page implements
		IHypothesisPage, IUserSupportManagerChangedListener {

	private static final IUserSupportManager USM = EventBPlugin
			.getUserSupportManager();

	// The User Support associated with this Hypothesis Page.
	protected IUserSupport userSupport;

	// The main prover editor associated with this Hypothesis Page
	protected ProverUI proverUI;

	// The hypothesis composite associate
	HypothesisComposite hypComp;
	
	private ProofStatusLineManager statusManager;
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this hypothesis page.
	 *            This must not be <code>null</code>.
	 * @param proverUI
	 *            the main prover editor ({@link ProverUI}). This must not be
	 *            <code>null</null>.
	 */
	public HypothesisPage(IUserSupport userSupport,
			ProverUI proverUI) {
		Assert.isNotNull(userSupport, "The User Suport should not be null"); // $NON-NLS-1$
		Assert.isNotNull(proverUI, "The main prover editor should not be null"); // $NON-NLS-1$
		this.userSupport = userSupport;
		this.proverUI = proverUI;
		hypComp = getHypypothesisCompsite();
		// Listen to the changes from the user support manager.
		USM.addChangeListener(this);
	}

	/**
	 * Abstract method for creating the main hypothesis composite to be used for
	 * this hypothesis page. Client needs to implement this method to provide
	 * the appropriate hypothesis composite of their choice.
	 * 
	 * @return a hypothesis composite.
	 */
	public abstract HypothesisComposite getHypypothesisCompsite();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.IPage#dispose()
	 */
	@Override
	public void dispose() {
		USM.removeChangeListener(this);
		// Disposing the main hypothesis composite
		hypComp.dispose();
		super.dispose();
	}
	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		// Create the content of the hypothesis composite.
		hypComp.createControl(parent);
		// Contribute to different action bars.  
		contributeToActionBars();
	}

	/*
	 * Utility method for setup the action bars.
	 */
	private void contributeToActionBars() {
		IActionBars bars = getSite().getActionBars();
		// Setup the local pull down menu.
		fillLocalPullDown(bars.getMenuManager());
		// Setup the local tool bar.
		fillLocalToolBar(bars.getToolBarManager());
		// Setup the context menu.
		fillContextMenu(bars.getMenuManager());
	}

	/*
	 * Fill the local pull down.
	 * 
	 * @param manager
	 *            the menu manager
	 */
	protected void fillLocalPullDown(IMenuManager manager) {
		manager.add(new Separator());
	}

	/*
	 * Fill the context menu.
	 * 
	 * @param manager
	 *            the menu manager
	 */
	protected void fillContextMenu(IMenuManager manager) {
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	/*
	 * Fill the local toolbar.
	 * <p>
	 * 
	 * @param manager
	 *            the toolbar manager
	 */
	protected void fillLocalToolBar(IToolBarManager manager) {
		// Do nothing
	}

	/**
	 * Passing the focus request to the hypothesis composite.
	 * <p>
	 * 
	 * @see org.eclipse.ui.part.IPage#setFocus()
	 */
	@Override
	public void setFocus() {
		// Pass the focus to the hypothesis composite.
		hypComp.setFocus();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.Page#getControl()
	 */
	@Override
	public Control getControl() {
		// Return the control of the hypothesis composite.
		return hypComp.getControl();
	}

	void setInformation(final IUserSupportInformation[] information) {
		if (statusManager == null) {
			statusManager = new ProofStatusLineManager(this.getSite()
					.getActionBars());
		}
		statusManager.setProofInformation(information);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.core.pm.IProofStateChangedListener#proofStateChanged(org.eventb.core.pm.IProofStateDelta)
	 */
	@Override
	public void userSupportManagerChanged(final IUserSupportManagerDelta delta) {

		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Begin User Support Manager Changed");

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

		Display display = this.getSite().getShell().getDisplay();

		display.syncExec(new Runnable() {
			@Override
			public void run() {

				// Handle the case where the user support has changed.
				if (kind == IUserSupportDelta.CHANGED) {
					int flags = affectedUserSupport.getFlags();

					// Set the information if it has been changed.
					if ((flags & IUserSupportDelta.F_INFORMATION) != 0) {
						setInformation(affectedUserSupport.getInformation());
					}

				}

			}
		});

		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("End User Support Manager Changed");

	}

}