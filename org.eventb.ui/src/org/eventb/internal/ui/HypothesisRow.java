/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.IUserSupport;
import org.eventb.core.seqprover.Hypothesis;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.internal.ui.prover.EventBPredicateText;
import org.eventb.internal.ui.prover.PredicateUtil;
import org.eventb.internal.ui.prover.ProverUIUtils;
import org.eventb.internal.ui.prover.TacticUIRegistry;
import org.eventb.ui.IEventBSharedImages;
import org.eventb.ui.prover.IProofCommand;
import org.eventb.ui.prover.ITacticProvider;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         A class to create a row containing a hypothesis and the set of proof
 *         buttons which is applicable to the hypothesis
 */
public class HypothesisRow {

	private static final FormulaFactory formulaFactory = FormulaFactory
			.getDefault();

	// Set of composites and button.
	private Button checkBox;

	private Composite buttonComposite;

	private ScrolledForm hypothesisComposite;

	EventBPredicateText hypothesisText;

	// The UserSupport associated with this instance of the editor.
	IUserSupport userSupport;

	// The hypothesis contains in this row.
	Hypothesis hyp;

	// This should be varied when the user resize.
	private int max_length = 30;

	private Color background;

	private boolean enable;

	private String actualString;

	private Predicate parsedPred;

