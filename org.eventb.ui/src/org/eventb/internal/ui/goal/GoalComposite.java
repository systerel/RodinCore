/*******************************************************************************
 * Copyright (c) 2007, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.goal;

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
import org.eventb.internal.ui.prover.HypothesisComposite;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.RodinDBException;

public class GoalComposite extends HypothesisComposite {

	ToolItem addItem;
	
	ToolItem removeItem;

	public GoalComposite(IUserSupport userSupport, ProverUI proverUI) {
		super(userSupport, IProofStateDelta.F_NODE | IProofStateDelta.F_CACHE,
				proverUI);
	}

	@Override
	public void createItems(ToolBar toolBar) {
		addItem = new ToolItem(toolBar, SWT.PUSH);
		addItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_ADD));
		addItem.setToolTipText("Add to selected");
		addItem.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				final IUserSupport us = getUserSupport();
				assert us != null;
				final Set<Predicate> selected = GoalComposite.this
						.getSelectedHyps();
				final ITactic t = Tactics.mngHyp(ProverFactory
						.makeSelectHypAction(selected));
				try {
					us.applyTacticToHypotheses(t, selected, true,
							new NullProgressMonitor());
				} catch (RodinDBException e1) {
					e1.printStackTrace();
				}
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

		});

		removeItem = new ToolItem(toolBar, SWT.PUSH);
		removeItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_REMOVE));
		removeItem.setToolTipText("Remove from cached");
		removeItem.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				final IUserSupport us = getUserSupport();
				assert us != null;
				final Set<Predicate> deselected = GoalComposite.this
						.getSelectedHyps();
				us.removeCachedHypotheses(deselected);
			}

			@Override
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
		return cached;
	}

	@Override
	public void updateToolbarItems() {
		addItem.setEnabled(!this.getSelectedHyps().isEmpty());
		removeItem.setEnabled(!this.getSelectedHyps().isEmpty());
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		updateToolbarItems();
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		widgetDefaultSelected(e);
	}

}
