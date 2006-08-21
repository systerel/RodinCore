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
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.SourceLocation;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.pm.UserSupport;
import org.eventb.core.seqprover.IProofTreeNode;
import org.eventb.core.seqprover.Lib;
import org.eventb.core.seqprover.sequent.Hypothesis;
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

	EventBPredicateText hypothesisText;

	// The UserSupport associated with this instance of the editor.
	private UserSupport userSupport;

	// The hypothesis contains in this row.
	private Hypothesis hyp;

	// This should be varied when the user resize.
	private int max_length = 25;

	private static final int tab = 2;

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
		this.enable = enable;

		toolkit = part.getManagedForm().getToolkit();
		if (odd)
			background = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
		else
			background = Display.getDefault().getSystemColor(
					SWT.COLOR_TITLE_BACKGROUND_GRADIENT);

		buttonComposite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 5;

		buttonComposite.setLayout(layout);
		buttonComposite.setBackground(background);
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
				true));
		createImageHyperlinks(buttonComposite, background, enable);

		hypothesisComposite = toolkit.createScrolledForm(parent);
		gd = new GridData(GridData.FILL_BOTH);
		hypothesisComposite.setLayoutData(gd);
		hypothesisComposite.getBody().setBackground(background);
		hypothesisComposite.getBody().setLayout(new GridLayout());

		Predicate pred = hyp.getPredicate();
		actualString = pred.toString();
		IParseResult parseResult = Lib.ff.parsePredicate(actualString);
		assert parseResult.isSuccess();
		parsedPred = parseResult.getParsedPredicate();


		createHypothesisText();
		checkBox = toolkit.createButton(parent, "", SWT.CHECK);
		checkBox.setBackground(background);
		checkBox.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		checkBox.setEnabled(enable);

	}
	
	private class HypothesisTextLayout extends Layout {

		@Override
		protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
			Point size = layout (composite, false, 0, 0, wHint, hHint, flushCache);
			if (wHint != SWT.DEFAULT) size.x = wHint;
			if (hHint != SWT.DEFAULT) size.y = hHint;
			return size;
		}

		private Point layout(Composite composite, boolean b, int i, int j, int hint, int hint2, boolean flushCache) {
			createHypothesisText();
			return hypothesisText.getMainTextWidget().getSize();
		}

		@Override
		protected void layout (Composite composite, boolean flushCache) {
			Rectangle rect = composite.getClientArea ();
			layout (composite, true, rect.x, rect.y, rect.width, rect.height, flushCache);
		}
		
	}

	public void createHypothesisText() {
		if (hypothesisText != null)
			hypothesisText.dispose();
		hypothesisText = new EventBPredicateText(toolkit, hypothesisComposite);
		hypothesisText.getMainTextWidget().setBackground(background);
		hypothesisText.getMainTextWidget().setLayoutData(
				new GridData(SWT.FILL, SWT.FILL, true, true));

		int borderWidth = hypothesisText.getMainTextWidget().getBorderWidth();
		hypothesisText.getMainTextWidget().setText(" ");
		hypothesisComposite.pack(true);
		int textWidth = hypothesisText.getMainTextWidget().getSize().x;

		Rectangle rec = hypothesisComposite.getBounds();
		Point size = hypothesisComposite.getSize();
		int compositeWidth = hypothesisComposite.getClientArea().width;
//		if (textWidth != 0) {
//			max_length = (compositeWidth - borderWidth) / textWidth;
//		} else
//			max_length = 30;

		if (enable && parsedPred instanceof QuantifiedPredicate
				&& parsedPred.getTag() == Formula.FORALL) {
			QuantifiedPredicate qpred = (QuantifiedPredicate) parsedPred;
			Collection<Point> indexes = new ArrayList<Point>();

			String string = "\u2200\n";
			BoundIdentDecl[] idents = qpred.getBoundIdentDecls();

			int i = 0;
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = actualString.substring(loc.getStart(), loc
						.getEnd());
				// ProverUIUtils.debugProverUI("Ident: " + image);
				string += "  " + image + "  ";
				int x = string.length();
				string += "      ";
				int y = string.length();
				indexes.add(new Point(x, y));

				if (++i == idents.length) {
					string += "\n";
				} else {
					string += "  ,\n";
				}
			}
			String str = prettyPrint(actualString, qpred.getPredicate(), 0);

			string += "\u00b7\n";
			string += str;
			hypothesisText.setText(string, indexes);
		} else {
			String str = prettyPrint(actualString, parsedPred);
			Collection<Point> indexes = new ArrayList<Point>();
			hypothesisText.setText(str, indexes);
		}
		toolkit.paintBordersFor(hypothesisComposite);
	}

	private String printSpace(int indent) {
		String result = "";
		for (int i = 0; i < indent; i++)
			result += " ";
		return result;
	}

	private String prettyPrint(String predString, Predicate pred) {
		return prettyPrint(predString, pred, 0);
	}

	private String prettyPrint(String predString, Predicate pred, int indent) {
		// Pseudo-code:

		String str = addSpacing(predString, pred);
		if (str.length() <= max_length - indent)
			return printSpace(indent) + str;
		if (pred instanceof AssociativePredicate) {
			AssociativePredicate aPred = (AssociativePredicate) pred;
			String op = "";
			int tag = aPred.getTag();
			if (tag == Predicate.LAND)
				op = "\u2227";
			else if (tag == Predicate.LOR)
				op = "\u2228";
			String result = "";
			ArrayList<Predicate> currentChildren = new ArrayList<Predicate>();
			String oldString = "";
			String currentString = "";
			String tmpString = "";
			int currentHeight = -1;
			int i = 0;

			Predicate[] children = aPred.getChildren();
			for (Predicate child : children) {
				i++;
				oldString = currentString;
				int height = getHeight(child);
				if (height > currentHeight) {
					currentString = recalculate(predString, currentChildren,
							op, height);
					currentHeight = height;
				}
				tmpString = addSpacing(predString, child, currentHeight);

				if (i != children.length) {
					tmpString += printSpace(currentHeight + 1) + op;
				}

				String newString;
				if (currentString != "")
					newString = currentString + printSpace(currentHeight + 1)
							+ tmpString;
				else
					newString = tmpString;

				if (i == children.length) {
					if (newString.length() <= max_length - indent) {
						result += printSpace(indent) + newString;
					} else {
						if (oldString != "")
							result += printSpace(indent) + oldString + "\n";
						result += prettyPrint(predString, child, indent);
					}
				}

				else {
					if (newString.length() <= max_length - indent) {
						currentString = newString;
						currentChildren.add(child);
					} else if (oldString == "") {
						result += prettyPrint(predString, child, indent);
						result += printSpace(indent) + op + "\n";
						currentString = "";
						currentChildren.clear();
						currentHeight = -1;
					} else {
						result += printSpace(indent) + oldString + "\n";
						currentHeight = getHeight(child);
						currentChildren.clear();
						currentChildren.add(child);
						currentString = addSpacing(predString, child,
								currentHeight)
								+ printSpace(currentHeight + 1) + op;
					}
				}

			}
			ProverUIUtils.debugProverUI("Pred: " + pred);
			ProverUIUtils.debugProverUI("Result: \n" + result);
			return result;
		}

		if (pred instanceof BinaryPredicate) {
			BinaryPredicate bPred = (BinaryPredicate) pred;
			String op = "";
			int tag = bPred.getTag();
			if (tag == Predicate.LIMP)
				op = "\u21d2";
			else if (tag == Predicate.LEQV)
				op = "\u21d4";
			return prettyPrint(predString, bPred.getLeft(), indent + tab)
					+ "\n" + printSpace(indent) + op + "\n"
					+ prettyPrint(predString, bPred.getRight(), indent + tab);
		}

		if (pred instanceof LiteralPredicate) {
			return printSpace(indent) + str;
		}

		if (pred instanceof QuantifiedPredicate) {
			QuantifiedPredicate qPred = (QuantifiedPredicate) pred;
			int tag = qPred.getTag();
			String op = "";
			if (tag == Predicate.FORALL)
				op = "\u2200";
			else if (tag == Predicate.EXISTS)
				op = "\u2203";
			BoundIdentDecl[] idents = qPred.getBoundIdentDecls();

			String result = printSpace(indent) + op + " ";
			int i = 0;
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = predString.substring(loc.getStart(), loc
						.getEnd());
				ProverUIUtils.debugProverUI("Ident: " + image);

				if (i++ == 0) {
					result += image;
				} else {
					result += ", " + image;
				}
			}

			result += " \u00b7 ";

			return result
					+ "\n"
					+ prettyPrint(predString, qPred.getPredicate(), indent
							+ tab);
		}

		if (pred instanceof RelationalPredicate) {
			return printSpace(indent) + str;
		}

		if (pred instanceof SimplePredicate) {
			return printSpace(indent) + str;
		}

		if (pred instanceof UnaryPredicate) {
			UnaryPredicate uPred = (UnaryPredicate) pred;
			int tag = uPred.getTag();
			String op = "";
			if (tag == Predicate.NOT)
				op = "\u00ac";

			return printSpace(indent) + op + "\n"
					+ prettyPrint(predString, uPred.getChild(), indent + tab);
		}
		return "";
	}

	private String recalculate(String predString,
			ArrayList<Predicate> currentChildren, String op, int height) {
		String result = "";
		int i = 0;
		for (Predicate child : currentChildren) {
			if (i != 0)
				result += printSpace(height + 1);
			result += addSpacing(predString, child, height);
			result += printSpace(height + 1) + op;
			i++;
		}
		return result;
	}

	private String addSpacing(String predString, Predicate pred) {
		return addSpacing(predString, pred, getHeight(pred));
	}

	private String addSpacing(String predString, Predicate pred, int height) {
		if (pred instanceof AssociativePredicate) {
			AssociativePredicate aPred = (AssociativePredicate) pred;
			String op = "";
			int tag = aPred.getTag();
			if (tag == Predicate.LAND)
				op = "\u2227";
			else if (tag == Predicate.LOR)
				op = "\u2228";
			String result = "";
			int i = 0;
			for (Predicate child : aPred.getChildren()) {
				if (i != 0)
					result += addSpacing(op, height);
				result += addSpacing(predString, child, height - 1);
				i++;
			}
			return result;
		}

		if (pred instanceof BinaryPredicate) {
			BinaryPredicate bPred = (BinaryPredicate) pred;
			String op = "";
			int tag = bPred.getTag();
			if (tag == Predicate.LIMP)
				op = "\u21d2";
			else if (tag == Predicate.LEQV)
				op = "\u21d4";
			return addSpacing(predString, bPred.getLeft(), height - 1)
					+ addSpacing(op, height)
					+ addSpacing(predString, bPred.getRight(), height - 1);
		}

		if (pred instanceof LiteralPredicate) {
			SourceLocation loc = pred.getSourceLocation();
			String image = predString.substring(loc.getStart(), loc.getEnd());
			return image;
		}

		if (pred instanceof QuantifiedPredicate) {
			QuantifiedPredicate qPred = (QuantifiedPredicate) pred;

			int tag = qPred.getTag();
			String op = "";
			if (tag == Predicate.FORALL)
				op = "\u2200";
			else if (tag == Predicate.EXISTS)
				op = "\u2203";
			BoundIdentDecl[] idents = qPred.getBoundIdentDecls();

			String result = op + " ";
			int i = 0;
			for (BoundIdentDecl ident : idents) {
				SourceLocation loc = ident.getSourceLocation();
				String image = predString.substring(loc.getStart(), loc
						.getEnd());
				ProverUIUtils.debugProverUI("Ident: " + image);

				if (i++ == 0) {
					result += image;
				} else {
					result += ", " + image;
				}
			}

			result += " \u00b7 "
					+ addSpacing(predString, qPred.getPredicate(), height);

			return result;
		}

		if (pred instanceof RelationalPredicate) {
			RelationalPredicate rPred = (RelationalPredicate) pred;

			int tag = rPred.getTag();
			String op = "";
			if (tag == Predicate.EQUAL)
				op = "=";
			else if (tag == Predicate.NOTEQUAL)
				op = "\u2260";
			else if (tag == Predicate.LT)
				op = "<";
			else if (tag == Predicate.LE)
				op = "\u2264";
			else if (tag == Predicate.GT)
				op = ">";
			else if (tag == Predicate.GE)
				op = "\u2265";
			else if (tag == Predicate.IN)
				op = "\u2208";
			else if (tag == Predicate.NOTIN)
				op = "\u2209";
			else if (tag == Predicate.SUBSET)
				op = "\u2282";
			else if (tag == Predicate.NOTSUBSET)
				op = "\u2284";
			else if (tag == Predicate.SUBSETEQ)
				op = "\u2286";
			else if (tag == Predicate.NOTSUBSETEQ)
				op = "\u2288";

			SourceLocation loc = rPred.getLeft().getSourceLocation();
			String imageLeft = predString.substring(loc.getStart(), loc
					.getEnd());
			loc = rPred.getRight().getSourceLocation();
			String imageRight = predString.substring(loc.getStart(), loc
					.getEnd());
			return imageLeft + addSpacing(op, height) + imageRight;
		}

		if (pred instanceof SimplePredicate) {
			SourceLocation loc = pred.getSourceLocation();
			String image = predString.substring(loc.getStart(), loc.getEnd());
			return image;
		}

		if (pred instanceof UnaryPredicate) {
			UnaryPredicate uPred = (UnaryPredicate) pred;
			int tag = uPred.getTag();
			String op = "";
			if (tag == Predicate.NOT)
				op = "\u00ac";

			return addSpacing(op, height)
					+ addSpacing(predString, uPred.getChild(), height - 1);
		}
		return "";
	}

	private String addSpacing(String op, int height) {
		String result = "";
		for (int i = 0; i < height; i++)
			result += " ";
		result += op;
		for (int i = 0; i < height; i++)
			result += " ";
		return result;
	}

	private int getHeight(Predicate pred) {
		if (pred instanceof AssociativePredicate) {
			int maxHeight = 0;
			AssociativePredicate associativePred = (AssociativePredicate) pred;
			for (Predicate child : associativePred.getChildren()) {
				int height = getHeight(child);
				maxHeight = height > maxHeight ? height : maxHeight;
			}
			return maxHeight + 1;
		}

		if (pred instanceof BinaryPredicate) {
			int leftHeight = getHeight(((BinaryPredicate) pred).getLeft());
			int rightHeight = getHeight(((BinaryPredicate) pred).getRight());
			return leftHeight > rightHeight ? leftHeight : rightHeight + 1;
		}

		if (pred instanceof LiteralPredicate) {
			return 0;
		}

		if (pred instanceof QuantifiedPredicate) {
			return getHeight(((QuantifiedPredicate) pred).getPredicate());
		}

		if (pred instanceof RelationalPredicate) {
			// TODO Get the height of the expression?
			return 1;
		}

		if (pred instanceof SimplePredicate) {
			// TODO Get the height of the expression?
			return 0;
		}

		if (pred instanceof UnaryPredicate) {
			return getHeight(((UnaryPredicate) pred).getChild()) + 1;
		}

		return 0;
	}

	/**
	 * Utility methods to create image hyperlinks for applicable tactics.
	 * <p>
	 * 
	 * @param formText
	 *            the formText parent of these hyperlinks
	 */
	private void createImageHyperlinks(Composite parent, Color background,
			boolean enable) {
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
					String[] inputs = hypothesisText.getResults();
					for (String input : inputs) {
						ProverUIUtils.debugProverUI("Input: \"" + input + "\"");
					}
					userSupport.applyTacticToHypotheses(tactic.getTactic(node,
							hyp, inputs), hypSet);
				}

			});
			ds.setBackground(background);
			ds.setToolTipText(tactic.getHint());
			ds.setEnabled(enable);
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
