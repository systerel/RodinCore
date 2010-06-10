/*******************************************************************************
 * Copyright (c) 2006, 2010 ETH Zurich and others.
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

import static org.eventb.internal.ui.EventBUtils.setHyperlinkImage;
import static org.eventb.internal.ui.prover.ProverUIUtils.applyCommand;
import static org.eventb.internal.ui.prover.ProverUIUtils.applyTactic;
import static org.eventb.internal.ui.prover.ProverUIUtils.getHyperlinks;
import static org.eventb.internal.ui.prover.ProverUIUtils.getParsed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.IUserSupport;
import org.eventb.internal.ui.EventBImage;
import org.eventb.internal.ui.EventBSharedColor;
import org.eventb.internal.ui.eventbeditor.EventBEditorUtils;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.prover.IPredicateApplication;
import org.eventb.ui.prover.ITacticApplication;

/**
 * @author htson
 *         <p>
 *         A class to create a row containing a hypothesis and the set of proof
 *         buttons which is applicable to the hypothesis
 */
public class HypothesisRow {

	// Set of composites and button.
	private final Button checkBox;

	private final Composite buttonComposite;

	private final Composite hypothesisComposite;

	private final ProverUI proverUI;
	
	private EventBPredicateText hypothesisText;

	// The UserSupport associated with this instance of the editor.
	private final IUserSupport userSupport;

	// The hypothesis contains in this row.
	private final Predicate hyp;

	// This should be varied when the user resize.
	private static final int max_length = 30;

	private final Color background;

	private final boolean enable;

	private final FormToolkit toolkit;

	private final SelectionListener listener;
	
	private final ScrolledForm scrolledForm;
	
	private final Collection<ImageHyperlink> hyperlinks;

	
	/**
	 * @author htson
	 *         <p>
	 *         This class extends HyperlinkAdapter and provide response actions
	 *         when a hyperlink is activated.
	 */
	/**
	 * Constructor.
	 * 
	 * @param toolkit
	 *            The Form Toolkit to create this row
	 * @param parent
	 *            The composite parent
	 */
	public HypothesisRow(FormToolkit toolkit, Composite parent, Predicate hyp,
			IUserSupport userSupport, boolean odd,
			boolean enable,
			SelectionListener listener, ProverUI proverUI, ScrolledForm scrolledForm) {
		GridData gd;
		this.hyp = hyp;
		this.scrolledForm = scrolledForm;
		this.listener = listener;
		this.userSupport = userSupport;
		this.enable = enable;
		this.proverUI = proverUI;

		this.toolkit = toolkit;
		// FIXME why twice the same color?
		if (odd)
			background = EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);
		else
			background = EventBSharedColor.getSystemColor(SWT.COLOR_WHITE);

