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
import org.eventb.internal.ui.prover.HypothesisPage;
import org.eventb.ui.IEventBSharedImages;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         This class is an implementation of a Search Hypothesis Page.
 */
public class SearchHypothesisPage extends HypothesisPage implements
		ISearchHypothesisPage {

	ToolItem addItem;
	
	ToolItem removeItem;
	
	/**
	 * Constructor.
	 * <p>
	 * 
	 * @param userSupport
	 *            the User Support associated with this Hypothesis Page.
	 */
	public SearchHypothesisPage(IUserSupport userSupport) {
		super(userSupport, IProofStateDelta.F_NODE | IProofStateDelta.F_SEARCH);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eventb.internal.ui.prover.HypothesisPage#getHypotheses(org.eventb.core.pm.IProofState)
	 */
	@Override
	public Collection<Predicate> getHypotheses(IProofState ps) {
		Collection<Predicate> searched = new ArrayList<Predicate>();
		if (ps != null) {
			searched = ps.getSearched();
		}
		return searched;
	}

	@Override
	public void createItems(ToolBar toolBar) {
		addItem = new ToolItem(toolBar, SWT.PUSH);
		addItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_ADD));
		addItem.setToolTipText("Add to selected");
		addItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				IUserSupport userSupport = SearchHypothesisPage.this.getUserSupport();
				assert userSupport != null;
				
				Set<Predicate> selected = SearchHypothesisPage.this.getSelectedHyps();
				ITactic t = Tactics.mngHyp(ProverFactory.makeSelectHypAction(selected));
				try {
					userSupport.applyTacticToHypotheses(t, selected,
							new NullProgressMonitor());
				} catch (RodinDBException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});

		removeItem = new ToolItem(toolBar, SWT.PUSH);
		removeItem.setImage(EventBImage.getImage(IEventBSharedImages.IMG_REMOVE));
		removeItem.setToolTipText("Remove from searched");
		removeItem.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				IUserSupport userSupport = SearchHypothesisPage.this.getUserSupport();
				assert userSupport != null;
				
				Set<Predicate> deselected = SearchHypothesisPage.this.getSelectedHyps();
				userSupport.removeSearchedHypotheses(deselected);
			}

			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}
			
		});
	}

	@Override
	public void updateToolbarItems() {
		addItem.setEnabled(!this.getSelectedHyps().isEmpty());
		removeItem.setEnabled(!this.getSelectedHyps().isEmpty());
	}
	

}
