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
import static org.eventb.internal.ui.prover.ProverUIUtils.debug;
import static org.eventb.internal.ui.prover.ProverUIUtils.getParsed;

import java.util.Collections;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author htson
 *         <p>
 *         A class to create a row containing a predicate and the set of proof
 *         buttons which is applicable to the predicate
 *         </p>
 */
public class PredicateRow {

	private static final Color WHITE = EventBSharedColor
			.getSystemColor(SWT.COLOR_WHITE);

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

	private boolean isGoal;

	private final ControlPainter controlPainter;
	private final ControlMaker checkBoxMaker;
	private final ControlMaker appliMaker;
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
			TacticHyperlinkManager manager, ControlPainter controlPainter,
			ControlMaker checkboxMaker, ControlMaker appliMaker, YellowBoxMaker yellowBoxMaker) {

		this.pred = pred;
		this.isGoal = isGoal;
		this.checkboxListener = listener;
		this.userSupport = userSupport;
		this.enable = enable;
		this.proverUI = proverUI;
		this.manager = manager;
		this.nbTabsFromLeft = nbTabsFromLeft;
		this.controlPainter = controlPainter;
		this.checkBoxMaker = checkboxMaker;
		this.appliMaker = appliMaker;
		this.yellowBoxMaker = yellowBoxMaker;

		final FormulaFactory ff = userSupport.getFormulaFactory();
		final String parsedString = pred.toString();		
		// Predicate containing the SourceLocations
		this.parsedPredicate = getParsed(parsedString, ff);
		
		createPredicateText();
	}

	public ControlPainter getControlPainter() {
		return controlPainter;
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
		predAppliHolder.attach(true);
		predicateText.attach();
	}
	
	private void createControlButtons(boolean odd) {
		final int checkBoxOffset = manager.getCurrentOffset() - nbTabsFromLeft;
		final Color bgColor = odd ? SOFT_BG_COLOR : WHITE;
		if (!isGoal) {
			checkBoxHolder = new ControlHolder(this, checkBoxMaker,
					checkBoxOffset, false, bgColor);
			checkBoxHolder.addSelectionListener(checkboxListener);
		}
		final int menuOffset = checkBoxOffset + 1;
		predAppliHolder = new ControlHolder(this, appliMaker, menuOffset,
				false, bgColor);
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
	 * Tells if the hypothesis is selected or not.
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
	 * Applies the given tactic application to the predicate held by this row.
	 * 
	 * @param tacticAppli
	 *            the tactic application
	 * @param skipPostTactic
	 *            the boolean telling if the post tactics should be skiped
	 */
	protected void apply(ITacticApplication tacticAppli, boolean skipPostTactic) {
		final String[] inputs = getPredicateText().getResults();
		if (ProverUIUtils.DEBUG)
			for (String input : inputs)
				debug("Input: \"" + input + "\"");

		final String globalInput = getProverUI().getProofControl().getInput();
		final IUserSupport us = getUserSupport();
		if (isGoal()) {
			ProverUIUtils.applyTactic(
					tacticAppli.getTactic(inputs, globalInput), us, null,
					skipPostTactic, new NullProgressMonitor());
			return;
		}
		final Set<Predicate> hypSet = Collections.singleton(getPredicate());
		ProverUIUtils.applyTactic(tacticAppli.getTactic(inputs, globalInput),
				us, hypSet, skipPostTactic, new NullProgressMonitor());
	}

	/**
	 * Applies the command to the predicate held by this row.
	 * 
	 * @param commandAppli
	 *            the command application
	 */
	protected void apply(ICommandApplication commandAppli) {
		final String[] inputs = getPredicateText().getResults();
		final IUserSupport us = getUserSupport();
		if (isGoal()) {
			ProverUIUtils.applyCommand(commandAppli.getProofCommand(), us,
					null, inputs, new NullProgressMonitor());
			return;
		}
		ProverUIUtils.applyCommand(commandAppli.getProofCommand(), us,
				getPredicate(), inputs, new NullProgressMonitor());
	}

	/**
	 * Get the contained predicate.
	 * 
	 * @return the predicate corresponding to this row
	 */
	public Predicate getPredicate() {
		return pred;
	}
	
	/**
	 * Tells if the predicate row appears activated (i.e. the current proof tree
	 * node is open)
	 * 
	 * @return <code>true</code> if the current proof tree node where this
	 *         predicate comes from is open, <code>false</code> otherwise
	 */
	public boolean isEnabled() {
		return enable;
	}
	
	/**
	 * Tells if the predicate held by this row is the goal of a sequent.
	 * 
	 * @return <code>true</code> if the current the predicate of this row is a
	 *         goal, <code>false</code> otherwise
	 */
	public boolean isGoal() {
		return isGoal;
	}
	
	/**
	 * Returns the user support from which the predicate has been computed.
	 * 
	 * @return the current user support
	 */
	public IUserSupport getUserSupport() {
		return userSupport;
	}

	/**
	 * Selects this row by checking the checkbox button. The checkbox button is
	 * rendered (i.e. created and packed) if it has not yet been created.
	 * 
	 * @param selected
	 *            the selection
	 */
	public void setSelected(boolean selected) {
		if (!enable || checkBoxHolder == null)
			return;
		checkBoxHolder.render();
		final Button checkbox = (Button) checkBoxHolder.getControl();
		checkbox.setSelection(selected);
	}
	
	/**
	 * Returns the number of tabulations and controls that are making a left
	 * margin.
	 * 
	 * @return the number of tabulations and controls in the left margin
	 */
	public int getNbTabsFromLeft() {
		return nbTabsFromLeft;
	}
	
	/**
	 * Returns the predicate text object of this row.
	 * 
	 * @return the pretty print and display object of the predicate held by this
	 *         row
	 */
	public EventBPredicateText getPredicateText() {
		return predicateText;
	}
	
	/**
	 * Returns the parent prover UI.
	 * 
	 * @return the parent prover UI
	 */
	public ProverUI getProverUI() {
		return proverUI;
	}

}