	private FormToolkit toolkit;

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
	public HypothesisRow(FormToolkit toolkit, Composite parent, Hypothesis hyp,
			IUserSupport userSupport, boolean odd, boolean enable) {
		GridData gd;
		this.hyp = hyp;
		this.userSupport = userSupport;
		this.enable = enable;

		this.toolkit = toolkit;
		if (odd)
			background = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		else
			background = new Color(Display.getDefault(), 225, 255, 255);

		buttonComposite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 5;

		buttonComposite.setLayout(layout);
		buttonComposite.setBackground(background);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				false));
		createImageHyperlinks(buttonComposite);

		hypothesisComposite = toolkit.createScrolledForm(parent);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		hypothesisComposite.setLayoutData(gd);
		hypothesisComposite.getBody().setBackground(background);
		hypothesisComposite.getBody().setLayout(new GridLayout());

		Predicate pred = hyp.getPredicate();
		actualString = pred.toString();
		IParseResult parseResult = formulaFactory.parsePredicate(actualString);
		assert parseResult.isSuccess();
		parsedPred = parseResult.getParsedPredicate();

		createHypothesisText();
		checkBox = toolkit.createButton(parent, "", SWT.CHECK);
		checkBox.setBackground(background);
		checkBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		checkBox.setEnabled(enable);

	}

	// private class HypothesisTextLayout extends Layout {
	//
	// @Override
	// protected Point computeSize(Composite composite, int wHint, int hHint,
	// boolean flushCache) {
	// Point size = layout(composite, false, 0, 0, wHint, hHint,
	// flushCache);
	// if (wHint != SWT.DEFAULT)
	// size.x = wHint;
	// if (hHint != SWT.DEFAULT)
	// size.y = hHint;
	// return size;
	// }
	//
	// private Point layout(Composite composite, boolean b, int i, int j,
	// int hint, int hint2, boolean flushCache) {
	// createHypothesisText();
	// return hypothesisText.getMainTextWidget().getSize();
	// }
	//
	// @Override
	// protected void layout(Composite composite, boolean flushCache) {
	// Rectangle rect = composite.getClientArea();
	// layout(composite, true, rect.x, rect.y, rect.width, rect.height,
	// flushCache);
	// }
	//
	// }

	public void createHypothesisText() {
		if (hypothesisText != null)
			hypothesisText.dispose();
		hypothesisText = new EventBPredicateText(toolkit, hypothesisComposite);
		hypothesisText.getMainTextWidget().setBackground(background);
		hypothesisText.getMainTextWidget().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		// int borderWidth =
		// hypothesisText.getMainTextWidget().getBorderWidth();
		// hypothesisText.getMainTextWidget().setText(" ");
		// hypothesisComposite.pack(true);
		// int textWidth = hypothesisText.getMainTextWidget().getSize().x;
		//
		// Rectangle rec = hypothesisComposite.getBounds();
		// Point size = hypothesisComposite.getSize();
		// int compositeWidth = hypothesisComposite.getClientArea().width;
		// if (textWidth != 0) {
		// max_length = (compositeWidth - borderWidth) / textWidth;
		// } else
		// max_length = 30;

		if (enable && parsedPred instanceof QuantifiedPredicate
				&& parsedPred.getTag() == Formula.FORALL) {
			QuantifiedPredicate qpred = (QuantifiedPredicate) parsedPred;
			Collection<Point> indexes = new ArrayList<Point>();

			String string = "\u2200 ";
			BoundIdentDecl[] idents = qpred.getBoundIdentDecls();

			int i = 0;
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = actualString.substring(loc.getStart(), loc
						.getEnd() + 1);
				if (ProverUIUtils.DEBUG)
					ProverUIUtils.debug("Ident: " + image);
				string += " " + image + " ";
				int x = string.length();
				string += "      ";
				int y = string.length();
				indexes.add(new Point(x, y));

				if (++i == idents.length) {
					string += "\u00b7\n";
				} else {
					string += ", ";
				}
			}
			String str = PredicateUtil.prettyPrint(max_length, actualString,
					qpred.getPredicate());
			// SourceLocation loc = qpred.getPredicate().getSourceLocation();
			// String str = actualString.substring(loc.getStart(),
			// loc.getEnd());

			string += str;
			hypothesisText.setText(string, indexes);
		} else {
			String str = PredicateUtil.prettyPrint(max_length, actualString,
					parsedPred);
			// SourceLocation loc = parsedPred.getSourceLocation();
			// String str = actualString.substring(loc.getStart(),
			// loc.getEnd());

			Collection<Point> indexes = new ArrayList<Point>();
			hypothesisText.setText(str, indexes);
		}
		toolkit.paintBordersFor(hypothesisComposite);
	}

	/*
	 * Creating a null hyperlink
	 */
	private void createNullHyperlinks() {
		if (ProverUIUtils.DEBUG)
			ProverUIUtils.debug("Create Null Image");
		ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
				SWT.CENTER);
		hyperlink.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		toolkit.adapt(hyperlink, true, true);
		hyperlink.setImage(EventBImage.getImage(IEventBSharedImages.IMG_NULL));
		hyperlink.setBackground(background);
		hyperlink.setEnabled(false);
		return;
	}

	/**
	 * Utility methods to create image hyperlinks for applicable tactics.
	 * <p>
	 * 
	 */
	private void createImageHyperlinks(Composite parent) {
		final IProofTreeNode node = userSupport.getCurrentPO().getCurrentNode();
		// Collection<HypothesisTacticUI> tactics = ProverUIUtils
		// .getApplicableToHypothesis(node, hyp);

		final TacticUIRegistry tacticUIRegistry = TacticUIRegistry.getDefault();
		String[] tactics = tacticUIRegistry.getApplicableToHypothesis(
				userSupport, hyp);

		if (tactics.length == 0) {
			createNullHyperlinks();
			return;
		}

		for (final String tacticID : tactics) {
			ImageHyperlink hyperlink = new ImageHyperlink(buttonComposite,
					SWT.CENTER);
			hyperlink
					.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			toolkit.adapt(hyperlink, true, true);
			hyperlink.setImage(tacticUIRegistry.getIcon(tacticID));
			hyperlink.addHyperlinkListener(new IHyperlinkListener() {

				public void linkEntered(HyperlinkEvent e) {
					return;
				}

				public void linkExited(HyperlinkEvent e) {
					return;
				}

				public void linkActivated(HyperlinkEvent e) {
					Set<Hypothesis> hypSet = new HashSet<Hypothesis>();
					hypSet.add(hyp);
					String[] inputs = hypothesisText.getResults();
					if (ProverUIUtils.DEBUG)
						for (String input : inputs)
							ProverUIUtils.debug("Input: \"" + input + "\"");

					ITacticProvider provider = tacticUIRegistry
							.getTacticProvider(tacticID);
					if (provider != null)
						userSupport.applyTacticToHypotheses(provider.getTactic(
								node, hyp, inputs), hypSet,
								new NullProgressMonitor());
					else {
						IProofCommand command = tacticUIRegistry
								.getProofCommand(tacticID,
										TacticUIRegistry.TARGET_HYPOTHESIS);
						if (command != null) {
							try {
								command.apply(userSupport, hyp, inputs, new NullProgressMonitor());
							} catch (RodinDBException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}

			});
			hyperlink.setBackground(background);
			hyperlink.setToolTipText(tacticUIRegistry.getTip(tacticID));
			hyperlink.setEnabled(enable);
		}

		return;
	}

	/**
	 * Utility method to dispose the compsites and check boxes.
	 */
	public void dispose() {
		if (hypothesisText != null)
			hypothesisText.dispose();

		checkBox.dispose();
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
	public Hypothesis getHypothesis() {
		return hyp;
	}

}
