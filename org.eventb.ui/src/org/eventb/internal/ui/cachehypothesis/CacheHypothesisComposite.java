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

package org.eventb.internal.ui.cachehypothesis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBUIExceptionHandler;
import org.eventb.internal.ui.prover.HypothesisComposite;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class extends {@link HypothesisComposite} to implements the
 *         displaying of cached hypotheses by adding different tool items, e.g
 *         "add"/"remove" hypotheses or "select all". This is used in
 *         {@link CacheHypothesisPage}.
 */
public class CacheHypothesisComposite extends HypothesisComposite {

	// Tool item for adding the selected (cached) hypotheses to the set of
	// selected hypotheses.
	ToolItem addItem;
	
	// Tool item for removing the selected (cached) hypotheses out of the cache.
	ToolItem removeItem;

	// Tool item for inverse the current selection.
	ToolItem inverseSelection;

	// Tool item for select all cached hypotheses.
	ToolItem selectAll;
	
	// Tool item for de-select all cached hypothesis 
	ToolItem selectNone;
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this cached hypothesis
	 *            composite.
	 * @param proverUI
	 *            the main prover editor associated with this cached hypothesis
	 *            composite.
	 */
	public CacheHypothesisComposite(IUserSupport userSupport,
			ProverUI proverUI) {
		// Create a hypothesis composite which listens to the changes for:
		// Current Proof Tree Node, changes in the Proof Tree itself and changes
		// for the cache.
		super(userSupport, IProofStateDelta.F_NODE
				| IProofStateDelta.F_PROOFTREE | IProofStateDelta.F_CACHE,
				proverUI, "CacheHyp");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisComposite#createItems(org.eclipse.swt.widgets.ToolBar)
	 */
	@Override
	public void createItems(ToolBar toolBar) {
		// Create item for adding hypotheses.
		addItem = new ToolItem(toolBar, SWT.PUSH);
		addItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_ADD));
		addItem
				.setToolTipText(Messages.cachedHypothesis_toolItem_add_toolTipText);
		addItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				IUserSupport userSupport = CacheHypothesisComposite.this
						.getUserSupport();
				assert userSupport != null;

				Set<Predicate> selected = CacheHypothesisComposite.this
						.getSelectedHyps();
				ITactic t = Tactics.mngHyp(ProverFactory
						.makeSelectHypAction(selected));
				try {
					userSupport.applyTacticToHypotheses(t, selected, true,
							new NullProgressMonitor());
				} catch (RodinDBException exception) {
					EventBUIExceptionHandler
							.handleApplyTacticException(exception);
				}
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

		// Create item for remove hypotheses.
		removeItem = new ToolItem(toolBar, SWT.PUSH);
		removeItem.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_REMOVE));
		removeItem
				.setToolTipText(Messages.cachedHypothesis_toolItem_remove_toolTipText);
		removeItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				IUserSupport userSupport = CacheHypothesisComposite.this
						.getUserSupport();
				assert userSupport != null;

				Set<Predicate> deselected = CacheHypothesisComposite.this
						.getSelectedHyps();
				userSupport.removeCachedHypotheses(deselected);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});
		
		// Create item for select all (cached) hypotheses.
		selectAll = new ToolItem(toolBar, SWT.PUSH);
		selectAll.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_SELECT_ALL));
		selectAll
				.setToolTipText(Messages.cachedHypothesis_toolItem_selectAll_toolTipText);
		selectAll.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				CacheHypothesisComposite.this.selectAllHyps();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

		});

		// Create item for inverting the current selection.
		inverseSelection = new ToolItem(toolBar, SWT.PUSH);
		inverseSelection.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_INVERSE));
		inverseSelection
				.setToolTipText(Messages.cachedHypothesis_toolItem_inverseSelection_toolTipText);
		inverseSelection.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				CacheHypothesisComposite.this.inverseSelectedHyps();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

		});

		// Create item for de-select all cached hypotheses.
		selectNone = new ToolItem(toolBar, SWT.PUSH);
		selectNone.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_SELECT_NONE));
		selectNone
				.setToolTipText(Messages.cachedHypothesis_toolItem_selectNone_toolTipText);
		selectNone.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				CacheHypothesisComposite.this.deselectAllHyps();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisComposite#getHypotheses(org.eventb.core.pm.IProofState)
	 */
	@Override
	public Iterable<Predicate> getHypotheses(IProofState ps) {
		Collection<Predicate> cached = new ArrayList<Predicate>();

		// Get the cached hypotheses associated with the proof state.
		if (ps != null) {
			cached = ps.getCached();
		}

		// Return the valid cached hypotheses only.
		Collection<Predicate> validCached = new ArrayList<Predicate>();
		for (Predicate cache : cached) {
			if (ps.getCurrentNode().getSequent().containsHypothesis(cache))
				validCached.add(cache);
		}
		return validCached;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisComposite#updateToolbarItems()
	 */
	@Override
	public void updateToolbarItems() {
		if (CacheHypothesisUtils.DEBUG)
			CacheHypothesisUtils.debug("Update toolbar item: Add, Remove");
		addItem.setEnabled(!this.getSelectedHyps().isEmpty());
		removeItem.setEnabled(!this.getSelectedHyps().isEmpty());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// Update the status of the toolbar items.
		updateToolbarItems();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	@Override
	public void widgetSelected(SelectionEvent e) {
		// Behave as the default selected.
		widgetDefaultSelected(e);
	}

}
