/*******************************************************************************
 * Copyright (c) 2005 ETH Zurich.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Rodin @ ETH Zurich
 ******************************************************************************/

package org.eventb.internal.ui.prover;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.HyperlinkSettings;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.prover.Lib;
import org.eventb.core.prover.sequent.Hypothesis;
import org.eventb.core.prover.tactics.Tactics;
import org.eventb.internal.ui.EventBFormText;
import org.eventb.internal.ui.EventBMath;
import org.eventb.internal.ui.EventBUIPlugin;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.UIUtils;
import org.rodinp.core.RodinDBException;

/**
 * @author htson
 *         <p>
 *         An abstract class to create an input row (a label and a text field)
 *         for editing Rodin elements (e.g. name, content, attribute, etc.).
 */
public class HypothesisRow {

	// Set of composites and button.
	private Button checkBox;

	private Composite buttonComposite;

	private Composite hypothesisComposite;

	private List<Text> textBoxes;

	// The UserSupport associated with this instance of the editor.
	private UserSupport userSupport;

	// The hypothesis contains in this row.
	private Hypothesis hyp;

	/**
	 * @author htson
	 *         <p>
	 *         This class extends HyperlinkAdapter and provide response actions
	 *         when a hyperlink is activated.
	 */
	private class HypothesisITacticHyperlinkAdapter extends HyperlinkAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.forms.events.IHyperlinkListener#linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent)
		 */
		public void linkActivated(HyperlinkEvent e) {
			try {
				Set<Hypothesis> hypSet = new HashSet<Hypothesis>();
				hypSet.add(hyp);
				if (e.getHref().equals(UIUtils.ALLF_SYMBOL)) {
					String[] inputs = new String[textBoxes.size()];
					int i = 0;
					for (Text text : textBoxes) {
						inputs[i++] = text.getText();
					}
					userSupport.applyTacticToHypotheses(Tactics.allF(hyp,
							inputs), hypSet);
					return;
				}
				if (e.getHref().equals(UIUtils.CONJD_SYMBOL)) {
					userSupport.applyTacticToHypotheses(Tactics.conjD(hyp),
							hypSet);
					return;
				}
				if (e.getHref().equals(UIUtils.IMPD1_SYMBOL)) {
					userSupport.applyTacticToHypotheses(Tactics
							.impD(hyp, false), hypSet);
					return;
				}
				if (e.getHref().equals(UIUtils.IMPD2_SYMBOL)) {
					userSupport.applyTacticToHypotheses(
							Tactics.impD(hyp, true), hypSet);
					return;
				}
				if (e.getHref().equals(UIUtils.DISJE_SYMBOL)) {
					userSupport.applyTacticToHypotheses(Tactics.disjE(hyp),
							hypSet);
					return;
				}
				if (e.getHref().equals(UIUtils.EXF_SYMBOL)) {
					userSupport.applyTacticToHypotheses(Tactics.exF(hyp),
							hypSet);
					return;
				}
				if (e.getHref().equals(UIUtils.EQE1_SYMBOL)) {
					userSupport.applyTacticToHypotheses(
							Tactics.eqE(hyp, false), hypSet);
					return;
				}
				if (e.getHref().equals(UIUtils.EQE2_SYMBOL)) {
					userSupport.applyTacticToHypotheses(Tactics.eqE(hyp, true),
							hypSet);
					return;
				}
				if (e.getHref().equals(UIUtils.FALSIFY_SYMBOL)) {
					userSupport.applyTacticToHypotheses(
							Tactics.falsifyHyp(hyp), hypSet);
					return;
				}
				if (e.getHref().equals(UIUtils.NEG_SYMBOL)) {
					userSupport.applyTacticToHypotheses(Tactics
							.removeNegHyp(hyp), hypSet);
					return;
				}
			} catch (RodinDBException exception) {
				exception.printStackTrace();
			}
		}

	}

	/**
	 * Constructor.
	 * 
	 * @param page
	 *            The detail page
	 * @param toolkit
	 *            The Form Toolkit to create this row
	 * @param parent
	 *            The composite parent
	 * @param label
	 *            The label of the input row
	 * @param tip
	 *            The tip for the input row
	 * @param style
	 *            The style
	 */
	public HypothesisRow(FormToolkit toolkit, Composite parent, Hypothesis hyp,
			UserSupport userSupport) {
		GridData gd;
		this.hyp = hyp;
		this.userSupport = userSupport;

		checkBox = toolkit.createButton(parent, "", SWT.CHECK);

		buttonComposite = toolkit.createComposite(parent);
		buttonComposite.setLayout(new GridLayout());
		gd = new GridData();

		IEventBFormText formText = new EventBFormText(toolkit.createFormText(
				buttonComposite, true));
		gd = new GridData();
		gd.widthHint = 25;
		gd.horizontalAlignment = SWT.CENTER;
		FormText ft = formText.getFormText();
		ft.setLayoutData(gd);
		HyperlinkSettings hyperlinkSettings = new HyperlinkSettings(
				EventBUIPlugin.getActiveWorkbenchWindow().getWorkbench()
						.getDisplay());
		hyperlinkSettings
				.setHyperlinkUnderlineMode(HyperlinkSettings.UNDERLINE_HOVER);
		ft.setHyperlinkSettings(hyperlinkSettings);
		ft.addHyperlinkListener(new HypothesisITacticHyperlinkAdapter());
		gd = new GridData(SWT.FILL, SWT.FILL, false, false);
		gd.widthHint = 100;
		ft.setLayoutData(gd);
		toolkit.paintBordersFor(buttonComposite);
		createHyperlinks(ft);

		if (hypothesisComposite == null) {
			hypothesisComposite = toolkit.createComposite(parent);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			hypothesisComposite.setLayoutData(gd);
			hypothesisComposite.setLayout(new GridLayout());
		}

		if (Lib.isUnivQuant(hyp.getPredicate())) {
			Predicate pred = hyp.getPredicate();
			String goalString = pred.toString();
			IParseResult parseResult = Lib.ff.parsePredicate(goalString);
			assert parseResult.isSuccess();
			QuantifiedPredicate qpred = (QuantifiedPredicate) parseResult
					.getParsedPredicate();

			BoundIdentDecl[] idents = qpred.getBoundIdentDecls();

			GridLayout gl = new GridLayout();
			gl.numColumns = idents.length * 2 + 2;
			hypothesisComposite.setLayout(gl);

			toolkit.createLabel(hypothesisComposite, "\u2200 ");

			int i = 0;
			textBoxes = new ArrayList<Text>();
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = goalString.substring(loc.getStart(), loc
						.getEnd());
				UIUtils.debugProverUI("Ident: " + image);
				if (i++ != 0)
					toolkit.createLabel(hypothesisComposite, ", " + image);
				else
					toolkit.createLabel(hypothesisComposite, image);
				Text box = toolkit.createText(hypothesisComposite, "");
				gd = new GridData();
				gd.widthHint = 15;
				box.setLayoutData(gd);
				toolkit.paintBordersFor(hypothesisComposite);
				textBoxes.add(box);
			}

			IEventBFormText form = new EventBFormText(toolkit.createFormText(
					hypothesisComposite, false));
			gd = new GridData(SWT.FILL, SWT.FILL, true, false);
			form.getFormText().setLayoutData(gd);
			SourceLocation loc = qpred.getPredicate().getSourceLocation();
			String image = goalString.substring(loc.getStart(), loc.getEnd());
			UIUtils.debugProverUI("Pred: " + image);
			form.getFormText().setText(
					"<form><p>" + UIUtils.XMLWrapUp(image) + "</p></form>",
					true, false);
		} else {
			Text hypothesisText = toolkit.createText(hypothesisComposite, hyp
					.toString(), SWT.READ_ONLY);
			new EventBMath(hypothesisText);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			hypothesisText.setLayoutData(gd);
		}
	}

	/**
	 * Utility methods to create hyperlinks for applicable tactics.
	 * <p>
	 * 
	 * @param formText
	 *            the formText parent of these hyperlinks
	 */
	private void createHyperlinks(FormText formText) {
		String formString = "<form><li style=\"text\" value=\"\">";

		List<String> tactics = UIUtils.getApplicableToHypothesis(hyp);
		for (Iterator<String> it = tactics.iterator(); it.hasNext();) {
			String t = it.next();
			UIUtils.debugProverUI("Create tactic for " + t);
			formString = formString + "<a href=\"" + UIUtils.XMLWrapUp(t)
					+ "\">" + UIUtils.XMLWrapUp(t) + "</a> ";
		}

		formString = formString + "</li></form>";
		formText.setText(formString, true, false);
		formText.redraw();
		buttonComposite.pack(true);

		return;
	}

	/**
	 * Utility method to dispose the compsites and check boxes.
	 */
	public void dispose() {
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
	protected boolean isSelected() {
		return checkBox.getSelection();
	}

	/**
	 * Get the contained hypothesis.
	 * <p>
	 * 
	 * @return the hypothesis corresponding to this row
	 */
	protected Hypothesis getHypothesis() {
		return hyp;
	}

}
