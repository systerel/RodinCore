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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.sequent.Hypothesis;
import org.eventb.eventBKeyboard.EventBStyledTextModifyListener;
import org.eventb.internal.ui.IEventBFormText;
import org.eventb.internal.ui.IEventBInputText;
import org.eventb.internal.ui.prover.hypothesisTactics.HypothesisTacticUI;

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

	private ScrolledForm hypothesisComposite;

	private List<IEventBInputText> textBoxes;

	private IEventBInputText hypothesisText;

	EventBPredicateText predText;
	
	// The UserSupport associated with this instance of the editor.
	private UserSupport userSupport;

	// The hypothesis contains in this row.
	private Hypothesis hyp;

	private Collection<IEventBInputText> labelTexts;

	private IEventBFormText form;

	/**
	 * @author htson
	 *         <p>
	 *         This class extends HyperlinkAdapter and provide response actions
	 *         when a hyperlink is activated.
	 */
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
	public HypothesisRow(SectionPart part, Composite parent, Hypothesis hyp,
			UserSupport userSupport, boolean odd, boolean enable) {
		GridData gd;
		this.hyp = hyp;
		this.userSupport = userSupport;

		FormToolkit toolkit = part.getManagedForm().getToolkit();
		Color background;
		if (odd)
			background = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
		else
			background = part.getSection().getTitleBarGradientBackground();

		buttonComposite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 5;

		labelTexts = new ArrayList<IEventBInputText>();

		buttonComposite.setLayout(layout);
		buttonComposite.setBackground(background);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				true));
		createHyperlinks(toolkit, buttonComposite, background, enable);

		if (hypothesisComposite == null) {
			hypothesisComposite = toolkit.createScrolledForm(parent);
			gd = new GridData(GridData.FILL_BOTH);
			hypothesisComposite.setLayoutData(gd);
			// hypothesisComposite.setLayout(new GridLayout());
			hypothesisComposite.getBody().setBackground(background);
		}
		textBoxes = new ArrayList<IEventBInputText>();

		Predicate pred = hyp.getPredicate();
		String predString = pred.toString();
		IParseResult parseResult = Lib.ff.parsePredicate(predString);
		assert parseResult.isSuccess();
		Predicate parsedPred = parseResult.getParsedPredicate();

		if (parsedPred instanceof QuantifiedPredicate
				&& parsedPred.getTag() == Formula.FORALL) {
			QuantifiedPredicate qpred = (QuantifiedPredicate) parsedPred;
			Collection<Point> indexes = new ArrayList<Point>();

			String string = "\u2200\n";
			BoundIdentDecl[] idents = qpred.getBoundIdentDecls();

			// GridLayout gl = new GridLayout();
			// gl.numColumns = idents.length * 2 + 2;
			// hypothesisComposite.setLayout(gl);

			// IEventBInputText text = new EventBMath(toolkit.createText(
			// hypothesisComposite, "\u2200", SWT.READ_ONLY));
			// text.getTextWidget().setBackground(background);

			// labelTexts.add(text);

			int i = 0;
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = predString.substring(loc.getStart(), loc
						.getEnd());
				ProverUIUtils.debugProverUI("Ident: " + image);
				string += "  " + image + "  ";
				// text = new EventBMath(toolkit.createText(
				// hypothesisComposite, ", " + image, SWT.READ_ONLY));
				// text.getTextWidget().setBackground(background);
				// labelTexts.add(text);
				// } else {
				// text = new EventBMath(toolkit.createText(
				// hypothesisComposite, image, SWT.READ_ONLY));
				// text.getTextWidget().setBackground(background);
				// labelTexts.add(text);
				// }
				int x = string.length();
				string += "      ";
				int y = string.length();
				indexes.add(new Point(x, y));

				if (++i == idents.length) {
					string += "\n";
				} else {
					string += "  ,\n";
				}
				// Text box = toolkit.createText(hypothesisComposite, "");

				// gd = new GridData();
				// gd.widthHint = 15;
				// box.setLayoutData(gd);
				// box.setBackground(background);
				// textBoxes.add(new EventBMath(box));
			}

			SourceLocation loc = qpred.getPredicate().getSourceLocation();
			String image = predString.substring(loc.getStart(), loc.getEnd());
			ProverUIUtils.debugProverUI("Pred: " + image);
			string += "\u00b7\n";
			string += "  " + image;
			predText = new EventBPredicateText(toolkit,
					hypothesisComposite, string, indexes);
			predText.getMainTextWidget().setBackground(background);
			predText.getMainTextWidget().addModifyListener(
					new EventBStyledTextModifyListener());
		} else {

			Collection<Point> indexes = new ArrayList<Point>();
			// indexes.add(new Point(2, 6));
			// indexes.add(new Point(14, 20));
			predText = new EventBPredicateText(toolkit,
					hypothesisComposite, hyp.getPredicate().toString(), indexes);
			// hypothesisText = new EventBMath(toolkit.createText(
			// hypothesisComposite.getBody(), hyp.toString(),
			// SWT.READ_ONLY));

			// gd = new GridData(GridData.FILL_HORIZONTAL);
			// hypothesisText.getTextWidget().setLayoutData(gd);
			predText.getMainTextWidget().setBackground(background);

			// Rectangle bounds = hypothesisComposite.getClientArea();
			// hypothesisText.getTextWidget().setBounds(bounds.x + inset,
			// bounds.y + inset, bounds.width - inset * 2,
			// bounds.height - inset * 2);

		}
		toolkit.paintBordersFor(hypothesisComposite);

		checkBox = toolkit.createButton(parent, "", SWT.CHECK);
		checkBox.setBackground(background);
		checkBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		checkBox.setEnabled(enable);
	}

	/**
	 * Utility methods to create hyperlinks for applicable tactics.
	 * <p>
	 * 
	 * @param formText
	 *            the formText parent of these hyperlinks
	 */
	private void createHyperlinks(FormToolkit toolkit, Composite parent,
			Color background, boolean enable) {
		final IProofTreeNode node = userSupport.getCurrentPO().getCurrentNode();
		Collection<HypothesisTacticUI> tactics = ProverUIUtils
				.getApplicableToHypothesis(node, hyp);

		for (final HypothesisTacticUI tactic : tactics) {
			ImageHyperlink ds = new ImageHyperlink(buttonComposite, SWT.CENTER);
			ds.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			toolkit.adapt(ds, true, true);
			ds.setImage(tactic.getImage());
			ds.addHyperlinkListener(new IHyperlinkListener() {

				public void linkEntered(HyperlinkEvent e) {
					return;
				}

				public void linkExited(HyperlinkEvent e) {
					return;
				}

				public void linkActivated(HyperlinkEvent e) {
					Set<Hypothesis> hypSet = new HashSet<Hypothesis>();
					hypSet.add(hyp);
					String[] inputs = predText.getResults();
//					int i = 0;
					for (String input : inputs) {
						ProverUIUtils.debugProverUI("Input: \"" + input+"\"");
					}
					userSupport.applyTacticToHypotheses(tactic.getTactic(node,
							hyp, inputs), hypSet);
				}

			});
			ds.setBackground(background);
			ds.setToolTipText(tactic.getHint());
			ds.setEnabled(enable);
		}

		// for (Iterator<String> it = tactics.iterator(); it.hasNext();) {
		// String t = it.next();
		// ImageHyperlink ds = new ImageHyperlink(buttonComposite, SWT.CENTER);
		// ds.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// toolkit.adapt(ds, true, true);
		// ImageRegistry registry = EventBUIPlugin.getDefault()
		// .getImageRegistry();
		// ds.setImage(registry.get(EventBImage.IMG_PENDING));
		// // ds.addHyperlinkListener(new CachedHyperlinkAdapter());
		// ds.setBackground(background);
		// ds.setToolTipText("Deselect checked hypotheses");
		// }

		return;
	}

	/**
	 * Utility method to dispose the compsites and check boxes.
	 */
	public void dispose() {
		// if (formText != null)
		// formText.dispose();
		if (form != null)
			form.dispose();
		if (textBoxes != null)
			for (IEventBInputText text : textBoxes)
				text.dispose();
		if (hypothesisText != null)
			hypothesisText.dispose();
		if (labelTexts != null)
			for (IEventBInputText text : labelTexts)
				text.dispose();

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
