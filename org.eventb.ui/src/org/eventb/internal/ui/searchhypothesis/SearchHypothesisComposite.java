/*******************************************************************************
 * Copyright (c) 2005, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.searchhypothesis;

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
import org.eventb.internal.ui.prover.HypothesisComposite;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.IEventBSharedImages;

public class SearchHypothesisComposite extends HypothesisComposite {

	// Tool item for adding the selected (searched) hypotheses to the set of
	// selected hypotheses.
	ToolItem addItem;
	
	// Tool item for removing the selected (searched) hypotheses out of the
	// searched hypotheses.
	ToolItem removeItem;

	// Tool item for inverse the current selection.
	ToolItem inverseSelection;

	// Tool item for select all searched hypotheses.
	ToolItem selectAll;
	
	// Tool item for de-select all searched hypothesis 
	ToolItem selectNone;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this searched hypothesis
	 *            composite.
	 * @param proverUI
	 *            the main prover editor associated with this searched
	 *            hypothesis composite.
	 */
	public SearchHypothesisComposite(IUserSupport userSupport,
			ProverUI proverUI) {
		super(userSupport, IProofStateDelta.F_SEARCH, proverUI, "SearchHyp");
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
		addItem.setToolTipText(Messages.searchedHypothesis_toolItem_add_toolTipText);
		addItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				final IUserSupport us = getUserSupport();
				assert us != null;

				final Set<Predicate> selected = SearchHypothesisComposite.this
						.getSelectedHyps();
				final ITactic t = Tactics.mngHyp(ProverFactory
						.makeSelectHypAction(selected));
				us.applyTacticToHypotheses(t, selected, true,
						new NullProgressMonitor());
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
				.setToolTipText(Messages.searchedHypothesis_toolItem_remove_toolTipText);
		removeItem.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				final IUserSupport us = getUserSupport();
				assert us != null;

				final Set<Predicate> deselected = SearchHypothesisComposite.this
						.getSelectedHyps();
				us.removeSearchedHypotheses(deselected);
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
				.setToolTipText(Messages.searchedHypothesis_toolItem_selectAll_toolTipText);
		selectAll.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				SearchHypothesisComposite.this.selectAllHyps();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

		// Create item for inverting the current selection.
		inverseSelection = new ToolItem(toolBar, SWT.PUSH);
		inverseSelection.setImage(EventBImage.getImage(IEventBSharedImages.IMG_INVERSE));
		inverseSelection
				.setToolTipText(Messages.searchedHypothesis_toolItem_inverseSelection_toolTipText);
		inverseSelection.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				SearchHypothesisComposite.this.inverseSelectedHyps();
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
				.setToolTipText(Messages.searchedHypothesis_toolItem_selectNone_toolTipText);
		selectNone.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				SearchHypothesisComposite.this.deselectAllHyps();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

	}

	@Override
	public Iterable<Predicate> getHypotheses(IProofState ps) {
		return ps.filterHypotheses(ps.getSearched());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisComposite#updateToolbarItems()
	 */
	@Override
	public void updateToolbarItems() {
		if (SearchHypothesisUtils.DEBUG)
			SearchHypothesisUtils.debug("Update Tool Item: Add, Remove");
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
