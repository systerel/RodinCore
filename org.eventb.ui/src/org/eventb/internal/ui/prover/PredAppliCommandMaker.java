/*******************************************************************************
 * Copyright (c) 2011 Systerel and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Systerel - Initial API and implementation
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static org.eventb.internal.ui.prover.ProverUIUtils.getIcon;
import static org.eventb.internal.ui.prover.ProverUIUtils.getTooltip;

import java.util.ArrayList;
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
import org.eventb.core.pm.IUserSupport;
import org.eventb.ui.prover.IPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * Class able to create controls to place in the predicate row which will allow
 * the user to apply predicate applications or commands.
 * 
 * @author "Thomas Muller"
 */
public class PredAppliCommandMaker extends ControlMaker {

	public PredAppliCommandMaker(Composite parent) {
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
		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		final List<IPredicateApplication> tactics;
		final List<ICommandApplication> commands;

		final IUserSupport us = row.getUserSupport();
		if (row.isGoal()) {
			tactics = retainPredicateApplications(tacticUIRegistry, row);
			commands = tacticUIRegistry.getCommandApplicationsToGoal(us);
		} else {
			tactics = retainPredicateApplications(tacticUIRegistry, row);
			commands = tacticUIRegistry.getCommandApplicationsToHypothesis(us,
					row.getPredicate());
		}
		final int comSize = commands.size();
		final int tacSize = tactics.size();
		if (tacSize == 0 && comSize == 0){
			return new Link(text, SWT.NONE);
		}
		if (tacSize == 1 && comSize == 0) {
			final ITacticApplication appli = tactics.get(0);
			final Link tacLink = new Link(text, SWT.NONE);
			final IPredicateApplication predAppli = (IPredicateApplication) appli;
			final String tooltip = getTooltip(predAppli);
			tacLink.setText(getText(tooltip));
			tacLink.setToolTipText(tooltip);
			tacLink.addSelectionListener(getTacticSelectionListener(row,
					tacticUIRegistry, appli));
			return tacLink;
		}
		if (tacSize == 0 && comSize == 1) {
			final ICommandApplication command = commands.get(0);
			final Link comLink = new Link(text, SWT.NONE);
			final String tooltip = command.getTooltip();
			comLink.setText(getText(tooltip));
			comLink.setToolTipText(tooltip);
			comLink.addSelectionListener(getCommandListener(row, command));
			return comLink;
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
		createImageHyperlinks(row, menu, tacticUIRegistry, tactics, commands);
		return button;
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
	 * Utility methods to create menu items for applicable tactics and commands
	 */
	private static void createImageHyperlinks(PredicateRow row, Menu menu,
			TacticUIRegistry registry, List<IPredicateApplication> tactics,
			List<ICommandApplication> commands) {
		for (final ITacticApplication tacticAppli : tactics) {
			if (!(tacticAppli instanceof IPredicateApplication))
				continue;
			final IPredicateApplication predAppli = (IPredicateApplication) tacticAppli;
			final Image icon = getIcon(predAppli);
			final String tooltip = getTooltip(predAppli);
			final SelectionListener tacListener = getTacticSelectionListener(
					row, registry, predAppli);
			addMenuItem(menu, icon, tooltip, row.isEnabled(), tacListener);
		}
		for (final ICommandApplication commandAppli : commands) {
			final SelectionListener hlListener = getCommandListener(row,
					commandAppli);
			addMenuItem(menu, commandAppli.getIcon(),
					commandAppli.getTooltip(), row.isEnabled(), hlListener);
		}
	}

	/**
	 * Utility method to keep only the predicate applications calculated for the
	 * predicate of the given row
	 */
	private static List<IPredicateApplication> retainPredicateApplications(
			TacticUIRegistry tacticUIRegistry, PredicateRow row) {
		final List<IPredicateApplication> predApplis = new ArrayList<IPredicateApplication>();
		final List<ITacticApplication> tactics;
		final IUserSupport us = row.getUserSupport();
		if (row.isGoal()) {
			tactics = tacticUIRegistry.getTacticApplicationsToGoal(us);
		} else {
			tactics = tacticUIRegistry.getTacticApplicationsToHypothesis(us,
					row.getPredicate());
		}
		for (ITacticApplication tactic : tactics) {
			if (tactic instanceof IPredicateApplication) {
				predApplis.add((IPredicateApplication) tactic);
			}
		}
		return predApplis;
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
			final PredicateRow row, final TacticUIRegistry tacticUIRegistry,
			final ITacticApplication appli) {
		return new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				row.apply(appli,
						tacticUIRegistry.isSkipPostTactic(appli.getTacticID()));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
	}

	private static SelectionListener getCommandListener(final PredicateRow row,
			final ICommandApplication command) {
		return new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				row.apply(command);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
	}

}
