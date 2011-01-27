/*******************************************************************************
 * Copyright (c) 2006, 2011 ETH Zurich and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     ETH Zurich - initial API and implementation
 *     Systerel - used EventBSharedColor
 *     Systerel - mathematical language V2
 *     Systerel - added dispose checkboxListener to hypothesis composite
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *     Systerel - bug correction (oftype) #2884753
 *     Systerel - fixed Hyperlink.setImage() calls
 *******************************************************************************/
package org.eventb.internal.ui.prover;

import static org.eventb.internal.ui.prover.ProverUIUtils.SOFT_BG_COLOR;
import static org.eventb.internal.ui.prover.ProverUIUtils.applyCommand;
import static org.eventb.internal.ui.prover.ProverUIUtils.applyTactic;
import static org.eventb.internal.ui.prover.ProverUIUtils.debug;
import static org.eventb.internal.ui.prover.ProverUIUtils.getIcon;
import static org.eventb.internal.ui.prover.ProverUIUtils.getParsed;
import static org.eventb.internal.ui.prover.ProverUIUtils.getTooltip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.ui.prover.IPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author htson
 *         <p>
 *         A class to create a row containing a predicate and the set of proof
 *         buttons which is applicable to the predicate
 *         </p>
 */
public class PredicateRow {

	private static final Color WHITE = EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);

	// Number of tabulations from the left
	private final int nbTabsFromLeft;

	private final ProverUI proverUI;

	// The UserSupport associated with this instance of the editor
	private final IUserSupport userSupport;

	// The predicate contained by this row
	private final Predicate pred;
	// The predicate re-parsed and containing source locations
	private final Predicate parsedPredicate;

	private final boolean enable;

	private EventBPredicateText predicateText;

	protected TacticHyperlinkManager manager;

	// Check Button holder
	private ControlHolder checkBoxHolder;
	private SelectionListener checkboxListener;
	
	// Predicate/Command holder
	private ControlHolder predAppliHolder;
	private SelectionListener predAppliListener;

	private boolean isGoal;

	private final ControlMaker checkBoxMaker;
	private final YellowBoxMaker yellowBoxMaker;

	/**
	 * @author htson
	 *         This class extends and provide response actions
	 *         when a hyperlink is activated.
	 * @param yellowBoxMaker 
	 */
	public PredicateRow(int nbTabsFromLeft, Predicate pred, boolean isGoal,
			IUserSupport userSupport, boolean enable,
			SelectionListener listener, ProverUI proverUI,
			TacticHyperlinkManager manager, ControlMaker checkboxMaker,
			YellowBoxMaker yellowBoxMaker) {

		this.pred = pred;
		this.isGoal = isGoal;
		this.checkboxListener = listener;
		this.userSupport = userSupport;
		this.enable = enable;
		this.proverUI = proverUI;
		this.manager = manager;
		this.nbTabsFromLeft = nbTabsFromLeft;
		this.checkBoxMaker = checkboxMaker;
		this.yellowBoxMaker = yellowBoxMaker;

		final FormulaFactory ff = userSupport.getFormulaFactory();
		final String parsedString = pred.toString();		
		// Predicate containing the SourceLocations
		this.parsedPredicate = getParsed(parsedString, ff);
		
		createPredicateText();
	}

	private void createPredicateText() {
		if (predicateText != null)
			predicateText.dispose();
		predicateText = new EventBPredicateText(this, isGoal, enable, proverUI,
				yellowBoxMaker);
		predicateText.load(pred.toString(), userSupport, pred, parsedPredicate);
	}

	public void append(boolean odd) {
		createControlButtons(odd);
		predicateText.append(manager, odd);
	}
	
	public void attachButtons() {
		if (!isGoal) {
			checkBoxHolder.attach(true);
		}
		predAppliHolder.attach(false);
		predicateText.attach();
	}
	
	private void createControlButtons(boolean odd) {
		final int checkBoxOffset = manager.getCurrentOffset() - nbTabsFromLeft;
		final Color bgColor = odd ? SOFT_BG_COLOR : WHITE;
		if (!isGoal) {
			checkBoxHolder = new ControlHolder(checkBoxMaker,
					checkBoxOffset, false, bgColor);
			checkBoxHolder.addSelectionListener(checkboxListener);
		}
		final int menuOffset = checkBoxOffset + 1;
		final Button predAppliButton = createApplicationsButton();
		predAppliHolder = new ControlHolder(predAppliButton,
				menuOffset, false, bgColor);
		predAppliButton.setVisible(false);
	}
	
	private Button createApplicationsButton() {
		final StyledText text = manager.getText();
		final Button button = new Button(text, SWT.ARROW | SWT.DOWN);
		button.setEnabled(false);
		if (!enable) {
			return button;
		}
		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		final List<IPredicateApplication> tactics;
		final List<ICommandApplication> commands;
		
		if (isGoal) {
			tactics = retainPredicateApplications(tacticUIRegistry);
			commands = tacticUIRegistry
					.getCommandApplicationsToGoal(userSupport);
		} else {
			tactics = retainPredicateApplications(tacticUIRegistry);
			commands = tacticUIRegistry.getCommandApplicationsToHypothesis(
					userSupport, pred);
		}
		final int comSize = commands.size();
		final int tacSize = tactics.size();
		if (tacSize == 1 && comSize == 0) {
			final ITacticApplication appli = tactics.get(0);
			final Button tacButton = new Button(text, SWT.PUSH);
			final IPredicateApplication predAppli = (IPredicateApplication) appli;
			tacButton.setImage(getIcon(predAppli));
			tacButton.setToolTipText(getTooltip(predAppli));
			tacButton.addSelectionListener(getTacticSelectionListener(
					tacticUIRegistry, appli));
			return tacButton;
		}
		if (tacSize == 0 && comSize == 1) {
			final ICommandApplication command = commands.get(0);
			final Button comButton = new Button(text, SWT.PUSH);
			comButton.setImage(command.getIcon());
			comButton.setToolTipText(command.getTooltip());
			return comButton;
		}
		button.setEnabled(true);
		final Menu menu = new Menu(button);
		predAppliListener = new SelectionListener() {
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
		createImageHyperlinks(menu, tacticUIRegistry, tactics, commands);
		return button;
	}

	private List<IPredicateApplication> retainPredicateApplications(
			TacticUIRegistry tacticUIRegistry) {
		final List<IPredicateApplication> predApplis = new ArrayList<IPredicateApplication>();
		final List<ITacticApplication> tactics;
		if (isGoal) {
			tactics = tacticUIRegistry.getTacticApplicationsToGoal(userSupport);
		} else {
			tactics = tacticUIRegistry.getTacticApplicationsToHypothesis(
					userSupport, pred);
		}
		for (ITacticApplication tactic : tactics) {
			if (tactic instanceof IPredicateApplication) {
				predApplis.add((IPredicateApplication) tactic);
			}
		}
		return predApplis;
	}

	/**
	 * Utility methods to create menu items for applicable tactics and commands
	 */
	private void createImageHyperlinks(Menu menu, TacticUIRegistry registry,
			List<IPredicateApplication> tactics, List<ICommandApplication> commands) {
		for (final ITacticApplication tacticAppli : tactics) {
			if (!(tacticAppli instanceof IPredicateApplication))
				continue;
			final IPredicateApplication predAppli = (IPredicateApplication) tacticAppli;
			final Image icon = getIcon(predAppli);
			final String tooltip = getTooltip(predAppli);
			final SelectionListener tacListener = getTacticSelectionListener(
					registry, predAppli);
			addMenuItem(menu, icon, tooltip, enable, tacListener);
		}
		for (final ICommandApplication commandAppli : commands) {
			final SelectionListener hlListener = getCommandListener(commandAppli);
			addMenuItem(menu, commandAppli.getIcon(),
					commandAppli.getTooltip(), enable, hlListener);
		}
	}
	
	private SelectionListener getTacticSelectionListener(
			final TacticUIRegistry tacticUIRegistry,
			final ITacticApplication appli) {
		return new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				apply(appli,
						tacticUIRegistry.isSkipPostTactic(appli.getTacticID()));
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
	}
	
	private SelectionListener getCommandListener(final ICommandApplication appli) {
		return new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				apply(appli);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
	}

	private static void addMenuItem(Menu menu, Image icon, String tooltip, boolean enable, SelectionListener listener) {
		final MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setImage(icon);
		item.setText(tooltip);
		item.setEnabled(enable);
		item.addSelectionListener(listener);
	}

	/**
	 * Utility method to dispose the composites and check boxes.
	 */
	public void dispose() {
		if (predicateText != null)
			predicateText.dispose();

		if (checkBoxHolder != null) {
			checkBoxHolder.remove();
		}
		if (predAppliHolder != null) {
			predAppliHolder.remove();			
		}
		
	}

	/**
	 * Return if the hypothesis is selected or not.
	 * 
	 * @return <code>true</code> if the row is selected, and <code>false</code>
	 *         otherwise
	 */
	public boolean isSelected() {
		if (enable && checkBoxHolder != null) {
			final Button checkbox = (Button) checkBoxHolder.getControl();
			if (checkbox != null) {
				return checkbox.getSelection();
			}
		}
		return false;
	}

	/**
	 * Get the contained predicate.
	 * 
	 * @return the predicate corresponding to this row
	 */
	public Predicate getPredicate() {
		return pred;
	}

	void apply(ITacticApplication tacticAppli, boolean skipPostTactic) {
		final String[] inputs = predicateText.getResults();
		if (ProverUIUtils.DEBUG)
			for (String input : inputs)
				debug("Input: \"" + input + "\"");

		final String globalInput = this.proverUI.getProofControl().getInput();
		if (isGoal) {
			applyTactic(tacticAppli.getTactic(inputs, globalInput), userSupport,
					null, skipPostTactic, new NullProgressMonitor());
			return;
		}
		final Set<Predicate> hypSet = Collections.singleton(pred);
		applyTactic(tacticAppli.getTactic(inputs, globalInput), userSupport,
				hypSet, skipPostTactic, new NullProgressMonitor());
	}

	void apply(ICommandApplication commandAppli) {
		final String[] inputs = predicateText.getResults();
		if (isGoal) {
			applyCommand(commandAppli.getProofCommand(), userSupport,
					null, inputs, new NullProgressMonitor());
			return;
		}
		applyCommand(commandAppli.getProofCommand(), userSupport, pred, inputs,
				new NullProgressMonitor());
	}

	public void setSelected(boolean selected) {
		if (!enable || checkBoxHolder == null)
			return;
		checkBoxHolder.render();
		final Button checkbox = (Button) checkBoxHolder.getControl();
		checkbox.setSelection(selected);
	}
	
	public int getNbTabsFromLeft() {
		return nbTabsFromLeft;
	}

}