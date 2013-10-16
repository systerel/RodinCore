/*******************************************************************************
 * Copyright (c) 2013 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.proofcontrol;

import static org.eventb.internal.ui.prover.ProverUIUtils.applyTactic;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eventb.core.IPOSequent;
import org.eventb.core.pm.IProofState;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.ITacticDescriptor;
import org.eventb.internal.ui.prover.ProverUI;
import org.eventb.ui.prover.IUIDynTactic;
import org.eventb.ui.prover.IUIDynTacticProvider;

/**
 * Manager for dynamic dropdown tactic lists.
 * @author beauger
 * 
 */
public class DynamicDropdownManager extends SelectionAdapter {

	private final IUIDynTacticProvider tacticProvider;
	private final ProverUI proverUI;
	private final Menu menu;
	private final ToolItem toolItem;

	public DynamicDropdownManager(ToolItem toolItem,
			IUIDynTacticProvider tacticProvider, ProverUI proverUI) {
		this.tacticProvider = tacticProvider;
		this.proverUI = proverUI;
		this.toolItem = toolItem;
		this.menu = new Menu(toolItem.getParent().getShell());
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (menu.isDisposed()) {
			return;
		}

		final IUserSupport us = proverUI.getUserSupport();

		final IProofState currentPO = us.getCurrentPO();
		if (currentPO == null) {
			return;
		}

		final IProofTreeNode ptNode = currentPO.getCurrentNode();
		final IPOSequent poSequent = currentPO.getPSStatus().getPOSequent();

		final Collection<IUIDynTactic> dynTactics = tacticProvider
				.getDynTactics(ptNode, poSequent);

		updateMenu(us, dynTactics);
		menu.setLocation(computeMenuLocation((ToolItem) e.widget));
		menu.setVisible(true);
	}

	// TODO code copied from GlobalTacticDropdownToolItem (factorize)
	private static Point computeMenuLocation(ToolItem toolItem) {
		// Determine where to put the dropdown list
		final Rectangle rect = toolItem.getBounds();
		return toolItem.getParent().toDisplay(
				new Point(rect.x, rect.y + rect.height));
	}

	private void updateMenu(final IUserSupport us,
			Collection<IUIDynTactic> dynTactics) {
		// dispose previous menu items
		for (MenuItem item : menu.getItems()) {
			item.dispose();
		}

		for (IUIDynTactic dynTactic : dynTactics) {
			makeMenuItem(us, dynTactic);
		}
	}

	private void makeMenuItem(final IUserSupport us, IUIDynTactic dynTactic) {
		final MenuItem menuItem = new MenuItem(menu, SWT.NONE);

		menuItem.setText(dynTactic.getName());
		menuItem.setData(dynTactic.getTacticDescriptor());

		menuItem.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent event) {
				final ITacticDescriptor tacDesc = (ITacticDescriptor) event.widget
						.getData();
				applyTactic(tacDesc.getTacticInstance(), us, null, true, null);
			}
		});
	}

	public void update() {
		if (toolItem.isDisposed()) {
			return;
		}

		toolItem.setEnabled(shouldBeEnabled());
	}

	private boolean shouldBeEnabled() {
		final IUserSupport us = proverUI.getUserSupport();
		final IProofState currentPO = us.getCurrentPO();
		if (currentPO == null) {
			return false;
		}

		final IProofTreeNode currentNode = currentPO.getCurrentNode();
		if (currentNode == null) {
			return false;
		}

		if (currentNode.isClosed()) {
			return false;
		}

		return true;
	}
	
	public void dispose() {
		menu.dispose();
	}
}
