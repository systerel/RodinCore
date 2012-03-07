/*******************************************************************************
 * Copyright (c) 2011, 2012 Systerel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Systerel - initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.prover.registry.PredicateApplicationProxy;

/**
 * Class able to create controls to place in a predicate row which will allow
 * the user to apply predicate applications.
 * 
 * @author "Thomas Muller"
 */
public class PredicateApplicationMaker extends ControlMaker {

	public PredicateApplicationMaker(Composite parent) {
		super(parent);
	}

	@Override
	public Control makeControl(ControlHolder holder) {
		final StyledText text = holder.getText();
		final PredicateRow row = holder.getRow();
		final boolean enabled = row.isEnabled();
		if (!enabled) {
			return new Link(text, SWT.NONE);
		}
		final Button button = new Button(text, SWT.ARROW | SWT.DOWN);
		button.setEnabled(false);
		final List<PredicateApplicationProxy> tactics;
		tactics = getPredicateApplications(row);
		final int tacSize = tactics.size();
		if (tacSize == 0){
			return new Link(text, SWT.NONE);
		}
		if (tacSize == 1) {
			final PredicateApplicationProxy appli = tactics.get(0);
			final Link tacLink = new Link(text, SWT.NONE);
			final String tooltip = appli.getTooltip();
			tacLink.setText(getText(tooltip));
			tacLink.setToolTipText(tooltip);
			tacLink.addSelectionListener(getTacticSelectionListener(row,
					appli));
			return tacLink;
		}
		button.setEnabled(true);
		final Menu menu = new Menu(button);
		final SelectionListener predAppliListener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menu.setVisible(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
		button.addSelectionListener(predAppliListener);
		createImageHyperlinks(row, menu, tactics);
		return button;
	}

	private List<PredicateApplicationProxy> getPredicateApplications(
			PredicateRow row) {
		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		final IUserSupport us = row.getUserSupport();
		final Predicate hyp = row.isGoal() ? null : row.getPredicate();
		return tacticUIRegistry.getPredicateApplications(us, hyp);
	}

	private static String getText(String tooltip) {
		final StringBuilder sb = new StringBuilder("<a>");
		if (tooltip.length() >= 4) {
			sb.append(tooltip.substring(0, 1));
			sb.append(tooltip.substring(3, 4));
		} else if (tooltip.length() > 2) {
			sb.append(tooltip.substring(0, 2));
		} else {
			sb.append(tooltip);
		}
		sb.append("</a>");
		final String result = sb.toString().toLowerCase();
		return result;
	}

	/**
	 * Utility methods to create menu items for applicable tactics
	 */
	private static void createImageHyperlinks(PredicateRow row, Menu menu,
			List<PredicateApplicationProxy> applis) {
		for (final PredicateApplicationProxy appli : applis) {
			final Image icon = appli.getIcon();
			final String tooltip = appli.getTooltip();
			final SelectionListener tacListener = getTacticSelectionListener(
					row, appli);
			addMenuItem(menu, icon, tooltip, row.isEnabled(), tacListener);
		}
	}

	private static void addMenuItem(Menu menu, Image icon, String tooltip,
			boolean enable, SelectionListener listener) {
		final MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setImage(icon);
		item.setText(tooltip);
		item.setEnabled(enable);
		item.addSelectionListener(listener);
	}

	private static SelectionListener getTacticSelectionListener(
			final PredicateRow row, final PredicateApplicationProxy appli) {
		return new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				row.apply(appli);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
	}

}