		checkBox = toolkit.createButton(parent, "", SWT.CHECK);
		if (ProverUIUtils.DEBUG) {
			checkBox.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		}
		else {
			checkBox.setBackground(background);
		}
		checkBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
				false, false));
		checkBox.setEnabled(enable);
		checkBox.addSelectionListener(listener);
		
		buttonComposite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 3;

		buttonComposite.setLayout(layout);
		if (ProverUIUtils.DEBUG) {
			buttonComposite.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		}
		else {
			buttonComposite.setBackground(background);
		}
		buttonComposite.setLayoutData(new GridData(SWT.FILL,
				SWT.FILL, false, false));
		hyperlinks = new ArrayList<ImageHyperlink>();

		hypothesisComposite = toolkit.createComposite(parent);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		hypothesisComposite.setLayoutData(gd);
		if (ProverUIUtils.DEBUG) {
			hypothesisComposite.setBackground(EventBSharedColor.getSystemColor(SWT.COLOR_DARK_MAGENTA));
		}
		else {
			hypothesisComposite.setBackground(background);		
		}
		hypothesisComposite.setLayout(new GridLayout());
		EventBEditorUtils.changeFocusWhenDispose(hypothesisComposite,
				scrolledForm.getParent());

		final String parsedString = hyp.toString();

		//Predicate containing the SourceLocations
		final Predicate parsedPredicate = getParsed(parsedString); 
						
		createImageHyperlinks(buttonComposite);
		
		createHypothesisText(parsedPredicate, parsedString);

	}

	public void createHypothesisText(Predicate parsedPredicate, String parsedString) {
		if (hypothesisText != null)
			hypothesisText.dispose();
		hypothesisText = new EventBPredicateText(false, toolkit,
				hypothesisComposite, proverUI, scrolledForm);
		StyledText textWidget = hypothesisText.getMainTextWidget();
		textWidget.setBackground(background);
		textWidget.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (enable && parsedPredicate.getTag() == Formula.FORALL) {
			QuantifiedPredicate qpred = (QuantifiedPredicate) parsedPredicate;

			String string = "\u2200 ";
			BoundIdentDecl[] idents = qpred.getBoundIdentDecls();
			int[] indexes = new int[idents.length];

			int i = 0;
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = parsedString.substring(loc.getStart(), loc
						.getEnd() + 1);
				ProverUIUtils.debug("Ident: " + image);
				string += " " + image + " ";
				int x = string.length();
				string += " ";
				indexes[i] = x;

				if (++i == idents.length) {
					string += "\u00b7\n";
				} else {
					string += ", ";
				}
			}
			String str = PredicateUtil.prettyPrint(max_length, parsedString,
					qpred.getPredicate());

			string += str;

			final Map<Point, List<ITacticApplication>> links = getHyperlinks(
					userSupport, true, string, hyp);
					hypothesisText.setText(string, userSupport, hyp, indexes, links);
		} else {
			String str = PredicateUtil.prettyPrint(max_length, parsedString,
					parsedPredicate);

			int[] indexes = new int[0];

			final Map<Point, List<ITacticApplication>> links;

			if (enable) {
				links = getHyperlinks(userSupport, true, str, hyp);
			} else {
				links = Collections.emptyMap();
			}
			hypothesisText.setText(str, userSupport, hyp, indexes, links);
		}
		toolkit.paintBordersFor(hypothesisComposite);
	}

	/*
	 * Creating a null hyperlink
	 */
	private void createNullHyperlinks() {
		ProverUIUtils.debug("Create Null Image");
		ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
				SWT.CENTER);
		hyperlink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		toolkit.adapt(hyperlink, true, true);
		setHyperlinkImage(hyperlink, EventBImage.getImage(IEventBSharedImages.IMG_NULL));
		hyperlink.setBackground(background);
		hyperlink.setEnabled(false);
		hyperlinks.add(hyperlink);
		return;
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

				public void linkEntered(HyperlinkEvent e) {
					return;
				}

				public void linkExited(HyperlinkEvent e) {
					return;
				}

				public void linkActivated(HyperlinkEvent e) {
					apply(tacticAppli, tacticUIRegistry
							.isSkipPostTactic(tacticAppli.getTacticID()));
				}
			};
			final IPredicateApplication predAppli = (IPredicateApplication) tacticAppli;
			final Image icon = ProverUIUtils.getIcon(predAppli);
			final String tooltip = ProverUIUtils.getTooltip(predAppli);
			ProverUIUtils.addHyperlink(buttonComposite, toolkit, SWT.BEGINNING,
					icon, tooltip, hlListener, enable);
		}
		
		for (final ICommandApplication commandAppli : commands) {
			final IHyperlinkListener hlListener = new IHyperlinkListener() {

				public void linkEntered(HyperlinkEvent e) {
					return;
				}

				public void linkExited(HyperlinkEvent e) {
					return;
				}

				public void linkActivated(HyperlinkEvent e) {
					apply(commandAppli);
				}

			};
			ProverUIUtils.addHyperlink(buttonComposite, toolkit, SWT.FILL, commandAppli
					.getIcon(), commandAppli.getTooltip(), hlListener, enable);
		}

	}

	/**
	 * Utility method to dispose the compsites and check boxes.
	 */
	public void dispose() {
//		if (hypothesisText != null)
//			hypothesisText.dispose();

		if (!checkBox.isDisposed()) {
			checkBox.removeSelectionListener(listener);
			checkBox.dispose();
		}
		for (ImageHyperlink hyperlink : hyperlinks)
			hyperlink.dispose();
		buttonComposite.dispose();
		hypothesisComposite.dispose();
	}

	/**
	 * Return if the hypothesis is selected or not.
	 * <p>
	 * 
	 * @return <code>true</code> if the row is selected, and
	 *         <code>false</code> otherwise
	 */
	public boolean isSelected() {
		return checkBox.getSelection();
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
				ProverUIUtils.debug("Input: \"" + input + "\"");

		final String globalInput = this.proverUI.getProofControl().getInput();
		final Set<Predicate> hypSet = Collections.singleton(hyp);
		applyTactic(tacticAppli.getTactic(inputs, globalInput),
				userSupport, hypSet, skipPostTactic, new NullProgressMonitor());
	}

	void apply(ICommandApplication commandAppli) {
		final String[] inputs = hypothesisText.getResults();
		applyCommand(commandAppli.getProofCommand(), userSupport, hyp, inputs, new NullProgressMonitor());
	}

	public void setSelected(boolean selected) {
		checkBox.setSelection(selected);
	}

}