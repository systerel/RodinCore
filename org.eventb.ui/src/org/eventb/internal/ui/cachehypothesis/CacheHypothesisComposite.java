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
	
	ToolItem selectNone;
	
	public CacheHypothesisComposite(IUserSupport userSupport,
			ProverUI proverUI) {
		super(userSupport, IProofStateDelta.F_NODE
				| IProofStateDelta.F_PROOFTREE | IProofStateDelta.F_CACHE,
				proverUI);
	}

	@Override
	public void createItems(ToolBar toolBar) {
		addItem = new ToolItem(toolBar, SWT.PUSH);
		addItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_ADD));
		addItem
				.setToolTipText(Messages.cacheHypothesis_toolItem_add_toolTipText);
		addItem.addSelectionListener(new SelectionListener() {

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

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

		removeItem = new ToolItem(toolBar, SWT.PUSH);
		removeItem.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_REMOVE));
		removeItem
				.setToolTipText(Messages.cacheHypothesis_toolItem_remove_toolTipText);
		removeItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				IUserSupport userSupport = CacheHypothesisComposite.this
						.getUserSupport();
				assert userSupport != null;

				Set<Predicate> deselected = CacheHypothesisComposite.this
						.getSelectedHyps();
				userSupport.removeCachedHypotheses(deselected);
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});
		
		selectAll = new ToolItem(toolBar, SWT.PUSH);
		selectAll.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_SELECT_ALL));
		selectAll
				.setToolTipText(Messages.cacheHypothesis_toolItem_selectAll_toolTipText);
		selectAll.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				CacheHypothesisComposite.this.selectAllHyps();
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

		});

		inverseSelection = new ToolItem(toolBar, SWT.PUSH);
		inverseSelection.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_INVERSE));
		inverseSelection
				.setToolTipText(Messages.cacheHypothesis_toolItem_inverseSelection_toolTipText);
		inverseSelection.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				CacheHypothesisComposite.this.inverseSelectedHyps();
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

		});

		selectNone = new ToolItem(toolBar, SWT.PUSH);
		selectNone.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_SELECT_NONE));
		selectNone
				.setToolTipText(Messages.cacheHypothesis_toolItem_selectNone_toolTipText);
		selectNone.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				CacheHypothesisComposite.this.deselectAllHyps();
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});
	}

	@Override
	public Iterable<Predicate> getHypotheses(IProofState ps) {
		Collection<Predicate> cached = new ArrayList<Predicate>();
		if (ps != null) {
			cached = ps.getCached();
		}
		Collection<Predicate> validCached = new ArrayList<Predicate>();
		for (Predicate cache : cached) {
			if (ps.getCurrentNode().getSequent().containsHypothesis(cache))
				validCached.add(cache);
		}
		return validCached;
	}

	@Override
	public void updateToolbarItems() {
		if (CacheHypothesisUtils.DEBUG)
			CacheHypothesisUtils.debug("Update toolbar item: Add, Remove");
		addItem.setEnabled(!this.getSelectedHyps().isEmpty());
		removeItem.setEnabled(!this.getSelectedHyps().isEmpty());
	}

	public void widgetDefaultSelected(SelectionEvent e) {
		updateToolbarItems();
	}

	public void widgetSelected(SelectionEvent e) {
		widgetDefaultSelected(e);
	}

}
