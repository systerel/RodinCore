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

	// Check Button holder
	private final ControlHolder<Button> checkBoxHolder;

	private final ProverUI proverUI;

	private EventBPredicateText hypothesisText;

	// The UserSupport associated with this instance of the editor.
	private final IUserSupport userSupport;

	// The hypothesis contains in this row.
	private final Predicate hyp;

	private final boolean enable;

	private final SelectionListener listener;

	// private final Collection<ImageHyperlink> hyperlinks;

	protected StyledText styledText;

	protected TacticHyperlinkManager manager;

	private final int nbTabsFromLeft;

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
		if (ProverUIUtils.DEBUG) {
			checkBox.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		} else {
			checkBox.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_WHITE));
		}
		checkBox.setEnabled(enable);
		checkBox.addSelectionListener(listener);

		final int checkBoxOffset = styledText.getCharCount() - nbTabsFromLeft;
		checkBoxHolder = new ControlHolder<Button>(styledText, checkBox, checkBoxOffset);
		checkBoxHolder.attach();
		
		
		final int menuOffset = checkBoxOffset + 1;
		final Button arrow = new Button(styledText, SWT.ARROW | SWT.DOWN);
		menuHolder = new ControlHolder<Button>(styledText, arrow, menuOffset);
		menuHolder.attach();
		
		final Menu menu = new Menu(arrow);
		
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
		
		arrow.addSelectionListener(arrowListener);
		createImageHyperlinks(menu);
		
//		final int comboOffset = checkBoxOffset + 1;
//		final ImageCombo combo = new ImageCombo(styledText, SWT.DROP_DOWN);
//		comboHolder = new ControlHolder<ImageCombo>(styledText, combo, comboOffset);
//		comboHolder.attach();
		//createImageHyperlinks(combo);
		
		// buttonComposite = toolkit.createComposite(parent);
		// GridLayout layout = new GridLayout();
		// layout.makeColumnsEqualWidth = true;
		// layout.numColumns = 3;
		//
		// buttonComposite.setLayout(layout);
		// if (ProverUIUtils.DEBUG) {
		// buttonComposite.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		// }
		// else {
		// buttonComposite.setBackground(background);
		// }
		// buttonComposite.setLayoutData(new GridData(SWT.FILL,
		// SWT.FILL, false, false));
		// hyperlinks = new ArrayList<ImageHyperlink>();

		// hypothesisComposite = toolkit.createComposite(parent);
		// gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		// hypothesisComposite.setLayoutData(gd);
		// if (ProverUIUtils.DEBUG) {
		// hypothesisComposite.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		// }
		// else {
		// hypothesisComposite.setBackground(background);
		// }
		// hypothesisComposite.setLayout(new GridLayout());
		// EventBEditorUtils.changeFocusWhenDispose(hypothesisComposite,
		// styledText);

		final String parsedString = hyp.toString();
		// Predicate containing the SourceLocations
		final FormulaFactory ff = userSupport.getFormulaFactory();
		final Predicate parsedPredicate = getParsed(parsedString, ff);


		createHypothesisText(parsedPredicate, parsedString);

	}

	public void createHypothesisText(Predicate parsedPredicate,
			String parsedString) {
		if (hypothesisText != null)
			hypothesisText.dispose();
		hypothesisText = new EventBPredicateText(this, false, enable, proverUI);
		hypothesisText.append(parsedString, userSupport, hyp, parsedPredicate);
	}

	/**
	 * Utility methods to create image hyperlinks for applicable tactics.
	 */
	private void createImageHyperlinks(Menu menu) {
		if (! enable) {
			return;
		}
		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		final List<ITacticApplication> tactics = tacticUIRegistry
				.getTacticApplicationsToHypothesis(userSupport, hyp);
		final List<ICommandApplication> commands = tacticUIRegistry
				.getCommandApplicationsToHypothesis(userSupport, hyp);
		if (tactics.isEmpty() && commands.isEmpty()) {
			// createNullHyperlinks();
			return;
		}

		for (final ITacticApplication tacticAppli : tactics) {

			if (!(tacticAppli instanceof IPredicateApplication))
				continue;

			final SelectionListener hlListener = new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					apply(tacticAppli,
							tacticUIRegistry.isSkipPostTactic(tacticAppli
									.getTacticID()));
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			};
			final IPredicateApplication predAppli = (IPredicateApplication) tacticAppli;
			final Image icon = getIcon(predAppli);
			final String tooltip = getTooltip(predAppli);
			addMenuItem(menu, icon, tooltip, enable, hlListener);
		}

		for (final ICommandApplication commandAppli : commands) {
			final SelectionListener hlListener = new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					apply(commandAppli);
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
			};
			addMenuItem(menu, commandAppli.getIcon(),
					commandAppli.getTooltip(), enable, hlListener);
		}

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