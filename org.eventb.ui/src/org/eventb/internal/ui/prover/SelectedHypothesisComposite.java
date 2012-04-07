/*******************************************************************************
 * Copyright (c) 2007, 2012 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - Initial API and implementation
 ******************************************************************************/

package org.eventb.internal.ui.prover;

import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IProofStateDelta;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.ITactic;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.eventbExtensions.Tactics;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.utils.Messages;
import org.eventb.ui.IEventBSharedImages;

public class SelectedHypothesisComposite extends HypothesisComposite {

	// Tool item for removing the selected hypotheses.
	private ToolItem removeItem;

	// Tool item for inverse the current selection.
	private ToolItem inverseSelection;

	// Tool item for select all selected hypotheses.
	private ToolItem selectAll;
	
	// Tool item for de-select all selected hypothesis 
	private ToolItem selectNone;
	
	// The parent scrolled form of this selected hypothesis composite.
	private ScrolledForm parentForm;

	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this selected hypothesis
	 *            composite.
	 * @param parentForm
	 *            the parent scrolled form of this selected hypothesis
	 *            composite.
	 * @param proverUI
	 *            the main prover editor associated with this selected
	 *            hypothesis composite.
	 */
	public SelectedHypothesisComposite(IUserSupport userSupport,
			ScrolledForm parentForm, ProverUI proverUI) {
		super(userSupport, IProofStateDelta.F_NODE
				| IProofStateDelta.F_PROOFTREE, proverUI, "SelHyp");
		this.parentForm = parentForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisComposite#createItems(org.eclipse.swt.widgets.ToolBar)
	 */
	@Override
	public void createItems(ToolBar toolBar) {
		// Create item for remove hypotheses.
		removeItem = new ToolItem(toolBar, SWT.PUSH);
		removeItem.setImage(EventBImage
				.getImage(IEventBSharedImages.IMG_REMOVE));
		removeItem
				.setToolTipText(Messages.selectedHypothesis_toolItem_remove_toolTipText);
		removeItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				assert getUserSupport() != null;
				Set<Predicate> deselected = SelectedHypothesisComposite.this
						.getSelectedHyps();
				ITactic t = Tactics.mngHyp(ProverFactory
						.makeDeselectHypAction(deselected));
				getUserSupport().applyTacticToHypotheses(t, deselected,
						true, new NullProgressMonitor());
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
				.setToolTipText(Messages.selectedHypothesis_toolItem_selectAll_toolTipText);
		selectAll.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				SelectedHypothesisComposite.this.selectAllHyps();
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
				.setToolTipText(Messages.selectedHypothesis_toolItem_inverseSelection_toolTipText);
		inverseSelection.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				assert getUserSupport() != null;
				SelectedHypothesisComposite.this.inverseSelectedHyps();
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
				.setToolTipText(Messages.selectedHypothesis_toolItem_selectNone_toolTipText);
		selectNone.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				SelectedHypothesisComposite.this.deselectAllHyps();
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

	}

	@Override
	public Iterable<Predicate> getHypotheses(IProofState ps) {
		return ps.getSelected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisComposite#updateToolbarItems()
	 */
	@Override
	public void updateToolbarItems() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisComposite#refresh()
	 */
	@Override
	protected void refresh() {
		// Override this method to re-layout the parent scrolled form and scroll
		// to the bottom of the hypothesis rows.
		super.refresh();
		parentForm.reflow(true);
		scrollToBottom();
	}

}