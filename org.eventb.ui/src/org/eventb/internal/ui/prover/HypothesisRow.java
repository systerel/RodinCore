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
 *     Systerel - added dispose listener to hypothesis composite
 *     Systerel - refactored to use ITacticProvider2 and ITacticApplication
 *     Systerel - bug correction (oftype) #2884753
 *     Systerel - fixed Hyperlink.setImage() calls
 *******************************************************************************/
package org.eventb.internal.ui.prover;

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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
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
 *         A class to create a row containing a hypothesis and the set of proof
 *         buttons which is applicable to the hypothesis
 *         </p>
 */
public class HypothesisRow {


	private final ProverUI proverUI;

	private EventBPredicateText hypothesisText;

	// The UserSupport associated with this instance of the editor.
	private final IUserSupport userSupport;

	// The hypothesis contains in this row.
	private final Predicate hyp;

	private final boolean enable;

	private final SelectionListener listener;

	protected StyledText styledText;

	protected TacticHyperlinkManager manager;

	private final int nbTabsFromLeft;

	// Check Button holder
	private final ControlHolder<Button> checkBoxHolder;
	
	// Predicate/Command holder
	private ControlHolder<Button> menuHolder;

	/**
	 * @author htson
	 *         <p>
	 *         This class extends HyperlinkAdapter and provide response actions
	 *         when a hyperlink is activated.
	 */
	public HypothesisRow(StyledText styledText, int nbTabsFromLeft, Predicate hyp,
			IUserSupport userSupport, boolean odd, boolean enable,
			SelectionListener listener, ProverUI proverUI,
			TacticHyperlinkManager manager) {

		this.styledText = styledText;
		this.hyp = hyp;
		this.listener = listener;
		this.userSupport = userSupport;
		this.enable = enable;
		this.proverUI = proverUI;
		this.manager = manager;
		this.nbTabsFromLeft = nbTabsFromLeft;

		final Button checkBox = new Button(styledText, SWT.CHECK);
		checkBox.addSelectionListener(listener);

		final int checkBoxOffset = styledText.getCharCount() - nbTabsFromLeft;
		checkBoxHolder = new ControlHolder<Button>(styledText, checkBox, checkBoxOffset);
		checkBoxHolder.attach(enable);
		
		final int menuOffset = checkBoxOffset + 1;
		final Button predAppli = createApplicationsButton();
		menuHolder = new ControlHolder<Button>(styledText, predAppli, menuOffset);
		menuHolder.attach(enable);

		final String parsedString = hyp.toString();
		// Predicate containing the SourceLocations
		final FormulaFactory ff = userSupport.getFormulaFactory();
		final Predicate parsedPredicate = getParsed(parsedString, ff);
		createHypothesisText(parsedPredicate, parsedString);

	}

	private void createHypothesisText(Predicate parsedPredicate,
			String parsedString) {
		if (hypothesisText != null)
			hypothesisText.dispose();
		hypothesisText = new EventBPredicateText(this, false, enable, proverUI);
		hypothesisText.append(parsedString, userSupport, hyp, parsedPredicate);
	}
	
	private Button createApplicationsButton() {
		Button button = new Button(styledText, SWT.ARROW | SWT.DOWN);
		button.setEnabled(false);
		if (!enable) {
			return button;
		}
		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		final List<IPredicateApplication> tactics = retainPredicateApplications(tacticUIRegistry);
		final List<ICommandApplication> commands = tacticUIRegistry
				.getCommandApplicationsToHypothesis(userSupport, hyp);
		final int comSize = commands.size();
		final int tacSize = tactics.size();
		if (tacSize == 1 && comSize == 0) {
			final ITacticApplication appli = tactics.get(0);
			button = new Button(styledText, SWT.PUSH);
			button.setImage(getIcon((IPredicateApplication) appli));
			button.addSelectionListener(getTacticSelectionListener(
					tacticUIRegistry, appli));
			return button;
		}
		if (tacSize == 0 && comSize == 1) {
			final ICommandApplication command = commands.get(0);
			button = new Button(styledText, SWT.PUSH);
			button.setImage(command.getIcon());
			return button;
		}
		button.setEnabled(true);
		final Menu menu = new Menu(button);
		final SelectionListener arrowListener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menu.setVisible(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
		button.addSelectionListener(arrowListener);
		createImageHyperlinks(menu, tacticUIRegistry, tactics, commands);
		return button;

	}

	private List<IPredicateApplication> retainPredicateApplications(
			TacticUIRegistry tacticUIRegistry) {
		final List<IPredicateApplication> predApplis = new ArrayList<IPredicateApplication>();
		final List<ITacticApplication> tactics = tacticUIRegistry
				.getTacticApplicationsToHypothesis(userSupport, hyp);
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
	 * Utility method to dispose the compsites and check boxes.
	 */
	public void dispose() {
		if (hypothesisText != null)
			hypothesisText.dispose();

		final Button checkbox = checkBoxHolder.getControl();
		if (!checkbox.isDisposed()) {
			checkbox.removeSelectionListener(listener);
		}
		checkBoxHolder.remove();
		menuHolder.remove();
	}

	/**
	 * Return if the hypothesis is selected or not.
	 * 
	 * @return <code>true</code> if the row is selected, and <code>false</code>
	 *         otherwise
	 */
	public boolean isSelected() {
		final Button checkbox = checkBoxHolder.getControl();
		return checkbox.getSelection();
	}

	/**
	 * Get the contained hypothesis.
	 * 
	 * @return the hypothesis corresponding to this row
	 */
	public Predicate getHypothesis() {
		return hyp;
	}

	void apply(ITacticApplication tacticAppli, boolean skipPostTactic) {
		final String[] inputs = hypothesisText.getResults();
		if (ProverUIUtils.DEBUG)
			for (String input : inputs)
				debug("Input: \"" + input + "\"");

		final String globalInput = this.proverUI.getProofControl().getInput();
		final Set<Predicate> hypSet = Collections.singleton(hyp);
		applyTactic(tacticAppli.getTactic(inputs, globalInput), userSupport,
				hypSet, skipPostTactic, new NullProgressMonitor());
	}

	void apply(ICommandApplication commandAppli) {
		final String[] inputs = hypothesisText.getResults();
		applyCommand(commandAppli.getProofCommand(), userSupport, hyp, inputs,
				new NullProgressMonitor());
	}

	public void setSelected(boolean selected) {
		checkBoxHolder.getControl().setSelection(selected);
	}

	public Control getLeftmostControl() {
		return checkBoxHolder.getControl();
	}

	public int getNbTabsFromLeft() {
		return nbTabsFromLeft;
	}

}