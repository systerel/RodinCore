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
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
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

	private final Color background;

	private final boolean enable;

	private final SelectionListener listener;

	// private final Collection<ImageHyperlink> hyperlinks;

	protected StyledText styledText;

	protected TacticHyperlinkManager manager;

	private ControlHolder<Button> checkBoxHolder2;

	private final int nbTabsFromLeft;

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

		// FIXME why twice the same color?
		if (odd)
			background = EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);
		else
			background = EventBSharedColor.getSystemColor(SWT.COLOR_GRAY);

		final Button checkBox = new Button(styledText, SWT.CHECK);
		checkBox.setSize(15, 15);
		if (ProverUIUtils.DEBUG) {
			checkBox.setBackground(EventBSharedColor
					.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		} else {
			checkBox.setBackground(background);
		}
		//checkBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		checkBox.setEnabled(enable);
		checkBox.addSelectionListener(listener);

		final int checkBoxOffset = styledText.getCharCount() - 2;
		checkBoxHolder = new ControlHolder<Button>(styledText, checkBox, checkBoxOffset);
		checkBoxHolder.attach();
		
		final Button checkbox2 = new Button(styledText, SWT.PUSH);
		final int checkbox2Offset = checkBoxOffset + 1;
		checkBoxHolder2 = new ControlHolder<Button>(styledText, checkbox2, checkbox2Offset);
		checkBoxHolder2.attach();
		
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

		// createImageHyperlinks(buttonComposite);

		createHypothesisText(parsedPredicate, parsedString);

	}

	public void createHypothesisText(Predicate parsedPredicate,
			String parsedString) {
		if (hypothesisText != null)
			hypothesisText.dispose();
		hypothesisText = new EventBPredicateText(this, false, enable, proverUI);
		hypothesisText.append(parsedString, userSupport, hyp, parsedPredicate);
	}

	/*
	 * Creating a null hyperlink
	 */
	private void createNullHyperlinks() {
		// if (ProverUIUtils.DEBUG)
		// debug("Create Null Image");
		// ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
		// SWT.CENTER);
		// hyperlink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		// true));
		//
		// toolkit.adapt(hyperlink, true, true);
		// setHyperlinkImage(hyperlink,
		// EventBImage.getImage(IEventBSharedImages.IMG_NULL));
		// hyperlink.setBackground(background);
		// hyperlink.setEnabled(false);
		// hyperlinks.add(hyperlink);
		// return;
	}

	/**
	 * Utility methods to create image hyperlinks for applicable tactics.
	 * <p>
	 * 
	 */
	private void createImageHyperlinks(Composite parent) {
		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		List<ITacticApplication> tactics = tacticUIRegistry
				.getTacticApplicationsToHypothesis(userSupport, hyp);
		final List<ICommandApplication> commands = tacticUIRegistry
				.getCommandApplicationsToHypothesis(userSupport, hyp);
		if (tactics.isEmpty() && commands.isEmpty()) {
			createNullHyperlinks();
			return;
		}

		for (final ITacticApplication tacticAppli : tactics) {

			if (!(tacticAppli instanceof IPredicateApplication))
				continue;

			final IHyperlinkListener hlListener = new IHyperlinkListener() {

				@Override
				public void linkEntered(HyperlinkEvent e) {
					return;
				}

				@Override
				public void linkExited(HyperlinkEvent e) {
					return;
				}

				@Override
				public void linkActivated(HyperlinkEvent e) {
					apply(tacticAppli,
							tacticUIRegistry.isSkipPostTactic(tacticAppli
									.getTacticID()));
				}
			};
			final IPredicateApplication predAppli = (IPredicateApplication) tacticAppli;
			final Image icon = getIcon(predAppli);
			final String tooltip = getTooltip(predAppli);
			// addHyperlink(buttonComposite, toolkit, SWT.BEGINNING,
			// icon, tooltip, hlListener, enable);
		}

		for (final ICommandApplication commandAppli : commands) {
			final IHyperlinkListener hlListener = new IHyperlinkListener() {

				@Override
				public void linkEntered(HyperlinkEvent e) {
					return;
				}

				@Override
				public void linkExited(HyperlinkEvent e) {
					return;
				}

				@Override
				public void linkActivated(HyperlinkEvent e) {
					apply(commandAppli);
				}

			};
			// addHyperlink(buttonComposite, toolkit, SWT.FILL, commandAppli
			// .getIcon(), commandAppli.getTooltip(), hlListener, enable);
		}

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
		checkBoxHolder2.remove();
		// for (ImageHyperlink hyperlink : hyperlinks)
		// hyperlink.dispose();
		// buttonComposite.dispose();
		// hypothesisComposite.dispose();
	}

	/**
	 * Return if the hypothesis is selected or not.
	 * <p>
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
	 * <p>
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